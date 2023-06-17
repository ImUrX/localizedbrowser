package io.github.imurx.localizedbrowser.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class PublicURLClassLoader extends URLClassLoader {
    public PublicURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public PublicURLClassLoader(URL[] urls) {
        super(urls);
    }

    public PublicURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public PublicURLClassLoader(String name, URL[] urls, ClassLoader parent) {
        super(name, urls, parent);
    }

    public PublicURLClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(name, urls, parent, factory);
    }

    public void addURLs(URL... urls) {
        for (URL url : urls) {
            this.addURL(url);
        }
    }
}
