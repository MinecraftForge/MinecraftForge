package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.src.forge.ForgeHooks;

public class World implements IBlockAccess
{
    /**
     * boolean; if true updates scheduled by scheduleBlockUpdate happen immediately
     */
    public boolean scheduledUpdatesAreImmediate = false;
    public List loadedEntityList = new ArrayList();
    private List unloadedEntityList = new ArrayList();

    /**
     * TreeSet of scheduled ticks which is used as a priority queue for the ticks
     */
    private TreeSet scheduledTickTreeSet = new TreeSet();

    /** Set of scheduled ticks (used for checking if a tick already exists) */
    private Set scheduledTickSet = new HashSet();
    public List loadedTileEntityList = new ArrayList();
    private List addedTileEntityList = new ArrayList();

    /** Entities marked for removal. */
    private List entityRemoval = new ArrayList();

    /** Array list of players in the world. */
    public List playerEntities = new ArrayList();

    /** a list of all the lightning entities */
    public List weatherEffects = new ArrayList();
    private long cloudColour = 16777215L;

    /** How much light is subtracted from full daylight */
    public int skylightSubtracted = 0;

    /**
     * Contains the current Linear Congruential Generator seed for block updates. Used with an A value of 3 and a C
     * value of 0x3c6ef35f, producing a highly planar series of values ill-suited for choosing random blocks in a
     * 16x128x16 field.
     */
    protected int updateLCG = (new Random()).nextInt();

    /**
     * magic number used to generate fast random numbers for 3d distribution within a chunk
     */
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;
    protected int lastLightningBolt = 0;
    public int lightningFlash = 0;

    /** true while the server is editing blocks */
    public boolean editingBlocks = false;

    /**
     * Contains a timestamp from when the World object was created. Is used in the session.lock file
     */
    private long lockTimestamp = System.currentTimeMillis();
    public int autosavePeriod = 40;

    /** Whether monsters are enabled or not. (1 = on, 0 = off) */
    public int difficultySetting;

    /** RNG for World. */
    public Random rand = new Random();

    /**
     * Used to differentiate between a newly generated world and an already existing world.
     */
    public boolean isNewWorld = false;
    public final WorldProvider worldProvider;
    protected List worldAccesses = new ArrayList();

    /** Handles chunk operations and caching */
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;

    /**
     * holds information about a world (size on disk, time, spawn point, seed, ...)
     */
    protected WorldInfo worldInfo;

    /**
     * if set, this flag forces a request to load a chunk to load the chunk rather than defaulting to the world's
     * chunkprovider's dummy if possible
     */
    public boolean findingSpawnPoint;
    private boolean allPlayersSleeping;
    public MapStorage mapStorage;
    public final VillageCollection villageCollectionObj = new VillageCollection(this);
    private final VillageSiege villageSiegeObj = new VillageSiege(this);
    private ArrayList collidingBoundingBoxes = new ArrayList();
    private boolean scanningTileEntities;

    /** indicates if enemies are spawned or not */
    protected boolean spawnHostileMobs = true;
    protected boolean spawnPeacefulMobs = true;

    /** populated by chunks that are within 9 chunks of any player */
    protected Set activeChunkSet = new HashSet();

    /** number of ticks until the next random ambients play */
    private int ambientTickCountdown;

    /**
     * is a temporary list of blocks and light values used when updating light levels. Holds up to 32x32x32 blocks (the
     * maximum influence of a light source.) Every element is a packed bit value: 0000000000LLLLzzzzzzyyyyyyxxxxxx. The
     * 4-bit L is a light level used when darkening blocks. 6-bit numbers x, y and z represent the block's offset from
     * the original block, plus 32 (i.e. value of 31 would mean a -1 offset
     */
    int[] lightUpdateBlockList;

    /**
     * entities within AxisAlignedBB excluding one, set and returned in getEntitiesWithinAABBExcludingEntity(Entity
     * var1, AxisAlignedBB var2)
     */
    private List entitiesWithinAABBExcludingEntity;

    /** This is always false on a server */
    public boolean isRemote;

    public BiomeGenBase func_48091_a(int par1, int par2)
    {
        if (this.blockExists(par1, 0, par2))
        {
            Chunk var3 = this.getChunkFromBlockCoords(par1, par2);

            if (var3 != null)
            {
                return var3.func_48560_a(par1 & 15, par2 & 15, this.worldProvider.worldChunkMgr);
            }
        }

        return this.worldProvider.worldChunkMgr.getBiomeGenAt(par1, par2);
    }

    public WorldChunkManager getWorldChunkManager()
    {
        return this.worldProvider.worldChunkMgr;
    }

    public World(ISaveHandler par1ISaveHandler, String par2Str, WorldSettings par3WorldSettings, WorldProvider par4WorldProvider)
    {
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.lightUpdateBlockList = new int[32768];
        this.entitiesWithinAABBExcludingEntity = new ArrayList();
        this.isRemote = false;
        this.saveHandler = par1ISaveHandler;
        this.mapStorage = new MapStorage(par1ISaveHandler);
        this.worldInfo = par1ISaveHandler.loadWorldInfo();
        this.isNewWorld = this.worldInfo == null;

        if (par4WorldProvider != null)
        {
            this.worldProvider = par4WorldProvider;
        }
        else if (this.worldInfo != null && this.worldInfo.getDimension() != 0)
        {
            this.worldProvider = WorldProvider.getProviderForDimension(this.worldInfo.getDimension());
        }
        else
        {
            this.worldProvider = WorldProvider.getProviderForDimension(0);
        }

        boolean var5 = false;

        if (this.worldInfo == null)
        {
            this.worldInfo = new WorldInfo(par3WorldSettings, par2Str);
            var5 = true;
        }
        else
        {
            this.worldInfo.setWorldName(par2Str);
        }

        this.worldProvider.registerWorld(this);
        this.chunkProvider = this.createChunkProvider();

        if (var5)
        {
            this.generateSpawnPoint();
        }

        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        IChunkLoader var1 = this.saveHandler.getChunkLoader(this.worldProvider);
        return new ChunkProvider(this, var1, this.worldProvider.getChunkProvider());
    }

    /**
     * generates a spawn point for this world
     */
    protected void generateSpawnPoint()
    {
        if (!this.worldProvider.canRespawnHere())
        {
            this.worldInfo.setSpawnPosition(0, this.worldProvider.getAverageGroundLevel(), 0);
        }
        else
        {
            this.findingSpawnPoint = true;
            WorldChunkManager var1 = this.worldProvider.worldChunkMgr;
            List var2 = var1.getBiomesToSpawnIn();
            Random var3 = new Random(this.getSeed());
            ChunkPosition var4 = var1.findBiomePosition(0, 0, 256, var2, var3);
            int var5 = 0;
            int var6 = this.worldProvider.getAverageGroundLevel();
            int var7 = 0;

            if (var4 != null)
            {
                var5 = var4.x;
                var7 = var4.z;
            }
            else
            {
                System.out.println("Unable to find spawn biome");
            }

            int var8 = 0;

            while (!this.worldProvider.canCoordinateBeSpawn(var5, var7))
            {
                var5 += var3.nextInt(64) - var3.nextInt(64);
                var7 += var3.nextInt(64) - var3.nextInt(64);
                ++var8;

                if (var8 == 1000)
                {
                    break;
                }
            }

            this.worldInfo.setSpawnPosition(var5, var6, var7);
            this.findingSpawnPoint = false;
        }
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension
     */
    public ChunkCoordinates getEntrancePortalLocation()
    {
        return this.worldProvider.getEntrancePortalLocation();
    }

    /**
     * Returns the block ID of the first block at this (x,z) location with air above it, searching from sea level
     * upwards.
     */
    public int getFirstUncoveredBlock(int par1, int par2)
    {
        int var3;

        for (var3 = 63; !this.isAirBlock(par1, var3 + 1, par2); ++var3)
        {
            ;
        }

        return this.getBlockId(par1, var3, par2);
    }

    public void saveWorld(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        if (this.chunkProvider.canSave())
        {
            if (par2IProgressUpdate != null)
            {
                par2IProgressUpdate.displaySavingString("Saving level");
            }

            this.saveLevel();

            if (par2IProgressUpdate != null)
            {
                par2IProgressUpdate.displayLoadingString("Saving chunks");
            }

            this.chunkProvider.saveChunks(par1, par2IProgressUpdate);
        }
    }

    private void saveLevel()
    {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfoAndPlayer(this.worldInfo, this.playerEntities);
        this.mapStorage.saveAllData();
    }

    /**
     * Returns the block ID at coords x,y,z
     */
    public int getBlockId(int par1, int par2, int par3)
    {
        return par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000 ? (par2 < 0 ? 0 : (par2 >= 256 ? 0 : this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).getBlockID(par1 & 15, par2, par3 & 15))) : 0;
    }

