/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pipeline;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockAndTintGetter;

public class BlockInfo
{
    private static final Direction[] SIDES = Direction.values();

    private final BlockColors colors;
    private BlockAndTintGetter level;
    private BlockState state;
    private BlockPos blockPos;

    private final boolean[][][] t = new boolean[3][3][3];
    private final int[][][] s = new int[3][3][3];
    private final int[][][] b = new int[3][3][3];
    private final float[][][][] skyLight = new float[3][2][2][2];
    private final float[][][][] blockLight = new float[3][2][2][2];
    private final float[][][] ao = new float[3][3][3];

    private final int[] packed = new int[7];

    private boolean full;

    private int cachedTint = -1;
    private int cachedMultiplier = -1;

    public BlockInfo(BlockColors colors)
    {
        this.colors = colors;
    }

    public int getColorMultiplier(int tint)
    {
        if(cachedTint == tint) return cachedMultiplier;
        cachedTint = tint;
        cachedMultiplier = colors.getColor(state, level, blockPos, tint);
        return cachedMultiplier;
    }

    public void setLevel(BlockAndTintGetter level)
    {
        this.level = level;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    public void setState(BlockState state)
    {
        this.state = state;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    public void setBlockPos(BlockPos blockPos)
    {
        this.blockPos = blockPos;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    public void reset()
    {
        this.level = null;
        this.state = null;
        this.blockPos = null;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    private float combine(int c, int s1, int s2, int s3, boolean t0, boolean t1, boolean t2, boolean t3)
    {
        if (c  == 0 && !t0) c  = Math.max(0, Math.max(s1, s2) - 1);
        if (s1 == 0 && !t1) s1 = Math.max(0, c - 1);
        if (s2 == 0 && !t2) s2 = Math.max(0, c - 1);
        if (s3 == 0 && !t3) s3 = Math.max(0, Math.max(s1, s2) - 1);
        return (c + s1 + s2 + s3) / (0xF * 4f);
    }

    public void updateLightMatrix()
    {
        for(int x = 0; x <= 2; x++)
        {
            for(int y = 0; y <= 2; y++)
            {
                for(int z = 0; z <= 2; z++)
                {
                    BlockPos pos = blockPos.offset(x - 1, y - 1, z - 1);
                    BlockState state = level.getBlockState(pos);
                    t[x][y][z] = state.getLightBlock(level, pos) < 15;
                    int brightness = LevelRenderer.getLightColor(level, pos);
                    s[x][y][z] = LightTexture.sky(brightness);
                    b[x][y][z] = LightTexture.block(brightness);
                    ao[x][y][z] = state.getShadeBrightness(level, pos);
                }
            }
        }
        for(Direction side : SIDES)
        {
            BlockPos pos = blockPos.relative(side);
            BlockState state = level.getBlockState(pos);

            BlockState thisStateShape = this.state.canOcclude() && this.state.useShapeForLightOcclusion() ? this.state : Blocks.AIR.defaultBlockState();
            BlockState otherStateShape = state.canOcclude() && state.useShapeForLightOcclusion() ? state : Blocks.AIR.defaultBlockState();

            if(state.getLightBlock(level, pos) == 15 || Shapes.faceShapeOccludes(thisStateShape.getFaceOcclusionShape(level, blockPos, side), otherStateShape.getFaceOcclusionShape(level, pos, side.getOpposite())))
            {
                int x = side.getStepX() + 1;
                int y = side.getStepY() + 1;
                int z = side.getStepZ() + 1;
                s[x][y][z] = Math.max(s[1][1][1] - 1, s[x][y][z]);
                b[x][y][z] = Math.max(b[1][1][1] - 1, b[x][y][z]);
            }
        }
        for(int x = 0; x < 2; x++)
        {
            for(int y = 0; y < 2; y++)
            {
                for(int z = 0; z < 2; z++)
                {
                    int x1 = x * 2;
                    int y1 = y * 2;
                    int z1 = z * 2;

                    int     sxyz = s[x1][y1][z1];
                    int     bxyz = b[x1][y1][z1];
                    boolean txyz = t[x1][y1][z1];

                    int     sxz = s[x1][1][z1], sxy = s[x1][y1][1], syz = s[1][y1][z1];
                    int     bxz = b[x1][1][z1], bxy = b[x1][y1][1], byz = b[1][y1][z1];
                    boolean txz = t[x1][1][z1], txy = t[x1][y1][1], tyz = t[1][y1][z1];

                    int     sx = s[x1][1][1], sy = s[1][y1][1], sz = s[1][1][z1];
                    int     bx = b[x1][1][1], by = b[1][y1][1], bz = b[1][1][z1];
                    boolean tx = t[x1][1][1], ty = t[1][y1][1], tz = t[1][1][z1];

                    skyLight  [0][x][y][z] = combine(sx, sxz, sxy, txz || txy ? sxyz : sx,
                                                     tx, txz, txy, txz || txy ? txyz : tx);
                    blockLight[0][x][y][z] = combine(bx, bxz, bxy, txz || txy ? bxyz : bx,
                                                     tx, txz, txy, txz || txy ? txyz : tx);

                    skyLight  [1][x][y][z] = combine(sy, sxy, syz, txy || tyz ? sxyz : sy,
                                                     ty, txy, tyz, txy || tyz ? txyz : ty);
                    blockLight[1][x][y][z] = combine(by, bxy, byz, txy || tyz ? bxyz : by,
                                                     ty, txy, tyz, txy || tyz ? txyz : ty);

                    skyLight  [2][x][y][z] = combine(sz, syz, sxz, tyz || txz ? sxyz : sz,
                                                     tz, tyz, txz, tyz || txz ? txyz : tz);
                    blockLight[2][x][y][z] = combine(bz, byz, bxz, tyz || txz ? bxyz : bz,
                                                     tz, tyz, txz, tyz || txz ? txyz : tz);
                }
            }
        }
    }

    public void updateFlatLighting()
    {
        full = Block.isShapeFullBlock(state.getCollisionShape(level, blockPos));
        packed[0] = LevelRenderer.getLightColor(level, blockPos);

        for (Direction side : SIDES)
        {
            int i = side.ordinal() + 1;
            packed[i] = LevelRenderer.getLightColor(level, blockPos.relative(side));
        }
    }

    public BlockAndTintGetter getLevel()
    {
        return level;
    }

    public BlockState getState()
    {
        return state;
    }

    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    public boolean[][][] getTranslucent()
    {
        return t;
    }

    public float[][][][] getSkyLight()
    {
        return skyLight;
    }

    public float[][][][] getBlockLight()
    {
        return blockLight;
    }

    public float[][][] getAo()
    {
        return ao;
    }

    public int[] getPackedLight()
    {
        return packed;
    }

    public boolean isFullCube()
    {
        return full;
    }

    public int getCachedTint()
    {
        return cachedTint;
    }

    public int getCachedMultiplier()
    {
        return cachedMultiplier;
    }
}
