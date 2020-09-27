package net.minecraftforge.dimension;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;

/**
 * Holds various builder functions for Biome Providers
 *
 */
public class BiomeProviders {
	/**
	 * Creates a normal OverworldBiomeProvider
	 * @param seed the seed
	 * @param legacyBiomeInitLayer Whether the world is default_1_1
	 * @param largeBiomes Whether the biomes are large.
	 * @return the overworld biome provider
	 */
	public static OverworldBiomeProvider createOverworldBiomeProvider(long seed, boolean legacyBiomeInitLayer, boolean largeBiomes) {
		return new OverworldBiomeProvider(seed, legacyBiomeInitLayer, largeBiomes, DynamicDimensionManager.getDimensionManager().getBiomeRegistry());
	}
	
	/**
	 * Creates a normal NetherBiomeProvider
	 * @param seed the seed
	 * @return the nether biome provider
	 */
	public static NetherBiomeProvider createNetherBiomeProvider(long seed) {
		return NetherBiomeProvider.Preset.field_235288_b_.func_242619_a(DynamicDimensionManager.getDimensionManager().getBiomeRegistry(), seed);
	}

	/**
	 * Creates a normal EndBiomeProvider
	 * @param seed the seed
	 * @return the end biome provider
	 */
	public static EndBiomeProvider createEndBiomeProvider(long seed) {
		return new EndBiomeProvider(DynamicDimensionManager.getDimensionManager().getBiomeRegistry(), seed);
	}
	
	/**
	 * Creates a CheckerboardBiomeProvider
	 * @param biomeList the list of biomes
	 * @param scale the size of the squares on an exponential scale.
	 * @return the checkerboard biome provider
	 */
	public static CheckerboardBiomeProvider createCheckerboardBiomeProvider(List<Supplier<Biome>> biomeList, int scale) {
		return new CheckerboardBiomeProvider(biomeList, scale);
	}
	
	/**
	 * Creates a SingleBiomeProvider which returns only one biome
	 * @param biome the biome
	 * @return the single biome provider
	 */
	public static SingleBiomeProvider createSingleBiomeProvider(Biome biome) {
		return new SingleBiomeProvider(biome);
	}

}
