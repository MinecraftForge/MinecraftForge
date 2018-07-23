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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

/**
 * Class which handles sky rendering. Sky render layers can be registered here.
 * TODO Proper sorting system
 * */
public class SkyRenderHandler
{
    private boolean enabled = false;
    private final SkyLayer rootLayer;
    private final Map<ResourceLocation, SkyLayer> layerMap = new HashMap<>();
    private final Map<ResourceLocation, ResourceLocation> parentMap = new HashMap<>();

    /**
     * Registers certain layer as part of certain layer group.
     * Replaces previous one with the same id if it exists.
     * @param layerGroup the layer group
     * @param id the layer id
     * */
    public SkyLayer register(SkyLayer.Group layerGroup, ResourceLocation id)
    {
        if(layerMap.containsKey(id))
        {
            layerMap.remove(id);
            parentMap.remove(id);
        }

        SkyLayer layer = new SkyLayer(id);
        layerMap.put(id, layer);
        return layer;
    }

    /**
     * @return sky layer registered for specified id
     * */
    public SkyLayer getLayer(ResourceLocation id)
    {
        return layerMap.get(id);
    }

    public SkyRenderHandler()
    {
        this.rootLayer = new SkyLayer(new ResourceLocation("sky_all"));
        SkyLayer.Group rootLayerGroup = rootLayer.makeGroup();
        SkyLayer skyBg = this.register(rootLayerGroup, new ResourceLocation("sky_background"));
        SkyLayer celestial = this.register(rootLayerGroup, new ResourceLocation("celestial"));
        SkyLayer skyFg = this.register(rootLayerGroup, new ResourceLocation("sky_foreground"));

        SkyLayer.Group celestialGroup = celestial.makeGroup();
        SkyLayer planetary = this.register(celestialGroup, new ResourceLocation("planetary"));
        SkyLayer stellar = this.register(celestialGroup, new ResourceLocation("stellar"));

        SkyLayer.Group planetaryGroup = planetary.makeGroup();
        SkyLayer sun = this.register(planetaryGroup, new ResourceLocation("sun"));
        SkyLayer moon = this.register(planetaryGroup, new ResourceLocation("moon"));
    }

    public boolean render(float partialTicks, WorldClient world, Minecraft mc)
    {
        if(!this.enabled)
            return false;
        //for(SkyLayer layer : rootLayerGroup.subLayers())
        //    this.render(layer, partialTicks, world, mc);
        return true;
    }

    private void render(SkyLayer layer, float partialTicks, WorldClient world, Minecraft mc)
    {
        if(layer.getGroup() != null)
        {
         //   for(SkyLayer subLayer : layer.asGroup().subLayers())
         //       this.render(subLayer, partialTicks, world, mc);
        }
        if(layer.getRenderer() != null)
            layer.getRenderer().render(partialTicks, world, mc);
    }

    /*private static class SkyBackRenderer extends IRenderHandler
    {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            
        }
    }*/
}
