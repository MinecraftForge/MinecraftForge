package cpw.mods.fml.common.network;

import static cpw.mods.fml.common.network.FMLPacket.Type.MOD_LIST_REQUEST;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.World;
import net.minecraft.src.WorldType;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.network.FMLPacket.Type;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

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
        }
        if (target.equals("FML"))
        {
            instance().handleFMLPacket(packet, network, handler);
        }
        else
        {
            NetworkRegistry.instance().handleCustomPacket(packet, network, handler);
        }
    }

    public static void onConnectionEstablishedToServer(NetHandler clientHandler, NetworkManager manager, Packet1Login login)
    {
        NetworkRegistry.instance().clientLoggedIn(clientHandler, manager, login);
    }

    private void handleFMLPacket(Packet250CustomPayload packet, NetworkManager network, NetHandler netHandler)
    {
        FMLPacket pkt = FMLPacket.readPacket(packet.field_73629_c);
        String userName = "";
        if (netHandler instanceof NetLoginHandler)
        {
            userName = ((NetLoginHandler) netHandler).field_72543_h;
        }
        else
        {
            EntityPlayer pl = netHandler.getPlayer();
            if (pl != null)
            {
                userName = pl.func_70005_c_();
            }
        }

        pkt.execute(network, this, netHandler, userName);
    }

    public static void onConnectionReceivedFromClient(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        instance().handleClientConnection(netLoginHandler, server, address, userName);
    }

    private void handleClientConnection(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        if (!loginStates.containsKey(netLoginHandler))
        {
            if (handleVanillaLoginKick(netLoginHandler, server, address, userName))
            {
                // No FML on the client
                FMLLog.fine("Connection from %s rejected - no FML packet received from client", userName);
                netLoginHandler.completeConnection("You don't have FML installed, or your installation is too old");
                return;
            }

        }
        // Are we ready to negotiate with the client?
        if (loginStates.get(netLoginHandler) == 1)
        {
            // mods can try and kick undesireables here
            String modKick = NetworkRegistry.instance().connectionReceived(netLoginHandler, netLoginHandler.field_72538_b);
            if (modKick != null)
            {
                netLoginHandler.completeConnection(modKick);
                loginStates.remove(netLoginHandler);
                return;
            }
            // The vanilla side wanted to kick
            if (!handleVanillaLoginKick(netLoginHandler, server, address, userName))
            {
                loginStates.remove(netLoginHandler);
                return;
            }

            // Reset the "connection completed" flag so processing can continue
            NetLoginHandler.func_72531_a(netLoginHandler, false);
            // Send the mod list request packet to the client from the server
            netLoginHandler.field_72538_b.func_74429_a(getModListRequestPacket());
            loginStates.put(netLoginHandler, 2);
        }
        // We must be good to go - the ModIdentifiers packet was sent and the
        // continuation signal was indicated
        else if (loginStates.get(netLoginHandler) == 2)
        {
            netLoginHandler.completeConnection(null);
            loginStates.remove(netLoginHandler);
        }
        else if (loginStates.get(netLoginHandler) == 3)
        {
            netLoginHandler.completeConnection("The server requires mods that are missing on your client");
            loginStates.remove(netLoginHandler);
        }
        // We have to abort this connection - there was a negotiation problem
        // (most likely missing mods)
        else
        {
            netLoginHandler.completeConnection("There was a problem during FML negotiation");
            loginStates.remove(netLoginHandler);
        }
    }

    /**
     * @param netLoginHandler
     * @param server
     * @param address
     * @param userName
     * @return if the user can carry on
     */
    private boolean handleVanillaLoginKick(NetLoginHandler netLoginHandler, MinecraftServer server, SocketAddress address, String userName)
    {
        // Vanilla reasons first
        ServerConfigurationManager playerList = server.func_71203_ab();
        String kickReason = playerList.func_72399_a(address, userName);

        if (kickReason != null)
        {
            netLoginHandler.completeConnection(kickReason);
        }
        return kickReason == null;
    }

    public static void handleLoginPacketOnServer(NetLoginHandler handler, Packet1Login login)
    {
        if (login.field_73561_a == FML_HASH && login.field_73558_e == PROTOCOL_VERSION)
        {
            FMLLog.finest("Received valid FML login packet from %s", handler.field_72538_b.func_74430_c());
            instance().loginStates.put(handler, 1);
        }
        else
        {
            FMLLog.fine("Received invalid FML login packet %d, %d from %s", login.field_73561_a, login.field_73558_e,
                    handler.field_72538_b.func_74430_c());
        }
    }

    static void setHandlerState(NetLoginHandler handler, int state)
    {
        instance().loginStates.put(handler, state);
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
        fake.field_73557_d = EnumGameType.NOT_SET;
        fake.field_73559_b = WorldType.field_77139_a[0];
        return fake;
    }

    public Packet250CustomPayload getModListRequestPacket()
    {
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "FML";
        pkt.field_73629_c = FMLPacket.makePacket(MOD_LIST_REQUEST);
        pkt.field_73628_b = pkt.field_73629_c.length;
        return pkt;
    }

    public void registerNetworkMod(NetworkModHandler handler)
    {
        networkModHandlers.put(handler.getContainer(), handler);
        networkIdLookup.put(handler.getNetworkId(), handler);
    }
    public boolean registerNetworkMod(ModContainer container, Class<?> networkModClass, ASMDataTable asmData)
    {
        NetworkModHandler handler = new NetworkModHandler(container, networkModClass, asmData);
        if (handler.isNetworkMod())
        {
            registerNetworkMod(handler);
        }

        return handler.isNetworkMod();
    }

    public NetworkModHandler findNetworkModHandler(Object mc)
    {
        if (mc instanceof ModContainer)
        {
            return networkModHandlers.get(mc);
        }
        else if (mc instanceof Integer)
        {
            return networkIdLookup.get(mc);
        }
        else
        {
            return networkModHandlers.get(FMLCommonHandler.instance().findContainerFor(mc));
        }
    }

    public Set<ModContainer> getNetworkModList()
    {
        return networkModHandlers.keySet();
    }

    public static void handlePlayerLogin(EntityPlayerMP player, NetServerHandler netHandler, NetworkManager manager)
    {
        NetworkRegistry.instance().playerLoggedIn(player, netHandler, manager);
        GameRegistry.onPlayerLogin(player);
    }

    public Map<Integer, NetworkModHandler> getNetworkIdMap()
    {
        return networkIdLookup;
    }

    public void bindNetworkId(String key, Integer value)
    {
        Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
        NetworkModHandler handler = findNetworkModHandler(mods.get(key));
        if (handler != null)
        {
            handler.setNetworkId(value);
            networkIdLookup.put(value, handler);
        }
    }

    public static void onClientConnectionToRemoteServer(NetHandler netClientHandler, String server, int port, NetworkManager networkManager)
    {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, port, networkManager);
    }

    public static void onClientConnectionToIntegratedServer(NetHandler netClientHandler, MinecraftServer server, NetworkManager networkManager)
    {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, networkManager);
    }

    public static void onConnectionClosed(NetworkManager manager)
    {
        NetworkRegistry.instance().connectionClosed(manager);
    }


    public static void sendPacket(Player player, Packet packet)
    {
    }

    public static void openGui(EntityPlayer player, Object mod, int modGuiId, World world, int x, int y, int z)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null)
        {
            NetworkModHandler nmh = instance().findNetworkModHandler(mod);
            if (nmh != null)
            {
                mc = nmh.getContainer();
            }
            else
            {
                FMLLog.warning("A mod tried to open a gui on the server without being a NetworkMod");
                return;
            }
        }
        if (player instanceof EntityPlayerMP)
        {
            NetworkRegistry.instance().openRemoteGui(mc, (EntityPlayerMP) player, modGuiId, world, x, y, z);
        }
        else
        {
            NetworkRegistry.instance().openLocalGui(mc, player, modGuiId, world, x, y, z);
        }
    }

    public static Packet getEntitySpawningPacket(Entity entity)
    {
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
        if (er == null)
        {
            return null;
        }
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "FML";
        pkt.field_73629_c = FMLPacket.makePacket(Type.ENTITYSPAWN, er, entity, instance().findNetworkModHandler(er.getContainer()));
        pkt.field_73628_b = pkt.field_73629_c.length;
        return pkt;
    }

    public static void makeEntitySpawnAdjustment(int entityId, EntityPlayerMP player, int serverX, int serverY, int serverZ)
    {
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "FML";
        pkt.field_73629_c = FMLPacket.makePacket(Type.ENTITYSPAWNADJUSTMENT, entityId, serverX, serverY, serverZ);
        pkt.field_73628_b = pkt.field_73629_c.length;

        player.field_71135_a.func_72567_b(pkt);
    }
}