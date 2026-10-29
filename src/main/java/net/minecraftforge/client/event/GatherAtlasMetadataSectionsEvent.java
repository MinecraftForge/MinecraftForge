/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import java.util.Set;

/**
 * Fired when an Atlas is being loaded.
 * This allows you to add custom metadata sections that will be loaded from the sprite's .mcmeta file.
 *
 * @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
 *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
 *             We may keep it or add it back in a future version if there is enough demand.
 */
@Deprecated(forRemoval = true, since = "26.4")
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
