package io.github.imurx.localizedbrowser.util;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Only reason this exists is because if multiple languages are going to have dependencies then
 * I will need more than one dictionary and that means more than 40MB in the main jar...
 */
public class DependencyManager {
    public static final Logger LOGGER = LoggerFactory.getLogger(LocalizedBrowser.MOD_ID + "/DependencyManager");
    private final Path cache;
    private final HttpClient client = HttpClient.newHttpClient();
    public final PublicURLClassLoader classLoader;

    public DependencyManager(Path cache) {
        this.cache = cache;
        this.classLoader = new PublicURLClassLoader(this.getJars());
    }

    private URL[] getJars() {
        File folder = this.cache.toFile();
        File[] files = folder.listFiles((file, name) -> name.endsWith(".jar"));
        if (files == null) {
            return new URL[0];
        }
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                urls[i] = files[i].toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return urls;
    }

    public CompletableFuture<String> getHash(String pkg) {
        LOGGER.info("Getting hash of {}", pkg);
        String[] split = pkg.split(":");
        Path file = this.cache.resolve(split[1] + ".jar");
        AsynchronousFileChannel channel = null;
        try {
            channel = AsynchronousFileChannel.open(file);
            var buffer = ByteBuffer.allocate((int) channel.size());
            CompletableFuture<ByteBuffer> completableFuture = new CompletableFuture<>();
            AsynchronousFileChannel finalChannel = channel;
            channel.read(buffer, 0, null, new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
                    try {
                        finalChannel.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    completableFuture.complete(buffer);
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    try {
                        finalChannel.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    completableFuture.completeExceptionally(exc);
                }
            });
            // I like the hashing from Guava
            return completableFuture.thenApply(byteBuffer -> {
                String hash = Hashing.sha256().hashBytes(byteBuffer.flip()).toString();
                LOGGER.info("Got hash of {}: {}", pkg, hash);
                return hash;
            });
        } catch (IOException ex) {
            if(channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<Boolean> compareHash(String pkg, String hash) {
        return this.getHash(pkg).thenApply(hash::equals);
    }

    public CompletableFuture<File> downloadJarAsync(String pkg, String hash) {
        LOGGER.info("Downloading {}", pkg);
        String[] split = pkg.split(":");
        // Using Google's Maven Central mirror for now
        URI url = URI.create("https://maven-central.storage.googleapis.com/maven2/")
                .resolve(split[0].replaceAll("\\.", "/") + "/")
                .resolve(split[1] + "/")
                .resolve(split[2] + "/")
                .resolve(split[1] + "-" + split[2] + ".jar");
        HttpRequest request = HttpRequest.newBuilder(url)
                .timeout(Duration.ofMinutes(1))
                .header("User-Agent", "LocalizedBrowser/2.0.0 (Minecraft; gh:ImUrX)")
                .GET()
                .build();
        return this.client
                .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body)
                .thenApply(is -> {
                    File jar = this.cache.resolve(split[1] + ".jar").toFile();
                    try {
                        Files.createParentDirs(jar);
                        try (var out = new FileOutputStream(jar)) {
                            is.transferTo(out);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    LOGGER.info("Downloaded {}", pkg);
                    return jar;
                });
    }

    public CompletableFuture<Void> loadFromResource(String filename) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        try (var stream = DependencyManager.class.getResourceAsStream(filename)) {
            assert stream != null;
            var reader = new BufferedReader(new InputStreamReader(stream));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ");
                futures.add(this.compareHash(split[0], split[1])
                        .thenCompose(x -> x ? CompletableFuture.completedFuture(null)
                                : downloadJarAsync(split[0], split[1])
                                .thenAccept(jar -> {
                                    try {
                                        this.classLoader.addURLs(jar.toURI().toURL());
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        )
                );
            }
        } catch (IOException | NullPointerException ex) {
            throw new RuntimeException(ex);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public Path getCache() {
        return cache;
    }
}
