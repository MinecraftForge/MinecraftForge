package net.minecraftforge.event.entity.living.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/** Event for when a potion */
@Cancelable
public class PotionEvent extends LivingEvent {
	protected PotionEffect potionEffect;

	public PotionEvent(EntityLivingBase entity, PotionEffect effect) {
		super(entity);
		this.potionEffect = effect;
	}

	public PotionEffect getEffect() {
		return this.potionEffect;
	}

}
