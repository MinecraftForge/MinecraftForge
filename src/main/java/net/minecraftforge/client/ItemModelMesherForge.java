package net.minecraftforge.client;

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
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

    protected IBakedModel getItemModel(Item item, int meta)
    {
        TIntObjectHashMap<IBakedModel> map = models.get(item);
        return map == null ? null : map.get(meta);
    }

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
