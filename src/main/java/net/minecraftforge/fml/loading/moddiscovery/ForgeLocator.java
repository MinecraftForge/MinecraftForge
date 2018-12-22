/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.IModLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.SCAN;

/*
 * Locate Forge in the classpath, this works similar to ClasspathLocator.
 * Forge/Minecraft are in the following formats:
 *   Runtime:
 *     %lib%/net/minecraftforge/forge/{ver}/forge-{ver}.jar or ./forge-{ver}-universal.jar - Client is in libs, Server is in root
 *     %lib%/net/minecraftforge/forge/{ver}/forge-{ver}-{client|server}.jar - Forge, Vanilla classes, Patched
 *     %lib%/net/minecraft/{client|server}/{mcver}/{client|server}-{mcver}-srg.jar - Vanilla classes, SRG, Unpatched
 *
 *   UserDev:
 *     Forge and vanilla is in a single jar file, patched, on the classpath.
 *
 *   Eclipse:
 *     bin/{sourceset}/
 *
 *   Intellij: TODO: Verify, I don't use intellij
 *     out/production/resources/../classes/ - Resources are a separate folder so we have to navigate to classes
 *
 *   Gradle:
 *     build/classes/{language}/{sourceset}
 *     build/resources/{sourceset}
 *
 */
public class ForgeLocator implements IModLocator
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FORGE_META = "forgemod.toml";
    private static final String MAIN_SOURCESET = "main";
    private static final String LIB_RESOURCE = "org/objectweb/asm/Opcodes.class";
    private static final String LIB_PATH = "org/ow2/asm/asm".replace('/', File.separatorChar);
    private List<Path> roots = new ArrayList<>();
    private boolean filterClasses = false;

    public ForgeLocator() { }

    @Override
    public List<ModFile> scanMods()
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(FORGE_META);
        if (url == null)
            throw new IllegalStateException("Could not locate \"" + FORGE_META + "\", Forge is correupt/not on classpath!");

        File base = getRoot(url, FORGE_META);
        if (base == null)
            throw new IllegalStateException("Could not determine root for Forge resource: " + url);

        url = loader.getResource(LIB_RESOURCE);
        if (url == null)
            throw new IllegalStateException("Could not locate \"" + LIB_RESOURCE + "\" to locate libraries directory.");
        File libs = getRoot(url, LIB_RESOURCE);
        //If In maven style the libraries folder we need to drop the file name, and version folder
        if (libs != null && libs.getParentFile().getParentFile().getAbsolutePath().endsWith(LIB_PATH))
        {
            int count = 2 + LIB_PATH.split(Pattern.quote("" + File.separatorChar)).length;
            for (int x = 0; x < count; x++)
                libs = libs.getParentFile();
        }
        else
        {
            libs = null;
        }

        LOGGER.info("Forge Locator: ");
        LOGGER.info("  Forge: " + base);
        LOGGER.info("  Libraries: " + libs);

        Path path = base.toPath();
        if (base.isDirectory())
        { // Development workspace.
            int count = path.getNameCount();
            //Eclipse: /bin/{sourceset}/
            if (count >= 2 && MAIN_SOURCESET.equals(path.getName(count - 1).toString()) && "bin".equals(path.getName(count - 2).toString()))
            {
                roots.add(path);
                roots.add(path.getParent().resolve("userdev")); //Do I need this?
            } //Intellij: /out/production/resources/ & /out/production/classes/
            else if (count >= 1 && "resources".equals(path.getName(count - 1).toString()))
            {
                roots.add(path.getParent().resolve("classes"));
                roots.add(path);
            } //Gradle: build/resources/{sourceset} & build/classes/{language}/{sourceset}
            else if (count >= 2 && MAIN_SOURCESET.equals(path.getName(count - 1).toString()) && "resources".equals(path.getName(count - 2).toString()))
            {
                Path classes = path.getParent().getParent().resolve("classes");
                roots.add(classes.resolve(MAIN_SOURCESET));
                roots.add(classes.resolve("userdev"));
                roots.add(path);
                roots.add(path.getParent().resolve("userdev"));
            }
            filterClasses = true;
        }
        else if (base.isFile())
        {
            if (libs == null) //User dev workspace.
            {
                roots.add(path);
            }
            else //Standard install
            {
                String dist = FMLEnvironment.dist == Dist.CLIENT ? "client" : "server";
                String version = MCPVersion.getMCVersion() + "-" + ForgeVersion.getVersion();
                roots.add(path); // universal
                roots.add(libs.toPath().resolve(Paths.get(ForgeVersion.getGroup().replace('.', File.separatorChar), "forge", version, "forge-" + version + "-" + dist + ".jar"))); // Patched classes
                roots.add(libs.toPath().resolve(Paths.get("net", "minecraft", dist, MCPVersion.getMCPandMCVersion(), dist + "-" + MCPVersion.getMCPandMCVersion() + "-srg.jar"))); //Vanilla jar in srg names
            }

        }
        else
            throw new IllegalStateException("Forge path is neither File or Directory... " + base);

        if (roots.isEmpty())
            return Collections.emptyList();

        roots.forEach(r -> LOGGER.info("  Root: " + r));
        return Collections.singletonList(new ModFile(roots.get(0), this));
    }

    protected File getRoot(URL url, String resource)
    {
        String path = url.getPath();
        if ("jar".equals(url.getProtocol()))
        {
            int idx = path.indexOf("!/");
            path = path.substring(0, idx);
        }
        else
        {
            //Strip resource path off the end
            path = path.substring(0, path.length() - resource.length());
        }

        if (path.startsWith("file:"))
            path = path.substring(5);

        return new File(path).getAbsoluteFile();
    }

    @Override
    public String name()
    {
        return "forge locator";
    }

    @Override
    public Path findPath(final ModFile modFile, String... path)
    {
        if (path.length < 1)
            throw new IllegalArgumentException("Missing path");

        if (path.length == 2 && "META-INF".equals(path[0]) && "mods.toml".equals(path[1]))
            path = FORGE_META.split("/");

        if (filterClasses && path[path.length - 1].endsWith(".class"))
            path[path.length - 1] = path[path.length - 1] + "THIS_FILE_WILL_NEVER_EXITS_DIRTY_HAX";

        String[] tail = Arrays.copyOfRange(path, 1, path.length);
        for (Path root : roots)
        {
            Path target = root.resolve(root.getFileSystem().getPath(path[0], tail));
            if (Files.exists(target))
                return target;
        }

        return modFile.getFilePath().resolve(modFile.getFilePath().getFileSystem().getPath(path[0], tail));
    }

    @Override
    public void scanFile(final ModFile modFile, final Consumer<Path> pathConsumer)
    {
        final Set<String> visited = new HashSet<>();
        roots.stream().forEach(root ->
        {
            LOGGER.debug(SCAN,"Scanning Forge: " + root);
            try (Stream<Path> files = Files.find(root, Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class")))
            {
                files.forEach(f -> {
                    String relative = root.relativize(f).toString(); //TODO: Test jared files...
                    if (visited.add(relative))
                        pathConsumer.accept(Paths.get(relative));
                });
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        LOGGER.debug(SCAN,"Forge scan complete");
    }

    @Override
    public String toString()
    {
        return "{Forge locator}";
    }

    @Override
    public Optional<Manifest> findManifest(Path file)
    {
        return Optional.empty();
    }
}
