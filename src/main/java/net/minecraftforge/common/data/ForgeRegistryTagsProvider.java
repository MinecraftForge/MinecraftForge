/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

public abstract class ForgeRegistryTagsProvider<T> extends TagsProvider<T>
{
    private static <T> Registry<T> wrapRegistry(IForgeRegistry<T> forgeRegistry)
    {
        if (forgeRegistry.tags() == null)
            throw new IllegalArgumentException("Forge registry " + forgeRegistry.getRegistryName() + " does not have support for tags");
        if (forgeRegistry.getDefaultKey() == null)
            return GameData.getWrapper(forgeRegistry.getRegistryKey(), Lifecycle.experimental());
        return GameData.getWrapper(forgeRegistry.getRegistryKey(), Lifecycle.experimental(), "default");
    }

    public ForgeRegistryTagsProvider(DataGenerator generator, IForgeRegistry<T> forgeRegistry, String modId, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(generator, wrapRegistry(forgeRegistry), modId, existingFileHelper);
    }
}
