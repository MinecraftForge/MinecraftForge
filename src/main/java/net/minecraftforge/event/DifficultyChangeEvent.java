/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import net.minecraft.world.Difficulty;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;

/**
 * DifficultyChangeEvent is fired when difficulty is changing. <br>
 * <br>
 * This event is fired via the {@link singularityEventFactory#onDifficultyChange(Difficulty, Difficulty)}.<br>
 */
public record DifficultyChangeEvent(Difficulty getDifficulty, Difficulty getOldDifficulty) implements RecordEvent {
    public static final EventBus<DifficultyChangeEvent> BUS = EventBus.create(DifficultyChangeEvent.class);
}
