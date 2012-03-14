package net.minecraft.src;

import java.io.PrintStream;
import java.util.Random;

public class InfiHybridPowers
{
    private static Random random = new Random();

    public InfiHybridPowers()
    {
    }

    public static void iceAxeSwap(int i, int j, int k, int l, int i1, World world, EntityLiving entityliving)
    {
        if (l == Block.ice.blockID)
        {
            spawnItemBlock(i, j, k, Block.ice.blockID, 0, world);
        }
        else
        {
            Block.blocksList[l].harvestBlock(world, (EntityPlayer)entityliving, i, j, k, i1);
        }
    }

    public static void freezingIceAxe(int i, int j, int k, int l, int i1, World world, EntityLiving entityliving)
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
    }

    public static void freezingHammer(int i, int j, int k, int l, int i1, World world, EntityLiving entityliving)
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
    }

    public static void spawnItemBlock(int i, int j, int k, int l, int i1, World world)
    {
        EntityItem entityitem = new EntityItem(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, new ItemStack(l, 1, i1));
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }

    public static void burningHammer(int i, int j, int k, int l, int i1, World world, EntityLiving entityliving)
    {
        int j1 = burn(l);
        int k1 = burnMd(l);
        if (l == j1)
        {
            Block.blocksList[l].harvestBlock(world, (EntityPlayer)entityliving, i, j, k, i1);
        }
        else
        {
            bash(i, j, k, l, i1, world, entityliving);
        }
    }

    private static int burn(int i)
    {
        int j = i;
        switch (i)
        {
            case 1:
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

    public static void bash(int x, int y, int z, int bID, int md, World world, EntityLiving entityliving)
    {
        int j1 = smash(bID);
        boolean flag = false;
        if (bID == j1)
        {
            Block.blocksList[bID].harvestBlock(world, (EntityPlayer)entityliving, x, y, z, md);
        }
        else
        {
            crushSort(x, y, z, bID, md, world, entityliving);
        }
    }

    private static int smash(int i)
    {
        int j = i;
        switch (i)
        {
            case 1: j = mod_InfiTools.stoneShard.shiftedIndex;           break;
            case 4: j = mod_InfiTools.stoneShard.shiftedIndex;           break;
            case 5: j = mod_InfiTools.woodSplinters.shiftedIndex;        break;
            case 13: j = Block.sand.blockID;							 break;

            case 14:
                j = mod_InfiTools.goldChunks.shiftedIndex;
                break;

            case 15:
                j = mod_InfiTools.ironChunks.shiftedIndex;
                break;

            case 16:
                j = mod_InfiTools.coalBits.shiftedIndex;
                break;

            case 17:
                j = mod_InfiTools.woodSplinters.shiftedIndex;
                break;

            case 20:
                j = mod_InfiTools.glassShard.shiftedIndex;
                break;

            case 24:
                j = mod_InfiTools.sandstoneShard.shiftedIndex;
                break;

            case 29:
                j = Item.stick.shiftedIndex;
                break;

            case 33:
                j = Item.stick.shiftedIndex;
                break;

            case 45:
                j = Item.brick.shiftedIndex;
                break;

            case 48:
                j = mod_InfiTools.mossBall.shiftedIndex;
                break;

            case 49:
                j = mod_InfiTools.obsidianShard.shiftedIndex;
                break;

            case 50:
                j = Item.coal.shiftedIndex;
                break;

            case 53:
                j = Block.planks.blockID;
                break;

            case 56:
                j = mod_InfiTools.diamondShard.shiftedIndex;
                break;

            case 58:
                j = Block.planks.blockID;
                break;

            case 63:
                j = Block.wood.blockID;
                break;

            case 64:
                j = Block.wood.blockID;
                break;

            case 65:
                j = Block.planks.blockID;
                break;

            case 67:
                j = Block.stone.blockID;
                break;

            case 68:
                j = Block.wood.blockID;
                break;

            case 79:
                j = mod_InfiTools.iceShard.shiftedIndex;
                break;

            case 85:
                j = Item.stick.shiftedIndex;
                break;

            case 86:
                j = mod_InfiTools.pumpkinPulp.shiftedIndex;
                break;

            case 87:
                j = mod_InfiTools.netherrackShard.shiftedIndex;
                break;

            case 91:
                j = mod_InfiTools.pumpkinPulp.shiftedIndex;
                break;

            case 96:
                j = Block.planks.blockID;
                break;

            case 98:
                j = mod_InfiTools.stoneRod.shiftedIndex;
                break;

            case 107:
                j = Block.fence.blockID;
                break;

            case 108:
                j = Item.brick.shiftedIndex;
                break;
        }
        return j;
    }

    private static void crushSort(int i, int j, int k, int l, int i1, World world, EntityLiving entityliving)
    {
        if (l == Block.stone.blockID)
        {
            //System.out.println((new StringBuilder()).append("bX: ").append(i).append(" bY: ").append(j).append(" bZ: ").append(k).toString());
            if (random.nextInt(100) + 1 <= 75)
            {
                spawnItem(i, j, k, world, Block.cobblestone.blockID, 1);
            }
            else
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.stoneShard.shiftedIndex, 5, 2);
            }
        }
        else if (l == Block.cobblestone.blockID)
        {
            if (random.nextInt(100) + 1 <= 85)
            {
                spawnItem(i, j, k, world, Block.cobblestone.blockID, 1);
            }
            else
            {
                spawnItem(i, j, k, world, Block.gravel.blockID, 1);
            }
        }
        else if (l == Block.planks.blockID)
        {
            if (random.nextInt(3) <= 1)
            {
                spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 4, 1);
            }
            if (random.nextInt(3) <= 1)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 2, 1);
            }
        }
        else if (l == Block.gravel.blockID)
        {
            if (random.nextInt(100) + 1 <= 30)
            {
                spawnItem(i, j, k, world, Item.flint.shiftedIndex, 1);
            }
            else if (random.nextInt(100) + 1 <= 50)
            {
                spawnItem(i, j, k, world, Block.sand.blockID, 1);
            }
            else
            {
                spawnItem(i, j, k, world, Block.gravel.blockID, 1);
            }
        }
        else if (l == Block.oreCoal.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.coalBits.shiftedIndex, 4, 3);
        }
        else if (l == Block.oreDiamond.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.diamondShard.shiftedIndex, 3, 2);
        }
        else if (l == Block.oreIron.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.ironChunks.shiftedIndex, 3, 2);
        }
        else if (l == Block.oreGold.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.goldChunks.shiftedIndex, 3, 2);
        }
        else if (l == Block.glass.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.glassShard.shiftedIndex, 4, 1);
        }
        else if (l == Block.wood.blockID)
        {
            if (random.nextInt(4) > 0)
            {
                spawnItemWithChance(i, j, k, world, Block.planks.blockID, 4, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 3, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 3, 1);
            }
        }
        else if (l == Block.sandStone.blockID)
        {
            if (random.nextInt(4) > 0)
            {
                spawnItemWithChance(i, j, k, world, Block.sand.blockID, 4, 1);
            }
            if (random.nextInt(3) > 0)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.sandstoneShard.shiftedIndex, 4, 1);
            }
        }
        else if (l == Block.pistonStickyBase.blockID)
        {
            spawnItem(i, j, k, world, Block.cobblestone.blockID, 4);
            spawnItem(i, j, k, world, Block.planks.blockID, 3);
            spawnItem(i, j, k, world, Item.redstone.shiftedIndex, 1);
            spawnItem(i, j, k, world, Item.ingotIron.shiftedIndex, 1);
            spawnItem(i, j, k, world, Item.slimeBall.shiftedIndex, 1);
        }
        else if (l == Block.pistonBase.blockID)
        {
            spawnItem(i, j, k, world, Block.cobblestone.blockID, 4);
            spawnItem(i, j, k, world, Block.planks.blockID, 3);
            spawnItem(i, j, k, world, Item.redstone.shiftedIndex, 1);
            spawnItem(i, j, k, world, Item.ingotIron.shiftedIndex, 1);
        }
        else if (l == Block.brick.blockID)
        {
            spawnItem(i, j, k, world, Item.brick.shiftedIndex, 4);
        }
        else if (l == Block.cobblestoneMossy.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.mossBall.shiftedIndex, 3, 2);
        }
        else if (l == Block.obsidian.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.obsidianShard.shiftedIndex, 3, 2);
        }
        else if (l == Block.torchWood.blockID)
        {
            spawnItem(i, j, k, world, mod_InfiTools.coalBits.shiftedIndex, 1);
        }
        else if (l == Block.torchRedstoneActive.blockID || l == Block.torchRedstoneIdle.blockID)
        {
            spawnItem(i, j, k, world, Item.redstone.shiftedIndex, 1);
        }
        else if (l == Block.stairCompactPlanks.blockID)
        {
            if (random.nextInt(3) <= 1)
            {
                spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 4, 1);
            }
            if (random.nextInt(3) <= 1)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 2, 1);
            }
        }
        else if (l == Block.workbench.blockID)
        {
            if (random.nextInt(4) > 0)
            {
                spawnItemWithChance(i, j, k, world, Block.planks.blockID, 4, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 3, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 3, 1);
            }
        }
        else if (l == Block.signPost.blockID || l == Block.signWall.blockID)
        {
            if (random.nextInt(4) > 0)
            {
                spawnItemWithChance(i, j, k, world, Block.planks.blockID, 4, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 3, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 3, 1);
            }
        }
        else if (l == Block.doorWood.blockID)
        {
            if (random.nextInt(4) > 0)
            {
                spawnItemWithChance(i, j, k, world, Block.planks.blockID, 4, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 3, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 3, 1);
            }
        }
        else if (l == Block.ladder.blockID)
        {
            spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 6, 1);
            spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 3, 1);
        }
        else if (l == Block.fence.blockID)
        {
            spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 5, 1);
            spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 3, 1);
        }
        else if (l == Block.pumpkin.blockID || l == Block.pumpkinLantern.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.pumpkinPulp.shiftedIndex, 2, 1);
            spawnItemWithChance(i, j, k, world, Item.pumpkinSeeds.shiftedIndex, 4, 1);
        }
        else if (l == Block.netherrack.blockID)
        {
            if (random.nextInt(100) + 1 <= 75)
            {
                spawnItem(i, j, k, world, Block.netherrack.blockID, 1);
            }
            else
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.netherrackShard.shiftedIndex, 5, 2);
            }
        }
        else if (l == Block.ice.blockID)
        {
            spawnItemWithChance(i, j, k, world, mod_InfiTools.iceShard.shiftedIndex, 3, 2);
        }
        else if (l == Block.stoneBrick.blockID)
        {
            spawnItemWithMetadata(i, j, k, world, l, 1, 2);
        }
        else if (l == Block.melon.blockID)
        {
            spawnItemWithChance(i, j, k, world, Item.melon.shiftedIndex, 3, 4);
            spawnItemWithChance(i, j, k, world, Item.melonSeeds.shiftedIndex, 4, 1);
        }
        else if (l == Block.fenceGate.blockID)
        {
            if (random.nextInt(4) > 0)
            {
                spawnItemWithChance(i, j, k, world, Block.planks.blockID, 2, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, Item.stick.shiftedIndex, 3, 1);
            }
            if (random.nextInt(2) == 1)
            {
                spawnItemWithChance(i, j, k, world, mod_InfiTools.woodSplinters.shiftedIndex, 3, 1);
            }
        }
        else if (l == Block.stairsBrick.blockID)
        {
            spawnItem(i, j, k, world, Item.brick.shiftedIndex, 4);
        }
    }

    public static void spawnItem(int i, int j, int k, World world, int l, int i1)
    {
        EntityItem entityitem = new EntityItem(world, i, j, k, new ItemStack(l, i1, 0));
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }

    public static void spawnItemWithMetadata(int i, int j, int k, World world, int l, int i1, int j1)
    {
        EntityItem entityitem = new EntityItem(world, i, j, k, new ItemStack(l, i1, j1));
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }

    public static void spawnItemWithChance(int i, int j, int k, World world, int l, int i1, int j1)
    {
        EntityItem entityitem = new EntityItem(world, i, j, k, new ItemStack(l, random.nextInt(i1) + j1, 0));
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }

    public static boolean searchForBlock(World world, int i, int j, int k, int l, int i1)
    {
        for (int j1 = k - j; j1 <= k + j; j1++)
        {
            for (int k1 = l - j; k1 <= l + j; k1++)
            {
                for (int l1 = i1 - j; l1 <= i1 + j; l1++)
                {
                    if (world.getBlockId(j1, k1, l1) == i)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
