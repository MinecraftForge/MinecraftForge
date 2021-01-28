/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraftforge.fml.loading.StringSubstitutor;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModInfo implements IModInfo, IConfigurable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DefaultArtifactVersion DEFAULT_VERSION = new DefaultArtifactVersion("1");
    private static final Pattern VALID_LABEL = Pattern.compile("^[a-z][a-z0-9_-]{1,63}$");

    private final ModFileInfo owningFile;
    private final String modId;
    private final String namespace;
    private final ArtifactVersion version;
    private final String displayName;
    private final String description;
    private final Optional<String> logoFile;
    private final boolean logoBlur;
    private final URL updateJSONURL;
    private final List<? extends IModInfo.ModVersion> dependencies;
    private final Map<String,Object> properties;
    private final IConfigurable config;

    public ModInfo(final ModFileInfo owningFile, final IConfigurable config)
    {
        Optional<ModFileInfo> ownFile = Optional.ofNullable(owningFile);
        this.owningFile = owningFile;
        this.config = config;
        this.modId = config.<String>getConfigElement("modId")
                .orElseThrow(() -> new InvalidModFileException("Missing modId", owningFile));
        if (!VALID_LABEL.matcher(this.modId).matches()) {
            LOGGER.fatal("Invalid modId found in file {} - {} does not match the standard: {}", this.owningFile.getFile().getFilePath(), this.modId, VALID_LABEL.pattern());
            throw new InvalidModFileException("Invalid modId found: " + this.modId, owningFile);
        }
        this.namespace = config.<String>getConfigElement("namespace").orElse(this.modId);
        if (!VALID_LABEL.matcher(this.namespace).matches()) {
            LOGGER.fatal("Invalid override namespace found in file {} - {} does not match the standard: {}", this.owningFile.getFile().getFilePath(), this.namespace, VALID_LABEL.pattern());
            throw new InvalidModFileException("Invalid override namespace found: " + this.namespace, owningFile);
        }
        this.version = config.<String>getConfigElement("version")
                .map(s -> StringSubstitutor.replace(s, ownFile.map(ModFileInfo::getFile).orElse(null)))
                .map(DefaultArtifactVersion::new).orElse(DEFAULT_VERSION);
        this.displayName = config.<String>getConfigElement("displayName").orElse(this.modId);
        this.description = config.<String>getConfigElement("description").orElse("MISSING DESCRIPTION");

        this.logoFile = Optional.ofNullable(config.<String>getConfigElement("logoFile")
                .orElseGet(() -> ownFile.flatMap(mf -> mf.<String>getConfigElement("logoFile")).orElse(null)));
        this.logoBlur = config.<Boolean>getConfigElement("logoBlur")
                .orElseGet(() -> ownFile.flatMap(f -> f.<Boolean>getConfigElement("logoBlur"))
                        .orElse(true));

        this.updateJSONURL = config.<String>getConfigElement("updateJSONURL")
                .map(StringUtils::toURL)
                .orElse(null);

        this.dependencies = ownFile.map(mfi -> mfi.getConfigList("dependencies", this.modId)
                .stream()
                .map(dep -> new ModVersion(this, dep))
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        this.properties = ownFile.map(mfi -> mfi.<Map<String, Object>>getConfigElement("modproperties", this.modId)
                .orElse(Collections.emptyMap()))
                .orElse(Collections.emptyMap());
    }

    @Override
    public ModFileInfo getOwningFile() {
        return owningFile;
    }

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public ArtifactVersion getVersion() {
        return version;
    }

    @Override
    public List<? extends IModInfo.ModVersion> getDependencies() {
        return this.dependencies;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public Map<String, Object> getModProperties() {
        return this.properties;
    }

    @Override
    public URL getUpdateURL() {
        return this.updateJSONURL;
    }

    public Optional<String> getLogoFile()
    {
        return this.logoFile;
    }

    public boolean getLogoBlur()
    {
        return this.logoBlur;
    }

    /**
     * This is no longer used. The Mods List GUI currently directly checks whether there is an EntryPoint registered.
     */
    @Deprecated
    public boolean hasConfigUI()
    {
        return false;
    }

    @Override
    public <T> Optional<T> getConfigElement(final String... key) {
        return this.config.getConfigElement(key);
    }

    @Override
    public List<? extends IConfigurable> getConfigList(final String... key) {
        return null;
    }

    class ModVersion implements net.minecraftforge.forgespi.language.IModInfo.ModVersion {
        private IModInfo owner;
        private final String modId;
        private final VersionRange versionRange;
        private final boolean mandatory;
        private final Ordering ordering;
        private final DependencySide side;

        public ModVersion(final IModInfo owner, final IConfigurable config) {
            this.owner = owner;
            this.modId = config.<String>getConfigElement("modId")
                    .orElseThrow(()->new InvalidModFileException("Missing required field modid in dependency", getOwningFile()));
            this.mandatory = config.<Boolean>getConfigElement("mandatory")
                    .orElseThrow(()->new InvalidModFileException("Missing required field mandatory in dependency", getOwningFile()));
            this.versionRange = config.<String>getConfigElement("versionRange")
                    .map(MavenVersionAdapter::createFromVersionSpec)
                    .orElse(UNBOUNDED);
            this.ordering = config.<String>getConfigElement("ordering")
                    .map(Ordering::valueOf)
                    .orElse(Ordering.NONE);
            this.side = config.<String>getConfigElement("side")
                    .map(DependencySide::valueOf)
                    .orElse(DependencySide.BOTH);
        }


        @Override
        public String getModId()
        {
            return modId;
        }

        @Override
        public VersionRange getVersionRange()
        {
            return versionRange;
        }

        @Override
        public boolean isMandatory()
        {
            return mandatory;
        }

        @Override
        public Ordering getOrdering()
        {
            return ordering;
        }

        @Override
        public DependencySide getSide()
        {
            return side;
        }

        @Override
        public void setOwner(final IModInfo owner)
        {
            this.owner = owner;
        }

        @Override
        public IModInfo getOwner()
        {
            return owner;
        }
    }

}
