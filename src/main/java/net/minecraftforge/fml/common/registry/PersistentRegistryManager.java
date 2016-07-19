/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.ZipperUtil;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;

import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Persistent registry manager. Manages the registries loading from disk, and from network. Handles staging
 * registry data before loading uniformly into the active registry, and keeps a frozen registry instance
 * for reversion after connection.
 *
 * @author cpw
 */
@SuppressWarnings("WeakerAccess")
public class PersistentRegistryManager
{
    enum PersistentRegistry
    {
        ACTIVE, VANILLA, FROZEN, STAGING;

        private final BiMap<ResourceLocation, FMLControlledNamespacedRegistry<?>> registries = HashBiMap.create();
        private final BiMap<Class<? extends IForgeRegistryEntry<?>>, ResourceLocation> registrySuperTypes = HashBiMap.create();

        @SuppressWarnings("unchecked")
        private <T extends IForgeRegistryEntry<T>> Class<T> getRegistrySuperType(ResourceLocation key)
        {
            return (Class<T>)registrySuperTypes.inverse().get(key);
        }

        @SuppressWarnings("unchecked")
        private <T extends IForgeRegistryEntry<T>> FMLControlledNamespacedRegistry<T> getRegistry(ResourceLocation key, @SuppressWarnings("UnusedParameters") Class<T> regType)
        {
            return (FMLControlledNamespacedRegistry<T>)registries.get(key);
        }

        private <T extends IForgeRegistryEntry<T>> FMLControlledNamespacedRegistry<T> getOrShallowCopyRegistry(ResourceLocation key, Class<T> regType, FMLControlledNamespacedRegistry<T> other)
        {
            if (!registries.containsKey(key))
            {
                registries.put(key, other.makeShallowCopy());
                registrySuperTypes.put(regType, key);
            }
            return getRegistry(key, regType);
        }

        private <T extends IForgeRegistryEntry<T>> FMLControlledNamespacedRegistry<T> createRegistry(ResourceLocation registryName, Class<T> type, ResourceLocation defaultObjectKey, int minId, int maxId, IForgeRegistry.AddCallback<T> addCallback, IForgeRegistry.ClearCallback<T> clearCallback, IForgeRegistry.CreateCallback<T> createCallback)
        {
            Set<Class<?>> parents = Sets.newHashSet();
            findSuperTypes(type, parents);
            SetView<Class<?>> overlappedTypes = Sets.intersection(parents, registrySuperTypes.keySet());
            if (!overlappedTypes.isEmpty())
            {
                Class<?> foundType = overlappedTypes.iterator().next();
                FMLLog.severe("Found existing registry of type %1s named %2s, you cannot create a new registry (%3s) with type %4s, as %4s has a parent of that type", foundType, registrySuperTypes.get(foundType), registryName, type);
                throw new IllegalArgumentException("Duplicate registry parent type found - you can only have one registry for a particular super type");
            }
            FMLControlledNamespacedRegistry<T> fmlControlledNamespacedRegistry = new FMLControlledNamespacedRegistry<T>(defaultObjectKey, minId, maxId, type, addCallback, clearCallback, createCallback);
            registries.put(registryName, fmlControlledNamespacedRegistry);
            registrySuperTypes.put(type, registryName);
            return getRegistry(registryName, type);
        }

        private void findSuperTypes(Class<?> type, Set<Class<?>> types)
        {
            if (type == null || type == Object.class)
            {
                return;
            }
            types.add(type);
            for (Class<?> interfac : type.getInterfaces())
            {
                findSuperTypes(interfac, types);
            }
            findSuperTypes(type.getSuperclass(), types);
        }


        void clean()
        {
            registries.clear();
            registrySuperTypes.clear();
        }

        boolean isPopulated()
        {
            return !registries.isEmpty();
        }

        boolean containsRegistry(FMLControlledNamespacedRegistry<?> registry)
        {
            return registries.containsValue(registry);
        }

