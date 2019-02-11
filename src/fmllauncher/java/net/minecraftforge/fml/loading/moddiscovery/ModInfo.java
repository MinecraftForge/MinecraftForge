/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.StringSubstitutor;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ModInfo implements IModInfo
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
    private final URL updateJSONURL;
    private final List<IModInfo.ModVersion> dependencies;
    private final Map<String,Object> properties;
    private final UnmodifiableConfig modConfig;

    public ModInfo(final ModFileInfo owningFile, final UnmodifiableConfig modConfig)
    {
        this.owningFile = owningFile;
        this.modConfig = modConfig;
        this.modId = modConfig.<String>getOptional("modId").orElseThrow(() -> new InvalidModFileException("Missing modId entry", owningFile));
        if (!VALID_LABEL.matcher(this.modId).matches()) {
            LOGGER.fatal("Invalid modId found in file {} - {} does not match the standard: {}", this.owningFile.getFile().getFilePath(), this.modId, VALID_LABEL.pattern());
            throw new InvalidModFileException("Invalid modId found : "+ this.modId, owningFile);
        }
        this.namespace = modConfig.<String>getOptional("namespace").orElse(this.modId);
        if (!VALID_LABEL.matcher(this.namespace).matches()) {
            LOGGER.fatal("Invalid override namespace found in file {} - {} does not match the standard: {}", this.owningFile.getFile().getFilePath(), this.namespace, VALID_LABEL.pattern());
            throw new InvalidModFileException("Invalid override namespace found : "+ this.namespace, owningFile);
        }
        this.version = modConfig.<String>getOptional("version").
                map(s->StringSubstitutor.replace(s, owningFile != null ? owningFile.getFile() : null )).
                map(DefaultArtifactVersion::new).orElse(DEFAULT_VERSION);
        this.displayName = modConfig.<String>getOptional("displayName").orElse(null);
        this.description = modConfig.get("description");
        this.updateJSONURL = modConfig.<String>getOptional("updateJSONURL").map(StringUtils::toURL).orElse(null);
        if (owningFile != null) {
            this.dependencies = owningFile.getConfig().<List<UnmodifiableConfig>>getOptional(Arrays.asList("dependencies", this.modId)).
                    orElse(Collections.emptyList()).stream().map(dep -> new ModVersion(this, dep)).collect(Collectors.toList());
            this.properties = owningFile.getConfig().<UnmodifiableConfig>getOptional(Arrays.asList("modproperties", this.modId)).
                    map(UnmodifiableConfig::valueMap).orElse(Collections.emptyMap());
        } else {
            this.dependencies = Collections.emptyList();
            this.properties = Collections.emptyMap();
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
    public List<IModInfo.ModVersion> getDependencies() {
        return this.dependencies;
    }

    @Override
    public UnmodifiableConfig getModConfig() {
        return this.modConfig;
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
        return this.owningFile != null ? this.owningFile.getConfig().getOptional("logoFile") : this.modConfig.getOptional("logoFile");
    }

    public boolean hasConfigUI()
    {
        return false;
    }
}
