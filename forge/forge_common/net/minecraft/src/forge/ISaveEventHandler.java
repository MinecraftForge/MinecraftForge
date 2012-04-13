package net.minecraft.src.forge;

import net.minecraft.src.Chunk;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public interface ISaveEventHandler
{
    /**
     * Called when the world is created, either newly created or loaded from a save file
     * @param world The world being loaded.
     */
    public void onWorldLoad(World world);
    
    /**
     * Called whenever the world is saving. Use this to save extra data alongside the world, eg. maps.
     * @param world The world being saved.
     */
    public void onWorldSave(World world);

    /**
     * Called when a chunk is created, either newly generated or loaded from a save file
     * @param world The world containing this chunk.
     * @param chunk The chunk being loaded.
     */
    public void onChunkLoad(World world, Chunk chunk);

    /**
     * Called when a chunk is unloaded and removed from the world
     * @param world The world containing this chunk.
     * @param chunk The chunk being loaded.
     */
    public void onChunkUnload(World world, Chunk chunk);

    /**
     * Use this to save extra data in with the chunk file.
     * @param world The world containing this chunk.
     * @param chunk The chunk being saved.
     * @param data The compound to save data into and be written to disk
     */
    public void onChunkSaveData(World world, Chunk chunk, NBTTagCompound data);

    /**
     * Use this to load extra save data from a chunk file.
     * @param world The world containing this chunk.
     * @param chunk The chunk being loaded.
     * @param data The compound to load data from
     */
    public void onChunkLoadData(World world, Chunk chunk, NBTTagCompound data);
}