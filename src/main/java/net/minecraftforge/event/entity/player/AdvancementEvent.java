/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

public class AdvancementEvent extends PlayerEvent
{
    public static enum ProgressType{
        GRANT,REVOKE
    }

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
    public static class AdvancementEarnEvent extends AdvancementEvent {
        public AdvancementEarnEvent(Player player, Advancement earned)
        {
            super(player, earned);
        }
    }

    /**
     * This event is fired when a player progresses on an advancement criterion.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     */
    public static class AdvancementProgressEvent extends AdvancementEvent {
        public final AdvancementProgress advancementProgress;
        public final String criterion;
        public final ProgressType progressType;

        /**
         *
         * @param player The Event's {@link net.minecraft.world.entity.player.Player}
         * @param progressed The {@link Advancement} that was progressed
         * @param advancementProgress The {@link net.minecraft.advancements.AdvancementProgress} of the {@link net.minecraft.advancements.Advancement}
         * @param criterion The {@link String} representing the {@link net.minecraft.advancements.CriterionProgress} that was progressed.
         * @param progressType Defines of what {@link net.minecraftforge.event.entity.player.AdvancementEvent.ProgressType} was the {@link net.minecraft.advancements.CriterionProgress}
         */
        public AdvancementProgressEvent(Player player, Advancement progressed, AdvancementProgress advancementProgress, String criterion, ProgressType progressType)
        {
            super(player, progressed);
            this.advancementProgress = advancementProgress;
            this.criterion = criterion;
            this.progressType = progressType;
        }
    }
}
