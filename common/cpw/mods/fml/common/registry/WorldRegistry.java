package cpw.mods.fml.common.registry;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import cpw.mods.fml.common.IWorldGenerator;

public class WorldRegistry
{

    private static Set<IWorldGenerator> worldGenerators = new HashSet<IWorldGenerator>();

    public static void registerWorldGenerator(IWorldGenerator generator)
    {
        worldGenerators.add(generator);
    }
    
    public static void generateWorld(int chunkX, int chunkZ, long worldSeed, Object... data)
    {
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        fmlRandom.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ worldSeed);

        for (IWorldGenerator generator : worldGenerators)
        {
            generator.generate(fmlRandom, chunkX, chunkZ, data);
        }

    }

}
