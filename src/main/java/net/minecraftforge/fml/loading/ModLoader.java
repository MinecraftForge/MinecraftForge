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

import net.minecraftforge.fml.common.ModContainer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ModLoader
{
    private final ClassLoader launchClassLoader;
    private final ModList modList;
    private final ModLoadingClassLoader modClassLoader;

    public ModLoader(final ClassLoader launchClassLoader, final ModList modList)
    {
        this.launchClassLoader = launchClassLoader;
        this.modList = modList;
        this.modClassLoader = new ModLoadingClassLoader(this, this.launchClassLoader);
    }

    public void classloadModFiles()
    {

    }

    public ModList getModList()
    {
        return modList;
    }

    public void loadMods() {
        final List<ModContainer> collect = modList.getModFiles().stream().map(mf -> mf.buildMods(this.modClassLoader)).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
