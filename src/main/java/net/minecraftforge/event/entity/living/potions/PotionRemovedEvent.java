package net.minecraftforge.event.entity.living.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public class PotionRemovedEvent extends PotionEvent {
	public final boolean wasCured;

	public PotionRemovedEvent(EntityLivingBase entity, PotionEffect effect, boolean cured) {
		super(entity, effect);
		this.wasCured = cured;
	}
}