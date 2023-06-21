/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.lighting;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

/**
 * Implementation of {@link QuadLighter} that lights {@link BakedQuad baked quads} using ambient occlusion and
 * light interpolation.
 */
public class SmoothQuadLighter extends QuadLighter
{
    private static final Direction[] SIDES = Direction.values();

    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private final boolean[][][] t = new boolean[3][3][3];
    private final int[][][] s = new int[3][3][3];
    private final int[][][] b = new int[3][3][3];
    private final float[][][][] skyLight = new float[3][2][2][2];
    private final float[][][][] blockLight = new float[3][2][2][2];
    private final float[][][] ao = new float[3][3][3];

    public SmoothQuadLighter(BlockColors colors)
    {
        super(colors);
    }

    @Override
    protected void computeLightingAt(BlockAndTintGetter level, BlockPos origin, BlockState state)
    {
        for (int x = 0; x <= 2; x++)
        {
            for (int y = 0; y <= 2; y++)
            {
                for (int z = 0; z <= 2; z++)
                {
                    pos.setWithOffset(origin, x - 1, y - 1, z - 1);
                    BlockState neighborState = level.getBlockState(pos);
                    t[x][y][z] = neighborState.getLightBlock(level, pos) < 15;
                    int brightness = getLightColor(level, pos, state);
                    s[x][y][z] = LightTexture.sky(brightness);
                    b[x][y][z] = LightTexture.block(brightness);
                    ao[x][y][z] = neighborState.getShadeBrightness(level, pos);
                }
            }
        }
        for (Direction side : SIDES)
        {
            pos.setWithOffset(origin, side);
            BlockState neighborState = level.getBlockState(pos);

            BlockState thisStateShape = state.canOcclude() && state.useShapeForLightOcclusion() ? state : Blocks.AIR.defaultBlockState();
            BlockState otherStateShape = neighborState.canOcclude() && neighborState.useShapeForLightOcclusion() ? neighborState : Blocks.AIR.defaultBlockState();

            if (neighborState.getLightBlock(level, pos) == 15 || Shapes.faceShapeOccludes(thisStateShape.getFaceOcclusionShape(level, origin, side), otherStateShape.getFaceOcclusionShape(level, pos, side.getOpposite())))
            {
                int x = side.getStepX() + 1;
                int y = side.getStepY() + 1;
                int z = side.getStepZ() + 1;
                s[x][y][z] = Math.max(s[1][1][1] - 1, s[x][y][z]);
                b[x][y][z] = Math.max(b[1][1][1] - 1, b[x][y][z]);
            }
        }
        for (int x = 0; x < 2; x++)
        {
            for (int y = 0; y < 2; y++)
            {
                for (int z = 0; z < 2; z++)
                {
                    int x1 = x * 2;
                    int y1 = y * 2;
                    int z1 = z * 2;

                    int sxyz = s[x1][y1][z1];
                    int bxyz = b[x1][y1][z1];
                    boolean txyz = t[x1][y1][z1];

                    int sxz = s[x1][1][z1], sxy = s[x1][y1][1], syz = s[1][y1][z1];
                    int bxz = b[x1][1][z1], bxy = b[x1][y1][1], byz = b[1][y1][z1];
                    boolean txz = t[x1][1][z1], txy = t[x1][y1][1], tyz = t[1][y1][z1];

                    int sx = s[x1][1][1], sy = s[1][y1][1], sz = s[1][1][z1];
                    int bx = b[x1][1][1], by = b[1][y1][1], bz = b[1][1][z1];
                    boolean tx = t[x1][1][1], ty = t[1][y1][1], tz = t[1][1][z1];

                    skyLight[0][x][y][z] = combine(sx, sxz, sxy, txz || txy ? sxyz : sx,
                            tx, txz, txy, txz || txy ? txyz : tx);
                    blockLight[0][x][y][z] = combine(bx, bxz, bxy, txz || txy ? bxyz : bx,
                            tx, txz, txy, txz || txy ? txyz : tx);

                    skyLight[1][x][y][z] = combine(sy, sxy, syz, txy || tyz ? sxyz : sy,
                            ty, txy, tyz, txy || tyz ? txyz : ty);
                    blockLight[1][x][y][z] = combine(by, bxy, byz, txy || tyz ? bxyz : by,
                            ty, txy, tyz, txy || tyz ? txyz : ty);

                    skyLight[2][x][y][z] = combine(sz, syz, sxz, tyz || txz ? sxyz : sz,
                            tz, tyz, txz, tyz || txz ? txyz : tz);
                    blockLight[2][x][y][z] = combine(bz, byz, bxz, tyz || txz ? bxyz : bz,
                            tz, tyz, txz, tyz || txz ? txyz : tz);
                }
            }
        }
    }

