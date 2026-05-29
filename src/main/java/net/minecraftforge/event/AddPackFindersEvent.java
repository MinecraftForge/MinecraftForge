/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;

import java.util.function.Consumer;

/**
 * Fired on {@link PackRepository} creation to allow mods to add new pack finders.
 *
 * @param getPackType the {@link PackType} of the pack repository being constructed.
 */
public record AddPackFindersEvent(PackType getPackType, Consumer<RepositorySource> sourceAdder) implements RecordEvent {
    public static final EventBus<AddPackFindersEvent> BUS = EventBus.create(AddPackFindersEvent.class);

    /**
     * Adds a new source to the list of pack finders.
     *
     * @param source the pack finder
     */
    public void addRepositorySource(RepositorySource source) {
        sourceAdder.accept(source);
    }
}
