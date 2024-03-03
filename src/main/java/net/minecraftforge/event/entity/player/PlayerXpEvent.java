/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
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

    public PlayerXpEvent(Player player)
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

        private final ExperienceOrb orb;

        public PickupXp(Player player, ExperienceOrb orb)
        {
            super(player);
            this.orb = orb;
        }

        public ExperienceOrb getOrb()
        {
            return orb;
        }

    }

    /**
     * This event is fired when the player's experience changes through the {@link Player#giveExperiencePoints(int)} method.
     * It can be cancelled, and no further processing will be done.
     */
    @Cancelable
    public static class XpChange extends PlayerXpEvent
    {

        private int amount;

        public XpChange(Player player, int amount)
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
     * This event is fired when the player's experience level changes through the {@link Player#giveExperienceLevels(int)} method.
     * It can be cancelled, and no further processing will be done.
     */
    @Cancelable
    public static class LevelChange extends PlayerXpEvent
    {

        private int levels;

        public LevelChange(Player player, int levels)
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
