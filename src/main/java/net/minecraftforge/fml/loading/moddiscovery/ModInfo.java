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

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.loading.StringSubstitutor;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModInfo implements IModInfo
{
    private static final DefaultArtifactVersion DEFAULT_VERSION = new DefaultArtifactVersion("1");
    private final ModFileInfo owningFile;
    private final String modId;
    private final ArtifactVersion version;
    private final String displayName;
    private final String description;
    private final List<IModInfo.ModVersion> dependencies;
    private final Map<String,Object> properties;
    private final UnmodifiableConfig modConfig;

    public ModInfo(final ModFileInfo owningFile, final UnmodifiableConfig modConfig)
    {
        this.owningFile = owningFile;
        this.modConfig = modConfig;
        this.modId = modConfig.<String>getOptional("modId").orElseThrow(() -> new InvalidModFileException("Missing modId entry", owningFile));
        this.version = modConfig.<String>getOptional("version").
                map(s->StringSubstitutor.replace(s, owningFile != null ? owningFile.getFile() : null )).
                map(DefaultArtifactVersion::new).orElse(DEFAULT_VERSION);
        this.displayName = modConfig.<String>getOptional("displayName").orElse(null);
        this.description = modConfig.get("description");
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

    public Optional<String> getLogoFile()
    {
        return this.owningFile != null ? this.owningFile.getConfig().getOptional("logoFile") : Optional.empty();
    }

    public boolean hasConfigUI()
    {
        return false;
    }
}
