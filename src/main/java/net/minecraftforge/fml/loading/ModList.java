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

import com.google.common.collect.Multimap;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Master list of all mods
 */
public class ModList
{
    private final List<ModFile> modFiles;
    private final List<ModInfo> sortedList;
    private BackgroundScanHandler scanner;

    public ModList(final List<ModFile> modFiles, final List<ModInfo> sortedList)
    {
        this.modFiles = modFiles;
        this.sortedList = sortedList;
    }

    public void addCoreMods()
    {
        modFiles.stream().map(ModFile::getCoreMods).flatMap(List::stream).forEach(FMLLoader.getCoreModProvider()::addCoreMod);
    }

    public void addAccessTransformers()
    {
        modFiles.forEach(mod -> mod.getAccessTransformer().ifPresent(path -> FMLLoader.addAccessTransformer(path, mod)));
    }

    public void addForScanning(BackgroundScanHandler backgroundScanHandler)
    {
        this.scanner = backgroundScanHandler;
        backgroundScanHandler.setModList(this);
        modFiles.forEach(backgroundScanHandler::submitForScanning);
    }

    public List<ModFile> getModFiles()
    {
        return modFiles;
    }

    public Path findResource(final String className)
    {
        for (final ModFile mf : modFiles) {
            final Path resource = mf.findResource(className);
            if (resource != null) return resource;
        }
        return null;
    }
}
