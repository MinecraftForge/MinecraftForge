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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

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
     * This event is {@link Cancelable}
     * If cancelled, the replacement will not occur
     */
    @Cancelable
    public static class Pre extends LivingConversionEvent
    {
        private final EntityType<? extends LivingEntity> outcome;

        public Pre(LivingEntity entity, EntityType<? extends LivingEntity> outcome)
        {
            super(entity);
            this.outcome = outcome;
        }

        public EntityType<? extends LivingEntity> getOutcome()
        {
            return outcome;
        }
    }

    /**
     * LivingConversionEvent.Post is triggered when an entity is replacing
     * itself with another entity
     */
    public static class Post extends LivingConversionEvent
    {
        private final LivingEntity outcome;

        public Post(LivingEntity entity, LivingEntity outcome)
        {
            super(entity);
            this.outcome = outcome;
        }

        public LivingEntity getOutcome()
        {
            return outcome;
        }
    }
}
