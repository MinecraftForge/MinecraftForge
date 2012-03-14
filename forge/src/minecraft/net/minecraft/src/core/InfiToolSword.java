package net.minecraft.src.core;

import net.minecraft.src.forge.ITextureProvider;
import java.util.Random;
import net.minecraft.src.*;

public class InfiToolSword extends InfiWeaponBase
	implements ITextureProvider
{
	private int weaponDamage;
	private static Material materialEffectiveAgainst[];
	private static Random random = new Random();

	public InfiToolSword(int i, int j, int k, int power1, int power2)
	{
		super(i, 2, 3, j, 15F, k, true, materialEffectiveAgainst, power1, power2);
		weaponDamage = 4 + k * 2;
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
			case 1: InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.woodSplinters, world);	break;
			case 2: InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.stoneShard, world);		break;
			case 7: InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.obsidianShard, world);	break;
			case 8:	InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.sandstoneShard, world);	break;
			case 12:InfiToolPowers.splinterAttack(entityliving, mod_InfiTools.netherrackShard, world);	break;

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
		int i1 = itemstack.getItemDamage();
		if (i1 >= dur)
		{
			itemstack.stackSize = 0;
		}
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

	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		return EnumAction.block;
	}

	public int getMaxItemUseDuration(ItemStack itemstack)
	{
		return 0x11940;
	}

	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	public boolean canHarvestBlock(Block block)
	{
		return block.blockID == Block.web.blockID;
	}

	public String getTextureFile()
	{
		return "/infitools/infiswords.png";
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
