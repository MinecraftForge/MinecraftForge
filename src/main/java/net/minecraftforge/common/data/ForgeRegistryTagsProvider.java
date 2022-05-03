/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Lifecycle;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class ForgeRegistryTagsProvider<T extends IForgeRegistryEntry<T>> extends TagsProvider<T>
{
    //Special handling for vanilla tag types in case someone decides to use the ForgeRegistryTagsProvider instead of one of the vanilla subtypes
    private static final Map<IForgeRegistry<?>, String> vanillaTypes = ImmutableMap.<IForgeRegistry<?>, String>builder()
          .put(ForgeRegistries.BLOCKS, "blocks")
          .put(ForgeRegistries.ENTITIES, "entity_types")
          .put(ForgeRegistries.FLUIDS, "fluids")
          .put(ForgeRegistries.ITEMS, "items")
          .build();

    private static <T extends IForgeRegistryEntry<T>> Registry<T> wrapRegistry(IForgeRegistry<T> registryIn)
    {
        if (!(registryIn instanceof ForgeRegistry))
            throw new IllegalArgumentException("Forge registry " + registryIn.getRegistryName() + " is not an instance of a ForgeRegistry");
        ForgeRegistry<T> forgeRegistry = (ForgeRegistry<T>) registryIn;
        if (forgeRegistry.getTagFolder() == null && !vanillaTypes.containsKey(registryIn))
            throw new IllegalArgumentException("Forge registry " + registryIn.getRegistryName() + " does not have support for tags");
        if (forgeRegistry.getDefaultKey() == null)
            return GameData.getWrapper(forgeRegistry.getRegistryKey(), Lifecycle.experimental());
        return GameData.getWrapper(forgeRegistry.getRegistryKey(), Lifecycle.experimental(), "default");
    }

    private static <T extends IForgeRegistryEntry<T>> String getTagFolder(IForgeRegistry<T> registryIn)
    {
        String tagFolder = ((ForgeRegistry<T>) registryIn).getTagFolder();
        return tagFolder == null ? vanillaTypes.get(registryIn) : tagFolder;
    }

    public ForgeRegistryTagsProvider(DataGenerator generatorIn, IForgeRegistry<T> registryIn, String modId, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(generatorIn, wrapRegistry(registryIn), modId, existingFileHelper, getTagFolder(registryIn));
    }

    @Override
    protected Path getPath(ResourceLocation id)
    {
        return generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/" + folder + "/" + id.getPath() + ".json");
    }
}
