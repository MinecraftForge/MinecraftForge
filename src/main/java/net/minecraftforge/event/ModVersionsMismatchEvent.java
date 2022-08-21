/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;


import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * Fires when the mod loader is in the process of loading a world that was last saved
 * with mod versions that differ from the currently-loaded versions. This can be used to
 * enqueue work to run at a later point, such as multi-file migration of data.
 * </p>
 *
 * <p>
 * <b>Note that level and world information has not yet been fully loaded;</b> as such, it is
 * unsafe to access server or level information during handling of this event.
 * </p>
 */
public class ModVersionsMismatchEvent extends Event implements IModBusEvent
{
    /**
     * The level being loaded. Useful for things like {@link net.minecraft.world.level.storage.DimensionDataStorage}
     * to manage multiple files changing between mod versions.
     */
    private final LevelStorageSource.LevelDirectory levelDirectory;

    /**
     * A set of previously-known versions that have mismatched with the currently loaded versions.
     */
    private final Map<String, ArtifactVersion> previousVersions;

    /**
     * State values of which mods have specified that they have handled version mismatches.
     */
    private final Map<String, MismatchHandlingState> states;

    /**
     * Create an event instance to handle mod version mismatches.
     *
     * @param levelDirectory The directory of the level being loaded.
     * @param previousVersions Map of mismatched mod ids and their previously-loaded version.
     */
    public ModVersionsMismatchEvent(LevelStorageSource.LevelDirectory levelDirectory, Map<String, ArtifactVersion> previousVersions)
    {
        this.levelDirectory = levelDirectory;
        this.previousVersions = previousVersions;
        this.states = new HashMap<>(previousVersions.size());
    }

    /**
     * Gets the current level directory for the world being loaded.
     * Can be used for file operations and manual modification of mod files before world load.
     */
    public LevelStorageSource.LevelDirectory getLevelDirectory()
    {
        return this.levelDirectory;
    }

    /**
     * Fetch a previous version of a given mod, if it has been mismatched.
     * @param modId The mod to fetch previous version for.
     * @return The previously known mod version, or {@link Optional#empty()} if unknown/not found.
     */
    public Optional<ArtifactVersion> getPreviousVersion(String modId)
    {
        return Optional.ofNullable(this.previousVersions.get(modId));
    }

    /**
     * Utility method to fetch current mod version information from the mod list.
     * @param modId The mod to query.
     * @return Current mod version information.
     */
    public ArtifactVersion getCurrentVersion(String modId)
    {
        return ModList.get().getModContainerById(modId)
                .map(ModContainer::getModInfo)
                .map(IModInfo::getVersion)
                .orElseThrow();
    }

    /**
     * Marks the mod version mismatch as having been handled safely by a mod.
     */
    public void setModState(String modId, MismatchHandlingState handled)
    {
        this.states.put(modId, handled);
    }

    /**
     * Fetches the status of a mod mismatch handling state.
     */
    public MismatchHandlingState getModState(String modId)
    {
        return this.states.getOrDefault(modId, MismatchHandlingState.UNHANDLED);
    }

    /**
     * State values for mod version mismatches. Can be queried by any mod listening to the event.
     */
    public enum MismatchHandlingState
    {
        UNHANDLED,
        NEEDS_FURTHER_WORK,
        FULLY_HANDLED
    }
}
