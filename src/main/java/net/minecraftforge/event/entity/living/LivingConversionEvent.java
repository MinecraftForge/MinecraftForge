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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

import java.util.function.Consumer;

public class LivingConversionEvent extends LivingEvent
{
    public LivingConversionEvent(LivingEntity entity)
    {
        super(entity);
    }

    /**
     * LivingConversionEvent.Pre is triggered when an entity is trying
     * to replace itself with another entity
     *
     * This event may trigger every tick even if it was cancelled last tick
     * for entities like Zombies and Hoglins. To prevent it, the conversion
     * timer needs to be changed or reset
     *
     * This event is {@link Cancelable}
     * If cancelled, the replacement will not occur
     */
    @Cancelable
    public static class Pre extends LivingConversionEvent
    {
        private final EntityType<? extends LivingEntity> outcome;
        private final Consumer<Integer> timer;

        public Pre(LivingEntity entity, EntityType<? extends LivingEntity> outcome, Consumer<Integer> timer)
        {
            super(entity);
            this.outcome = outcome;
            this.timer = timer;
        }

        /**
         * Gets the entity type of the new entity this living entity is
         * converting to
         * @return the entity type of the new entity
         */
        public EntityType<? extends LivingEntity> getOutcome()
        {
            return outcome;
        }

        /**
         * Sets the conversion timer, by changing this it prevents the
         * event being triggered every tick
         * Do note the timer of some of the entities are increments, but
         * some of them are decrements
         * Not every conversion is applicable for this
         * @param ticks timer ticks
         */
        public void setConversionTimer(int ticks)
        {
            timer.accept(ticks);
        }
    }

    /**
     * LivingConversionEvent.Post is triggered when an entity is replacing
     * itself with another entity.
     * The old living entity is likely to be removed right after this event.
     */
    public static class Post extends LivingConversionEvent
    {
        private final LivingEntity outcome;

        public Post(LivingEntity entity, LivingEntity outcome)
        {
            super(entity);
            this.outcome = outcome;
        }

        /**
         * Gets the finalized new entity (with all data like potion
         * effect and equipments set)
         * @return the finalized new entity
         */
        public LivingEntity getOutcome()
        {
            return outcome;
        }
    }
}
