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

    /**
     * Registers sky renderer for certain pass and id.
     * Registering a renderer with pre-existing id will replace the previous renderer.
     * Renderer with higher priority is called earlier.
     * @param pass the sky render pass
     * @param id the id for the renderer
     * @param renderer the renderer
     * @param priority the priority for the renderer
     * */
    public void registerSkyRenderer(SkyRenderPass pass, ResourceLocation id, IRenderHandler renderer, double priority)
    {
        renderers.get(pass).registerSkyRenderer(id, renderer, priority);
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

    private static RenderHandler handler(IRenderHandler renderer, double priority) {
        return new RenderHandler(renderer, priority);
    }

    private Map<ResourceLocation, RenderHandler> getSkyBackDefault()
    {
        ImmutableMap.Builder<ResourceLocation, RenderHandler> builder = ImmutableMap.builder();
        builder.put(new ResourceLocation("sky"), handler(new SkyBackRenderer(), 0.0));
        return builder.build();
    }

    private Map<ResourceLocation, RenderHandler> getSunMoonStarsDefault()
    {
        ImmutableMap.Builder<ResourceLocation, RenderHandler> builder = ImmutableMap.builder();
        builder.put(new ResourceLocation("sun"), handler(new SunRenderer(), 2.0));
        builder.put(new ResourceLocation("moon"), handler(new MoonRenderer(), 1.0));
        builder.put(new ResourceLocation("stars"), handler(new StarsRenderer(), 0.0));
        return builder.build();
    }

    private Map<ResourceLocation, RenderHandler> getSkyFrontDefault()
    {
        ImmutableMap.Builder<ResourceLocation, RenderHandler> builder = ImmutableMap.builder();
        builder.put(new ResourceLocation("sky"), handler(new SkyFrontRenderer(), 0.0));
        return builder.build();
    }

    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");

    private boolean vboEnabled;
    private int glSkyList, glSkyList2;
    private VertexBuffer skyVBO, sky2VBO;
    private int starGLCallList;
    private VertexBuffer starVBO;

    private class SkyBackRenderer extends IRenderHandler {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            if (mc.world.provider.getDimensionType().getId() == 1)
            {
                this.renderSkyEnd(mc);
            }
            else if (mc.world.provider.isSurfaceWorld())
            {
                GlStateManager.disableTexture2D();
                Vec3d vec3d = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
                float f = (float)vec3d.x;
                float f1 = (float)vec3d.y;
                float f2 = (float)vec3d.z;

                if (mc.gameSettings.anaglyph)
                {
                    float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                    float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                    float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                    f = f3;
                    f1 = f4;
                    f2 = f5;
                }

                GlStateManager.color(f, f1, f2);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                GlStateManager.depthMask(false);
                GlStateManager.enableFog();
                GlStateManager.color(f, f1, f2);

                if (SkyRenderHandler.this.vboEnabled)
                {
                    SkyRenderHandler.this.skyVBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    SkyRenderHandler.this.skyVBO.drawArrays(7);
                    SkyRenderHandler.this.skyVBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                }
                else
                {
                    GlStateManager.callList(SkyRenderHandler.this.glSkyList);
                }

                GlStateManager.disableFog();
                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                RenderHelper.disableStandardItemLighting();
                float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);

                if (afloat != null)
                {
                    GlStateManager.disableTexture2D();
                    GlStateManager.shadeModel(7425);
                    GlStateManager.pushMatrix();
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                    float f6 = afloat[0];
                    float f7 = afloat[1];
                    float f8 = afloat[2];

                    if (mc.gameSettings.anaglyph)
                    {
                        float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                        float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                        float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                        f6 = f9;
                        f7 = f10;
                        f8 = f11;
                    }

                    bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
                    int l1 = 16;

                    for (int j2 = 0; j2 <= 16; ++j2)
                    {
                        float f21 = (float)j2 * ((float)Math.PI * 2F) / 16.0F;
                        float f12 = MathHelper.sin(f21);
                        float f13 = MathHelper.cos(f21);
                        bufferbuilder.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                    }

                    tessellator.draw();
                    GlStateManager.popMatrix();
                    GlStateManager.shadeModel(7424);
                }
            }
        }

        private void renderSkyEnd(Minecraft mc)
        {
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            mc.renderEngine.bindTexture(END_SKY_TEXTURES);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int k1 = 0; k1 < 6; ++k1)
            {
                GlStateManager.pushMatrix();

                if (k1 == 1)
                {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (k1 == 2)
                {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (k1 == 3)
                {
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (k1 == 4)
                {
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (k1 == 5)
                {
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
                bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
                bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
                bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
        }
    }

    private static class SunRenderer extends IRenderHandler {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            if (mc.world.provider.isSurfaceWorld())
            {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                GlStateManager.enableTexture2D();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.pushMatrix();
                float f16 = 1.0F - world.getRainStrength(partialTicks);
                GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
                float f17 = 30.0F;
                mc.renderEngine.bindTexture(SUN_TEXTURES);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos((double)(-f17), 100.0D, (double)(-f17)).tex(0.0D, 0.0D).endVertex();
                bufferbuilder.pos((double)f17, 100.0D, (double)(-f17)).tex(1.0D, 0.0D).endVertex();
                bufferbuilder.pos((double)f17, 100.0D, (double)f17).tex(1.0D, 1.0D).endVertex();
                bufferbuilder.pos((double)(-f17), 100.0D, (double)f17).tex(0.0D, 1.0D).endVertex();
                tessellator.draw();
            }
        }
    }

    private static class MoonRenderer extends IRenderHandler {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            if (mc.world.provider.isSurfaceWorld())
            {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                float f17 = 20.0F;
                mc.renderEngine.bindTexture(MOON_PHASES_TEXTURES);
                int k1 = world.getMoonPhase();
                int i2 = k1 % 4;
                int k2 = k1 / 4 % 2;
                float f22 = (float)(i2 + 0) / 4.0F;
                float f23 = (float)(k2 + 0) / 2.0F;
                float f24 = (float)(i2 + 1) / 4.0F;
                float f14 = (float)(k2 + 1) / 2.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos((double)(-f17), -100.0D, (double)f17).tex((double)f24, (double)f14).endVertex();
                bufferbuilder.pos((double)f17, -100.0D, (double)f17).tex((double)f22, (double)f14).endVertex();
                bufferbuilder.pos((double)f17, -100.0D, (double)(-f17)).tex((double)f22, (double)f23).endVertex();
                bufferbuilder.pos((double)(-f17), -100.0D, (double)(-f17)).tex((double)f24, (double)f23).endVertex();
                tessellator.draw();
                GlStateManager.disableTexture2D();
            }
        }
    }

    private class StarsRenderer extends IRenderHandler {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            if (mc.world.provider.isSurfaceWorld())
            {
                float f16 = 1.0F - world.getRainStrength(partialTicks);
                float f15 = world.getStarBrightness(partialTicks) * f16;

                if (f15 > 0.0F)
                {
                    GlStateManager.color(f15, f15, f15, f15);

                    if (SkyRenderHandler.this.vboEnabled)
                    {
                        SkyRenderHandler.this.starVBO.bindBuffer();
                        GlStateManager.glEnableClientState(32884);
                        GlStateManager.glVertexPointer(3, 5126, 12, 0);
                        SkyRenderHandler.this.starVBO.drawArrays(7);
                        SkyRenderHandler.this.starVBO.unbindBuffer();
                        GlStateManager.glDisableClientState(32884);
                    }
                    else
                    {
                        GlStateManager.callList(SkyRenderHandler.this.starGLCallList);
                    }
                }
            }
        }
    }

    private class SkyFrontRenderer extends IRenderHandler {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            if (mc.world.provider.isSurfaceWorld())
            {
                Vec3d vec3d = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
                float f = (float)vec3d.x;
                float f1 = (float)vec3d.y;
                float f2 = (float)vec3d.z;

                if (mc.gameSettings.anaglyph)
                {
                    float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
                    float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
                    float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                    f = f3;
                    f1 = f4;
                    f2 = f5;
                }

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableFog();
                GlStateManager.popMatrix();
                GlStateManager.disableTexture2D();
                GlStateManager.color(0.0F, 0.0F, 0.0F);
                double d3 = mc.player.getPositionEyes(partialTicks).y - world.getHorizon();

                if (d3 < 0.0D)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F, 12.0F, 0.0F);

                    if (SkyRenderHandler.this.vboEnabled)
                    {
                        SkyRenderHandler.this.sky2VBO.bindBuffer();
                        GlStateManager.glEnableClientState(32884);
                        GlStateManager.glVertexPointer(3, 5126, 12, 0);
                        SkyRenderHandler.this.sky2VBO.drawArrays(7);
                        SkyRenderHandler.this.sky2VBO.unbindBuffer();
                        GlStateManager.glDisableClientState(32884);
                    }
                    else
                    {
                        GlStateManager.callList(SkyRenderHandler.this.glSkyList2);
                    }

                    GlStateManager.popMatrix();
                    float f18 = 1.0F;
                    float f19 = -((float)(d3 + 65.0D));
                    float f20 = -1.0F;
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                    tessellator.draw();
                }

                if (world.provider.isSkyColored())
                {
                    GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
                }
                else
                {
                    GlStateManager.color(f, f1, f2);
                }

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, -((float)(d3 - 16.0D)), 0.0F);
                GlStateManager.callList(SkyRenderHandler.this.glSkyList2);
                GlStateManager.popMatrix();
                GlStateManager.enableTexture2D();
                GlStateManager.depthMask(true);
            }
        }
    }
}
