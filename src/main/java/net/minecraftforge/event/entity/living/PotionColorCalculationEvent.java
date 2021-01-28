/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import java.util.Collections;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;

/**
 * Fires after Potion Color Calculation.
 *
 * this event is not {@link Cancelable}
 *
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class PotionColorCalculationEvent extends LivingEvent
{
    private int color;
    private boolean hideParticle;
    private final Collection<EffectInstance> effectList;

    public PotionColorCalculationEvent(LivingEntity entity, int color, boolean hideParticle,
            Collection<EffectInstance> effectList)
    {
        super(entity);
        this.color = color;
        this.effectList = effectList;
        this.hideParticle = hideParticle;
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public boolean areParticlesHidden()
    {
        return hideParticle;
    }

    public void shouldHideParticles(boolean hideParticle)
    {
        this.hideParticle = hideParticle;
    }

    /**
     * Note that returned list is unmodifiable.
     *
     * @return effects
     */
    public Collection<EffectInstance> getEffects()
    {
        return Collections.unmodifiableCollection(effectList);
    }
}
