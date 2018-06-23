/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.server;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.SidedProvider;

public class ServerModLoader
{
    private static DedicatedServer server;
    public static void begin(DedicatedServer dedicatedServer) {
        ServerModLoader.server = dedicatedServer;
        SidedProvider.setServer(()->dedicatedServer);
        LogicalSidedProvider.setServer(()->dedicatedServer);
        ModLoader.get().loadMods();
    }

    public static void end() {
        ModLoader.get().finishMods();
    }
}
