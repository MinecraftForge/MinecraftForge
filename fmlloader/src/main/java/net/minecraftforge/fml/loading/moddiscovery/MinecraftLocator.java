/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

import com.electronwill.nightconfig.core.Config;
import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import net.minecraftforge.forgespi.locating.ModFileFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinecraftLocator extends AbstractModLocator {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<IModFile> scanMods() {
        final var launchHandler = FMLLoader.getLaunchHandler();
        var baseMC = launchHandler.getMinecraftPaths();
        var mcjar = ModJarMetadata.buildFile(j->ModFileFactory.FACTORY.build(j, this, this::buildMinecraftTOML), j->true, baseMC.minecraftFilter(), baseMC.minecraftPaths().toArray(Path[]::new)).orElseThrow();
        var artifacts = baseMC.otherArtifacts().stream()
                .map(SecureJar::from)
                .map(sj -> new ModFile(sj, this, ModFileParser::modsTomlParser))
                .collect(Collectors.<IModFile>toList());
        var othermods = baseMC.otherModPaths().stream()
                .map(p -> createMod(p.toArray(Path[]::new)).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        artifacts.add(mcjar);
        artifacts.addAll(othermods);
        return artifacts;
    }

    private IModFileInfo buildMinecraftTOML(final IModFile iModFile) {
        final ModFile modFile = (ModFile) iModFile;
        /*
        final Path mcmodtoml = modFile.findResource("META-INF", "minecraftmod.toml");
        if (Files.notExists(mcmodtoml)) {
            LOGGER.fatal(LOADING, "Mod file {} is missing minecraftmod.toml file", modFile.getFilePath());
            return null;
        }

        final FileConfig mcmodstomlfile = FileConfig.builder(mcmodtoml).build();
        mcmodstomlfile.load();
        mcmodstomlfile.close();
        */


        // We haven't changed this in years, and I can't be asked right now to special case this one file in the path.
        final var conf = Config.inMemory();
        conf.set("modLoader", "minecraft");
        conf.set("loaderVersion", "1");
        conf.set("license", "Mojang Studios, All Rights Reserved");
        final var mods = Config.inMemory();
        mods.set("modId", "minecraft");
        mods.set("version", FMLLoader.versionInfo().mcVersion());
        mods.set("displayName", "Minecraft");
        mods.set("logoFile", "mcplogo.png");
        mods.set("credits", "Mojang, deobfuscated by MCP");
        mods.set("authors", "MCP: Searge,ProfMobius,IngisKahn,Fesh0r,ZeuX,R4wk,LexManos,Bspkrs");
        mods.set("description", "Minecraft, decompiled and deobfuscated with MCP technology");
        conf.set("mods", List.of(mods));
        /*
        conf.putAll(mcmodstomlfile);

        final var extralangs = Stream.<IModFileInfo.LanguageSpec>builder();
        final Path forgemodtoml = modFile.findResource("META-INF", "mods.toml");
        if (Files.notExists(forgemodtoml)) {
            LOGGER.info("No forge mods.toml file found, not loading forge mod");
        } else {
            final FileConfig forgemodstomlfile = FileConfig.builder(forgemodtoml).build();
            forgemodstomlfile.load();
            forgemodstomlfile.close();
            conf.putAll(forgemodstomlfile);
            conf.<List<Object>>get("mods").add(0, mcmodstomlfile.<List<Object>>get("mods").get(0)); // Add MC as a sub-mod
            extralangs.add(new IModFileInfo.LanguageSpec(mcmodstomlfile.get("modLoader"), MavenVersionAdapter.createFromVersionSpec(mcmodstomlfile.get("loaderVersion"))));
        }
        */


        final NightConfigWrapper configWrapper = new NightConfigWrapper(conf);
        //final ModFileInfo modFileInfo = new ModFileInfo(modFile, configWrapper, extralangs.build().toList());
        final ModFileInfo modFileInfo = new ModFileInfo(modFile, configWrapper, List.of());
        configWrapper.setFile(modFileInfo);
        return modFileInfo;
    }

    @Override
    public String name() {
        return "minecraft";
    }

    @Override
    public void scanFile(final IModFile modFile, final Consumer<Path> pathConsumer) {
        LOGGER.debug(LogMarkers.SCAN, "Scan started: {}", modFile);
        try (Stream<Path> files = Files.find(modFile.getSecureJar().getRootPath(), Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
            files.forEach(pathConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug(LogMarkers.SCAN, "Scan finished: {}", modFile);
    }

    @Override
    public void initArguments(final Map<String, ?> arguments) {
        // no op
    }
}
