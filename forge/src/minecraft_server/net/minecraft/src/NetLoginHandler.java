package net.minecraft.src;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.*;
import net.minecraft.src.forge.packets.*;
import java.io.UnsupportedEncodingException;

public class NetLoginHandler extends NetHandler
{
    /** The Minecraft logger. */
    public static Logger logger = Logger.getLogger("Minecraft");

    /** The Random object used to generate serverId hex strings. */
    private static Random rand = new Random();

    /** The underlying network manager for this login handler. */
    public NetworkManager netManager;

    /**
     * Returns if the login handler is finished and can be removed. It is set to true on either error or successful
     * login.
     */
    public boolean finishedProcessing = false;

    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /** While waiting to login, if this field ++'s to 600 it will kick you. */
    private int loginTimer = 0;

    /** The username for this login. */
    private String username = null;

    /** holds the login packet of the current getting handled login packet */
    private Packet1Login packet1login = null;

    /**
     * The hex string that corresponds to the random number generated as a server ID. Used in online mode.
     */
    private String serverId = "";

    public NetLoginHandler(MinecraftServer par1MinecraftServer, Socket par2Socket, String par3Str) throws IOException
    {
        this.mcServer = par1MinecraftServer;
        this.netManager = new NetworkManager(par2Socket, par3Str, this);
        this.netManager.chunkDataSendCounter = 0;
        ForgeHooks.onConnect(netManager);
    }

    /**
     * Logs the user in if a login packet is found, otherwise keeps processing network packets unless the timeout has
     * occurred.
     */
    public void tryLogin()
    {
        if (this.packet1login != null)
        {
            this.doLogin(this.packet1login);
            this.packet1login = null;
        }

        if (this.loginTimer++ == 600)
        {
            this.kickUser("Took too long to log in");
        }
        else
        {
            this.netManager.processReadPackets();
        }
    }

    /**
     * Disconnects the user with the given reason.
     */
    public void kickUser(String par1Str)
    {
        try
        {
            logger.info("Disconnecting " + this.getUserAndIPString() + ": " + par1Str);
            this.netManager.addToSendQueue(new Packet255KickDisconnect(par1Str));
            this.netManager.serverShutdown();
            this.finishedProcessing = true;
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }
    }

    public void handleHandshake(Packet2Handshake par1Packet2Handshake)
    {
        if (this.mcServer.onlineMode)
        {
            this.serverId = Long.toString(rand.nextLong(), 16);
            this.netManager.addToSendQueue(new Packet2Handshake(this.serverId));
        }
        else
        {
            this.netManager.addToSendQueue(new Packet2Handshake("-"));
        }
    }

    public void handleLogin(Packet1Login par1Packet1Login)
    {
        this.username = par1Packet1Login.username;

        if (par1Packet1Login.protocolVersion != 28)
        {
            if (par1Packet1Login.protocolVersion > 28)
            {
                this.kickUser("Outdated server!");
            }
            else
            {
                this.kickUser("Outdated client!");
            }
        }
        else
        {
            if (!this.mcServer.onlineMode)
            {
                this.doLogin(par1Packet1Login);
            }
            else
            {
                (new ThreadLoginVerifier(this, par1Packet1Login)).start();
            }
        }
    }

