/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
