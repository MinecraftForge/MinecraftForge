/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

/**
 * This event is fired when a player gets an advancement.
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class AdvancementEvent extends PlayerEvent
{
    private final Advancement advancement;

    public AdvancementEvent(Player player, Advancement advancement)
    {
        super(player);
        this.advancement = advancement;
    }

    public Advancement getAdvancement()
    {
        return advancement;
    }

    /**
     * This event is fired when a player earns an advancement/recipe.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     */
    public static class AdvancementEarnEvent extends AdvancementEvent
    {
        public AdvancementEarnEvent(Player player, Advancement earned)
        {
            super(player, earned);
        }
    }

    /**
     * This event is fired when a player progresses on an advancement criterionName.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     */
    public static class AdvancementProgressEvent extends AdvancementEvent
    {
        public enum ProgressType
        {
            GRANT, REVOKE
        }

        private final AdvancementProgress advancementProgress;
        private final String criterionName;
        private final AdvancementEvent.AdvancementProgressEvent.ProgressType progressType;

        public AdvancementProgressEvent(Player player, Advancement progressed, AdvancementProgress advancementProgress, String criterionName, AdvancementEvent.AdvancementProgressEvent.ProgressType progressType)
        {
            super(player, progressed);
            this.advancementProgress = advancementProgress;
            this.criterionName = criterionName;
            this.progressType = progressType;
        }

        /**
         * {@return The AdvancementProgress of the Advancement}
         */
        public AdvancementProgress getAdvancementProgress()
        {
            return advancementProgress;
        }

        /**
         * {@return The Criterion's name that was progressed}
         */
        public String getCriterionName()
        {
            return criterionName;
        }

        /**
         * {@return The ProgressType of the progressed Advancement}
         */
        public ProgressType getProgressType()
        {
            return progressType;
        }
    }
}
