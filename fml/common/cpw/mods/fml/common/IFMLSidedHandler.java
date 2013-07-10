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

import com.google.common.collect.MapDifference;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.relauncher.Side;

public interface IFMLSidedHandler
{
    List<String> getAdditionalBrandingInformation();

    Side getSide();

    void haltGame(String message, Throwable exception);

    void showGuiScreen(Object clientGuiElement);

    Entity spawnEntityIntoClientWorld(EntityRegistration registration, EntitySpawnPacket packet);

    void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket);

    void beginServerLoading(MinecraftServer server);

    void finishServerLoading();

    MinecraftServer getServer();

    void sendPacket(Packet packet);

    void displayMissingMods(ModMissingPacket modMissingPacket);

    void handleTinyPacket(NetHandler handler, Packet131MapData mapData);

    void setClientCompatibilityLevel(byte compatibilityLevel);

    byte getClientCompatibilityLevel();

    boolean shouldServerShouldBeKilledQuietly();

    void disconnectIDMismatch(MapDifference<Integer, ItemData> s, NetHandler toKill, INetworkManager mgr);

    void addModAsResource(ModContainer container);

    void updateResourcePackList();
}
