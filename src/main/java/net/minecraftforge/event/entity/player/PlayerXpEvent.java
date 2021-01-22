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

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * PlayerXpEvent is fired whenever an event involving player experience occurs. <br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class PlayerXpEvent extends PlayerEvent
{

    public PlayerXpEvent(PlayerEntity player)
    {
        super(player);
    }

    /**
     * This event is fired after the player collides with an experience orb, but before the player has been given the experience.
     * It can be cancelled, and no further processing will be done.
     */
    @Cancelable
    public static class PickupXp extends PlayerXpEvent
    {

        private final ExperienceOrbEntity orb;

        public PickupXp(PlayerEntity player, ExperienceOrbEntity orb)
        {
            super(player);
            this.orb = orb;
        }

        public ExperienceOrbEntity getOrb()
        {
            return orb;
        }

    }

    /**
     * This event is fired when the player's experience changes through the {@link PlayerEntity#giveExperiencePoints} method.
     * It can be cancelled, and no further processing will be done.
     */
    @Cancelable
    public static class XpChange extends PlayerXpEvent
    {

        private int amount;

        public XpChange(PlayerEntity player, int amount)
        {
            super(player);
            this.amount = amount;
        }

        public int getAmount()
        {
            return this.amount;
        }

        public void setAmount(int amount)
        {
            this.amount = amount;
        }

    }

    /**
     * This event is fired when the player's experience level changes through the {@link PlayerEntity#addExperienceLevel} method.
     * It can be cancelled, and no further processing will be done.
     */
    @Cancelable
    public static class LevelChange extends PlayerXpEvent
    {

        private int levels;

        public LevelChange(PlayerEntity player, int levels)
        {
            super(player);
            this.levels = levels;
        }

        public int getLevels()
        {
            return this.levels;
        }

        public void setLevels(int levels)
        {
            this.levels = levels;
        }

    }

}
