package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.MavenCoordinateResolver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MavenDirectoryLocator extends AbstractJarFileLocator implements IModLocator {
    private List<Path> modCoords;

    @Override
    public List<ModFile> scanMods() {
        return modCoords.stream().
                map(mc -> new ModFile(mc, this)).
                peek(f->modJars.compute(f, (mf, fs)->createFileSystem(mf))).
                collect(Collectors.toList());
    }

    @Override
    public String name() {
        return "maven libs";
    }

    public String toString() {
        return "{Maven Directory locator for mods "+this.modCoords+"}";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initArguments(final Map<String, ?> arguments) {
        final List<String> mavenRoots = (List<String>) arguments.get("mavenRoots");
        final List<Path> mavenRootPaths = mavenRoots.stream().map(n -> FMLPaths.GAMEDIR.get().resolve(n)).collect(Collectors.toList());
        final List<String> mods = (List<String>) arguments.get("mods");
        List<Path> localModCoords = mods.stream().map(MavenCoordinateResolver::get).collect(Collectors.toList());
        // find the modCoords path in each supplied maven path, and turn it into a mod file. (skips not found files)

        this.modCoords = localModCoords.stream().map(mc -> mavenRootPaths.stream().map(root -> root.resolve(mc)).filter(path -> Files.exists(path)).findFirst().orElseThrow(() -> new IllegalArgumentException("Failed to locate requested mod coordinate " + mc))).collect(Collectors.toList());
    }
}
