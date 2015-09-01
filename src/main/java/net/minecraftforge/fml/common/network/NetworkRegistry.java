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

package net.minecraftforge.fml.common.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.INetHandler;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.internal.NetworkModHolder;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @author cpw
 *
 */
public enum NetworkRegistry
{
    INSTANCE;
    private EnumMap<Side,Map<String,FMLEmbeddedChannel>> channels = Maps.newEnumMap(Side.class);
    private Map<ModContainer, NetworkModHolder> registry = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> serverGuiHandlers = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> clientGuiHandlers = Maps.newHashMap();

    /**
     * Set in the {@link ChannelHandlerContext}
     */
    public static final AttributeKey<String> FML_CHANNEL = AttributeKey.valueOf("fml:channelName");
    public static final AttributeKey<Side> CHANNEL_SOURCE = AttributeKey.valueOf("fml:channelSource");
    public static final AttributeKey<ModContainer> MOD_CONTAINER = AttributeKey.valueOf("fml:modContainer");
    public static final AttributeKey<INetHandler> NET_HANDLER = AttributeKey.valueOf("fml:netHandler");
    public static final AttributeKey<Boolean> FML_MARKER = AttributeKey.valueOf("fml:hasMarker");

    // Version 1: ServerHello only contains this value as a byte
    // Version 2: ServerHello additionally contains a 4 byte (int) dimension for the logging in client
    public static final byte FML_PROTOCOL = 2;

    private NetworkRegistry()
    {
        channels.put(Side.CLIENT, Maps.<String,FMLEmbeddedChannel>newConcurrentMap());
        channels.put(Side.SERVER, Maps.<String,FMLEmbeddedChannel>newConcurrentMap());
    }

