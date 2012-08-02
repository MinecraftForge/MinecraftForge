package cpw.mods.fml.common.registry;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.BiMap;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.SpawnListEntry;

public class EntityRegistry
{

    private static final EntityRegistry INSTANCE = new EntityRegistry();

    private BitSet availableIndicies;
    
    public static EntityRegistry instance()
    {
        return INSTANCE;
    }
    
    private EntityRegistry()
    {
        availableIndicies = new BitSet(256);
        availableIndicies.set(0,255);
        for (Object id : EntityList.field_75623_d.keySet())
        {
            availableIndicies.clear((Integer)id);
        }
    }
    
    public static void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id)
    {
        instance().validateAndClaimId(id);
        EntityList.func_75618_a(entityClass, entityName, id);
    }

    private void validateAndClaimId(int id)
    {
        if (!availableIndicies.get(id))
        {
            throw new RuntimeException(String.format("Unable to claim entity id %d", id));
        }
        availableIndicies.clear(id);
    }

    public static void registerEntityID(Class <? extends Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour)
    {
        instance().validateAndClaimId(id);
        EntityList.func_75614_a(entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
    }

    public static void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
        for (BiomeGenBase biome : biomes)
        {
            @SuppressWarnings("unchecked")
            List<SpawnListEntry> spawns = biome.func_76747_a(typeOfCreature);
    
            for (SpawnListEntry entry : spawns)
            {
                //Adjusting an existing spawn entry
                if (entry.field_76300_b == entityClass)
                {
                    entry.field_76292_a = weightedProb;
                    entry.field_76301_c = min;
                    entry.field_76299_d = max;
                    break;
                }
            }
    
            spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
        }
    }

    public static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = (Class<? extends Entity>) EntityList.field_75625_b.get(entityName);
    
        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            addSpawn((Class <? extends EntityLiving >) entityClazz, weightedProb, min, max, spawnList, biomes);
        }
    }

    public static void removeSpawn(Class <? extends EntityLiving > entityClass, EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
        for (BiomeGenBase biome : biomes)
        {
            @SuppressWarnings("unchecked")
            Iterator<SpawnListEntry> spawns = biome.func_76747_a(typeOfCreature).iterator();
    
            while (spawns.hasNext())
            {
                SpawnListEntry entry = spawns.next();
                if (entry.field_76300_b == entityClass)
                {
                    spawns.remove();
                }
            }
        }
    }

    public static void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = (Class<? extends Entity>) EntityList.field_75625_b.get(entityName);
    
        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends EntityLiving >) entityClazz, spawnList, biomes);
        }
    }

    public static int findGlobalUniqueEntityId()
    {
        int res = instance().availableIndicies.nextSetBit(0);
        if (res < 0)
        {
            throw new RuntimeException("No more entity indicies left");
        }
        return res;
    }

}
