package net.minecraftforge.event.entity.living.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public class PotionAppliedEvent extends PotionEvent {

	public PotionAppliedEvent(EntityLivingBase entity, PotionEffect effect) {
		super(entity, effect);
	}
}