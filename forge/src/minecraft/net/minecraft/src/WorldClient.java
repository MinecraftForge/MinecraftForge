package net.minecraft.src;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class WorldClient extends World
{
    /**
     * Contains a list of blocks to to receive and process after they've been 'accepted' by the client (i.e., not
     * invalidated).
     */
    private LinkedList blocksToReceive = new LinkedList();

    /** The packets that need to be sent to the server. */
    private NetClientHandler sendQueue;
    private ChunkProviderClient field_20915_C;

    /**
     * The hash set of entities handled by this client. Uses the entity's ID as the hash set's key.
     */
    private IntHashMap entityHashSet = new IntHashMap();

    /** Contains all entities for this client, both spawned and non-spawned. */
    private Set entityList = new HashSet();

    /**
     * Contains all entities for this client that were not spawned due to a non-present chunk. The game will attempt to
     * spawn up to 10 pending entities with each subsequent tick until the spawn queue is empty.
     */
    private Set entitySpawnQueue = new HashSet();

    public WorldClient(NetClientHandler par1NetClientHandler, WorldSettings par2WorldSettings, int par3, int par4)
    {
        super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(par3), par2WorldSettings);
        this.sendQueue = par1NetClientHandler;
        this.difficultySetting = par4;
        this.setSpawnPoint(new ChunkCoordinates(8, 64, 8));
        this.mapStorage = par1NetClientHandler.mapStorage;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        this.setWorldTime(this.getWorldTime() + 1L);
        int var1;

        for (var1 = 0; var1 < 10 && !this.entitySpawnQueue.isEmpty(); ++var1)
        {
            Entity var2 = (Entity)this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(var2);

            if (!this.loadedEntityList.contains(var2))
            {
                this.spawnEntityInWorld(var2);
            }
        }

        this.sendQueue.processReadPackets();

        for (var1 = 0; var1 < this.blocksToReceive.size(); ++var1)
        {
            WorldBlockPositionType var3 = (WorldBlockPositionType)this.blocksToReceive.get(var1);

            if (--var3.acceptCountdown == 0)
            {
                super.setBlockAndMetadata(var3.posX, var3.posY, var3.posZ, var3.blockID, var3.metadata);
                super.markBlockNeedsUpdate(var3.posX, var3.posY, var3.posZ);
                this.blocksToReceive.remove(var1--);
            }
        }

        this.field_20915_C.unload100OldestChunks();
        this.tickBlocksAndAmbiance();
    }

    /**
     * Invalidates an AABB region of blocks from the receive queue, in the event that the block has been modified
     * client-side in the intervening 80 receive ticks.
     */
    public void invalidateBlockReceiveRegion(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        for (int var7 = 0; var7 < this.blocksToReceive.size(); ++var7)
        {
            WorldBlockPositionType var8 = (WorldBlockPositionType)this.blocksToReceive.get(var7);

            if (var8.posX >= par1 && var8.posY >= par2 && var8.posZ >= par3 && var8.posX <= par4 && var8.posY <= par5 && var8.posZ <= par6)
            {
                this.blocksToReceive.remove(var7--);
            }
        }
    }

    protected IChunkProvider createChunkProvider()
    {
        this.field_20915_C = new ChunkProviderClient(this);
        return this.field_20915_C;
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setSpawnLocation()
    {
        this.setSpawnPoint(new ChunkCoordinates(8, 64, 8));
    }

    protected void tickBlocksAndAmbiance()
    {
        this.func_48461_r();
        Iterator var1 = this.activeChunkSet.iterator();

        while (var1.hasNext())
        {
            ChunkCoordIntPair var2 = (ChunkCoordIntPair)var1.next();
            int var3 = var2.chunkXPos * 16;
            int var4 = var2.chunkZPos * 16;
            Profiler.startSection("getChunk");
            Chunk var5 = this.getChunkFromChunkCoords(var2.chunkXPos, var2.chunkZPos);
            this.func_48458_a(var3, var4, var5);
            Profiler.endSection();
        }
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     */
    public void scheduleBlockUpdate(int par1, int par2, int par3, int par4, int par5) {}

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(boolean par1)
    {
        return false;
    }

    public void doPreChunk(int par1, int par2, boolean par3)
    {
        if (par3)
        {
            this.field_20915_C.loadChunk(par1, par2);
        }
        else
        {
            this.field_20915_C.func_539_c(par1, par2);
        }

        if (!par3)
        {
            this.markBlocksDirty(par1 * 16, 0, par2 * 16, par1 * 16 + 15, 256, par2 * 16 + 15);
        }
    }

    /**
     * Called to place all entities as part of a world
     */
    public boolean spawnEntityInWorld(Entity par1Entity)
    {
        boolean var2 = super.spawnEntityInWorld(par1Entity);
        this.entityList.add(par1Entity);

        if (!var2)
        {
            this.entitySpawnQueue.add(par1Entity);
        }

        return var2;
    }

    /**
     * Not sure what this does 100%, but from the calling methods this method should be called like this.
     */
    public void setEntityDead(Entity par1Entity)
    {
        super.setEntityDead(par1Entity);
        this.entityList.remove(par1Entity);
    }

    /**
     * Start the skin for this entity downloading, if necessary, and increment its reference counter
     */
    protected void obtainEntitySkin(Entity par1Entity)
    {
        super.obtainEntitySkin(par1Entity);

        if (this.entitySpawnQueue.contains(par1Entity))
        {
            this.entitySpawnQueue.remove(par1Entity);
        }
    }

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    protected void releaseEntitySkin(Entity par1Entity)
    {
        super.releaseEntitySkin(par1Entity);

        if (this.entityList.contains(par1Entity))
        {
            this.entitySpawnQueue.add(par1Entity);
        }
    }

    /**
     * Add an ID to Entity mapping to entityHashSet
     */
    public void addEntityToWorld(int par1, Entity par2Entity)
    {
        Entity var3 = this.getEntityByID(par1);

        if (var3 != null)
        {
            this.setEntityDead(var3);
        }

        this.entityList.add(par2Entity);
        par2Entity.entityId = par1;

        if (!this.spawnEntityInWorld(par2Entity))
        {
            this.entitySpawnQueue.add(par2Entity);
        }

        this.entityHashSet.addKey(par1, par2Entity);
    }

    /**
     * Lookup and return an Entity based on its ID
     */
    public Entity getEntityByID(int par1)
    {
        return (Entity)this.entityHashSet.lookup(par1);
    }

    public Entity removeEntityFromWorld(int par1)
    {
        Entity var2 = (Entity)this.entityHashSet.removeObject(par1);

        if (var2 != null)
        {
            this.entityList.remove(var2);
            this.setEntityDead(var2);
        }

        return var2;
    }

    /**
     * Set the metadata of a block in global coordinates
     */
    public boolean setBlockMetadata(int par1, int par2, int par3, int par4)
    {
        this.getBlockId(par1, par2, par3);
        this.getBlockMetadata(par1, par2, par3);
        return super.setBlockMetadata(par1, par2, par3, par4);
    }

    /**
     * Sets the block ID and metadata of a block in global coordinates
     */
    public boolean setBlockAndMetadata(int par1, int par2, int par3, int par4, int par5)
    {
        this.getBlockId(par1, par2, par3);
        this.getBlockMetadata(par1, par2, par3);
        return super.setBlockAndMetadata(par1, par2, par3, par4, par5);
    }

    /**
     * Sets the block to the specified blockID at the block coordinates Args x, y, z, blockID
     */
    public boolean setBlock(int par1, int par2, int par3, int par4)
    {
        this.getBlockId(par1, par2, par3);
        this.getBlockMetadata(par1, par2, par3);
        return super.setBlock(par1, par2, par3, par4);
    }

    public boolean setBlockAndMetadataAndInvalidate(int par1, int par2, int par3, int par4, int par5)
    {
        this.invalidateBlockReceiveRegion(par1, par2, par3, par1, par2, par3);
        return super.setBlockAndMetadataWithNotify(par1, par2, par3, par4, par5);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket()
    {
        this.sendQueue.quitWithPacket(new Packet255KickDisconnect("Quitting"));
    }

    protected void updateWeather()
    {
        if (!this.worldProvider.hasNoSky)
        {
            if (this.lastLightningBolt > 0)
            {
                --this.lastLightningBolt;
            }

            this.prevRainingStrength = this.rainingStrength;

            if (this.worldInfo.isRaining())
            {
                this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
            }
            else
            {
                this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
            }

            if (this.rainingStrength < 0.0F)
            {
                this.rainingStrength = 0.0F;
            }

            if (this.rainingStrength > 1.0F)
            {
                this.rainingStrength = 1.0F;
            }

            this.prevThunderingStrength = this.thunderingStrength;

            if (this.worldInfo.isThundering())
            {
                this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
            }
            else
            {
                this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
            }

            if (this.thunderingStrength < 0.0F)
            {
                this.thunderingStrength = 0.0F;
            }

            if (this.thunderingStrength > 1.0F)
            {
                this.thunderingStrength = 1.0F;
            }
        }
    }
}
