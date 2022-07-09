/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.lighting;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implementation of {@link QuadLighter} that lights {@link BakedQuad quads} with flat lighting.
 */
public class FlatQuadLighter extends QuadLighter
{
    private static final Direction[] SIDES = Direction.values();

    private static final float MAX_POSITION = 1f - 1e-2f;
    private static final byte MAX_NORMAL = 127;

    private boolean isFullCube;
    private final int[] packedLight = new int[7];

    public FlatQuadLighter(BlockColors colors)
    {
        super(colors);
    }

    @Override
    protected void computeLightingAt(BlockAndTintGetter level, BlockPos pos, BlockState state)
    {
        isFullCube = Block.isShapeFullBlock(state.getCollisionShape(level, pos));
        for (Direction side : SIDES)
        {
            packedLight[side.ordinal()] = LevelRenderer.getLightColor(level, pos.relative(side));
        }
        packedLight[6] = LevelRenderer.getLightColor(level, pos);
    }

    @Override
    protected float calculateBrightness(float[] position)
    {
        return 1.0f; // No shading in flat lighting
    }

    @Override
    protected int calculateLightmap(float[] position, byte[] normal)
    {
        if ((isFullCube || position[1] < -MAX_POSITION) && normal[1] <= -MAX_NORMAL)
            return packedLight[Direction.DOWN.ordinal()];

        if ((isFullCube || position[1] > MAX_POSITION) && normal[1] >= MAX_NORMAL)
            return packedLight[Direction.UP.ordinal()];

        if ((isFullCube || position[2] < -MAX_POSITION) && normal[2] <= -MAX_NORMAL)
            return packedLight[Direction.NORTH.ordinal()];

        if ((isFullCube || position[2] > MAX_POSITION) && normal[2] >= MAX_NORMAL)
            return packedLight[Direction.SOUTH.ordinal()];

        if ((isFullCube || position[0] < -MAX_POSITION) && normal[0] <= -MAX_NORMAL)
            return packedLight[Direction.WEST.ordinal()];

        if ((isFullCube || position[0] > MAX_POSITION) && normal[0] >= MAX_NORMAL)
            return packedLight[Direction.EAST.ordinal()];

        return packedLight[6];
    }
}
