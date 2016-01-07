package net.minecraftforge.client.model.pipeline;

import net.minecraft.block.Block;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockInfo
{
    private IBlockAccess world;
    private Block block;
    private BlockPos blockPos;

    private final boolean[][][] translucent = new boolean[3][3][3];
    private final int[][][] s = new int[3][3][3];
    private final int[][][] b = new int[3][3][3];
    private final float[][][][] skyLight = new float[3][2][2][2];
    private final float[][][][] blockLight = new float[3][2][2][2];
    private final float[][][] ao = new float[3][3][3];

    private float shx = 0, shy = 0, shz = 0;

    private int cachedTint = -1;
    private int cachedMultiplier = -1;

    public int getColorMultiplier(int tint)
    {
        if(cachedTint == tint) return cachedMultiplier;
        cachedTint = tint;
        cachedMultiplier = block.colorMultiplier(world, blockPos, tint);
        return cachedMultiplier;
    }

    public void updateShift()
    {
        updateShift(false);
    }

    public void updateShift(boolean ignoreY)
    {
        long rand = 0;
        if(block.getOffsetType() != EnumOffsetType.NONE)
        {
            rand = MathHelper.getCoordinateRandom(blockPos.getX(), ignoreY ? 0 : blockPos.getY(), blockPos.getZ());
            shx = ((float)((rand >> 16) & 0xF) / 0xF - .5f) * .5f;
            shz = ((float)((rand >> 24) & 0xF) / 0xF - .5f) * .5f;
            if(block.getOffsetType() == EnumOffsetType.XYZ)
            {
                shy = ((float)((rand >> 20) & 0xF) / 0xF - 1) * .2f;
            }
        }
    }

    public void setWorld(IBlockAccess world)
    {
        this.world = world;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    public void setBlock(Block block)
    {
        this.block = block;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    public void setBlockPos(BlockPos blockPos)
    {
        this.blockPos = blockPos;
        cachedTint = -1;
        cachedMultiplier = -1;
        shx = shy = shz = 0;
    }

    private float combine(int c, int s1, int s2, int s3)
    {
        if(c == 0) c = Math.max(0, Math.max(s1, s2) - 1);
        if(s1 == 0) s1 = Math.max(0, c - 1);
        if(s2 == 0) s2 = Math.max(0, c - 1);
        if(s3 == 0) s3 = Math.max(0, Math.max(s1, s2) - 1);
        return (float)(c + s1 + s2 + s3) * 0x20 / (4 * 0xFFFF);
    }

    public void updateLightMatrix()
    {
        boolean full = false;
        for(int x = 0; x <= 2; x++)
        {
            for(int y = 0; y <= 2; y++)
            {
                for(int z = 0; z <= 2; z++)
                {
                    BlockPos pos = blockPos.add(x - 1, y - 1, z - 1);
                    Block block = world.getBlockState(pos).getBlock();
                    translucent[x][y][z] = block.isTranslucent();
                    //translucent[x][y][z] = world.getBlockState(pos).getBlock().getLightOpacity(world, pos) == 0;
                    int brightness = this.block.getMixedBrightnessForBlock(world, pos);
                    s[x][y][z] = (brightness >> 0x14) & 0xF;
                    b[x][y][z] = (brightness >> 0x04) & 0xF;
                    ao[x][y][z] = block.getAmbientOcclusionLightValue();
                    if(x == 1 && y == 1 && z == 1)
                    {
                        full = block.isFullCube();
                    }
                }
            }
        }
        if(!full)
        {
            for(EnumFacing side : EnumFacing.values())
            {
                int x = side.getFrontOffsetX() + 1;
                int y = side.getFrontOffsetY() + 1;
                int z = side.getFrontOffsetZ() + 1;
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

                    boolean tx = translucent[x1][1][z1] || translucent[x1][y1][1];
                    skyLight[0][x][y][z] = combine(s[x1][1][1], s[x1][1][z1], s[x1][y1][1], tx ? s[x1][y1][z1] : s[x1][1][1]);
                    blockLight[0][x][y][z] = combine(b[x1][1][1], b[x1][1][z1], b[x1][y1][1], tx ? b[x1][y1][z1] : b[x1][1][1]);

                    boolean ty = translucent[x1][y1][1] || translucent[1][y1][z1];
                    skyLight[1][x][y][z] = combine(s[1][y1][1], s[x1][y1][1], s[1][y1][z1], ty ? s[x1][y1][z1] : s[1][y1][1]);
                    blockLight[1][x][y][z] = combine(b[1][y1][1], b[x1][y1][1], b[1][y1][z1], ty ? b[x1][y1][z1] : b[1][y1][1]);

                    boolean tz = translucent[1][y1][z1] || translucent[1][y1][z1];
                    skyLight[2][x][y][z] = combine(s[1][1][z1], s[1][y1][z1], s[x1][1][z1], tz ? s[x1][y1][z1] : s[1][1][z1]);
                    blockLight[2][x][y][z] = combine(b[1][1][z1], b[1][y1][z1], b[x1][1][z1], tz ? b[x1][y1][z1] : b[1][1][z1]);
                }
            }
        }
    }

    public IBlockAccess getWorld()
    {
        return world;
    }

    public Block getBlock()
    {
        return block;
    }

    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    public boolean[][][] getTranslucent()
    {
        return translucent;
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

    public float getShx()
    {
        return shx;
    }

    public float getShy()
    {
        return shy;
    }

    public float getShz()
    {
        return shz;
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
