package net.minecraft.src;

import java.util.List;

public interface IChunkProvider
{
    /**
     * Checks to see if a chunk exists at x, y
     */
    boolean chunkExists(int var1, int var2);

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    Chunk provideChunk(int var1, int var2);

    /**
     * Creates an empty chunk ready to put data from the server in
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

    /**
     * Unloads the 100 oldest chunks from memory, due to a bug with chunkSet.add() never being called it thinks the list
     * is always empty and will not remove any chunks.
     */
    boolean unload100OldestChunks();

    /**
     * Returns if the IChunkProvider supports saving.
     */
    boolean canSave();

    /**
     * Converts the instance data to a readable string.
     */
    String makeString();

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4);

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    ChunkPosition findClosestStructure(World var1, String var2, int var3, int var4, int var5);
}
