/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CustomParticleTypeTest.MOD_ID)
public class CustomParticleTypeTest
{
    public static final String MOD_ID = "custom_particle_type_test";
    private static final boolean ENABLED = true;

    public CustomParticleTypeTest() { }

    @Mod.EventBusSubscriber(modid = CustomParticleTypeTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents
    {
        private static final ParticleRenderType CUSTOM_TYPE = new ParticleRenderType()
        {
            @Override
            public void begin(BufferBuilder buffer, TextureManager texMgr)
            {
                Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                ParticleRenderType.TERRAIN_SHEET.begin(buffer, texMgr);
            }

            @Override
            public void end(Tesselator tess)
            {
                ParticleRenderType.TERRAIN_SHEET.end(tess);
            }
        };
        private static final ParticleRenderType CUSTOM_TYPE_TWO = new ParticleRenderType()
        {
            @Override
            public void begin(BufferBuilder buffer, TextureManager texMgr)
            {
                Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                ParticleRenderType.TERRAIN_SHEET.begin(buffer, texMgr);
            }

            @Override
            public void end(Tesselator tess)
            {
                ParticleRenderType.TERRAIN_SHEET.end(tess);
            }
        };

        private static class CustomParticle extends TerrainParticle
        {
            public CustomParticle(ClientLevel level, double x, double y, double z)
            {
                super(level, x, y, z, 0, .25, 0, Blocks.OBSIDIAN.defaultBlockState());
            }

            @Override
            public ParticleRenderType getRenderType()
            {
                return CUSTOM_TYPE;
            }
        }
        private static class AnotherCustomParticle extends TerrainParticle
        {
            public AnotherCustomParticle(ClientLevel level, double x, double y, double z)
            {
                super(level, x, y, z, 0, .25, 0, Blocks.SAND.defaultBlockState());
            }

            @Override
            public ParticleRenderType getRenderType()
            {
                return CUSTOM_TYPE_TWO;
            }
        }

        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent event)
        {
            if (!ENABLED || event.phase != TickEvent.Phase.START) { return; }

            ClientLevel level = Minecraft.getInstance().level;
            Player player = Minecraft.getInstance().player;
            if (player == null || level == null || !player.isShiftKeyDown()) { return; }

            Minecraft.getInstance().particleEngine.add(new CustomParticle(level, player.getX(), player.getY(), player.getZ()));
            Minecraft.getInstance().particleEngine.add(new AnotherCustomParticle(level, player.getX(), player.getY(), player.getZ()));
        }
    }
}
