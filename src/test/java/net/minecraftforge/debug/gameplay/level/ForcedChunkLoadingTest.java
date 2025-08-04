/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.level;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(ForcedChunkLoadingTest.MOD_ID)
public class ForcedChunkLoadingTest extends BaseTestMod {
    static final String MOD_ID = "forced_chunk_loading";

    private static final int MAX_CHUNK_LOCATION_ATTEMPTS = 5;

    public ForcedChunkLoadingTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void force_far_away_chunk(GameTestHelper helper) {
        var random = RandomSource.create();
        var level = helper.getLevel();
        var chunkSource = level.getChunkSource();

        // try to find a far away chunk that is not already ticking
        int attempts;
        var origin = helper.absolutePos(BlockPos.ZERO);
        ChunkAccess chunk = null;
        for (attempts = 0; attempts < MAX_CHUNK_LOCATION_ATTEMPTS && chunk == null; attempts++) {
            int x = random.nextInt(1, 10) * 10000;
            int z = random.nextInt(1, 10) * 10000;
            chunk = level.getChunk(origin.offset(x, 0, z));

            // If the chunk is already ticking, we can't force it
            chunk = chunkSource.isPositionTicking(chunk.getPos().toLong()) ? null : chunk;
        }

        if (chunk == null) {
            helper.fail(Component.literal("Failed to find a far away chunk that is not already ticking after " + MAX_CHUNK_LOCATION_ATTEMPTS + " attempts"));
            return;
        } else if (attempts > 1) {
            helper.say("WARNING: Finding a far away chunk took " + attempts + " attempts.", ChatFormatting.YELLOW);
        }

        var pos = chunk.getPos();
        var posLong = pos.toLong();
        helper.say("Attempting to force far away chunk: " + pos, ChatFormatting.YELLOW);
        chunkSource.updateChunkForced(pos, true);

        helper.runAfterDelay(20, () -> {
            helper.assertTrue(chunkSource.chunkMap.getDistanceManager().shouldForceTicks(posLong), "Chunk is not ticketed as a force loaded chunk");
            helper.assertTrue(chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(pos.toLong()), "Forced chunk cannot tick entities");
            helper.assertTrue(chunkSource.chunkMap.getDistanceManager().inBlockTickingRange(pos.toLong()), "Forced chunk cannot tick blocks");
            helper.assertTrue(chunkSource.hasChunk(pos.x, pos.z), "Chunk is not loaded despite being ticketed as a force loaded chunk");
            helper.succeed();
        });
    }
}
