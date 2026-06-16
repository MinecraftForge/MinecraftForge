/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.level;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(TrySleepTest.MOD_ID)
public class TrySleepTest extends BaseTestMod {
    static final String MOD_ID = "try_sleep";

    public TrySleepTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void sleep_obstructed(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerFull(GameType.SURVIVAL);

        var bed = putBed(helper);
        helper.setAndAssertBlock(bed.above(), Blocks.STONE);
        setTimeAndTest(helper, ClockTimeMarkers.NIGHT, bed, player, false, "Player was able to sleep in an obstructed bed.");
    }

    @GameTest
    public static void sleep_daytime(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerFull(GameType.SURVIVAL);
        var bed = putBed(helper);
        setTimeAndTest(helper, ClockTimeMarkers.DAY, bed, player, false, "Player was able to sleep during daytime.");
    }

    @GameTest
    public static void sleep_unsafe(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerFull(GameType.SURVIVAL);
        var bed = putBed(helper);
        helper.spawn(EntityTypes.ZOMBIE, bed.east());
        setTimeAndTest(helper, ClockTimeMarkers.NIGHT, bed, player, false, "Player was able to sleep in an unsafe bed.");
    }

    @GameTest
    public static void sleep_normally(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerFull(GameType.SURVIVAL);
        var bed = putBed(helper);
        setTimeAndTest(helper, ClockTimeMarkers.NIGHT, bed, player, true, "Player was not able to sleep. There might be a slime or something causing this to fail.");
    }

    private static BlockPos putBed(GameTestHelper helper) {
        var mid = new BlockPos(0,0,0);
        var south = mid.south();
        helper.setAndAssertBlock(south, Blocks.BED.black().defaultBlockState());
        helper.setAndAssertBlock(mid, Blocks.BED.black().defaultBlockState().setValue(BedBlock.PART, BedPart.HEAD));
        return mid;
    }

    private static void setTimeAndTest(GameTestHelper helper, ResourceKey<ClockTimeMarker> time, BlockPos bed, Player player, boolean shouldBeSleeping, String err) {
        var manager = helper.getLevel().clockManager();
        var overworld = helper.getLevel().registryAccess().getOrThrow(WorldClocks.OVERWORLD);
        var origTime = manager.getTotalTicks(overworld);

        player.setPos(Vec3.atBottomCenterOf(helper.absolutePos(bed)));
        manager.moveToTimeMarker(overworld, time);
        helper.getLevel().tick(() -> true);
        helper.useBlock(bed, player);
        var sleep = player.isSleeping();
        manager.setTotalTicks(overworld, origTime);
        helper.assertTrue(sleep == shouldBeSleeping, err);
        helper.succeed();
    }
}
