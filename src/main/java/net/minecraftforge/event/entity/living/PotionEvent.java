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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This Event and its subevents gets fired from  {@link EntityLivingBase} on the  {@link MinecraftForge#EVENT_BUS}.<br>
 */
public class PotionEvent extends LivingEvent
{
    @Nullable
    protected final MobEffectInstance effectInstance;

    public PotionEvent(LivingEntity living, @Nullable MobEffectInstance effectInstance)
    {
        super(living);
        this.effectInstance = effectInstance;
    }

    /**
     * Retuns the PotionEffect.
     */
    @Nullable
    public MobEffectInstance getEffectInstance()
    {
        return effectInstance;
    }

    /**
     * This Event is fired when a Potion is about to get removed from an Entity.
     * This Event is {@link Cancelable}.
     * This Event does not have a result.
     */
    @Cancelable
    public static class PotionRemoveEvent extends PotionEvent
    {
        private final MobEffect effect;

        public PotionRemoveEvent(LivingEntity living, MobEffect effect)
        {
            super(living, living.getEffect(effect));
            this.effect = effect;
        }

        public PotionRemoveEvent(LivingEntity living, MobEffectInstance effectInstance)
        {
            super(living, effectInstance);
            this.effect = effectInstance.getEffect();
        }

        /**
         * @return the Potion which is tried to remove from the Entity.
         */
        public MobEffect getEffect()
        {
            return this.effect;
        }

        /**
         * @return the PotionEffect. In the remove event this can be null if the Entity does not have a {@link Potion} of the right type active.
         */
        @Override
        @Nullable
        public MobEffectInstance getEffectInstance()
        {
            return super.getEffectInstance();
        }
    }

    /**
     * This Event is fired to check if a Potion can get applied to an Entity.
     * This Event is not {@link Cancelable}
     * This Event has a result {@link HasResult}.
     * ALLOW will apply this potion effect.
     * DENY will not apply this potion effect.
     * DEFAULT will run vanilla logic to determine if this potion isApplicable.
     */
    @HasResult
    public static class PotionApplicableEvent extends PotionEvent
    {
        public PotionApplicableEvent(LivingEntity living, @Nonnull MobEffectInstance effectInstance)
        {
            super(living, effectInstance);
        }

        /**
         * @return the PotionEffect.
         */
        @Override
        @Nonnull
        public MobEffectInstance getEffectInstance()
        {
            return super.getEffectInstance();
        }
    }

    /**
     * This Event is fired when a new Potion is added to the Entity. This is also fired if the Entity already has this effect but with different duration/level.
     * This Event is not {@link Cancelable}
     * This Event does not have a Result.
     */
    public static class PotionAddedEvent extends PotionEvent
    {
        private final MobEffectInstance oldEffect;

        public PotionAddedEvent(LivingEntity living, MobEffectInstance oldEffectInstance, @Nonnull MobEffectInstance newEffectInstance)
        {
            super(living, newEffectInstance);
            this.oldEffect = oldEffectInstance;
        }

        /**
         * @return the added PotionEffect. This is the umerged PotionEffect if the old PotionEffect is not null.
         */
        @Override
        @Nonnull
        public MobEffectInstance getEffectInstance()
        {
            return super.getEffectInstance();
        }

        /**
         * @return the old PotionEffect. THis can be null if the entity did not have an effect of this kind before.
         */
        @Nullable
        public MobEffectInstance getOldEffectInstance()
        {
            return oldEffect;
        }
    }

    /**
     * This Event is fired when a Potion effect expires on an Entity.
     * This Event is not {@link Cancelable}
     * This Event does not have a Result.
     */
    public static class PotionExpiryEvent extends PotionEvent
    {
        public PotionExpiryEvent(LivingEntity living, MobEffectInstance effectInstance)
        {
            super(living, effectInstance);
        }
    }
}    
