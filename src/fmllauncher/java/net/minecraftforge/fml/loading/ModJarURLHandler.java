/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModJarURLHandler extends URLStreamHandler
{
    // modjar://modid/path/to/file
    @Override
    protected URLConnection openConnection(URL url) {
        return new URLConnection(url) {
            private Path resource;
            private String modpath;
            private String modid;

            @Override
            public void connect()
            {
                if (resource == null) {
                    modid = url.getHost();
                    // trim first char
                    modpath = url.getPath().substring(1);
                    resource = FMLLoader.getLoadingModList().getModFileById(modid).getFile().findResource(modpath);
                }
            }
            @Override
            public InputStream getInputStream() throws IOException
            {
                connect();
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
        };
    }
}
