/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
