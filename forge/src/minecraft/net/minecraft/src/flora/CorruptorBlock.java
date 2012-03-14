package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.src.*;

public class CorruptorBlock extends Block
    implements ITextureProvider
{
    public CorruptorBlock(int i)
    {
        super(i, 64, Material.rock);
        blockIndexInTexture = 96;
        this.setTickRandomly(true);
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (world.isRemote)
        {
            return;
        }
        int l = world.getBlockMetadata(i, j, k);
        int i1 = world.getBlockId(i, j + 1, k);
        if (i1 != 0 && l >= 8)
        {
            world.setBlockMetadataWithNotify(i, j, k, l - 8);
        }
        if (l >= 8 && i1 == 0 && random.nextInt(15) == 0)
        {
            if (random.nextInt(2) == 0)
            {
                world.setBlock(i, j + 1, k, Block.mushroomBrown.blockID);
            }
            else
            {
                world.setBlock(i, j + 1, k, Block.mushroomRed.blockID);
            }
        }
        if (random.nextInt(mod_FloraSoma.corruptionSpeed) == 0)
        {
            int posX = (i + random.nextInt(3)) - 1;
            int posY = (j + random.nextInt(3)) - 1;
            int posZ = (k + random.nextInt(3)) - 1;
            int bID = world.getBlockId(posX, posY, posZ);
            int md = world.getBlockMetadata(posX, posY, posZ);
            Material material = world.getBlockMaterial(posX, posY, posZ);
            if (bID == Block.netherrack.blockID || bID == mod_FloraSoma.corruptBrick.blockID && md % 8 != l)
            {
                if (l >= 8)
                {
                    int j1 = world.getBlockId(posX, posY + 1, posZ);
                    if (j1 == 0)
                    {
                        world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, l);
                    }
                    else
                    {
                        world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, l - 8);
                    }
                }
                else
                {
                    world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, l);
                }
            }
            else if (bID == mod_FloraSoma.corruptor.blockID && md % 8 == l && random.nextInt(4) == 0)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptBrick.blockID, md % 8);
            }
            else if (bID == Block.stone.blockID)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, 7);
            }
            else if (bID == Block.grass.blockID)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, l % 8 + 8);
            }
            else if (material == Material.ground)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, 3);
            }
            else if (material == Material.sand && bID != Block.slowSand.blockID)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, 6);
            }
            else if (material == Material.water)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, 1);
            }
            else if (bID == Block.wood.blockID)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, 5);
            }
            else if (bID == mod_FloraSoma.redwood.blockID && (md % 2 == 1 || md == 0))
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, 5);
            }
            else if (material == Material.leaves)
            {
                world.setBlockAndMetadataWithNotify(posX, posY, posZ, mod_FloraSoma.corruptor.blockID, 2);
            }
        }
    }

    public boolean isFireSource(World world, int i, int j, int k, int l, int i1)
    {
        return true;
    }

    public int tickRate()
    {
        return 5;
    }

    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (j < 8)
        {
            return blockIndexInTexture + j;
        }
        int k = blockIndexInTexture - 8;
        if (i == 0)
        {
            return k + j;
        }
        if (i == 1)
        {
            return (k - 32) + j;
        }
        else
        {
            return (k - 16) + j;
        }
    }

    protected int damageDropped(int i)
    {
        if (i > 8)
        {
            return i - 8;
        }
        else
        {
            return i;
        }
    }

    public String getTextureFile()
    {
        return "/floratex/stone.png";
    }

    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 0));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 1));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 2));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 3));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 4));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 5));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 6));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 7));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 8));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 9));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 10));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 11));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 12));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 13));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 14));
        arraylist.add(new ItemStack(mod_FloraSoma.corruptor, 1, 15));
    }
}
