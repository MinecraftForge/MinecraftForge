package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

class PlayerInstance
{
    /** the list of all players in this instance (chunk) */
    private List players;

    /** the x coordinate of the chunk they are in */
    private int chunkX;

    /** the z coordinate of the chunk they are in */
    private int chunkZ;

    /** the chunk the player currently resides in */
    private ChunkCoordIntPair currentChunk;

    /** array of blocks to update this tick */
    private short[] blocksToUpdate;

    /** the number of blocks that need to be updated next tick */
    private int numBlocksToUpdate;
    private int field_48475_h;

    final PlayerManager playerManager;

    public PlayerInstance(PlayerManager par1PlayerManager, int par2, int par3)
    {
        this.playerManager = par1PlayerManager;
        this.players = new ArrayList();
        this.blocksToUpdate = new short[64];
        this.numBlocksToUpdate = 0;
        this.chunkX = par2;
        this.chunkZ = par3;
        this.currentChunk = new ChunkCoordIntPair(par2, par3);
        par1PlayerManager.getMinecraftServer().chunkProviderServer.loadChunk(par2, par3);
    }

    /**
     * adds this player to the playerInstance
     */
    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.players.contains(par1EntityPlayerMP))
        {
            throw new IllegalStateException("Failed to add player. " + par1EntityPlayerMP + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
        }
        else
        {
            par1EntityPlayerMP.listeningChunks.add(this.currentChunk);
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos, true));
            this.players.add(par1EntityPlayerMP);
            par1EntityPlayerMP.loadedChunks.add(this.currentChunk);
        }
    }

    /**
     * remove player from this instance
     */
    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.players.contains(par1EntityPlayerMP))
        {
            this.players.remove(par1EntityPlayerMP);

            if (this.players.size() == 0)
            {
                long var2 = (long)this.chunkX + 2147483647L | (long)this.chunkZ + 2147483647L << 32;
                PlayerManager.getPlayerInstances(this.playerManager).remove(var2);

                if (this.numBlocksToUpdate > 0)
                {
                    PlayerManager.getPlayerInstancesToUpdate(this.playerManager).remove(this);
                }

                this.playerManager.getMinecraftServer().chunkProviderServer.dropChunk(this.chunkX, this.chunkZ);
            }

            par1EntityPlayerMP.loadedChunks.remove(this.currentChunk);

            if (par1EntityPlayerMP.listeningChunks.contains(this.currentChunk))
            {
                par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkZ, false));
            }
        }
    }

    /**
     * mark the block as changed so that it will update clients who need to know about it
     */
    public void markBlockNeedsUpdate(int par1, int par2, int par3)
    {
        if (this.numBlocksToUpdate == 0)
        {
            PlayerManager.getPlayerInstancesToUpdate(this.playerManager).add(this);
        }

        this.field_48475_h |= 1 << (par2 >> 4);

        if (this.numBlocksToUpdate < 64)
        {
            short var4 = (short)(par1 << 12 | par3 << 8 | par2);

            for (int var5 = 0; var5 < this.numBlocksToUpdate; ++var5)
            {
                if (this.blocksToUpdate[var5] == var4)
                {
                    return;
                }
            }

            this.blocksToUpdate[this.numBlocksToUpdate++] = var4;
        }
    }

    /**
     * sends the packet to all players in the current instance
     */
    public void sendPacketToPlayersInInstance(Packet par1Packet)
    {
        for (int var2 = 0; var2 < this.players.size(); ++var2)
        {
            EntityPlayerMP var3 = (EntityPlayerMP)this.players.get(var2);

            if (var3.listeningChunks.contains(this.currentChunk) && !var3.loadedChunks.contains(this.currentChunk))
            {
                var3.playerNetServerHandler.sendPacket(par1Packet);
            }
        }
    }

    public void onUpdate()
    {
        WorldServer var1 = this.playerManager.getMinecraftServer();

        if (this.numBlocksToUpdate != 0)
        {
            int var2;
            int var3;
            int var4;

            if (this.numBlocksToUpdate == 1)
            {
                var2 = this.chunkX * 16 + (this.blocksToUpdate[0] >> 12 & 15);
                var3 = this.blocksToUpdate[0] & 255;
                var4 = this.chunkZ * 16 + (this.blocksToUpdate[0] >> 8 & 15);
                this.sendPacketToPlayersInInstance(new Packet53BlockChange(var2, var3, var4, var1));

                if (var1.func_48084_h(var2, var3, var4))
                {
                    this.updateTileEntity(var1.getBlockTileEntity(var2, var3, var4));
                }
            }
            else
            {
                int var5;

                if (this.numBlocksToUpdate == 64)
                {
                    var2 = this.chunkX * 16;
                    var3 = this.chunkZ * 16;
                    this.sendPacketToPlayersInInstance(new Packet51MapChunk(var1.getChunkFromChunkCoords(this.chunkX, this.chunkZ), false, this.field_48475_h));

                    for (var4 = 0; var4 < 16; ++var4)
                    {
                        if ((this.field_48475_h & 1 << var4) != 0)
                        {
                            var5 = var4 << 4;
                            List var6 = var1.getTileEntityList(var2, var5, var3, var2 + 16, var5 + 16, var3 + 16);

                            for (int var7 = 0; var7 < var6.size(); ++var7)
                            {
                                this.updateTileEntity((TileEntity)var6.get(var7));
                            }
                        }
                    }
                }
                else
                {
                    this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.chunkX, this.chunkZ, this.blocksToUpdate, this.numBlocksToUpdate, var1));

                    for (var2 = 0; var2 < this.numBlocksToUpdate; ++var2)
                    {
                        var3 = this.chunkX * 16 + (this.blocksToUpdate[var2] >> 12 & 15);
                        var4 = this.blocksToUpdate[var2] & 255;
                        var5 = this.chunkZ * 16 + (this.blocksToUpdate[var2] >> 8 & 15);

                        if (var1.func_48084_h(var3, var4, var5))
                        {
                            this.updateTileEntity(var1.getBlockTileEntity(var3, var4, var5));
                        }
                    }
                }
            }

            this.numBlocksToUpdate = 0;
            this.field_48475_h = 0;
        }
    }

    /**
     * sends players update packet about the given entity
     */
    private void updateTileEntity(TileEntity par1TileEntity)
    {
        if (par1TileEntity != null)
        {
            Packet var2 = par1TileEntity.getDescriptionPacket();

            if (var2 != null)
            {
                this.sendPacketToPlayersInInstance(var2);
            }
        }
    }
}
