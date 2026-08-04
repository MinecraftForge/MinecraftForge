/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.packs;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
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
    private static Map<ModFile, ModFileResourcePack> modResourcePacks;
    private static ResourcePackList<?> resourcePackList;

    public static Optional<ModFileResourcePack> getResourcePackFor(String modId)
    {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).
                map(ModFileInfo::getFile).map(mf->modResourcePacks.get(mf));
    }

    public static <T extends ResourcePackInfo> void loadResourcePacks(ResourcePackList<T> resourcePacks, BiFunction<Map<ModFile, ? extends ModFileResourcePack>, BiConsumer<? super ModFileResourcePack, T>, IPackInfoFinder> packFinder) {
        resourcePackList = resourcePacks;
        modResourcePacks = ModList.get().getModFiles().stream().
                filter(mf->!Objects.equals(mf.getModLoader(),"minecraft")).
                map(mf -> new ModFileResourcePack(mf.getFile())).
                collect(Collectors.toMap(ModFileResourcePack::getModFile, Function.identity()));
        resourcePacks.addPackFinder(new LambdaFriendlyPackFinder(packFinder.apply(modResourcePacks, ModFileResourcePack::setPackInfo)));
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
