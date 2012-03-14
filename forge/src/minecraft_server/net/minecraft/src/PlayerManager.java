package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class PlayerManager
{
    /** players in the current instance */
    public List players = new ArrayList();

    /** the hash of all playerInstances created */
    private LongHashMap playerInstances = new LongHashMap();

    /** the playerInstances(chunks) that need to be updated */
    private List playerInstancesToUpdate = new ArrayList();

    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /**
     * Holds the player dimension object. 0 is the surface, -1 is the Nether.
     */
    private int playerDimension;

    /**
     * Number of chunks the server sends to the client. Valid 3<=x<=15. In server.properties.
     */
    private int playerViewRadius;

    /** x, z direction vectors: east, south, west, north */
    private final int[][] xzDirectionsConst = new int[][] {{1, 0}, {0, 1}, { -1, 0}, {0, -1}};

    public PlayerManager(MinecraftServer par1MinecraftServer, int par2, int par3)
    {
        if (par3 > 15)
        {
            throw new IllegalArgumentException("Too big view radius!");
        }
        else if (par3 < 3)
        {
            throw new IllegalArgumentException("Too small view radius!");
        }
        else
        {
            this.playerViewRadius = par3;
            this.mcServer = par1MinecraftServer;
            this.playerDimension = par2;
        }
    }

    /**
     * Returns the MinecraftServer associated with the PlayerManager.
     */
    public WorldServer getMinecraftServer()
    {
        return this.mcServer.getWorldManager(this.playerDimension);
    }

    /**
     * updates all the player instances that need to be updated
     */
    public void updatePlayerInstances()
    {
        for (int var1 = 0; var1 < this.playerInstancesToUpdate.size(); ++var1)
        {
            ((PlayerInstance)this.playerInstancesToUpdate.get(var1)).onUpdate();
        }

        this.playerInstancesToUpdate.clear();

        if (this.players.isEmpty())
        {
            WorldServer var3 = this.mcServer.getWorldManager(this.playerDimension);
            WorldProvider var2 = var3.worldProvider;

            if (!var2.canRespawnHere())
            {
                var3.chunkProviderServer.unloadAllChunks();
            }
        }
    }

    /**
     * passi n the chunk x and y and a flag as to whether or not the instance should be made if it doesnt exist
     */
    private PlayerInstance getPlayerInstance(int par1, int par2, boolean par3)
    {
        long var4 = (long)par1 + 2147483647L | (long)par2 + 2147483647L << 32;
        PlayerInstance var6 = (PlayerInstance)this.playerInstances.getValueByKey(var4);

        if (var6 == null && par3)
        {
            var6 = new PlayerInstance(this, par1, par2);
            this.playerInstances.add(var4, var6);
        }

        return var6;
    }

    public void markBlockNeedsUpdate(int par1, int par2, int par3)
    {
        int var4 = par1 >> 4;
        int var5 = par3 >> 4;
        PlayerInstance var6 = this.getPlayerInstance(var4, var5, false);

        if (var6 != null)
        {
            var6.markBlockNeedsUpdate(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Adds an EntityPlayerMP to the PlayerManager.
     */
    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.posX >> 4;
        int var3 = (int)par1EntityPlayerMP.posZ >> 4;
        par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
        par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
        int var4 = 0;
        int var5 = this.playerViewRadius;
        int var6 = 0;
        int var7 = 0;
        this.getPlayerInstance(var2, var3, true).addPlayer(par1EntityPlayerMP);
        int var8;

        for (var8 = 1; var8 <= var5 * 2; ++var8)
        {
            for (int var9 = 0; var9 < 2; ++var9)
            {
                int[] var10 = this.xzDirectionsConst[var4++ % 4];

                for (int var11 = 0; var11 < var8; ++var11)
                {
                    var6 += var10[0];
                    var7 += var10[1];
                    this.getPlayerInstance(var2 + var6, var3 + var7, true).addPlayer(par1EntityPlayerMP);
                }
            }
        }

        var4 %= 4;

        for (var8 = 0; var8 < var5 * 2; ++var8)
        {
            var6 += this.xzDirectionsConst[var4][0];
            var7 += this.xzDirectionsConst[var4][1];
            this.getPlayerInstance(var2 + var6, var3 + var7, true).addPlayer(par1EntityPlayerMP);
        }

        this.players.add(par1EntityPlayerMP);
    }

    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     */
    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.managedPosX >> 4;
        int var3 = (int)par1EntityPlayerMP.managedPosZ >> 4;

        for (int var4 = var2 - this.playerViewRadius; var4 <= var2 + this.playerViewRadius; ++var4)
        {
            for (int var5 = var3 - this.playerViewRadius; var5 <= var3 + this.playerViewRadius; ++var5)
            {
                PlayerInstance var6 = this.getPlayerInstance(var4, var5, false);

                if (var6 != null)
                {
                    var6.removePlayer(par1EntityPlayerMP);
                }
            }
        }

        this.players.remove(par1EntityPlayerMP);
    }

    /**
     * args: targetChunkX, targetChunkZ, playerChunkX, playerChunkZ - return true if the target chunk is outside the
     * cube of player visibility
     */
    private boolean isOutsidePlayerViewRadius(int par1, int par2, int par3, int par4)
    {
        int var5 = par1 - par3;
        int var6 = par2 - par4;
        return var5 >= -this.playerViewRadius && var5 <= this.playerViewRadius ? var6 >= -this.playerViewRadius && var6 <= this.playerViewRadius : false;
    }

    /**
     * update chunks around a player being moved by server logic (e.g. cart, boat)
     */
    public void updateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.posX >> 4;
        int var3 = (int)par1EntityPlayerMP.posZ >> 4;
        double var4 = par1EntityPlayerMP.managedPosX - par1EntityPlayerMP.posX;
        double var6 = par1EntityPlayerMP.managedPosZ - par1EntityPlayerMP.posZ;
        double var8 = var4 * var4 + var6 * var6;

        if (var8 >= 64.0D)
        {
            int var10 = (int)par1EntityPlayerMP.managedPosX >> 4;
            int var11 = (int)par1EntityPlayerMP.managedPosZ >> 4;
            int var12 = var2 - var10;
            int var13 = var3 - var11;

            if (var12 != 0 || var13 != 0)
            {
                for (int var14 = var2 - this.playerViewRadius; var14 <= var2 + this.playerViewRadius; ++var14)
                {
                    for (int var15 = var3 - this.playerViewRadius; var15 <= var3 + this.playerViewRadius; ++var15)
                    {
                        if (!this.isOutsidePlayerViewRadius(var14, var15, var10, var11))
                        {
                            this.getPlayerInstance(var14, var15, true).addPlayer(par1EntityPlayerMP);
                        }

                        if (!this.isOutsidePlayerViewRadius(var14 - var12, var15 - var13, var2, var3))
                        {
                            PlayerInstance var16 = this.getPlayerInstance(var14 - var12, var15 - var13, false);

                            if (var16 != null)
                            {
                                var16.removePlayer(par1EntityPlayerMP);
                            }
                        }
                    }
                }

                par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
                par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
            }
        }
    }

    public int getMaxTrackingDistance()
    {
        return this.playerViewRadius * 16 - 16;
    }

    /**
     * get the hash of all player instances
     */
    static LongHashMap getPlayerInstances(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.playerInstances;
    }

    /**
     * retrieve the list of all playerInstances that need to be updated on tick
     */
    static List getPlayerInstancesToUpdate(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.playerInstancesToUpdate;
    }
}
