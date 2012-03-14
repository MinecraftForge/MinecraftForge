package net.minecraft.src;

import java.util.Random;

public class InfiToolPowers extends mod_InfiTools
{
    private static Random random = new Random();

    public InfiToolPowers()
    {
    }

    public static void splintering(int i, int j, int k, Item item, World world)
    {
        if (random.nextInt(100) + 1 <= 5)
        {
            EntityItem entityitem = new EntityItem(world, i, j, k, new ItemStack(item.shiftedIndex, 1, 0));
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    public static void splinterAttack(EntityLiving entityliving, Item item, World world)
    {
        if (random.nextInt(100) + 1 <= 2)
        {
            EntityItem entityitem = new EntityItem(world, entityliving.posX, entityliving.posY - 1.0D, entityliving.posZ, new ItemStack(item.shiftedIndex, 1, 0));
            world.spawnEntityInWorld(entityitem);
        }
    }

    public static void freezing(int i, int j, int k, int l, int i1, World world, EntityLiving entityliving)
    {
        for (int j1 = i - 1; j1 <= i + 1; j1++)
        {
            for (int k1 = j - 1; k1 <= j + 1; k1++)
            {
                for (int l1 = k - 1; l1 <= k + 1; l1++)
                {
                    if (world.getBlockId(j1, k1, l1) == Block.waterStill.blockID || world.getBlockId(j1, k1, l1) == Block.waterMoving.blockID)
                    {
                        world.setBlockWithNotify(j1, k1, l1, Block.ice.blockID);
                    }
                }
            }
        }

        if (l != Block.ice.blockID)
        {
            Block.blocksList[l].harvestBlock(world, (EntityPlayer)entityliving, i, j, k, i1);
        }
    }
    
    public static void freezingEnchant(ItemStack itemstack, EntityLiving mob)
	{
		int freezeCheck = 0;
		NBTTagList nbttaglist = itemstack.getEnchantmentTagList();
        if(nbttaglist == null)
        {
            return;
        }
        for(int j = 0; j < nbttaglist.tagCount(); j++)
        {
            short word0 = ((NBTTagCompound)nbttaglist.tagAt(j)).getShort("id");
            short word1 = ((NBTTagCompound)nbttaglist.tagAt(j)).getShort("lvl");
            if(word0 == 42)
            {
                freezeCheck = word1;
            }
        }
		if(freezeCheck > 0)
        {
            mob.freeze(freezeCheck * 60);
			//mob.freeze();
        }
	}

    public static void burning(int i, int j, int k, int l, int i1, World world, EntityLiving entityliving)
    {
        int j1 = burn(l);
        int k1 = burnMd(l);
        if (l == j1)
        {
            Block.blocksList[l].harvestBlock(world, (EntityPlayer)entityliving, i, j, k, i1);
        }
        else if (j1 == Item.brick.shiftedIndex)
        {
            spawnItemBlock(i, j, k, j1, k1, world);
            spawnItemBlock(i, j, k, j1, k1, world);
            spawnItemBlock(i, j, k, j1, k1, world);
            spawnItemBlock(i, j, k, j1, k1, world);
        }
        else
        {
            if (j1 == mod_InfiTools.stoneShard.shiftedIndex)
            {
                j1 = Block.stone.blockID;
            }
            spawnItemBlock(i, j, k, j1, k1, world);
        }
    }

    private static int burn(int i)
    {
        int j = i;
        switch (i)
        {
            case 1:
                j = mod_InfiTools.stoneShard.shiftedIndex;
                break;

            case 4:
                j = Block.stone.blockID;
                break;

            case 12:
                j = Block.glass.blockID;
                break;

            case 14:
                j = Item.ingotGold.shiftedIndex;
                break;

            case 15:
                j = Item.ingotIron.shiftedIndex;
                break;

            case 17:
                j = Item.coal.shiftedIndex;
                break;

            case 82:
                j = Item.brick.shiftedIndex;
                break;
        }
        return j;
    }

    private static int burnMd(int i)
    {
        int j = i;
        switch (i)
        {
            case 17:
                j = 1;
                break;

            default:
                j = 0;
                break;
        }
        return j;
    }

    public static void slimePower(int i, int j, int k, World world)
    {
        if (random.nextInt(100) + 1 <= 12)
        {
            int l = 0;
            switch (random.nextInt(5) + 1)
            {
                case 1:
                    l = Item.slimeBall.shiftedIndex;
                    break;

                case 2:
                    l = Item.slimeBall.shiftedIndex;
                    break;

                case 3:
                    l = Block.plantYellow.blockID;
                    break;

                case 4:
                    l = Block.plantRed.blockID;
                    break;

                case 5:
                    l = Item.seeds.shiftedIndex;
                    break;
            }
            spawnItemBlock(i, j, k, l, 0, world);
        }
        if (random.nextInt(100) + 1 <= 5)
        {
            EntitySlime entityslime = new EntitySlime(world);
            entityslime.setSlimeSize(1);
            entityslime.setPosition((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
            world.spawnEntityInWorld(entityslime);
        }
    }

    public static void spawnItemBlock(int i, int j, int k, int l, int i1, World world)
    {
        EntityItem entityitem = new EntityItem(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, new ItemStack(l, 1, i1));
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }
}
