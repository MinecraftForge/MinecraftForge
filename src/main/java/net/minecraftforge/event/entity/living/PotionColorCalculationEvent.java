/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

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
    private final Collection<MobEffectInstance> effectList;

    public PotionColorCalculationEvent(LivingEntity entity, int color, boolean hideParticle,
            Collection<MobEffectInstance> effectList)
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
    public Collection<MobEffectInstance> getEffects()
    {
        return Collections.unmodifiableCollection(effectList);
    }
}
