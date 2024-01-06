/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import javax.annotation.Nonnull;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Stub class to extend for item model data providers, eliminates some
 * boilerplate constructor parameters.
 */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Item Models: " + modid;
    }
}
