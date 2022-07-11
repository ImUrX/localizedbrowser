package io.github.imurx.localizedbrowser.util;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import io.github.imurx.localizedbrowser.LocalizedBrowser;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
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
import java.util.concurrent.ExecutionException;

public class DependencyManager {
    private final Path cache;
    private final HttpClient client = HttpClient.newHttpClient();
    private URLClassLoader classLoader;

    public DependencyManager(Path cache) {
        this.cache = cache;
        this.reloadClassloader();
    }

    public void reloadClassloader() {
        try {
            if(this.classLoader != null) this.classLoader.close();
            File folder = this.cache.toFile();
            File[] files = folder.listFiles((file, name) -> name.endsWith(".jar"));
            if(files == null) {
                this.classLoader = new URLClassLoader(new URL[0]);
                return;
            }
            URL[] urls = new URL[files.length];
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
            this.classLoader = new URLClassLoader(urls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public CompletableFuture<String> getHash(String pkg) {
        LocalizedBrowser.LOGGER.info("Getting hash of {}", pkg);
        String[] split = pkg.split(":");
        Path file = this.cache.resolve(split[1] + ".jar");
        try {
            var channel = AsynchronousFileChannel.open(file);
            var buffer = ByteBuffer.allocate((int) channel.size());
            CompletableFuture<ByteBuffer> completableFuture = new CompletableFuture<>();
            channel.read(buffer, 0, null, new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    completableFuture.complete(buffer);
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    completableFuture.completeExceptionally(exc);
                }
            });
            return completableFuture.thenApply(byteBuffer -> {
                String hash = Hashing.sha256().hashBytes(byteBuffer.flip()).toString();
                LocalizedBrowser.LOGGER.info("Got hash of {}: {}", pkg, hash);
                return hash;
            });
        } catch (IOException ex) {
            return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<Boolean> compareHash(String pkg, String hash) {
        return this.getHash(pkg).thenApply(hash::equals);
    }

    public CompletableFuture<Void> downloadJarAsync(String pkg, String hash) {
        LocalizedBrowser.LOGGER.info("Downloading {}", pkg);
        String[] split = pkg.split(":");
        URI url = URI.create("https://repo1.maven.org/maven2/")
                .resolve(split[0].replaceAll("\\.", "/") + "/")
                .resolve(split[1] + "/")
                .resolve(split[2] + "/")
                .resolve(split[1] + "-" + split[2] + ".jar");
        LocalizedBrowser.LOGGER.info(url.toString());
        HttpRequest request = HttpRequest.newBuilder(url)
                .timeout(Duration.ofMinutes(2))
                .GET()
                .build();
        return this.client
                .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body)
                .thenAcceptAsync(is -> {
                    File jar = this.cache.resolve(split[1] + ".jar").toFile();
                    try {
                        Files.createParentDirs(jar);
                        try (var out = new FileOutputStream(jar)) {
                            is.transferTo(out);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    LocalizedBrowser.LOGGER.info("Downloaded {}", pkg);
                });
    }

    public void loadFromResource(String filename) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        try (var stream = DependencyManager.class.getResourceAsStream(filename)) {
            assert stream != null;
            var reader = new BufferedReader(new InputStreamReader(stream));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ");
                futures.add(this.compareHash(split[0], split[1]).thenCompose(x -> x ? CompletableFuture.completedFuture(null) : downloadJarAsync(split[0], split[1])));
            }
        } catch (IOException | NullPointerException ex) {
            throw new RuntimeException(ex);
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
            this.reloadClassloader();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Path getCache() {
        return cache;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}
