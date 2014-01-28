/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.registry;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedBytes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.internal.FMLMessage.EntitySpawnMessage;

public class EntityRegistry
{
    public class EntityRegistration
    {
        private Class<? extends Entity> entityClass;
        private ModContainer container;
        private String entityName;
        private int modId;
        private int trackingRange;
        private int updateFrequency;
        private boolean sendsVelocityUpdates;
        private Function<EntitySpawnMessage, Entity> customSpawnCallback;
        private boolean usesVanillaSpawning;
        public EntityRegistration(ModContainer mc, Class<? extends Entity> entityClass, String entityName, int id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
        {
            this.container = mc;
            this.entityClass = entityClass;
            this.entityName = entityName;
            this.modId = id;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
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
        public int getTrackingRange()
        {
            return trackingRange;
        }
        public int getUpdateFrequency()
        {
            return updateFrequency;
        }
        public boolean sendsVelocityUpdates()
        {
            return sendsVelocityUpdates;
        }

        public boolean usesVanillaSpawning()
        {
            return usesVanillaSpawning;
        }
        public boolean hasCustomSpawning()
        {
            return customSpawnCallback != null;
        }
        public Entity doCustomSpawning(EntitySpawnMessage spawnMsg) throws Exception
        {
            return customSpawnCallback.apply(spawnMsg);
        }
        public void setCustomSpawning(Function<EntitySpawnMessage, Entity> callable, boolean usesVanillaSpawning)
        {
            this.customSpawnCallback = callable;
            this.usesVanillaSpawning = usesVanillaSpawning;
        }
    }

    private static final EntityRegistry INSTANCE = new EntityRegistry();

    private BitSet availableIndicies;
    private ListMultimap<ModContainer, EntityRegistration> entityRegistrations = ArrayListMultimap.create();
    private Map<String,ModContainer> entityNames = Maps.newHashMap();
    private BiMap<Class<? extends Entity>, EntityRegistration> entityClassRegistrations = HashBiMap.create();
    public static EntityRegistry instance()
    {
        return INSTANCE;
    }

    private EntityRegistry()
    {
        availableIndicies = new BitSet(256);
        availableIndicies.set(1,255);
        for (Object id : EntityList.IDtoClassMapping.keySet())
        {
            availableIndicies.clear((Integer)id);
        }
    }

    /**
     * Register the mod entity type with FML

     * @param entityClass The entity class
     * @param entityName A unique name for the entity
     * @param id A mod specific ID for the entity
     * @param mod The mod
     * @param trackingRange The range at which MC will send tracking updates
     * @param updateFrequency The frequency of tracking updates
     * @param sendsVelocityUpdates Whether to send velocity information packets as well
     */
    public static void registerModEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        instance().doModEntityRegistration(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    @SuppressWarnings("unchecked")
    private void doModEntityRegistration(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        EntityRegistration er = new EntityRegistration(mc, entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates);
        try
        {
            entityClassRegistrations.put(entityClass, er);
            entityNames.put(entityName, mc);
            if (!EntityList.classToStringMapping.containsKey(entityClass))
            {
                String entityModName = String.format("%s.%s", mc.getModId(), entityName);
                EntityList.classToStringMapping.put(entityClass, entityModName);
                EntityList.stringToClassMapping.put(entityModName, entityClass);
                FMLLog.finer("Automatically registered mod %s entity %s as %s", mc.getModId(), entityName, entityModName);
            }
            else
            {
                FMLLog.fine("Skipping automatic mod %s entity registration for already registered class %s", mc.getModId(), entityClass.getName());
            }
        }
        catch (IllegalArgumentException e)
        {
            FMLLog.log(Level.WARN, e, "The mod %s tried to register the entity (name,class) (%s,%s) one or both of which are already registered", mc.getModId(), entityName, entityClass.getName());
            return;
        }
        entityRegistrations.put(mc, er);
    }

    public static void registerGlobalEntityID(Class <? extends Entity > entityClass, String entityName, int id)
    {
        if (EntityList.classToStringMapping.containsKey(entityClass))
        {
            ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null)
            {
                modId = activeModContainer.getModId();
            }
            else
            {
                FMLLog.severe("There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.");
            }
            FMLLog.warning("The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first", modId, entityClass);
            return;
        }
        id = instance().validateAndClaimId(id);
        EntityList.addMapping(entityClass, entityName, id);
    }

    private int validateAndClaimId(int id)
    {
        // workaround for broken ML
        int realId = id;
        if (id < Byte.MIN_VALUE)
        {
            FMLLog.warning("Compensating for modloader out of range compensation by mod : entityId %d for mod %s is now %d", id, Loader.instance().activeModContainer().getModId(), realId);
            realId += 3000;
        }

        if (realId < 0)
        {
            realId += Byte.MAX_VALUE;
        }
        try
        {
            UnsignedBytes.checkedCast(realId);
        }
        catch (IllegalArgumentException e)
        {
            FMLLog.log(Level.ERROR, "The entity ID %d for mod %s is not an unsigned byte and may not work", id, Loader.instance().activeModContainer().getModId());
        }

        if (!availableIndicies.get(realId))
        {
            FMLLog.severe("The mod %s has attempted to register an entity ID %d which is already reserved. This could cause severe problems", Loader.instance().activeModContainer().getModId(), id);
        }
        availableIndicies.clear(realId);
        return realId;
    }

    public static void registerGlobalEntityID(Class <? extends Entity > entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour)
    {
        if (EntityList.classToStringMapping.containsKey(entityClass))
        {
            ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null)
            {
                modId = activeModContainer.getModId();
            }
            else
            {
                FMLLog.severe("There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.");
            }
            FMLLog.warning("The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first", modId, entityClass);
            return;
        }
        instance().validateAndClaimId(id);
        EntityList.addMapping(entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
    }

    public static void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
        for (BiomeGenBase biome : biomes)
        {
            @SuppressWarnings("unchecked")
            List<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature);

            for (SpawnListEntry entry : spawns)
            {
                //Adjusting an existing spawn entry
                if (entry.entityClass == entityClass)
                {
                    entry.itemWeight = weightedProb;
                    entry.minGroupCount = min;
                    entry.maxGroupCount = max;
                    break;
                }
            }

            spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
        }
    }

    @SuppressWarnings("unchecked")
    public static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(entityName);

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
            Iterator<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature).iterator();

            while (spawns.hasNext())
            {
                SpawnListEntry entry = spawns.next();
                if (entry.entityClass == entityClass)
                {
                    spawns.remove();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void removeSpawn(String entityName, EnumCreatureType spawnList, BiomeGenBase... biomes)
    {
        Class <? extends Entity > entityClazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends EntityLiving>) entityClazz, spawnList, biomes);
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

    public boolean tryTrackingEntity(EntityTracker entityTracker, Entity entity)
    {

        EntityRegistration er = lookupModSpawn(entity.getClass(), true);
        if (er != null)
        {
            entityTracker.addEntityToTracker(entity, er.getTrackingRange(), er.getUpdateFrequency(), er.sendsVelocityUpdates());
            return true;
        }
        return false;
    }
}