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

package net.minecraftforge.registries;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.ModThreadContext;
import net.minecraftforge.fml.StartupQuery;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * INTERNAL ONLY
 * MODDERS SHOULD HAVE NO REASON TO USE THIS CLASS
 */
public class GameData
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker GD = MarkerManager.getMarker("GAMEDATA");

    public static final ResourceLocation BLOCKS       = new ResourceLocation("minecraft:blocks");
    public static final ResourceLocation ITEMS        = new ResourceLocation("minecraft:items");
    public static final ResourceLocation POTIONS      = new ResourceLocation("minecraft:potions");
    public static final ResourceLocation BIOMES       = new ResourceLocation("minecraft:biomes");
    public static final ResourceLocation SOUNDEVENTS  = new ResourceLocation("minecraft:soundevents");
    public static final ResourceLocation POTIONTYPES  = new ResourceLocation("minecraft:potiontypes");
    public static final ResourceLocation ENCHANTMENTS = new ResourceLocation("minecraft:enchantments");
    public static final ResourceLocation ENTITIES     = new ResourceLocation("minecraft:entities");
    public static final ResourceLocation RECIPES      = new ResourceLocation("minecraft:recipes");
    public static final ResourceLocation PROFESSIONS  = new ResourceLocation("minecraft:villagerprofessions");
    private static final int MAX_BLOCK_ID = 4095;
    private static final int MIN_ITEM_ID = MAX_BLOCK_ID + 1;
    private static final int MAX_ITEM_ID = 31999;
    private static final int MAX_POTION_ID = 255; // SPacketEntityEffect sends bytes, we can only use 255
    private static final int MAX_BIOME_ID = 255; // Maximum number in a byte in the chunk
    private static final int MAX_SOUND_ID = Integer.MAX_VALUE >> 5; // Varint (SPacketSoundEffect)
    private static final int MAX_POTIONTYPE_ID = Integer.MAX_VALUE >> 5; // Int (SPacketEffect)
    private static final int MAX_ENCHANTMENT_ID = Short.MAX_VALUE - 1; // Short - serialized as a short in ItemStack NBTs.
    private static final int MAX_ENTITY_ID = Integer.MAX_VALUE >> 5; // Varint (SPacketSpawnMob)
    private static final int MAX_RECIPE_ID = Integer.MAX_VALUE >> 5; // Varint CPacketRecipeInfo/SPacketRecipeBook
    private static final int MAX_PROFESSION_ID = 1024; //TODO: Is this serialized anywhere anymore?

    private static final ResourceLocation BLOCK_TO_ITEM    = new ResourceLocation("minecraft:blocktoitemmap");
    private static final ResourceLocation BLOCKSTATE_TO_ID = new ResourceLocation("minecraft:blockstatetoid");
    private static boolean hasInit = false;
    private static final boolean DISABLE_VANILLA_REGISTRIES = Boolean.parseBoolean(System.getProperty("forge.disableVanillaGameData", "false")); // Use for unit tests/debugging
    private static final BiConsumer<ResourceLocation, ForgeRegistry<?>> LOCK_VANILLA = (name, reg) -> reg.slaves.values().stream().filter(o -> o instanceof ILockableRegistry).forEach(o -> ((ILockableRegistry)o).lock());

    static {
        init();
    }

    public static void init()
    {
        if ( DISABLE_VANILLA_REGISTRIES)
        {
            LOGGER.warn(GD, "DISABLING VANILLA REGISTRY CREATION AS PER SYSTEM VARIABLE SETTING! forge.disableVanillaGameData");
            return;
        }
        if (hasInit)
            return;
        hasInit = true;
        makeRegistry(BLOCKS,       Block.class,       MAX_BLOCK_ID, new ResourceLocation("air")).addCallback(BlockCallbacks.INSTANCE).create();
        makeRegistry(ITEMS,        Item.class,        MIN_ITEM_ID, MAX_ITEM_ID).addCallback(ItemCallbacks.INSTANCE).create();
        makeRegistry(POTIONS,      Potion.class,      MAX_POTION_ID).create();
        makeRegistry(BIOMES,       Biome.class,       MAX_BIOME_ID).create();
        makeRegistry(SOUNDEVENTS,  SoundEvent.class,  MAX_SOUND_ID).create();
        makeRegistry(POTIONTYPES,  PotionType.class,  MAX_POTIONTYPE_ID, new ResourceLocation("empty")).create();
        makeRegistry(ENCHANTMENTS, Enchantment.class, MAX_ENCHANTMENT_ID).create();
//        makeRegistry(RECIPES,      IRecipe.class,     MAX_RECIPE_ID).disableSaving().allowModification().addCallback(RecipeCallbacks.INSTANCE).create();
//        makeRegistry(PROFESSIONS,  VillagerProfession.class, MAX_PROFESSION_ID).create();
//        entityRegistry = (ForgeRegistry<EntityEntry>)makeRegistry(ENTITIES, EntityEntry.class, MAX_ENTITY_ID).addCallback(EntityCallbacks.INSTANCE).create();
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int min, int max)
    {
        return new RegistryBuilder<T>().setName(name).setType(type).setIDRange(min, max).addCallback(new NamespacedWrapper.Factory<T>());
    }
    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int max)
    {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(max).addCallback(new NamespacedWrapper.Factory<T>());
    }
    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int max, ResourceLocation _default)
    {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(max).addCallback(new NamespacedDefaultedWrapper.Factory<T>()).setDefaultKey(_default);
    }

    public static <V extends IForgeRegistryEntry<V>> RegistryNamespacedDefaultedByKey<ResourceLocation, V> getWrapperDefaulted(Class<V> cls)
    {
        IForgeRegistry<V> reg = RegistryManager.ACTIVE.getRegistry(cls);
        Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + cls.toString());
        @SuppressWarnings("unchecked")
        RegistryNamespacedDefaultedByKey<ResourceLocation, V> ret = reg.getSlaveMap(NamespacedDefaultedWrapper.Factory.ID, NamespacedDefaultedWrapper.class);
        Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + cls.toString());
        return ret;
    }

    public static <V extends IForgeRegistryEntry<V>> RegistryNamespaced<ResourceLocation, V> getWrapper(Class<V> cls)
    {
        IForgeRegistry<V> reg = RegistryManager.ACTIVE.getRegistry(cls);
        Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + cls.toString());
        @SuppressWarnings("unchecked")
        RegistryNamespaced<ResourceLocation, V> ret = reg.getSlaveMap(NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
        Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + cls.toString());
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static BiMap<Block,Item> getBlockItemMap()
    {
        return RegistryManager.ACTIVE.getRegistry(Item.class).getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
    }

    @SuppressWarnings("unchecked")
    public static ObjectIntIdentityMap<IBlockState> getBlockStateIDMap()
    {
        return RegistryManager.ACTIVE.getRegistry(Block.class).getSlaveMap(BLOCKSTATE_TO_ID, ObjectIntIdentityMap.class);
    }

    public static <K extends IForgeRegistryEntry<K>> K register_impl(K value)
    {
        Validate.notNull(value, "Attempted to register a null object");
        Validate.notNull(value.getRegistryName(), String.format("Attempt to register object without having set a registry name %s (type %s)", value, value.getClass().getName()));
        final IForgeRegistry<K> registry = RegistryManager.ACTIVE.getRegistry(value.getRegistryType());
        Validate.notNull(registry, "Attempted to registry object without creating registry first: " + value.getRegistryType().getName());
        registry.register(value);
        return value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void vanillaSnapshot()
    {
        LOGGER.debug("Creating vanilla freeze snapshot");
        for (Map.Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(r.getKey());
            loadRegistry(r.getKey(), RegistryManager.ACTIVE, RegistryManager.VANILLA, clazz, true);
        }
        RegistryManager.VANILLA.registries.forEach((name, reg) ->
        {
            reg.validateContent(name);
            reg.freeze();
        });
        RegistryManager.VANILLA.registries.forEach(LOCK_VANILLA);
        RegistryManager.ACTIVE.registries.forEach(LOCK_VANILLA);
        LOGGER.debug("Vanilla freeze snapshot created");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void freezeData()
    {
        LOGGER.debug(GD, "Freezing registries");
        for (Map.Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(r.getKey());
            loadRegistry(r.getKey(), RegistryManager.ACTIVE, RegistryManager.FROZEN, clazz, true);
        }
        RegistryManager.FROZEN.registries.forEach((name, reg) ->
        {
            reg.validateContent(name);
            reg.freeze();
        });
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.freeze());

        // the id mapping is finalized, no ids actually changed but this is a good place to tell everyone to 'bake' their stuff.
        //Loader.instance().fireRemapEvent(ImmutableMap.of(), true);

        LOGGER.debug(GD, "All registries frozen");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void revertToFrozen()
    {
        if (RegistryManager.FROZEN.registries.isEmpty())
        {
            LOGGER.warn(GD, "Can't revert to frozen GameData state without freezing first.");
            return;
        }
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());

        LOGGER.debug(GD, "Reverting to frozen data state.");
        for (Map.Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(r.getKey());
            loadRegistry(r.getKey(), RegistryManager.FROZEN, RegistryManager.ACTIVE, clazz, true);
        }
        // the id mapping has reverted, fire remap events for those that care about id changes
        //Loader.instance().fireRemapEvent(ImmutableMap.of(), true);

        // the id mapping has reverted, ensure we sync up the object holders
        LOGGER.debug("Frozen state restored.");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void revert(RegistryManager state, ResourceLocation registry, boolean lock)
    {
        LOGGER.debug(GD, "Reverting {} to {}", registry, state.getName());
        final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(registry);
        loadRegistry(registry, state, RegistryManager.ACTIVE, clazz, lock);
        LOGGER.debug(GD, "Reverting complete");
    }

    //Lets us clear the map so we can rebuild it.
    static class ClearableObjectIntIdentityMap<I> extends ObjectIntIdentityMap<I>
    {
        void clear()
        {
            this.identityMap.clear();
            this.objectList.clear();
        }
    }


    private static class BlockCallbacks implements IForgeRegistry.AddCallback<Block>, IForgeRegistry.ClearCallback<Block>, IForgeRegistry.CreateCallback<Block>, IForgeRegistry.DummyFactory<Block>
    {
        static final BlockCallbacks INSTANCE = new BlockCallbacks();

        @SuppressWarnings("deprecation")
        @Override
        public void onAdd(IForgeRegistryInternal<Block> owner, RegistryManager stage, int id, Block block, @Nullable Block oldBlock)
        {
            @SuppressWarnings("unchecked")
            ClearableObjectIntIdentityMap<IBlockState> blockstateMap = owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class);
/*

            if ("minecraft:tripwire".equals(block.getRegistryName().toString())) //Tripwire is crap so we have to special case whee!
            {
                for (int meta = 0; meta < 15; meta++)
                    blockstateMap.put(block.getStateFromMeta(meta), id << 4 | meta);
            }

            //So, due to blocks having more in-world states then metadata allows, we have to turn the map into a semi-milti-bimap.
            //We can do this however because the implementation of the map is last set wins. So we can add all states, then fix the meta bimap.
            //Multiple states -> meta. But meta to CORRECT state.

            final boolean[] usedMeta = new boolean[16]; //Hold a list of known meta from all states.
            for (IBlockState state : block.getBlockState().getValidStates())
            {
                final int meta = block.getMetaFromState(state);
                blockstateMap.put(state, id << 4 | meta); //Add ALL the things!
                usedMeta[meta] = true;
            }

            for (int meta = 0; meta < 16; meta++)
            {
                if (block.getClass() == BlockObserver.class)
                    continue; //Observers are bad and have non-cyclical states. So we HAVE to use the vanilla logic above.
                if (usedMeta[meta])
                    blockstateMap.put(block.getStateFromMeta(meta), id << 4 | meta); // Put the CORRECT thing!
            }

            if (oldBlock != null)
            {
                @SuppressWarnings("unchecked")
                BiMap<Block, Item> blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
                Item item = blockToItem.get(oldBlock);
                if (item != null)
                    blockToItem.forcePut(block, item);
            }
*/
        }

        @Override
        public void onClear(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
//            owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
/*
            final ClearableObjectIntIdentityMap<IBlockState> idMap = new ClearableObjectIntIdentityMap<IBlockState>()
            {
                @SuppressWarnings("deprecation")
                @Override
                public int get(IBlockState key)
                {
                    Integer integer = (Integer)this.identityMap.get(key);
                    // There are some cases where this map is queried to serialize a state that is valid,
                    //but somehow not in this list, so attempt to get real metadata. Doing this hear saves us 7 patches
                    if (integer == null && key != null)
                        integer = this.identityMap.get(key.getBlock().getStateFromMeta(key.getBlock().getMetaFromState(key)));
                    return integer == null ? -1 : integer.intValue();
                }
            };
            owner.setSlaveMap(BLOCKSTATE_TO_ID, idMap);
*/
            owner.setSlaveMap(BLOCK_TO_ITEM, HashBiMap.create());
        }

        @Override
        public Block createDummy(ResourceLocation key)
        {
            Block ret = new BlockDummyAir(Block.Builder.func_200945_a(Material.AIR));
            GameData.forceRegistryName(ret, key);
            return ret;
        }
        private static class BlockDummyAir extends BlockAir //A named class so DummyBlockReplacementTest can detect if its a dummy
        {
            private BlockDummyAir(Block.Builder builder)
            {
                super(builder);
            }

            @Override
            public String getTranslationKey()
            {
                return "block.minecraft.air";
            }
        }
    }

    private static class ItemCallbacks implements IForgeRegistry.AddCallback<Item>, IForgeRegistry.ClearCallback<Item>, IForgeRegistry.CreateCallback<Item>
    {
        static final ItemCallbacks INSTANCE = new ItemCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<Item> owner, RegistryManager stage, int id, Item item, @Nullable Item oldItem)
        {
            if (item instanceof ItemBlock)
            {
                @SuppressWarnings("unchecked")
                BiMap<Block, Item> blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
                blockToItem.forcePut(((ItemBlock)item).getBlock(), item);
            }
        }

        @Override
        public void onClear(IForgeRegistryInternal<Item> owner, RegistryManager stage)
        {
            owner.getSlaveMap(BLOCK_TO_ITEM, BiMap.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Item> owner, RegistryManager stage)
        {
            // We share the blockItem map between items and blocks registries
            BiMap<?, ?> map = stage.getRegistry(BLOCKS).getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
            owner.setSlaveMap(BLOCK_TO_ITEM, map);
        }
    }

/*
    private static class RecipeCallbacks implements IForgeRegistry.ValidateCallback<IRecipe>, IForgeRegistry.MissingFactory<IRecipe>
    {
        static final RecipeCallbacks INSTANCE = new RecipeCallbacks();

        @Override
        public void onValidate(IForgeRegistryInternal<IRecipe> owner, RegistryManager stage, int id, ResourceLocation key, IRecipe obj)
        {
            if (stage != RegistryManager.ACTIVE) return;
            // verify the recipe output yields a registered item
            Item item = obj.getRecipeOutput().getItem();
            if (!stage.getRegistry(Item.class).containsValue(item))
            {
                throw new IllegalStateException(String.format("Recipe %s (%s) produces unregistered item %s (%s)", key, obj, item.getRegistryName(), item));
            }
        }

        @Override
        public IRecipe createMissing(ResourceLocation key, boolean isNetwork)
        {
            return isNetwork ? new DummyRecipe().setRegistryName(key) : null;
        }
        private static class DummyRecipe implements IRecipe
        {
            private static ItemStack result = new ItemStack(Items.DIAMOND, 64);
            private ResourceLocation name;

            @Override
            public IRecipe setRegistryName(ResourceLocation name) {
                this.name = name;
                return this;
            }
            @Override public ResourceLocation getRegistryName() { return name; }
            @Override public Class<IRecipe> getRegistryType() { return IRecipe.class; }
            @Override public boolean matches(InventoryCrafting inv, World worldIn) { return false; } //dirt?
            @Override public ItemStack getCraftingResult(InventoryCrafting inv) { return result; }
            @Override public boolean canFit(int width, int height) { return false; }
            @Override public ItemStack getRecipeOutput() { return result; }
            @Override public boolean isDynamic() { return true; }
        }
    }


    private static ForgeRegistry<EntityEntry> entityRegistry;
    public static ForgeRegistry<EntityEntry> getEntityRegistry() { return entityRegistry; }
    public static void registerEntity(int id, ResourceLocation key, Class<? extends Entity> clazz, String oldName)
    {
        RegistryNamespaced<ResourceLocation, EntityEntry> reg = getWrapper(EntityEntry.class);
        reg.register(id, key, new EntityEntry(clazz, oldName));
    }

    private static class EntityCallbacks implements IForgeRegistry.AddCallback<EntityEntry>
    {
        static final EntityCallbacks INSTANCE = new EntityCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<EntityEntry> owner, RegistryManager stage, int id, EntityEntry entry, @Nullable EntityEntry oldEntry)
        {
            if (entry instanceof EntityEntryBuilder.BuiltEntityEntry)
            {
                ((EntityEntryBuilder.BuiltEntityEntry) entry).addedToRegistry();
            }
            if (entry.getEgg() != null)
                EntityList.ENTITY_EGGS.put(entry.getRegistryName(), entry.getEgg());
        }
    }
*/

    private static <T extends IForgeRegistryEntry<T>> void loadRegistry(final ResourceLocation registryName, final RegistryManager from, final RegistryManager to, final Class<T> regType, boolean freeze)
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
        LOGGER.info(GD, "Injecting existing registry data into this {} instance", EffectiveSide.get());
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.validateContent(name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.dump(name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());

        List<ResourceLocation> missingRegs = snapshot.keySet().stream().filter(name -> !RegistryManager.ACTIVE.registries.containsKey(name)).collect(Collectors.toList());
        if (missingRegs.size() > 0)
        {
            String text = "Forge Mod Loader detected missing/unknown registrie(s).\n\n" +
                    "There are " + missingRegs.size() + " missing registries in this save.\n" +
                    "If you continue the missing registries will get removed.\n" +
                    "This may cause issues, it is advised that you create a world backup before continuing.\n\n" +
                    "Missing Registries:\n";

            for (ResourceLocation s : missingRegs)
                text += s.toString() + "\n";

            if (!StartupQuery.confirm(text))
                StartupQuery.abort();
        }

        RegistryManager STAGING = new RegistryManager("STAGING");

        final Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps = Maps.newHashMap();
        final LinkedHashMap<ResourceLocation, Map<ResourceLocation, Integer>> missing = Maps.newLinkedHashMap();
        // Load the snapshot into the "STAGING" registry
        snapshot.forEach((key, value) ->
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(key);
            remaps.put(key, Maps.newLinkedHashMap());
            missing.put(key, Maps.newHashMap());
            loadPersistentDataToStagingRegistry(RegistryManager.ACTIVE, STAGING, remaps.get(key), missing.get(key), key, value, clazz);
        });

        snapshot.forEach((key, value) ->
        {
            value.dummied.forEach(dummy ->
            {
                Map<ResourceLocation, Integer> m = missing.get(key);
                ForgeRegistry<?> reg = STAGING.getRegistry(key);

                // Currently missing locally, we just inject and carry on
                if (m.containsKey(dummy))
                {
                    if (reg.markDummy(dummy, m.get(dummy)))
                        m.remove(dummy);
                }
                else if (isLocalWorld)
                {
                   LOGGER.debug("Registry {}: Resuscitating dummy entry {}", key, dummy);
                }
                else
                {
                    // The server believes this is a dummy block identity, but we seem to have one locally. This is likely a conflict
                    // in mod setup - Mark this entry as a dummy
                    int id = reg.getID(dummy);
                    LOGGER.warn(GD, "Registry {}: The ID {} @ {} is currently locally mapped - it will be replaced with a dummy for this session", dummy, key, id);
                    reg.markDummy(dummy, id);
                }
            });
        });

        int count = missing.values().stream().mapToInt(Map::size).sum();
        if (count > 0)
        {
            LOGGER.debug(GD,"There are {} mappings missing - attempting a mod remap", count);
            Multimap<ResourceLocation, ResourceLocation> defaulted = ArrayListMultimap.create();
            Multimap<ResourceLocation, ResourceLocation> failed = ArrayListMultimap.create();

            missing.entrySet().stream().filter(e -> e.getValue().size() > 0).forEach(m ->
            {
                ResourceLocation name = m.getKey();
                ForgeRegistry<?> reg = STAGING.getRegistry(name);
                RegistryEvent.MissingMappings<?> event = reg.getMissingEvent(name, m.getValue());
                MinecraftForge.EVENT_BUS.post(event);

                List<MissingMappings.Mapping<?>> lst = event.getAllMappings().stream().filter(e -> e.getAction() == MissingMappings.Action.DEFAULT).sorted((a, b) -> a.toString().compareTo(b.toString())).collect(Collectors.toList());
                if (!lst.isEmpty())
                {
                    LOGGER.error(GD,"Unidentified mapping from registry {}", name);
                    lst.forEach(map -> LOGGER.error("    {}: {}", map.key, map.id));
                }
                event.getAllMappings().stream().filter(e -> e.getAction() == MissingMappings.Action.FAIL).forEach(fail -> failed.put(name, fail.key));

                final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(name);
                processMissing(clazz, name, STAGING, event, m.getValue(), remaps.get(name), defaulted.get(name), failed.get(name), !isLocalWorld);
            });

            if (!defaulted.isEmpty() && !isLocalWorld)
                return defaulted;

            if (!defaulted.isEmpty())
            {
                StringBuilder buf = new StringBuilder();
                buf.append("Forge Mod Loader detected missing registry entries.\n\n")
                   .append("There are ").append(defaulted.size()).append(" missing entries in this save.\n")
                   .append("If you continue the missing entries will get removed.\n")
                   .append("A world backup will be automatically created in your saves directory.\n\n");

                defaulted.asMap().forEach((name, entries) ->
                {
                    buf.append("Missing ").append(name).append(":\n");
                    entries.forEach(rl -> buf.append("    ").append(rl).append("\n"));
                });

                boolean confirmed = StartupQuery.confirm(buf.toString());
                if (!confirmed)
                    StartupQuery.abort();

/*
                try
                {
                    String skip = System.getProperty("fml.doNotBackup");
                    if (skip == null || !"true".equals(skip))
                    {
                        ZipperUtil.backupWorld();
                    }
                    else
                    {
                        for (int x = 0; x < 10; x++)
                            LOGGER.error(GD, "!!!!!!!!!! UPDATING WORLD WITHOUT DOING BACKUP !!!!!!!!!!!!!!!!");
                    }
                }
                catch (IOException e)
                {
                    StartupQuery.notify("The world backup couldn't be created.\n\n" + e);
                    StartupQuery.abort();
                }
*/
            }

            if (!defaulted.isEmpty())
            {
                if (isLocalWorld)
                    LOGGER.error(GD, "There are unidentified mappings in this world - we are going to attempt to process anyway");
            }

        }

        if (injectFrozenData)
        {
            // If we're loading from disk, we can actually substitute air in the block map for anything that is otherwise "missing". This keeps the reference in the map, in case
            // the block comes back later
            missing.forEach((name, m) ->
            {
                ForgeRegistry<?> reg = STAGING.getRegistry(name);
                m.forEach((rl, id) -> reg.markDummy(rl, id));
            });


            // If we're loading up the world from disk, we want to add in the new data that might have been provisioned by mods
            // So we load it from the frozen persistent registry
            RegistryManager.ACTIVE.registries.forEach((name, reg) ->
            {
                final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(name);
                loadFrozenDataToStagingRegistry(STAGING, name, remaps.get(name), clazz);
            });
        }

        // Validate that all the STAGING data is good
        STAGING.registries.forEach((name, reg) -> reg.validateContent(name));

        // Load the STAGING registry into the ACTIVE registry
        //for (Map.Entry<ResourceLocation, IForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        RegistryManager.ACTIVE.registries.forEach((key, value) ->
        {
            final Class<? extends IForgeRegistryEntry> registrySuperType = RegistryManager.ACTIVE.getSuperType(key);
            loadRegistry(key, STAGING, RegistryManager.ACTIVE, registrySuperType, true);
        });

        // Dump the active registry
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.dump(name));

        // Tell mods that the ids have changed
        //Loader.instance().fireRemapEvent(remaps, false);

        // The id map changed, ensure we apply object holders
        //ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        // Return an empty list, because we're good
        return ArrayListMultimap.create();
    }

    //Has to be split because of generics, Yay!
    private static <T extends IForgeRegistryEntry<T>> void loadPersistentDataToStagingRegistry(RegistryManager pool, RegistryManager to, Map<ResourceLocation, Integer[]> remaps, Map<ResourceLocation, Integer> missing, ResourceLocation name, ForgeRegistry.Snapshot snap, Class<T> regType)
    {
        ForgeRegistry<T> active  = pool.getRegistry(name);
        if (active == null)
            return; // We've already asked the user if they wish to continue. So if the reg isnt found just assume the user knows and accepted it.
        ForgeRegistry<T> _new = to.getRegistry(name, RegistryManager.ACTIVE);
        snap.aliases.forEach(_new::addAlias);
        snap.blocked.forEach(_new::block);
        // Load current dummies BEFORE the snapshot is loaded so that add() will remove from the list.
        snap.dummied.forEach(_new::addDummy);
        _new.loadIds(snap.ids, snap.overrides, missing, remaps, active, name);
    }

    //Another bouncer for generic reasons
    @SuppressWarnings("unchecked")
    private static <T extends IForgeRegistryEntry<T>> void processMissing(Class<T> clazz, ResourceLocation name, RegistryManager STAGING, MissingMappings<?> e, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, Integer[]> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies)
    {
        List<MissingMappings.Mapping<T>> mappings = ((MissingMappings<T>)e).getAllMappings();
        ForgeRegistry<T> active = RegistryManager.ACTIVE.getRegistry(name);
        ForgeRegistry<T> staging = STAGING.getRegistry(name);
        staging.processMissingEvent(name, active, mappings, missing, remaps, defaulted, failed, injectNetworkDummies);
    }

    private static <T extends IForgeRegistryEntry<T>> void loadFrozenDataToStagingRegistry(RegistryManager STAGING, ResourceLocation name, Map<ResourceLocation, Integer[]> remaps, Class<T> clazz)
    {
        ForgeRegistry<T> frozen = RegistryManager.FROZEN.getRegistry(name);
        ForgeRegistry<T> newRegistry = STAGING.getRegistry(name, RegistryManager.FROZEN);
        Map<ResourceLocation, Integer> _new = Maps.newHashMap();
        frozen.getKeys().stream().filter(key -> !newRegistry.containsKey(key)).forEach(key -> _new.put(key, frozen.getID(key)));
        newRegistry.loadIds(_new, frozen.getOverrideOwners(), Maps.newLinkedHashMap(), remaps, frozen, name);
    }

    public static void fireCreateRegistryEvents()
    {
        MinecraftForge.EVENT_BUS.post(new RegistryEvent.NewRegistry());
    }

    public static void fireRegistryEvents()
    {
        fireRegistryEvents(rl -> true);
    }
    public static void fireRegistryEvents(Predicate<ResourceLocation> filter)
    {
        List<ResourceLocation> keys = Lists.newArrayList(RegistryManager.ACTIVE.registries.keySet());
        Collections.sort(keys, (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
        /*
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            if (filter.test(name))
                ((ForgeRegistry<?>)reg).unfreeze();
        });
        */

        if (filter.test(BLOCKS))
        {
            MinecraftForge.EVENT_BUS.post(RegistryManager.ACTIVE.getRegistry(BLOCKS).getRegisterEvent(BLOCKS));
        }
        if (filter.test(ITEMS))
        {
            MinecraftForge.EVENT_BUS.post(RegistryManager.ACTIVE.getRegistry(ITEMS).getRegisterEvent(ITEMS));
        }
        for (ResourceLocation rl : keys)
        {
            if (!filter.test(rl)) continue;
            if (rl == BLOCKS || rl == ITEMS) continue;
            MinecraftForge.EVENT_BUS.post(RegistryManager.ACTIVE.getRegistry(rl).getRegisterEvent(rl));
        }


        /*
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            if (filter.test(name))
                ((ForgeRegistry<?>)reg).freeze();
        });
        */
    }

    public static ResourceLocation checkPrefix(String name)
    {
        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
        name = index == -1 ? name : name.substring(index + 1);
        String prefix = ModThreadContext.get().getActiveContainer().getPrefix();
        if (!oldPrefix.equals(prefix) && oldPrefix.length() > 0)
        {
            LogManager.getLogger().info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
            prefix = oldPrefix;
        }
        return new ResourceLocation(prefix, name);
    }

    private static Field regName;
    private static void forceRegistryName(IForgeRegistryEntry<?> entry, ResourceLocation name)
    {
        if (regName == null)
        {
            try
            {
                regName = ForgeRegistryEntry.class.getDeclaredField("registryName");
                regName.setAccessible(true);
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                LOGGER.error(GD, "Could not get `registryName` field from IForgeRegistryEntry.Impl", e);
                throw new RuntimeException(e);
            }
        }
        try
        {
            regName.set(entry, name);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            LOGGER.error(GD,"Could not set `registryName` field in IForgeRegistryEntry.Impl to `{}`", name.toString(), e);
            throw new RuntimeException(e);
        }

    }
}
