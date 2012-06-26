package cpw.mods.fml.common.registry;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

public interface IMinecraftRegistry
{

    public abstract void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes);

    public abstract void removeSpawn(Class <? extends EntityLiving > entityClass, EnumCreatureType typeOfCreature, BiomeGenBase... biomes);

    public abstract void removeBiome(BiomeGenBase biome);

    public abstract void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes);

    public abstract void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, BiomeGenBase... biomes);

    public abstract void addBiome(BiomeGenBase biome);

    public abstract void registerTileEntity(Class <? extends TileEntity > tileEntityClass, String id);

    public abstract void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour);

    public abstract void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id);

    public abstract void registerBlock(Block block, Class <? extends ItemBlock > itemclass);

    public abstract void registerBlock(Block block);

    public abstract void addSmelting(int input, ItemStack output);

    public abstract void addShapelessRecipe(ItemStack output, Object... params);

    public abstract void addRecipe(ItemStack output, Object... params);

    public abstract void addRecipe(IRecipe recipe);

}