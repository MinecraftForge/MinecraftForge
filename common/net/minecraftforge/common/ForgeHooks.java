package net.minecraftforge.common;

import java.util.*;

import net.minecraft.src.*;

public class ForgeHooks
{
    static class GrassEntry extends WeightedRandomItem
    {
        public final Block block;
        public final int metadata;
        public GrassEntry(Block block, int meta, int weight)
        {
            super(weight);
            this.block = block;
            this.metadata = meta;
        }
    }
    
    static class SeedEntry extends WeightedRandomItem
    {
        public final ItemStack seed;
        public SeedEntry(ItemStack seed, int weight)
        {
            super(weight);
            this.seed = seed;
        }
    }
    static final List<GrassEntry> grassList = new ArrayList<GrassEntry>();
    static final List<SeedEntry> seedList = new ArrayList<SeedEntry>();
    
    public static void plantGrass(World world, int x, int y, int z)
    {
        GrassEntry grass = (GrassEntry)WeightedRandom.getRandomItem(world.rand, grassList);
        if (grass == null || grass.block == null || !grass.block.canBlockStay(world, x, y, z))
        {
            return;
        }
        world.setBlockAndMetadataWithNotify(x, y, z, grass.block.blockID, grass.metadata);
    }
    
    static
    {
        grassList.add(new GrassEntry(Block.plantYellow, 0, 20));
        grassList.add(new GrassEntry(Block.plantRed,    0, 10));
        seedList.add(new SeedEntry(new ItemStack(Item.seeds), 10));
        System.out.printf("MinecraftForge v%s Initialized\n", ForgeVersion.getVersion());
        ModLoader.getLogger().info(String.format("MinecraftForge v%s Initialized", ForgeVersion.getVersion()));
    }
}
