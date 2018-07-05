/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.client;

import java.util.Map;

import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wrapper around ItemModeMesher that cleans up the internal maps to respect ID remapping.
 */
public class ItemModelMesherForge extends ItemModelMesher
{
    final Map<IRegistryDelegate<Item>, Int2ObjectMap<ModelResourceLocation>> locations = Maps.newHashMap();
    final Map<IRegistryDelegate<Item>, Int2ObjectMap<IBakedModel>> models = Maps.newHashMap();

    public ItemModelMesherForge(ModelManager manager)
    {
        super(manager);
    }

    @Override
    @Nullable
    protected IBakedModel getItemModel(Item item, int meta)
    {
        Int2ObjectMap<IBakedModel> map = models.get(item.delegate);
        return map == null ? null : map.get(meta);
    }

    @Override
    public void register(Item item, int meta, ModelResourceLocation location)
    {
        IRegistryDelegate<Item> key = item.delegate;
        Int2ObjectMap<ModelResourceLocation> locs = locations.get(key);
        Int2ObjectMap<IBakedModel>           mods = models.get(key);
        if (locs == null)
        {
            locs = new Int2ObjectOpenHashMap<>();
            locations.put(key, locs);
        }
        if (mods == null)
        {
            mods = new Int2ObjectOpenHashMap<>();
            models.put(key, mods);
        }
        locs.put(meta, location);
        mods.put(meta, getModelManager().getModel(location));
    }

    @Override
    public void rebuildCache()
    {
        final ModelManager manager = this.getModelManager();
        for (Map.Entry<IRegistryDelegate<Item>, Int2ObjectMap<ModelResourceLocation>> e : locations.entrySet())
        {
            Int2ObjectMap<IBakedModel> mods = models.get(e.getKey());
            if (mods != null)
            {
                mods.clear();
            }
            else
            {
                mods = new Int2ObjectOpenHashMap<>();
                models.put(e.getKey(), mods);
            }
            final Int2ObjectMap<IBakedModel> map = mods;
            e.getValue().int2ObjectEntrySet().forEach(entry ->
                map.put(entry.getIntKey(), manager.getModel(entry.getValue()))
            );
        }
    }

    public ModelResourceLocation getLocation(@Nonnull ItemStack stack)
    {
        Item item = stack.getItem();
        ModelResourceLocation location = null;

        Int2ObjectMap<ModelResourceLocation> map = locations.get(item.delegate);
        if (map != null)
        {
            location = map.get(getMetadata(stack));
        }

        if (location == null)
        {
            ItemMeshDefinition definition = shapers.get(item);
            if (definition != null)
            {
                location = definition.getModelLocation(stack);
            }
        }

        if (location == null)
        {
            location = ModelBakery.MODEL_MISSING;
        }

        return location;
    }
}
