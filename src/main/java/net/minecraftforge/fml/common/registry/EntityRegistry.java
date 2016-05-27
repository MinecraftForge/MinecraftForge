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

package net.minecraftforge.fml.common.registry;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.internal.FMLMessage.EntitySpawnMessage;

import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedBytes;

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

    private ListMultimap<ModContainer, EntityRegistration> entityRegistrations = ArrayListMultimap.create();
    private Map<String,ModContainer> entityNames = Maps.newHashMap();
    private BiMap<Class<? extends Entity>, EntityRegistration> entityClassRegistrations = HashBiMap.create();

    public static EntityRegistry instance()
    {
        return INSTANCE;
    }

    private EntityRegistry()
    {
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

    /**
     * Register the mod entity type with FML
     * This will also register a spawn egg.

     * @param entityClass The entity class
     * @param entityName A unique name for the entity
     * @param id A mod specific ID for the entity
     * @param mod The mod
     * @param trackingRange The range at which MC will send tracking updates
     * @param updateFrequency The frequency of tracking updates
     * @param sendsVelocityUpdates Whether to send velocity information packets as well
     * @param eggPrimary Primary egg color
     * @param eggSecondary Secondary egg color
     */
    public static void registerModEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary)
    {
        instance().doModEntityRegistration(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
        EntityRegistry.registerEgg(entityClass, eggPrimary, eggSecondary);
    }

    private void doModEntityRegistration(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        EntityRegistration er = new EntityRegistration(mc, entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates);
        try
        {
            entityClassRegistrations.put(entityClass, er);
            entityNames.put(entityName, mc);
            if (!EntityList.CLASS_TO_NAME.containsKey(entityClass))
            {
                String entityModName = String.format("%s.%s", mc.getModId(), entityName);
                EntityList.CLASS_TO_NAME.put(entityClass, entityModName);
                EntityList.NAME_TO_CLASS.put(entityModName, entityClass);
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

    /**
     * Registers a spawn egg for the specified entity class.
     * The class must already be registered in the EntityList.classToStringMapping.
     * This can be done either by using the global ID system, or preferably the registerModEntity functions above.
     * Once registered mob eggs can be created by using minecraft:spawn_egg with NBT entry 'entity_name' with
     * value of the name this class is registered in the classToStringMapping with.
     *
     * @param entityClass The entity class
     * @param primary Primary egg color
     * @param secondary Secondary egg color
     *
     * @throws IllegalArgumentException if entityClass is not registered in classToStringMapping.
     *
     */

    public static void registerEgg(Class<? extends Entity> entityClass, int primary, int secondary)
    {
        if (EntityList.CLASS_TO_NAME.containsKey(entityClass))
        {
            String name = EntityList.CLASS_TO_NAME.get(entityClass);
            EntityList.ENTITY_EGGS.put(name, new EntityList.EntityEggInfo(name, primary, secondary));
            FMLLog.fine("Registering entity egg '%s' for %s", name, entityClass);
        }
        else
        {
            FMLLog.fine("Failed registering entity egg %s (No entity found)", entityClass.getName());
        }
    }

    /**
     * Add a spawn entry for the supplied entity in the supplied {@link BiomeGenBase} list
     * @param entityClass Entity class added
     * @param weightedProb Probability
     * @param min Min spawn count
     * @param max Max spawn count
     * @param typeOfCreature Type of spawn
     * @param biomes List of biomes
     */
    public static void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, Biome... biomes)
    {
        for (Biome biome : biomes)
        {
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

    /**
     * Add a spawn entry for the supplied entity in the supplied {@link BiomeGenBase} list
     * @param entityName The entity name
     * @param weightedProb Probability
     * @param min Min spawn count
     * @param max Max spawn count
     * @param typeOfCreature type of spawn
     * @param biomes List of biomes
     */
    @SuppressWarnings("unchecked")
    public static void addSpawn(String entityName, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, Biome... biomes)
    {
        Class <? extends Entity > entityClazz = EntityList.NAME_TO_CLASS.get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            addSpawn((Class <? extends EntityLiving >) entityClazz, weightedProb, min, max, typeOfCreature, biomes);
        }
    }

    /**
     * Remove the spawn entry for the supplied entity
     * @param entityClass The entity class
     * @param typeOfCreature type of spawn
     * @param biomes Biomes to remove from
     */
    public static void removeSpawn(Class <? extends EntityLiving > entityClass, EnumCreatureType typeOfCreature, Biome... biomes)
    {
        for (Biome biome : biomes)
        {
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

    /**
     * Remove the spawn entry for the supplied entity
     * @param entityName Name of entity being removed
     * @param typeOfCreature type of spawn
     * @param biomes Biomes to remove from
     */
    @SuppressWarnings("unchecked")
    public static void removeSpawn(String entityName, EnumCreatureType typeOfCreature, Biome... biomes)
    {
        Class <? extends Entity > entityClazz = EntityList.NAME_TO_CLASS.get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends EntityLiving>) entityClazz, typeOfCreature, biomes);
        }
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
