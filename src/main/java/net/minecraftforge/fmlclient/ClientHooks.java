/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
