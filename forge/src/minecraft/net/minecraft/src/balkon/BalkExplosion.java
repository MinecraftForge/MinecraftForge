package net.minecraft.src.balkon;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.src.*;

public class BalkExplosion extends Explosion
{
    private World worldObj;

    public BalkExplosion(World world, Entity entity, double d, double d1, double d2, float f)
    {
        super(world, entity, d, d1, d2, f);
        worldObj = world;
    }

    public void doExplosionA()
    {
        super.doExplosionA();
    }

    public void doExplosionB(boolean flag, boolean flag1, boolean flag2)
    {
        worldObj.playSoundEffect(explosionX, explosionY, explosionZ, "random.explode", 4F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        if (flag1)
        {
            worldObj.spawnParticle("hugeexplosion", explosionX, explosionY, explosionZ, 0.0D, 0.0D, 0.0D);
        }
        ArrayList arraylist = new ArrayList();
        arraylist.addAll(destroyedBlockPositions);
        for (int i = arraylist.size() - 1; i >= 0; i--)
        {
            ChunkPosition chunkposition = (ChunkPosition)arraylist.get(i);
            int j = chunkposition.x;
            int k = chunkposition.y;
            int l = chunkposition.z;
            int i1 = worldObj.getBlockId(j, k, l);
            if (flag)
            {
                double d = (float)j + worldObj.rand.nextFloat();
                double d1 = (float)k + worldObj.rand.nextFloat();
                double d2 = (float)l + worldObj.rand.nextFloat();
                double d3 = d - explosionX;
                double d4 = d1 - explosionY;
                double d5 = d2 - explosionZ;
                double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                d3 /= d6;
                d4 /= d6;
                d5 /= d6;
                double d7 = 0.5D / (d6 / (double)explosionSize + 0.10000000000000001D);
                d7 *= worldObj.rand.nextFloat() * worldObj.rand.nextFloat() + 0.3F;
                d3 *= d7;
                d4 *= d7;
                d5 *= d7;
                worldObj.spawnParticle("explode", (d + explosionX * 1.0D) / 2D, (d1 + explosionY * 1.0D) / 2D, (d2 + explosionZ * 1.0D) / 2D, d3, d4, d5);
                worldObj.spawnParticle("smoke", d, d1, d2, d3, d4, d5);
            }
            if (flag2 && i1 > 0)
            {
                Block.blocksList[i1].dropBlockAsItemWithChance(worldObj, j, k, l, worldObj.getBlockMetadata(j, k, l), 0.3F, 0);
                worldObj.setBlockWithNotify(j, k, l, 0);
                Block.blocksList[i1].onBlockDestroyedByExplosion(worldObj, j, k, l);
            }
        }
    }
}
