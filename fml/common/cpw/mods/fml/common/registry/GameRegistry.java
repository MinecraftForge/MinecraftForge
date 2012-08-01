package cpw.mods.fml.common.registry;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.src.CraftingManager;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ItemStack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod.Block;
import cpw.mods.fml.common.ModContainer;

public class GameRegistry
{
    private static Multimap<ModContainer,BlockProxy> blockRegistry = ArrayListMultimap.create();
    private static Multimap<ModContainer,ItemProxy> itemRegistry = ArrayListMultimap.create();
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

    public static Object buildBlock(ModContainer container, Class<?> type, Block annotation) throws Exception
    {
        Object o = type.getConstructor(int.class).newInstance(250);
        registerBlock((BlockProxy)o);
        return o;
    }

    public static void registerBlock(BlockProxy block)
    {
        
    }

    public static void addRecipe(ItemStack output, Object... params)
    {
        CraftingManager.func_77594_a().addRecipe(output, params);
    }

    public static void addShapelessRecipe(ItemStack output, Object... params)
    {
        FMLRegistry.instance.addShapelessRecipe(output, params);
    }

    public static void addRecipe(IRecipe recipe)
    {
        FMLRegistry.instance.addRecipe(recipe);
    }

    public static void addSmelting(int input, ItemStack output, float xp)
    {
        FurnaceRecipes.func_77602_a().func_77600_a(input, output, xp);
    }

}
