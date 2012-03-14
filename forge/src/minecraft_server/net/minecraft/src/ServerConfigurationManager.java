package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.DimensionManager;

public class ServerConfigurationManager
{
    /** Reference to the logger. */
    public static Logger logger = Logger.getLogger("Minecraft");

    /** A list of player entities that exist on this server. */
    public List playerEntities = new ArrayList();

    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /** Reference to the PlayerManager object. */
    //private PlayerManager[] playerManagerObj = new PlayerManager[3];

    /** the maximum amount of players that can be connected */
    private int maxPlayers;

    /** the set of all banned players names */
    private Set bannedPlayers = new HashSet();

    /** A set containing the banned IPs. */
    private Set bannedIPs = new HashSet();

    /** A set containing the OPs. */
    private Set ops = new HashSet();

    /** the set of all white listed IP addresses */
    private Set whiteListedIPs = new HashSet();

    /** The file that contains the banned players. */
    private File bannedPlayersFile;

    /** the file which contains the list of banned IPs */
    private File ipBanFile;

    /** the file which contains the list of ops */
    private File opFile;

    /** File containing list of whitelisted players */
    private File whitelistPlayersFile;

    /** Reference to the PlayerNBTManager object. */
    private IPlayerFileData playerNBTManagerObj;

    /**
     * Server setting to only allow OP's and whitelisted players to join the server
     */
    private boolean whiteListEnforced;
    private int field_35482_p = 0;

    public ServerConfigurationManager(MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
        this.bannedPlayersFile = par1MinecraftServer.getFile("banned-players.txt");
        this.ipBanFile = par1MinecraftServer.getFile("banned-ips.txt");
        this.opFile = par1MinecraftServer.getFile("ops.txt");
        this.whitelistPlayersFile = par1MinecraftServer.getFile("white-list.txt");
        int var2 = par1MinecraftServer.propertyManagerObj.getIntProperty("view-distance", 10);
        /*
        this.playerManagerObj[0] = new PlayerManager(par1MinecraftServer, 0, var2);
        this.playerManagerObj[1] = new PlayerManager(par1MinecraftServer, -1, var2);
        this.playerManagerObj[2] = new PlayerManager(par1MinecraftServer, 1, var2);
        */
        this.maxPlayers = par1MinecraftServer.propertyManagerObj.getIntProperty("max-players", 20);
        this.whiteListEnforced = par1MinecraftServer.propertyManagerObj.getBooleanProperty("white-list", false);
        this.readBannedPlayers();
        this.loadBannedList();
        this.loadOps();
        this.loadWhiteList();
        this.writeBannedPlayers();
        this.saveBannedList();
        this.saveOps();
        this.saveWhiteList();
    }

    /**
     * Sets the NBT manager to the one for the worldserver given
     */
    public void setPlayerManager(WorldServer[] par1ArrayOfWorldServer)
    {
        this.playerNBTManagerObj = par1ArrayOfWorldServer[0].getSaveHandler().getPlayerNBTManager();
    }

    /**
     * called when a player is teleported to a new dimension in order to clean up old dim refs, send them new dim
     * chunks, and make sure their new location chunk is loaded and initialized
     */
    public void joinNewPlayerManager(EntityPlayerMP par1EntityPlayerMP)
    {
        for (World world : DimensionManager.getWorlds())
        {
            ((WorldServer)world).playerManager.removePlayer(par1EntityPlayerMP);
        }
        this.getPlayerManager(par1EntityPlayerMP.dimension).addPlayer(par1EntityPlayerMP);
        WorldServer var2 = this.mcServer.getWorldManager(par1EntityPlayerMP.dimension);
        var2.chunkProviderServer.loadChunk((int)par1EntityPlayerMP.posX >> 4, (int)par1EntityPlayerMP.posZ >> 4);
    }

    public int getMaxTrackingDistance()
    {
        return getPlayerManager(0).getMaxTrackingDistance();
    }

    /**
     * returns the player manager object for the specified dimension
     */
    private PlayerManager getPlayerManager(int par1)
    {
        WorldServer world = (WorldServer)DimensionManager.getWorld(par1);
        return (world == null ? null : world.playerManager);
    }

