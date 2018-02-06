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

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.SortedMap;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Class which handles sky rendering. Sky renderer can be registered here,
 *  see {@link #registerSkyRenderer(SkyRenderPass, ResourceLocation, IRenderHandler, double) registerSkyRenderer}.
 * */
public class SkyRenderHandler
{
    private EnumMap<SkyRenderPass, PartSkyRenderer> renderers = new EnumMap<SkyRenderPass, PartSkyRenderer>(SkyRenderPass.class)
    {{
        this.put(SkyRenderPass.SKY_BACK, new PartSkyRenderer(getSkyBackDefault()));
        this.put(SkyRenderPass.SUN_MOON_STARS, new PartSkyRenderer(getSunMoonStarsDefault()));
        this.put(SkyRenderPass.SKY_FRONT, new PartSkyRenderer(getSkyFrontDefault()));
    }};

    // The vanilla render pass for parts of vanilla sky rendering on renderglobal call.
    // 0 means it's the original call.
    private int vanillaRenderPass;

    /**
     * <p>Registers sky renderer for certain pass and id.</p>
     * <p>Registering a renderer with pre-existing pass & id pair will replace the previous renderer.</p>
     * <p>You can unregister a renderer by giving <code>null</code> for the renderer parameter.
     * Unregistering a renderer with its vanilla-counterpart will re-enable a vanilla renderer.</p>
     * <p>Renderer with higher priority is called earlier.</p>
     * @param pass the sky render pass
     * @param id the id for the renderer
     * @param renderer the renderer
     * @param priority the priority for the renderer
     * */
    public void registerSkyRenderer(SkyRenderPass pass, ResourceLocation id, @Nullable IRenderHandler renderer, double priority)
    {
        renderers.get(pass).registerSkyRenderer(id, renderer, priority);
    }

    /**
     * Unregisters vanilla sky renderer for certain pass and id.
     * This will only remove the renderer if it's the vanilla one.
     * @param pass the sky render pass
     * @param id the id to unregister
     * @return the vanilla render handler, or <code>null</code> if there wasn't a vanilla renderer for specified pass-id pair
     * */
    public @Nullable IRenderHandler unregisterVanillaRenderer(SkyRenderPass pass, ResourceLocation id)
    {
        return renderers.get(pass).unregisterVanillaRenderer(id);
    }

    public boolean render(float partialTicks, WorldClient world, Minecraft mc)
    {
        boolean enabled = false;
        for(PartSkyRenderer part : renderers.values())
            enabled = enabled || part.enabled();

        if(!enabled)
            return false;

        renderers.get(SkyRenderPass.SKY_BACK).render(partialTicks, world, mc);
        renderers.get(SkyRenderPass.SUN_MOON_STARS).render(partialTicks, world, mc);
        renderers.get(SkyRenderPass.SKY_FRONT).render(partialTicks, world, mc);

        return true;
    }

    private static class PartSkyRenderer {
        Map<ResourceLocation, RenderHandler> defHandlers;
        SortedMap<ResourceLocation, RenderHandler> handlers = Maps.newTreeMap();

        PartSkyRenderer(Map<ResourceLocation, RenderHandler> defaultHandlers) {
            this.defHandlers = defaultHandlers;
        }

        void registerSkyRenderer(ResourceLocation id, IRenderHandler renderer, double priority)
        {
            if(renderer != null)
                handlers.put(id, new RenderHandler(renderer, priority));
            else if(defHandlers.containsKey(id))
                handlers.put(id, defHandlers.get(id));
            else handlers.remove(id);
        }

        IRenderHandler unregisterVanillaRenderer(ResourceLocation id)
        {
            if(handlers.containsKey(id) && defHandlers.get(id) == handlers.get(id))
                return handlers.remove(id).handler;
            else return null;
        }

        boolean enabled() {
            return !handlers.equals(this.defHandlers);
        }

        void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            for(RenderHandler handler : handlers.values())
            {
                handler.handler.render(partialTicks, world, mc);
            }
        }
    }

    private static class RenderHandler implements Comparable<RenderHandler> {
        IRenderHandler handler;
        double priority;

        RenderHandler(IRenderHandler handler, double priority)
        {
            this.handler = handler;
            this.priority = priority;
        }

        @Override
        public int compareTo(RenderHandler o)
        {
            // Higher priority comes first
            return -Comparator.<RenderHandler>comparingDouble(k -> k.priority).compare(this, o);
        }
    }

    private static RenderHandler handler(int vanillaRenderPass, double priority) {
        return new RenderHandler(new VanillaSkyRenderer(vanillaRenderPass), priority);
    }

    private Map<ResourceLocation, RenderHandler> getSkyBackDefault()
    {
        ImmutableMap.Builder<ResourceLocation, RenderHandler> builder = ImmutableMap.builder();
        builder.put(new ResourceLocation("sky"), handler(1, 0.0));
        return builder.build();
    }

    private Map<ResourceLocation, RenderHandler> getSunMoonStarsDefault()
    {
        ImmutableMap.Builder<ResourceLocation, RenderHandler> builder = ImmutableMap.builder();
        builder.put(new ResourceLocation("sun"), handler(2, 2.0));
        builder.put(new ResourceLocation("moon"), handler(3, 1.0));
        builder.put(new ResourceLocation("stars"), handler(4, 0.0));
        return builder.build();
    }

    private Map<ResourceLocation, RenderHandler> getSkyFrontDefault()
    {
        ImmutableMap.Builder<ResourceLocation, RenderHandler> builder = ImmutableMap.builder();
        builder.put(new ResourceLocation("sky"), handler(5, 0.0));
        return builder.build();
    }

    // Internal method
    public int getVanillaRenderPass() {
        return this.vanillaRenderPass;
    }

    private static class VanillaSkyRenderer extends IRenderHandler {
        private int vanillaRenderPass;

        VanillaSkyRenderer(int vanillaRenderPass)
        {
            this.vanillaRenderPass = vanillaRenderPass;
        }

        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            SkyRenderHandler renderHandler = world.provider.getSkyRenderHandler();
            renderHandler.vanillaRenderPass = this.vanillaRenderPass;
            mc.renderGlobal.renderSky(partialTicks, mc.gameSettings.anaglyph? 0 : 2);
            renderHandler.vanillaRenderPass = 0;
        }
    }
}
