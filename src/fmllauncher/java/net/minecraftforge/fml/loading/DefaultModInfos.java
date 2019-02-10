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

import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DefaultModInfos
{
    static {
        FileConfig minecraftmod;
        try
        {
            final URI jarFileURI = DefaultModInfos.class.getClassLoader().getResource("minecraftmod.toml").toURI();
            if (Objects.equals(jarFileURI.getScheme(), "jar")) {
                // Initialize the filesystem for the forge jar, because otherwise this barfs?
                FileSystems.newFileSystem(jarFileURI, new HashMap<>());
            }
            minecraftmod = FileConfig.of(Paths.get(jarFileURI));
            minecraftmod.load();
        }
        catch (IOException | URISyntaxException | NullPointerException e)
        {
            throw new RuntimeException("Missing toml config for minecraft!", e);
        }
        minecraftModInfo = new ModInfo(null, minecraftmod);
    }

    public static final IModInfo minecraftModInfo;

    // no construction
    private DefaultModInfos() {}

    public static List<IModInfo> getModInfos()
    {
        return Arrays.asList(minecraftModInfo);
    }


}