    public int func_48092_f(int par1, int par2, int par3)
    {
        return par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000 ? (par2 < 0 ? 0 : (par2 >= 256 ? 0 : this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).func_48555_b(par1 & 15, par2, par3 & 15))) : 0;
    }

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isAirBlock(int par1, int par2, int par3)
    {
        int id = getBlockId(par1, par2, par3);
        return (id == 0 || Block.blocksList[id] == null || Block.blocksList[id].isAirBlock(this, par1, par2, par3));
    }

    public boolean func_48084_h(int par1, int par2, int par3)
    {
        int var4 = this.getBlockId(par1, par2, par3);
        return Block.blocksList[var4] != null && Block.blocksList[var4].func_48124_n();
    }

    public boolean blockExists(int par1, int par2, int par3)
    {
        return par2 >= 0 && par2 < 256 ? this.chunkExists(par1 >> 4, par3 >> 4) : false;
    }

    /**
     * Checks if the chunks within (argument 4) chunks of the given block exist
     */
    public boolean doChunksNearChunkExist(int par1, int par2, int par3, int par4)
    {
        return this.checkChunksExist(par1 - par4, par2 - par4, par3 - par4, par1 + par4, par2 + par4, par3 + par4);
    }

    /**
     * Checks between a min and max all the chunks inbetween actually exist. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public boolean checkChunksExist(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        if (par5 >= 0 && par2 < 256)
        {
            par1 >>= 4;
            par3 >>= 4;
            par4 >>= 4;
            par6 >>= 4;

            for (int var7 = par1; var7 <= par4; ++var7)
            {
                for (int var8 = par3; var8 <= par6; ++var8)
                {
                    if (!this.chunkExists(var7, var8))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    private boolean chunkExists(int par1, int par2)
    {
        return this.chunkProvider.chunkExists(par1, par2);
    }

    /**
     * Returns a chunk looked up by block coordinates. Args: x, y
     */
    public Chunk getChunkFromBlockCoords(int par1, int par2)
    {
        return this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    public Chunk getChunkFromChunkCoords(int par1, int par2)
    {
        return this.chunkProvider.provideChunk(par1, par2);
    }

    public boolean setBlockAndMetadata(int par1, int par2, int par3, int par4, int par5)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var6 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                boolean var7 = var6.setBlockIDWithMetadata(par1 & 15, par2, par3 & 15, par4, par5);
                Profiler.startSection("checkLight");
                this.updateAllLightTypes(par1, par2, par3);
                Profiler.endSection();
                return var7;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets the block to the specified blockID at the block coordinates Args x, y, z, blockID
     */
    public boolean setBlock(int par1, int par2, int par3, int par4)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var5 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                boolean var6 = var5.setBlockID(par1 & 15, par2, par3 & 15, par4);
                Profiler.startSection("checkLight");
                this.updateAllLightTypes(par1, par2, par3);
                Profiler.endSection();
                return var6;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the block's material.
     */
    public Material getBlockMaterial(int par1, int par2, int par3)
    {
        int var4 = this.getBlockId(par1, par2, par3);
        return var4 == 0 ? Material.air : Block.blocksList[var4].blockMaterial;
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return 0;
            }
            else if (par2 >= 256)
            {
                return 0;
            }
            else
            {
                Chunk var4 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                par1 &= 15;
                par3 &= 15;
                return var4.getBlockMetadata(par1, par2, par3);
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Sets the blocks metadata and if set will then notify blocks that this block changed. Args: x, y, z, metadata
     */
    public void setBlockMetadataWithNotify(int par1, int par2, int par3, int par4)
    {
        if (this.setBlockMetadata(par1, par2, par3, par4))
        {
            int var5 = this.getBlockId(par1, par2, par3);

            if (Block.requiresSelfNotify[var5 & 4095])
            {
                this.notifyBlockChange(par1, par2, par3, var5);
            }
            else
            {
                this.notifyBlocksOfNeighborChange(par1, par2, par3, var5);
            }
        }
    }

    /**
     * Set the metadata of a block in global coordinates
     */
    public boolean setBlockMetadata(int par1, int par2, int par3, int par4)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var5 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                par1 &= 15;
                par3 &= 15;
                return var5.setBlockMetadata(par1, par2, par3, par4);
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets a block and notifies relevant systems with the block change  Args: x, y, z, blockID
     */
    public boolean setBlockWithNotify(int par1, int par2, int par3, int par4)
    {
        if (this.setBlock(par1, par2, par3, par4))
        {
            this.notifyBlockChange(par1, par2, par3, par4);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setBlockAndMetadataWithNotify(int par1, int par2, int par3, int par4, int par5)
    {
        if (this.setBlockAndMetadata(par1, par2, par3, par4, par5))
        {
            this.notifyBlockChange(par1, par2, par3, par4);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void markBlockNeedsUpdate(int par1, int par2, int par3)
    {
        for (int var4 = 0; var4 < this.worldAccesses.size(); ++var4)
        {
            ((IWorldAccess)this.worldAccesses.get(var4)).markBlockNeedsUpdate(par1, par2, par3);
        }
    }

    /**
     * The block type change and need to notify other systems  Args: x, y, z, blockID
     */
    public void notifyBlockChange(int par1, int par2, int par3, int par4)
    {
        this.markBlockNeedsUpdate(par1, par2, par3);
        this.notifyBlocksOfNeighborChange(par1, par2, par3, par4);
    }

    /**
     * marks a vertical line of blocks as dirty
     */
    public void markBlocksDirtyVertical(int par1, int par2, int par3, int par4)
    {
        int var5;

        if (par3 > par4)
        {
            var5 = par4;
            par4 = par3;
            par3 = var5;
        }

        if (!this.worldProvider.hasNoSky)
        {
            for (var5 = par3; var5 <= par4; ++var5)
            {
                this.updateLightByType(EnumSkyBlock.Sky, par1, var5, par2);
            }
        }

        this.markBlocksDirty(par1, par3, par2, par1, par4, par2);
    }

    /**
     * calls the 'MarkBlockAsNeedsUpdate' in all block accesses in this world
     */
    public void markBlockAsNeedsUpdate(int par1, int par2, int par3)
    {
        for (int var4 = 0; var4 < this.worldAccesses.size(); ++var4)
        {
            ((IWorldAccess)this.worldAccesses.get(var4)).markBlockRangeNeedsUpdate(par1, par2, par3, par1, par2, par3);
        }
    }

    public void markBlocksDirty(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        for (int var7 = 0; var7 < this.worldAccesses.size(); ++var7)
        {
            ((IWorldAccess)this.worldAccesses.get(var7)).markBlockRangeNeedsUpdate(par1, par2, par3, par4, par5, par6);
        }
    }

    /**
     * Notifies neighboring blocks that this specified block changed  Args: x, y, z, blockID
     */
    public void notifyBlocksOfNeighborChange(int par1, int par2, int par3, int par4)
    {
        this.notifyBlockOfNeighborChange(par1 - 1, par2, par3, par4);
        this.notifyBlockOfNeighborChange(par1 + 1, par2, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2 - 1, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2 + 1, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2, par3 - 1, par4);
        this.notifyBlockOfNeighborChange(par1, par2, par3 + 1, par4);
    }

    /**
     * Notifies a block that one of its neighbor change to the specified type Args: x, y, z, blockID
     */
    private void notifyBlockOfNeighborChange(int par1, int par2, int par3, int par4)
    {
        if (!this.editingBlocks && !this.isRemote)
        {
            Block var5 = Block.blocksList[this.getBlockId(par1, par2, par3)];

            if (var5 != null)
            {
                var5.onNeighborBlockChange(this, par1, par2, par3, par4);
            }
        }
    }

    /**
     * Checks if the specified block is able to see the sky
     */
    public boolean canBlockSeeTheSky(int par1, int par2, int par3)
    {
        return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).canBlockSeeTheSky(par1 & 15, par2, par3 & 15);
    }

    /**
     * gets the block's light value - without the _do function's checks.
     */
    public int getFullBlockLightValue(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else
        {
            if (par2 >= 256)
            {
                par2 = 255;
            }

            return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).getBlockLightValue(par1 & 15, par2, par3 & 15, 0);
        }
    }

    /**
     * Gets the light value of a block location
     */
    public int getBlockLightValue(int par1, int par2, int par3)
    {
        return this.getBlockLightValue_do(par1, par2, par3, true);
    }

    /**
     * Gets the light value of a block location. This is the actual function that gets the value and has a bool flag
     * that indicates if its a half step block to get the maximum light value of a direct neighboring block (left,
     * right, forward, back, and up)
     */
    public int getBlockLightValue_do(int par1, int par2, int par3, boolean par4)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par4)
            {
                int var5 = this.getBlockId(par1, par2, par3);

                if (var5 == Block.stairSingle.blockID || var5 == Block.tilledField.blockID || var5 == Block.stairCompactCobblestone.blockID || var5 == Block.stairCompactPlanks.blockID)
                {
                    int var6 = this.getBlockLightValue_do(par1, par2 + 1, par3, false);
                    int var7 = this.getBlockLightValue_do(par1 + 1, par2, par3, false);
                    int var8 = this.getBlockLightValue_do(par1 - 1, par2, par3, false);
                    int var9 = this.getBlockLightValue_do(par1, par2, par3 + 1, false);
                    int var10 = this.getBlockLightValue_do(par1, par2, par3 - 1, false);

                    if (var7 > var6)
                    {
                        var6 = var7;
                    }

                    if (var8 > var6)
                    {
                        var6 = var8;
                    }

                    if (var9 > var6)
                    {
                        var6 = var9;
                    }

                    if (var10 > var6)
                    {
                        var6 = var10;
                    }

                    return var6;
                }
            }

            if (par2 < 0)
            {
                return 0;
            }
            else
            {
                if (par2 >= 256)
                {
                    par2 = 255;
                }

                Chunk var11 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                par1 &= 15;
                par3 &= 15;
                return var11.getBlockLightValue(par1, par2, par3, this.skylightSubtracted);
            }
        }
        else
        {
            return 15;
        }
    }

    /**
     * Returns the y coordinate with a block in it at this x, z coordinate
     */
    public int getHeightValue(int par1, int par2)
    {
        if (par1 >= -30000000 && par2 >= -30000000 && par1 < 30000000 && par2 < 30000000)
        {
            if (!this.chunkExists(par1 >> 4, par2 >> 4))
            {
                return 0;
            }
            else
            {
                Chunk var3 = this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
                return var3.getHeightValue(par1 & 15, par2 & 15);
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns saved light value without taking into account the time of day.  Either looks in the sky light map or
     * block light map based on the enumSkyBlock arg.
     */
    public int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000)
        {
            int var5 = par2 >> 4;
            int var6 = par4 >> 4;

            if (!this.chunkExists(var5, var6))
            {
                return par1EnumSkyBlock.defaultLightValue;
            }
            else
            {
                Chunk var7 = this.getChunkFromChunkCoords(var5, var6);
                return var7.getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
            }
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    /**
     * Sets the light value either into the sky map or block map depending on if enumSkyBlock is set to sky or block.
     * Args: enumSkyBlock, x, y, z, lightValue
     */
    public void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5)
    {
        if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000)
        {
            if (par3 >= 0)
            {
                if (par3 < 256)
                {
                    if (this.chunkExists(par2 >> 4, par4 >> 4))
                    {
                        Chunk var6 = this.getChunkFromChunkCoords(par2 >> 4, par4 >> 4);
                        var6.setLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15, par5);

                        for (int var7 = 0; var7 < this.worldAccesses.size(); ++var7)
                        {
                            ((IWorldAccess)this.worldAccesses.get(var7)).func_48414_b(par2, par3, par4);
                        }
                    }
                }
            }
        }
    }

    public void func_48086_o(int par1, int par2, int par3)
    {
        for (int var4 = 0; var4 < this.worldAccesses.size(); ++var4)
        {
            ((IWorldAccess)this.worldAccesses.get(var4)).func_48414_b(par1, par2, par3);
        }
    }

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brihgtness). Args: x, y, z
     */
    public float getLightBrightness(int par1, int par2, int par3)
    {
        return this.worldProvider.lightBrightnessTable[this.getBlockLightValue(par1, par2, par3)];
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean isDaytime()
    {
        return this.skylightSubtracted < 4;
    }

    /**
     * ray traces all blocks, including non-collideable ones
     */
    public MovingObjectPosition rayTraceBlocks(Vec3D par1Vec3D, Vec3D par2Vec3D)
    {
        return this.rayTraceBlocks_do_do(par1Vec3D, par2Vec3D, false, false);
    }

    public MovingObjectPosition rayTraceBlocks_do(Vec3D par1Vec3D, Vec3D par2Vec3D, boolean par3)
    {
        return this.rayTraceBlocks_do_do(par1Vec3D, par2Vec3D, par3, false);
    }

    public MovingObjectPosition rayTraceBlocks_do_do(Vec3D par1Vec3D, Vec3D par2Vec3D, boolean par3, boolean par4)
    {
        if (!Double.isNaN(par1Vec3D.xCoord) && !Double.isNaN(par1Vec3D.yCoord) && !Double.isNaN(par1Vec3D.zCoord))
        {
            if (!Double.isNaN(par2Vec3D.xCoord) && !Double.isNaN(par2Vec3D.yCoord) && !Double.isNaN(par2Vec3D.zCoord))
            {
                int var5 = MathHelper.floor_double(par2Vec3D.xCoord);
                int var6 = MathHelper.floor_double(par2Vec3D.yCoord);
                int var7 = MathHelper.floor_double(par2Vec3D.zCoord);
                int var8 = MathHelper.floor_double(par1Vec3D.xCoord);
                int var9 = MathHelper.floor_double(par1Vec3D.yCoord);
                int var10 = MathHelper.floor_double(par1Vec3D.zCoord);
                int var11 = this.getBlockId(var8, var9, var10);
                int var12 = this.getBlockMetadata(var8, var9, var10);
                Block var13 = Block.blocksList[var11];

                if ((!par4 || var13 == null || var13.getCollisionBoundingBoxFromPool(this, var8, var9, var10) != null) && var11 > 0 && var13.canCollideCheck(var12, par3))
                {
                    MovingObjectPosition var14 = var13.collisionRayTrace(this, var8, var9, var10, par1Vec3D, par2Vec3D);

                    if (var14 != null)
                    {
                        return var14;
                    }
                }

                var11 = 200;

                while (var11-- >= 0)
                {
                    if (Double.isNaN(par1Vec3D.xCoord) || Double.isNaN(par1Vec3D.yCoord) || Double.isNaN(par1Vec3D.zCoord))
                    {
                        return null;
                    }

                    if (var8 == var5 && var9 == var6 && var10 == var7)
                    {
                        return null;
                    }

                    boolean var39 = true;
                    boolean var40 = true;
                    boolean var41 = true;
                    double var15 = 999.0D;
                    double var17 = 999.0D;
                    double var19 = 999.0D;

                    if (var5 > var8)
                    {
                        var15 = (double)var8 + 1.0D;
                    }
                    else if (var5 < var8)
                    {
                        var15 = (double)var8 + 0.0D;
                    }
                    else
                    {
                        var39 = false;
                    }

                    if (var6 > var9)
                    {
                        var17 = (double)var9 + 1.0D;
                    }
                    else if (var6 < var9)
                    {
                        var17 = (double)var9 + 0.0D;
                    }
                    else
                    {
                        var40 = false;
                    }

                    if (var7 > var10)
                    {
                        var19 = (double)var10 + 1.0D;
                    }
                    else if (var7 < var10)
                    {
                        var19 = (double)var10 + 0.0D;
                    }
                    else
                    {
                        var41 = false;
                    }

                    double var21 = 999.0D;
                    double var23 = 999.0D;
                    double var25 = 999.0D;
                    double var27 = par2Vec3D.xCoord - par1Vec3D.xCoord;
                    double var29 = par2Vec3D.yCoord - par1Vec3D.yCoord;
                    double var31 = par2Vec3D.zCoord - par1Vec3D.zCoord;

                    if (var39)
                    {
                        var21 = (var15 - par1Vec3D.xCoord) / var27;
                    }

                    if (var40)
                    {
                        var23 = (var17 - par1Vec3D.yCoord) / var29;
                    }

                    if (var41)
                    {
                        var25 = (var19 - par1Vec3D.zCoord) / var31;
                    }

                    boolean var33 = false;
                    byte var42;

                    if (var21 < var23 && var21 < var25)
                    {
                        if (var5 > var8)
                        {
                            var42 = 4;
                        }
                        else
                        {
                            var42 = 5;
                        }

                        par1Vec3D.xCoord = var15;
                        par1Vec3D.yCoord += var29 * var21;
                        par1Vec3D.zCoord += var31 * var21;
                    }
                    else if (var23 < var25)
                    {
                        if (var6 > var9)
                        {
                            var42 = 0;
                        }
                        else
                        {
                            var42 = 1;
                        }

                        par1Vec3D.xCoord += var27 * var23;
                        par1Vec3D.yCoord = var17;
                        par1Vec3D.zCoord += var31 * var23;
                    }
                    else
                    {
                        if (var7 > var10)
                        {
                            var42 = 2;
                        }
                        else
                        {
                            var42 = 3;
                        }

                        par1Vec3D.xCoord += var27 * var25;
                        par1Vec3D.yCoord += var29 * var25;
                        par1Vec3D.zCoord = var19;
                    }

                    Vec3D var34 = Vec3D.createVector(par1Vec3D.xCoord, par1Vec3D.yCoord, par1Vec3D.zCoord);
                    var8 = (int)(var34.xCoord = (double)MathHelper.floor_double(par1Vec3D.xCoord));

                    if (var42 == 5)
                    {
                        --var8;
                        ++var34.xCoord;
                    }

                    var9 = (int)(var34.yCoord = (double)MathHelper.floor_double(par1Vec3D.yCoord));

                    if (var42 == 1)
                    {
                        --var9;
                        ++var34.yCoord;
                    }

                    var10 = (int)(var34.zCoord = (double)MathHelper.floor_double(par1Vec3D.zCoord));

                    if (var42 == 3)
                    {
                        --var10;
                        ++var34.zCoord;
                    }

                    int var35 = this.getBlockId(var8, var9, var10);
                    int var36 = this.getBlockMetadata(var8, var9, var10);
                    Block var37 = Block.blocksList[var35];

                    if ((!par4 || var37 == null || var37.getCollisionBoundingBoxFromPool(this, var8, var9, var10) != null) && var35 > 0 && var37.canCollideCheck(var36, par3))
                    {
                        MovingObjectPosition var38 = var37.collisionRayTrace(this, var8, var9, var10, par1Vec3D, par2Vec3D);

                        if (var38 != null)
                        {
                            return var38;
                        }
                    }
                }

                return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Plays a sound at the entity's position. Args: entity, sound, unknown1, unknown2
     */
    public void playSoundAtEntity(Entity par1Entity, String par2Str, float par3, float par4)
    {
        for (int var5 = 0; var5 < this.worldAccesses.size(); ++var5)
        {
            ((IWorldAccess)this.worldAccesses.get(var5)).playSound(par2Str, par1Entity.posX, par1Entity.posY - (double)par1Entity.yOffset, par1Entity.posZ, par3, par4);
        }
    }

    /**
     * Play a sound effect. Many many parameters for this function. Not sure what they do, but a classic call is :
     * (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 'random.door_open', 1.0F, world.rand.nextFloat() * 0.1F +
     * 0.9F with i,j,k position of the block.
     */
    public void playSoundEffect(double par1, double par3, double par5, String par7Str, float par8, float par9)
    {
        for (int var10 = 0; var10 < this.worldAccesses.size(); ++var10)
        {
            ((IWorldAccess)this.worldAccesses.get(var10)).playSound(par7Str, par1, par3, par5, par8, par9);
        }
    }

    /**
     * Plays a record at the specified coordinates of the specified name. Args: recordName, x, y, z
     */
    public void playRecord(String par1Str, int par2, int par3, int par4)
    {
        for (int var5 = 0; var5 < this.worldAccesses.size(); ++var5)
        {
            ((IWorldAccess)this.worldAccesses.get(var5)).playRecord(par1Str, par2, par3, par4);
        }
    }

    /**
     * Spawns a particle.  Args particleName, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        for (int var14 = 0; var14 < this.worldAccesses.size(); ++var14)
        {
            ((IWorldAccess)this.worldAccesses.get(var14)).spawnParticle(par1Str, par2, par4, par6, par8, par10, par12);
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity par1Entity)
    {
        this.weatherEffects.add(par1Entity);
        return true;
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntityInWorld(Entity par1Entity)
    {
        int var2 = MathHelper.floor_double(par1Entity.posX / 16.0D);
        int var3 = MathHelper.floor_double(par1Entity.posZ / 16.0D);
        boolean var4 = false;

        if (par1Entity instanceof EntityPlayer)
        {
            var4 = true;
        }

        if (!var4 && !this.chunkExists(var2, var3))
        {
            return false;
        }
        else
        {
            if (par1Entity instanceof EntityPlayer)
            {
                EntityPlayer var5 = (EntityPlayer)par1Entity;
                this.playerEntities.add(var5);
                this.updateAllPlayersSleepingFlag();
            }

            this.getChunkFromChunkCoords(var2, var3).addEntity(par1Entity);
            this.loadedEntityList.add(par1Entity);
            this.obtainEntitySkin(par1Entity);
            return true;
        }
    }

    protected void obtainEntitySkin(Entity par1Entity)
    {
        for (int var2 = 0; var2 < this.worldAccesses.size(); ++var2)
        {
            ((IWorldAccess)this.worldAccesses.get(var2)).obtainEntitySkin(par1Entity);
        }
    }

    protected void releaseEntitySkin(Entity par1Entity)
    {
        for (int var2 = 0; var2 < this.worldAccesses.size(); ++var2)
        {
            ((IWorldAccess)this.worldAccesses.get(var2)).releaseEntitySkin(par1Entity);
        }
    }

    /**
     * Dismounts the entity (and anything riding the entity), sets the dead flag, and removes the player entity from the
     * player entity list. Called by the playerLoggedOut function.
     */
    public void setEntityDead(Entity par1Entity)
    {
        if (par1Entity.riddenByEntity != null)
        {
            par1Entity.riddenByEntity.mountEntity((Entity)null);
        }

        if (par1Entity.ridingEntity != null)
        {
            par1Entity.mountEntity((Entity)null);
        }

        par1Entity.setEntityDead();

        if (par1Entity instanceof EntityPlayer)
        {
            this.playerEntities.remove((EntityPlayer)par1Entity);
            this.updateAllPlayersSleepingFlag();
        }
    }

    /**
     * remove dat player from dem servers
     */
    public void removePlayer(Entity par1Entity)
    {
        par1Entity.setEntityDead();

        if (par1Entity instanceof EntityPlayer)
        {
            this.playerEntities.remove((EntityPlayer)par1Entity);
            this.updateAllPlayersSleepingFlag();
        }

        int var2 = par1Entity.chunkCoordX;
        int var3 = par1Entity.chunkCoordZ;

        if (par1Entity.addedToChunk && this.chunkExists(var2, var3))
        {
            this.getChunkFromChunkCoords(var2, var3).removeEntity(par1Entity);
        }

        this.loadedEntityList.remove(par1Entity);
        this.releaseEntitySkin(par1Entity);
    }

    /**
     * Adds a IWorldAccess to the list of worldAccesses
     */
    public void addWorldAccess(IWorldAccess par1IWorldAccess)
    {
        this.worldAccesses.add(par1IWorldAccess);
    }

    /**
     * Returns a list of bounding boxes that collide with aabb excluding the passed in entity's collision. Args: entity,
     * aabb
     */
    public List getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB)
    {
        this.collidingBoundingBoxes.clear();
        int var3 = MathHelper.floor_double(par2AxisAlignedBB.minX);
        int var4 = MathHelper.floor_double(par2AxisAlignedBB.maxX + 1.0D);
        int var5 = MathHelper.floor_double(par2AxisAlignedBB.minY);
        int var6 = MathHelper.floor_double(par2AxisAlignedBB.maxY + 1.0D);
        int var7 = MathHelper.floor_double(par2AxisAlignedBB.minZ);
        int var8 = MathHelper.floor_double(par2AxisAlignedBB.maxZ + 1.0D);

        for (int var9 = var3; var9 < var4; ++var9)
        {
            for (int var10 = var7; var10 < var8; ++var10)
            {
                if (this.blockExists(var9, 64, var10))
                {
                    for (int var11 = var5 - 1; var11 < var6; ++var11)
                    {
                        Block var12 = Block.blocksList[this.getBlockId(var9, var11, var10)];

                        if (var12 != null)
                        {
                            var12.getCollidingBoundingBoxes(this, var9, var11, var10, par2AxisAlignedBB, this.collidingBoundingBoxes);
                        }
                    }
                }
            }
        }

        double var14 = 0.25D;
        List var16 = this.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB.expand(var14, var14, var14));

        for (int var15 = 0; var15 < var16.size(); ++var15)
        {
            AxisAlignedBB var13 = ((Entity)var16.get(var15)).getBoundingBox();

            if (var13 != null && var13.intersectsWith(par2AxisAlignedBB))
            {
                this.collidingBoundingBoxes.add(var13);
            }

            var13 = par1Entity.getCollisionBox((Entity)var16.get(var15));

            if (var13 != null && var13.intersectsWith(par2AxisAlignedBB))
            {
                this.collidingBoundingBoxes.add(var13);
            }
        }

        return this.collidingBoundingBoxes;
    }

    /**
     * Returns the amount of skylight subtracted for the current time
     */
    public int calculateSkylightSubtracted(float par1)
    {
        float var2 = this.getCelestialAngle(par1);
        float var3 = 1.0F - (MathHelper.cos(var2 * (float)Math.PI * 2.0F) * 2.0F + 0.5F);

        if (var3 < 0.0F)
        {
            var3 = 0.0F;
        }

        if (var3 > 1.0F)
        {
            var3 = 1.0F;
        }

        var3 = 1.0F - var3;
        var3 = (float)((double)var3 * (1.0D - (double)(this.getRainStrength(par1) * 5.0F) / 16.0D));
        var3 = (float)((double)var3 * (1.0D - (double)(this.getWeightedThunderStrength(par1) * 5.0F) / 16.0D));
        var3 = 1.0F - var3;
        return (int)(var3 * 11.0F);
    }

    public float getCelestialAngle(float par1)
    {
        return this.worldProvider.calculateCelestialAngle(this.worldInfo.getWorldTime(), par1);
    }

    /**
     * Gets the height to which rain/snow will fall. Calculates it if not already stored.
     */
    public int getPrecipitationHeight(int par1, int par2)
    {
        return this.getChunkFromBlockCoords(par1, par2).getPrecipitationHeight(par1 & 15, par2 & 15);
    }

    /**
     * Finds the highest block on the x, z coordinate that is solid and returns its y coord. Args x, z
     */
    public int getTopSolidOrLiquidBlock(int par1, int par2)
    {
        Chunk var3 = this.getChunkFromBlockCoords(par1, par2);
        int var4 = var3.func_48561_g() + 15;
        if(var4 > 255)
        	var4 = 255;
        par1 &= 15;

        for (par2 &= 15; var4 > 0; --var4)
        {
            int var5 = var3.getBlockID(par1, var4, par2);

            if (var5 != 0 && Block.blocksList[var5].blockMaterial.blocksMovement() && Block.blocksList[var5].blockMaterial != Material.leaves)
            {
                return var4 + 1;
            }
        }

        return -1;
    }

    /**
     * Used to schedule a call to the updateTick method on the specified block.
     */
    public void scheduleBlockUpdate(int par1, int par2, int par3, int par4, int par5)
    {
        NextTickListEntry var6 = new NextTickListEntry(par1, par2, par3, par4);
        byte var7 = 8;

        if (this.scheduledUpdatesAreImmediate)
        {
            if (this.checkChunksExist(var6.xCoord - var7, var6.yCoord - var7, var6.zCoord - var7, var6.xCoord + var7, var6.yCoord + var7, var6.zCoord + var7))
            {
                int var8 = this.getBlockId(var6.xCoord, var6.yCoord, var6.zCoord);

                if (var8 == var6.blockID && var8 > 0)
                {
                    Block.blocksList[var8].updateTick(this, var6.xCoord, var6.yCoord, var6.zCoord, this.rand);
                }
            }
        }
        else
        {
            if (this.checkChunksExist(par1 - var7, par2 - var7, par3 - var7, par1 + var7, par2 + var7, par3 + var7))
            {
                if (par4 > 0)
                {
                    var6.setScheduledTime((long)par5 + this.worldInfo.getWorldTime());
                }

                if (!this.scheduledTickSet.contains(var6))
                {
                    this.scheduledTickSet.add(var6);
                    this.scheduledTickTreeSet.add(var6);
                }
            }
        }
    }

    /**
     * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
     */
    public void scheduleBlockUpdateFromLoad(int par1, int par2, int par3, int par4, int par5)
    {
        NextTickListEntry var6 = new NextTickListEntry(par1, par2, par3, par4);

        if (par4 > 0)
        {
            var6.setScheduledTime((long)par5 + this.worldInfo.getWorldTime());
        }

        if (!this.scheduledTickSet.contains(var6))
        {
            this.scheduledTickSet.add(var6);
            this.scheduledTickTreeSet.add(var6);
        }
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities()
    {
        Profiler.startSection("entities");
        Profiler.startSection("global");
        int var1;
        Entity var2;

        for (var1 = 0; var1 < this.weatherEffects.size(); ++var1)
        {
            var2 = (Entity)this.weatherEffects.get(var1);
            var2.onUpdate();

            if (var2.isDead)
            {
                this.weatherEffects.remove(var1--);
            }
        }

        Profiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int var3;
        int var4;

        for (var1 = 0; var1 < this.unloadedEntityList.size(); ++var1)
        {
            var2 = (Entity)this.unloadedEntityList.get(var1);
            var3 = var2.chunkCoordX;
            var4 = var2.chunkCoordZ;

            if (var2.addedToChunk && this.chunkExists(var3, var4))
            {
                this.getChunkFromChunkCoords(var3, var4).removeEntity(var2);
            }
        }

        for (var1 = 0; var1 < this.unloadedEntityList.size(); ++var1)
        {
            this.releaseEntitySkin((Entity)this.unloadedEntityList.get(var1));
        }

        this.unloadedEntityList.clear();
        Profiler.endStartSection("regular");

        for (var1 = 0; var1 < this.loadedEntityList.size(); ++var1)
        {
            var2 = (Entity)this.loadedEntityList.get(var1);

            if (var2.ridingEntity != null)
            {
                if (!var2.ridingEntity.isDead && var2.ridingEntity.riddenByEntity == var2)
                {
                    continue;
                }

                var2.ridingEntity.riddenByEntity = null;
                var2.ridingEntity = null;
            }

            if (!var2.isDead)
            {
                this.updateEntity(var2);
            }

            Profiler.startSection("remove");

            if (var2.isDead)
            {
                var3 = var2.chunkCoordX;
                var4 = var2.chunkCoordZ;

                if (var2.addedToChunk && this.chunkExists(var3, var4))
                {
                    this.getChunkFromChunkCoords(var3, var4).removeEntity(var2);
                }

                this.loadedEntityList.remove(var1--);
                this.releaseEntitySkin(var2);
            }

            Profiler.endSection();
        }

        Profiler.endStartSection("tileEntities");
        this.scanningTileEntities = true;
        Iterator var10 = this.loadedTileEntityList.iterator();

        while (var10.hasNext())
        {
            TileEntity var5 = (TileEntity)var10.next();

            if (!var5.isInvalid() && var5.worldObj != null && this.blockExists(var5.xCoord, var5.yCoord, var5.zCoord))
            {
                var5.updateEntity();
            }

            if (var5.isInvalid())
            {
                var10.remove();

                if (this.chunkExists(var5.xCoord >> 4, var5.zCoord >> 4))
                {
                    Chunk var7 = this.getChunkFromChunkCoords(var5.xCoord >> 4, var5.zCoord >> 4);

                    if (var7 != null)
                    {
                        var7.cleanChunkBlockTileEntity(var5.xCoord & 15, var5.yCoord, var5.zCoord & 15);
                    }
                }
            }
        }

        this.scanningTileEntities = false;

        if (!this.entityRemoval.isEmpty())
        {
            this.loadedTileEntityList.removeAll(this.entityRemoval);
            this.entityRemoval.clear();
        }

        Profiler.endStartSection("pendingTileEntities");

        if (!this.addedTileEntityList.isEmpty())
        {
            Iterator var6 = this.addedTileEntityList.iterator();

            while (var6.hasNext())
            {
                TileEntity var8 = (TileEntity)var6.next();

                if (!var8.isInvalid())
                {
                    if (!this.loadedTileEntityList.contains(var8))
                    {
                        this.loadedTileEntityList.add(var8);
                    }
                }
                else
                {
                    if (this.chunkExists(var8.xCoord >> 4, var8.zCoord >> 4))
                    {
                        Chunk var9 = this.getChunkFromChunkCoords(var8.xCoord >> 4, var8.zCoord >> 4);

                        if (var9 != null)
                        {
                            var9.cleanChunkBlockTileEntity(var8.xCoord & 15, var8.yCoord, var8.zCoord & 15);
                        }
                    }

                    this.markBlockNeedsUpdate(var8.xCoord, var8.yCoord, var8.zCoord);
                }
            }

            this.addedTileEntityList.clear();
        }

        Profiler.endSection();
        Profiler.endSection();
    }

    public void addTileEntity(Collection par1Collection)
    {
        List dest = scanningTileEntities ? addedTileEntityList : loadedTileEntityList;
        for(Object entity : par1Collection)
        {
            if(((TileEntity)entity).canUpdate()) 
            {
                dest.add(entity);
            }
        }
    }

    public void updateEntity(Entity par1Entity)
    {
        this.updateEntityWithOptionalForce(par1Entity, true);
    }

    public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2)
    {
        int var3 = MathHelper.floor_double(par1Entity.posX);
        int var4 = MathHelper.floor_double(par1Entity.posZ);
        byte var5 = 32;

        if (!par2 || this.checkChunksExist(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5))
        {
            par1Entity.lastTickPosX = par1Entity.posX;
            par1Entity.lastTickPosY = par1Entity.posY;
            par1Entity.lastTickPosZ = par1Entity.posZ;
            par1Entity.prevRotationYaw = par1Entity.rotationYaw;
            par1Entity.prevRotationPitch = par1Entity.rotationPitch;

            if (par2 && par1Entity.addedToChunk)
            {
                if (par1Entity.ridingEntity != null)
                {
                    par1Entity.updateRidden();
                }
                else
                {
                    par1Entity.onUpdate();
                }
            }

            Profiler.startSection("chunkCheck");

            if (Double.isNaN(par1Entity.posX) || Double.isInfinite(par1Entity.posX))
            {
                par1Entity.posX = par1Entity.lastTickPosX;
            }

            if (Double.isNaN(par1Entity.posY) || Double.isInfinite(par1Entity.posY))
            {
                par1Entity.posY = par1Entity.lastTickPosY;
            }

            if (Double.isNaN(par1Entity.posZ) || Double.isInfinite(par1Entity.posZ))
            {
                par1Entity.posZ = par1Entity.lastTickPosZ;
            }

            if (Double.isNaN((double)par1Entity.rotationPitch) || Double.isInfinite((double)par1Entity.rotationPitch))
            {
                par1Entity.rotationPitch = par1Entity.prevRotationPitch;
            }

            if (Double.isNaN((double)par1Entity.rotationYaw) || Double.isInfinite((double)par1Entity.rotationYaw))
            {
                par1Entity.rotationYaw = par1Entity.prevRotationYaw;
            }

            int var6 = MathHelper.floor_double(par1Entity.posX / 16.0D);
            int var7 = MathHelper.floor_double(par1Entity.posY / 16.0D);
            int var8 = MathHelper.floor_double(par1Entity.posZ / 16.0D);

            if (!par1Entity.addedToChunk || par1Entity.chunkCoordX != var6 || par1Entity.chunkCoordY != var7 || par1Entity.chunkCoordZ != var8)
            {
                if (par1Entity.addedToChunk && this.chunkExists(par1Entity.chunkCoordX, par1Entity.chunkCoordZ))
                {
                    this.getChunkFromChunkCoords(par1Entity.chunkCoordX, par1Entity.chunkCoordZ).removeEntityAtIndex(par1Entity, par1Entity.chunkCoordY);
                }

                if (this.chunkExists(var6, var8))
                {
                    par1Entity.addedToChunk = true;
                    this.getChunkFromChunkCoords(var6, var8).addEntity(par1Entity);
                }
                else
                {
                    par1Entity.addedToChunk = false;
                }
            }

            Profiler.endSection();

            if (par2 && par1Entity.addedToChunk && par1Entity.riddenByEntity != null)
            {
                if (!par1Entity.riddenByEntity.isDead && par1Entity.riddenByEntity.ridingEntity == par1Entity)
                {
                    this.updateEntity(par1Entity.riddenByEntity);
                }
                else
                {
                    par1Entity.riddenByEntity.ridingEntity = null;
                    par1Entity.riddenByEntity = null;
                }
            }
        }
    }

    public boolean checkIfAABBIsClear(AxisAlignedBB par1AxisAlignedBB)
    {
        List var2 = this.getEntitiesWithinAABBExcludingEntity((Entity)null, par1AxisAlignedBB);

        for (int var3 = 0; var3 < var2.size(); ++var3)
        {
            Entity var4 = (Entity)var2.get(var3);

            if (!var4.isDead && var4.preventEntitySpawning)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * checks to see if there are any blocks in the region constrained by an AxisAlignedBB
     */
    public boolean isAABBEmpty(AxisAlignedBB par1AxisAlignedBB)
    {
        int var2 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var3 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (par1AxisAlignedBB.minX < 0.0D)
        {
            --var2;
        }

        if (par1AxisAlignedBB.minY < 0.0D)
        {
            --var4;
        }

        if (par1AxisAlignedBB.minZ < 0.0D)
        {
            --var6;
        }

        for (int var8 = var2; var8 < var3; ++var8)
        {
            for (int var9 = var4; var9 < var5; ++var9)
            {
                for (int var10 = var6; var10 < var7; ++var10)
                {
                    Block var11 = Block.blocksList[this.getBlockId(var8, var9, var10)];

                    if (var11 != null)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns if any of the blocks within the aabb are liquids. Args: aabb
     */
    public boolean isAnyLiquid(AxisAlignedBB par1AxisAlignedBB)
    {
        int var2 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var3 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (par1AxisAlignedBB.minX < 0.0D)
        {
            --var2;
        }

        if (par1AxisAlignedBB.minY < 0.0D)
        {
            --var4;
        }

        if (par1AxisAlignedBB.minZ < 0.0D)
        {
            --var6;
        }

        for (int var8 = var2; var8 < var3; ++var8)
        {
            for (int var9 = var4; var9 < var5; ++var9)
            {
                for (int var10 = var6; var10 < var7; ++var10)
                {
                    Block var11 = Block.blocksList[this.getBlockId(var8, var9, var10)];

                    if (var11 != null && var11.blockMaterial.isLiquid())
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isBoundingBoxBurning(AxisAlignedBB par1AxisAlignedBB)
    {
        int var2 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var3 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (this.checkChunksExist(var2, var4, var6, var3, var5, var7))
        {
            for (int var8 = var2; var8 < var3; ++var8)
            {
                for (int var9 = var4; var9 < var5; ++var9)
                {
                    for (int var10 = var6; var10 < var7; ++var10)
                    {
                        int var11 = this.getBlockId(var8, var9, var10);

                        if (var11 == Block.fire.blockID || var11 == Block.lavaMoving.blockID || var11 == Block.lavaStill.blockID)
                        {
                            return true;
                        } 
                        else 
                        {
                            if (var11 > 0 && Block.blocksList[var11] != null && Block.blocksList[var11].isBlockBurning(this, var8, var9, var10))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * handles the acceleration of an object whilst in water. Not sure if it is used elsewhere.
     */
    public boolean handleMaterialAcceleration(AxisAlignedBB par1AxisAlignedBB, Material par2Material, Entity par3Entity)
    {
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int var8 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var9 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (!this.checkChunksExist(var4, var6, var8, var5, var7, var9))
        {
            return false;
        }
        else
        {
            boolean var10 = false;
            Vec3D var11 = Vec3D.createVector(0.0D, 0.0D, 0.0D);

            for (int var12 = var4; var12 < var5; ++var12)
            {
                for (int var13 = var6; var13 < var7; ++var13)
                {
                    for (int var14 = var8; var14 < var9; ++var14)
                    {
                        Block var15 = Block.blocksList[this.getBlockId(var12, var13, var14)];

                        if (var15 != null && var15.blockMaterial == par2Material)
                        {
                            double var16 = (double)((float)(var13 + 1) - BlockFluid.getFluidHeightPercent(this.getBlockMetadata(var12, var13, var14)));

                            if ((double)var7 >= var16)
                            {
                                var10 = true;
                                var15.velocityToAddToEntity(this, var12, var13, var14, par3Entity, var11);
                            }
                        }
                    }
                }
            }

            if (var11.lengthVector() > 0.0D)
            {
                var11 = var11.normalize();
                double var18 = 0.014D;
                par3Entity.motionX += var11.xCoord * var18;
                par3Entity.motionY += var11.yCoord * var18;
                par3Entity.motionZ += var11.zCoord * var18;
            }

            return var10;
        }
    }

    /**
     * Returns true if the given bounding box contains the given material
     */
    public boolean isMaterialInBB(AxisAlignedBB par1AxisAlignedBB, Material par2Material)
    {
        int var3 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var8 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int var9 = var3; var9 < var4; ++var9)
        {
            for (int var10 = var5; var10 < var6; ++var10)
            {
                for (int var11 = var7; var11 < var8; ++var11)
                {
                    Block var12 = Block.blocksList[this.getBlockId(var9, var10, var11)];

                    if (var12 != null && var12.blockMaterial == par2Material)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * checks if the given AABB is in the material given. Used while swimming.
     */
    public boolean isAABBInMaterial(AxisAlignedBB par1AxisAlignedBB, Material par2Material)
    {
        int var3 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var8 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int var9 = var3; var9 < var4; ++var9)
        {
            for (int var10 = var5; var10 < var6; ++var10)
            {
                for (int var11 = var7; var11 < var8; ++var11)
                {
                    Block var12 = Block.blocksList[this.getBlockId(var9, var10, var11)];

                    if (var12 != null && var12.blockMaterial == par2Material)
                    {
                        int var13 = this.getBlockMetadata(var9, var10, var11);
                        double var14 = (double)(var10 + 1);

                        if (var13 < 8)
                        {
                            var14 = (double)(var10 + 1) - (double)var13 / 8.0D;
                        }

                        if (var14 >= par1AxisAlignedBB.minY)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Explosion createExplosion(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
        return this.newExplosion(par1Entity, par2, par4, par6, par8, false);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9)
    {
        Explosion var10 = new Explosion(this, par1Entity, par2, par4, par6, par8);
        var10.isFlaming = par9;
        var10.doExplosionA();
        var10.doExplosionB(true);
        return var10;
    }

    /**
     * Gets the percentage of real blocks within within a bounding box, along a specified vector.
     */
    public float getBlockDensity(Vec3D par1Vec3D, AxisAlignedBB par2AxisAlignedBB)
    {
        double var3 = 1.0D / ((par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * 2.0D + 1.0D);
        double var5 = 1.0D / ((par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * 2.0D + 1.0D);
        double var7 = 1.0D / ((par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * 2.0D + 1.0D);
        int var9 = 0;
        int var10 = 0;

        for (float var11 = 0.0F; var11 <= 1.0F; var11 = (float)((double)var11 + var3))
        {
            for (float var12 = 0.0F; var12 <= 1.0F; var12 = (float)((double)var12 + var5))
            {
                for (float var13 = 0.0F; var13 <= 1.0F; var13 = (float)((double)var13 + var7))
                {
                    double var14 = par2AxisAlignedBB.minX + (par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * (double)var11;
                    double var16 = par2AxisAlignedBB.minY + (par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * (double)var12;
                    double var18 = par2AxisAlignedBB.minZ + (par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * (double)var13;

                    if (this.rayTraceBlocks(Vec3D.createVector(var14, var16, var18), par1Vec3D) == null)
                    {
                        ++var9;
                    }

                    ++var10;
                }
            }
        }

        return (float)var9 / (float)var10;
    }

    public boolean func_48093_a(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5)
    {
        if (par5 == 0)
        {
            --par3;
        }

        if (par5 == 1)
        {
            ++par3;
        }

        if (par5 == 2)
        {
            --par4;
        }

        if (par5 == 3)
        {
            ++par4;
        }

        if (par5 == 4)
        {
            --par2;
        }

        if (par5 == 5)
        {
            ++par2;
        }

        if (this.getBlockId(par2, par3, par4) == Block.fire.blockID)
        {
            this.playAuxSFXAtEntity(par1EntityPlayer, 1004, par2, par3, par4, 0);
            this.setBlockWithNotify(par2, par3, par4, 0);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getBlockTileEntity(int par1, int par2, int par3)
    {
        if (par2 >= 256)
        {
            return null;
        }
        else
        {
            Chunk var4 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);

            if (var4 == null)
            {
                return null;
            }
            else
            {
                TileEntity var5 = var4.getChunkBlockTileEntity(par1 & 15, par2, par3 & 15);

                if (var5 == null)
                {
                    Iterator var6 = this.addedTileEntityList.iterator();

                    while (var6.hasNext())
                    {
                        TileEntity var7 = (TileEntity)var6.next();

                        if (!var7.isInvalid() && var7.xCoord == par1 && var7.yCoord == par2 && var7.zCoord == par3)
                        {
                            var5 = var7;
                            break;
                        }
                    }
                }

                return var5;
            }
        }
    }

    /**
     * Sets the TileEntity for a given block in X, Y, Z coordinates
     */
    public void setBlockTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity)
    {
        if (par4TileEntity == null || par4TileEntity.isInvalid())
        {
            return;
        }
        List dest = scanningTileEntities ? addedTileEntityList : loadedTileEntityList;
        if (par4TileEntity.canUpdate())
        {
            dest.add(par4TileEntity);
        }
        
        Chunk var5 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);

        if (var5 != null)
        {
            var5.setChunkBlockTileEntity(par1 & 15, par2, par3 & 15, par4TileEntity);
        }
    }

    /**
     * Removes the TileEntity for a given block in X,Y,Z coordinates
     */
    public void removeBlockTileEntity(int par1, int par2, int par3)
    {
        Chunk var5 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
        if (var5 != null)
        {
            var5.removeChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Adds TileEntity to despawn list
     */
    public void markTileEntityForDespawn(TileEntity par1TileEntity)
    {
        this.entityRemoval.add(par1TileEntity);
    }

    public boolean isBlockOpaqueCube(int par1, int par2, int par3)
    {
        Block var4 = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return var4 == null ? false : var4.isOpaqueCube();
    }

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean isBlockNormalCube(int par1, int par2, int par3)
    {
        Block block = Block.blocksList[getBlockId(par1, par2, par3)];
        return block != null && block.isBlockNormalCube(this, par1, par2, par3);
    }

    /**
     * Checks if the block is a solid, normal cube. If the chunk does not exist, or is not loaded, it returns the
     * boolean parameter.
     */
    public boolean isBlockNormalCubeDefault(int par1, int par2, int par3, boolean par4)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            Chunk var5 = this.chunkProvider.provideChunk(par1 >> 4, par3 >> 4);

            if (var5 != null && !var5.isEmpty())
            {
                Block var6 = Block.blocksList[this.getBlockId(par1, par2, par3)];
                return var6 == null ? false : var6.blockMaterial.isOpaque() && var6.renderAsNormalBlock();
            }
            else
            {
                return par4;
            }
        }
        else
        {
            return par4;
        }
    }

    /**
     * Called on construction of the World class to setup the initial skylight values
     */
    public void calculateInitialSkylight()
    {
        int var1 = this.calculateSkylightSubtracted(1.0F);

        if (var1 != this.skylightSubtracted)
        {
            this.skylightSubtracted = var1;
        }
    }

    /**
     * first boolean for hostile mobs and second for peaceful mobs
     */
    public void setAllowedSpawnTypes(boolean par1, boolean par2)
    {
        this.spawnHostileMobs = par1;
        this.spawnPeacefulMobs = par2;
    }

    public void tick()
    {
        if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting < 3)
        {
            this.difficultySetting = 3;
        }

        this.worldProvider.worldChunkMgr.cleanupCache();
        this.updateWeather();
        long var2;

        if (this.isAllPlayersFullyAsleep())
        {
            boolean var1 = false;

            if (this.spawnHostileMobs && this.difficultySetting >= 1)
            {
                ;
            }

            if (!var1)
            {
                var2 = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(var2 - var2 % 24000L);
                this.wakeUpAllPlayers();
            }
        }

        Profiler.startSection("mobSpawner");
        SpawnerAnimals.performSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs && this.worldInfo.getWorldTime() % 400L == 0L);
        Profiler.endStartSection("chunkSource");
        this.chunkProvider.unload100OldestChunks();
        int var4 = this.calculateSkylightSubtracted(1.0F);

        if (var4 != this.skylightSubtracted)
        {
            this.skylightSubtracted = var4;
        }

        var2 = this.worldInfo.getWorldTime() + 1L;

        if (var2 % (long)this.autosavePeriod == 0L)
        {
            Profiler.endStartSection("save");
            this.saveWorld(false, (IProgressUpdate)null);
        }

        this.worldInfo.setWorldTime(var2);
        Profiler.endStartSection("tickPending");
        this.tickUpdates(false);
        Profiler.endStartSection("tickTiles");
        this.tickBlocksAndAmbiance();
        Profiler.endStartSection("village");
        this.villageCollectionObj.tick();
        this.villageSiegeObj.tick();
        Profiler.endSection();
    }

    /**
     * updates rainingStrength and thunderingStrength
     */
    private void calculateInitialWeather()
    {
        if (this.worldInfo.isRaining())
        {
            this.rainingStrength = 1.0F;

            if (this.worldInfo.isThundering())
            {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    /**
     * update's all weather states.
     */
    protected void updateWeather()
    {
        if (!this.worldProvider.hasNoSky)
        {
            if (this.lastLightningBolt > 0)
            {
                --this.lastLightningBolt;
            }

            int var1 = this.worldInfo.getThunderTime();

            if (var1 <= 0)
            {
                if (this.worldInfo.isThundering())
                {
                    this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                }
                else
                {
                    this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                }
            }
            else
            {
                --var1;
                this.worldInfo.setThunderTime(var1);

                if (var1 <= 0)
                {
                    this.worldInfo.setThundering(!this.worldInfo.isThundering());
                }
            }

            int var2 = this.worldInfo.getRainTime();

            if (var2 <= 0)
            {
                if (this.worldInfo.isRaining())
                {
                    this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                }
                else
                {
                    this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                }
            }
            else
            {
                --var2;
                this.worldInfo.setRainTime(var2);

                if (var2 <= 0)
                {
                    this.worldInfo.setRaining(!this.worldInfo.isRaining());
                }
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

    /**
     * stops all weather effects
     */
    private void clearWeather()
    {
        this.worldInfo.setRainTime(0);
        this.worldInfo.setRaining(false);
        this.worldInfo.setThunderTime(0);
        this.worldInfo.setThundering(false);
    }

    /**
     * start precipitation in this world (2 ticks after command posted)
     */
    public void commandToggleDownfall()
    {
        this.worldInfo.setRainTime(1);
    }

    protected void func_48090_k()
    {
        this.activeChunkSet.clear();
        Profiler.startSection("buildList");
        int var1;
        EntityPlayer var2;
        int var3;
        int var4;

        for (var1 = 0; var1 < this.playerEntities.size(); ++var1)
        {
            var2 = (EntityPlayer)this.playerEntities.get(var1);
            var3 = MathHelper.floor_double(var2.posX / 16.0D);
            var4 = MathHelper.floor_double(var2.posZ / 16.0D);
            byte var5 = 7;

            for (int var6 = -var5; var6 <= var5; ++var6)
            {
                for (int var7 = -var5; var7 <= var5; ++var7)
                {
                    this.activeChunkSet.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
                }
            }
        }
        ForgeHooks.addActiveChunks(this, activeChunkSet);

        Profiler.endSection();

        if (this.ambientTickCountdown > 0)
        {
            --this.ambientTickCountdown;
        }

        Profiler.startSection("playerCheckLight");

        if (!this.playerEntities.isEmpty())
        {
            var1 = this.rand.nextInt(this.playerEntities.size());
            var2 = (EntityPlayer)this.playerEntities.get(var1);
            var3 = MathHelper.floor_double(var2.posX) + this.rand.nextInt(11) - 5;
            var4 = MathHelper.floor_double(var2.posY) + this.rand.nextInt(11) - 5;
            int var8 = MathHelper.floor_double(var2.posZ) + this.rand.nextInt(11) - 5;
            this.updateAllLightTypes(var3, var4, var8);
        }

        Profiler.endSection();
    }

    protected void func_48094_a(int par1, int par2, Chunk par3Chunk)
    {
        Profiler.endStartSection("tickChunk");
        par3Chunk.updateSkylight();
        Profiler.endStartSection("moodSound");

        if (this.ambientTickCountdown == 0)
        {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int var4 = this.updateLCG >> 2;
            int var5 = var4 & 15;
            int var6 = var4 >> 8 & 15;
            int var7 = var4 >> 16 & 127;
            int var8 = par3Chunk.getBlockID(var5, var7, var6);
            var5 += par1;
            var6 += par2;

            if (var8 == 0 && this.getFullBlockLightValue(var5, var7, var6) <= this.rand.nextInt(8) && this.getSavedLightValue(EnumSkyBlock.Sky, var5, var7, var6) <= 0)
            {
                EntityPlayer var9 = this.getClosestPlayer((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, 8.0D);

                if (var9 != null && var9.getDistanceSq((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D) > 4.0D)
                {
                    this.playSoundEffect((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                    this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                }
            }
        }

        Profiler.endStartSection("checkLight");
        par3Chunk.func_48557_n();
    }

    /**
     * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
     * player
     */
    protected void tickBlocksAndAmbiance()
    {
        this.func_48090_k();
        int var1 = 0;
        int var2 = 0;
        Iterator var3 = this.activeChunkSet.iterator();

        while (var3.hasNext())
        {
            ChunkCoordIntPair var4 = (ChunkCoordIntPair)var3.next();
            int var5 = var4.chunkXPos * 16;
            int var6 = var4.chunkZPos * 16;
            Profiler.startSection("getChunk");
            Chunk var7 = this.getChunkFromChunkCoords(var4.chunkXPos, var4.chunkZPos);
            this.func_48094_a(var5, var6, var7);
            Profiler.endStartSection("thunder");
            int var8;
            int var9;
            int var10;
            int var11;

            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering())
            {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                var8 = this.updateLCG >> 2;
                var9 = var5 + (var8 & 15);
                var10 = var6 + (var8 >> 8 & 15);
                var11 = this.getPrecipitationHeight(var9, var10);

                if (this.canLightningStrikeAt(var9, var11, var10))
                {
                    this.addWeatherEffect(new EntityLightningBolt(this, (double)var9, (double)var11, (double)var10));
                    this.lastLightningBolt = 2;
                }
            }

            Profiler.endStartSection("iceandsnow");

            if (this.rand.nextInt(16) == 0)
            {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                var8 = this.updateLCG >> 2;
                var9 = var8 & 15;
                var10 = var8 >> 8 & 15;
                var11 = this.getPrecipitationHeight(var9 + var5, var10 + var6);

                if (this.isBlockHydratedIndirectly(var9 + var5, var11 - 1, var10 + var6))
                {
                    this.setBlockWithNotify(var9 + var5, var11 - 1, var10 + var6, Block.ice.blockID);
                }

                if (this.isRaining() && this.canSnowAt(var9 + var5, var11, var10 + var6))
                {
                    this.setBlockWithNotify(var9 + var5, var11, var10 + var6, Block.snow.blockID);
                }
            }

            Profiler.endStartSection("tickTiles");
            ExtendedBlockStorage[] var19 = var7.func_48553_h();
            var9 = var19.length;

            for (var10 = 0; var10 < var9; ++var10)
            {
                ExtendedBlockStorage var20 = var19[var10];

                if (var20 != null && var20.func_48607_b())
                {
                    for (int var12 = 0; var12 < 3; ++var12)
                    {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int var13 = this.updateLCG >> 2;
                        int var14 = var13 & 15;
                        int var15 = var13 >> 8 & 15;
                        int var16 = var13 >> 16 & 15;
                        int var17 = var20.func_48591_a(var14, var16, var15);
                        ++var2;
                        Block var18 = Block.blocksList[var17];

                        if (var18 != null && var18.func_48125_m())
                        {
                            ++var1;
                            var18.updateTick(this, var14 + var5, var16 + var20.func_48597_c(), var15 + var6, this.rand);
                        }
                    }
                }
            }

            Profiler.endSection();
        }
    }

    /**
     * Checks if the block is hydrating itself.
     */
    public boolean isBlockHydratedDirectly(int par1, int par2, int par3)
    {
        return this.isBlockHydrated(par1, par2, par3, false);
    }

    /**
     * Check if the block is being hydrated by an adjacent block.
     */
    public boolean isBlockHydratedIndirectly(int par1, int par2, int par3)
    {
        return this.isBlockHydrated(par1, par2, par3, true);
    }

    /**
     * (I think)
     */
    public boolean isBlockHydrated(int par1, int par2, int par3, boolean par4)
    {
        BiomeGenBase var5 = this.func_48091_a(par1, par3);
        float var6 = var5.getFloatTemperature();

        if (var6 > 0.15F)
        {
            return false;
        }
        else
        {
            if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10)
            {
                int var7 = this.getBlockId(par1, par2, par3);

                if ((var7 == Block.waterStill.blockID || var7 == Block.waterMoving.blockID) && this.getBlockMetadata(par1, par2, par3) == 0)
                {
                    if (!par4)
                    {
                        return true;
                    }

                    boolean var8 = true;

                    if (var8 && this.getBlockMaterial(par1 - 1, par2, par3) != Material.water)
                    {
                        var8 = false;
                    }

                    if (var8 && this.getBlockMaterial(par1 + 1, par2, par3) != Material.water)
                    {
                        var8 = false;
                    }

                    if (var8 && this.getBlockMaterial(par1, par2, par3 - 1) != Material.water)
                    {
                        var8 = false;
                    }

                    if (var8 && this.getBlockMaterial(par1, par2, par3 + 1) != Material.water)
                    {
                        var8 = false;
                    }

                    if (!var8)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * Tests whether or not snow can be placed at a given location
     */
    public boolean canSnowAt(int par1, int par2, int par3)
    {
        BiomeGenBase var4 = this.func_48091_a(par1, par3);
        float var5 = var4.getFloatTemperature();

        if (var5 > 0.15F)
        {
            return false;
        }
        else
        {
            if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10)
            {
                int var6 = this.getBlockId(par1, par2 - 1, par3);
                int var7 = this.getBlockId(par1, par2, par3);

                if (var7 == 0 && Block.snow.canPlaceBlockAt(this, par1, par2, par3) && var6 != 0 && var6 != Block.ice.blockID && Block.blocksList[var6].blockMaterial.blocksMovement())
                {
                    return true;
                }
            }

            return false;
        }
    }

    public void updateAllLightTypes(int par1, int par2, int par3)
    {
        if (!this.worldProvider.hasNoSky)
        {
            this.updateLightByType(EnumSkyBlock.Sky, par1, par2, par3);
        }

        this.updateLightByType(EnumSkyBlock.Block, par1, par2, par3);
    }

    private int computeSkyLightValue(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = 0;

        if (this.canBlockSeeTheSky(par2, par3, par4))
        {
            var7 = 15;
        }
        else
        {
            if (par6 == 0)
            {
                par6 = 1;
            }

            int var8 = this.getSavedLightValue(EnumSkyBlock.Sky, par2 - 1, par3, par4) - par6;
            int var9 = this.getSavedLightValue(EnumSkyBlock.Sky, par2 + 1, par3, par4) - par6;
            int var10 = this.getSavedLightValue(EnumSkyBlock.Sky, par2, par3 - 1, par4) - par6;
            int var11 = this.getSavedLightValue(EnumSkyBlock.Sky, par2, par3 + 1, par4) - par6;
            int var12 = this.getSavedLightValue(EnumSkyBlock.Sky, par2, par3, par4 - 1) - par6;
            int var13 = this.getSavedLightValue(EnumSkyBlock.Sky, par2, par3, par4 + 1) - par6;

            if (var8 > var7)
            {
                var7 = var8;
            }

            if (var9 > var7)
            {
                var7 = var9;
            }

            if (var10 > var7)
            {
                var7 = var10;
            }

            if (var11 > var7)
            {
                var7 = var11;
            }

            if (var12 > var7)
            {
                var7 = var12;
            }

            if (var13 > var7)
            {
                var7 = var13;
            }
        }

        return var7;
    }

    private int computeBlockLightValue(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        int var7 = (par5 == 0 || Block.blocksList[par5] == null ? 0 : Block.blocksList[par5].getLightValue(this, par2, par3, par4));
        int var8 = this.getSavedLightValue(EnumSkyBlock.Block, par2 - 1, par3, par4) - par6;
        int var9 = this.getSavedLightValue(EnumSkyBlock.Block, par2 + 1, par3, par4) - par6;
        int var10 = this.getSavedLightValue(EnumSkyBlock.Block, par2, par3 - 1, par4) - par6;
        int var11 = this.getSavedLightValue(EnumSkyBlock.Block, par2, par3 + 1, par4) - par6;
        int var12 = this.getSavedLightValue(EnumSkyBlock.Block, par2, par3, par4 - 1) - par6;
        int var13 = this.getSavedLightValue(EnumSkyBlock.Block, par2, par3, par4 + 1) - par6;

        if (var8 > var7)
        {
            var7 = var8;
        }

        if (var9 > var7)
        {
            var7 = var9;
        }

        if (var10 > var7)
        {
            var7 = var10;
        }

        if (var11 > var7)
        {
            var7 = var11;
        }

        if (var12 > var7)
        {
            var7 = var12;
        }

        if (var13 > var7)
        {
            var7 = var13;
        }

        return var7;
    }

    public void updateLightByType(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (this.doChunksNearChunkExist(par2, par3, par4, 17))
        {
            int var5 = 0;
            int var6 = 0;
            Profiler.startSection("getBrightness");
            int var7 = this.getSavedLightValue(par1EnumSkyBlock, par2, par3, par4);
            boolean var8 = false;
            int var10 = this.getBlockId(par2, par3, par4);
            int var11 = this.func_48092_f(par2, par3, par4);

            if (var11 == 0)
            {
                var11 = 1;
            }

            boolean var12 = false;
            int var25;

            if (par1EnumSkyBlock == EnumSkyBlock.Sky)
            {
                var25 = this.computeSkyLightValue(var7, par2, par3, par4, var10, var11);
            }
            else
            {
                var25 = this.computeBlockLightValue(var7, par2, par3, par4, var10, var11);
            }

            int var9;
            int var13;
            int var14;
            int var15;
            int var17;
            int var16;

            if (var25 > var7)
            {
                this.lightUpdateBlockList[var6++] = 133152;
            }
            else if (var25 < var7)
            {
                if (par1EnumSkyBlock != EnumSkyBlock.Block)
                {
                    ;
                }

                this.lightUpdateBlockList[var6++] = 133152 + (var7 << 18);

                while (var5 < var6)
                {
                    var9 = this.lightUpdateBlockList[var5++];
                    var10 = (var9 & 63) - 32 + par2;
                    var11 = (var9 >> 6 & 63) - 32 + par3;
                    var25 = (var9 >> 12 & 63) - 32 + par4;
                    var13 = var9 >> 18 & 15;
                    var14 = this.getSavedLightValue(par1EnumSkyBlock, var10, var11, var25);

                    if (var14 == var13)
                    {
                        this.setLightValue(par1EnumSkyBlock, var10, var11, var25, 0);

                        if (var13 > 0)
                        {
                            var15 = var10 - par2;
                            var16 = var11 - par3;
                            var17 = var25 - par4;

                            if (var15 < 0)
                            {
                                var15 = -var15;
                            }

                            if (var16 < 0)
                            {
                                var16 = -var16;
                            }

                            if (var17 < 0)
                            {
                                var17 = -var17;
                            }

                            if (var15 + var16 + var17 < 17)
                            {
                                for (int var18 = 0; var18 < 6; ++var18)
                                {
                                    int var19 = var18 % 2 * 2 - 1;
                                    int var20 = var10 + var18 / 2 % 3 / 2 * var19;
                                    int var21 = var11 + (var18 / 2 + 1) % 3 / 2 * var19;
                                    int var22 = var25 + (var18 / 2 + 2) % 3 / 2 * var19;
                                    var14 = this.getSavedLightValue(par1EnumSkyBlock, var20, var21, var22);
                                    int var23 = Block.lightOpacity[this.getBlockId(var20, var21, var22)];

                                    if (var23 == 0)
                                    {
                                        var23 = 1;
                                    }

                                    if (var14 == var13 - var23 && var6 < this.lightUpdateBlockList.length)
                                    {
                                        this.lightUpdateBlockList[var6++] = var20 - par2 + 32 + (var21 - par3 + 32 << 6) + (var22 - par4 + 32 << 12) + (var13 - var23 << 18);
                                    }
                                }
                            }
                        }
                    }
                }

                var5 = 0;
            }

            Profiler.endSection();
            Profiler.startSection("tcp < tcc");

            while (var5 < var6)
            {
                var7 = this.lightUpdateBlockList[var5++];
                int var24 = (var7 & 63) - 32 + par2;
                var9 = (var7 >> 6 & 63) - 32 + par3;
                var10 = (var7 >> 12 & 63) - 32 + par4;
                var11 = this.getSavedLightValue(par1EnumSkyBlock, var24, var9, var10);
                var25 = this.getBlockId(var24, var9, var10);
                var13 = Block.lightOpacity[var25];

                if (var13 == 0)
                {
                    var13 = 1;
                }

                boolean var26 = false;

                if (par1EnumSkyBlock == EnumSkyBlock.Sky)
                {
                    var14 = this.computeSkyLightValue(var11, var24, var9, var10, var25, var13);
                }
                else
                {
                    var14 = this.computeBlockLightValue(var11, var24, var9, var10, var25, var13);
                }

                if (var14 != var11)
                {
                    this.setLightValue(par1EnumSkyBlock, var24, var9, var10, var14);

                    if (var14 > var11)
                    {
                        var15 = var24 - par2;
                        var16 = var9 - par3;
                        var17 = var10 - par4;

                        if (var15 < 0)
                        {
                            var15 = -var15;
                        }

                        if (var16 < 0)
                        {
                            var16 = -var16;
                        }

                        if (var17 < 0)
                        {
                            var17 = -var17;
                        }

                        if (var15 + var16 + var17 < 17 && var6 < this.lightUpdateBlockList.length - 6)
                        {
                            if (this.getSavedLightValue(par1EnumSkyBlock, var24 - 1, var9, var10) < var14)
                            {
                                this.lightUpdateBlockList[var6++] = var24 - 1 - par2 + 32 + (var9 - par3 + 32 << 6) + (var10 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, var24 + 1, var9, var10) < var14)
                            {
                                this.lightUpdateBlockList[var6++] = var24 + 1 - par2 + 32 + (var9 - par3 + 32 << 6) + (var10 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, var24, var9 - 1, var10) < var14)
                            {
                                this.lightUpdateBlockList[var6++] = var24 - par2 + 32 + (var9 - 1 - par3 + 32 << 6) + (var10 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, var24, var9 + 1, var10) < var14)
                            {
                                this.lightUpdateBlockList[var6++] = var24 - par2 + 32 + (var9 + 1 - par3 + 32 << 6) + (var10 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, var24, var9, var10 - 1) < var14)
                            {
                                this.lightUpdateBlockList[var6++] = var24 - par2 + 32 + (var9 - par3 + 32 << 6) + (var10 - 1 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, var24, var9, var10 + 1) < var14)
                            {
                                this.lightUpdateBlockList[var6++] = var24 - par2 + 32 + (var9 - par3 + 32 << 6) + (var10 + 1 - par4 + 32 << 12);
                            }
                        }
                    }
                }
            }

            Profiler.endSection();
        }
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(boolean par1)
    {
        int var2 = this.scheduledTickTreeSet.size();

        if (var2 != this.scheduledTickSet.size())
        {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        else
        {
            if (var2 > 1000)
            {
                var2 = 1000;
            }

            for (int var3 = 0; var3 < var2; ++var3)
            {
                NextTickListEntry var4 = (NextTickListEntry)this.scheduledTickTreeSet.first();

                if (!par1 && var4.scheduledTime > this.worldInfo.getWorldTime())
                {
                    break;
                }

                this.scheduledTickTreeSet.remove(var4);
                this.scheduledTickSet.remove(var4);
                byte var5 = 8;

                if (this.checkChunksExist(var4.xCoord - var5, var4.yCoord - var5, var4.zCoord - var5, var4.xCoord + var5, var4.yCoord + var5, var4.zCoord + var5))
                {
                    int var6 = this.getBlockId(var4.xCoord, var4.yCoord, var4.zCoord);

                    if (var6 == var4.blockID && var6 > 0)
                    {
                        Block.blocksList[var6].updateTick(this, var4.xCoord, var4.yCoord, var4.zCoord, this.rand);
                    }
                }
            }

            return this.scheduledTickTreeSet.size() != 0;
        }
    }

    public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2)
    {
        ArrayList var3 = null;
        ChunkCoordIntPair var4 = par1Chunk.getChunkCoordIntPair();
        int var5 = var4.chunkXPos << 4;
        int var6 = var5 + 16;
        int var7 = var4.chunkZPos << 4;
        int var8 = var7 + 16;
        Iterator var9 = this.scheduledTickSet.iterator();

        while (var9.hasNext())
        {
            NextTickListEntry var10 = (NextTickListEntry)var9.next();

            if (var10.xCoord >= var5 && var10.xCoord < var6 && var10.zCoord >= var7 && var10.zCoord < var8)
            {
                if (par2)
                {
                    this.scheduledTickTreeSet.remove(var10);
                    var9.remove();
                }

                if (var3 == null)
                {
                    var3 = new ArrayList();
                }

                var3.add(var10);
            }
        }

        return var3;
    }

    /**
     * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
     */
    public List getEntitiesWithinAABBExcludingEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB)
    {
        this.entitiesWithinAABBExcludingEntity.clear();
        int var3 = MathHelper.floor_double((par2AxisAlignedBB.minX - 2.0D) / 16.0D);
        int var4 = MathHelper.floor_double((par2AxisAlignedBB.maxX + 2.0D) / 16.0D);
        int var5 = MathHelper.floor_double((par2AxisAlignedBB.minZ - 2.0D) / 16.0D);
        int var6 = MathHelper.floor_double((par2AxisAlignedBB.maxZ + 2.0D) / 16.0D);

        for (int var7 = var3; var7 <= var4; ++var7)
        {
            for (int var8 = var5; var8 <= var6; ++var8)
            {
                if (this.chunkExists(var7, var8))
                {
                    this.getChunkFromChunkCoords(var7, var8).getEntitiesWithinAABBForEntity(par1Entity, par2AxisAlignedBB, this.entitiesWithinAABBExcludingEntity);
                }
            }
        }

        return this.entitiesWithinAABBExcludingEntity;
    }

    /**
     * Returns all entities of the specified class type which intersect with the AABB. Args: entityClass, aabb
     */
    public List getEntitiesWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB)
    {
        int var3 = MathHelper.floor_double((par2AxisAlignedBB.minX - 2.0D) / 16.0D);
        int var4 = MathHelper.floor_double((par2AxisAlignedBB.maxX + 2.0D) / 16.0D);
        int var5 = MathHelper.floor_double((par2AxisAlignedBB.minZ - 2.0D) / 16.0D);
        int var6 = MathHelper.floor_double((par2AxisAlignedBB.maxZ + 2.0D) / 16.0D);
        ArrayList var7 = new ArrayList();

        for (int var8 = var3; var8 <= var4; ++var8)
        {
            for (int var9 = var5; var9 <= var6; ++var9)
            {
                if (this.chunkExists(var8, var9))
                {
                    this.getChunkFromChunkCoords(var8, var9).getEntitiesOfTypeWithinAAAB(par1Class, par2AxisAlignedBB, var7);
                }
            }
        }

        return var7;
    }

    public Entity findNearestEntityWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, Entity par3Entity)
    {
        List var4 = this.getEntitiesWithinAABB(par1Class, par2AxisAlignedBB);
        Entity var5 = null;
        double var6 = Double.MAX_VALUE;
        Iterator var8 = var4.iterator();

        while (var8.hasNext())
        {
            Entity var9 = (Entity)var8.next();

            if (var9 != par3Entity)
            {
                double var10 = par3Entity.getDistanceSqToEntity(var9);

                if (var10 <= var6)
                {
                    var5 = var9;
                    var6 = var10;
                }
            }
        }

        return var5;
    }

    /**
     * marks the chunk that contains this tilentity as modified and then calls worldAccesses.doNothingWithTileEntity
     */
    public void updateTileEntityChunkAndDoNothing(int par1, int par2, int par3, TileEntity par4TileEntity)
    {
        if (this.blockExists(par1, par2, par3))
        {
            this.getChunkFromBlockCoords(par1, par3).setChunkModified();
        }

        for (int var5 = 0; var5 < this.worldAccesses.size(); ++var5)
        {
            ((IWorldAccess)this.worldAccesses.get(var5)).doNothingWithTileEntity(par1, par2, par3, par4TileEntity);
        }
    }

    /**
     * Counts how many entities of an entity class exist in the world. Args: entityClass
     */
    public int countEntities(Class par1Class)
    {
        int var2 = 0;

        for (int var3 = 0; var3 < this.loadedEntityList.size(); ++var3)
        {
            Entity var4 = (Entity)this.loadedEntityList.get(var3);

            if (par1Class.isAssignableFrom(var4.getClass()))
            {
                ++var2;
            }
        }

        return var2;
    }

    /**
     * adds entities to the loaded entities list, and loads thier skins.
     */
    public void addLoadedEntities(List par1List)
    {
        this.loadedEntityList.addAll(par1List);

        for (int var2 = 0; var2 < par1List.size(); ++var2)
        {
            this.obtainEntitySkin((Entity)par1List.get(var2));
        }
    }

    /**
     * adds entities to the list of unloaded entities
     */
    public void unloadEntities(List par1List)
    {
        this.unloadedEntityList.addAll(par1List);
    }

    public boolean canBlockBePlacedAt(int par1, int par2, int par3, int par4, boolean par5, int par6)
    {
        int var7 = this.getBlockId(par2, par3, par4);
        Block var8 = Block.blocksList[var7];
        Block var9 = Block.blocksList[par1];
        AxisAlignedBB var10 = var9.getCollisionBoundingBoxFromPool(this, par2, par3, par4);

        if (par5)
        {
            var10 = null;
        }

        if (var10 != null && !this.checkIfAABBIsClear(var10))
        {
            return false;
        }
        else
        {
            if (var8 != null && (var8 == Block.waterMoving || var8 == Block.waterStill || var8 == Block.lavaMoving || var8 == Block.lavaStill || var8 == Block.fire || var8.blockMaterial.isGroundCover()))
            {
                var8 = null;
            }
            
            if (var8 != null && var8.isBlockReplaceable(this, par2, par3, par4))
            {
                var8 = null;
            }

            return par1 > 0 && var8 == null && var9.canPlaceBlockOnSide(this, par2, par3, par4, par6);
        }
    }

    public PathEntity getPathEntityToEntity(Entity par1Entity, Entity par2Entity, float par3, boolean par4, boolean par5, boolean par6, boolean par7)
    {
        Profiler.startSection("pathfind");
        int var8 = MathHelper.floor_double(par1Entity.posX);
        int var9 = MathHelper.floor_double(par1Entity.posY + 1.0D);
        int var10 = MathHelper.floor_double(par1Entity.posZ);
        int var11 = (int)(par3 + 16.0F);
        int var12 = var8 - var11;
        int var13 = var9 - var11;
        int var14 = var10 - var11;
        int var15 = var8 + var11;
        int var16 = var9 + var11;
        int var17 = var10 + var11;
        ChunkCache var18 = new ChunkCache(this, var12, var13, var14, var15, var16, var17);
        PathEntity var19 = (new PathFinder(var18, par4, par5, par6, par7)).createEntityPathTo(par1Entity, par2Entity, par3);
        Profiler.endSection();
        return var19;
    }

    public PathEntity getEntityPathToXYZ(Entity par1Entity, int par2, int par3, int par4, float par5, boolean par6, boolean par7, boolean par8, boolean par9)
    {
        Profiler.startSection("pathfind");
        int var10 = MathHelper.floor_double(par1Entity.posX);
        int var11 = MathHelper.floor_double(par1Entity.posY);
        int var12 = MathHelper.floor_double(par1Entity.posZ);
        int var13 = (int)(par5 + 8.0F);
        int var14 = var10 - var13;
        int var15 = var11 - var13;
        int var16 = var12 - var13;
        int var17 = var10 + var13;
        int var18 = var11 + var13;
        int var19 = var12 + var13;
        ChunkCache var20 = new ChunkCache(this, var14, var15, var16, var17, var18, var19);
        PathEntity var21 = (new PathFinder(var20, par6, par7, par8, par9)).createEntityPathTo(par1Entity, par2, par3, par4, par5);
        Profiler.endSection();
        return var21;
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    public boolean isBlockProvidingPowerTo(int par1, int par2, int par3, int par4)
    {
        int var5 = this.getBlockId(par1, par2, par3);
        return var5 == 0 ? false : Block.blocksList[var5].isIndirectlyPoweringTo(this, par1, par2, par3, par4);
    }

    /**
     * Whether one of the neighboring blocks is putting power into this block. Args: x, y, z
     */
    public boolean isBlockGettingPowered(int par1, int par2, int par3)
    {
        return this.isBlockProvidingPowerTo(par1, par2 - 1, par3, 0) ? true : (this.isBlockProvidingPowerTo(par1, par2 + 1, par3, 1) ? true : (this.isBlockProvidingPowerTo(par1, par2, par3 - 1, 2) ? true : (this.isBlockProvidingPowerTo(par1, par2, par3 + 1, 3) ? true : (this.isBlockProvidingPowerTo(par1 - 1, par2, par3, 4) ? true : this.isBlockProvidingPowerTo(par1 + 1, par2, par3, 5)))));
    }

    /**
     * Is a block next to you getting powered (if its an attachable block) or is it providing power directly to you.
     * Args: x, y, z, direction
     */
    public boolean isBlockIndirectlyProvidingPowerTo(int par1, int par2, int par3, int par4)
    {
        if (this.isBlockNormalCube(par1, par2, par3))
        {
            return this.isBlockGettingPowered(par1, par2, par3);
        }
        else
        {
            int var5 = this.getBlockId(par1, par2, par3);
            return var5 == 0 ? false : Block.blocksList[var5].isPoweringTo(this, par1, par2, par3, par4);
        }
    }

    /**
     * Used to see if one of the blocks next to you or your block is getting power from a neighboring block. Used by
     * items like TNT or Doors so they don't have redstone going straight into them.  Args: x, y, z
     */
    public boolean isBlockIndirectlyGettingPowered(int par1, int par2, int par3)
    {
        return this.isBlockIndirectlyProvidingPowerTo(par1, par2 - 1, par3, 0) ? true : (this.isBlockIndirectlyProvidingPowerTo(par1, par2 + 1, par3, 1) ? true : (this.isBlockIndirectlyProvidingPowerTo(par1, par2, par3 - 1, 2) ? true : (this.isBlockIndirectlyProvidingPowerTo(par1, par2, par3 + 1, 3) ? true : (this.isBlockIndirectlyProvidingPowerTo(par1 - 1, par2, par3, 4) ? true : this.isBlockIndirectlyProvidingPowerTo(par1 + 1, par2, par3, 5)))));
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     * Args: entity, dist
     */
    public EntityPlayer getClosestPlayerToEntity(Entity par1Entity, double par2)
    {
        return this.getClosestPlayer(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par2);
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not
     * limit the distance). Args: x, y, z, dist
     */
    public EntityPlayer getClosestPlayer(double par1, double par3, double par5, double par7)
    {
        double var9 = -1.0D;
        EntityPlayer var11 = null;

        for (int var12 = 0; var12 < this.playerEntities.size(); ++var12)
        {
            EntityPlayer var13 = (EntityPlayer)this.playerEntities.get(var12);
            double var14 = var13.getDistanceSq(par1, par3, par5);

            if ((par7 < 0.0D || var14 < par7 * par7) && (var9 == -1.0D || var14 < var9))
            {
                var9 = var14;
                var11 = var13;
            }
        }

        return var11;
    }

    public EntityPlayer func_48087_a(double par1, double par3, double par5)
    {
        double var7 = -1.0D;
        EntityPlayer var9 = null;

        for (int var10 = 0; var10 < this.playerEntities.size(); ++var10)
        {
            EntityPlayer var11 = (EntityPlayer)this.playerEntities.get(var10);
            double var12 = var11.getDistanceSq(par1, var11.posY, par3);

            if ((par5 < 0.0D || var12 < par5 * par5) && (var7 == -1.0D || var12 < var7))
            {
                var7 = var12;
                var9 = var11;
            }
        }

        return var9;
    }

    /**
     * Returns the closest vulnerable player to this entity within the given radius, or null if none is found
     */
    public EntityPlayer getClosestVulnerablePlayerToEntity(Entity par1Entity, double par2)
    {
        return this.getClosestVulnerablePlayer(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par2);
    }

    /**
     * Returns the closest vulnerable player within the given radius, or null if none is found.
     */
    public EntityPlayer getClosestVulnerablePlayer(double par1, double par3, double par5, double par7)
    {
        double var9 = -1.0D;
        EntityPlayer var11 = null;

        for (int var12 = 0; var12 < this.playerEntities.size(); ++var12)
        {
            EntityPlayer var13 = (EntityPlayer)this.playerEntities.get(var12);

            if (!var13.capabilities.disableDamage)
            {
                double var14 = var13.getDistanceSq(par1, par3, par5);

                if ((par7 < 0.0D || var14 < par7 * par7) && (var9 == -1.0D || var14 < var9))
                {
                    var9 = var14;
                    var11 = var13;
                }
            }
        }

        return var11;
    }

    /**
     * Find a player by name in this world.
     */
    public EntityPlayer getPlayerEntityByName(String par1Str)
    {
        for (int var2 = 0; var2 < this.playerEntities.size(); ++var2)
        {
            if (par1Str.equals(((EntityPlayer)this.playerEntities.get(var2)).username))
            {
                return (EntityPlayer)this.playerEntities.get(var2);
            }
        }

        return null;
    }

    /**
     * Checks whether the session lock file was modified by another process
     */
    public void checkSessionLock()
    {
        this.saveHandler.checkSessionLock();
    }

    public void setWorldTime(long par1)
    {
        this.worldInfo.setWorldTime(par1);
    }

    /**
     * Gradually advances the time of the world.
     */
    public void advanceTime(long par1)
    {
        long var3 = par1 - this.worldInfo.getWorldTime();
        NextTickListEntry var6;

        for (Iterator var5 = this.scheduledTickSet.iterator(); var5.hasNext(); var6.scheduledTime += var3)
        {
            var6 = (NextTickListEntry)var5.next();
        }

        this.setWorldTime(par1);
    }

    /**
     * gets the random world seed
     */
    public long getSeed()
    {
        return this.worldInfo.getSeed();
    }

    public long getWorldTime()
    {
        return this.worldInfo.getWorldTime();
    }

    /**
     * Returns the coordinates of the spawn point
     */
    public ChunkCoordinates getSpawnPoint()
    {
        return new ChunkCoordinates(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
    {
        return true;
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity par1Entity, byte par2) {}

    /**
     * gets the world's chunk provider
     */
    public IChunkProvider getChunkProvider()
    {
        return this.chunkProvider;
    }

    /**
     * plays a given note at x, y, z. args: x, y, z, instrument, note
     */
    public void playNoteAt(int par1, int par2, int par3, int par4, int par5)
    {
        int var6 = this.getBlockId(par1, par2, par3);

        if (var6 > 0)
        {
            Block.blocksList[var6].powerBlock(this, par1, par2, par3, par4, par5);
        }
    }

    /**
     * Returns this world's current save handler
     */
    public ISaveHandler getSaveHandler()
    {
        return this.saveHandler;
    }

    /**
     * Returns the world's WorldInfo object
     */
    public WorldInfo getWorldInfo()
    {
        return this.worldInfo;
    }

    public void updateAllPlayersSleepingFlag()
    {
        this.allPlayersSleeping = !this.playerEntities.isEmpty();
        Iterator var1 = this.playerEntities.iterator();

        while (var1.hasNext())
        {
            EntityPlayer var2 = (EntityPlayer)var1.next();

            if (!var2.isPlayerSleeping())
            {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    protected void wakeUpAllPlayers()
    {
        this.allPlayersSleeping = false;
        Iterator var1 = this.playerEntities.iterator();

        while (var1.hasNext())
        {
            EntityPlayer var2 = (EntityPlayer)var1.next();

            if (var2.isPlayerSleeping())
            {
                var2.wakeUpPlayer(false, false, true);
            }
        }

        this.clearWeather();
    }

    public boolean isAllPlayersFullyAsleep()
    {
        if (this.allPlayersSleeping && !this.isRemote)
        {
            Iterator var1 = this.playerEntities.iterator();
            EntityPlayer var2;

            do
            {
                if (!var1.hasNext())
                {
                    return true;
                }

                var2 = (EntityPlayer)var1.next();
            }
            while (var2.isPlayerFullyAsleep());

            return false;
        }
        else
        {
            return false;
        }
    }

    public float getWeightedThunderStrength(float par1)
    {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * par1) * this.getRainStrength(par1);
    }

    /**
     * Not sure about this actually. Reverting this one myself.
     */
    public float getRainStrength(float par1)
    {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * par1;
    }

    public boolean isThundering()
    {
        return (double)this.getWeightedThunderStrength(1.0F) > 0.9D;
    }

    public boolean isRaining()
    {
        return (double)this.getRainStrength(1.0F) > 0.2D;
    }

    public boolean canLightningStrikeAt(int par1, int par2, int par3)
    {
        if (!this.isRaining())
        {
            return false;
        }
        else if (!this.canBlockSeeTheSky(par1, par2, par3))
        {
            return false;
        }
        else if (this.getPrecipitationHeight(par1, par3) > par2)
        {
            return false;
        }
        else
        {
            BiomeGenBase var4 = this.func_48091_a(par1, par3);
            return var4.getEnableSnow() ? false : var4.canSpawnLightningBolt();
        }
    }

    public boolean func_48089_z(int par1, int par2, int par3)
    {
        BiomeGenBase var4 = this.func_48091_a(par1, par3);
        return var4.func_48441_d();
    }

    public void setItemData(String par1Str, WorldSavedData par2WorldSavedData)
    {
        this.mapStorage.setData(par1Str, par2WorldSavedData);
    }

    public WorldSavedData loadItemData(Class par1Class, String par2Str)
    {
        return this.mapStorage.loadData(par1Class, par2Str);
    }

    public int getUniqueDataId(String par1Str)
    {
        return this.mapStorage.getUniqueDataId(par1Str);
    }

    public void playAuxSFX(int par1, int par2, int par3, int par4, int par5)
    {
        this.playAuxSFXAtEntity((EntityPlayer)null, par1, par2, par3, par4, par5);
    }

    public void playAuxSFXAtEntity(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
        for (int var7 = 0; var7 < this.worldAccesses.size(); ++var7)
        {
            ((IWorldAccess)this.worldAccesses.get(var7)).playAuxSFX(par1EntityPlayer, par2, par3, par4, par5, par6);
        }
    }

    /**
     * Returns current world height
     */
    public int getWorldHeight()
    {
        return 256;
    }

    /**
     * puts the World Random seed to a specific state dependant on the inputs
     */
    public Random setRandomSeed(int par1, int par2, int par3)
    {
        long var4 = (long)par1 * 341873128712L + (long)par2 * 132897987541L + this.getWorldInfo().getSeed() + (long)par3;
        this.rand.setSeed(var4);
        return this.rand;
    }

    public boolean updatingLighting()
    {
        return false;
    }

    /**
     * Gets a random mob for spawning in this world.
     */
    public SpawnListEntry getRandomMob(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        List var5 = this.getChunkProvider().getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
        return var5 != null && !var5.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, var5) : null;
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(String par1Str, int par2, int par3, int par4)
    {
        return this.getChunkProvider().findClosestStructure(this, par1Str, par2, par3, par4);
    }

    /**
     * Adds a single TileEntity to the world.
     * TODO: Eloraam fully describe the bug this fixes.
     * @param entity The TileEntity to be added.
     */
    public void addTileEntity(TileEntity entity) 
    {
        List dest = scanningTileEntities ? addedTileEntityList : loadedTileEntityList;
        if(entity.canUpdate())
        {
            dest.add(entity);
        }
    }
    
    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     * 
     * @param x Block X Position
     * @param y Block Y Position
     * @param z Block Z Position
     * @param side The Side in question
     * @return True if the side is solid
     */
    public boolean isBlockSolidOnSide(int x, int y, int z, int side)
    {
        Block block = Block.blocksList[getBlockId(x, y, z)];
        if(block == null)
        {
            return false;
        }
        return block.isBlockSolidOnSide(this, x, y, z, side);
    }
}
