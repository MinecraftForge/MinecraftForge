/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.level;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(TrySleepTest.MOD_ID)
public class TrySleepTest extends BaseTestMod {
    static final String MOD_ID = "try_sleep";
    static final long NIGHT = 13000;
    static final long DAY = 0;

    public TrySleepTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void sleep_obstructed(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);

        var bed = putBed(helper);
        helper.setAndAssertBlock(bed.above(), Blocks.STONE);
        setTimeAndTest(helper, NIGHT, bed, player, false, "Player was able to sleep in an obstructed bed.");
    }

    @GameTest
    public static void sleep_daytime(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);
        var bed = putBed(helper);
        setTimeAndTest(helper, DAY, bed, player, false, "Player was able to sleep during daytime.");
    }

    @GameTest
    public static void sleep_unsafe(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);
        var bed = putBed(helper);
        helper.spawn(EntityType.ZOMBIE, bed.east());
        setTimeAndTest(helper, NIGHT, bed, player, false, "Player was able to sleep in an unsafe bed.");
    }

    @GameTest
    public static void sleep_normally(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);
        var bed = putBed(helper);
        setTimeAndTest(helper, NIGHT, bed, player, true, "Player was not able to sleep. There might be a slime or something causing this to fail.");
    }

    private static BlockPos putBed(GameTestHelper helper) {
        var mid = new BlockPos(0,0,0);
        var south = mid.south();
        helper.setAndAssertBlock(south, Blocks.BLACK_BED.defaultBlockState());
        helper.setAndAssertBlock(mid, Blocks.BLACK_BED.defaultBlockState().setValue(BedBlock.PART, BedPart.HEAD));
        return mid;
    }

    private static void setTimeAndTest(GameTestHelper helper, long time, BlockPos bed, Player player, boolean shouldBeSleeping, String err) {
        var origTime = helper.getLevel().getDayTime();
        player.setPos(helper.absolutePos(bed).getCenter());
        helper.getLevel().setDayTime(time);
        helper.getLevel().tick(() -> true);
        helper.useBlock(bed, player);
        var sleep = player.isSleeping();
        helper.getLevel().setDayTime(origTime);
        helper.assertTrue(sleep == shouldBeSleeping, err);
        helper.succeed();
    }
}
