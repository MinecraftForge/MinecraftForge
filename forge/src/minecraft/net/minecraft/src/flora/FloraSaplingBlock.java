package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.src.*;

public class FloraSaplingBlock extends BlockFlower
    implements ITextureProvider
{
    protected FloraSaplingBlock(int i, int j)
    {
        super(i, j);
        float f = 0.4F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
        return super.canPlaceBlockAt(world, i, j, k) && canThisPlantGrowOnThisBlockID(world.getBlockId(i, j - 1, k));
    }

    protected boolean canThisPlantGrowOnThisBlockID(int i)
    {
        return i == Block.grass.blockID || i == Block.dirt.blockID || i == Block.slowSand.blockID || i == Block.netherrack.blockID;
    }

    public FloraSaplingBlock(int i)
    {
        this(i, 52);
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (world.isRemote)
        {
            return;
        }
        super.updateTick(world, i, j, k, random);
        int md = world.getBlockMetadata(i, j, k);
        if (md % 8 == 0)
        {
            if (world.getBlockLightValue(i, j + 1, k) >= 9 && random.nextInt(120) == 0)
            {
                if ((md & 8) == 0)
                {
                    world.setBlockMetadataWithNotify(i, j, k, md | 8);
                }
                else
                {
                    growTree(world, i, j, k, random);
                }
            }
        }
        else if (md % 8 == 1 && random.nextInt(7) == 0)
        {
            if ((md & 8) == 0)
            {
                world.setBlockMetadataWithNotify(i, j, k, md | 8);
                int i1 = world.getBlockMetadata(i, j, k);
                System.out.println((new StringBuilder()).append("Sapling metadata: ").append(i1).toString());
            }
            else
            {
                growTree(world, i, j, k, random);
                System.out.println("Blood tree has grown");
            }
        }
        else if (md % 8 == 2 && random.nextInt(10) == 0)
        {
            if ((md & 8) == 0)
            {
                world.setBlockMetadataWithNotify(i, j, k, md | 8);
                int i1 = world.getBlockMetadata(i, j, k);
                System.out.println((new StringBuilder()).append("Sapling metadata: ").append(i1).toString());
            }
            else
            {
                growTree(world, i, j, k, random);
                System.out.println("Eucalyptus tree has grown");
            }
        }
        else if (md % 8 == 3 && random.nextInt(7) == 0)
        {
            if ((md & 8) == 0)
            {
                world.setBlockMetadataWithNotify(i, j, k, md | 8);
                int i1 = world.getBlockMetadata(i, j, k);
                System.out.println((new StringBuilder()).append("Sapling metadata: ").append(i1).toString());
            }
            else
            {
                growTree(world, i, j, k, random);
                System.out.println("Sakura tree has grown");
            }
        }
        else if (md % 8 == 4 && random.nextInt(7) == 0)
        {
            if ((md & 8) == 0)
            {
                world.setBlockMetadataWithNotify(i, j, k, md | 8);
                int i1 = world.getBlockMetadata(i, j, k);
                System.out.println((new StringBuilder()).append("Sapling metadata: ").append(i1).toString());
            }
            else
            {
                growTree(world, i, j, k, random);
                System.out.println("White tree has grown");
            }
        }
    }

    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        //j &= 8;
    	
    	return blockIndexInTexture + md % 8;
        /*if (md == 1)
        {
            return 53;
        } else
        if (md == 2)
        {
        	return 54;
        }
        else
        {
            return super.getBlockTextureFromSideAndMetadata(side, md);
        }*/
    }

    public void growTree(World world, int i, int j, int k, Random random)
    {
        int md = world.getBlockMetadata(i, j, k) % 8;
        world.setBlock(i, j, k, 0);
        Object obj = null;
        if (md == 1)
        {
            obj = new BloodTreeGen(3, 1);
        } else
        	
      	if (md == 2)
        {
            obj = new EucalyptusTreeGenShort(7, 2);
        } else
        
        if (md == 3) {
        	obj = new CherryTreeGen(true, mod_FloraSoma.redwood.blockID, 0);
        }  else
        	
        if (md == 4) {
          	obj = new WhiteTreeGen(true, mod_FloraSoma.redwood.blockID, 1);
        }  else
        	
        {
            obj = new RedwoodTreeGen(true);
        }
        if (!((WorldGenerator)obj).generate(world, random, i, j, k))
        {
            world.setBlockAndMetadata(i, j, k, blockID, md);
            //return true;
        }
        //return false;
    }

    protected int damageDropped(int i)
    {
        return i % 8;
    }

    public String getTextureFile()
    {
        return "/floratex/plantblocks.png";
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_FloraSoma.floraSapling, 1, 0));
        arraylist.add(new ItemStack(mod_FloraSoma.floraSapling, 1, 1));
        arraylist.add(new ItemStack(mod_FloraSoma.floraSapling, 1, 2));
        arraylist.add(new ItemStack(mod_FloraSoma.floraSapling, 1, 3));
        arraylist.add(new ItemStack(mod_FloraSoma.floraSapling, 1, 4));
    }
}
