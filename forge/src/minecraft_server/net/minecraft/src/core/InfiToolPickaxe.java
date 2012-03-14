package net.minecraft.src.core;

import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.ITextureProvider;
import java.util.Random;
import net.minecraft.src.*;

public class InfiToolPickaxe extends InfiToolBase
    implements ITextureProvider
{
    private static Material materialEffectiveAgainst[];
    private static Random random = new Random();

    public InfiToolPickaxe(int i, int j, int k, float f, int l, int i1, int j1)
    {
        super(i, 2, j, k, f, l, true, materialEffectiveAgainst, i1, j1);
    }

    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer entityplayer)
    {
        World world = entityplayer.worldObj;
        if (world.isRemote)
        {
            return false;
        }
        int bID = world.getBlockId(x, y, z);
        int md = world.getBlockMetadata(x, y, z);
        boolean flag = true;
        boolean flag1 = true;
        if (type1 == type2)
        {
            flag = powers(itemstack, bID, x, y, z, world, entityplayer, md, type1);
        }
        else
        {
            if (random.nextInt(100) + 1 <= 80)
            {
                flag = powers(itemstack, bID, x, y, z, world, entityplayer, md, type1);
            }
            if (random.nextInt(100) + 1 <= 20)
            {
                flag1 = powers(itemstack, bID, x, y, z, world, entityplayer, md, type2);
            }
        }
        if (!ForgeHooks.canHarvestBlock(Block.blocksList[bID], entityplayer, md))
        {
            flag = false;
        }
        if (!flag || !flag1)
        {
        	world.playAuxSFX(2001, x, y, z, bID + (md << 12));
            world.setBlockWithNotify(x, y, z, 0);
            onBlockDestroyed(itemstack, bID, x, y, z, entityplayer);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean onBlockDestroyed(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving)
    {
        int i1 = itemstack.getItemDamage();
        if (i1 >= dur)
        {
            itemstack.stackSize = 0;
        }
        if (type1 == 3 || type2 == 3 || type1 == 4 || type2 == 4 || type1 == 8 || type2 == 8)
        {
            if (random.nextInt(100) + 1 <= 90)
            {
                itemstack.damageItem(1, entityliving);
            }
        }
        else
        {
            itemstack.damageItem(1, entityliving);
        }
        return true;
    }

    public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1)
    {
        World world = entityliving1.worldObj;
        if (type1 == type2)
        {
            attacks(itemstack, world, entityliving1, entityliving, type1);
        }
        else
        {
            if (random.nextInt(100) + 1 <= 80)
            {
                attacks(itemstack, world, entityliving1, entityliving, type1);
            }
            if (random.nextInt(100) + 1 <= 20)
            {
                attacks(itemstack, world, entityliving1, entityliving, type2);
            }
        }
        int i = itemstack.getItemDamage();
        if (i >= dur)
        {
            itemstack.stackSize = 0;
        }
        if (type1 == 3 || type2 == 3 || type1 == 4 || type2 == 4 || type1 == 8 || type2 == 8)
        {
            if (random.nextInt(100) + 1 <= 90)
            {
                itemstack.damageItem(2, entityliving1);
            }
        }
        else
        {
            itemstack.damageItem(2, entityliving1);
        }
        return true;
    }

    public boolean powers(ItemStack itemstack, int i, int j, int k, int l, World world, EntityLiving entityliving,
            int i1, int j1)
    {
        switch (j1)
        {
            case 1:
                InfiToolPowers.splintering(j, k, l, mod_InfiTools.woodSplinters, world);
                break;

            case 2:
                InfiToolPowers.splintering(j, k, l, mod_InfiTools.stoneShard, world);
                break;

            case 7:
                InfiToolPowers.splintering(j, k, l, mod_InfiTools.obsidianShard, world);
                break;

            case 8:
                InfiToolPowers.splintering(j, k, l, mod_InfiTools.sandstoneShard, world);
                break;

            case 12:
                InfiToolPowers.splintering(j, k, l, mod_InfiTools.netherrackShard, world);
                break;

            case 13:
                InfiToolPowers.splintering(j, k, l, Item.lightStoneDust, world);
                break;

            case 14:
                InfiToolPowers.freezing(j, k, l, i, i1, world, entityliving);
                break;

            case 15:
                InfiToolPowers.burning(j, k, l, i, i1, world, entityliving);
                break;

            case 16:
                InfiToolPowers.slimePower(j, k, l, world);
                break;
        }
        return j1 != 14 && j1 != 15;
    }

    public void attacks(ItemStack itemstack, World world, EntityLiving entityliving, EntityLiving entityliving1, int i)
    {
        switch (i)
        {
            case 1:
                InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.woodSplinters, world);
                break;

            case 2:
                InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.stoneShard, world);
                break;

            case 7:
                InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.obsidianShard, world);
                break;

            case 8:
                InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.sandstoneShard, world);
                break;

            case 12:
                InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.netherrackShard, world);
                break;

            case 13:
                InfiToolPowers.splinterAttack(entityliving, Item.lightStoneDust, world);
                break;

            case 14:
                entityliving1.freeze();
                break;

            case 15:
                entityliving1.setFire(100);
                break;

            case 20:
                entityliving1.setFire(100);
                break;
        }
    }

    public boolean canHarvestBlock(Block block)
    {
        if (block == Block.obsidian)
        {
            return toolHarvestLevel >= 3;
        }
        if (block == Block.blockDiamond || block == Block.oreDiamond)
        {
            return toolHarvestLevel >= 2;
        }
        if (block == Block.blockGold || block == Block.oreGold)
        {
            return toolHarvestLevel >= 2;
        }
        if (block == Block.blockSteel || block == Block.oreIron)
        {
            return toolHarvestLevel >= 1;
        }
        if (block == Block.blockLapis || block == Block.oreLapis)
        {
            return toolHarvestLevel >= 1;
        }
        if (block == Block.oreRedstone || block == Block.oreRedstoneGlowing)
        {
            return toolHarvestLevel >= 2;
        }
        if (InfiGather.isDeclared(block))
        {
            return InfiGather.getBlockLevel(block) <= toolHarvestLevel;
        }
        if (block.blockMaterial == Material.rock)
        {
            return true;
        }
        else
        {
            return block.blockMaterial == Material.iron;
        }
    }

    public float getStrVsBlock(ItemStack itemstack, Block block, int md)
    {
        for (int i = 0; i < materialEffectiveAgainst.length; i++)
        {
            if (materialEffectiveAgainst[i] == block.blockMaterial)
            {
                if (type1 == 2 || type1 == 8 || type1 == 10 || type1 == 12 || type1 == 14 || type1 == 15 || type1 == 17 || type1 == 20)
                {
                    return efficiencyOnProperMaterial + (float)itemstack.getItemDamage() / 100F;
                }
                else
                {
                    return efficiencyOnProperMaterial;
                }
            }
        }

        return 1.0F;
    }

    public String getTextureFile()
    {
        return "/infitools/infipicks.png";
    }

    public int getItemEnchantability()
    {
        return mod_InfiTools.enchantBase(type1);
    }

    static
    {
        materialEffectiveAgainst = (new Material[]
                {
                    Material.rock, Material.iron, Material.ice, Material.glass, Material.piston
                });
    }
}
