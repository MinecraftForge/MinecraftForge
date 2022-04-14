/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IRegistryDelegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper around ItemModeMesher that cleans up the internal maps to respect ID remapping.
 */
public class ItemModelMesherForge extends ItemModelShaper
{
    final Map<IRegistryDelegate<Item>, ModelResourceLocation> locations = Maps.newHashMap();
    final Map<IRegistryDelegate<Item>, BakedModel> models = Maps.newHashMap();

    public ItemModelMesherForge(ModelManager manager)
    {
        super(manager);
    }

    @Override
    @Nullable
    public BakedModel getItemModel(Item item)
    {
        return models.get(item.delegate);
    }

    @Override
    public void register(Item item, ModelResourceLocation location)
    {
        IRegistryDelegate<Item> key = item.delegate;
        locations.put(key, location);
        models.put(key, getModelManager().getModel(location));
    }

    @Override
    public void rebuildCache()
    {
        final ModelManager manager = this.getModelManager();
        for (Map.Entry<IRegistryDelegate<Item>, ModelResourceLocation> e : locations.entrySet())
        {
        	models.put(e.getKey(), manager.getModel(e.getValue()));
        }
    }

    public ModelResourceLocation getLocation(@NotNull ItemStack stack)
    {
        ModelResourceLocation location = locations.get(stack.getItem().delegate);

        if (location == null)
        {
            location = ModelBakery.MISSING_MODEL_LOCATION;
        }

        return location;
    }
}
