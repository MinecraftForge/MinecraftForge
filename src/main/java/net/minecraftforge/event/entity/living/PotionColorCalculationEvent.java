/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.living;

import java.util.Collection;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

/**
 * Fired at server after Potion Color Calculation.
 * 
 * this event is not {@link Cancelable}
 * 
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class PotionColorCalculationEvent extends LivingEvent {
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
