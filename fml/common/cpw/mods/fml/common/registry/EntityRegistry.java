package cpw.mods.fml.common.registry;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.SpawnListEntry;

public class EntityRegistry
{

    public class EntityRegistration
    {
        private Class<? extends Entity> entityClass;
        private ModContainer container;
        private String entityName;
        private int modId;
        public EntityRegistration(ModContainer mc, Class<? extends Entity> entityClass, String entityName, int id)
        {
            this.container = mc;
            this.entityClass = entityClass;
            this.entityName = entityName;
            this.modId = id;
        }
        public Class<? extends Entity> getEntityClass()
        {
            return entityClass;
        }
        public ModContainer getContainer()
        {
            return container;
        }
        public String getEntityName()
        {
            return entityName;
        }
        public int getModEntityId()
        {
            return modId;
        }
    }

    private static final EntityRegistry INSTANCE = new EntityRegistry();

    private BitSet availableIndicies;
    private ListMultimap<ModContainer, EntityRegistration> entityRegistrations = ArrayListMultimap.create();
    private BiMap<String,ModContainer> entityNames = HashBiMap.create();
    private BiMap<Class<? extends Entity>, EntityRegistration> entityClassRegistrations = HashBiMap.create();
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
    
    public static void registerModEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod)
    {
        instance().doModEntityRegistration(entityClass, entityName, id, mod);
    }
    private void doModEntityRegistration(Class<? extends Entity> entityClass, String entityName, int id, Object mod)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        EntityRegistration er = new EntityRegistration(mc, entityClass, entityName, id);
        try
        {
            entityClassRegistrations.put(entityClass, er);
            entityNames.put(entityName, mc);
        }
        catch (IllegalArgumentException e)
        {
            FMLLog.log(Level.WARNING, e, "The mod %s tried to register the entity (name,class) (%s,%s) one or bothe of which are already registered", mc.getModId(), entityName, entityClass.getName());
            return;
        }
        entityRegistrations.put(mc, er);
    }

    public static void registerGlobalEntityID(Class <? extends Entity > entityClass, String entityName, int id)
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

    public static void registerGlobalEntityID(Class <? extends Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour)
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

    public EntityRegistration lookupModSpawn(Class<? extends Entity> clazz, boolean keepLooking)
    {
        Class<?> localClazz = clazz;
    
        do
        {
            EntityRegistration er = entityClassRegistrations.get(localClazz);
            if (er != null)
            {
                return er;
            }
            localClazz = localClazz.getSuperclass();
            keepLooking = (!Object.class.equals(localClazz));
        }
        while (keepLooking);
        
        return null;
    }

    public EntityRegistration lookupModSpawn(ModContainer mc, int modEntityId)
    {
        for (EntityRegistration er : entityRegistrations.get(mc))
        {
            if (er.getModEntityId() == modEntityId)
            {
                return er;
            }
        }
        return null;
    }

}
