/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import java.io.File;
import java.util.List;
import java.util.Set;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;

public interface IFMLSidedHandler
{
    List<String> getAdditionalBrandingInformation();

    Side getSide();

    void haltGame(String message, Throwable exception);

    void showGuiScreen(Object clientGuiElement);

    void queryUser(StartupQuery query) throws InterruptedException;

    void beginServerLoading(MinecraftServer server);

    void finishServerLoading();

    File getSavesDirectory();

    MinecraftServer getServer();

    boolean shouldServerShouldBeKilledQuietly();

    void addModAsResource(ModContainer container);

    String getCurrentLanguage();

    void serverStopped();

    NetworkManager getClientToServerNetworkManager();

    INetHandler getClientPlayHandler();

    void fireNetRegistrationEvent(EventBus bus, NetworkManager manager, Set<String> channelSet, String channel, Side side);

    boolean shouldAllowPlayerLogins();

    void allowLogins();

    IThreadListener getWorldThread(INetHandler net);

    void processWindowMessages();

    String stripSpecialChars(String message);
}
