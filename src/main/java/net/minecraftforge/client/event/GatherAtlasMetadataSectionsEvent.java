/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import java.util.Set;

/**
 * Fired when an Atlas is being loaded.
 * This allows you to add custom metadata sections that will be loaded from the sprite's .mcmeta file.
 */
public final class GatherAtlasMetadataSectionsEvent extends MutableEvent {
    public static final EventBus<GatherAtlasMetadataSectionsEvent> BUS = EventBus.create(GatherAtlasMetadataSectionsEvent.class);

    private final Set<MetadataSectionType<?>> types;

    public GatherAtlasMetadataSectionsEvent(Set<MetadataSectionType<?>> types) {
        this.types = types;
    }

    public void addType(MetadataSectionType<?> definition) {
        this.types.add(definition);
    }
}
