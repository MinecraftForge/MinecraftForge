/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common.registry;

import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.Level;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.internal.FMLMessage.EntitySpawnMessage;

import java.util.function.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;

import javax.annotation.Nullable;

public class EntityRegistry
{
    public class EntityRegistration
    {
        @Deprecated
        private Class<? extends Entity> entityClass;
        private Function<World, ? extends Entity> factory;
        private ModContainer container;
        private ResourceLocation regName;
        private String entityName;
        private int modId;
        private int trackingRange;
        private int updateFrequency;
        private boolean sendsVelocityUpdates;
        private Function<EntitySpawnMessage, Entity> customSpawnCallback;
        private boolean usesVanillaSpawning;

        @Deprecated //1.13
        public EntityRegistration(ModContainer mc, ResourceLocation registryName, Class<? extends Entity> entityClass, String entityName, int id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
        {
            this(mc, registryName, entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates, null);
        }

        public EntityRegistration(ModContainer mc, ResourceLocation registryName, Class<? extends Entity> entityClass, String entityName, int id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, Function<World, ? extends Entity> factory)
        {
            this.container = mc;
            this.regName = registryName;
            this.entityClass = entityClass;
            this.entityName = entityName;
            this.modId = id;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
            this.factory = factory != null ? factory :
                new EntityEntryBuilder.ConstructorFactory<Entity>(entityClass) {
                    @Override
                    protected String describeEntity() {
                        return String.valueOf(EntityRegistration.this.getRegistryName());
                    }
                };
        }
        public ResourceLocation getRegistryName()
        {
            return regName;
        }
        @Deprecated //Used only for creating a new instance in EntitySpawnHandler, use newInstance(world) instead.
        public Class<? extends Entity> getEntityClass()
        {
            return entityClass;
        }
        public Entity newInstance(World world)
        {
            return this.factory.apply(world);
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
    public static void registerModEntity(ResourceLocation registryName, Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        instance().doModEntityRegistration(registryName, entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
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
    public static void registerModEntity(ResourceLocation registryName, Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary)
    {
        instance().doModEntityRegistration(registryName, entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
        EntityRegistry.registerEgg(registryName, eggPrimary, eggSecondary);
    }

    private void doModEntityRegistration(ResourceLocation registryName, Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        EntityRegistration er = new EntityRegistration(mc, registryName, entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates);
        try
        {
            entityClassRegistrations.put(entityClass, er);
            if (!ForgeRegistries.ENTITIES.containsKey(registryName))
            {
                EntityEntry entry = new EntityEntry(entityClass, entityName).setRegistryName(registryName);
                ForgeRegistries.ENTITIES.register(entry);
                FMLLog.log.trace("Automatically registered mod {} entity {} as {}", mc.getModId(), entityName, entry.getRegistryName());
            }
            else
            {
                FMLLog.log.debug("Skipping automatic mod {} entity registration for already registered entry {} class {}", mc.getModId(), registryName, entityClass.getName());
            }
        }
        catch (IllegalArgumentException e)
        {
            FMLLog.log.warn("The mod {} tried to register the entity (registry,name,class) ({},{},{}) one or both of which are already registered", mc.getModId(), registryName, entityName, entityClass.getName(), e);
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
     * @param name The entity ResourceLocation
     * @param primary Primary egg color
     * @param secondary Secondary egg color
     *
     */
    public static void registerEgg(ResourceLocation name, int primary, int secondary)
    {
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(name);
        if (entry == null)
        {
            FMLLog.bigWarning("Attempted to registry a entity egg for entity ({}) that is not in the Entity Registry", name);
            return;
        }
        entry.setEgg(new EntityEggInfo(name, primary, secondary));
    }

    /**
     * Add a spawn entry for the supplied entity in the supplied {@link Biome} list
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

            boolean found = false;
            for (SpawnListEntry entry : spawns)
            {
                //Adjusting an existing spawn entry
                if (entry.entityClass == entityClass)
                {
                    entry.itemWeight = weightedProb;
                    entry.minGroupCount = min;
                    entry.maxGroupCount = max;
                    found = true;
                    break;
                }
            }

            if (!found)
                spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
        }
    }

    /**
     * Add a spawn entry for the supplied entity in the supplied {@link Biome} list
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
        Class <? extends Entity > entityClazz = null;

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
            biome.getSpawnableList(typeOfCreature).removeIf(entry -> entry.entityClass == entityClass);
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
        Class <? extends Entity > entityClazz = null;

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends EntityLiving>) entityClazz, typeOfCreature, biomes);
        }
    }

    @Nullable
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
            keepLooking &= (!Object.class.equals(localClazz));
        }
        while (keepLooking);

        return null;
    }

    @Nullable
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
            entityTracker.track(entity, er.getTrackingRange(), er.getUpdateFrequency(), er.sendsVelocityUpdates());
            return true;
        }
        return false;
    }

    //Helper function
    @Nullable
    public static EntityEntry getEntry(Class<? extends Entity> entry)
    {
        //TODO: Slave map for faster lookup?
        for (EntityEntry e : ForgeRegistries.ENTITIES)
        {
            if (e.getEntityClass() == entry)
                return e;
        }
        return null;
    }

    // This is an internal method - do not touch.
    final void insert(final Class<? extends Entity> entity, final EntityRegistration registration)
    {
        this.entityClassRegistrations.put(entity, registration);
        this.entityRegistrations.put(registration.container, registration);
    }
}
