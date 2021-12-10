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

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.jarhandling.JarMetadata;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public abstract class AbstractModLocator extends AbstractModProvider implements IModLocator {
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final String MODS_TOML = "META-INF/mods.toml";
    protected static final String MANIFEST = "META-INF/MANIFEST.MF";

    public AbstractModLocator(final String name) {
        super(name);
    }


    public List<IModFile> scanMods()
    {
        return scanCandidates()
          .map(this::createMod)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());
    }

    public abstract Stream<Path> scanCandidates();

    protected Optional<IModFile> createMod(Path path) {
        var mjm = new ModJarMetadata();
        var sj = SecureJar.from(
            Manifest::new,
            jar -> jar.findFile(MODS_TOML).isPresent() ? mjm : JarMetadata.from(jar, path),
            (root, p) -> true,
            path
        );

        IModFile mod = null;
        var type = sj.getManifest().getMainAttributes().getValue(ModFile.TYPE);
        if (sj.findFile(MODS_TOML).isPresent()) {
            LOGGER.debug(SCAN, "Found {} mod of type {}: {}", MODS_TOML, type, path);
            mod = ModFile.newFMLInstance(this, sj);
        } else if (type != null) {
            LOGGER.debug(SCAN, "Found {} mod of type {}: {}", MANIFEST, type, path);
            mod = new ModFile(sj, this, this::manifestParser);
        } else {
            return Optional.empty();
        }

        mjm.setModFile(mod);
        return Optional.ofNullable(mod);
    }

    private IModFileInfo manifestParser(final IModFile mod) {
        Function<String, Optional<String>> cfg = name -> Optional.ofNullable(mod.getSecureJar().getManifest().getMainAttributes().getValue(name));
        var license = cfg.apply("LICENSE").orElse("");
        var dummy = new IConfigurable() {
            @Override
            public <T> Optional<T> getConfigElement(String... key) {
                return Optional.empty();
            }
            @Override
            public List<? extends IConfigurable> getConfigList(String... key) {
                return Collections.emptyList();
            }
        };

        return new IModFileInfo() {
            @Override public List<IModInfo> getMods() { return Collections.emptyList(); }
            @Override public List<LanguageSpec> requiredLanguageLoaders() { return Collections.emptyList(); }
            @Override public boolean showAsResourcePack() { return false; }
            @Override public Map<String, Object> getFileProperties() { return Collections.emptyMap(); }
            @Override public String getLicense() { return license; }
            @Override public IModFile getFile() { return mod; }
            @Override public IConfigurable getConfig() { return dummy; }

            // These Should never be called as it's only called from ModJarMetadata.version and we bypass that
            @Override public String moduleName() { return mod.getSecureJar().name(); }
            @Override public String versionString() { return null; }
            @Override public List<String> usesServices() { return null; }

            @Override
            public String toString() {
                return "IModFileInfo(" + mod.getFilePath() + ")";
            }
        };
    }
}
