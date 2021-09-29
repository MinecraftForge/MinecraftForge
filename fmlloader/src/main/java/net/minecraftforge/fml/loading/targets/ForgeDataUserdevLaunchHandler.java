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

import net.minecraftforge.api.distmarker.Dist;

import java.util.concurrent.Callable;

public class ForgeDataUserdevLaunchHandler extends ForgeUserdevLaunchHandler {
    @Override
    public String name() { return "forgedatauserdev"; }

    @Override
    public Dist getDist() { return Dist.CLIENT; }

    @Override
    public boolean isData() { return true; }

    @Override
    public Callable<Void> launchService(String[] arguments, ModuleLayer layer) {
        return () -> {
            var args = preLaunch(arguments, layer);

            Class.forName(layer.findModule("minecraft").orElseThrow(), "net.minecraft.data.Main").getMethod("main", String[].class).invoke(null, (Object) args);
            return null;
        };
    }
}