        public <T extends IForgeRegistryEntry<T>> FMLControlledNamespacedRegistry<T> getRegistry(Class<T> rootClass)
        {
            ResourceLocation rl = registrySuperTypes.get(rootClass);
            return getRegistry(rl, rootClass);
        }
    }

    public static final ResourceLocation BLOCKS = new ResourceLocation("minecraft:blocks");
    public static final ResourceLocation ITEMS = new ResourceLocation("minecraft:items");
    public static final ResourceLocation POTIONS = new ResourceLocation("minecraft:potions");
    public static final ResourceLocation BIOMES = new ResourceLocation("minecraft:biomes");
    public static final ResourceLocation SOUNDEVENTS = new ResourceLocation("minecraft:soundevents");
    public static final ResourceLocation POTIONTYPES = new ResourceLocation("minecraft:potiontypes");
    public static final ResourceLocation ENCHANTMENTS = new ResourceLocation("minecraft:enchantments");

    public static <T extends IForgeRegistryEntry<T>> FMLControlledNamespacedRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, ResourceLocation optionalDefaultKey, int minId, int maxId, boolean hasDelegates, IForgeRegistry.AddCallback<T> addCallback, IForgeRegistry.ClearCallback<T> clearCallback, IForgeRegistry.CreateCallback<T> createCallback)
    {
        return PersistentRegistry.ACTIVE.createRegistry(registryName, registryType, optionalDefaultKey, minId, maxId, addCallback, clearCallback, createCallback);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> findRegistry(IForgeRegistryEntry<?> entry)
    {
        final Class<V> registryType = (Class<V>)(Class)entry.getRegistryType();
        final FMLControlledNamespacedRegistry<V> registry = PersistentRegistry.ACTIVE.getRegistry(registryType);
        if (registry == null)
        {
            FMLLog.getLogger().log(Level.ERROR, "Unable to locate registry for registered object of type {}", entry.getClass().getName());
            throw new IllegalArgumentException("Unable to locate registry for registered object");
        }
        return registry;
    }

    static <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> findRegistryByType(Class<V> registryType)
    {
        return PersistentRegistry.ACTIVE.getRegistry(registryType);
    }


    public static List<String> injectSnapshot(GameDataSnapshot snapshot, boolean injectFrozenData, boolean isLocalWorld)
    {
        FMLLog.info("Injecting existing block and item data into this %s instance", FMLCommonHandler.instance().getEffectiveSide().isServer() ? "server" : "client");
        final Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps = Maps.newHashMap();
        final LinkedHashMap<ResourceLocation, Map<ResourceLocation, Integer>> missing = Maps.newLinkedHashMap();

        forAllRegistries(PersistentRegistry.ACTIVE, ValidateRegistryFunction.OPERATION);
        forAllRegistries(PersistentRegistry.ACTIVE, DumpRegistryFunction.OPERATION);
        forAllRegistries(PersistentRegistry.ACTIVE, ResetDelegatesFunction.OPERATION);

        List<ResourceLocation> missingRegs = Lists.newArrayList();
        for (ResourceLocation name : snapshot.entries.keySet())
        {
            name = getFixedName(name);
            if (PersistentRegistry.ACTIVE.getRegistry(name, null) == null)
            {
                missingRegs.add(name);
            }
        }

        if (missingRegs.size() > 0)
        {
            String text = "Forge Mod Loader detected missing/unknown registrie(s).\n\n" +
                    "There are " + missingRegs.size() + " missing registries in this save.\n" +
                    "If you continue the missing registries will get removed.\n" +
                    "This may cause issues, it is advised that you create a world backup before continuing.\n\n" +
                    "Missing Registries:\n";

            for (ResourceLocation s : missingRegs)
                text += s.toString() + "\n";

            boolean confirmed = StartupQuery.confirm(text);
            if (!confirmed)
            {
                StartupQuery.abort();
                ;
            }
        }

        // Load the snapshot into the "STAGING" registry
        for (Map.Entry<ResourceLocation, GameDataSnapshot.Entry> snapshotEntry : snapshot.entries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> registrySuperType = PersistentRegistry.ACTIVE.getRegistrySuperType(snapshotEntry.getKey());
            loadPersistentDataToStagingRegistry(injectFrozenData, remaps, missing, snapshotEntry, registrySuperType);
        }

        // Handle dummied blocks
        for (ResourceLocation dummy : snapshot.entries.get(BLOCKS).dummied)
        {
            // Currently missing locally, we just inject and carry on
            if (missing.get(BLOCKS).containsKey(dummy))
            {
                Integer id = missing.get(BLOCKS).remove(dummy);
                // Mark this entry as a dummy
                PersistentRegistry.STAGING.getRegistry(BLOCKS, Block.class).markDummy(dummy, id, new BlockDummyAir());
            }
            else if (isLocalWorld)
            {
                // Carry on, we resuscitated the block
                if (FMLControlledNamespacedRegistry.DEBUG)
                {
                    FMLLog.log(Level.DEBUG, "Registry: Resuscitating dummy block %s", dummy);
                }
            }
            else
            {
                Integer id = PersistentRegistry.STAGING.getRegistry(BLOCKS, Block.class).getId(dummy);
                // The server believes this is a dummy block identity, but we seem to have one locally. This is likely a conflict
                // in mod setup - Mark this entry as a dummy
                FMLLog.log(Level.WARN, "The ID %d is currently locally mapped - it will be replaced with air for this session", id);
                PersistentRegistry.STAGING.getRegistry(BLOCKS, Block.class).markDummy(dummy, id, new BlockDummyAir());
            }
        }

        // If we have missed data, fire the missing mapping event
        List<String> missedMappings = Loader.instance().fireMissingMappingEvent(missing.get(BLOCKS), missing.get(ITEMS), isLocalWorld, remaps.get(BLOCKS), remaps.get(ITEMS));
        // If there's still missed mappings, we return, because that's an error
        if (!missedMappings.isEmpty())
        {
            return missedMappings;
        }

        // If we're loading from disk, we can actually substitute air in the block map for anything that is otherwise "missing". This keeps the reference in the map, in case
        // the block comes back later
        if (injectFrozenData)
        {
            for (Map.Entry<ResourceLocation, Integer> missingBlock: missing.get(BLOCKS).entrySet())
            {
                ResourceLocation rl = missingBlock.getKey();
                Integer id = missingBlock.getValue();
                FMLLog.log(Level.DEBUG, "Replacing id %s named as %s with air block. If the mod becomes available again later, it can reload here", id, rl);
                // Mark this entry as a dummy
                PersistentRegistry.STAGING.getRegistry(BLOCKS, Block.class).markDummy(rl, id, new BlockDummyAir());
            }
        }
        // If we're loading up the world from disk, we want to add in the new data that might have been provisioned by mods
        if (injectFrozenData)
        {
            // So we load it from the frozen persistent registry
            for (Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> r : PersistentRegistry.ACTIVE.registries.entrySet())
            {
                final Class<? extends IForgeRegistryEntry> registrySuperType = PersistentRegistry.ACTIVE.getRegistrySuperType(r.getKey());
                loadFrozenDataToStagingRegistry(remaps, r.getKey(), registrySuperType);
            }
        }

        // Validate that all the STAGING data is good
        forAllRegistries(PersistentRegistry.STAGING, ValidateRegistryFunction.OPERATION);

        // Load the STAGING registry into the ACTIVE registry
        for (Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> r : PersistentRegistry.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> registrySuperType = PersistentRegistry.ACTIVE.getRegistrySuperType(r.getKey());
            loadRegistry(r.getKey(), PersistentRegistry.STAGING, PersistentRegistry.ACTIVE, registrySuperType);
        }

        // Dump the active registry
        forAllRegistries(PersistentRegistry.ACTIVE, DumpRegistryFunction.OPERATION);

        // Tell mods that the ids have changed
        Loader.instance().fireRemapEvent(remaps.get(BLOCKS), remaps.get(ITEMS), false);

        // The id map changed, ensure we apply object holders
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();

        // Clean out the staging registry now, we're done with it
        PersistentRegistry.STAGING.clean();

        // Return an empty list, because we're good
        return ImmutableList.of();
    }
    private static class BlockDummyAir extends BlockAir {
        private BlockDummyAir() {
            setUnlocalizedName("air");
        }
    }
    private static void forAllRegistries(PersistentRegistry registrySet, Function<Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>>, Void> operation)
    {
        for (Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> r : registrySet.registries.entrySet())
        {
            operation.apply(r);
        }
    }

    private static <T extends IForgeRegistryEntry<T>> void loadRegistry(final ResourceLocation registryName, final PersistentRegistry from, final PersistentRegistry to, final Class<T> regType)
    {
        FMLControlledNamespacedRegistry<T> fromRegistry = from.getRegistry(registryName, regType);
        if (fromRegistry == null)
        {
            FMLControlledNamespacedRegistry<T> toRegistry = to.getRegistry(registryName, regType);
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
            toRegistry.notifyCallbacks();
        }
        else
        {
            FMLControlledNamespacedRegistry<T> toRegistry = to.getOrShallowCopyRegistry(registryName, regType, fromRegistry);
            toRegistry.set(fromRegistry);
        }
    }

    private static <T extends IForgeRegistryEntry<T>> void loadFrozenDataToStagingRegistry(Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, ResourceLocation registryName, Class<T> regType)
    {
        FMLControlledNamespacedRegistry<T> frozenRegistry = PersistentRegistry.FROZEN.getRegistry(registryName, regType);
        FMLControlledNamespacedRegistry<T> newRegistry = PersistentRegistry.STAGING.getOrShallowCopyRegistry(registryName, regType, frozenRegistry);
        newRegistry.loadIds(frozenRegistry.getEntriesNotIn(newRegistry), Maps.<ResourceLocation, Integer>newLinkedHashMap(), remaps.get(registryName), frozenRegistry, registryName);
    }

    // TODO: Remove when we don't care about loading 1.7.10(?) worlds?
    private static ResourceLocation getFixedName(ResourceLocation registryName)
    {
        //Translate old names
        if ("fml:blocks".equals(registryName.toString())) registryName = PersistentRegistryManager.BLOCKS;
        else if ("fml:items".equals(registryName.toString())) registryName = PersistentRegistryManager.ITEMS;
        else if ("fmlgr:villagerprofessions".equals(registryName.toString())) registryName = VillagerRegistry.PROFESSIONS;

        return registryName;
    }

    private static <T extends IForgeRegistryEntry<T>> void loadPersistentDataToStagingRegistry(boolean injectFrozenData, Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, LinkedHashMap<ResourceLocation, Map<ResourceLocation, Integer>> missing, Map.Entry<ResourceLocation, GameDataSnapshot.Entry> snapEntry, Class<T> regType)
    {
        ResourceLocation registryName = getFixedName(snapEntry.getKey());

        FMLControlledNamespacedRegistry<T> currentRegistry = PersistentRegistry.ACTIVE.getRegistry(registryName, regType);
        if (currentRegistry == null)
        {
            return; // We've already asked the user if they wish to continue. So if the reg isnt found just assume the user knows and accepted it.
        }
        FMLControlledNamespacedRegistry<T> newRegistry = PersistentRegistry.STAGING.getOrShallowCopyRegistry(registryName, regType, currentRegistry);
        // Copy the persistent substitution set from the currently active one into the new registry
        newRegistry.getPersistentSubstitutions().putAll(currentRegistry.getPersistentSubstitutions());
        GameDataSnapshot.Entry snapshotEntry = snapEntry.getValue();
        Set<ResourceLocation> substitutions = snapshotEntry.substitutions;
        if (injectFrozenData)
        {
            substitutions = Sets.union(snapshotEntry.substitutions, currentRegistry.getActiveSubstitutions());
        }
        newRegistry.loadAliases(snapshotEntry.aliases);
        newRegistry.loadBlocked(snapshotEntry.blocked);
        missing.put(registryName, Maps.<ResourceLocation, Integer>newLinkedHashMap());
        remaps.put(registryName, Maps.<ResourceLocation, Integer[]>newHashMap());
        // Load current dummies BEFORE the snapshot is loaded so that add() will remove from the list.
        // Potentially causes issues from cpw's previous comment. Must keep eye on.
        newRegistry.loadDummied(snapshotEntry.dummied);
        newRegistry.loadIds(snapshotEntry.ids, missing.get(registryName), remaps.get(registryName), currentRegistry, registryName);
        newRegistry.loadSubstitutions(substitutions);
    }

    public static boolean isFrozen(FMLControlledNamespacedRegistry<?> registry)
    {
        return PersistentRegistry.FROZEN.containsRegistry(registry);
    }

    public static void revertToFrozen()
    {
        if (!PersistentRegistry.FROZEN.isPopulated())
        {
            FMLLog.warning("Can't revert to frozen GameData state without freezing first.");
            return;
        }
        else
        {
            FMLLog.fine("Reverting to frozen data state.");
        }
        for (Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> r : PersistentRegistry.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> registrySuperType = PersistentRegistry.ACTIVE.getRegistrySuperType(r.getKey());
            loadRegistry(r.getKey(), PersistentRegistry.FROZEN, PersistentRegistry.ACTIVE, registrySuperType);
        }
        // the id mapping has reverted, fire remap events for those that care about id changes
        Loader.instance().fireRemapEvent(ImmutableMap.<ResourceLocation, Integer[]>of(), ImmutableMap.<ResourceLocation, Integer[]>of(), true);

        // the id mapping has reverted, ensure we sync up the object holders
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        FMLLog.fine("Frozen state restored.");
    }

    public static void freezeData()
    {
        FMLLog.fine("Freezing block and item id maps");
        for (Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> r : PersistentRegistry.ACTIVE.registries.entrySet())
        {
            // This has to be performed prior to invoking the method so the compiler can precompute the type bounds for the method
            final Class<? extends IForgeRegistryEntry> registrySuperType = PersistentRegistry.ACTIVE.getRegistrySuperType(r.getKey());
            loadRegistry(r.getKey(), PersistentRegistry.ACTIVE, PersistentRegistry.FROZEN, registrySuperType);
        }
        forAllRegistries(PersistentRegistry.FROZEN, ValidateRegistryFunction.OPERATION);
    }

    public static void freezeVanilla()
    {
        FMLLog.fine("Creating vanilla freeze snapshot");
        for (Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> r : PersistentRegistry.ACTIVE.registries.entrySet())
        {
            // This has to be performed prior to invoking the method so the compiler can precompute the type bounds for the method
            final Class<? extends IForgeRegistryEntry> registrySuperType = PersistentRegistry.ACTIVE.getRegistrySuperType(r.getKey());
            loadRegistry(r.getKey(), PersistentRegistry.ACTIVE, PersistentRegistry.VANILLA, registrySuperType);
        }
        forAllRegistries(PersistentRegistry.VANILLA, ValidateRegistryFunction.OPERATION);
        FMLLog.fine("Vanilla freeze snapshot created");
    }

    public static List<String> processIdRematches(Iterable<FMLMissingMappingsEvent.MissingMapping> missedMappings, boolean isLocalWorld, Map<ResourceLocation, Integer> missingBlocks, Map<ResourceLocation, Integer> missingItems, Map<ResourceLocation, Integer[]> remapBlocks, Map<ResourceLocation, Integer[]> remapItems)
    {
        List<String> failed = Lists.newArrayList();
        List<String> ignored = Lists.newArrayList();
        List<String> warned = Lists.newArrayList();
        List<String> defaulted = Lists.newArrayList();

        final PersistentRegistry staging = PersistentRegistry.STAGING;
        final PersistentRegistry active = PersistentRegistry.ACTIVE;
        for (FMLMissingMappingsEvent.MissingMapping remap : missedMappings)
        {
            FMLMissingMappingsEvent.Action action = remap.getAction();

            if (action == FMLMissingMappingsEvent.Action.REMAP)
            {
                // block/item re-mapped, finish the registration with the new name/object, but the old id
                int currId = -1, newId = -1;
                ResourceLocation newName;

                if (remap.type == GameRegistry.Type.BLOCK)
                {
                    currId = staging.getRegistry(BLOCKS, Block.class).getId((Block)remap.getTarget());
                    newName = active.getRegistry(BLOCKS, Block.class).getNameForObject((Block)remap.getTarget());
                    FMLLog.fine("The Block %s is being remapped to %s.", remap.name, newName);

                    missingBlocks.remove(new ResourceLocation(remap.name));
                    newId = staging.getRegistry(BLOCKS, Block.class).add(remap.id, newName, (Block)remap.getTarget());
                    staging.getRegistry(BLOCKS, Block.class).addAlias(remap.resourceLocation, newName);
                }
                else if (remap.type == GameRegistry.Type.ITEM)
                {
                    currId = staging.getRegistry(ITEMS, Item.class).getId((Item)remap.getTarget());
                    newName = active.getRegistry(ITEMS, Item.class).getNameForObject((Item)remap.getTarget());
                    FMLLog.fine("The Item %s is being remapped to %s.", remap.name, newName);

                    missingItems.remove(new ResourceLocation(remap.name));
                    newId = staging.getRegistry(ITEMS, Item.class).add(remap.id, newName, (Item)remap.getTarget());
                    staging.getRegistry(ITEMS, Item.class).addAlias(remap.resourceLocation, newName);
                }
                else
                {
                    // currently not remapping non-blocks and items
                    continue;
                }

                if (newId != remap.id)
                {
                    throw new IllegalStateException();
                }

                if (currId != newId)
                {
                    FMLLog.info("Fixed %s id mismatch %s: %d (init) -> %d (map).", remap.type == GameRegistry.Type.BLOCK ? "block" : "item", newName, currId, newId);
                    (remap.type == GameRegistry.Type.BLOCK ? remapBlocks : remapItems).put(newName, new Integer[] {currId, newId});
                }
            }
            else if (action == FMLMissingMappingsEvent.Action.BLOCKONLY)
            {
                // Pulled out specifically so the block doesn't get reassigned a new ID just because it's
                // Item block has gone away
                FMLLog.fine("The ItemBlock %s is no longer present in the game. The residual block will remain", remap.name);
            }
            else
            {
                // block item missing, warn as requested and block the id
                if (action == FMLMissingMappingsEvent.Action.DEFAULT)
                {
                    defaulted.add(remap.name);
                }
                else if (action == FMLMissingMappingsEvent.Action.IGNORE)
                {
                    ignored.add(remap.name);
                }
                else if (action == FMLMissingMappingsEvent.Action.FAIL)
                {
                    failed.add(remap.name);
                }
                else if (action == FMLMissingMappingsEvent.Action.WARN)
                {
                    warned.add(remap.name);
                }
                // prevent the id from being reused later
                if (remap.type == GameRegistry.Type.BLOCK)
                {
                    staging.getRegistry(BLOCKS, Block.class).blockId(remap.id);
                }
                else if (remap.type == GameRegistry.Type.ITEM)
                {
                    staging.getRegistry(ITEMS, Item.class).blockId(remap.id);
                }
            }
        }

        if (!defaulted.isEmpty())
        {
            String text = "Forge Mod Loader detected missing blocks/items.\n\n" +
                    "There are " + defaulted.size() + " missing blocks and items in this save.\n" +
                    "If you continue the missing blocks/items will get removed.\n" +
                    "A world backup will be automatically created in your saves directory.\n\n" +
                    "Missing Blocks/Items:\n";

            for (String s : defaulted) text += s + "\n";

            boolean confirmed = StartupQuery.confirm(text);
            if (!confirmed)
            {
                StartupQuery.abort();
            }

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
                        FMLLog.severe("!!!!!!!!!! UPDATING WORLD WITHOUT DOING BACKUP !!!!!!!!!!!!!!!!");
                }
            } catch (IOException e)
            {
                StartupQuery.notify("The world backup couldn't be created.\n\n" + e);
                StartupQuery.abort();
            }

            warned.addAll(defaulted);
        }
        if (!failed.isEmpty())
        {
            FMLLog.severe("This world contains blocks and items that refuse to be remapped. The world will not be loaded");
            return failed;
        }
        if (!warned.isEmpty())
        {
            FMLLog.warning("This world contains block and item mappings that may cause world breakage");
            return failed;
        }
        else if (!ignored.isEmpty())
        {
            FMLLog.fine("There were %d missing mappings that have been ignored", ignored.size());
        }
        return failed;
    }


    public static GameDataSnapshot takeSnapshot()
    {
        final GameDataSnapshot snap = new GameDataSnapshot();
        forAllRegistries(PersistentRegistry.ACTIVE, new Function<Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>>, Void>()
        {
            @Override
            public Void apply(Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> input)
            {
                snap.entries.put(input.getKey(), new GameDataSnapshot.Entry(input.getValue()));
                return null;
            }
        });
        return snap;
    }


    public static class GameDataSnapshot
    {
        public static class Entry
        {
            public final Map<ResourceLocation, Integer> ids;
            public final Set<ResourceLocation> substitutions;
            public final Map<ResourceLocation, ResourceLocation> aliases;
            public final Set<Integer> blocked;
            public final Set<ResourceLocation> dummied;

            public Entry()
            {
                this(new HashMap<ResourceLocation, Integer>(), new HashSet<ResourceLocation>(), new HashMap<ResourceLocation, ResourceLocation>(), new HashSet<Integer>(), new HashSet<ResourceLocation>());
            }

            public Entry(Map<ResourceLocation, Integer> ids, Set<ResourceLocation> substitutions, Map<ResourceLocation, ResourceLocation> aliases, Set<Integer> blocked, Set<ResourceLocation> dummies)
            {
                this.ids = ids;
                this.substitutions = substitutions;
                this.aliases = aliases;
                this.blocked = blocked;
                this.dummied = dummies;
            }

            public Entry(FMLControlledNamespacedRegistry<?> registry)
            {
                this.ids = Maps.newHashMap();
                this.substitutions = Sets.newHashSet();
                this.aliases = Maps.newHashMap();
                this.blocked = Sets.newHashSet();
                this.dummied = Sets.newHashSet();

                registry.serializeIds(this.ids);
                registry.serializeSubstitutions(this.substitutions);
                registry.serializeAliases(this.aliases);
                registry.serializeBlockList(this.blocked);
                registry.serializeDummied(this.dummied);
            }
        }

        public final Map<ResourceLocation, Entry> entries = Maps.newHashMap();
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryDelegate<T> makeDelegate(T obj, Class<T> rootClass)
    {
        return PersistentRegistry.ACTIVE.getRegistry(rootClass).getDelegate(obj, rootClass);
    }


    private static class DumpRegistryFunction implements Function<Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>>, Void>
    {
        static final DumpRegistryFunction OPERATION = new DumpRegistryFunction();

        @Override
        public Void apply(Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> input)
        {
            input.getValue().dump(input.getKey());
            return null;
        }
    }

    private static class ValidateRegistryFunction implements Function<Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>>, Void>
    {
        static final ValidateRegistryFunction OPERATION = new ValidateRegistryFunction();

        @Override
        public Void apply(Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> input)
        {
            input.getValue().validateContent(input.getKey());
            return null;
        }
    }

    private static class ResetDelegatesFunction implements Function<Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>>, Void>
    {
        static final ResetDelegatesFunction OPERATION = new ResetDelegatesFunction();

        @Override
        public Void apply(Map.Entry<ResourceLocation, FMLControlledNamespacedRegistry<?>> input)
        {
            input.getValue().resetSubstitutionDelegates();
            return null;
        }
    }

}
