/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Fires when the mod loader is in the process of loading a world that was last saved
 * with mod versions that differ from the currently-loaded versions. This can be used to
 * enqueue work to run at a later point, such as multi-file migration of data.
 *
 * <p>
 * <b>Note that level and world information has not yet been fully loaded;</b> as such, it is
 * unsafe to access server or level information during handling of this event.
 * </p>
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * on both {@linkplain LogicalSide logical sides}.</p>
 */
public class ModMismatchEvent extends Event implements IModBusEvent
{
    /**
     * The level being loaded. Useful for things like {@link net.minecraft.world.level.storage.DimensionDataStorage}
     * to manage multiple files changing between mod versions.
     */
    private final LevelStorageSource.LevelDirectory levelDirectory;

    /**
     * A set of previously-known versions that have mismatched with the currently loaded versions.
     */
    private final HashMap<String, MismatchedVersionInfo> versionDifferences;

    /**
     * Which mods have specified that they have handled version mismatches.
     */
    private final HashMap<String, ModContainer> resolved;

    @ApiStatus.Internal
    public ModMismatchEvent(LevelStorageSource.LevelDirectory levelDirectory, Map<String, ArtifactVersion> previousVersions, Map<String, ArtifactVersion> missingVersions)
    {
        this.levelDirectory = levelDirectory;
        this.resolved = new HashMap<>(previousVersions.size());
        this.versionDifferences = new HashMap<>();
        previousVersions.forEach((modId, version) -> versionDifferences.put(modId, new MismatchedVersionInfo(version, ModList.get()
                .getModContainerById(modId)
                .map(ModContainer::getModInfo)
                .map(IModInfo::getVersion)
                .orElse(null))));

        missingVersions.forEach((modId, version) -> versionDifferences.put(modId, new MismatchedVersionInfo(version, null)));
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
    @Nullable
    public ArtifactVersion getPreviousVersion(String modId)
    {
        if (this.versionDifferences.containsKey(modId))
            return this.versionDifferences.get(modId).oldVersion();

        return null;
    }

    @Nullable
    public ArtifactVersion getCurrentVersion(String modid)
    {
        if (this.versionDifferences.containsKey(modid))
            return this.versionDifferences.get(modid).newVersion();

        return null;
    }

    /**
     * Marks the mod version mismatch as having been resolved safely by the current mod.
     */
    public void markResolved(String modId)
    {
        final var resolvedBy = ModLoadingContext.get().getActiveContainer();
        resolved.putIfAbsent(modId, resolvedBy);
    }

    /**
     * Fetches the status of a mod mismatch handling state.
     */
    public boolean wasResolved(String modId)
    {
        return this.resolved.containsKey(modId);
    }

    public Optional<MismatchedVersionInfo> getVersionDifference(String modid)
    {
        return Optional.ofNullable(this.versionDifferences.get(modid));
    }

    public Optional<ModContainer> getResolver(String modid)
    {
        return Optional.ofNullable(this.resolved.get(modid));
    }

    public boolean anyUnresolved()
    {
        return resolved.size() < versionDifferences.size();
    }

    public Stream<MismatchResolutionResult> getUnresolved()
    {
        return versionDifferences.keySet().stream()
                .filter(modid -> !resolved.containsKey(modid))
                .map(unresolved -> new MismatchResolutionResult(unresolved, versionDifferences.get(unresolved), null))
                .sorted(Comparator.comparing(MismatchResolutionResult::modid));
    }

    public boolean anyResolved()
    {
        return !resolved.isEmpty();
    }

    public Stream<MismatchResolutionResult> getResolved()
    {
        return resolved.keySet().stream()
                .map(modid -> new MismatchResolutionResult(modid, versionDifferences.get(modid), resolved.get(modid)))
                .sorted(Comparator.comparing(MismatchResolutionResult::modid));
    }

    public record MismatchResolutionResult(String modid, MismatchedVersionInfo versionDifference, @Nullable ModContainer resolver)
    {
        public boolean wasSelfResolved()
        {
            return resolver != null && resolver.getModId().equals(modid);
        }
    }

    public record MismatchedVersionInfo(ArtifactVersion oldVersion, @Nullable ArtifactVersion newVersion)
    {
        public boolean isMissing()
        {
            return newVersion == null;
        }

        public boolean wasUpgrade()
        {
            if (newVersion == null) return false;
            return newVersion.compareTo(oldVersion) > 0;
        }
    }
}