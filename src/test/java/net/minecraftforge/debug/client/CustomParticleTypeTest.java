/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.lang.reflect.Field;
import java.util.Map;

@GameTestNamespace("forge")
@Mod(CustomParticleTypeTest.MOD_ID)
public class CustomParticleTypeTest extends BaseTestMod {
    public static final String MOD_ID = "custom_particle_type_test";
    private static final ParticleRenderType CUSTOM_TYPE = new ParticleRenderType("GRP_ONE");
    private static final ParticleRenderType CUSTOM_TYPE_TWO = new ParticleRenderType("GRP_TWO");
    private static final ParticleRenderType CUSTOM_TYPE_DUP = new ParticleRenderType("GRP_DUP");

    public CustomParticleTypeTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void is_custom_group(GameTestHelper helper) {
        helper.addRecordListener(TickEvent.ClientTickEvent.Pre.BUS, CustomParticleTypeTest::onClientTick);
        try {
            ParticleEngine.registerParticleGroup(CUSTOM_TYPE, engine -> new TestParticleGroup(engine, CUSTOM_TYPE));
            ParticleEngine.registerParticleGroup(CUSTOM_TYPE_TWO, engine -> new OtherTestParticleGroup(engine, CUSTOM_TYPE_TWO));
        } catch (IllegalArgumentException ignored) {} // OK to call multiple times.
        helper.runAfterDelay(20, () -> {
            // Wait a little bit so there are some particles around.
            // If the particles (field) map contains our types, then we know they were added successfully and the test is OK.
            var engine = Minecraft.getInstance().particleEngine;
            try {
                Field field = ParticleEngine.class.getDeclaredField("particles");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                var val = ((Map<ParticleRenderType, ParticleGroup<?>>) field.get(engine));
                var isPresentOne = val.get(CUSTOM_TYPE);
                var isPresentTwo = val.get(CUSTOM_TYPE_TWO);
                if (isPresentTwo instanceof OtherTestParticleGroup && isPresentOne instanceof TestParticleGroup) helper.succeed();
                else helper.fail("Particle types were not present when CustomParticleTypeTest checked");
            } catch (Exception e) {
                helper.fail("Unable to get 'particles' field from ParticleEngine, was it renamed or removed?");
            }
        });
    }

    @GameTest
    public static void prevent_duplicate_group(GameTestHelper helper) {
        boolean flag = false;
        try {
            ParticleEngine.registerParticleGroup(CUSTOM_TYPE_DUP, engine -> new TestParticleGroup(engine, CUSTOM_TYPE_DUP));
            ParticleEngine.registerParticleGroup(CUSTOM_TYPE_DUP, engine -> new TestParticleGroup(engine, CUSTOM_TYPE_DUP));
        } catch (IllegalArgumentException e) {
            flag = true;
        }
        helper.assertTrue(flag, "Test did not catch duplicate particle group registration.");
        helper.succeed();
    }

    private static class TestParticleGroup extends QuadParticleGroup {
        public TestParticleGroup(ParticleEngine p_422302_, ParticleRenderType p_427417_) {
            super(p_422302_, p_427417_);
        }
    }
    private static class OtherTestParticleGroup extends QuadParticleGroup {
        public OtherTestParticleGroup(ParticleEngine p_422302_, ParticleRenderType p_427417_) {
            super(p_422302_, p_427417_);
        }
    }

    private static class CustomParticle extends TerrainParticle {
        public CustomParticle(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z, 0, .25, 0, Blocks.OBSIDIAN.defaultBlockState());
        }

        @Override
        public ParticleRenderType getGroup()
        {
            return CUSTOM_TYPE;
        }
    }
    private static class AnotherCustomParticle extends TerrainParticle {
        public AnotherCustomParticle(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z, 0, .25, 0, Blocks.SAND.defaultBlockState());
        }

        @Override
        public ParticleRenderType getGroup()
        {
            return CUSTOM_TYPE_TWO;
        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent.Pre event) {
        ClientLevel level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (player == null || level == null) { return; }

        Minecraft.getInstance().particleEngine.add(new CustomParticle(level, player.getX(), player.getY(), player.getZ()));
        Minecraft.getInstance().particleEngine.add(new AnotherCustomParticle(level, player.getX(), player.getY(), player.getZ()));
    }
}
