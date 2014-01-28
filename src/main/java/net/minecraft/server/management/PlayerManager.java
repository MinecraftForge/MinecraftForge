package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class PlayerManager
{
    private final WorldServer theWorldServer;
    // JAVADOC FIELD $$ field_72699_b
    private final List players = new ArrayList();
    // JAVADOC FIELD $$ field_72700_c
    private final LongHashMap playerInstances = new LongHashMap();
    // JAVADOC FIELD $$ field_72697_d
    private final List chunkWatcherWithPlayers = new ArrayList();
    // JAVADOC FIELD $$ field_111193_e
    private final List playerInstanceList = new ArrayList();
    // JAVADOC FIELD $$ field_72698_e
    private final int playerViewRadius;
    // JAVADOC FIELD $$ field_111192_g
    private long previousTotalWorldTime;
    // JAVADOC FIELD $$ field_72696_f
    private final int[][] xzDirectionsConst = new int[][] {{1, 0}, {0, 1}, { -1, 0}, {0, -1}};
    private static final String __OBFID = "CL_00001434";

    public PlayerManager(WorldServer par1WorldServer, int par2)
    {
        if (par2 > 15)
        {
            throw new IllegalArgumentException("Too big view radius!");
        }
        else if (par2 < 3)
        {
            throw new IllegalArgumentException("Too small view radius!");
        }
        else
        {
            this.playerViewRadius = par2;
            this.theWorldServer = par1WorldServer;
        }
    }

    public WorldServer getWorldServer()
    {
        return this.theWorldServer;
    }

    // JAVADOC METHOD $$ func_72693_b
    public void updatePlayerInstances()
    {
        long i = this.theWorldServer.getTotalWorldTime();
        int j;
        PlayerManager.PlayerInstance playerinstance;

        if (i - this.previousTotalWorldTime > 8000L)
        {
            this.previousTotalWorldTime = i;

            for (j = 0; j < this.playerInstanceList.size(); ++j)
            {
                playerinstance = (PlayerManager.PlayerInstance)this.playerInstanceList.get(j);
                playerinstance.sendChunkUpdate();
                playerinstance.processChunk();
            }
        }
        else
        {
            for (j = 0; j < this.chunkWatcherWithPlayers.size(); ++j)
            {
                playerinstance = (PlayerManager.PlayerInstance)this.chunkWatcherWithPlayers.get(j);
                playerinstance.sendChunkUpdate();
            }
        }

        this.chunkWatcherWithPlayers.clear();

        if (this.players.isEmpty())
        {
            WorldProvider worldprovider = this.theWorldServer.provider;

            if (!worldprovider.canRespawnHere())
            {
                this.theWorldServer.theChunkProviderServer.unloadAllChunks();
            }
        }
    }

    private PlayerManager.PlayerInstance getOrCreateChunkWatcher(int par1, int par2, boolean par3)
    {
        long k = (long)par1 + 2147483647L | (long)par2 + 2147483647L << 32;
        PlayerManager.PlayerInstance playerinstance = (PlayerManager.PlayerInstance)this.playerInstances.getValueByKey(k);

        if (playerinstance == null && par3)
        {
            playerinstance = new PlayerManager.PlayerInstance(par1, par2);
            this.playerInstances.add(k, playerinstance);
            this.playerInstanceList.add(playerinstance);
        }

        return playerinstance;
    }

    public void func_151250_a(int p_151250_1_, int p_151250_2_, int p_151250_3_)
    {
        int l = p_151250_1_ >> 4;
        int i1 = p_151250_3_ >> 4;
        PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(l, i1, false);

        if (playerinstance != null)
        {
            playerinstance.func_151253_a(p_151250_1_ & 15, p_151250_2_, p_151250_3_ & 15);
        }
    }

    // JAVADOC METHOD $$ func_72683_a
    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int i = (int)par1EntityPlayerMP.posX >> 4;
        int j = (int)par1EntityPlayerMP.posZ >> 4;
        par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
        par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;

        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k)
        {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l)
            {
                this.getOrCreateChunkWatcher(k, l, true).addPlayer(par1EntityPlayerMP);
            }
        }

        this.players.add(par1EntityPlayerMP);
        this.filterChunkLoadQueue(par1EntityPlayerMP);
    }

    // JAVADOC METHOD $$ func_72691_b
    public void filterChunkLoadQueue(EntityPlayerMP par1EntityPlayerMP)
    {
        ArrayList arraylist = new ArrayList(par1EntityPlayerMP.loadedChunks);
        int i = 0;
        int j = this.playerViewRadius;
        int k = (int)par1EntityPlayerMP.posX >> 4;
        int l = (int)par1EntityPlayerMP.posZ >> 4;
        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = this.getOrCreateChunkWatcher(k, l, true).chunkLocation;
        par1EntityPlayerMP.loadedChunks.clear();

        if (arraylist.contains(chunkcoordintpair))
        {
            par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
        }

        int k1;

        for (k1 = 1; k1 <= j * 2; ++k1)
        {
            for (int l1 = 0; l1 < 2; ++l1)
            {
                int[] aint = this.xzDirectionsConst[i++ % 4];

                for (int i2 = 0; i2 < k1; ++i2)
                {
                    i1 += aint[0];
                    j1 += aint[1];
                    chunkcoordintpair = this.getOrCreateChunkWatcher(k + i1, l + j1, true).chunkLocation;

                    if (arraylist.contains(chunkcoordintpair))
                    {
                        par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
                    }
                }
            }
        }

        i %= 4;

        for (k1 = 0; k1 < j * 2; ++k1)
        {
            i1 += this.xzDirectionsConst[i][0];
            j1 += this.xzDirectionsConst[i][1];
            chunkcoordintpair = this.getOrCreateChunkWatcher(k + i1, l + j1, true).chunkLocation;

            if (arraylist.contains(chunkcoordintpair))
            {
                par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
            }
        }
    }

    // JAVADOC METHOD $$ func_72695_c
    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int i = (int)par1EntityPlayerMP.managedPosX >> 4;
        int j = (int)par1EntityPlayerMP.managedPosZ >> 4;

        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k)
        {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l)
            {
                PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(k, l, false);

                if (playerinstance != null)
                {
                    playerinstance.removePlayer(par1EntityPlayerMP);
                }
            }
        }

        this.players.remove(par1EntityPlayerMP);
    }

    // JAVADOC METHOD $$ func_72684_a
    private boolean overlaps(int par1, int par2, int par3, int par4, int par5)
    {
        int j1 = par1 - par3;
        int k1 = par2 - par4;
        return j1 >= -par5 && j1 <= par5 ? k1 >= -par5 && k1 <= par5 : false;
    }

    // JAVADOC METHOD $$ func_72685_d
    public void updateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int i = (int)par1EntityPlayerMP.posX >> 4;
        int j = (int)par1EntityPlayerMP.posZ >> 4;
        double d0 = par1EntityPlayerMP.managedPosX - par1EntityPlayerMP.posX;
        double d1 = par1EntityPlayerMP.managedPosZ - par1EntityPlayerMP.posZ;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D)
        {
            int k = (int)par1EntityPlayerMP.managedPosX >> 4;
            int l = (int)par1EntityPlayerMP.managedPosZ >> 4;
            int i1 = this.playerViewRadius;
            int j1 = i - k;
            int k1 = j - l;

            if (j1 != 0 || k1 != 0)
            {
                for (int l1 = i - i1; l1 <= i + i1; ++l1)
                {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2)
                    {
                        if (!this.overlaps(l1, i2, k, l, i1))
                        {
                            this.getOrCreateChunkWatcher(l1, i2, true).addPlayer(par1EntityPlayerMP);
                        }

                        if (!this.overlaps(l1 - j1, i2 - k1, i, j, i1))
                        {
                            PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(l1 - j1, i2 - k1, false);

                            if (playerinstance != null)
                            {
                                playerinstance.removePlayer(par1EntityPlayerMP);
                            }
                        }
                    }
                }

                this.filterChunkLoadQueue(par1EntityPlayerMP);
                par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
                par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP par1EntityPlayerMP, int par2, int par3)
    {
        PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(par2, par3, false);
        return playerinstance == null ? false : playerinstance.playersInChunk.contains(par1EntityPlayerMP) && !par1EntityPlayerMP.loadedChunks.contains(playerinstance.chunkLocation);
    }

    // JAVADOC METHOD $$ func_72686_a
    public static int getFurthestViewableBlock(int par0)
    {
        return par0 * 16 - 16;
    }

    class PlayerInstance
    {
        private final List playersInChunk = new ArrayList();
        // JAVADOC FIELD $$ field_73264_c
        private final ChunkCoordIntPair chunkLocation;
        private short[] field_151254_d = new short[64];
        private int numberOfTilesToUpdate;
        // JAVADOC FIELD $$ field_73260_f
        private int flagsYAreasToUpdate;
        // JAVADOC FIELD $$ field_111198_g
        private long previousWorldTime;
        private static final String __OBFID = "CL_00001435";

        public PlayerInstance(int par2, int par3)
        {
            this.chunkLocation = new ChunkCoordIntPair(par2, par3);
            PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(par2, par3);
        }

        public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
        {
            if (this.playersInChunk.contains(par1EntityPlayerMP))
            {
                throw new IllegalStateException("Failed to add player. " + par1EntityPlayerMP + " already is in chunk " + this.chunkLocation.chunkXPos + ", " + this.chunkLocation.chunkZPos);
            }
            else
            {
                if (this.playersInChunk.isEmpty())
                {
                    this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
                }

                this.playersInChunk.add(par1EntityPlayerMP);
                par1EntityPlayerMP.loadedChunks.add(this.chunkLocation);
            }
        }

        public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
        {
            if (this.playersInChunk.contains(par1EntityPlayerMP))
            {
                Chunk chunk = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);

                if (chunk.func_150802_k())
                {
                    par1EntityPlayerMP.playerNetServerHandler.func_147359_a(new S21PacketChunkData(chunk, true, 0));
                }

                this.playersInChunk.remove(par1EntityPlayerMP);
                par1EntityPlayerMP.loadedChunks.remove(this.chunkLocation);

                MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(chunkLocation, par1EntityPlayerMP));

                if (this.playersInChunk.isEmpty())
                {
                    long i = (long)this.chunkLocation.chunkXPos + 2147483647L | (long)this.chunkLocation.chunkZPos + 2147483647L << 32;
                    this.increaseInhabitedTime(chunk);
                    PlayerManager.this.playerInstances.remove(i);
                    PlayerManager.this.playerInstanceList.remove(this);

                    if (this.numberOfTilesToUpdate > 0)
                    {
                        PlayerManager.this.chunkWatcherWithPlayers.remove(this);
                    }

                    PlayerManager.this.getWorldServer().theChunkProviderServer.unloadChunksIfNotNearSpawn(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
                }
            }
        }

        // JAVADOC METHOD $$ func_111194_a
        public void processChunk()
        {
            this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos));
        }

        // JAVADOC METHOD $$ func_111196_a
        private void increaseInhabitedTime(Chunk par1Chunk)
        {
            par1Chunk.inhabitedTime += PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime;
            this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
        }

        public void func_151253_a(int p_151253_1_, int p_151253_2_, int p_151253_3_)
        {
            if (this.numberOfTilesToUpdate == 0)
            {
                PlayerManager.this.chunkWatcherWithPlayers.add(this);
            }

            this.flagsYAreasToUpdate |= 1 << (p_151253_2_ >> 4);

            //if (this.numberOfTilesToUpdate < 64) //Forge; Cache everything, so always run
            {
                short short1 = (short)(p_151253_1_ << 12 | p_151253_3_ << 8 | p_151253_2_);

                for (int l = 0; l < this.numberOfTilesToUpdate; ++l)
                {
                    if (this.field_151254_d[l] == short1)
                    {
                        return;
                    }
                }

                if (numberOfTilesToUpdate == field_151254_d.length)
                {
                    field_151254_d = Arrays.copyOf(field_151254_d, field_151254_d.length << 1);
                }
                this.field_151254_d[this.numberOfTilesToUpdate++] = short1;
            }
        }

        public void func_151251_a(Packet p_151251_1_)
        {
            for (int i = 0; i < this.playersInChunk.size(); ++i)
            {
                EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playersInChunk.get(i);

                if (!entityplayermp.loadedChunks.contains(this.chunkLocation))
                {
                    entityplayermp.playerNetServerHandler.func_147359_a(p_151251_1_);
                }
            }
        }

        public void sendChunkUpdate()
        {
            if (this.numberOfTilesToUpdate != 0)
            {
                int i;
                int j;
                int k;

                if (this.numberOfTilesToUpdate == 1)
                {
                    i = this.chunkLocation.chunkXPos * 16 + (this.field_151254_d[0] >> 12 & 15);
                    j = this.field_151254_d[0] & 255;
                    k = this.chunkLocation.chunkZPos * 16 + (this.field_151254_d[0] >> 8 & 15);
                    this.func_151251_a(new S23PacketBlockChange(i, j, k, PlayerManager.this.theWorldServer));

                    if (PlayerManager.this.theWorldServer.func_147439_a(i, j, k).hasTileEntity(PlayerManager.this.theWorldServer.getBlockMetadata(i, j, k)))
                    {
                        this.func_151252_a(PlayerManager.this.theWorldServer.func_147438_o(i, j, k));
                    }
                }
                else
                {
                    int l;

                    if (this.numberOfTilesToUpdate == ForgeModContainer.clumpingThreshold)
                    {
                        i = this.chunkLocation.chunkXPos * 16;
                        j = this.chunkLocation.chunkZPos * 16;
                        this.func_151251_a(new S21PacketChunkData(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), false, this.flagsYAreasToUpdate));

                        /* Forge: Grabs ALL tile entities is costly on a modded server, only send needed ones
                        for (k = 0; k < 16; ++k)
                        {
                            if ((this.flagsYAreasToUpdate & 1 << k) != 0)
                            {
                                l = k << 4;
                                List list = PlayerManager.this.theWorldServer.func_147486_a(i, l, j, i + 16, l + 16, j + 16);

                                for (int i1 = 0; i1 < list.size(); ++i1)
                                {
                                    this.func_151252_a((TileEntity)list.get(i1));
                                }
                            }
                        }
                        */
                    }
                    else
                    {
                        this.func_151251_a(new S22PacketMultiBlockChange(this.numberOfTilesToUpdate, this.field_151254_d, PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos)));
                    }
                    
                    { //Forge: Send only the tile entities that are updated, Adding this brace lets us keep the indent and the patch small
                        WorldServer world = PlayerManager.this.theWorldServer;
                        for (i = 0; i < this.numberOfTilesToUpdate; ++i)
                        {
                            j = this.chunkLocation.chunkXPos * 16 + (this.field_151254_d[i] >> 12 & 15);
                            k = this.field_151254_d[i] & 255;
                            l = this.chunkLocation.chunkZPos * 16 + (this.field_151254_d[i] >> 8 & 15);

                            if (world.func_147439_a(j, k, l).hasTileEntity(world.getBlockMetadata(j, k, l)))
                            {
                                this.func_151252_a(PlayerManager.this.theWorldServer.func_147438_o(j, k, l));
                            }
                        }
                    }
                }

                this.numberOfTilesToUpdate = 0;
                this.flagsYAreasToUpdate = 0;
            }
        }

        private void func_151252_a(TileEntity p_151252_1_)
        {
            if (p_151252_1_ != null)
            {
                Packet packet = p_151252_1_.func_145844_m();

                if (packet != null)
                {
                    this.func_151251_a(packet);
                }
            }
        }
    }
}