package net.minecraft.server;

import java.util.Collections;
import java.util.List;

public class BukkitRegistry implements IMinecraftRegistry
{

    @Override
    public void addRecipe(ItemStack output, Object... params)
    {
        CraftingManager.getInstance().registerShapedRecipe(output, params);
    }

    @Override
    public void addShapelessRecipe(ItemStack output, Object... params)
    {
        CraftingManager.getInstance().registerShapelessRecipe(output, params);
    }

    @Override
    public void addSmelting(int input, ItemStack output)
    {
        FurnaceRecipes.getInstance().registerRecipe(input, output);
    }

    @Override
    public void registerBlock(Block block)
    {
        registerBlock(block, ItemBlock.class);
    }

    @Override
    public void registerBlock(Block block, Class <? extends ItemBlock > itemclass)
    {
        try
        {
            assert block != null : "registerBlock: block cannot be null";
            assert itemclass != null : "registerBlock: itemclass cannot be null";
            int blockItemId = block.id - 256;
            itemclass.getConstructor(int.class).newInstance(blockItemId);
        }
        catch (Exception e)
        {
            //HMMM
        }
    }

    @Override
    public void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id)
    {
        EntityTypes.addNewEntityListMapping(entityClass, entityName, id);
    }

    @Override
    public void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour)
    {
        EntityTypes.addNewEntityListMapping(entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
    }

    @Override
    public void registerTileEntity(Class <? extends TileEntity > tileEntityClass, String id)
    {
        TileEntity.addNewTileEntityMapping(tileEntityClass, id);
    }

    @Override
    public void addBiome(Object biome)
    {
        //NOOP because the implementation idea is broken. Creating a BiomeGenBase adds the biome already.
    }

    @Override
    public void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, Object... biomes)
    {
    	BiomeBase[] realBiomes=(BiomeBase[]) biomes;
        for (BiomeBase biome : realBiomes)
        {
            @SuppressWarnings("unchecked")
            List<BiomeMeta> spawns = biome.getMobs(typeOfCreature);
    
            for (BiomeMeta entry : spawns)
            {
                //Adjusting an existing spawn entry
                if (entry.a == entityClass)
                {
                    entry.d = weightedProb;
                    entry.b = min;
                    entry.c = max;
                    break;
                }
            }
    
            spawns.add(new BiomeMeta(entityClass, weightedProb, min, max));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, Object... biomes)
    {
        Class <? extends Entity > entityClazz = EntityTypes.getEntityToClassMapping().get(entityName);
    
        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            addSpawn((Class <? extends EntityLiving >) entityClazz, weightedProb, min, max, spawnList, biomes);
        }
    }

    @Override
    public void removeBiome(Object biome)
    {
        // NOOP because broken
    }

    @Override
    public void removeSpawn(Class <? extends EntityLiving > entityClass, EnumCreatureType typeOfCreature, Object... biomesO)
    {
    	BiomeBase[] biomes=(BiomeBase[]) biomesO;
        for (BiomeBase biome : biomes)
        {
            @SuppressWarnings("unchecked")
            List<BiomeMeta> spawns = biome.getMobs(typeOfCreature);
    
            for (BiomeMeta entry : Collections.unmodifiableList(spawns))
            {
                if (entry.a == entityClass)
                {
                    spawns.remove(entry);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeSpawn(String entityName, EnumCreatureType spawnList, Object... biomes)
    {
        Class <? extends Entity > entityClazz = EntityTypes.getEntityToClassMapping().get(entityName);
    
        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends EntityLiving >) entityClazz, spawnList, biomes);
        }
    }

}
