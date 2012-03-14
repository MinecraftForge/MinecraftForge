package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.src.*;

public class FloraCrops extends BlockFlower
    implements ITextureProvider
{
    public FloraCrops(int i, int j)
    {
        super(i, j);
        blockIndexInTexture = j;
        this.setTickRandomly(true);
        float f = 0.5F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        super.updateTick(world, i, j, k, random);
        if (world.getBlockLightValue(i, j + 1, k) >= 9)
        {
            int l = world.getBlockMetadata(i, j, k);
            if (l < 3)
            {
                float f = getGrowthRate(world, i, j, k);
                if (random.nextInt((int)(25F / f) + 1) == 0)
                {
                    l++;
                    world.setBlockMetadataWithNotify(i, j, k, l);
                }
            }
        }
    }

    public void fertilize(World world, int i, int j, int k)
    {
        world.setBlockMetadataWithNotify(i, j, k, 3);
    }

    private float getGrowthRate(World world, int i, int j, int k)
    {
        float f = 1.0F;
        int l = world.getBlockId(i, j, k - 1);
        int i1 = world.getBlockId(i, j, k + 1);
        int j1 = world.getBlockId(i - 1, j, k);
        int k1 = world.getBlockId(i + 1, j, k);
        int l1 = world.getBlockId(i - 1, j, k - 1);
        int i2 = world.getBlockId(i + 1, j, k - 1);
        int j2 = world.getBlockId(i + 1, j, k + 1);
        int k2 = world.getBlockId(i - 1, j, k + 1);
        boolean flag = j1 == blockID || k1 == blockID;
        boolean flag1 = l == blockID || i1 == blockID;
        boolean flag2 = l1 == blockID || i2 == blockID || j2 == blockID || k2 == blockID;
        for (int l2 = i - 1; l2 <= i + 1; l2++)
        {
            for (int i3 = k - 1; i3 <= k + 1; i3++)
            {
                int j3 = world.getBlockId(l2, j - 1, i3);
                float f1 = 0.0F;
                if (j3 == Block.tilledField.blockID)
                {
                    f1 = 1.0F;
                    if (world.getBlockMetadata(l2, j - 1, i3) > 0)
                    {
                        f1 = 3F;
                    }
                }
                if (l2 != i || i3 != k)
                {
                    f1 /= 4F;
                }
                f += f1;
            }
        }

        if (flag2 || flag && flag1)
        {
            f /= 2.0F;
        }
        return f;
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (j < 0)
        {
            j = 3;
        }
        return blockIndexInTexture + j;
    }

    public int getRenderType()
    {
        return 6;
    }

    public ArrayList getBlockDropped(World world, int i, int j, int k, int l, int i1)
    {
        ArrayList arraylist = new ArrayList();
        if (l == 3)
        {
            arraylist.add(new ItemStack(mod_FloraSoma.food, 1, 0));
        }
        for (int j1 = 0; j1 < 3 + i1; j1++)
        {
            if (world.rand.nextInt(10) <= l)
            {
                arraylist.add(new ItemStack(mod_FloraSoma.barleySeed));
            }
        }

        return arraylist;
    }

    public int idDropped(int i, Random random, int j)
    {
        if (i == 3)
        {
            return Item.wheat.shiftedIndex;
        }
        else
        {
            return -1;
        }
    }

    public int quantityDropped(Random random)
    {
        return 1;
    }

    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }
}
