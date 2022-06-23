/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Wrapper around {@link ItemModelShaper} that cleans up the internal maps to respect ID remapping.
 */
public class ForgeItemModelShaper extends ItemModelShaper
{
    private final Map<Holder.Reference<Item>, ModelResourceLocation> locations = Maps.newHashMap();
    private final Map<Holder.Reference<Item>, BakedModel> models = Maps.newHashMap();

    public ForgeItemModelShaper(ModelManager manager)
    {
        super(manager);
    }

    @Override
    @Nullable
    public BakedModel getItemModel(Item item)
    {
        return models.get(ForgeRegistries.ITEMS.getDelegateOrThrow(item));
    }

    @Override
    public void register(Item item, ModelResourceLocation location)
    {
        Holder.Reference<Item> key = ForgeRegistries.ITEMS.getDelegateOrThrow(item);
        locations.put(key, location);
        models.put(key, getModelManager().getModel(location));
    }

    @Override
    public void rebuildCache()
    {
        final ModelManager manager = this.getModelManager();
        for (Map.Entry<Holder.Reference<Item>, ModelResourceLocation> e : locations.entrySet())
        {
            models.put(e.getKey(), manager.getModel(e.getValue()));
        }
    }

    public ModelResourceLocation getLocation(@NotNull ItemStack stack)
    {
        ModelResourceLocation location = locations.get(ForgeRegistries.ITEMS.getDelegateOrThrow(stack.getItem()));
        return location == null ? ModelBakery.MISSING_MODEL_LOCATION : location;
    }
}
