package net.minecraftforge.event.entity.living;

import java.util.Collection;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

/**
 * Fired at server after Potion Color Calculation.
 * 
 * @author hasunwoo
 *
 */
public class PotionColorCalculationEvent extends LivingEvent 
{
	private int color;
	private boolean hideParticle;
	private Collection<PotionEffect> effectList;

	private boolean isDirty = false;

	public PotionColorCalculationEvent(EntityLivingBase entity, int color, boolean hideParticle,
			Collection<PotionEffect> effectList) {
		super(entity);
		this.color = color;
		this.effectList = effectList;
		this.hideParticle = hideParticle;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		isDirty = true;
	}

	public boolean isHideParticle() {
		return hideParticle;
	}

	public void setHideParticle(boolean hideParticle) {
		this.hideParticle = hideParticle;
		isDirty = true;
	}

	public Collection<PotionEffect> getEffectList() {
		return effectList;
	}

	public boolean isDirty() {
		return isDirty;
	}

}
