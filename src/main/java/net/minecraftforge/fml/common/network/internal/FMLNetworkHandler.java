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

package net.minecraftforge.fml.common.network.internal;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLMessage.CompleteHandshake;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.core.helpers.Integers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FMLNetworkHandler
{
    public static final int READ_TIMEOUT = Integers.parseInt(System.getProperty("fml.readTimeout","30"),30);
    public static final int LOGIN_TIMEOUT = Integers.parseInt(System.getProperty("fml.loginTimeout","600"),600);
    private static EnumMap<Side, FMLEmbeddedChannel> channelPair;

    public static void fmlServerHandshake(ServerConfigurationManager scm, NetworkManager manager, EntityPlayerMP player)
    {
        NetworkDispatcher dispatcher = NetworkDispatcher.allocAndSet(manager, scm);
        dispatcher.serverToClientHandshake(player);
    }

    public static void fmlClientHandshake(NetworkManager networkManager)
    {
        NetworkDispatcher dispatcher = NetworkDispatcher.allocAndSet(networkManager);
        dispatcher.clientToServerHandshake();
    }

    public static void openGui(EntityPlayer entityPlayer, Object mod, int modGuiId, World world, int x, int y, int z)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (entityPlayer instanceof EntityPlayerMP && !(entityPlayer instanceof FakePlayer))
        {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityPlayer;
            Container remoteGuiContainer = NetworkRegistry.INSTANCE.getRemoteGuiContainer(mc, entityPlayerMP, modGuiId, world, x, y, z);
            if (remoteGuiContainer != null)
            {
                entityPlayerMP.getNextWindowId();
                entityPlayerMP.closeContainer();
                int windowId = entityPlayerMP.currentWindowId;
                FMLMessage.OpenGui openGui = new FMLMessage.OpenGui(windowId, mc.getModId(), modGuiId, x, y, z);
                EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(entityPlayerMP);
                embeddedChannel.writeOutbound(openGui);
                entityPlayerMP.openContainer = remoteGuiContainer;
                entityPlayerMP.openContainer.windowId = windowId;
                entityPlayerMP.openContainer.onCraftGuiOpened(entityPlayerMP);
            }
        }
        else if (entityPlayer instanceof FakePlayer)
        {
            // NO OP - I won't even log a message!
        }
        else if (FMLCommonHandler.instance().getSide().equals(Side.CLIENT))
        {
            Object guiContainer = NetworkRegistry.INSTANCE.getLocalGuiContainer(mc, entityPlayer, modGuiId, world, x, y, z);
            FMLCommonHandler.instance().showGuiScreen(guiContainer);
        }
        else
        {
            FMLLog.fine("Invalid attempt to open a local GUI on a dedicated server. This is likely a bug. GUIID: %s,%d", mc.getModId(), modGuiId);
        }

    }

    public static void makeEntitySpawnAdjustment(Entity entity, EntityPlayerMP player, int serverX, int serverY, int serverZ)
    {
        EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        embeddedChannel.writeOutbound(new FMLMessage.EntityAdjustMessage(entity, serverX, serverY, serverZ));
    }

    public static Packet<?> getEntitySpawningPacket(Entity entity)
    {
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
        if (er == null)
        {
            return null;
        }
        if (er.usesVanillaSpawning())
        {
            return null;
        }

        return channelPair.get(Side.SERVER).generatePacketFrom(new FMLMessage.EntitySpawnMessage(er, entity, er.getContainer()));
    }

    public static String checkModList(FMLHandshakeMessage.ModList modListPacket, Side side)
    {
        Map<String,String> modList = modListPacket.modList();
        return checkModList(modList, side);
    }
    public static String checkModList(Map<String,String> listData, Side side)
    {
        List<ModContainer> rejects = Lists.newArrayList();
        for (Entry<ModContainer, NetworkModHolder> networkMod : NetworkRegistry.INSTANCE.registry().entrySet())
        {
            boolean result = networkMod.getValue().check(listData, side);
            if (!result)
            {
                rejects.add(networkMod.getKey());
            }
        }
        if (rejects.isEmpty())
        {
            return null;
        }
        else
        {
            FMLLog.info("Rejecting connection %s: %s", side, rejects);
            return String.format("Mod rejections %s",rejects);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void addClientHandlers()
    {
        ChannelPipeline pipeline = channelPair.get(Side.CLIENT).pipeline();
        String targetName = channelPair.get(Side.CLIENT).findChannelHandlerNameForType(FMLRuntimeCodec.class);
        pipeline.addAfter(targetName, "GuiHandler", new OpenGuiHandler());
        pipeline.addAfter(targetName, "EntitySpawnHandler", new EntitySpawnHandler());
    }
    public static void registerChannel(FMLContainer container, Side side)
    {
        channelPair = NetworkRegistry.INSTANCE.newChannel(container, "FML", new FMLRuntimeCodec(), new HandshakeCompletionHandler());
        EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.NOWHERE);

        if (side == Side.CLIENT)
        {
            addClientHandlers();
        }
    }

    public static List<FMLProxyPacket> forwardHandshake(CompleteHandshake push, NetworkDispatcher target, Side side)
    {
        channelPair.get(side).attr(NetworkDispatcher.FML_DISPATCHER).set(target);
        channelPair.get(side).writeOutbound(push);

        ArrayList<FMLProxyPacket> list = new ArrayList<FMLProxyPacket>();
        for (Object o: channelPair.get(side).outboundMessages())
        {
            list.add((FMLProxyPacket)o);
        }
        channelPair.get(side).outboundMessages().clear();
        return list;
    }


    public static void enhanceStatusQuery(JsonObject jsonobject)
    {
        JsonObject fmlData = new JsonObject();
        fmlData.addProperty("type", "FML");
        JsonArray modList = new JsonArray();
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            JsonObject modData = new JsonObject();
            modData.addProperty("modid", mc.getModId());
            modData.addProperty("version", mc.getVersion());
            modList.add(modData);
        }
        fmlData.add("modList", modList);
        jsonobject.add("modinfo", fmlData);
    }
}
