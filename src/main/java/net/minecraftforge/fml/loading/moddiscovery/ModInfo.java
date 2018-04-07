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
import net.minecraftforge.fml.loading.IModLanguageProvider;

import java.net.URL;
import java.util.List;

public class ModInfo {
    private final ModFile owningFile;
    private final String modId;
    private final ArtifactVersion version;
    private final String displayName;
    private final String description;
    private final URL updateJSONURL;
    private final String modLoader;
    private final List<ModInfo.ModVersion> dependencies;
    private IModLanguageProvider.IModLanguageLoader loader;

    public ModInfo(final ModFile owningFile, final String modLoader, final String modId, final String displayName, final ArtifactVersion version, final String description, final URL updateJSONURL, final List<ModInfo.ModVersion> dependencies) {
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

    public void setLoader(IModLanguageProvider.IModLanguageLoader loader)
    {
        this.loader = loader;
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
