/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.Manifest;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class ModJarURLHandler extends URLStreamHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    // modjar://modid/path/to/file
    @Override
    protected URLConnection openConnection(URL url) {
        return new ModJarURLConnection(url);
    }

    static class ModJarURLConnection extends URLConnection {
        private Path resource;
        private String modpath;
        private String modid;
        private Optional<Manifest> manifest;

        public ModJarURLConnection(final URL url) {
            super(url);
        }

        @Override
        public void connect()
        {
            if (resource == null) {
                modid = url.getHost();
                // trim first char
                modpath = url.getPath().substring(1);
                resource = FMLLoader.getLoadingModList().getModFileById(modid).getFile().findResource(modpath);
                manifest = FMLLoader.getLoadingModList().getModFileById(modid).getManifest();
            }
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            connect();
            LOGGER.trace(CORE, "Loading modjar URL {} got resource {} {}", url, resource, resource != null ? Files.exists(resource) : "missing");
            return Files.newInputStream(resource);
        }

        @Override
        public long getContentLengthLong() {
            try {
                connect();
                return Files.size(resource);
            } catch (IOException e) {
                return -1L;
            }
        }

        public Optional<Manifest> getManifest() {
            return manifest;
        }
    }
}
