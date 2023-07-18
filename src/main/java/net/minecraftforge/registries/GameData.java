/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.IdMapper;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.util.EnhancedRuntimeException;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.holdersets.HolderSetType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * INTERNAL ONLY
 * MODDERS SHOULD HAVE NO REASON TO USE THIS CLASS
 */
@ApiStatus.Internal
public class GameData
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static Marker REGISTRIES = ForgeRegistry.REGISTRIES;
    private static final int MAX_VARINT = Integer.MAX_VALUE - 1; //We were told it is their intention to have everything in a reg be unlimited, so assume that until we find cases where it isnt.

    private static final ResourceLocation BLOCK_TO_ITEM = new ResourceLocation("minecraft:blocktoitemmap");
    private static final ResourceLocation BLOCKSTATE_TO_ID = new ResourceLocation("minecraft:blockstatetoid");
    private static final ResourceLocation BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE = new ResourceLocation("minecraft:blockstatetopointofinteresttype");

    private static boolean hasInit = false;
    private static final boolean DISABLE_VANILLA_REGISTRIES = Boolean.parseBoolean(System.getProperty("forge.disableVanillaGameData", "false")); // Use for unit tests/debugging
    private static final BiConsumer<ResourceLocation, ForgeRegistry<?>> LOCK_VANILLA = (name, reg) -> reg.slaves.values().stream().filter(o -> o instanceof ILockableRegistry).forEach(o -> ((ILockableRegistry)o).lock());

    static {
        init();
    }

    public static void init()
    {
        if (DISABLE_VANILLA_REGISTRIES)
        {
            LOGGER.warn(REGISTRIES, "DISABLING VANILLA REGISTRY CREATION AS PER SYSTEM VARIABLE SETTING! forge.disableVanillaGameData");
            return;
        }
        if (hasInit)
            return;
        hasInit = true;

        // Game objects
        makeRegistry(Keys.BLOCKS, "air").addCallback(BlockCallbacks.INSTANCE).legacyName("blocks").intrusiveHolderCallback(Block::builtInRegistryHolder).create();
        makeRegistry(Keys.FLUIDS, "empty").intrusiveHolderCallback(Fluid::builtInRegistryHolder).create();
        makeRegistry(Keys.ITEMS, "air").addCallback(ItemCallbacks.INSTANCE).legacyName("items").intrusiveHolderCallback(Item::builtInRegistryHolder).create();
        makeRegistry(Keys.MOB_EFFECTS).legacyName("potions").create();
        makeRegistry(Keys.SOUND_EVENTS).legacyName("soundevents").create();
        makeRegistry(Keys.POTIONS, "empty").legacyName("potiontypes").create();
        makeRegistry(Keys.ENCHANTMENTS).legacyName("enchantments").create();
        makeRegistry(Keys.ENTITY_TYPES, "pig").legacyName("entities").intrusiveHolderCallback(EntityType::builtInRegistryHolder).create();
        makeRegistry(Keys.BLOCK_ENTITY_TYPES).disableSaving().legacyName("blockentities").create();
        makeRegistry(Keys.PARTICLE_TYPES).disableSaving().create();
        makeRegistry(Keys.MENU_TYPES).disableSaving().create();
        makeRegistry(Keys.PAINTING_VARIANTS, "kebab").create();
        makeRegistry(Keys.RECIPE_TYPES).disableSaving().disableSync().create();
        makeRegistry(Keys.RECIPE_SERIALIZERS).disableSaving().create();
        makeRegistry(Keys.ATTRIBUTES).onValidate(AttributeCallbacks.INSTANCE).disableSaving().disableSync().create();
        makeRegistry(Keys.STAT_TYPES).create();
        makeRegistry(Keys.COMMAND_ARGUMENT_TYPES).disableSaving().create();

        // Villagers
        makeRegistry(Keys.VILLAGER_PROFESSIONS, "none").create();
        makeRegistry(Keys.POI_TYPES).addCallback(PointOfInterestTypeCallbacks.INSTANCE).disableSync().create();
        makeRegistry(Keys.MEMORY_MODULE_TYPES, "dummy").disableSync().create();
        makeRegistry(Keys.SENSOR_TYPES, "dummy").disableSaving().disableSync().create();
        makeRegistry(Keys.SCHEDULES).disableSaving().disableSync().create();
        makeRegistry(Keys.ACTIVITIES).disableSaving().disableSync().create();

        // Worldgen
        makeRegistry(Keys.WORLD_CARVERS).disableSaving().disableSync().create();
        makeRegistry(Keys.FEATURES).disableSaving().disableSync().create();
        makeRegistry(Keys.CHUNK_STATUS, "empty").disableSaving().disableSync().create();
        makeRegistry(Keys.BLOCK_STATE_PROVIDER_TYPES).disableSaving().disableSync().create();
        makeRegistry(Keys.FOLIAGE_PLACER_TYPES).disableSaving().disableSync().create();
        makeRegistry(Keys.TREE_DECORATOR_TYPES).disableSaving().disableSync().create();

        // Dynamic Worldgen
        makeRegistry(Keys.BIOMES).disableSync().create();
    }

    static RegistryBuilder<EntityDataSerializer<?>> getDataSerializersRegistryBuilder()
    {
        return makeRegistry(Keys.ENTITY_DATA_SERIALIZERS, 256 /*vanilla space*/, MAX_VARINT).disableSaving().disableOverrides();
    }

    static RegistryBuilder<Codec<? extends IGlobalLootModifier>> getGLMSerializersRegistryBuilder()
    {
        return makeRegistry(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS).disableSaving().disableSync();
    }

    static RegistryBuilder<Codec<? extends BiomeModifier>> getBiomeModifierSerializersRegistryBuilder()
    {
        return new RegistryBuilder<Codec<? extends BiomeModifier>>().disableSaving().disableSync();
    }

    static RegistryBuilder<Codec<? extends StructureModifier>> getStructureModifierSerializersRegistryBuilder()
    {
        return new RegistryBuilder<Codec<? extends StructureModifier>>().disableSaving().disableSync();
    }

    static RegistryBuilder<FluidType> getFluidTypeRegistryBuilder()
    {
        return makeRegistry(Keys.FLUID_TYPES).disableSaving();
    }

    static RegistryBuilder<HolderSetType> getHolderSetTypeRegistryBuilder()
    {
        return new RegistryBuilder<HolderSetType>().disableSaving().disableSync();
    }

    static RegistryBuilder<ItemDisplayContext> getItemDisplayContextRegistryBuilder()
    {
        return new RegistryBuilder<ItemDisplayContext>()
            .setMaxID(128 * 2) /* 0 -> 127 gets positive ID, 128 -> 256 gets negative ID */.disableOverrides().disableSaving()
            .setDefaultKey(new ResourceLocation("minecraft:none"))
            .onAdd(ItemDisplayContext.ADD_CALLBACK);
    }

    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key)
    {
        return new RegistryBuilder<T>().setName(key.location()).setMaxID(MAX_VARINT).hasWrapper();
    }
    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key, int min, int max)
    {
        return new RegistryBuilder<T>().setName(key.location()).setIDRange(min, max).hasWrapper();
    }
    private static <T> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key, String _default)
    {
        return new RegistryBuilder<T>().setName(key.location()).setMaxID(MAX_VARINT).hasWrapper().setDefaultKey(new ResourceLocation(_default));
    }

    public static <T> MappedRegistry<T> getWrapper(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle)
    {
        IForgeRegistry<T> reg = RegistryManager.ACTIVE.getRegistry(key);
        Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + key.toString());
        @SuppressWarnings("unchecked")
        MappedRegistry<T> ret = reg.getSlaveMap(NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
        Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + key.toString());
        return ret;
    }

    public static <T> MappedRegistry<T> getWrapper(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, String defKey)
    {
        IForgeRegistry<T> reg = RegistryManager.ACTIVE.getRegistry(key);
        Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + key.toString());
        @SuppressWarnings("unchecked")
        MappedRegistry<T> ret = reg.getSlaveMap(NamespacedDefaultedWrapper.Factory.ID, NamespacedDefaultedWrapper.class);
        Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + key.toString());
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static Map<Block,Item> getBlockItemMap()
    {
        return RegistryManager.ACTIVE.getRegistry(Keys.ITEMS).getSlaveMap(BLOCK_TO_ITEM, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static IdMapper<BlockState> getBlockStateIDMap()
    {
        return RegistryManager.ACTIVE.getRegistry(Keys.BLOCKS).getSlaveMap(BLOCKSTATE_TO_ID, IdMapper.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<BlockState, PoiType> getBlockStatePointOfInterestTypeMap()
    {
        return RegistryManager.ACTIVE.getRegistry(Keys.POI_TYPES).getSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, Map.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void vanillaSnapshot()
    {
        LOGGER.debug(REGISTRIES, "Creating vanilla freeze snapshot");
        for (Map.Entry<ResourceLocation, ForgeRegistry<?>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            loadRegistry(r.getKey(), RegistryManager.ACTIVE, RegistryManager.VANILLA, true);
        }
        RegistryManager.VANILLA.registries.forEach((name, reg) ->
        {
            reg.validateContent(name);
            reg.freeze();
        });
        RegistryManager.VANILLA.registries.forEach(LOCK_VANILLA);
        RegistryManager.ACTIVE.registries.forEach(LOCK_VANILLA);
        LOGGER.debug(REGISTRIES, "Vanilla freeze snapshot created");
    }

    public static void unfreezeData()
    {
        LOGGER.debug(REGISTRIES, "Unfreezing vanilla registries");
        BuiltInRegistries.REGISTRY.stream().filter(r -> r instanceof MappedRegistry).forEach(r -> ((MappedRegistry<?>)r).unfreeze());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void freezeData()
    {
        LOGGER.debug(REGISTRIES, "Freezing registries");
        BuiltInRegistries.REGISTRY.stream().filter(r -> r instanceof MappedRegistry).forEach(r -> ((MappedRegistry<?>)r).freeze());

        for (Map.Entry<ResourceLocation, ForgeRegistry<?>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            loadRegistry(r.getKey(), RegistryManager.ACTIVE, RegistryManager.FROZEN, true);
        }
        RegistryManager.FROZEN.registries.forEach((name, reg) ->
        {
            reg.validateContent(name);
            reg.freeze();
        });
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.freeze();
            reg.bake();
            reg.dump(name);
        });

        // the id mapping is finalized, no ids actually changed but this is a good place to tell everyone to 'bake' their stuff.
        fireRemapEvent(ImmutableMap.of(), true);

        LOGGER.debug(REGISTRIES, "All registries frozen");
    }

    public static void revertToFrozen() {
        revertTo(RegistryManager.FROZEN, true);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void revertTo(final RegistryManager target, boolean fireEvents)
    {
        if (target.registries.isEmpty())
        {
            LOGGER.warn(REGISTRIES, "Can't revert to {} GameData state without a valid snapshot.", target.getName());
            return;
        }
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());

        LOGGER.debug(REGISTRIES, "Reverting to {} data state.", target.getName());
        for (Map.Entry<ResourceLocation, ForgeRegistry<?>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            loadRegistry(r.getKey(), target, RegistryManager.ACTIVE, true);
        }
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.bake());
        // the id mapping has reverted, fire remap events for those that care about id changes
        if (fireEvents) {
            fireRemapEvent(ImmutableMap.of(), true);
            ObjectHolderRegistry.applyObjectHolders();
        }

        // the id mapping has reverted, ensure we sync up the object holders
        LOGGER.debug(REGISTRIES, "{} state restored.", target.getName());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void revert(RegistryManager state, ResourceLocation registry, boolean lock)
    {
        LOGGER.debug(REGISTRIES, "Reverting {} to {}", registry, state.getName());
        loadRegistry(registry, state, RegistryManager.ACTIVE, lock);
        LOGGER.debug(REGISTRIES, "Reverting complete");
    }

    @SuppressWarnings("deprecation")
    public static void postRegisterEvents()
    {
        Set<ResourceLocation> keySet = new HashSet<>(RegistryManager.ACTIVE.registries.keySet());
        keySet.addAll(RegistryManager.getVanillaRegistryKeys());

        Set<ResourceLocation> ordered = new LinkedHashSet<>(MappedRegistry.getKnownRegistries());
        ordered.retainAll(keySet);
        ordered.addAll(keySet.stream().sorted(ResourceLocation::compareNamespaced).toList());

        RuntimeException aggregate = new RuntimeException();
        for (ResourceLocation rootRegistryName : ordered)
        {
            try
            {
                ResourceKey<? extends Registry<?>> registryKey = ResourceKey.createRegistryKey(rootRegistryName);
                ForgeRegistry<?> forgeRegistry = RegistryManager.ACTIVE.getRegistry(rootRegistryName);
                Registry<?> vanillaRegistry = BuiltInRegistries.REGISTRY.get(rootRegistryName);
                RegisterEvent registerEvent = new RegisterEvent(registryKey, forgeRegistry, vanillaRegistry);

                StartupMessageManager.modLoaderConsumer().ifPresent(s -> s.accept("REGISTERING " + registryKey.location()));
                if (forgeRegistry != null)
                    forgeRegistry.unfreeze();

                ModLoader.get().postEventWrapContainerInModOrder(registerEvent);

                if (forgeRegistry != null)
                    forgeRegistry.freeze();
                LOGGER.debug(REGISTRIES, "Applying holder lookups: {}", registryKey.location());
                ObjectHolderRegistry.applyObjectHolders(registryKey.location()::equals);
                LOGGER.debug(REGISTRIES, "Holder lookups applied: {}", registryKey.location());
            } catch (Throwable t)
            {
                aggregate.addSuppressed(t);
            }
        }
        if (aggregate.getSuppressed().length > 0)
        {
            LOGGER.fatal("Failed to register some entries, see suppressed exceptions for details", aggregate);
            LOGGER.fatal("Detected errors during registry event dispatch, rolling back to VANILLA state");
            revertTo(RegistryManager.VANILLA, false);
            LOGGER.fatal("Detected errors during registry event dispatch, roll back to VANILLA complete");
            throw aggregate;
        } else
        {
            ForgeHooks.modifyAttributes();
            SpawnPlacements.fireSpawnPlacementEvent();
            CreativeModeTabRegistry.sortTabs();
        }
    }

    //Lets us clear the map so we can rebuild it.
    private static class ClearableObjectIntIdentityMap<I> extends IdMapper<I>
    {
        void clear()
        {
            this.tToId.clear();
            this.idToT.clear();
            this.nextId = 0;
        }

        void remove(I key)
        {
            Integer prev = this.tToId.remove(key);
            if (prev != null)
            {
                this.idToT.set(prev, null);
            }
        }
    }

    private static class BlockCallbacks implements IForgeRegistry.AddCallback<Block>, IForgeRegistry.ClearCallback<Block>, IForgeRegistry.BakeCallback<Block>, IForgeRegistry.CreateCallback<Block>
    {
        static final BlockCallbacks INSTANCE = new BlockCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<Block> owner, RegistryManager stage, int id, ResourceKey<Block> key, Block block, @Nullable Block oldBlock)
        {
            if (oldBlock != null)
            {
                StateDefinition<Block, BlockState> oldContainer = oldBlock.getStateDefinition();
                StateDefinition<Block, BlockState> newContainer = block.getStateDefinition();

                // Test vanilla blockstates, if the number matches, make sure they also match in their string representations
                if (key.location().getNamespace().equals("minecraft") && !oldContainer.getProperties().equals(newContainer.getProperties()))
                {
                    String oldSequence = oldContainer.getProperties().stream()
                            .map(s -> String.format(Locale.ENGLISH, "%s={%s}", s.getName(),
                                    s.getPossibleValues().stream().map(Object::toString).collect(Collectors.joining( "," ))))
                            .collect(Collectors.joining(";"));
                    String newSequence = newContainer.getProperties().stream()
                            .map(s -> String.format(Locale.ENGLISH, "%s={%s}", s.getName(),
                                    s.getPossibleValues().stream().map(Object::toString).collect(Collectors.joining( "," ))))
                            .collect(Collectors.joining(";"));

                    LOGGER.error(REGISTRIES,()-> LogMessageAdapter.adapt(sb-> {
                        sb.append("Registry replacements for vanilla block '").append(key.location())
                                .append("' must not change the number or order of blockstates.\n");
                        sb.append("\tOld: ").append(oldSequence).append('\n');
                        sb.append("\tNew: ").append(newSequence);
                    }));
                    throw new RuntimeException("Invalid vanilla replacement. See log for details.");
                }
            }
        }

        @Override
        public void onClear(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
            owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
            final ClearableObjectIntIdentityMap<BlockState> idMap = new ClearableObjectIntIdentityMap<BlockState>()
            {
                @Override
                public int getId(BlockState key)
                {
                    Integer integer = (Integer)this.tToId.get(key);
                    // There are some cases where this map is queried to serialize a state that is valid,
                    //but somehow not in this list, so attempt to get real metadata. Doing this hear saves us 7 patches
                    //if (integer == null && key != null)
                    //    integer = this.identityMap.get(key.getBlock().getStateFromMeta(key.getBlock().getMetaFromState(key)));
                    return integer == null ? -1 : integer.intValue();
                }
            };
            owner.setSlaveMap(BLOCKSTATE_TO_ID, idMap);
            owner.setSlaveMap(BLOCK_TO_ITEM, Maps.newHashMap());
        }

        @Override
        public void onBake(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
            @SuppressWarnings("unchecked")
            ClearableObjectIntIdentityMap<BlockState> blockstateMap = owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class);

            for (Block block : owner)
            {
                for (BlockState state : block.getStateDefinition().getPossibleStates())
                {
                    blockstateMap.add(state);
                    state.initCache();
                }

                block.getLootTable();
            }
            DebugLevelSource.initValidStates();
        }
    }

    private static class ItemCallbacks implements IForgeRegistry.AddCallback<Item>, IForgeRegistry.ClearCallback<Item>, IForgeRegistry.CreateCallback<Item>
    {
        static final ItemCallbacks INSTANCE = new ItemCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<Item> owner, RegistryManager stage, int id, ResourceKey<Item> key, Item item, @Nullable Item oldItem)
        {
            if (oldItem instanceof BlockItem)
            {
                @SuppressWarnings("unchecked")
                Map<Block, Item> blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, Map.class);
                ((BlockItem)oldItem).removeFromBlockToItemMap(blockToItem, item);
            }
            if (item instanceof BlockItem)
            {
                @SuppressWarnings("unchecked")
                Map<Block, Item> blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, Map.class);
                ((BlockItem)item).registerBlocks(blockToItem, item);
            }
        }

        @Override
        public void onClear(IForgeRegistryInternal<Item> owner, RegistryManager stage)
        {
            owner.getSlaveMap(BLOCK_TO_ITEM, Map.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Item> owner, RegistryManager stage)
        {
            // We share the blockItem map between items and blocks registries
            Map<?, ?> map = stage.getRegistry(Keys.BLOCKS).getSlaveMap(BLOCK_TO_ITEM, Map.class);
            owner.setSlaveMap(BLOCK_TO_ITEM, map);
        }
    }

    private static class AttributeCallbacks implements IForgeRegistry.ValidateCallback<Attribute> {

        static final AttributeCallbacks INSTANCE = new AttributeCallbacks();

        @Override
        public void onValidate(IForgeRegistryInternal<Attribute> owner, RegistryManager stage, int id, ResourceLocation key, Attribute obj)
        {
            // some stuff hard patched in can cause this to derp if it's JUST vanilla, so skip
            if (stage!=RegistryManager.VANILLA) DefaultAttributes.validate();
        }
    }

    private static class PointOfInterestTypeCallbacks implements IForgeRegistry.AddCallback<PoiType>, IForgeRegistry.ClearCallback<PoiType>, IForgeRegistry.CreateCallback<PoiType>
    {
        static final PointOfInterestTypeCallbacks INSTANCE = new PointOfInterestTypeCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<PoiType> owner, RegistryManager stage, int id, ResourceKey<PoiType> key, PoiType obj, @Nullable PoiType oldObj)
        {
            Map<BlockState, PoiType> map = owner.getSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, Map.class);
            if (oldObj != null)
            {
                oldObj.matchingStates().forEach(map::remove);
            }
            obj.matchingStates().forEach((state) ->
            {
                PoiType oldType = map.put(state, obj);
                if (oldType != null)
                {
                    throw new IllegalStateException(String.format(Locale.ENGLISH, "Point of interest types %s and %s both list %s in their blockstates, this is not allowed. Blockstates can only have one point of interest type each.", oldType, obj, state));
                }
            });
        }

        @Override
        public void onClear(IForgeRegistryInternal<PoiType> owner, RegistryManager stage)
        {
            owner.getSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, Map.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<PoiType> owner, RegistryManager stage)
        {
            owner.setSlaveMap(BLOCKSTATE_TO_POINT_OF_INTEREST_TYPE, new HashMap<>());
        }
    }

    private static <T> void loadRegistry(final ResourceLocation registryName, final RegistryManager from, final RegistryManager to, boolean freeze)
    {
        ForgeRegistry<T> fromRegistry = from.getRegistry(registryName);
        if (fromRegistry == null)
        {
            ForgeRegistry<T> toRegistry = to.getRegistry(registryName);
            if (toRegistry == null)
            {
                throw new EnhancedRuntimeException("Could not find registry to load: " + registryName){
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void printStackTrace(WrappedPrintStream stream)
                    {
                        stream.println("Looking For: " + registryName);
                        stream.println("Found From:");
                        for (ResourceLocation name : from.registries.keySet())
                            stream.println("  " + name);
                        stream.println("Found To:");
                        for (ResourceLocation name : to.registries.keySet())
                            stream.println("  " + name);
                    }
                };
            }
            // We found it in to, so lets trust to's state...
            // This happens when connecting to a server that doesn't have this registry.
            // Such as a 1.8.0 Forge server with 1.8.8+ Forge.
            // We must however, re-fire the callbacks as some internal data may be corrupted {potions}
            //TODO: With my rework of how registries add callbacks are done.. I don't think this is necessary.
            //fire addCallback for each entry
        }
        else
        {
            ForgeRegistry<T> toRegistry = to.getRegistry(registryName, from);
            toRegistry.sync(registryName, fromRegistry);
            if (freeze)
                toRegistry.isFrozen = true;
        }
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Multimap<ResourceLocation, ResourceLocation> injectSnapshot(Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot, boolean injectFrozenData, boolean isLocalWorld)
    {
        LOGGER.info(REGISTRIES, "Injecting existing registry data into this {} instance", EffectiveSide.get());
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.validateContent(name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.dump(name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());

        // Update legacy names
        snapshot = snapshot.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // FIXME Registries need dependency ordering, this makes sure blocks are done before items (for ItemCallbacks) but it's lazy as hell
                .collect(Collectors.toMap(e -> RegistryManager.ACTIVE.updateLegacyName(e.getKey()), Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));

        if (isLocalWorld)
        {
            List<ResourceLocation> missingRegs = snapshot.keySet().stream().filter(name -> !RegistryManager.ACTIVE.registries.containsKey(name)).collect(Collectors.toList());
            if (missingRegs.size() > 0)
            {
                String header = "Forge Mod Loader detected missing/unknown registrie(s).\n\n" +
                        "There are " + missingRegs.size() + " missing registries in this save.\n" +
                        "If you continue the missing registries will get removed.\n" +
                        "This may cause issues, it is advised that you create a world backup before continuing.\n\n";

                StringBuilder text = new StringBuilder("Missing Registries:\n");

                for (ResourceLocation s : missingRegs)
                    text.append(s).append("\n");

                LOGGER.warn(REGISTRIES, header);
                LOGGER.warn(REGISTRIES, text.toString());
            }
        }

        RegistryManager STAGING = new RegistryManager();

        final Map<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>> remaps = Maps.newHashMap();
        final LinkedHashMap<ResourceLocation, Map<ResourceLocation, Integer>> missing = Maps.newLinkedHashMap();
        // Load the snapshot into the "STAGING" registry
        snapshot.forEach((key, value) ->
        {
            remaps.put(key, Maps.newLinkedHashMap());
            missing.put(key, Maps.newLinkedHashMap());
            loadPersistentDataToStagingRegistry(RegistryManager.ACTIVE, STAGING, remaps.get(key), missing.get(key), key, value);
        });

        int count = missing.values().stream().mapToInt(Map::size).sum();
        if (count > 0)
        {
            LOGGER.debug(REGISTRIES,"There are {} mappings missing - attempting a mod remap", count);
            Multimap<ResourceLocation, ResourceLocation> defaulted = ArrayListMultimap.create();
            Multimap<ResourceLocation, ResourceLocation> failed = ArrayListMultimap.create();

            missing.entrySet().stream().filter(e -> e.getValue().size() > 0).forEach(m ->
            {
                ResourceLocation name = m.getKey();
                ForgeRegistry<?> reg = STAGING.getRegistry(name);
                MissingMappingsEvent event = reg.getMissingEvent(name, m.getValue());
                MinecraftForge.EVENT_BUS.post(event);

                List<MissingMappingsEvent.Mapping<?>> lst = event.getAllMappings(reg.getRegistryKey()).stream()
                        .filter(e -> e.action == MissingMappingsEvent.Action.DEFAULT)
                        .sorted(Comparator.comparing(Object::toString))
                        .collect(Collectors.toList());
                if (!lst.isEmpty())
                {
                    LOGGER.error(REGISTRIES, () -> LogMessageAdapter.adapt(sb -> {
                       sb.append("Unidentified mapping from registry ").append(name).append('\n');
                       lst.stream().sorted().forEach(map -> sb.append('\t').append(map.key).append(": ").append(map.id).append('\n'));
                    }));
                }
                event.getAllMappings(reg.getRegistryKey()).stream()
                        .filter(e -> e.action == MissingMappingsEvent.Action.FAIL)
                        .forEach(fail -> failed.put(name, fail.key));

                processMissing(name, STAGING, event, m.getValue(), remaps.get(name), defaulted.get(name), failed.get(name), !isLocalWorld);
            });

            if (!defaulted.isEmpty() && !isLocalWorld)
                return defaulted;

            if (!defaulted.isEmpty())
            {
                String header = "Forge Mod Loader detected missing registry entries.\n\n" +
                   "There are " + defaulted.size() + " missing entries in this save.\n" +
                   "If you continue the missing entries will get removed.\n" +
                   "A world backup will be automatically created in your saves directory.\n\n";

                StringBuilder buf = new StringBuilder();
                defaulted.asMap().forEach((name, entries) ->
                {
                    buf.append("Missing ").append(name).append(":\n");
                    entries.stream().sorted((o1, o2) -> o1.compareNamespaced(o2)).forEach(rl -> buf.append("    ").append(rl).append("\n"));
                    buf.append("\n");
                });

                LOGGER.warn(REGISTRIES, header);
                LOGGER.warn(REGISTRIES, buf.toString());
            }

            if (!defaulted.isEmpty())
            {
                if (isLocalWorld)
                    LOGGER.error(REGISTRIES, "There are unidentified mappings in this world - we are going to attempt to process anyway");
            }

        }

        if (injectFrozenData)
        {
            // If we're loading up the world from disk, we want to add in the new data that might have been provisioned by mods
            // So we load it from the frozen persistent registry
            RegistryManager.ACTIVE.registries.forEach((name, reg) ->
            {
                loadFrozenDataToStagingRegistry(STAGING, name, remaps.get(name));
            });
        }

        // Validate that all the STAGING data is good
        STAGING.registries.forEach((name, reg) -> reg.validateContent(name));

        // Load the STAGING registry into the ACTIVE registry
        //for (Map.Entry<ResourceLocation, IForgeRegistry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        RegistryManager.ACTIVE.registries.forEach((key, value) ->
        {
            loadRegistry(key, STAGING, RegistryManager.ACTIVE, true);
        });

        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.bake();

            // Dump the active registry
            reg.dump(name);
        });

        // Tell mods that the ids have changed
        fireRemapEvent(remaps, false);

        // The id map changed, ensure we apply object holders
        ObjectHolderRegistry.applyObjectHolders();

        // Return an empty list, because we're good
        return ArrayListMultimap.create();
    }

    private static void fireRemapEvent(final Map<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>> remaps, final boolean isFreezing) {
        MinecraftForge.EVENT_BUS.post(new IdMappingEvent(remaps, isFreezing));
    }

    //Has to be split because of generics, Yay!
    private static <T> void loadPersistentDataToStagingRegistry(RegistryManager pool, RegistryManager to, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Map<ResourceLocation, Integer> missing, ResourceLocation name, ForgeRegistry.Snapshot snap)
    {
        ForgeRegistry<T> active  = pool.getRegistry(name);
        if (active == null)
            return; // We've already asked the user if they wish to continue. So if the reg isnt found just assume the user knows and accepted it.
        ForgeRegistry<T> _new = to.getRegistry(name, RegistryManager.ACTIVE);
        snap.aliases.forEach(_new::addAlias);
        snap.blocked.forEach(_new::block);
        _new.loadIds(snap.ids, snap.overrides, missing, remaps, active, name);
    }

    //Another bouncer for generic reasons
    @SuppressWarnings("unchecked")
    private static <T> void processMissing(ResourceLocation name, RegistryManager STAGING, MissingMappingsEvent e, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies)
    {
        List<MissingMappingsEvent.Mapping<T>> mappings = e.getAllMappings(ResourceKey.createRegistryKey(name));
        ForgeRegistry<T> active = RegistryManager.ACTIVE.getRegistry(name);
        ForgeRegistry<T> staging = STAGING.getRegistry(name);
        staging.processMissingEvent(name, active, mappings, missing, remaps, defaulted, failed, injectNetworkDummies);
    }

    private static <T> void loadFrozenDataToStagingRegistry(RegistryManager STAGING, ResourceLocation name, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps)
    {
        ForgeRegistry<T> frozen = RegistryManager.FROZEN.getRegistry(name);
        ForgeRegistry<T> newRegistry = STAGING.getRegistry(name, RegistryManager.FROZEN);
        Map<ResourceLocation, Integer> _new = Maps.newLinkedHashMap();
        frozen.getKeys().stream().filter(key -> !newRegistry.containsKey(key)).forEach(key -> _new.put(key, frozen.getID(key)));
        newRegistry.loadIds(_new, frozen.getOverrideOwners(), Maps.newLinkedHashMap(), remaps, frozen, name);
    }

    /**
     * Check a name for a domain prefix, and if not present infer it from the
     * current active mod container.
     *
     * @param name          The name or resource location
     * @param warnOverrides If true, logs a warning if domain differs from that of
     *                      the currently currently active mod container
     *
     * @return The {@link ResourceLocation} with given or inferred domain
     */
    public static ResourceLocation checkPrefix(String name, boolean warnOverrides)
    {
        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
        name = index == -1 ? name : name.substring(index + 1);
        String prefix = ModLoadingContext.get().getActiveNamespace();
        if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0)
        {
            LogManager.getLogger().debug("Mod `{}` attempting to register `{}` to the namespace `{}`. This could be intended, but likely means an EventBusSubscriber without a modid.", prefix, name, oldPrefix);
            prefix = oldPrefix;
        }
        return new ResourceLocation(prefix, name);
    }
}
