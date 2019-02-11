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

package net.minecraftforge.fml.test;

import net.minecraftforge.fml.language.IModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModFileParser;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModsTomlTest
{
    @Test
    public void testTomlLoad() throws URISyntaxException
    {
        final URL resource = getClass().getClassLoader().getResource("mods.toml");
        final Path path = Paths.get(resource.toURI());
        final IModFileInfo modFileInfo = ModFileParser.loadModFile(null, path);
        modFileInfo.getMods();
    }

    @Test
    public void testTomlInJar() throws URISyntaxException, IOException
    {
        final URL resource = getClass().getClassLoader().getResource("mod.jar");

        final FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(resource.toURI()), getClass().getClassLoader());
        final Path path = fileSystem.getPath("META-INF", "mods.toml");
        ModFileParser.loadModFile(null, path);
    }
}
