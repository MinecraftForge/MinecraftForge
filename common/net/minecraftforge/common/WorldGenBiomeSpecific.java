package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenBiomeSpecific implements IWorldGenerator
{

  @Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(chunkX * 16, chunkZ * 16);
		ArrayList<BiomeType> types = BiomeDictionary.getTypesForBiome(biome);
		ArrayList<IWorldGenerator> generators;
		
		for(BiomeType type : types)
		{
			generators = BiomeWorldGenRegistry.getGeneratorsForType(type);
			
			for(IWorldGenerator gen : generators)
			{
				gen.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
			}
		}
		
		generators = BiomeDictionary.getWorldGenForBiome(biome);
		
		for(IWorldGenerator gen : generators)
		{
			gen.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}

}
