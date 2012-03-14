package net.minecraft.src.balkon;

import net.minecraft.src.forge.ITextureProvider;
import java.util.Random;
import net.minecraft.src.*;

public class BalkToolKnife extends InfiWeaponBase
    implements ITextureProvider
{
    private int weaponDamage;
    private static Material materialEffectiveAgainst[];
    private static Random random = new Random();

    public BalkToolKnife(int i, int j, int k, int l, int i1)
    {
        super(i, 2, 3, j, 15F, k, true, materialEffectiveAgainst, l, i1);
        weaponDamage = 3 + k * 2;
    }

    public float getStrVsBlock(ItemStack itemstack, Block block)
    {
        for (int i = 0; i < materialEffectiveAgainst.length; i++)
        {
            if (materialEffectiveAgainst[i] == block.blockMaterial)
            {
                if (type1 == 2 || type1 == 8 || type1 == 10 || type1 == 12 || type1 == 14 || type1 == 15 || type1 == 17)
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

    public boolean hitEntity(ItemStack itemstack, EntityLiving mob, EntityLiving entityliving1)
    {
        World world = entityliving1.worldObj;
        if (type1 == type2)
        {
            attacks(itemstack, world, entityliving1, mob, type1);
        }
        else
        {
            if (random.nextInt(100) + 1 <= 80)
            {
                attacks(itemstack, world, entityliving1, mob, type1);
            }
            if (random.nextInt(100) + 1 <= 20)
            {
                attacks(itemstack, world, entityliving1, mob, type2);
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
                itemstack.damageItem(1, entityliving1);
            }
        }
        else
        {
            itemstack.damageItem(1, entityliving1);
        }
        
      //Freezing Enchant
      	InfiToolPowers.freezingEnchant(itemstack, mob);
        
        return true;
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

    public boolean onBlockDestroyed(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving)
    {
        itemstack.damageItem(2, entityliving);
        return true;
    }

    public int getDamageVsEntity(Entity entity)
    {
        return weaponDamage;
    }

    public boolean isFull3D()
    {
        return true;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        world.playSoundAtEntity(entityplayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote)
        {
            world.spawnEntityInWorld(new BalkKnifeEntity(world, entityplayer, itemstack, weaponDamage));
        }
        itemstack = new ItemStack(this, 0);
        return itemstack;
    }

    public boolean canHarvestBlock(Block block)
    {
        return block.blockID == Block.web.blockID;
    }

    public String getTextureFile()
    {
        return "/infibalkon/balkknives.png";
    }

    public int getItemEnchantability()
    {
        return mod_InfiTools.enchantBase(type1);
    }

    static
    {
        materialEffectiveAgainst = (new Material[]
                {
                    Material.web
                });
    }
}