    /**
     * Processes the login packet and sends response packets to the user.
     */
    public void doLogin(Packet1Login par1Packet1Login)
    {
        EntityPlayerMP var2 = this.mcServer.configManager.login(this, par1Packet1Login.username);

        if (var2 != null)
        {
            this.mcServer.configManager.readPlayerDataFromFile(var2);
            var2.setWorld(this.mcServer.getWorldManager(var2.dimension));
            var2.itemInWorldManager.setWorld((WorldServer)var2.worldObj);
            logger.info(this.getUserAndIPString() + " logged in with entity id " + var2.entityId + " at (" + var2.posX + ", " + var2.posY + ", " + var2.posZ + ")");
            WorldServer var3 = this.mcServer.getWorldManager(var2.dimension);
            ChunkCoordinates var4 = var3.getSpawnPoint();
            var2.itemInWorldManager.func_35695_b(var3.getWorldInfo().getGameType());
            NetServerHandler var5 = new NetServerHandler(this.mcServer, this.netManager, var2);
            var5.sendPacket(new Packet1Login("", var2.entityId, var3.getWorldInfo().getTerrainType(), var2.itemInWorldManager.getGameType(), var3.worldProvider.worldType, (byte)var3.difficultySetting, (byte)var3.getWorldHeight(), (byte)this.mcServer.configManager.getMaxPlayers()));
            var5.sendPacket(new Packet6SpawnPosition(var4.posX, var4.posY, var4.posZ));
            this.mcServer.configManager.func_28170_a(var2, var3);
            this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + var2.username + " joined the game."));
            this.mcServer.configManager.playerLoggedIn(var2);
            var5.teleportTo(var2.posX, var2.posY, var2.posZ, var2.rotationYaw, var2.rotationPitch);
            this.mcServer.networkServer.addPlayer(var5);
            var5.sendPacket(new Packet4UpdateTime(var3.getWorldTime()));
            Iterator var7 = var2.getActivePotionEffects().iterator();

            while (var7.hasNext())
            {
                PotionEffect var6 = (PotionEffect)var7.next();
                var5.sendPacket(new Packet41EntityEffect(var2.entityId, var6));
            }

            var2.func_20057_k();
            if (par1Packet1Login.serverMode == ForgePacket.FORGE_ID)
            {
                //Pretty hackish place to put it, but it needs to go somewhere
                ForgeHooksServer.init();
                //pkt.mapSeed = ForgeHooks.buildVersion;
                ForgeHooks.onLogin(netManager, par1Packet1Login);                

                String[] channels = MessageManager.getInstance().getRegisteredChannels(netManager);
                StringBuilder tmp = new StringBuilder();
                tmp.append("Forge");
                for(String channel : channels)
                {
                    tmp.append("\0");
                    tmp.append(channel);
                }
                Packet250CustomPayload pkt = new Packet250CustomPayload(); 
                pkt.channel = "REGISTER";
                try {
                    pkt.data = tmp.toString().getBytes("UTF8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                pkt.length = pkt.data.length;
                var5.sendPacket(pkt);
                ForgeHooksServer.sendModListRequest(netManager);                
                ModLoaderMp.handleAllLogins(var2);
            }
            else
            {
                var5.kickPlayer("This server requires you to have Minecraft Forge installed.");
            }
        }

        this.finishedProcessing = true;
    }

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        logger.info(this.getUserAndIPString() + " lost connection");
        this.finishedProcessing = true;
    }

    /**
     * Handle a server ping packet.
     */
    public void handleServerPing(Packet254ServerPing par1Packet254ServerPing)
    {
        try
        {
            String var2 = this.mcServer.motd + "\u00a7" + this.mcServer.configManager.playersOnline() + "\u00a7" + this.mcServer.configManager.getMaxPlayers();
            this.netManager.addToSendQueue(new Packet255KickDisconnect(var2));
            this.netManager.serverShutdown();
            this.mcServer.networkServer.func_35505_a(this.netManager.getSocket());
            this.finishedProcessing = true;
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }
    }

    public void registerPacket(Packet par1Packet)
    {
        this.kickUser("Protocol error");
    }

    /**
     * Returns the user name (if any) and the remote address as a string.
     */
    public String getUserAndIPString()
    {
        return this.username != null ? this.username + " [" + this.netManager.getRemoteAddress().toString() + "]" : this.netManager.getRemoteAddress().toString();
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return true;
    }

    /**
     * Returns the server Id randomly generated by this login handler.
     */
    static String getServerId(NetLoginHandler par0NetLoginHandler)
    {
        return par0NetLoginHandler.serverId;
    }

    /**
     * Sets and returns the login packet provided.
     */
    static Packet1Login setLoginPacket(NetLoginHandler par0NetLoginHandler, Packet1Login par1Packet1Login)
    {
        return par0NetLoginHandler.packet1login = par1Packet1Login;
    }
}
