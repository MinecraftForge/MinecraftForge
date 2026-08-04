/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.common;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;


/**
 * This is called back during world generation.
 *
 * @author cpw
 *
 */
public interface IWorldGenerator
{
    /**
     * Generate some world
     *
     * @param random the chunk specific {@link Random}.
     * @param chunkX the chunk X coordinate of this chunk.
     * @param chunkZ the chunk Z coordinate of this chunk.
     * @param world : additionalData[0] The minecraft {@link World} we're generating for.
     * @param chunkGenerator : additionalData[1] The {@link IChunkProvider} that is generating.
     * @param chunkProvider : additionalData[2] {@link IChunkProvider} that is requesting the world generation.
     *
     */
    void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator, AbstractChunkProvider chunkProvider);
}
