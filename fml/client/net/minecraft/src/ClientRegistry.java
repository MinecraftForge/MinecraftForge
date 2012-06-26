package net.minecraft.src;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.FMLRegistry;
import cpw.mods.fml.common.registry.IMinecraftRegistry;

public class ClientRegistry implements IMinecraftRegistry
{

    public static ClientRegistry instance() {
        return (ClientRegistry) FMLRegistry.instance();
    }
    @Override
    public void addRecipe(ItemStack output, Object... params)
    {
        CraftingManager.func_1120_a().func_1121_a(output, params);
    }

    @Override
    public void addShapelessRecipe(ItemStack output, Object... params)
    {
        CraftingManager.func_1120_a().func_21187_b(output, params);
    }

    @Override
    public void addRecipe(IRecipe recipe)
    {
        CraftingManager.func_1120_a().func_25193_b().add(recipe);
    }

    @Override
    public void addSmelting(int input, ItemStack output)
    {
        FurnaceRecipes.func_21200_a().func_21199_a(input, output);
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
            int blockItemId = block.field_376_bc - 256;
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
        EntityList.addNewEntityListMapping(entityClass, entityName, id);
    }

    @Override
    public void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour)
    {
        EntityList.addNewEntityListMapping(entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
    }

    @Override
    public void registerTileEntity(Class <? extends TileEntity > tileEntityClass, String id)
    {
        TileEntity.addNewTileEntityMapping(tileEntityClass, id);
    }

    public void registerTileEntity(Class <? extends TileEntity > tileEntityClass, String id, TileEntitySpecialRenderer specialRenderer)
    {
        registerTileEntity(tileEntityClass, id);
        TileEntityRenderer.setTileEntityRenderer(tileEntityClass, specialRenderer);
    }

    @Override
    public void addBiome(BiomeGenBase biome)
    {
        FMLClientHandler.instance().addBiomeToDefaultWorldGenerator(biome);
    }

    @Override
    public void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
        for (BiomeGenBase biome : biomes)
        {
            @SuppressWarnings("unchecked")
            List<SpawnListEntry> spawns = biome.func_25063_a(typeOfCreature);

            for (SpawnListEntry entry : spawns)
            {
                //Adjusting an existing spawn entry
                if (entry.field_25212_a == entityClass)
                {
                    entry.field_35590_d = weightedProb;
                    entry.field_35591_b = min;
                    entry.field_35592_c = max;
                    break;
                }
            }

            spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = EntityList.getEntityToClassMapping().get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            addSpawn((Class <? extends EntityLiving >) entityClazz, weightedProb, min, max, spawnList, biomes);
        }
    }

    @Override
    public void removeBiome(BiomeGenBase biome)
    {
        FMLClientHandler.instance().removeBiomeFromDefaultWorldGenerator(biome);
    }

    @Override
    public void removeSpawn(Class <? extends EntityLiving > entityClass, EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
        for (BiomeGenBase biome : biomes)
        {
            @SuppressWarnings("unchecked")
            Iterator<SpawnListEntry> spawns = biome.func_25063_a(typeOfCreature).iterator();

            while (spawns.hasNext())
            {
                SpawnListEntry entry = spawns.next();
                if (entry.field_25212_a == entityClass)
                {
                    spawns.remove();
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = EntityList.getEntityToClassMapping().get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends EntityLiving >) entityClazz, spawnList, biomes);
        }
    }

}
