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

package net.minecraftforge.event.hunger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Base class for all StarvationEvent events.
 * 
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class StarvationEvent extends Event
{
    private final PlayerEntity player;

    public StarvationEvent(PlayerEntity player)
    {
        this.player = player;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * Fired each FoodStats update to determine whether or not starvation is allowed for the {@link #player}.
     * 
     * This event is not {@link Cancelable}.
     * 
     * This event uses the {@link Result}. {@link HasResult}
     * {@link Result#DEFAULT} will use the vanilla conditionals.
     * {@link Result#ALLOW} will allow starvation without condition.
     * {@link Result#DENY} will deny starvation without condition.
     */
    @HasResult
    public static class AllowStarvation extends StarvationEvent
    {
        public AllowStarvation(PlayerEntity player)
        {
            super(player);
        }
    }

    /**
     * Fired every time the starve tick period is retrieved to allow control over its value.
     * 
     * {@link #starveTickPeriod} contains the number of ticks between starvation damage being done.
     * 
     * This event is not {@link Cancelable}.
     * 
     * This event does not have a {@link Result}. {@link HasResult}
     */
    public static class GetStarveTickPeriod extends StarvationEvent
    {
        private int starveTickPeriod;

        public GetStarveTickPeriod(PlayerEntity player)
        {
            super(player);
            this.starveTickPeriod = 80;
        }

        public int getStarveTickPeriod()
        {
            return starveTickPeriod;
        }

        public void setStarveTickPeriod(int starveTickPeriod)
        {
            this.starveTickPeriod = starveTickPeriod;
        }
    }

    /**
     * Fired once the time since last starvation damage reaches starveTickPeriod (see {@link GetStarveTickPeriod}),
     * in order to control how much starvation damage to do.
     * 
     * {@link #starveDamage} contains the amount of damage to deal from starvation.
     * 
     * This event is {@link Cancelable}.
     * If this event is canceled, it will skip dealing starvation damage.
     * 
     * This event does not have a {@link Result}. {@link HasResult}
     */
    @Cancelable
    public static class Starve extends StarvationEvent
    {
        private float starveDamage;

        public Starve(PlayerEntity player)
        {
            super(player);

            Difficulty difficulty = player.level.getDifficulty();
            boolean shouldDoDamage = player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL;
            this.starveDamage = shouldDoDamage ? 1.0F : 0.0F;
        }

        public float getStarveDamage()
        {
            return starveDamage;
        }

        public void setStarveDamage(float starveDamage)
        {
            this.starveDamage = starveDamage;
        }
    }
}
