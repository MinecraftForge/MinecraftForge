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

package net.minecraftforge.fml.packs;


import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

public class ResourcePackLoader
{
    private static Map<ModFile, ModFileResourcePack> modResourcePacks = null;
    private static ResourcePackList<?> resourcePackList;

    public static Optional<ModFileResourcePack> getResourcePackFor(String modId)
    {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).
                map(ModFileInfo::getFile).map(mf->modResourcePacks.get(mf));
    }

    public static Map<ModFile, ModFileResourcePack> gatherModResourcePacks()
    {
        if (modResourcePacks == null)
        {
            ModList modList = ModList.get();
            if (modList == null)
                return Collections.emptyMap();
            modResourcePacks = modList.getModFiles().stream().
                    filter(mf -> !Objects.equals(mf.getModLoader(), "minecraft")).
                    map(mf -> new ModFileResourcePack(mf.getFile())).
                    collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
        }
        return modResourcePacks;
    }

    public static <T extends ResourcePackInfo> void loadResourcePacks(ResourcePackList<T> resourcePacks, Function<BiConsumer<? super ModFileResourcePack, T>, IPackInfoFinder> packFinder) {
        resourcePackList = resourcePacks;
        resourcePacks.addPackFinder(new IPackFinder()
        {
            private IPackFinder finder;

            @Override
            public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory)
            {
                if (finder == null)
                {
                    finder = new LambdaFriendlyPackFinder(packFinder.apply(ModFileResourcePack::setPackInfo));
                }
                finder.addPackInfosToMap(packList, factory);
            }
        });
    }

    public interface IPackInfoFinder<T extends ResourcePackInfo> {
        void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory);
    }

    // SO GROSS - DON'T @ me bro
    @SuppressWarnings("unchecked")
    private static class LambdaFriendlyPackFinder implements IPackFinder {
        private IPackInfoFinder wrapped;

        private LambdaFriendlyPackFinder(final IPackInfoFinder wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, ResourcePackInfo.IFactory<T> factory)
        {
            wrapped.addPackInfosToMap(packList, factory);
        }
    }
}
