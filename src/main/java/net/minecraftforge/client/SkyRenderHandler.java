/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    private final SkyLayerGroup rootLayerGroup;

    public SkyRenderHandler() {
        this.rootLayerGroup = new SkyLayerGroup();
        SkyLayer skyBg = rootLayerGroup.subLayer(new ResourceLocation("sky_background"));
        SkyLayer celestial = rootLayerGroup.subLayer(new ResourceLocation("celestial"));
        SkyLayer skyFg = rootLayerGroup.subLayer(new ResourceLocation("sky_foreground"));

        SkyLayerGroup celestialGroup = celestial.asGroup();
        SkyLayer planetary = celestialGroup.subLayer(new ResourceLocation("planetary"));
        SkyLayer stellar = celestialGroup.subLayer(new ResourceLocation("stellar"));

        SkyLayerGroup planetaryGroup = celestial.asGroup();
        SkyLayer sun = planetaryGroup.subLayer(new ResourceLocation("sun"));
        SkyLayer moon = planetaryGroup.subLayer(new ResourceLocation("moon"));
    }

    public boolean render(float partialTicks, WorldClient world, Minecraft mc)
    {
        if(!this.enabled)
            return false;
        for(SkyLayer layer : rootLayerGroup.subLayers())
            this.render(layer, partialTicks, world, mc);
        return true;
    }

    private void render(SkyLayer layer, float partialTicks, WorldClient world, Minecraft mc)
    {
        if(layer.isGroup())
        {
            for(SkyLayer subLayer : layer.asGroup().subLayers())
                this.render(subLayer, partialTicks, world, mc);
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
