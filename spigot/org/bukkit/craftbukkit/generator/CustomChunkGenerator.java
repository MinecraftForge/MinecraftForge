package org.bukkit.craftbukkit.generator;

import java.util.List;
import java.util.Random;


import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.craftbukkit.block.CraftBlock;

public class CustomChunkGenerator extends InternalChunkGenerator {
    private final ChunkGenerator generator;
    private final net.minecraft.world.WorldServer world;
    private final Random random;
    private final net.minecraft.world.gen.structure.MapGenStronghold strongholdGen = new net.minecraft.world.gen.structure.MapGenStronghold();

    private static class CustomBiomeGrid implements BiomeGrid {
        net.minecraft.world.biome.BiomeGenBase[] biome;

        public Biome getBiome(int x, int z) {
            return CraftBlock.biomeBaseToBiome(biome[(z << 4) | x]);
        }

        public void setBiome(int x, int z, Biome bio) {
           biome[(z << 4) | x] = CraftBlock.biomeToBiomeBase(bio);
        }
    }

    public CustomChunkGenerator(net.minecraft.world.World world, long seed, ChunkGenerator generator) {
        this.world = (net.minecraft.world.WorldServer) world;
        this.generator = generator;

        this.random = new Random(seed);
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int x, int z) {
        return true;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public net.minecraft.world.chunk.Chunk provideChunk(int x, int z) {
        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

        net.minecraft.world.chunk.Chunk chunk;

        // Get default biome data for chunk
        CustomBiomeGrid biomegrid = new CustomBiomeGrid();
        biomegrid.biome = new net.minecraft.world.biome.BiomeGenBase[256];
        world.getWorldChunkManager().loadBlockGeneratorData(biomegrid.biome, x << 4, z << 4, 16, 16);

        // Try extended block method (1.2+)
        short[][] xbtypes = generator.generateExtBlockSections(this.world.getWorld(), this.random, x, z, biomegrid);
        if (xbtypes != null) {
            chunk = new net.minecraft.world.chunk.Chunk(this.world, x, z);

            net.minecraft.world.chunk.storage.ExtendedBlockStorage[] csect = chunk.getBlockStorageArray();
            int scnt = Math.min(csect.length, xbtypes.length);

            // Loop through returned sections
            for (int sec = 0; sec < scnt; sec++) {
                if (xbtypes[sec] == null) {
                    continue;
                }
                byte[] secBlkID = new byte[4096]; // Allocate blk ID bytes
                byte[] secExtBlkID = null; // Delay getting extended ID nibbles
                short[] bdata = xbtypes[sec];
                // Loop through data, 2 blocks at a time
                for (int i = 0, j = 0; i < bdata.length; i += 2, j++) {
                    short b1 = bdata[i];
                    short b2 = bdata[i + 1];
                    byte extb = (byte) ((b1 >> 8) | ((b2 >> 4) & 0xF0));

                    secBlkID[i] = (byte) b1;
                    secBlkID[(i + 1)] = (byte) b2;

                    if (extb != 0) { // If extended block ID data
                        if (secExtBlkID == null) { // Allocate if needed
                            secExtBlkID = new byte[2048];
                        }
                        secExtBlkID[j] = extb;
                    }
                }
                // Build chunk section
                csect[sec] = new net.minecraft.world.chunk.storage.ExtendedBlockStorage(sec << 4, true, secBlkID, secExtBlkID);
            }
        }
        else { // Else check for byte-per-block section data
            byte[][] btypes = generator.generateBlockSections(this.world.getWorld(), this.random, x, z, biomegrid);

            if (btypes != null) {
                chunk = new net.minecraft.world.chunk.Chunk(this.world, x, z);

                net.minecraft.world.chunk.storage.ExtendedBlockStorage[] csect = chunk.getBlockStorageArray();
                int scnt = Math.min(csect.length, btypes.length);

                for (int sec = 0; sec < scnt; sec++) {
                    if (btypes[sec] == null) {
                        continue;
                    }
                    csect[sec] = new net.minecraft.world.chunk.storage.ExtendedBlockStorage(sec << 4, true, btypes[sec], null);
                }
            }
            else { // Else, fall back to pre 1.2 method
                @SuppressWarnings("deprecation")
                byte[] types = generator.generate(this.world.getWorld(), this.random, x, z);
                int ydim = types.length / 256;
                int scnt = ydim / 16;

                chunk = new net.minecraft.world.chunk.Chunk(this.world, x, z); // Create empty chunk

                net.minecraft.world.chunk.storage.ExtendedBlockStorage[] csect = chunk.getBlockStorageArray();

                scnt = Math.min(scnt, csect.length);
                // Loop through sections
                for (int sec = 0; sec < scnt; sec++) {
                    net.minecraft.world.chunk.storage.ExtendedBlockStorage cs = null; // Add sections when needed
                    byte[] csbytes = null;

                    for (int cy = 0; cy < 16; cy++) {
                        int cyoff = cy | (sec << 4);

                        for (int cx = 0; cx < 16; cx++) {
                            int cxyoff = (cx * ydim * 16) + cyoff;

                            for (int cz = 0; cz < 16; cz++) {
                                byte blk = types[cxyoff + (cz * ydim)];

                                if (blk != 0) { // If non-empty
                                    if (cs == null) { // If no section yet, get one
                                        cs = csect[sec] = new net.minecraft.world.chunk.storage.ExtendedBlockStorage(sec << 4, true);
                                        csbytes = cs.getBlockLSBArray();
                                    }
                                    csbytes[(cy << 8) | (cz << 4) | cx] = blk;
                                }
                            }
                        }
                    }
                    // If section built, finish prepping its state
                    if (cs != null) {
                        cs.removeInvalidBlocks();
                    }
                }
            }
        }
        // Set biome grid
        byte[] biomeIndex = chunk.getBiomeArray();
        for (int i = 0; i < biomeIndex.length; i++) {
            biomeIndex[i] = (byte) (biomegrid.biome[i].biomeID & 0xFF);
        }
        // Initialize lighting
        chunk.generateSkylightMap();

        return chunk;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(net.minecraft.world.chunk.IChunkProvider icp, int i, int i1) {
        // Nothing!
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean bln, net.minecraft.util.IProgressUpdate ipu) {
        return true;
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return true;
    }

    @SuppressWarnings("deprecation")
    public byte[] generate(org.bukkit.World world, Random random, int x, int z) {
        return generator.generate(world, random, x, z);
    }

    public byte[][] generateBlockSections(org.bukkit.World world, Random random, int x, int z, BiomeGrid biomes) {
        return generator.generateBlockSections(world, random, x, z, biomes);
    }

    public short[][] generateExtBlockSections(org.bukkit.World world, Random random, int x, int z, BiomeGrid biomes) {
        return generator.generateExtBlockSections(world, random, x, z, biomes);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public net.minecraft.world.chunk.Chunk loadChunk(int x, int z) {
        return provideChunk(x, z);
    }

    @Override
    public boolean canSpawn(org.bukkit.World world, int x, int z) {
        return generator.canSpawn(world, x, z);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world) {
        return generator.getDefaultPopulators(world);
    }

    public List<?> getPossibleCreatures(net.minecraft.entity.EnumCreatureType type, int x, int y, int z) {
        net.minecraft.world.biome.BiomeGenBase biomebase = world.getBiomeGenForCoords(x, z);

        return biomebase == null ? null : biomebase.getSpawnableList(type);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public net.minecraft.world.ChunkPosition findClosestStructure(net.minecraft.world.World world, String type, int x, int y, int z) {
        return "Stronghold".equals(type) && this.strongholdGen != null ? this.strongholdGen.getNearestInstance(world, x, y, z) : null;
    }

    public void recreateStructures(int i, int j) {}

    public int getLoadedChunkCount() {
        return 0;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "CustomChunkGenerator";
    }

    public void func_104112_b() {}
}
