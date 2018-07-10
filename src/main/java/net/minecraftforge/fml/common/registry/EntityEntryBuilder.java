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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * An entity registry entry builder.
 *
 * @param <E> The entity type
 */
public final class EntityEntryBuilder<E extends Entity>
{
    private final ModContainer mod;
    @Nullable private Class<? extends E> entity;
    @Nullable private Function<World, E> factory;
    @Nullable private ResourceLocation id;
    private int network;
    @Nullable private String name;
    private int trackingRange;
    private int trackingUpdateFrequency;
    private boolean trackingVelocityUpdates;
    private boolean eggProvided;
    private int primaryEggColor;
    private int secondaryEggColor;
    private boolean statisticsRegistered;
    @Nullable private StatBase killEntityStatistic;
    @Nullable private StatBase entityKilledByStatistic;
    @Nullable private Collection<Spawn> spawns;

    /**
     * Creates a new entity entry builder.
     *
     * @param <E> The entity type
     * @return A new entity entry builder
     */
    @Nonnull
    public static <E extends Entity> EntityEntryBuilder<E> create()
    {
        return new EntityEntryBuilder<>();
    }

    private EntityEntryBuilder()
    {
        this.mod = Loader.instance().activeModContainer();
    }

    /**
     * Sets the class of the entity.
     *
     * <p>Entities will be constructed using a constructor accepting {@link World}. If you wish
     * to use your own factory, use {@link #factory(Function)}.</p>
     *
     * @param entity The entity class
     * @return This builder
     * @throws NullPointerException If {@code entity} is {@code null}
     */
    @Nonnull
    public final EntityEntryBuilder<E> entity(@Nonnull final Class<? extends E> entity)
    {
        this.entity = checkNotNull(entity, "entity class");
        return this;
    }

    /**
     * Sets the factory of the entity.
     *
     * @param factory The entity factory
     * @return This builder
     * @throws NullPointerException If {@code entity} is {@code null}
     */
    @Nonnull
    public final EntityEntryBuilder<E> factory(@Nonnull final Function<World, E> factory)
    {
        this.factory = checkNotNull(factory, "entity factory");
        return this;
    }

    /**
     * Sets the id of the entity.
     *
     * @param id The entity id
     * @param network The network id
     * @return This builder
     * @throws NullPointerException If {@code id} is {@code null}
     */
    @Nonnull
    public final EntityEntryBuilder<E> id(@Nonnull final ResourceLocation id, final int network)
    {
        this.id = checkNotNull(id, "id");
        this.network = network;
        return this;
    }

    /**
     * Sets the id of the entity.
     *
     * @param id The entity id
     * @param network The network id
     * @return This builder
     * @throws NullPointerException If {@code id} is {@code null}
     */
    @Nonnull
    public final EntityEntryBuilder<E> id(@Nonnull final String id, final int network)
    {
        checkNotNull(id, "id");
        return this.id(new ResourceLocation(id.indexOf(':') == -1 ? this.mod.getModId() + ':' + id : id), network);
    }

    /**
     * Sets the name of the entity.
     *
     * @param name The entity name
     * @return This builder
     * @throws NullPointerException If {@code name} is {@code null}
     */
    @Nonnull
    public final EntityEntryBuilder<E> name(@Nonnull final String name)
    {
        this.name = checkNotNull(name, "name");
        return this;
    }

    /**
     * Sets entity tracking information.
     *
     * @param range The tracking range
     * @param updateFrequency The tracking update frequency
     * @param sendVelocityUpdates If the entity should send velocity updates
     * @return This builder
     */
    @Nonnull
    public final EntityEntryBuilder<E> tracker(final int range, final int updateFrequency, final boolean sendVelocityUpdates)
    {
        this.trackingRange = range;
        this.trackingUpdateFrequency = updateFrequency;
        this.trackingVelocityUpdates = sendVelocityUpdates;
        return this;
    }

    /**
     * Adds a spawn entry.
     *
     * @param type The creature type
     * @param weight The spawn entry weight
     * @param min The minimum spawn count
     * @param max The maximum spawn count
     * @param biomes The biomes to add an entry in
     * @return This builder
     * @throws IllegalArgumentException If the entity is not a {@link EntityLiving}
     * @throws NullPointerException If {@code type} is {@code null}
     * @throws NullPointerException If {@code biomes} is {@code null}
     */
    @Nonnull
    public final EntityEntryBuilder<E> spawn(@Nonnull final EnumCreatureType type, final int weight, final int min, final int max, @Nonnull final Biome... biomes)
    {
        checkNotNull(biomes, "biomes");
        return this.spawn(type, weight, min, max, Arrays.asList(biomes));
    }

    /**
     * Adds a spawn entry.
     *
     * @param type The creature type
     * @param weight The spawn entry weight
     * @param min The minimum spawn count
     * @param max The maximum spawn count
     * @param biomes The biomes to add an entry in
     * @return This builder
     * @throws IllegalArgumentException If the entity is not a {@link EntityLiving}
     * @throws NullPointerException If {@code type} is {@code null}
     * @throws NullPointerException If {@code biomes} is {@code null}
     */
    @Nonnull
    public final EntityEntryBuilder<E> spawn(@Nonnull final EnumCreatureType type, final int weight, final int min, final int max, @Nonnull final Iterable<Biome> biomes)
    {
        checkNotNull(type, "type");
        checkNotNull(biomes, "biomes");
        if (this.entity != null) checkArgument(EntityLiving.class.isAssignableFrom(this.entity), "Cannot add spawns to a non-%s", EntityLiving.class.getSimpleName());
        if (this.spawns == null) this.spawns = new ArrayList<>();
        this.spawns.add(new Spawn(type, weight, min, max, biomes));
        return this;
    }

