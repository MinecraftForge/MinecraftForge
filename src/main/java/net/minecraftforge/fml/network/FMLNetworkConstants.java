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

package net.minecraftforge.fml.network;

import io.netty.util.AttributeKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
/**
 * Constants related to networking
 */
public class FMLNetworkConstants
{
    public static final String FMLNETMARKER = "FML";
    public static final int FMLNETVERSION = 2;
    public static final String NETVERSION = FMLNETMARKER + FMLNETVERSION;
    public static final String NOVERSION = "NONE";

    static final Marker NETWORK = MarkerManager.getMarker("FMLNETWORK");
    static final AttributeKey<String> FML_NETVERSION = AttributeKey.valueOf("fml:netversion");
    static final AttributeKey<FMLHandshakeHandler> FML_HANDSHAKE_HANDLER = AttributeKey.valueOf("fml:handshake");
    static final AttributeKey<FMLMCRegisterPacketHandler.ChannelList> FML_MC_REGISTRY = AttributeKey.valueOf("minecraft:netregistry");
    static final AttributeKey<FMLConnectionData> FML_CONNECTION_DATA = AttributeKey.valueOf("fml:conndata");
    static final ResourceLocation FML_HANDSHAKE_RESOURCE = new ResourceLocation("fml:handshake");
    static final ResourceLocation FML_PLAY_RESOURCE = new ResourceLocation("fml:play");
    static final ResourceLocation MC_REGISTER_RESOURCE = new ResourceLocation("minecraft:register");
    static final ResourceLocation MC_UNREGISTER_RESOURCE = new ResourceLocation("minecraft:unregister");
    static final SimpleChannel handshakeChannel = NetworkInitialization.getHandshakeChannel();
    static final SimpleChannel playChannel = NetworkInitialization.getPlayChannel();
    static final List<EventNetworkChannel> mcRegChannels = NetworkInitialization.buildMCRegistrationChannels();
    /**
     * Return this value in your {@link net.minecraftforge.fml.ExtensionPoint#DISPLAYTEST} function to be ignored.
     */
    public static final String IGNORESERVERONLY = "OHNOES\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31";

    public static String init() {
        return FMLNetworkConstants.NETVERSION;
    }
}
