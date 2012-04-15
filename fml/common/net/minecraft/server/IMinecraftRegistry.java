package net.minecraft.server;

public interface IMinecraftRegistry
{

    public abstract void removeSpawn(String entityName, EnumCreatureType spawnList, Object... biomes);

    public abstract void removeSpawn(Class <? extends EntityLiving > entityClass, EnumCreatureType typeOfCreature, Object... biomes);

    public abstract void removeBiome(Object biome);

    public abstract void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, Object... biomes);

    public abstract void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, Object... biomes);

    public abstract void addBiome(Object biome);

    public abstract void registerTileEntity(Class <? extends TileEntity > tileEntityClass, String id);

    public abstract void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour);

    public abstract void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id);

    public abstract void registerBlock(Block block, Class <? extends ItemBlock > itemclass);

    public abstract void registerBlock(Block block);

    public abstract void addSmelting(int input, ItemStack output);

    public abstract void addShapelessRecipe(ItemStack output, Object... params);

    public abstract void addRecipe(ItemStack output, Object... params);

}