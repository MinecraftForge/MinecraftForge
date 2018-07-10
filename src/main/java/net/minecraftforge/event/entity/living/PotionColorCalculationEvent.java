/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

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
    private final Collection<PotionEffect> effectList;

    public PotionColorCalculationEvent(EntityLivingBase entity, int color, boolean hideParticle,
            Collection<PotionEffect> effectList)
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
    public Collection<PotionEffect> getEffects()
    {
        return Collections.unmodifiableCollection(effectList);
    }
}