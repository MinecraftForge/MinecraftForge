/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*package net.minecraftforge.event.world;

/**
 * Register all of your custom ModDimensons here, fired during server loading when
 * dimension data is read from the world file.
 *
 * Contains a list of missing entries. Registering an entry with the DimensionManger
 * will remove the matching entry from the missing list.
 * /
public class RegisterDimensionsEvent extends Event
{
    private final Map<ResourceLocation, SavedEntry> missing;
    private final Set<ResourceLocation> keys;

    public RegisterDimensionsEvent(Map<ResourceLocation, SavedEntry> missing)
    {
        this.missing = missing;
        this.keys = Collections.unmodifiableSet(this.missing.keySet());
    }

    public Set<ResourceLocation> getMissingNames()
    {
        return keys;
    }

    @Nullable
    public SavedEntry getEntry(ResourceLocation key)
    {
        return missing.get(key);
    }
}*/
