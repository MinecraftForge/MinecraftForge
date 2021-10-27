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

package net.minecraftforge.fmlclient;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraftforge.client.ForgeHooksClient;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceLocation;

@Deprecated(since="1.18", forRemoval = true)
public class ClientHooks
{
    @Deprecated(since="1.18", forRemoval = true)
    public static void processForgeListPingData(ServerStatus packet, ServerData target)
    {
        ForgeHooksClient.processForgeListPingData(packet, target);
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void drawForgePingInfo(JoinMultiplayerScreen gui, ServerData target, PoseStack mStack, int x, int y, int width, int relativeMouseX, int relativeMouseY)
    {
        ForgeHooksClient.drawForgePingInfo(gui, target, mStack, x, y, width, relativeMouseX, relativeMouseY);
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static String fixDescription(String description)
    {
        return description.endsWith(":NOFML§r") ? description.substring(0, description.length() - 8)+"§r" : description;
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void handleClientWorldClosing(ClientLevel world)
    {
        ForgeHooksClient.handleClientWorldClosing(world);
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void trackMissingTexture(ResourceLocation resourceLocation)
    {
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void trackBrokenTexture(ResourceLocation resourceLocation, String error)
    {
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void logMissingTextureErrors()
    {
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
        ForgeHooksClient.firePlayerLogin(pc, player, networkManager);
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void firePlayerLogout(MultiPlayerGameMode pc, LocalPlayer player) {
        ForgeHooksClient.firePlayerLogout(pc, player);
    }

    @Deprecated(since="1.18", forRemoval = true)
    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        ForgeHooksClient.firePlayerRespawn(pc, oldPlayer, newPlayer, networkManager);
    }


}
