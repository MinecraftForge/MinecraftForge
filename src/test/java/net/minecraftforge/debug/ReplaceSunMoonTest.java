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

package net.minecraftforge.debug;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.SkyRenderHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ReplaceSunMoonTest.MODID, name = "Replace Sun & Moon", version = "0.0.0", acceptableRemoteVersions = "*")
public class ReplaceSunMoonTest {
    public static final String MODID = "replacesunmoon";

    public static final boolean REMOVE_SUN = false;
    public static final boolean REMOVE_MOON = true;
    public static final boolean REMOVE_STARS = true;
    public static final boolean REPLACE_SUN = true;
    public static final boolean REPLACE_MOON = true;

    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event)
        {
            World world = event.getWorld();
            if(world.isRemote && world.provider.isSurfaceWorld())
            {
                SkyRenderHandler renderHandler = world.provider.getSkyRenderHandler();
                /*if(REMOVE_SUN)
                    renderHandler.unregisterVanillaRenderer(SkyRenderPass.SUN_MOON_STARS, new ResourceLocation("sun"));
                if(REMOVE_MOON)
                    renderHandler.unregisterVanillaRenderer(SkyRenderPass.SUN_MOON_STARS, new ResourceLocation("moon"));
                if(REMOVE_STARS)
                    renderHandler.unregisterVanillaRenderer(SkyRenderPass.SUN_MOON_STARS, new ResourceLocation("stars"));

                if(REPLACE_SUN)
                    renderHandler.registerSkyRenderer(SkyRenderPass.SUN_MOON_STARS, new ResourceLocation("sun"), new SunRenderer(), 2.0);
                if(REPLACE_MOON)
                    renderHandler.registerSkyRenderer(SkyRenderPass.SUN_MOON_STARS, new ResourceLocation("moon"), new MoonRenderer(), 1.0);*/
            }
        }
    }

    private static class SunRenderer extends IRenderHandler
    {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            float clearness = 1.0F - world.getRainStrength(partialTicks);
            float size = 30.0F;

            GlStateManager.enableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            GlStateManager.color(0.7F, 0.1F, 0.1F, clearness);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            mc.renderEngine.bindTexture(SUN_TEXTURES);
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos((double)(-size), 100.0D, (double)(-size)).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos((double)size, 100.0D, (double)(-size)).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos((double)size, 100.0D, (double)size).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos((double)(-size), 100.0D, (double)size).tex(0.0D, 1.0D).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    private static class MoonRenderer extends IRenderHandler
    {
        @Override
        public void render(float partialTicks, WorldClient world, Minecraft mc)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            float clearness = 1.0F - world.getRainStrength(partialTicks);
            float size = 20.0F;

            GlStateManager.enableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 0.3F, 0.3F, clearness);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            mc.renderEngine.bindTexture(MOON_PHASES_TEXTURES);

            int moonPhase = world.getMoonPhase();
            int progress = moonPhase % 4;
            int waxing = moonPhase / 4 % 2;
            float u1 = (float)(progress + 0) / 4.0F;
            float v1 = (float)(waxing + 0) / 2.0F;
            float u2 = (float)(progress + 1) / 4.0F;
            float v2 = (float)(waxing + 1) / 2.0F;

            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos((double)(-size), -100.0D, (double)size).tex((double)u2, (double)v2).endVertex();
            bufferbuilder.pos((double)size, -100.0D, (double)size).tex((double)u1, (double)v2).endVertex();
            bufferbuilder.pos((double)size, -100.0D, (double)(-size)).tex((double)u1, (double)v1).endVertex();
            bufferbuilder.pos((double)(-size), -100.0D, (double)(-size)).tex((double)u2, (double)v1).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }
}
