package org.bukkit.craftbukkit.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.chunk.IChunkProvider; // MCPC+

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.generator.BlockPopulator;

public class NormalChunkGenerator extends InternalChunkGenerator {
    private final net.minecraft.world.chunk.IChunkProvider provider;

    public NormalChunkGenerator(net.minecraft.world.World world, long seed) {
        provider = world.provider.createChunkGenerator();
    }

    public byte[] generate(org.bukkit.World world, Random random, int x, int z) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean canSpawn(org.bukkit.World world, int x, int z) {
        return ((CraftWorld) world).getHandle().provider.canCoordinateBeSpawn(x, z);
    }

    public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world) {
        return new ArrayList<BlockPopulator>();
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int i, int i1) {
        return provider.chunkExists(i, i1);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public net.minecraft.world.chunk.Chunk provideChunk(int i, int i1) {
        return provider.provideChunk(i, i1);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public net.minecraft.world.chunk.Chunk loadChunk(int i, int i1) {
        return provider.loadChunk(i, i1);
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(net.minecraft.world.chunk.IChunkProvider icp, int i, int i1) {
        provider.populate(icp, i, i1);
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean bln, net.minecraft.util.IProgressUpdate ipu) {
        return provider.saveChunks(bln, ipu);
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        return provider.unloadQueuedChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return provider.canSave();
    }

    public List<?> getPossibleCreatures(net.minecraft.entity.EnumCreatureType ect, int i, int i1, int i2) {
        return provider.getPossibleCreatures(ect, i, i1, i2);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public net.minecraft.world.ChunkPosition findClosestStructure(net.minecraft.world.World world, String string, int i, int i1, int i2) {
        return provider.findClosestStructure(world, string, i, i1, i2);
    }

    public void recreateStructures(int i, int j) {
        provider.recreateStructures(i, j);
    }

    // n.m.s implementations always return 0. (The true implementation is in ChunkProviderServer)
    public int getLoadedChunkCount() {
        return 0;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "NormalWorldGenerator";
    }

    public void func_104112_b() {}
    
    // MCPC+ start - return vanilla compatible IChunkProvider for forge
    public IChunkProvider getForgeChunkProvider()
    {
        return this.provider;
    }
    // MCPC+ end
}
