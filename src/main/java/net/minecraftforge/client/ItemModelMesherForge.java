/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * Wrapper around ItemModeMesher that cleans up the internal maps to respect ID remapping.
 */
public class ItemModelMesherForge extends ItemModelMesher
{
    IdentityHashMap<Item, TIntObjectHashMap<ModelResourceLocation>> locations = Maps.newIdentityHashMap();
    IdentityHashMap<Item, TIntObjectHashMap<IBakedModel>> models = Maps.newIdentityHashMap();

    public ItemModelMesherForge(ModelManager manager)
    {
        super(manager);
    }

    @Override
    protected IBakedModel getItemModel(Item item, int meta)
    {
        TIntObjectHashMap<IBakedModel> map = models.get(item);
        return map == null ? null : map.get(meta);
    }

    @Override
    public void register(Item item, int meta, ModelResourceLocation location)
    {
        TIntObjectHashMap<ModelResourceLocation> locs = locations.get(item);
        TIntObjectHashMap<IBakedModel>           mods = models.get(item);
        if (locs == null)
        {
            locs = new TIntObjectHashMap<ModelResourceLocation>();
            locations.put(item, locs);
        }
        if (mods == null)
        {
            mods = new TIntObjectHashMap<IBakedModel>();
            models.put(item, mods);
        }
        locs.put(meta, location);
        mods.put(meta, getModelManager().getModel(location));
    }

    @Override
    public void rebuildCache()
    {
        final ModelManager manager = this.getModelManager();
        for (Map.Entry<Item, TIntObjectHashMap<ModelResourceLocation>> e : locations.entrySet())
        {
            TIntObjectHashMap<IBakedModel> mods = models.get(e.getKey());
            if (mods != null)
            {
                mods.clear();
            }
            else
            {
                mods = new TIntObjectHashMap<IBakedModel>();
                models.put(e.getKey(), mods);
            }
            final TIntObjectHashMap<IBakedModel> map = mods;
            e.getValue().forEachEntry(new TIntObjectProcedure<ModelResourceLocation>()
            {
                @Override
                public boolean execute(int meta, ModelResourceLocation location)
                {
                    map.put(meta, manager.getModel(location));
                    return true;
                }
            });
        }
    }
}
