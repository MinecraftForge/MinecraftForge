package net.minecraft.src;

import java.util.List;

public interface IChunkProvider
{
    /**
     * Checks to see if a chunk exists at x, y
     */
    boolean chunkExists(int var1, int var2);

    Chunk provideChunk(int var1, int var2);

    /**
     * loads or generates the chunk at the chunk location specified
     */
    Chunk loadChunk(int var1, int var2);

    /**
     * Populates chunk with ores etc etc
     */
    void populate(IChunkProvider var1, int var2, int var3);

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    boolean saveChunks(boolean var1, IProgressUpdate var2);

    boolean unload100OldestChunks();

    boolean canSave();

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4);

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    ChunkPosition findClosestStructure(World var1, String var2, int var3, int var4, int var5);
}