    /**
     * called during player login. reads the player information from disk.
     */
    public void readPlayerDataFromFile(EntityPlayerMP par1EntityPlayerMP)
    {
        this.playerNBTManagerObj.readPlayerData(par1EntityPlayerMP);
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP)
    {
        this.sendPacketToAllPlayers(new Packet201PlayerInfo(par1EntityPlayerMP.username, true, 1000));
        this.playerEntities.add(par1EntityPlayerMP);
        WorldServer var2 = this.mcServer.getWorldManager(par1EntityPlayerMP.dimension);
        var2.chunkProviderServer.loadChunk((int)par1EntityPlayerMP.posX >> 4, (int)par1EntityPlayerMP.posZ >> 4);

        while (var2.getCollidingBoundingBoxes(par1EntityPlayerMP, par1EntityPlayerMP.boundingBox).size() != 0)
        {
            par1EntityPlayerMP.setPosition(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY + 1.0D, par1EntityPlayerMP.posZ);
        }

        var2.spawnEntityInWorld(par1EntityPlayerMP);
        this.getPlayerManager(par1EntityPlayerMP.dimension).addPlayer(par1EntityPlayerMP);

        for (int var3 = 0; var3 < this.playerEntities.size(); ++var3)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)this.playerEntities.get(var3);
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet201PlayerInfo(var4.username, true, var4.ping));
        }
    }

    /**
     * using player's dimension, update their movement when in a vehicle (e.g. cart, boat)
     */
    public void serverUpdateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        this.getPlayerManager(par1EntityPlayerMP.dimension).updateMountedMovingPlayer(par1EntityPlayerMP);
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP)
    {
        this.playerNBTManagerObj.writePlayerData(par1EntityPlayerMP);
        this.mcServer.getWorldManager(par1EntityPlayerMP.dimension).setEntityDead(par1EntityPlayerMP);
        this.playerEntities.remove(par1EntityPlayerMP);
        this.getPlayerManager(par1EntityPlayerMP.dimension).removePlayer(par1EntityPlayerMP);
        this.sendPacketToAllPlayers(new Packet201PlayerInfo(par1EntityPlayerMP.username, false, 9999));
    }

    /**
     * Called when a player tries to login. Checks whether they are banned/server is full etc.
     */
    public EntityPlayerMP login(NetLoginHandler par1NetLoginHandler, String par2Str)
    {
        if (this.bannedPlayers.contains(par2Str.trim().toLowerCase()))
        {
            par1NetLoginHandler.kickUser("You are banned from this server!");
            return null;
        }
        else if (!this.isAllowedToLogin(par2Str))
        {
            par1NetLoginHandler.kickUser("You are not white-listed on this server!");
            return null;
        }
        else
        {
            String var3 = par1NetLoginHandler.netManager.getRemoteAddress().toString();
            var3 = var3.substring(var3.indexOf("/") + 1);
            var3 = var3.substring(0, var3.indexOf(":"));

            if (this.bannedIPs.contains(var3))
            {
                par1NetLoginHandler.kickUser("Your IP address is banned from this server!");
                return null;
            }
            else if (this.playerEntities.size() >= this.maxPlayers)
            {
                par1NetLoginHandler.kickUser("The server is full!");
                return null;
            }
            else
            {
                for (int var4 = 0; var4 < this.playerEntities.size(); ++var4)
                {
                    EntityPlayerMP var5 = (EntityPlayerMP)this.playerEntities.get(var4);

                    if (var5.username.equalsIgnoreCase(par2Str))
                    {
                        var5.playerNetServerHandler.kickPlayer("You logged in from another location");
                    }
                }

                return new EntityPlayerMP(this.mcServer, this.mcServer.getWorldManager(0), par2Str, new ItemInWorldManager(this.mcServer.getWorldManager(0)));
            }
        }
    }

    /**
     * Called on respawn
     */
    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3)
    {
        this.mcServer.getEntityTracker(par1EntityPlayerMP.dimension).removeTrackedPlayerSymmetric(par1EntityPlayerMP);
        this.mcServer.getEntityTracker(par1EntityPlayerMP.dimension).untrackEntity(par1EntityPlayerMP);
        this.getPlayerManager(par1EntityPlayerMP.dimension).removePlayer(par1EntityPlayerMP);
        this.playerEntities.remove(par1EntityPlayerMP);
        this.mcServer.getWorldManager(par1EntityPlayerMP.dimension).removePlayer(par1EntityPlayerMP);
        ChunkCoordinates var4 = par1EntityPlayerMP.getSpawnChunk();
        par1EntityPlayerMP.dimension = par2;
        EntityPlayerMP var5 = new EntityPlayerMP(this.mcServer, this.mcServer.getWorldManager(par1EntityPlayerMP.dimension), par1EntityPlayerMP.username, new ItemInWorldManager(this.mcServer.getWorldManager(par1EntityPlayerMP.dimension)));

        if (par3)
        {
            var5.copyPlayer(par1EntityPlayerMP);
        }

        var5.entityId = par1EntityPlayerMP.entityId;
        var5.playerNetServerHandler = par1EntityPlayerMP.playerNetServerHandler;
        WorldServer var6 = this.mcServer.getWorldManager(par1EntityPlayerMP.dimension);
        var5.itemInWorldManager.toggleGameType(par1EntityPlayerMP.itemInWorldManager.getGameType());
        var5.itemInWorldManager.func_35695_b(var6.getWorldInfo().getGameType());

        if (var4 != null)
        {
            ChunkCoordinates var7 = EntityPlayer.verifyRespawnCoordinates(this.mcServer.getWorldManager(par1EntityPlayerMP.dimension), var4);

            if (var7 != null)
            {
                var5.setLocationAndAngles((double)((float)var7.posX + 0.5F), (double)((float)var7.posY + 0.1F), (double)((float)var7.posZ + 0.5F), 0.0F, 0.0F);
                var5.setSpawnChunk(var4);
            }
            else
            {
                var5.playerNetServerHandler.sendPacket(new Packet70Bed(0, 0));
            }
        }

        var6.chunkProviderServer.loadChunk((int)var5.posX >> 4, (int)var5.posZ >> 4);

        while (var6.getCollidingBoundingBoxes(var5, var5.boundingBox).size() != 0)
        {
            var5.setPosition(var5.posX, var5.posY + 1.0D, var5.posZ);
        }

        var5.playerNetServerHandler.sendPacket(new Packet9Respawn(var5.dimension, (byte)var5.worldObj.difficultySetting, var5.worldObj.getWorldInfo().getTerrainType(), var5.worldObj.getWorldHeight(), var5.itemInWorldManager.getGameType()));
        var5.playerNetServerHandler.teleportTo(var5.posX, var5.posY, var5.posZ, var5.rotationYaw, var5.rotationPitch);
        this.func_28170_a(var5, var6);
        this.getPlayerManager(var5.dimension).addPlayer(var5);
        var6.spawnEntityInWorld(var5);
        this.playerEntities.add(var5);
        var5.func_20057_k();
        var5.func_22068_s();
        return var5;
    }

    /**
     * moves provided player from overworld to nether or vice versa
     */
    public void sendPlayerToOtherDimension(EntityPlayerMP par1EntityPlayerMP, int par2)
    {
        sendPlayerToOtherDimension(par1EntityPlayerMP, par2, new Teleporter());
    }
    public void sendPlayerToOtherDimension(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter)
    {
        int var3 = par1EntityPlayerMP.dimension;
        WorldServer var4 = this.mcServer.getWorldManager(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.dimension = par2;
        WorldServer var5 = this.mcServer.getWorldManager(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet9Respawn(par1EntityPlayerMP.dimension, (byte)par1EntityPlayerMP.worldObj.difficultySetting, var5.getWorldInfo().getTerrainType(), var5.getWorldHeight(), par1EntityPlayerMP.itemInWorldManager.getGameType()));
        var4.removePlayer(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;

        WorldProvider pOld = DimensionManager.getProvider(var3);
        WorldProvider pNew = DimensionManager.getProvider(par2);
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double var6 = par1EntityPlayerMP.posX * moveFactor;
        double var8 = par1EntityPlayerMP.posZ * moveFactor;

        if (par1EntityPlayerMP.dimension == 1)
        {
            ChunkCoordinates var12 = var5.getEntrancePortalLocation();
            var6 = (double)var12.posX;
            par1EntityPlayerMP.posY = (double)var12.posY;
            var8 = (double)var12.posZ;
            par1EntityPlayerMP.setLocationAndAngles(var6, par1EntityPlayerMP.posY, var8, 90.0F, 0.0F);

            if (par1EntityPlayerMP.isEntityAlive())
            {
                var4.updateEntityWithOptionalForce(par1EntityPlayerMP, false);
            }
        }

        if (var3 != 1 && par1EntityPlayerMP.isEntityAlive())
        {
            var5.spawnEntityInWorld(par1EntityPlayerMP);
            par1EntityPlayerMP.setLocationAndAngles(var6, par1EntityPlayerMP.posY, var8, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
            var5.updateEntityWithOptionalForce(par1EntityPlayerMP, false);
            var5.chunkProviderServer.chunkLoadOverride = true;
            teleporter.placeInPortal(var5, par1EntityPlayerMP);
            var5.chunkProviderServer.chunkLoadOverride = false;
        }

        this.joinNewPlayerManager(par1EntityPlayerMP);
        par1EntityPlayerMP.playerNetServerHandler.teleportTo(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.setWorld(var5);
        par1EntityPlayerMP.itemInWorldManager.setWorld(var5);
        this.func_28170_a(par1EntityPlayerMP, var5);
        this.func_30008_g(par1EntityPlayerMP);
    }

    /**
     * self explanitory
     */
    public void onTick()
    {
        if (++this.field_35482_p > 200)
        {
            this.field_35482_p = 0;
        }

        if (this.field_35482_p < this.playerEntities.size())
        {
            EntityPlayerMP var1 = (EntityPlayerMP)this.playerEntities.get(this.field_35482_p);
            this.sendPacketToAllPlayers(new Packet201PlayerInfo(var1.username, true, var1.ping));
        }

        for (World world : DimensionManager.getWorlds())
        {
            ((WorldServer)world).playerManager.updatePlayerInstances();
        }
    }

    public void markBlockNeedsUpdate(int par1, int par2, int par3, int par4)
    {
        this.getPlayerManager(par4).markBlockNeedsUpdate(par1, par2, par3);
    }

    /**
     * sends a packet to all players
     */
    public void sendPacketToAllPlayers(Packet par1Packet)
    {
        for (int var2 = 0; var2 < this.playerEntities.size(); ++var2)
        {
            EntityPlayerMP var3 = (EntityPlayerMP)this.playerEntities.get(var2);
            var3.playerNetServerHandler.sendPacket(par1Packet);
        }
    }

    /**
     * Sends a packet to all players in the specified Dimension
     */
    public void sendPacketToAllPlayersInDimension(Packet par1Packet, int par2)
    {
        for (int var3 = 0; var3 < this.playerEntities.size(); ++var3)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)this.playerEntities.get(var3);

            if (var4.dimension == par2)
            {
                var4.playerNetServerHandler.sendPacket(par1Packet);
            }
        }
    }

    /**
     * returns a string containing a comma-seperated list of player names
     */
    public String getPlayerList()
    {
        String var1 = "";

        for (int var2 = 0; var2 < this.playerEntities.size(); ++var2)
        {
            if (var2 > 0)
            {
                var1 = var1 + ", ";
            }

            var1 = var1 + ((EntityPlayerMP)this.playerEntities.get(var2)).username;
        }

        return var1;
    }

    /**
     * Returns a list of usernames of all connected players
     */
    public String[] getPlayerNamesAsList()
    {
        String[] var1 = new String[this.playerEntities.size()];

        for (int var2 = 0; var2 < this.playerEntities.size(); ++var2)
        {
            var1[var2] = ((EntityPlayerMP)this.playerEntities.get(var2)).username;
        }

        return var1;
    }

    /**
     * add this player to the banned player list and save the ban list
     */
    public void banPlayer(String par1Str)
    {
        this.bannedPlayers.add(par1Str.toLowerCase());
        this.writeBannedPlayers();
    }

    /**
     * remove this player from the banned player list and save the ban list
     */
    public void pardonPlayer(String par1Str)
    {
        this.bannedPlayers.remove(par1Str.toLowerCase());
        this.writeBannedPlayers();
    }

    /**
     * Reads the banned players file from disk.
     */
    private void readBannedPlayers()
    {
        try
        {
            this.bannedPlayers.clear();
            BufferedReader var1 = new BufferedReader(new FileReader(this.bannedPlayersFile));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                this.bannedPlayers.add(var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            logger.warning("Failed to load ban list: " + var3);
        }
    }

    /**
     * Writes the banned players file to disk.
     */
    private void writeBannedPlayers()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(this.bannedPlayersFile, false));
            Iterator var2 = this.bannedPlayers.iterator();

            while (var2.hasNext())
            {
                String var3 = (String)var2.next();
                var1.println(var3);
            }

            var1.close();
        }
        catch (Exception var4)
        {
            logger.warning("Failed to save ban list: " + var4);
        }
    }

    /**
     * Returns a list of banned player names
     */
    public Set getBannedPlayersList()
    {
        return this.bannedPlayers;
    }

    /**
     * Returns the list of banned IP addresses
     */
    public Set getBannedIPsList()
    {
        return this.bannedIPs;
    }

    /**
     * add the ip to the banned ip list and save ban list
     */
    public void banIP(String par1Str)
    {
        this.bannedIPs.add(par1Str.toLowerCase());
        this.saveBannedList();
    }

    /**
     * removes the ip from the banned ip list and save ban list
     */
    public void pardonIP(String par1Str)
    {
        this.bannedIPs.remove(par1Str.toLowerCase());
        this.saveBannedList();
    }

    /**
     * loads the list of banned players
     */
    private void loadBannedList()
    {
        try
        {
            this.bannedIPs.clear();
            BufferedReader var1 = new BufferedReader(new FileReader(this.ipBanFile));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                this.bannedIPs.add(var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            logger.warning("Failed to load ip ban list: " + var3);
        }
    }

    /**
     * saves the list of banned players
     */
    private void saveBannedList()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(this.ipBanFile, false));
            Iterator var2 = this.bannedIPs.iterator();

            while (var2.hasNext())
            {
                String var3 = (String)var2.next();
                var1.println(var3);
            }

            var1.close();
        }
        catch (Exception var4)
        {
            logger.warning("Failed to save ip ban list: " + var4);
        }
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String par1Str)
    {
        this.ops.add(par1Str.toLowerCase());
        this.saveOps();
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String par1Str)
    {
        this.ops.remove(par1Str.toLowerCase());
        this.saveOps();
    }

    /**
     * loads the ops from the ops file
     */
    private void loadOps()
    {
        try
        {
            this.ops.clear();
            BufferedReader var1 = new BufferedReader(new FileReader(this.opFile));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                this.ops.add(var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            logger.warning("Failed to load operators list: " + var3);
        }
    }

    /**
     * saves the ops to the ops file
     */
    private void saveOps()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(this.opFile, false));
            Iterator var2 = this.ops.iterator();

            while (var2.hasNext())
            {
                String var3 = (String)var2.next();
                var1.println(var3);
            }

            var1.close();
        }
        catch (Exception var4)
        {
            logger.warning("Failed to save operators list: " + var4);
        }
    }

    /**
     * Loads the white list file
     */
    private void loadWhiteList()
    {
        try
        {
            this.whiteListedIPs.clear();
            BufferedReader var1 = new BufferedReader(new FileReader(this.whitelistPlayersFile));
            String var2 = "";

            while ((var2 = var1.readLine()) != null)
            {
                this.whiteListedIPs.add(var2.trim().toLowerCase());
            }

            var1.close();
        }
        catch (Exception var3)
        {
            logger.warning("Failed to load white-list: " + var3);
        }
    }

    /**
     * Saves the white list file
     */
    private void saveWhiteList()
    {
        try
        {
            PrintWriter var1 = new PrintWriter(new FileWriter(this.whitelistPlayersFile, false));
            Iterator var2 = this.whiteListedIPs.iterator();

            while (var2.hasNext())
            {
                String var3 = (String)var2.next();
                var1.println(var3);
            }

            var1.close();
        }
        catch (Exception var4)
        {
            logger.warning("Failed to save white-list: " + var4);
        }
    }

    /**
     * Determine if the player is allowed to connect based on current server settings
     */
    public boolean isAllowedToLogin(String par1Str)
    {
        par1Str = par1Str.trim().toLowerCase();
        return !this.whiteListEnforced || this.ops.contains(par1Str) || this.whiteListedIPs.contains(par1Str);
    }

    /**
     * Returns true if the player is an OP, false otherwise.
     */
    public boolean isOp(String par1Str)
    {
        return this.ops.contains(par1Str.trim().toLowerCase());
    }

    /**
     * gets the player entity for the player with the name specified
     */
    public EntityPlayerMP getPlayerEntity(String par1Str)
    {
        for (int var2 = 0; var2 < this.playerEntities.size(); ++var2)
        {
            EntityPlayerMP var3 = (EntityPlayerMP)this.playerEntities.get(var2);

            if (var3.username.equalsIgnoreCase(par1Str))
            {
                return var3;
            }
        }

        return null;
    }

    /**
     * sends a chat message to the player with the name specified (not necessarily a whisper)
     */
    public void sendChatMessageToPlayer(String par1Str, String par2Str)
    {
        EntityPlayerMP var3 = this.getPlayerEntity(par1Str);

        if (var3 != null)
        {
            var3.playerNetServerHandler.sendPacket(new Packet3Chat(par2Str));
        }
    }

    /**
     * sends a packet to players within d3 of point (x,y,z)
     */
    public void sendPacketToPlayersAroundPoint(double par1, double par3, double par5, double par7, int par9, Packet par10Packet)
    {
        this.func_28171_a((EntityPlayer)null, par1, par3, par5, par7, par9, par10Packet);
    }

    public void func_28171_a(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, double par8, int par10, Packet par11Packet)
    {
        for (int var12 = 0; var12 < this.playerEntities.size(); ++var12)
        {
            EntityPlayerMP var13 = (EntityPlayerMP)this.playerEntities.get(var12);

            if (var13 != par1EntityPlayer && var13.dimension == par10)
            {
                double var14 = par2 - var13.posX;
                double var16 = par4 - var13.posY;
                double var18 = par6 - var13.posZ;

                if (var14 * var14 + var16 * var16 + var18 * var18 < par8 * par8)
                {
                    var13.playerNetServerHandler.sendPacket(par11Packet);
                }
            }
        }
    }

    /**
     * sends a chat message to all ops currently connected
     */
    public void sendChatMessageToAllOps(String par1Str)
    {
        Packet3Chat var2 = new Packet3Chat(par1Str);

        for (int var3 = 0; var3 < this.playerEntities.size(); ++var3)
        {
            EntityPlayerMP var4 = (EntityPlayerMP)this.playerEntities.get(var3);

            if (this.isOp(var4.username))
            {
                var4.playerNetServerHandler.sendPacket(var2);
            }
        }
    }

    /**
     * sends a packet to the player with the name specified
     */
    public boolean sendPacketToPlayer(String par1Str, Packet par2Packet)
    {
        EntityPlayerMP var3 = this.getPlayerEntity(par1Str);

        if (var3 != null)
        {
            var3.playerNetServerHandler.sendPacket(par2Packet);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Saves all of the player's states
     */
    public void savePlayerStates()
    {
        for (int var1 = 0; var1 < this.playerEntities.size(); ++var1)
        {
            this.playerNBTManagerObj.writePlayerData((EntityPlayer)this.playerEntities.get(var1));
        }
    }

    /**
     * sends a tilentity to the player name specified
     */
    public void sentTileEntityToPlayer(int par1, int par2, int par3, TileEntity par4TileEntity) {}

    /**
     * add the specified player to the white list
     */
    public void addToWhiteList(String par1Str)
    {
        this.whiteListedIPs.add(par1Str);
        this.saveWhiteList();
    }

    /**
     * remove the specified player from the whitelist
     */
    public void removeFromWhiteList(String par1Str)
    {
        this.whiteListedIPs.remove(par1Str);
        this.saveWhiteList();
    }

    /**
     * returns the set of whitelisted ip addresses
     */
    public Set getWhiteListedIPs()
    {
        return this.whiteListedIPs;
    }

    /**
     * reloads the whitelist
     */
    public void reloadWhiteList()
    {
        this.loadWhiteList();
    }

    public void func_28170_a(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet4UpdateTime(par2WorldServer.getWorldTime()));

        if (par2WorldServer.isRaining())
        {
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet70Bed(1, 0));
        }
    }

    public void func_30008_g(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.func_28017_a(par1EntityPlayerMP.inventorySlots);
        par1EntityPlayerMP.func_30001_B();
    }

    /**
     * Returns the number of players on the server
     */
    public int playersOnline()
    {
        return this.playerEntities.size();
    }

    /**
     * Returns maximum amount of players that can join the server
     */
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }
}
