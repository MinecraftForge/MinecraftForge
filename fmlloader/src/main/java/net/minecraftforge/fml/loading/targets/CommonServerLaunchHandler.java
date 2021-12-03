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

package net.minecraftforge.fml.loading.targets;

import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.VersionInfo;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public abstract class CommonServerLaunchHandler extends CommonLaunchHandler {
    protected static final Logger LOGGER = LogManager.getLogger();

    @Override public Dist getDist()  { return Dist.DEDICATED_SERVER; }
    @Override public String getNaming() { return "srg"; }
    @Override public boolean isProduction() { return true; }

    @Override
    public Callable<Void> launchService(String[] arguments, ModuleLayer layer) {
        return () -> {
            Class.forName(layer.findModule("minecraft").orElseThrow(),"net.minecraft.server.Main").getMethod("main", String[].class).invoke(null, (Object)arguments);
            return null;
        };
    }

    @Override
    public LocatedPaths getMinecraftPaths() {
        final var vers = FMLLoader.versionInfo();
        var mc = LibraryFinder.findPathForMaven("net.minecraft", "server", "", "srg", vers.mcAndMCPVersion());
        var mcextra = LibraryFinder.findPathForMaven("net.minecraft", "server", "", "extra", vers.mcAndMCPVersion());
        var mcextra_filtered = SecureJar.from( // We only want it for it's resources. So filter everything else out.
            (path, base) -> {
                return path.equals("META-INF/versions/") || // This is required because it bypasses our filter for the manifest, and it's a multi-release jar.
                     (!path.endsWith(".class") &&
                      !path.startsWith("META-INF/"));
            }, mcextra
        );
        BiPredicate<String, String> filter = (path, base) -> true;

        var mcstream = Stream.<Path>builder().add(mc).add(mcextra_filtered.getRootPath());
        var modstream = Stream.<List<Path>>builder();

        filter = processMCStream(vers, mcstream, filter, modstream);

        var fmlcore = LibraryFinder.findPathForMaven(vers.forgeGroup(), "fmlcore", "", "", vers.mcAndForgeVersion());
        var javafmllang = LibraryFinder.findPathForMaven(vers.forgeGroup(), "javafmllanguage", "", "", vers.mcAndForgeVersion());
        var mclang = LibraryFinder.findPathForMaven(vers.forgeGroup(), "mclanguage", "", "", vers.mcAndForgeVersion());

        return new LocatedPaths(mcstream.build().toList(), filter, modstream.build().toList(), List.of(fmlcore, javafmllang, mclang));
    }

    protected abstract BiPredicate<String, String> processMCStream(VersionInfo versionInfo, Stream.Builder<Path> mc, BiPredicate<String, String> filter, Stream.Builder<List<Path>> mods);
}
