/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import java.io.File;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;

/**
 * The container that wraps around mods in the system.
 * <p>
 * The philosophy is that individual mod implementation technologies should not
 * impact the actual loading and management of mod code. This interface provides
 * a mechanism by which we can wrap actual mod code so that the loader and other
 * facilities can treat mods at arms length.
 * </p>
 *
 * @author cpw
 *
 */

public interface ModContainer
{
    public static enum Disableable {
        YES, RESTART, NEVER, DEPENDENCIES;
    }
    /**
     * The globally unique modid for this mod
     */
    String getModId();

    /**
     * A human readable name
     */

    String getName();

    /**
     * A human readable version identifier
     */
    String getVersion();

    /**
     * The location on the file system which this mod came from
     */
    File getSource();

    /**
     * The metadata for this mod
     */
    ModMetadata getMetadata();

    /**
     * Attach this mod to it's metadata from the supplied metadata collection
     */
    void bindMetadata(MetadataCollection mc);

    /**
     * Set the enabled/disabled state of this mod
     */
    void setEnabledState(boolean enabled);

    /**
     * A list of the modids that this mod requires loaded prior to loading
     */
    Set<ArtifactVersion> getRequirements();

    /**
     * A list of modids that should be loaded prior to this one. The special
     * value <strong>*</strong> indicates to load <em>after</em> any other mod.
     */
    List<ArtifactVersion> getDependencies();

    /**
     * A list of modids that should be loaded <em>after</em> this one. The
     * special value <strong>*</strong> indicates to load <em>before</em> any
     * other mod.
     */
    List<ArtifactVersion> getDependants();

    /**
     * A representative string encapsulating the sorting preferences for this
     * mod
     */
    String getSortingRules();

    /**
     * Register the event bus for the mod and the controller for error handling
     * Returns if this bus was successfully registered - disabled mods and other
     * mods that don't need real events should return false and avoid further
     * processing
     *
     * @param bus
     * @param controller
     */
    boolean registerBus(EventBus bus, LoadController controller);

    /**
     * Does this mod match the supplied mod
     *
     * @param mod
     */
    boolean matches(Object mod);

    /**
     * Get the actual mod object
     */
    Object getMod();

    ArtifactVersion getProcessedVersion();

    boolean isImmutable();

    String getDisplayVersion();

    VersionRange acceptableMinecraftVersionRange();

    Certificate getSigningCertificate();

    public static final Map<String,String> EMPTY_PROPERTIES = ImmutableMap.of();
    Map<String,String> getCustomModProperties();

    public Class<?> getCustomResourcePackClass();

    Map<String, String> getSharedModDescriptor();

    Disableable canBeDisabled();

    String getGuiClassName();

    List<String> getOwnedPackages();

    boolean shouldLoadInEnvironment();

    URL getUpdateUrl();
}
