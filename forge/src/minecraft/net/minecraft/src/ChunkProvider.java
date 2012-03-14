package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.src.forge.ForgeHooks;

public class ChunkProvider implements IChunkProvider
{
    /** A set of dropped chunks. Currently not used in single player. */
    private Set droppedChunksSet = new HashSet();
    private Chunk emptyChunk;

    /** The parent IChunkProvider for this ChunkProvider. */
    private IChunkProvider chunkProvider;

    /** The IChunkLoader used by this ChunkProvider */
    private IChunkLoader chunkLoader;

    /**
     * A map of all the currently loaded chunks, uses the chunk id as the key.
     */
    private LongHashMap chunkMap = new LongHashMap();

    /** A list of all the currently loaded chunks. */
    private List chunkList = new ArrayList();

    /** The World object which this ChunkProvider was constructed with */
    private World worldObj;
    private int field_35392_h;

    public ChunkProvider(World par1World, IChunkLoader par2IChunkLoader, IChunkProvider par3IChunkProvider)
    {
        this.emptyChunk = new EmptyChunk(par1World, 0, 0);
        this.worldObj = par1World;
        this.chunkLoader = par2IChunkLoader;
        this.chunkProvider = par3IChunkProvider;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int par1, int par2)
    {
        return this.chunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
    }

    public void dropChunk(int par1, int par2)
    {
        if(!ForgeHooks.canUnloadChunk(worldObj.getChunkFromChunkCoords(par1, par2)))
        {
            return;
        }
        
        ChunkCoordinates var3 = this.worldObj.getSpawnPoint();
        int var4 = par1 * 16 + 8 - var3.posX;
        int var5 = par2 * 16 + 8 - var3.posZ;
        short var6 = 128;

        if (var4 < -var6 || var4 > var6 || var5 < -var6 || var5 > var6)
        {
            this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par1, par2)));
        }
    }

    /**
     * Creates an empty chunk ready to put data from the server in
     */
    public Chunk loadChunk(int par1, int par2)
    {
        long var3 = ChunkCoordIntPair.chunkXZ2Int(par1, par2);
        this.droppedChunksSet.remove(Long.valueOf(var3));
        Chunk var5 = (Chunk)this.chunkMap.getValueByKey(var3);

        if (var5 == null)
        {
            int var6 = 1875004;

            if (par1 < -var6 || par2 < -var6 || par1 >= var6 || par2 >= var6)
            {
                return this.emptyChunk;
            }

            var5 = this.loadChunkFromFile(par1, par2);

            if (var5 == null)
            {
                if (this.chunkProvider == null)
                {
                    var5 = this.emptyChunk;
                }
                else
                {
                    var5 = this.chunkProvider.provideChunk(par1, par2);
                }
            }

            this.chunkMap.add(var3, var5);
            this.chunkList.add(var5);

            if (var5 != null)
            {
                var5.func_4143_d();
                var5.onChunkLoad();
            }

            var5.populateChunk(this, this, par1, par2);
        }

        return var5;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int par1, int par2)
    {
        Chunk var3 = (Chunk)this.chunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        return var3 == null ? this.loadChunk(par1, par2) : var3;
    }

    /**
     * Attemps to load the chunk from the save file, returns null if the chunk is not available.
     */
    private Chunk loadChunkFromFile(int par1, int par2)
    {
        if (this.chunkLoader == null)
        {
            return null;
        }
        else
        {
            try
            {
                Chunk var3 = this.chunkLoader.loadChunk(this.worldObj, par1, par2);

                if (var3 != null)
                {
                    var3.lastSaveTime = this.worldObj.getWorldTime();
                }

                return var3;
            }
            catch (Exception var4)
            {
                var4.printStackTrace();
                return null;
            }
        }
    }

    private void saveChunkExtraData(Chunk par1Chunk)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                this.chunkLoader.saveExtraChunkData(this.worldObj, par1Chunk);
            }
            catch (Exception var3)
            {
                var3.printStackTrace();
            }
        }
    }

    private void saveChunkData(Chunk par1Chunk)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                par1Chunk.lastSaveTime = this.worldObj.getWorldTime();
                this.chunkLoader.saveChunk(this.worldObj, par1Chunk);
            }
            catch (IOException var3)
            {
                var3.printStackTrace();
            }
        }
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
        Chunk var4 = this.provideChunk(par2, par3);

        if (!var4.isTerrainPopulated)
        {
            var4.isTerrainPopulated = true;

            if (this.chunkProvider != null)
            {
                this.chunkProvider.populate(par1IChunkProvider, par2, par3);
                ModLoader.populateChunk(this.chunkProvider, par2, par3, this.worldObj);
                var4.setChunkModified();
            }
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        int var3 = 0;

        for (int var4 = 0; var4 < this.chunkList.size(); ++var4)
        {
            Chunk var5 = (Chunk)this.chunkList.get(var4);

            if (par1)
            {
                this.saveChunkExtraData(var5);
            }

            if (var5.needsSaving(par1))
            {
                this.saveChunkData(var5);
                var5.isModified = false;
                ++var3;

                if (var3 == 24 && !par1)
                {
                    return false;
                }
            }
        }

        if (par1)
        {
            if (this.chunkLoader == null)
            {
                return true;
            }

            this.chunkLoader.saveExtraData();
        }

        return true;
    }

    /**
     * Unloads the 100 oldest chunks from memory, due to a bug with chunkSet.add() never being called it thinks the list
     * is always empty and will not remove any chunks.
     */
    public boolean unload100OldestChunks()
    {
        int var1;

        for (var1 = 0; var1 < 100; ++var1)
        {
            if (!this.droppedChunksSet.isEmpty())
            {
                Long var2 = (Long)this.droppedChunksSet.iterator().next();
                Chunk var3 = (Chunk)this.chunkMap.getValueByKey(var2.longValue());
                var3.onChunkUnload();
                this.saveChunkData(var3);
                this.saveChunkExtraData(var3);
                this.droppedChunksSet.remove(var2);
                this.chunkMap.remove(var2.longValue());
                this.chunkList.remove(var3);
            }
        }

        for (var1 = 0; var1 < 10; ++var1)
        {
            if (this.field_35392_h >= this.chunkList.size())
            {
                this.field_35392_h = 0;
                break;
            }

            Chunk var4 = (Chunk)this.chunkList.get(this.field_35392_h++);
            EntityPlayer var5 = this.worldObj.func_48456_a((double)(var4.xPosition << 4) + 8.0D, (double)(var4.zPosition << 4) + 8.0D, 288.0D);

            if (var5 == null)
            {
                this.dropChunk(var4.xPosition, var4.zPosition);
            }
        }

        if (this.chunkLoader != null)
        {
            this.chunkLoader.chunkTick();
        }

        return this.chunkProvider.unload100OldestChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "ServerChunkCache: " + this.chunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        return this.chunkProvider.getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int par4, int par5)
    {
        return this.chunkProvider.findClosestStructure(par1World, par2Str, par3, par4, par5);
    }
}
