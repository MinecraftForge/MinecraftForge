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

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.VersionInfo;

public class FMLClientLaunchHandler extends CommonClientLaunchHandler {
    @Override public String name() { return "fmlclient"; }

    @Override
    protected void processMCStream(VersionInfo versionInfo, Stream.Builder<Path> mc, Stream.Builder<List<Path>> mods) {
        var fmlonly = LibraryFinder.findPathForMaven(versionInfo.forgeGroup(), "fmlonly", "", "universal", versionInfo.mcAndForgeVersion());
        mods.add(List.of(fmlonly));
    }
}
