/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Stub class to extend for item model data providers, eliminates some
 * boilerplate constructor parameters.
 */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
    }

    public ItemModelBuilder basicItem(Item item)
    {
        return basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ItemModelBuilder basicItem(ResourceLocation item)
    {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + item.getPath()));
    }

    @NotNull
    @Override
    public String getName() {
        return "Item Models: " + modid;
    }
}
