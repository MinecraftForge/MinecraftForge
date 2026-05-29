/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Called whenever the ID mapping might have changed. If you register for this event, you
 * will be called back whenever the client or server loads an ID set. This includes both
 * when the ID maps are loaded from disk, as well as when the ID maps revert to the initial
 * state.
 * <p>
 * Note: you cannot change the IDs that have been allocated, but you might want to use
 * this event to update caches or other in-mod artifacts that might be impacted by an ID
 * change.
 */
public final class IdMappingEvent extends MutableEvent {
    public static final EventBus<IdMappingEvent> BUS = EventBus.create(IdMappingEvent.class);

    public static final class ModRemapping {
        public final Identifier registry;
        public final Identifier key;
        public final int oldId;
        public final int newId;

        private ModRemapping(Identifier registry, Identifier key, int oldId, int newId) {
            this.registry = registry;
            this.key = key;
            this.oldId = oldId;
            this.newId = newId;
        }
    }

    public record IdRemapping(int currId, int newId) {}

    private final Map<Identifier, ImmutableList<ModRemapping>> remaps;
    private final ImmutableSet<Identifier> keys;

    private final boolean isFrozen;

    public IdMappingEvent(Map<Identifier, Map<Identifier, IdRemapping>> remaps, boolean isFrozen) {
        this.isFrozen = isFrozen;
        this.remaps = Maps.newHashMap();
        remaps.forEach((name, rm) -> {
            List<ModRemapping> tmp = new ArrayList<>();
            rm.forEach((key, value) -> tmp.add(new ModRemapping(name, key, value.currId, value.newId)));
            tmp.sort(Comparator.comparingInt(o -> o.newId));
            this.remaps.put(name, ImmutableList.copyOf(tmp));
        });
        this.keys = ImmutableSet.copyOf(this.remaps.keySet());
    }

    public ImmutableSet<Identifier> getRegistries() {
        return this.keys;
    }

    public ImmutableList<ModRemapping> getRemaps(Identifier registry) {
        return this.remaps.get(registry);
    }

    public boolean isFrozen() {
        return isFrozen;
    }
}
