/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MarkerEvent;

/**
 * Base class used for advancement-related events. Should not be used directly.
 * @see AdvancementEarnEvent
 * @see AdvancementProgressEvent
 */
@MarkerEvent
public sealed interface AdvancementEvent extends PlayerEvent {
    AdvancementHolder advancement();

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
    record AdvancementEarnEvent(Player entity, AdvancementHolder advancement) implements AdvancementEvent {
        public static final EventBus<AdvancementEarnEvent> BUS = EventBus.create(AdvancementEarnEvent.class);
    }

    /**
     * Fired when the player's progress on an advancement criterion is granted or revoked.
     *
     * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}.</p>
     *
     * @param advancementProgress the progress of the advancement
     * @param criterionName name of the criterion that was progressed
     * @param progressType The type of progress for the criterion in this event
     *
     * @see AdvancementEarnEvent
     * @see net.minecraft.server.PlayerAdvancements#award(Advancement, String)
     * @see net.minecraft.server.PlayerAdvancements#revoke(Advancement, String)
     */
    record AdvancementProgressEvent(
            Player entity,
            AdvancementHolder advancement,
            AdvancementProgress advancementProgress,
            String criterionName,
            AdvancementEvent.AdvancementProgressEvent.ProgressType progressType
    ) implements AdvancementEvent {
        public static final EventBus<AdvancementProgressEvent> BUS = EventBus.create(AdvancementProgressEvent.class);

        public enum ProgressType {
            GRANT, REVOKE
        }
    }
}