    /**
     * Sets the egg of the entity.
     *
     * @param primaryColor the primary egg color
     * @param secondaryColor the secondary egg color
     * @return This builder
     */
    @Nonnull
    public final EntityEntryBuilder<E> egg(final int primaryColor, final int secondaryColor)
    {
        this.eggProvided = true;
        this.primaryEggColor = primaryColor;
        this.secondaryEggColor = secondaryColor;
        return this;
    }

    /**
     * Create an entity entry based on the data in this builder.
     *
     * @return The entity entry
     * @throws IllegalStateException If the entity class has not been provided
     * @throws IllegalStateException If the entity id has not been provided
     * @throws IllegalStateException If the entity name has not been provided
     * @throws IllegalStateException If spawns have been provided for a non {@link EntityLiving}
     * @throws ReflectionHelper.UnknownConstructorException If a {@link #factory} has not been provided
     *     and {@link #entity} does not have a constructor accepting {@link World}
     */
    @Nonnull
    public EntityEntry build()
    {
        checkState(this.entity != null, "entity class not provided");
        checkState(this.id != null, "entity id not provided");
        checkState(this.name != null, "entity name not provided");
        if (this.spawns != null) checkState(EntityLiving.class.isAssignableFrom(EntityEntryBuilder.this.entity), "Cannot add spawns to a non-%s", EntityLiving.class.getSimpleName());
        final BuiltEntityEntry entry = new BuiltEntityEntry(this.entity, this.name);
        entry.factory = this.factory != null ? this.factory : new ConstructorFactory<E>(this.entity) {
            @Override
            protected String describeEntity() {
                return String.valueOf(EntityEntryBuilder.this.id);
            }
        };
        entry.setRegistryName(this.id);
        if (this.eggProvided)
        {
            this.killEntityStatistic = new StatBase("stat.killEntity." + this.name, new TextComponentTranslation("stat.entityKill", new TextComponentTranslation("entity." + this.name + ".name")));
            this.entityKilledByStatistic = new StatBase("stat.entityKilledBy." + this.name, new TextComponentTranslation("stat.entityKilledBy", new TextComponentTranslation("entity." + this.name + ".name")));
            entry.setEgg(new EntityList.EntityEggInfo(this.id, this.primaryEggColor, this.secondaryEggColor, this.killEntityStatistic, this.entityKilledByStatistic));
        }
        return entry;
    }

    private void registerStatistics()
    {
        if (!this.statisticsRegistered && (this.killEntityStatistic != null && this.entityKilledByStatistic != null))
        {
            this.killEntityStatistic.registerStat();
            this.entityKilledByStatistic.registerStat();
            this.statisticsRegistered = true;
        }
    }

    static abstract class ConstructorFactory<E extends Entity> implements Function<World, E>
    {
        private final Constructor<? extends E> constructor;

        ConstructorFactory(final Class<? extends E> entity)
        {
            this.constructor = ReflectionHelper.findConstructor(entity, World.class);
        }

        @Override
        public E apply(final World world)
        {
            try
            {
                return this.constructor.newInstance(world);
            }
            catch (final IllegalAccessException | InstantiationException | InvocationTargetException e)
            {
                FMLLog.log.error("Encountered an exception while constructing entity '{}'", this.describeEntity(), e);
                return null;
            }
        }

        protected abstract String describeEntity();
    }

    public final class BuiltEntityEntry extends EntityEntry
    {
        private boolean added;

        BuiltEntityEntry(final Class<? extends Entity> cls, final String name)
        {
            super(cls, name);
        }

        @Override
        protected final void init() {
            // NOOP - we handle this in build
        }

        public final void addedToRegistry()
        {
            if (this.added) return;
            this.added = true;
            EntityEntryBuilder.this.registerStatistics();
            EntityRegistry.instance().insert(EntityEntryBuilder.this.entity, createRegistration());
            if (EntityEntryBuilder.this.spawns != null)
            {
                for (final Spawn spawn : EntityEntryBuilder.this.spawns)
                {
                    spawn.insert();
                }
                EntityEntryBuilder.this.spawns = null;
            }
        }

        @Nonnull
        private EntityRegistry.EntityRegistration createRegistration()
        {
            EntityEntryBuilder<E> builder = EntityEntryBuilder.this;
            return EntityRegistry.instance().new EntityRegistration(
                builder.mod, builder.id, builder.entity, builder.name, builder.network,
                builder.trackingRange, builder.trackingUpdateFrequency, builder.trackingVelocityUpdates, this.factory
            );
        }

    }

    public final class Spawn
    {
        private final EnumCreatureType type;
        private final int weight;
        private final int min;
        private final int max;
        private final Iterable<Biome> biomes;

        public Spawn(final EnumCreatureType type, final int weight, final int min, final int max, final Iterable<Biome> biomes)
        {
            this.type = type;
            this.weight = weight;
            this.min = min;
            this.max = max;
            this.biomes = biomes;
        }

        @SuppressWarnings("unchecked")
        final void insert()
        {
            for (final Biome biome : this.biomes) {
                final List<Biome.SpawnListEntry> entries = biome.getSpawnableList(this.type);
                boolean found = false;
                for (final Biome.SpawnListEntry entry : entries) {
                    if (entry.entityClass == EntityEntryBuilder.this.entity) {
                        entry.itemWeight = this.weight;
                        entry.minGroupCount = this.min;
                        entry.maxGroupCount = this.max;
                        found = true;
                        break;
                    }
                }
                if (!found) entries.add(new Biome.SpawnListEntry((Class<? extends EntityLiving>) EntityEntryBuilder.this.entity, this.weight, this.min, this.max));
            }
        }
    }
}