    @Override
    protected float calculateBrightness(float[] position)
    {
        float x = position[0], y = position[1], z = position[2];
        int sx = x < 0 ? 1 : 2;
        int sy = y < 0 ? 1 : 2;
        int sz = z < 0 ? 1 : 2;

        if (x < 0) x++;
        if (y < 0) y++;
        if (z < 0) z++;

        float a = 0;
        a += ao[sx - 1][sy - 1][sz - 1] * (1 - x) * (1 - y) * (1 - z);
        a += ao[sx - 1][sy - 1][sz - 0] * (1 - x) * (1 - y) * (0 + z);
        a += ao[sx - 1][sy - 0][sz - 1] * (1 - x) * (0 + y) * (1 - z);
        a += ao[sx - 1][sy - 0][sz - 0] * (1 - x) * (0 + y) * (0 + z);
        a += ao[sx - 0][sy - 1][sz - 1] * (0 + x) * (1 - y) * (1 - z);
        a += ao[sx - 0][sy - 1][sz - 0] * (0 + x) * (1 - y) * (0 + z);
        a += ao[sx - 0][sy - 0][sz - 1] * (0 + x) * (0 + y) * (1 - z);
        a += ao[sx - 0][sy - 0][sz - 0] * (0 + x) * (0 + y) * (0 + z);

        a = Mth.clamp(a, 0, 1);
        return a;
    }

    @Override
    protected int calculateLightmap(float[] position, byte[] normal)
    {
        var block = (int) (calcLightmap(blockLight, position[0], position[1], position[2]) * 0xF0);
        var sky = (int) (calcLightmap(skyLight, position[0], position[1], position[2]) * 0xF0);
        return block | (sky << 16);
    }

    private float combine(int c, int s1, int s2, int s3, boolean t0, boolean t1, boolean t2, boolean t3)
    {
        if (c == 0 && !t0) c = Math.max(0, Math.max(s1, s2) - 1);
        if (s1 == 0 && !t1) s1 = Math.max(0, c - 1);
        if (s2 == 0 && !t2) s2 = Math.max(0, c - 1);
        if (s3 == 0 && !t3) s3 = Math.max(0, Math.max(s1, s2) - 1);
        return (c + s1 + s2 + s3) / (0xF * 4f);
    }

    protected float calcLightmap(float[][][][] light, float x, float y, float z)
    {
        x *= 2;
        y *= 2;
        z *= 2;
        float l2 = x * x + y * y + z * z;
        if (l2 > 6 - 2e-2f)
        {
            float s = (float) Math.sqrt((6 - 2e-2f) / l2);
            x *= s;
            y *= s;
            z *= s;
        }
        float ax = x > 0 ? x : -x;
        float ay = y > 0 ? y : -y;
        float az = z > 0 ? z : -z;
        float e1 = 1 + 1e-4f;
        if (ax > 2 - 1e-4f && ay <= e1 && az <= e1)
        {
            x = x < 0 ? -2 + 1e-4f : 2 - 1e-4f;
        }
        else if (ay > 2 - 1e-4f && az <= e1 && ax <= e1)
        {
            y = y < 0 ? -2 + 1e-4f : 2 - 1e-4f;
        }
        else if (az > 2 - 1e-4f && ax <= e1 && ay <= e1)
        {
            z = z < 0 ? -2 + 1e-4f : 2 - 1e-4f;
        }
        ax = x > 0 ? x : -x;
        ay = y > 0 ? y : -y;
        az = z > 0 ? z : -z;
        if (ax <= e1 && ay + az > 3f - 1e-4f)
        {
            float s = (3f - 1e-4f) / (ay + az);
            y *= s;
            z *= s;
        }
        else if (ay <= e1 && az + ax > 3f - 1e-4f)
        {
            float s = (3f - 1e-4f) / (az + ax);
            z *= s;
            x *= s;
        }
        else if (az <= e1 && ax + ay > 3f - 1e-4f)
        {
            float s = (3f - 1e-4f) / (ax + ay);
            x *= s;
            y *= s;
        }
        else if (ax + ay + az > 4 - 1e-4f)
        {
            float s = (4 - 1e-4f) / (ax + ay + az);
            x *= s;
            y *= s;
            z *= s;
        }

        float l = 0;
        float s = 0;

        for (int ix = 0; ix <= 1; ix++)
        {
            for (int iy = 0; iy <= 1; iy++)
            {
                for (int iz = 0; iz <= 1; iz++)
                {
                    float vx = x * (1 - ix * 2);
                    float vy = y * (1 - iy * 2);
                    float vz = z * (1 - iz * 2);

                    float s3 = vx + vy + vz + 4;
                    float sx = vy + vz + 3;
                    float sy = vz + vx + 3;
                    float sz = vx + vy + 3;

                    float bx = (2 * vx + vy + vz + 6) / (s3 * sy * sz * (vx + 2));
                    s += bx;
                    l += bx * light[0][ix][iy][iz];

                    float by = (2 * vy + vz + vx + 6) / (s3 * sz * sx * (vy + 2));
                    s += by;
                    l += by * light[1][ix][iy][iz];

                    float bz = (2 * vz + vx + vy + 6) / (s3 * sx * sy * (vz + 2));
                    s += bz;
                    l += bz * light[2][ix][iy][iz];
                }
            }
        }

        l /= s;
        l = Mth.clamp(l, 0, 1);
        return l;
    }
}
