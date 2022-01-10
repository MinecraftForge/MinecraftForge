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

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Locator for test mods in the Minecraft Forge repository.
 *
 * <p>This serves to replace the {@code mods.toml} file in the test source set of the repository, which caused constant
 * merge conflicts between open PRs when they add their own test mods. By moving to a mod locator, contributors no
 * longer need to add their test mod to a {@code mods.toml} file, and maintainers can rest assured that merge conflicts
 * on a PR actually mean substantive conflicts rather than just a conflict in the test sourceset TOML file.</p>
 *
 * <p>This locator is hardcoded to look for mods using the FML Java built-in language provider, whose mods are marked
 * with the {@code net.minecraftforge.fml.common.Mod} annotation. A sizeable portion of code in this class is inspired
 * by and borrowed from {@link MinecraftLocator}</p>
 */
public class TestModLocator implements IModLocator
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type MOD_ANNOTATION = Type.getType("Lnet/minecraftforge/fml/common/Mod;");

    @SuppressWarnings("removal")
    @Override
    public List<IModFile> scanMods()
    {
        final TestSources testSources = getTestSources();
        if (testSources == TestSources.EMPTY) return List.of();

        LOGGER.info("Test mod locator is active and looking at the test sources for ID {} at paths: {}",
                testSources.id(), testSources.paths());

        // TODO: find out the non-deprecated replacement for ModJarMetadata#buildFile
        final List<IModFile> modFile = ModJarMetadata.buildFile(
                jar -> new ModFile(jar, this, this::buildTestModsTOML),
                jar -> true,
                (path, base) -> true,
                testSources.paths().toArray(Path[]::new)).stream().toList();

        if (modFile.isEmpty())
        {
            LOGGER.warn("Could not build test mod file from configured test sources");
        }

        return modFile;
    }

    private IModFileInfo buildTestModsTOML(IModFile modFileIn)
    {
        // We make the same assumption as MinecraftLocator that the IModFile provided is an instanceof ModFile
        final ModFile modFile = (ModFile) modFileIn;

        // At this point, there is no scan data in the ModFile yet, so let's scan it ourselves
        // This is normally done in a background thread, but as this is just the test source set, we just call directly
        final ModFileScanData scanData = modFile.compileContent();
        final List<ModFileScanData.AnnotationData> testModAnnotations = scanData.getAnnotations().stream()
                .filter(annotation -> annotation.targetType() == ElementType.TYPE)
                .filter(annotation -> annotation.annotationType().equals(MOD_ANNOTATION))
                .toList();

        LOGGER.info(LogMarkers.SCAN, "Found {} test mod(s) from Mod annotations", testModAnnotations.size());

        final CommentedConfig modFileTOML = TomlFormat.newConfig();
        modFileTOML.set("modLoader", "javafml"); // Mods in the test source set all use FML Java lang provider
        modFileTOML.set("loaderVersion", "[1,)"); // Accept any loader version
        modFileTOML.set("license", "LGPLv2.1"); // Forge license

        final List<CommentedConfig> mods = new ArrayList<>(testModAnnotations.size());
        for (ModFileScanData.AnnotationData modAnnotation : testModAnnotations)
        {
            final String modId = Objects.toString(modAnnotation.annotationData().get("value"));
            LOGGER.debug(LogMarkers.SCAN, "Found test mod with mod ID {}", modId);
            final CommentedConfig modInfo = TomlFormat.newConfig();
            mods.add(modInfo);
            modInfo.set("modId", modId); // We only need to set the modId
        }
        modFileTOML.set("mods", mods);

        final NightConfigWrapper configWrapper = new NightConfigWrapper(modFileTOML);
        final ModFileInfo modFileInfo = new ModFileInfo(modFile, configWrapper, List.of()); // No extra language specs
        configWrapper.setFile(modFileInfo);
        return modFileInfo;
    }

    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer)
    {
        LOGGER.debug(LogMarkers.SCAN, "Scan started: {}", modFile);
        try (Stream<Path> files = Files.find(modFile.getSecureJar().getRootPath(), Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class")))
        {
            files.forEach(pathConsumer);
        } catch (IOException e)
        {
            LOGGER.warn("Error while scanning file {}", modFile, e);
        }
        LOGGER.debug(LogMarkers.SCAN, "Scan finished: {}", modFile);
    }

    @Override
    public void initArguments(Map<String, ?> arguments)
    {
    }

    @Override
    public boolean isValid(IModFile modFile)
    {
        return true;
    }

    @Override
    public String name()
    {
        return "test mods locator";
    }

    // See CommonLaunchHandler#getModClasses
    static TestSources getTestSources()
    {
        final String modClasses = System.getenv("MOD_CLASSES");
        final String testId = System.getProperty("forge.test.id");
        if (modClasses == null || testId == null) return TestSources.EMPTY;

        // modid%%path;modid%%path
        // where ';' is File.pathSeparator

        record ExplodedModDirectory(String modId, Path path) {
            static ExplodedModDirectory create(String modClassesPart) {
                final String[] split = modClassesPart.split("%%", 2);
                final String modId = split.length == 1 ? "defaultmodid" : split[0];
                final Path path = Path.of(split[split.length - 1]);
                return new ExplodedModDirectory(modId, path);
            }
        }

        final Map<String, List<Path>> modClassPaths = Arrays.stream(modClasses.split(File.pathSeparator))
                .map(ExplodedModDirectory::create)
                .collect(Collectors.groupingBy(ExplodedModDirectory::modId, Collectors.mapping(ExplodedModDirectory::path, Collectors.toList())));

        return new TestSources(testId, modClassPaths.getOrDefault(testId, List.of()));
    }

    /**
     * @param id the named ID for the test sources
     * @param paths the paths to the test sources
     */
    record TestSources(String id, List<Path> paths)
    {
        public static final TestSources EMPTY = new TestSources("blank", List.of());
    }
}
