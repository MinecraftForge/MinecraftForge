/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This Event and its subevents gets fired from  {@link EntityLivingBase} on the  {@link MinecraftForge#EVENT_BUS}.<br>
 */
public class PotionEvent extends LivingEvent
{
    @Nullable
    protected final PotionEffect effect;
    
    public PotionEvent(EntityLivingBase living, PotionEffect effect)
    {
        super(living);
        this.effect = effect;
    }
    /**
     * Retuns the PotionEffect.
     */
    @Nullable
    public PotionEffect getPotionEffect()
    {
        return effect;
    }
    
    /**
     * This Event is fired when a Potion is about to get removed from an Entity.
     * This Event is {@link Cancelable}.
     * This Event does not have a result.
     */
    @Cancelable
    public static class PotionRemoveEvent extends PotionEvent
    {
        private final Potion potion;
        
        public PotionRemoveEvent(EntityLivingBase living, Potion potion)
        {
            super(living, living.getActivePotionEffect(potion));
            this.potion = potion;
        }
        
        public PotionRemoveEvent(EntityLivingBase living, PotionEffect effect)
        {
            super(living, effect);
            this.potion = effect.getPotion();            
        }
        
        /**
         * @return the Potion which is tried to remove from the Entity.
         */
        public Potion getPotion()
        {
            return this.potion;
        }
        
        /**
         * @return the PotionEffect. In the remove event this can be null if the Entity does not have a {@link Potion} of the right type active.
         */
        @Override
        @Nullable
        public PotionEffect getPotionEffect()
        {
            return super.getPotionEffect();
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
        public PotionApplicableEvent(EntityLivingBase living, PotionEffect effect)
        {
            super(living, effect);
        }
        
        /**
         * @return the PotionEffect.
         */
        @Override
        @Nonnull
        public PotionEffect getPotionEffect()
        {
            return super.getPotionEffect();
        }
    }
    
    /**
     * This Event is fired when a new Potion is added to the Entity. This is also fired if the Entity already has this effect but with different duration/level.
     * This Event is not {@link Cancelable}
     * This Event does not have a Result.
     */
    public static class PotionAddedEvent extends PotionEvent
    {
        private final PotionEffect oldEffect;
        
        public PotionAddedEvent(EntityLivingBase living, PotionEffect oldEffect, PotionEffect newEffect)
        {
            super(living, newEffect);
            this.oldEffect = oldEffect;
        }
        
        /**
         * @return the added PotionEffect. This is the umerged PotionEffect if the old PotionEffect is not null.
         */
        @Override
        @Nonnull
        public PotionEffect getPotionEffect()
        {
            return super.getPotionEffect();
        }
        
        /**
         * @return the old PotionEffect. THis can be null if the entity did not have an effect of this kind before.
         */
        @Nullable
        public PotionEffect getOldPotionEffect()
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
        public PotionExpiryEvent(EntityLivingBase living, PotionEffect effect)
        {
            super(living, effect);
        }
    }
}    
