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
 * @deprecated Use {@link net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent} and {@link net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent} instead
 */
@Deprecated(since = "1.19.2")
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
     * Fired when the player earns an advancement. An advancement is earned once its requirements are complete.
     *
     * <p>Note that advancements may be hidden from the player or used in background mechanics, such as recipe
     * advancements for unlocking recipes in the recipe book.</p>
     *
     * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}.</p>
     *
     * @see AdvancementProgress#isDone()
     */
    //todo: should extend AdvancementEvent in 1.20
    public static class AdvancementEarnEvent extends PlayerEvent
    {
        private final Advancement advancement;

        public AdvancementEarnEvent(Player player, Advancement earned)
        {
            super(player);
            this.advancement = earned;
        }

        /**
         *
         * {@return the advancement that was earned}
         */
        public Advancement getAdvancement()
        {
            return advancement;
        }
    }

    /**
     * Fired when the player's progress on an advancement criterion is granted or revoked.
     *
     * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}.</p>
     *
     * @see AdvancementEarnEvent
     * @see net.minecraft.server.PlayerAdvancements#award(Advancement, String)
     * @see net.minecraft.server.PlayerAdvancements#revoke(Advancement, String)
     */
    //todo: should extend AdvancementEvent in 1.20
    public static class AdvancementProgressEvent extends PlayerEvent
    {
        private final Advancement advancement;
        private final AdvancementProgress advancementProgress;
        private final String criterionName;
        private final AdvancementEvent.AdvancementProgressEvent.ProgressType progressType;

        public AdvancementProgressEvent(Player player, Advancement progressed, AdvancementProgress advancementProgress, String criterionName, AdvancementEvent.AdvancementProgressEvent.ProgressType progressType)
        {
            super(player);
            this.advancement = progressed;
            this.advancementProgress = advancementProgress;
            this.criterionName = criterionName;
            this.progressType = progressType;
        }

        /**
         *
         * {@return The advancement that was progressed}
         */
        public Advancement getAdvancement()
        {
            return advancement;
        }

        /**
         * {@return the progress of the advancement}
         */
        public AdvancementProgress getAdvancementProgress()
        {
            return advancementProgress;
        }

        /**
         * {@return name of the criterion that was progressed}
         */
        public String getCriterionName()
        {
            return criterionName;
        }

        /**
         * {@return The type of progress for the criterion in this event}
         */
        public ProgressType getProgressType()
        {
            return progressType;
        }

        public enum ProgressType
        {
            GRANT, REVOKE
        }
    }
}
