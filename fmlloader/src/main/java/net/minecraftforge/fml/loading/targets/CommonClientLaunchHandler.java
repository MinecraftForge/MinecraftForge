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

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.VersionInfo;
import net.minecraftforge.api.distmarker.Dist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public abstract class CommonClientLaunchHandler extends CommonLaunchHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override public Dist getDist()  { return Dist.CLIENT; }
    @Override public String getNaming() { return "srg"; }
    @Override public boolean isProduction() { return true; }

    @Override
    public Callable<Void> launchService(String[] arguments, ModuleLayer layer) {
        return () -> {
            Class.forName(layer.findModule("minecraft").orElseThrow(),"net.minecraft.client.main.Main").getMethod("main", String[].class).invoke(null, (Object)arguments);
            return null;
        };
    }

    @Override
    public LocatedPaths getMinecraftPaths() {
        final var vers = FMLLoader.versionInfo();
        var mc = LibraryFinder.findPathForMaven("net.minecraft", "client", "", "srg", vers.mcAndMCPVersion());
        var mcextra = LibraryFinder.findPathForMaven("net.minecraft", "client", "", "extra", vers.mcAndMCPVersion());
        var mcstream = Stream.<Path>builder().add(mc).add(mcextra);
        var modstream = Stream.<List<Path>>builder();

        processMCStream(vers, mcstream, modstream);

        var fmlcore = LibraryFinder.findPathForMaven(vers.forgeGroup(), "fmlcore", "", "", vers.mcAndForgeVersion());
        var javafmllang = LibraryFinder.findPathForMaven(vers.forgeGroup(), "javafmllanguage", "", "", vers.mcAndForgeVersion());
        var mclang = LibraryFinder.findPathForMaven(vers.forgeGroup(), "mclanguage", "", "", vers.mcAndForgeVersion());

        return new LocatedPaths(mcstream.build().toList(), (a,b) -> true, modstream.build().toList(), List.of(fmlcore, javafmllang, mclang));
    }

    protected abstract void processMCStream(VersionInfo versionInfo, Stream.Builder<Path> mc, Stream.Builder<List<Path>> mods);
}
