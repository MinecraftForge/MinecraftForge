package net.minecraft.src.core;

import net.minecraft.src.DamageSource;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.forge.IStrikeEntityHandler;

public class FreezeHandler
	implements IStrikeEntityHandler 
{

	@Override
	public ItemStack strikeEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {
		//entity.attackEntityFrom(DamageSource.onFire, 100);
		if (entity instanceof EntityLiving) {
			((EntityLiving) entity).freeze(500);
		}
		return stack;
	}
}

