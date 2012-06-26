package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import net.minecraft.src.BiomeGenBase;

import cpw.mods.fml.common.registry.IMinecraftRegistry;
import cpw.mods.fml.server.FMLBukkitHandler;

public class BukkitRegistry implements IMinecraftRegistry
{

    @Override
    public void addRecipe(net.minecraft.src.ItemStack output, Object... params)
    {
        CraftingManager.getInstance().registerShapedRecipe((ItemStack) output, params);
    }

    @Override
    public void addShapelessRecipe(net.minecraft.src.ItemStack output, Object... params)
    {
        CraftingManager.getInstance().registerShapelessRecipe((ItemStack) output, params);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void addRecipe(net.minecraft.src.IRecipe recipe)
    {
        CraftingManager.getInstance().getRecipies().add(recipe);
    }

    @Override
    public void addSmelting(int input, net.minecraft.src.ItemStack output)
    {
        FurnaceRecipes.getInstance().registerRecipe(input, (ItemStack) output);
    }

    @Override
    public void registerBlock(net.minecraft.src.Block block)
    {
        registerBlock(block, ItemBlock.class);
    }

    @Override
    public void registerBlock(net.minecraft.src.Block block, Class <? extends net.minecraft.src.ItemBlock > itemclass)
    {
        try
        {
            assert block != null : "registerBlock: block cannot be null";
            assert itemclass != null : "registerBlock: itemclass cannot be null";
            int blockItemId = ((Block)block).id - 256;
            itemclass.getConstructor(int.class).newInstance(blockItemId);
        }
        catch (Exception e)
        {
            //HMMM
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public void registerEntityID(Class <? extends net.minecraft.src.Entity > entityClass, String entityName, int id)
    {
        EntityTypes.addNewEntityListMapping((Class<? extends Entity>) entityClass, entityName, id);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void registerEntityID(Class <? extends net.minecraft.src.Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour)
    {
        EntityTypes.addNewEntityListMapping((Class<? extends Entity>) entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void registerTileEntity(Class <? extends net.minecraft.src.TileEntity > tileEntityClass, String id)
    {
        TileEntity.addNewTileEntityMapping((Class<? extends TileEntity>) tileEntityClass, id);
    }

    @Override
    public void addBiome(BiomeGenBase biome)
    {
    	FMLBukkitHandler.instance().addBiomeToDefaultWorldGenerator((BiomeBase) biome);
    }

    @Override
    public void addSpawn(Class <? extends net.minecraft.src.EntityLiving > entityClass, int weightedProb, int min, int max, net.minecraft.src.EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
    	BiomeBase[] realBiomes=(BiomeBase[]) biomes;
        for (BiomeBase biome : realBiomes)
        {
            @SuppressWarnings("unchecked")
            List<BiomeMeta> spawns = ((BiomeBase)biome).getMobs((EnumCreatureType)typeOfCreature);

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
    public void addSpawn(String entityName, int weightedProb, int min, int max, net.minecraft.src.EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = EntityTypes.getEntityToClassMapping().get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            addSpawn((Class <? extends net.minecraft.src.EntityLiving >) entityClazz, weightedProb, min, max, spawnList, biomes);
        }
    }

    @Override
    public void removeBiome(BiomeGenBase biome)
    {
    	FMLBukkitHandler.instance().removeBiomeFromDefaultWorldGenerator((BiomeBase)biome);
    }

    @Override
    public void removeSpawn(Class <? extends net.minecraft.src.EntityLiving > entityClass, net.minecraft.src.EnumCreatureType typeOfCreature, BiomeGenBase... biomesO)
    {
    	BiomeBase[] biomes=(BiomeBase[]) biomesO;
        for (BiomeBase biome : biomes)
        {
            @SuppressWarnings("unchecked")
            List<BiomeMeta> spawns = ((BiomeBase)biome).getMobs((EnumCreatureType) typeOfCreature);

            Iterator<BiomeMeta> entries = spawns.iterator();
            while (entries.hasNext())
            {
            	BiomeMeta entry = entries.next();
                if (entry.a == entityClass)
                {
                    entries.remove();
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeSpawn(String entityName, net.minecraft.src.EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = EntityTypes.getEntityToClassMapping().get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends net.minecraft.src.EntityLiving >) entityClazz, spawnList, biomes);
        }
    }

}