    /**
     * Represents a target point for the ALLROUNDPOINT target.
     *
     * @author cpw
     *
     */
    public static class TargetPoint {
        /**
         * A target point
         * @param dimension The dimension to target
         * @param x The X coordinate
         * @param y The Y coordinate
         * @param z The Z coordinate
         * @param range The range
         */
        public TargetPoint(int dimension, double x, double y, double z, double range)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.range = range;
            this.dimension = dimension;
        }
        public final double x;
        public final double y;
        public final double z;
        public final double range;
        public final int dimension;
    }
    /**
     * Create a new synchronous message channel pair based on netty.
     *
     * There are two preconstructed models available:
     * <ul>
     * <li> {@link #newSimpleChannel(String)} provides {@link SimpleNetworkWrapper}, a simple implementation of a netty handler, suitable for those who don't
     * wish to dive too deeply into netty.
     * <li> {@link #newEventChannel(String)} provides {@link FMLEventChannel} an event driven implementation, with lower level
     * access to the network data stream, for those with advanced bitbanging needs that don't wish to poke netty too hard.
     * <li> Alternatively, simply use the netty features provided here and implement the full power of the netty stack.
     * </ul>
     *
     * There are two channels created : one for each logical side (considered as the source of an outbound message)
     * The returned map will contain a value for each logical side, though both will only be working in the
     * integrated server case.
     *
     * The channel expects to read and write using {@link FMLProxyPacket}. All operation is synchronous, as the
     * asynchronous behaviour occurs at a lower level in netty.
     *
     * The first handler in the pipeline is special and should not be removed or moved from the head - it transforms
     * packets from the outbound of this pipeline into custom packets, based on the current {@link AttributeKey} value
     * {@link NetworkRegistry#FML_MESSAGETARGET} and {@link NetworkRegistry#FML_MESSAGETARGETARGS} set on the channel.
     * For the client to server channel (source side : CLIENT) this is fixed as "TOSERVER". For SERVER to CLIENT packets,
     * several possible values exist.
     *
     * Mod Messages should be transformed using a something akin to a {@link MessageToMessageCodec}. FML provides
     * a utility codec, {@link FMLIndexedMessageToMessageCodec} that transforms from {@link FMLProxyPacket} to a mod
     * message using a message discriminator byte. This is optional, but highly recommended for use.
     *
     * Note also that the handlers supplied need to be {@link ChannelHandler.Shareable} - they are injected into two
     * channels.
     *
     * @param name
     * @param handlers
     * @return
     */
    public EnumMap<Side,FMLEmbeddedChannel> newChannel(String name, ChannelHandler... handlers)
    {
        if (channels.get(Side.CLIENT).containsKey(name) || channels.get(Side.SERVER).containsKey(name) || name.startsWith("MC|") || name.startsWith("\u0001") || name.startsWith("FML"))
        {
            throw new RuntimeException("That channel is already registered");
        }
        EnumMap<Side,FMLEmbeddedChannel> result = Maps.newEnumMap(Side.class);

        for (Side side : Side.values())
        {
            FMLEmbeddedChannel channel = new FMLEmbeddedChannel(name, side, handlers);
            channels.get(side).put(name,channel);
            result.put(side, channel);
        }
        return result;
    }

    /**
     * Construct a new {@link SimpleNetworkWrapper} for the channel.
     *
     * @param name The name of the channel
     * @return A {@link SimpleNetworkWrapper} for handling this channel
     */
    public SimpleNetworkWrapper newSimpleChannel(String name)
    {
        return new SimpleNetworkWrapper(name);
    }
    /**
     * Construct a new {@link FMLEventChannel} for the channel.
     *
     * @param name The name of the channel
     * @return An {@link FMLEventChannel} for handling this channel
     */
    public FMLEventChannel newEventDrivenChannel(String name)
    {
        return new FMLEventChannel(name);
    }
    /**
     * INTERNAL Create a new channel pair with the specified name and channel handlers.
     * This is used internally in forge and FML
     *
     * @param container The container to associate the channel with
     * @param name The name for the channel
     * @param handlers Some {@link ChannelHandler} for the channel
     * @return an {@link EnumMap} of the pair of channels. keys are {@link Side}. There will always be two entries.
     */
    public EnumMap<Side,FMLEmbeddedChannel> newChannel(ModContainer container, String name, ChannelHandler... handlers)
    {
        if (channels.get(Side.CLIENT).containsKey(name) || channels.get(Side.SERVER).containsKey(name) || name.startsWith("MC|") || name.startsWith("\u0001") || (name.startsWith("FML") && !("FML".equals(container.getModId()))))
        {
            throw new RuntimeException("That channel is already registered");
        }
        EnumMap<Side,FMLEmbeddedChannel> result = Maps.newEnumMap(Side.class);

        for (Side side : Side.values())
        {
            FMLEmbeddedChannel channel = new FMLEmbeddedChannel(container, name, side, handlers);
            channels.get(side).put(name,channel);
            result.put(side, channel);
        }
        return result;
    }

    public FMLEmbeddedChannel getChannel(String name, Side source)
    {
        return channels.get(source).get(name);
    }
    /**
     * Register an {@link IGuiHandler} for the supplied mod object.
     *
     * @param mod The mod to handle GUIs for
     * @param handler A handler for creating GUI related objects
     */
    public void registerGuiHandler(Object mod, IGuiHandler handler)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null)
        {
            FMLLog.log(Level.ERROR, "Mod of type %s attempted to register a gui network handler during a construction phase", mod.getClass().getName());
            throw new RuntimeException("Invalid attempt to create a GUI during mod construction. Use an EventHandler instead");
        }
        serverGuiHandlers.put(mc, handler);
        clientGuiHandlers.put(mc, handler);
    }

    /**
     * INTERNAL method for accessing the Gui registry
     * @param mc Mod Container
     * @param player Player
     * @param modGuiId guiId
     * @param world World
     * @param x X coord
     * @param y Y coord
     * @param z Z coord
     * @return The server side GUI object (An instance of {@link Container})
     */
    public Container getRemoteGuiContainer(ModContainer mc, EntityPlayerMP player, int modGuiId, World world, int x, int y, int z)
    {
        IGuiHandler handler = serverGuiHandlers.get(mc);

        if (handler != null)
        {
            return (Container)handler.getServerGuiElement(modGuiId, player, world, x, y, z);
        }
        else
        {
            return null;
        }
    }

    /**
     * INTERNAL method for accessing the Gui registry
     * @param mc Mod Container
     * @param player Player
     * @param modGuiId guiId
     * @param world World
     * @param x X coord
     * @param y Y coord
     * @param z Z coord
     * @return The client side GUI object (An instance of {@link GUI})
     */
    public Object getLocalGuiContainer(ModContainer mc, EntityPlayer player, int modGuiId, World world, int x, int y, int z)
    {
        IGuiHandler handler = clientGuiHandlers.get(mc);
        return handler.getClientGuiElement(modGuiId, player, world, x, y, z);
    }

    /**
     * Is there a channel with this name on this side?
     * @param channelName The name
     * @param source the side
     * @return if there's a channel
     */
    public boolean hasChannel(String channelName, Side source)
    {
        return channels.get(source).containsKey(channelName);
    }

    /**
     * INTERNAL method for registering a mod as a network capable thing
     * @param fmlModContainer The fml mod container
     * @param clazz a class
     * @param remoteVersionRange the acceptable remote range
     * @param asmHarvestedData internal data
     */
    public void register(ModContainer fmlModContainer, Class<?> clazz, String remoteVersionRange, ASMDataTable asmHarvestedData)
    {
        NetworkModHolder networkModHolder = new NetworkModHolder(fmlModContainer, clazz, remoteVersionRange, asmHarvestedData);
        registry.put(fmlModContainer, networkModHolder);
        networkModHolder.testVanillaAcceptance();
    }

    public boolean isVanillaAccepted(Side from)
    {
        boolean result = true;
        for (Entry<ModContainer, NetworkModHolder> e : registry.entrySet())
        {
            result &= e.getValue().acceptsVanilla(from);
        }
        return result;
    }
    public Map<ModContainer,NetworkModHolder> registry()
    {
        return ImmutableMap.copyOf(registry);
    }

    /**
     * All the valid channel names for a side
     * @param side the side
     * @return the set of channel names
     */
    public Set<String> channelNamesFor(Side side)
    {
        return channels.get(side).keySet();
    }

    /**
     * INTERNAL fire a handshake to all channels
     * @param networkDispatcher The dispatcher firing
     * @param origin which side the dispatcher is on
     */
    public void fireNetworkHandshake(NetworkDispatcher networkDispatcher, Side origin)
    {
        NetworkHandshakeEstablished handshake = new NetworkHandshakeEstablished(networkDispatcher, networkDispatcher.getNetHandler(), origin);
        for (Entry<String, FMLEmbeddedChannel> channel : channels.get(origin).entrySet())
        {
            channel.getValue().attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.DISPATCHER);
            channel.getValue().attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(networkDispatcher);
            channel.getValue().pipeline().fireUserEventTriggered(handshake);
        }
    }
}
