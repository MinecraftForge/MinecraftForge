/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

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
 * <p>
 * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS forge bus}.
 */
public class IdMappingEvent extends Event
{
    public static class ModRemapping
    {
        public final ResourceLocation registry;
        public final ResourceLocation key;
        public final int oldId;
        public final int newId;

        private ModRemapping(ResourceLocation registry, ResourceLocation key, int oldId, int newId)
        {
            this.registry = registry;
            this.key = key;
            this.oldId = oldId;
            this.newId = newId;
        }
    }

    public record IdRemapping(int currId, int newId) {}

    private final Map<ResourceLocation, ImmutableList<ModRemapping>> remaps;
    private final ImmutableSet<ResourceLocation> keys;

    private final boolean isFrozen;

    public IdMappingEvent(Map<ResourceLocation, Map<ResourceLocation, IdRemapping>> remaps, boolean isFrozen)
    {
        this.isFrozen = isFrozen;
        this.remaps = Maps.newHashMap();
        remaps.forEach((name, rm) ->
        {
            List<ModRemapping> tmp = Lists.newArrayList();
            rm.forEach((key, value) -> tmp.add(new ModRemapping(name, key, value.currId, value.newId)));
            tmp.sort(Comparator.comparingInt(o -> o.newId));
            this.remaps.put(name, ImmutableList.copyOf(tmp));
        });
        this.keys = ImmutableSet.copyOf(this.remaps.keySet());
    }

    public ImmutableSet<ResourceLocation> getRegistries()
    {
        return this.keys;
    }

    public ImmutableList<ModRemapping> getRemaps(ResourceLocation registry)
    {
        return this.remaps.get(registry);
    }

    public boolean isFrozen()
    {
        return isFrozen;
    }
}
