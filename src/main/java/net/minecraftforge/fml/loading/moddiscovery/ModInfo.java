/*
 * Minecraft Forge
 * Copyright (c) 2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;

public class ModInfo {
    private final ModFile owningFile;
    private final String modId;
    private final ArtifactVersion version;
    private final String displayName;
    private final String description;
    private final java.net.URL updateJSONURL;
    private final String modLoader;
    private final java.util.List<net.minecraftforge.fml.loading.moddiscovery.ModInfo.ModVersion> dependencies;

    public ModInfo(final ModFile owningFile, final String modLoader, final String modId, final String displayName, final ArtifactVersion version, final String description, final java.net.URL updateJSONURL, final java.util.List<net.minecraftforge.fml.loading.moddiscovery.ModInfo.ModVersion> dependencies) {
        this.owningFile = owningFile;
        this.modLoader = modLoader;
        this.modId = modId;
        this.displayName = displayName;
        this.version = version;
        this.description = description;
        this.updateJSONURL = updateJSONURL;
        this.dependencies = dependencies;
    }

    public ModFile getOwningFile() {
        return owningFile;
    }

    public String getModLoader() {
        return modLoader;
    }

    public String getModId() {
        return modId;
    }

    public ArtifactVersion getVersion() {
        return version;
    }

    public enum Ordering {
        BEFORE, AFTER, NONE;
    }

    public enum DependencySide {
        CLIENT, SERVER, BOTH;
    }

    public static class ModVersion {
        private final String modId;
        private final ArtifactVersion version;
        private final boolean mandatory;
        private final Ordering ordering;
        private final DependencySide side;

        public ModVersion(final String modId, final ArtifactVersion version, final boolean mandatory, final Ordering ordering, final DependencySide side) {
            this.modId = modId;
            this.version = version;
            this.mandatory = mandatory;
            this.ordering = ordering;
            this.side = side;
        }
    }
}
