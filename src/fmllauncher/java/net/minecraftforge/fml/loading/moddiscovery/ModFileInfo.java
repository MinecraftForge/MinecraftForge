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
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.StringUtils;
import org.apache.maven.artifact.versioning.VersionRange;

import java.net.URL;
import java.util.*;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class ModFileInfo implements IModFileInfo
{
    private final UnmodifiableConfig config;
    private final ModFile modFile;
    private final URL issueURL;
    private final String modLoader;
    private final VersionRange modLoaderVersion;
    private final boolean showAsResourcePack;
    private final List<IModInfo> mods;
    private final Map<String,Object> properties;

    ModFileInfo(final ModFile modFile, final UnmodifiableConfig config)
    {
        this.modFile = modFile;
        this.config = config;
        this.modLoader = config.<String>getOptional("modLoader").
                orElseThrow(()->new InvalidModFileException("Missing ModLoader in file", this));
        this.modLoaderVersion = config.<String>getOptional("loaderVersion").
                map(MavenVersionAdapter::createFromVersionSpec).
                orElseThrow(()->new InvalidModFileException("Missing ModLoader version in file", this));
        this.showAsResourcePack = config.<Boolean>getOrElse("showAsResourcePack", false);
        this.properties = config.<UnmodifiableConfig>getOptional("properties").
                map(UnmodifiableConfig::valueMap).orElse(Collections.emptyMap());
        this.modFile.setFileProperties(this.properties);
        final ArrayList<UnmodifiableConfig> modConfigs = config.getOrElse("mods", ArrayList::new);
        if (modConfigs.isEmpty()) {
            throw new InvalidModFileException("Missing mods list", this);
        }
        this.mods = modConfigs.stream().map(mi-> new ModInfo(this, mi)).collect(Collectors.toList());
        this.issueURL = config.<String>getOptional("issueTrackerURL").map(StringUtils::toURL).orElse(null);
    }

    @Override
    public List<IModInfo> getMods()
    {
        return mods;
    }

    public ModFile getFile()
    {
        return this.modFile;
    }

    @Override
    public UnmodifiableConfig getConfig()
    {
        return this.config;
    }

    @Override
    public String getModLoader()
    {
        return modLoader;
    }

    @Override
    public VersionRange getModLoaderVersion()
    {
        return modLoaderVersion;
    }

    @Override
    public Map<String, Object> getFileProperties() {
        return this.properties;
    }

    public Optional<Manifest> getManifest() {
        return modFile.getLocator().findManifest(modFile.getFilePath());
    }

    @Override
    public boolean showAsResourcePack() {
        return this.showAsResourcePack;
    }
}
