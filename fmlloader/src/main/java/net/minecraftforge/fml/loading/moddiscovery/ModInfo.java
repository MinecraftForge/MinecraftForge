/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.StringSubstitutor;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import net.minecraftforge.forgespi.locating.ForgeFeature;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModInfo implements IModInfo, IConfigurable
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DefaultArtifactVersion DEFAULT_VERSION = new DefaultArtifactVersion("1");
    private static final Pattern VALID_MODID = Pattern.compile("^[a-z][a-z0-9_]{1,63}$");
    private static final Pattern VALID_NAMESPACE = Pattern.compile("^[a-z][a-z0-9_.-]{1,63}$");
    private static final Pattern VALID_VERSION = Pattern.compile("^\\d+.*");

    private final ModFileInfo owningFile;
    private final String modId;
    private final String namespace;
    private final ArtifactVersion version;
    private final String displayName;
    private final String description;
    private final Optional<String> logoFile;
    private final boolean logoBlur;
    private final Optional<URL> updateJSONURL;
    private final List<? extends IModInfo.ModVersion> dependencies;

    private final List<ForgeFeature.Bound> features;
    private final Map<String,Object> properties;
    private final IConfigurable config;
    private final Optional<URL> modUrl;

    public ModInfo(final ModFileInfo owningFile, final IConfigurable config)
    {
        Optional<ModFileInfo> ownFile = Optional.ofNullable(owningFile);
        this.owningFile = owningFile;
        this.config = config;
        this.modId = config.<String>getConfigElement("modId")
                .orElseThrow(() -> new InvalidModFileException("Missing modId", owningFile));
        if (!VALID_MODID.matcher(this.modId).matches()) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Invalid modId found in file {} - {} does not match the standard: {}", this.owningFile.getFile().getFilePath(), this.modId, VALID_MODID.pattern());
            throw new InvalidModFileException("Invalid modId found : " + this.modId, owningFile);
        }
        this.namespace = config.<String>getConfigElement("namespace").orElse(this.modId);
        if (!VALID_NAMESPACE.matcher(this.namespace).matches()) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Invalid override namespace found in file {} - {} does not match the standard: {}", this.owningFile.getFile().getFilePath(), this.namespace, VALID_NAMESPACE.pattern());
            throw new InvalidModFileException("Invalid override namespace found : " + this.namespace, owningFile);
        }
        this.version = config.<String>getConfigElement("version")
                .map(s -> StringSubstitutor.replace(s, ownFile.map(ModFileInfo::getFile).orElse(null)))
                .map(DefaultArtifactVersion::new).orElse(DEFAULT_VERSION);
        this.displayName = config.<String>getConfigElement("displayName").orElse(this.modId);
        this.description = config.<String>getConfigElement("description")
                .orElse("MISSING DESCRIPTION")
                .replace("\r\n", "\n").stripIndent();

        this.logoFile = Optional.ofNullable(config.<String>getConfigElement("logoFile")
                .orElseGet(() -> ownFile.flatMap(mf -> mf.<String>getConfigElement("logoFile")).orElse(null)));
        this.logoBlur = config.<Boolean>getConfigElement("logoBlur")
                .orElseGet(() -> ownFile.flatMap(f -> f.<Boolean>getConfigElement("logoBlur"))
                        .orElse(true));

        this.updateJSONURL = config.<String>getConfigElement("updateJSONURL")
                .map(StringUtils::toURL);

        this.dependencies = ownFile.map(mfi -> mfi.getConfigList("dependencies", this.modId)
                .stream()
                .map(dep -> new ModVersion(this, dep))
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        this.features = ownFile.map(mfi -> mfi.<Map<String, String>>getConfigElement("features", this.modId)
                .stream()
                .flatMap(m->m.entrySet().stream())
                .map(e->new ForgeFeature.Bound(e.getKey(), e.getValue(), this))
                .collect(Collectors.toList())).orElse(Collections.emptyList());

        this.properties = ownFile.map(mfi -> mfi.<Map<String, Object>>getConfigElement("modproperties", this.modId)
                .orElse(Collections.emptyMap()))
                .orElse(Collections.emptyMap());

        this.modUrl = config.<String>getConfigElement("modUrl")
                .map(StringUtils::toURL);

        // verify we have a valid mod version otherwise throw an exception
        if (!VALID_VERSION.matcher(this.version.toString()).matches()) {
            throw new InvalidModFileException("Illegal version number specified "+this.version, this.getOwningFile());
        }
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
    public Optional<URL> getUpdateURL() {
        return this.updateJSONURL;
    }

    @Override
    public Optional<String> getLogoFile()
    {
        return this.logoFile;
    }

    @Override
    public boolean getLogoBlur()
    {
        return this.logoBlur;
    }

    @Override
    public IConfigurable getConfig() {
        return this;
    }

    @Override
    public List<? extends ForgeFeature.Bound> getForgeFeatures() {
        return this.features;
    }

    @Override
    public <T> Optional<T> getConfigElement(final String... key) {
        return this.config.getConfigElement(key);
    }

    @Override
    public List<? extends IConfigurable> getConfigList(final String... key) {
        return null;
    }

    @Override
    public Optional<URL> getModURL() {
        return modUrl;
    }

    class ModVersion implements net.minecraftforge.forgespi.language.IModInfo.ModVersion {
        private IModInfo owner;
        private final String modId;
        private final VersionRange versionRange;
        private final boolean mandatory;
        private final Ordering ordering;
        private final DependencySide side;
        private final Optional<URL> referralUrl;

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
            this.referralUrl = config.<String>getConfigElement("referralUrl")
                    .map(StringUtils::toURL);
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

        @Override
        public Optional<URL> getReferralURL() {
            return referralUrl;
        }
    }

}
