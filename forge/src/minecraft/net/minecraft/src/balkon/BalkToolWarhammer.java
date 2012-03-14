package net.minecraft.src.balkon;

import net.minecraft.src.forge.ITextureProvider;
import java.util.Random;
import net.minecraft.src.*;

public class BalkToolWarhammer extends InfiWeaponBase
    implements ITextureProvider
{
    private static Material materialEffectiveAgainst[];
    private static Random random = new Random();
    private int weaponDamage;
    private static long chargeStartTime;
    private static int chargeDelay;

    public BalkToolWarhammer(int id, int j, int k, float f, int tier, int type1, int type2)
    {
        super(id, 2, j, k, f, tier, true, materialEffectiveAgainst, type1, type2);
        weaponDamage = 4 + tier * 2;
    }

    public boolean onBlockStartBreak(ItemStack itemstack, int i, int j, int k, EntityPlayer entityplayer)
    {
        World world = entityplayer.worldObj;
        if (world.isRemote)
        {
            return false;
        }
        int l = world.getBlockId(i, j, k);
        int i1 = world.getBlockMetadata(i, j, k);
        Boolean boolean1 = Boolean.valueOf(true);
        Boolean boolean2 = Boolean.valueOf(true);
        if (type1 == type2)
        {
            boolean1 = Boolean.valueOf(powers(itemstack, l, i, j, k, world, entityplayer, i1, type1));
        }
        else
        {
            if (random.nextInt(100) + 1 <= 80)
            {
                boolean1 = Boolean.valueOf(powers(itemstack, l, i, j, k, world, entityplayer, i1, type1));
            }
            if (random.nextInt(100) + 1 <= 20)
            {
                boolean2 = Boolean.valueOf(powers(itemstack, l, i, j, k, world, entityplayer, i1, type2));
            }
        }
        if (boolean1.booleanValue() && boolean2.booleanValue())
        {
            InfiHybridPowers.bash(i, j, k, l, i1, world, entityplayer);
        }
        world.playAuxSFX(2001, i, j, k, l + i1 * 256);
        world.setBlockWithNotify(i, j, k, 0);
        return true;
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
                InfiHybridPowers.freezingHammer(j, k, l, i, i1, world, entityliving);
                break;

            case 15:
                InfiHybridPowers.burningHammer(j, k, l, i, i1, world, entityliving);
                break;

            case 16:
                InfiToolPowers.slimePower(j, k, l, world);
                break;
        }
        return j1 != 15;
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

    public int getDamageVsEntity(Entity entity)
    {
        if (entity instanceof EntityZombie)
        {
            return (weaponDamage - 4) * 2 + 4;
        }
        if (entity instanceof EntitySkeleton)
        {
            return weaponDamage * 3;
        }
        else
        {
            return weaponDamage;
        }
    }

    public String getTextureFile()
    {
        return "/infibalkon/balkwarhammers.png";
    }

    public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i)
    {
        int j = getMaxItemUseDuration(itemstack) - i;
        float f = (float)j / 20F;
        f = (f * f + f * 2.0F) / 4F;
        if (f > 1.0F)
        {
            superSmash(itemstack, world, entityplayer);
        }
    }

    protected void superSmash(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if (entityplayer.isInWater())
        {
            return;
        }
        entityplayer.swingItem();
        if (!world.isRemote)
        {
            float f = (float)weaponDamage / 2.0F;
            BalkPhysHelper.createAdvancedExplosion(world, entityplayer, entityplayer.posX, entityplayer.posY - (double)entityplayer.getEyeHeight(), entityplayer.posZ, f, false, false, true, false);
        }
        setChargeDelay(entityplayer, 10000);
        itemstack.damageItem(4, entityplayer);
    }

    public void setChargeDelay(Entity entity, int i)
    {
        chargeStartTime = System.currentTimeMillis();
        chargeDelay = i;
    }

    public boolean isCharged()
    {
        return System.currentTimeMillis() > chargeStartTime + (long)chargeDelay;
    }

    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.bow;
    }

    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 0x11940;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if (isCharged())
        {
            int i = getMaxItemUseDuration(itemstack);
            entityplayer.setItemInUse(itemstack, i);
        }
        return itemstack;
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
