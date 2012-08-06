package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.*;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.TcpConnection;

public class FMLNetworkHandler
{
    private static final int FML_HASH = Hashing.murmur3_32().hashString("FML").asInt();
    private static final int PROTOCOL_VERSION = 0x1;
    private static final FMLNetworkHandler INSTANCE = new FMLNetworkHandler();

    private Map<NetLoginHandler, Integer> loginStates = Maps.newHashMap();
    private Map<ModContainer, NetworkModHandler> networkModHandlers = Maps.newHashMap();

    private Map<Integer, NetworkModHandler> networkIdLookup = Maps.newHashMap();

    public static void handlePacket250Packet(Packet250CustomPayload packet, NetworkManager network, NetHandler handler)
    {
        String target = packet.field_73630_a;

        if (target.startsWith("MC|"))
        {
            handler.handleVanilla250Packet(packet);
            return;
        }
        if (target.equals("FML"))
        {
            instance().handleFMLPacket(packet, network);
        }
    }

    private void handleFMLPacket(Packet250CustomPayload packet, NetworkManager network)
    {
        FMLPacket pkt = FMLPacket.readPacket(packet.field_73629_c);
        pkt.execute(network, this);
    }

    public static void onClientConnectToServer(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        instance().handleClientConnection(netLoginHandler, server, address, userName);
    }

    private void handleClientConnection(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        if (!loginStates.containsKey(netLoginHandler))
        {
            // Vanilla reasons first
            ServerConfigurationManager playerList = server.func_71203_ab();
            String kickReason = playerList.func_72399_a(address, userName);

            if (kickReason!=null)
            {
                netLoginHandler.completeConnection(kickReason);
            }
            // No FML on the client
            netLoginHandler.completeConnection("You don't have FML installed, or your installation is too old");
            return;
        }
        // Are we ready to negotiate with the client?
        if (loginStates.get(netLoginHandler) == 1)
        {
            // Reset the "connection completed" flag so processing can continue
            NetLoginHandler.func_72531_a(netLoginHandler, false);
            // Send the mod list request packet to the client from the server
            netLoginHandler.field_72538_b.func_74429_a(getModListRequestPacket());
            loginStates.put(netLoginHandler, 2);
        }
        // We must be good to go - the ModIdentifiers packet was sent and the continuation signal was indicated
        else if (loginStates.get(netLoginHandler) == 2)
        {
            netLoginHandler.completeConnection(null);
            loginStates.remove(netLoginHandler);
        }
        // We have to abort this connection - there was a negotiation problem (most likely missing mods)
        else
        {
            netLoginHandler.completeConnection("There was a problem during FML negotiation");
            loginStates.remove(netLoginHandler);
        }
    }

    public static void handleLoginPacketOnServer(NetLoginHandler handler, Packet1Login login)
    {
        if (login.field_73561_a == FML_HASH && login.field_73558_e == PROTOCOL_VERSION)
        {
            instance().loginStates.put(handler,1);
        }
    }
    public static FMLNetworkHandler instance()
    {
        return INSTANCE;
    }

    public static Packet1Login getFMLFakeLoginPacket()
    {
        Packet1Login fake = new Packet1Login();
        // Hash FML using a simple function
        fake.field_73561_a = FML_HASH;
        // The FML protocol version
        fake.field_73558_e = PROTOCOL_VERSION;
        return fake;
    }
    public static Packet250CustomPayload getModListRequestPacket()
    {
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "FML";
        pkt.field_73629_c = FMLPacket.makePacket(MOD_LIST_REQUEST);
        pkt.field_73628_b = pkt.field_73629_c.length;
        return pkt;
    }

    public boolean registerNetworkMod(ModContainer container, Class<?> networkModClass, ASMDataTable asmData)
    {
        NetworkModHandler handler = new NetworkModHandler(container, networkModClass, asmData);
        if (handler.isNetworkMod())
        {
            networkModHandlers.put(container, handler);
            networkIdLookup.put(handler.getNetworkId(), handler);
        }

        return handler.isNetworkMod();
    }


    public NetworkModHandler findNetworkModHandler(ModContainer mc)
    {
        return networkModHandlers.get(mc);
    }

    public List<ModContainer> getNetworkModList()
    {
        return null;
    }
}