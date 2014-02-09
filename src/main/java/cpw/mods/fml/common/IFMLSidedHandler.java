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

package cpw.mods.fml.common;

import java.util.List;
import java.util.Set;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.relauncher.Side;

public interface IFMLSidedHandler
{
    List<String> getAdditionalBrandingInformation();

    Side getSide();

    void haltGame(String message, Throwable exception);

    void showGuiScreen(Object clientGuiElement);

    void beginServerLoading(MinecraftServer server);

    void finishServerLoading();

    MinecraftServer getServer();

    boolean shouldServerShouldBeKilledQuietly();

    void addModAsResource(ModContainer container);

    void updateResourcePackList();

    String getCurrentLanguage();

    void serverStopped();

    NetworkManager getClientToServerNetworkManager();

    INetHandler getClientPlayHandler();

    void waitForPlayClient();

    void fireNetRegistrationEvent(EventBus bus, NetworkManager manager, Set<String> channelSet, String channel, Side side);

    FMLMissingMappingsEvent.Action getDefaultMissingAction();

    void serverLoadedSuccessfully();

    void failedServerLoading(RuntimeException ex, WorldAccessContainer wac);
}
