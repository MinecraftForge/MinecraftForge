/*
 * Minecraft Forge
 * Copyright (c) 2018.
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
import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DefaultModInfos
{
    static {
        FileConfig minecraftmod;
        FileConfig forgemod;
        try
        {
            minecraftmod = FileConfig.of(Paths.get(DefaultModInfos.class.getClassLoader().getResource("minecraftmod.toml").toURI()));
            forgemod = FileConfig.of(Paths.get(DefaultModInfos.class.getClassLoader().getResource("forgemod.toml").toURI()));
            minecraftmod.load();
            forgemod.load();
        }
        catch (URISyntaxException | NullPointerException e)
        {
            throw new RuntimeException("Missing toml configs for minecraft and forge!", e);
        }
        minecraftModInfo = new ModInfo(null, minecraftmod);
        forgeModInfo = new ModInfo(null, forgemod);

    }

    public static final IModInfo minecraftModInfo;
    public static final IModInfo forgeModInfo;

    // no construction
    private DefaultModInfos() {}

    public static List<IModInfo> getModInfos()
    {
        return Arrays.asList(minecraftModInfo, forgeModInfo);
    }


}