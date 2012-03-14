package net.minecraft.src.infiflora;

import net.minecraft.src.forge.ITextureProvider;
import java.util.Random;
import net.minecraft.src.*;

public class InfiToolFryingPan extends InfiWeaponBase
    implements ITextureProvider
{
    private int weaponDamage;
    private static Material materialEffectiveAgainst[];
    private static Random random = new Random();

    public InfiToolFryingPan(int i, int j, int k, int l, int i1)
    {
        super(i, 2, 3, j, 15F, k, true, materialEffectiveAgainst, l, i1);
        weaponDamage = 4 + k * 2;
    }

    public boolean hitEntity(ItemStack itemstack, EntityLiving mob, EntityLiving entityliving1)
    {
        World world = entityliving1.worldObj;
        mob.motionY *= 1.2D;
        mob.motionX *= 1.6D;
        mob.motionZ *= 1.6D;
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

    public String getTextureFile()
    {
        return "/infitools/infifryingpans.png";
    }

    public int getItemEnchantability()
    {
        return mod_InfiTools.enchantBase(type1);
    }

    static
    {
        materialEffectiveAgainst = (new Material[]
                {
                    Material.glass
                });
    }
}
