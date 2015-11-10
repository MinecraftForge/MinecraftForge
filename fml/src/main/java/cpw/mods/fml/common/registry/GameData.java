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

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.io.Files;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.StartupQuery;
import cpw.mods.fml.common.ZipperUtil;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.registry.GameRegistry.Type;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class GameData {
    static final int MIN_BLOCK_ID = 0;
    static final int MAX_BLOCK_ID = 4095;
    static final int MIN_ITEM_ID = 4096;
    static final int MAX_ITEM_ID = 31999;

    private static final GameData mainData = new GameData();

    /**
     * @deprecated use {@link #getBlockRegistry()} instead.
     */
    @Deprecated
    public static final FMLControlledNamespacedRegistry<Block> blockRegistry = getBlockRegistry();
    /**
     * @deprecated use {@link #getItemRegistry()} instead.
     */
    @Deprecated
    public static final FMLControlledNamespacedRegistry<Item> itemRegistry = getItemRegistry();

    private static Table<String, String, ItemStack> customItemStacks = HashBasedTable.create();
    private static Map<UniqueIdentifier, ModContainer> customOwners = Maps.newHashMap();
    private static GameData frozen;

    // public api

    /**
     * Get the currently active block registry.
     *
     * @return Block Registry.
     */
    public static FMLControlledNamespacedRegistry<Block> getBlockRegistry() {
        return getMain().iBlockRegistry;
    }

    /**
     * Get the currently active item registry.
     *
     * @return Item Registry.
     */
    public static FMLControlledNamespacedRegistry<Item> getItemRegistry() {
        return getMain().iItemRegistry;
    }

    /**
     * @deprecated no replacement planned
     */
    @Deprecated
    public static ModContainer findModOwner(String string)
    {
        UniqueIdentifier ui = new UniqueIdentifier(string);
        if (customOwners.containsKey(ui))
        {
            return customOwners.get(ui);
        }
        return Loader.instance().getIndexedModList().get(ui.modId);
    }

    // internal from here

    public static class GameDataSnapshot {
        public final Map<String,Integer> idMap;
        public final Set<String> blockSubstitutions;
        public final Set<String> itemSubstitutions;
        public GameDataSnapshot(Map<String, Integer> idMap, Set<String> blockSubstitutions, Set<String> itemSubstitutions)
        {
            this.idMap = idMap;
            this.blockSubstitutions = blockSubstitutions;
            this.itemSubstitutions = itemSubstitutions;
        }
    }
    public static GameDataSnapshot buildItemDataList()
    {
        Map<String,Integer> idMapping = Maps.newHashMap();
        getMain().iBlockRegistry.serializeInto(idMapping);
        getMain().iItemRegistry.serializeInto(idMapping);
        Set<String> blockSubs = Sets.newHashSet();
        getMain().iBlockRegistry.serializeSubstitutions(blockSubs);
        Set<String> itemSubs = Sets.newHashSet();
        getMain().iItemRegistry.serializeSubstitutions(itemSubs);
        return new GameDataSnapshot(idMapping, blockSubs, itemSubs);
    }

    public static int[] getBlockedIds()
    {
        int[] ret = new int[getMain().blockedIds.size()];
        int index = 0;

        for (int id : getMain().blockedIds)
        {
            ret[index] = id;
            index++;
        }

        return ret;
    }

    public static void dumpRegistry(File minecraftDir)
    {
        if (customItemStacks == null)
        {
            return;
        }
        if (Boolean.valueOf(System.getProperty("fml.dumpRegistry", "false")).booleanValue())
        {
            ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
            for (String modId : customItemStacks.rowKeySet())
            {
                builder.putAll(modId, customItemStacks.row(modId).keySet());
            }

            File f = new File(minecraftDir, "itemStackRegistry.csv");
            MapJoiner mapJoiner = Joiner.on("\n").withKeyValueSeparator(",");
            try
            {
                Files.write(mapJoiner.join(builder.build().entries()), f, Charsets.UTF_8);
                FMLLog.log(Level.INFO, "Dumped item registry data to %s", f.getAbsolutePath());
            }
            catch (IOException e)
            {
                FMLLog.log(Level.ERROR, e, "Failed to write registry data to %s", f.getAbsolutePath());
            }
        }
    }

    static Item findItem(String modId, String name)
    {
        return (Item) getMain().iItemRegistry.getObject(modId + ":" + name);
    }

    static Block findBlock(String modId, String name)
    {
        String key = modId + ":" + name;
        return getMain().iBlockRegistry.containsKey(key) ? getMain().iBlockRegistry.getObject(key) : null;
    }

    static ItemStack findItemStack(String modId, String name)
    {
        ItemStack is = customItemStacks.get(modId, name);
        if (is == null)
        {
            Item i = findItem(modId, name);
            if (i != null)
            {
                is = new ItemStack(i, 0 ,0);
            }
        }
        if (is == null)
        {
            Block b = findBlock(modId, name);
            if (b != null)
            {
                is = new ItemStack(b, 0, Short.MAX_VALUE);
            }
        }
        return is;
    }

    static void registerCustomItemStack(String name, ItemStack itemStack)
    {
        customItemStacks.put(Loader.instance().activeModContainer().getModId(), name, itemStack);
    }

    static UniqueIdentifier getUniqueName(Block block)
    {
        if (block == null) return null;
        String name = getMain().iBlockRegistry.getNameForObject(block);
        UniqueIdentifier ui = new UniqueIdentifier(name);
        if (customItemStacks.contains(ui.modId, ui.name))
        {
            return null;
        }

        return ui;
    }

    static UniqueIdentifier getUniqueName(Item item)
    {
        if (item == null) return null;
        String name = getMain().iItemRegistry.getNameForObject(item);
        UniqueIdentifier ui = new UniqueIdentifier(name);
        if (customItemStacks.contains(ui.modId, ui.name))
        {
            return null;
        }

        return ui;
    }

    /**
     * Fix IDs improperly allocated by early versions of the registry, best-effort.
     *
     * Items sharing the same ID with a block, but not sharing the same registry name will be
     * mapped to an unused id. Losing items instead of blocks should minimize the damage.
     *
     * @param dataList List containing the IDs to fix
     */
    public static void fixBrokenIds(Map<String, Integer> dataList, Set<Integer> blockedIds)
    {
        BitSet availabilityMap = new BitSet(MAX_ITEM_ID + 1);

        // reserve all ids occupied by blocks
        for (Entry<String, Integer> entry : dataList.entrySet())
        {
            String itemName = entry.getKey();
            @SuppressWarnings("unused")
            String realName = itemName.substring(1);

            if (itemName.charAt(0) == '\u0001') // is a block
            {
                availabilityMap.set(entry.getValue());
            }
        }

        Set<Integer> newBlockedIds = new HashSet<Integer>();
        Set<String> itemsToRemove = new HashSet<String>();
        Map<String, Integer> itemsToRelocate = new HashMap<String, Integer>();

        // check all ids occupied by items
        for (Entry<String, Integer> entry : dataList.entrySet())
        {
            String itemName = entry.getKey();

            if (itemName.charAt(0) != '\u0001') // is an item
            {
                int oldId = entry.getValue();
                String realName = itemName.substring(1);
                String blockName = '\u0001' + realName;
                Item item = getMain().iItemRegistry.getRaw(realName);
                boolean blockThisId = false; // block oldId unless it's used by a block

                if (item == null) // item no longer available
                {
                    // can't fix items without reliably checking if they are ItemBlocks
                    FMLLog.warning("Item %s (old id %d) is no longer available and thus can't be fixed.", realName, oldId);
                    itemsToRemove.add(itemName);
                    blockThisId = true;
                }
                else if (item instanceof ItemBlock)
                {
                    if (dataList.containsKey(blockName)) // the item was an ItemBlock before
                    {
                        int blockId = dataList.get(blockName);

                        if (blockId != oldId) // mis-located ItemBlock
                        {
                            // relocate to the matching block
                            FMLLog.warning("ItemBlock %s (old id %d) doesn't have the same id as its block (%d).", realName, oldId, blockId);
                            itemsToRelocate.put(entry.getKey(), blockId);
                            blockThisId = true;
                        }
                        else // intact ItemBlock
                        {
                            availabilityMap.set(oldId); // occupy id
                        }
                    }
                    else // the item hasn't been an ItemBlock before, but it's now
                    {
                        // can't fix these, drop them
                        FMLLog.warning("Item %s (old id %d) has been migrated to an ItemBlock and can't be fixed.", realName, oldId);
                        itemsToRemove.add(itemName);
                        blockThisId = true;
                    }
                }
                else if (availabilityMap.get(oldId)) // normal item, id is already occupied
                {
                    // remove the item mapping
                    FMLLog.warning("Item %s (old id %d) is conflicting with another block/item and can't be fixed.", realName, oldId);
                    itemsToRemove.add(itemName);
                }
                else // intact Item
                {
                    availabilityMap.set(oldId); // occupy id
                }

                // handle blocking the id from future use if possible (i.e. not used by a conflicting block)
                // blockThisId requests don't modify availabilityMap, it could only be set by a block (or another item, which isn't being handled)
                if (blockThisId && !availabilityMap.get(oldId))
                {
                    // there's no block occupying this id, thus block the id from future use
                    // as there may still be ItemStacks in the world referencing it
                    newBlockedIds.add(oldId);
                    availabilityMap.set(oldId);
                }
            }
        }

        if (itemsToRemove.isEmpty() && itemsToRelocate.isEmpty()) return; // nothing to do

        // confirm
        String text = "Forge Mod Loader detected that this save is damaged.\n\n" +
                "It's likely that an automatic repair can successfully restore\n" +
                "most of it, except some items which may get swapped with others.\n\n" +
                "A world backup will be created as a zip file in your saves\n" +
                "directory automatically.\n\n" +
                itemsToRemove.size()+" items need to be removed.\n"+
                itemsToRelocate.size()+" items need to be relocated.";

        boolean confirmed = StartupQuery.confirm(text);
        if (!confirmed) StartupQuery.abort();

        // confirm missing mods causing item removal
        Set<String> modsMissing = new HashSet<String>();

        for (String itemName : itemsToRemove)
        {
            modsMissing.add(itemName.substring(1, itemName.indexOf(':')));
        }

        for (Iterator<String> it = modsMissing.iterator(); it.hasNext(); )
        {
            String mod = it.next();

            if (mod.equals("minecraft") || Loader.isModLoaded(mod)) it.remove();
        }

        if (!modsMissing.isEmpty())
        {
            text = "Forge Mod Loader detected that "+modsMissing.size()+" mods are missing.\n\n" +
                        "If you continue items previously provided by those mods will be\n" +
                        "removed while repairing this world save.\n\n" +
                        "Missing mods:\n";

            for (String mod : modsMissing) text += mod+"\n";

            confirmed = StartupQuery.confirm(text);
            if (!confirmed) StartupQuery.abort();
        }

        // backup
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
        }
        catch (IOException e)
        {
            StartupQuery.notify("The world backup couldn't be created.\n\n"+e);
            StartupQuery.abort();
        }

        // apply fix
        for (String itemName : itemsToRemove)
        {
            int id = dataList.remove(itemName);

            FMLLog.warning("Removed Item %s, old id %d.", itemName.substring(1), id);
        }

        for (Map.Entry<String, Integer> entry : itemsToRelocate.entrySet())
        {
            String itemName = entry.getKey();
            int newId = entry.getValue();

            int oldId = dataList.put(itemName, newId);

            FMLLog.warning("Remapped Item %s to id %d, old id %d.", itemName.substring(1), newId, oldId);
        }

        blockedIds.addAll(newBlockedIds);
    }

    public static List<String> injectWorldIDMap(Map<String, Integer> dataList, Set<String> blockSubstitutions, Set<String> itemSubstitutions, boolean injectFrozenData, boolean isLocalWorld)
    {
        return injectWorldIDMap(dataList, new HashSet<Integer>(), new HashMap<String, String>(), new HashMap<String, String>(), blockSubstitutions, itemSubstitutions, injectFrozenData, isLocalWorld);
    }

    public static List<String> injectWorldIDMap(Map<String, Integer> dataList, Set<Integer> blockedIds, Map<String, String> blockAliases, Map<String, String> itemAliases, Set<String> blockSubstitutions, Set<String> itemSubstitutions, boolean injectFrozenData, boolean isLocalWorld)
    {
        FMLLog.info("Injecting existing block and item data into this %s instance", FMLCommonHandler.instance().getEffectiveSide().isServer() ? "server" : "client");
        Map<String, Integer[]> remaps = Maps.newHashMap();
        LinkedHashMap<String, Integer> missingMappings = new LinkedHashMap<String, Integer>();
        getMain().testConsistency();
        getMain().iBlockRegistry.dump();
        getMain().iItemRegistry.dump();

        getMain().iItemRegistry.resetSubstitutionDelegates();
        GameData newData = new GameData();

        for (int id : blockedIds)
        {
            newData.block(id);
        }

        for (Map.Entry<String, String> entry : blockAliases.entrySet())
        {
            newData.iBlockRegistry.addAlias(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entry : itemAliases.entrySet())
        {
            newData.iItemRegistry.addAlias(entry.getKey(), entry.getValue());
        }

        for (String entry : blockSubstitutions)
        {
            newData.iBlockRegistry.activateSubstitution(entry);
        }
        for (String entry : itemSubstitutions)
        {
            newData.iItemRegistry.activateSubstitution(entry);
        }
        if (injectFrozenData)
        {
            for (String newBlockSubstitution : getMain().blockSubstitutions.keySet())
            {
                if (!blockSubstitutions.contains(newBlockSubstitution))
                {
                    newData.iBlockRegistry.activateSubstitution(newBlockSubstitution);
                }
            }
            for (String newItemSubstitution : getMain().itemSubstitutions.keySet())
            {
                if (!itemSubstitutions.contains(newItemSubstitution))
                {
                    newData.iItemRegistry.activateSubstitution(newItemSubstitution);
                }
            }
        }
        // process blocks and items in the world, blocks in the first pass, items in the second
        // blocks need to be added first for proper ItemBlock handling
        for (int pass = 0; pass < 2; pass++)
        {
            boolean isBlock = (pass == 0);

            for (Entry<String, Integer> entry : dataList.entrySet())
            {
                String itemName = entry.getKey();
                int newId = entry.getValue();

                // names starting with 0x1 are blocks, skip if the type isn't handled by this pass
                if ((itemName.charAt(0) == '\u0001') != isBlock) continue;

                itemName = itemName.substring(1);
                int currId = isBlock ? getMain().iBlockRegistry.getId(itemName) : getMain().iItemRegistry.getId(itemName);

                if (currId == -1)
                {
                    FMLLog.info("Found a missing id from the world %s", itemName);
                    missingMappings.put(entry.getKey(), newId);
                    continue; // no block/item -> nothing to add
                }
                else if (currId != newId)
                {
                    FMLLog.fine("Fixed %s id mismatch %s: %d (init) -> %d (map).", isBlock ? "block" : "item", itemName, currId, newId);
                    remaps.put(itemName, new Integer[] { currId, newId });
                }

                // register
                if (isBlock)
                {
                    currId = newData.registerBlock(getMain().iBlockRegistry.getRaw(itemName), itemName, newId);
                }
                else
                {
                    currId = newData.registerItem(getMain().iItemRegistry.getRaw(itemName), itemName, newId);
                }

                if (currId != newId)
                {
                    throw new IllegalStateException(String.format("Can't map %s %s to id %d (seen at: %d), already occupied by %s, blocked %b, ItemBlock %b",
                            isBlock ? "block" : "item",
                                    itemName,
                                    newId,
                                    currId,
                                    isBlock ? newData.iBlockRegistry.getRaw(newId) : newData.iItemRegistry.getRaw(newId),
                                    newData.blockedIds.contains(newId),
                                    isBlock ? false : (getMain().iItemRegistry.getRaw(currId) instanceof ItemBlock)));
                }
            }
        }

        List<String> missedMappings = Loader.instance().fireMissingMappingEvent(missingMappings, isLocalWorld, newData, remaps);
        if (!missedMappings.isEmpty()) return missedMappings;

        if (injectFrozenData) // add blocks + items missing from the map
        {
            Map<String, Integer> missingBlocks = frozen.iBlockRegistry.getEntriesNotIn(newData.iBlockRegistry);
            Map<String, Integer> missingItems = frozen.iItemRegistry.getEntriesNotIn(newData.iItemRegistry);

            if (!missingBlocks.isEmpty() || !missingItems.isEmpty())
            {
                FMLLog.info("Injecting new block and item data into this server instance.");

                for (int pass = 0; pass < 2; pass++)
                {
                    boolean isBlock = pass == 0;
                    Map<String, Integer> missing = (pass == 0) ? missingBlocks : missingItems;

                    for (Entry<String, Integer> entry : missing.entrySet())
                    {
                        String itemName = entry.getKey();
                        int currId = entry.getValue();
                        int newId;

                        if (isBlock)
                        {
                            newId = newData.registerBlock(frozen.iBlockRegistry.getRaw(itemName), itemName, currId);
                        }
                        else
                        {
                            newId = newData.registerItem(frozen.iItemRegistry.getRaw(itemName), itemName, currId);
                        }

                        FMLLog.info("Injected new block/item %s: %d (init) -> %d (map).", itemName, currId, newId);

                        if (newId != currId) // a new id was assigned
                        {
                            remaps.put(itemName, new Integer[] { entry.getValue(), newId });
                        }
                    }
                }
            }
        }

        newData.testConsistency();
        getMain().set(newData);

        getMain().iBlockRegistry.dump();
        getMain().iItemRegistry.dump();
        Loader.instance().fireRemapEvent(remaps);
        // The id map changed, ensure we apply object holders
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        return ImmutableList.of();
    }

    public static List<String> processIdRematches(Iterable<MissingMapping> missedMappings, boolean isLocalWorld, GameData gameData, Map<String, Integer[]> remaps)
    {
        List<String> failed = Lists.newArrayList();
        List<String> ignored = Lists.newArrayList();
        List<String> warned = Lists.newArrayList();
        List<String> defaulted = Lists.newArrayList();

        for (MissingMapping remap : missedMappings)
        {
            FMLMissingMappingsEvent.Action action = remap.getAction();

            if (action == FMLMissingMappingsEvent.Action.REMAP)
            {
                // block/item re-mapped, finish the registration with the new name/object, but the old id
                int currId, newId;
                String newName;

                if (remap.type == Type.BLOCK)
                {
                    currId = getMain().iBlockRegistry.getId((Block) remap.getTarget());
                    newName = getMain().iBlockRegistry.getNameForObject(remap.getTarget());
                    FMLLog.fine("The Block %s is being remapped to %s.", remap.name, newName);

                    newId = gameData.registerBlock((Block) remap.getTarget(), newName, remap.id);
                    gameData.iBlockRegistry.addAlias(remap.name, newName);
                }
                else
                {
                    currId = getMain().iItemRegistry.getId((Item) remap.getTarget());
                    newName = getMain().iItemRegistry.getNameForObject(remap.getTarget());
                    FMLLog.fine("The Item %s is being remapped to %s.", remap.name, newName);

                    newId = gameData.registerItem((Item) remap.getTarget(), newName, remap.id);
                    gameData.iItemRegistry.addAlias(remap.name, newName);
                }

                if (newId != remap.id) throw new IllegalStateException();

                if (currId != newId)
                {
                    FMLLog.info("Fixed %s id mismatch %s: %d (init) -> %d (map).", remap.type == Type.BLOCK ? "block" : "item", newName, currId, newId);
                    remaps.put(newName, new Integer[] { currId, newId });
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

                gameData.block(remap.id); // prevent the id from being reused later
            }
        }

        if (!defaulted.isEmpty())
        {
            String text = "Forge Mod Loader detected missing blocks/items.\n\n" +
                    "There are "+defaulted.size()+" missing blocks and items in this save.\n" +
                    "If you continue the missing blocks/items will get removed.\n" +
                    "A world backup will be automatically created in your saves directory.\n\n" +
                    "Missing Blocks/Items:\n";

            for (String s : defaulted) text += s + "\n";

            boolean confirmed = StartupQuery.confirm(text);
            if (!confirmed) StartupQuery.abort();

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
            }
            catch (IOException e)
            {
                StartupQuery.notify("The world backup couldn't be created.\n\n"+e);
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
            FMLLog.severe("This world contains block and item mappings that may cause world breakage");
            return failed;
        }
        else if (!ignored.isEmpty())
        {
            FMLLog.fine("There were %d missing mappings that have been ignored", ignored.size());
        }
        return failed;
    }

    public static void freezeData()
    {
        FMLLog.fine("Freezing block and item id maps");

        getMain().testConsistency();
        frozen = new GameData(getMain());
        frozen.testConsistency();
    }

    public static void revertToFrozen()
    {
        if (frozen == null)
        {
            FMLLog.warning("Can't revert to frozen GameData state without freezing first.");
        }
        else
        {
            FMLLog.fine("Reverting to frozen data state.");

            getMain().set(frozen);
        }
        // the id mapping has reverted, fire remap events for those that care about id changes
        Loader.instance().fireRemapEvent(ImmutableMap.<String,Integer[]>of());
        // the id mapping has reverted, ensure we sync up the object holders
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
    }

    protected static boolean isFrozen(FMLControlledNamespacedRegistry<?> registry)
    {
        return frozen != null && (getMain().iBlockRegistry == registry || getMain().iItemRegistry == registry);
    }

    protected static GameData getMain()
    {
        return mainData;
    }

    // internal registry objects
    private final FMLControlledNamespacedRegistry<Block> iBlockRegistry;
    private final FMLControlledNamespacedRegistry<Item> iItemRegistry;
    // bit set marking ids as occupied
    private final BitSet availabilityMap;
    // IDs previously allocated in a world, but now unmapped/dangling; prevents the IDs from being reused
    private final Set<Integer> blockedIds;

    private GameData()
    {
        iBlockRegistry = new FMLControlledNamespacedRegistry<Block>("minecraft:air", MAX_BLOCK_ID, MIN_BLOCK_ID, Block.class,'\u0001');
        iItemRegistry = new FMLControlledNamespacedRegistry<Item>(null, MAX_ITEM_ID, MIN_ITEM_ID, Item.class,'\u0002');
        availabilityMap = new BitSet(MAX_ITEM_ID + 1);
        blockedIds = new HashSet<Integer>();
    }

    private GameData(GameData data)
    {
        this();
        set(data);
    }

    private void set(GameData data)
    {
        iBlockRegistry.set(data.iBlockRegistry);
        iItemRegistry.set(data.iItemRegistry);
        availabilityMap.clear();
        availabilityMap.or(data.availabilityMap);
        blockedIds.clear();
        blockedIds.addAll(data.blockedIds);
    }

    int register(Object obj, String name, int idHint) // from FMLControlledNamespacedRegistry.addObject
    {
        // tolerate extra name prefixes here since mc does it as well
        name = addPrefix(name);

        if (obj instanceof Block)
        {
            return registerBlock((Block) obj, name, idHint);
        }
        else if (obj instanceof Item)
        {
            return registerItem((Item) obj, name, idHint);
        }
        else
        {
            throw new IllegalArgumentException("An invalid registry object is to be added, only instances of Block or Item are allowed.");
        }
    }

    int registerItem(Item item, String name) // from GameRegistry
    {
        int index = name.indexOf(':');
        if (name.indexOf(':') != -1) FMLLog.bigWarning("Illegal extra prefix %s for name %s, invalid registry invocation/invalid name?", name.substring(0, index), name);

        name = addPrefix(name);
        return registerItem(item, name, -1);
    }

    private int registerItem(Item item, String name, int idHint)
    {
        if (item instanceof ItemBlock) // ItemBlock, adjust id and clear the slot already occupied by the corresponding block
        {
            Block block = ((ItemBlock) item).field_150939_a;
            if (idHint != -1 && getMain().blockSubstitutions.containsKey(name))
            {
                block = getMain().blockSubstitutions.get(name);
            }
            int id = iBlockRegistry.getId(block);

            if (id == -1) // ItemBlock before its Block
            {
                if (idHint < 0 || availabilityMap.get(idHint) || idHint > MAX_BLOCK_ID) // non-suitable id, allocate one in the block id range, add would use the item id range otherwise
                {
                    id = availabilityMap.nextClearBit(MIN_BLOCK_ID); // find suitable id here, iItemRegistry would search from MIN_ITEM_ID
                    if (id > MAX_BLOCK_ID) throw new RuntimeException(String.format("Invalid id %d - maximum id range exceeded.", id));
                    FMLLog.fine("Allocated id %d for ItemBlock %s in the block id range, original id requested: %d.", id, name, idHint);
                }
                else // idHint is suitable without changes
                {
                    id = idHint;
                }
            }
            else // ItemBlock after its Block
            {
                if (FMLControlledNamespacedRegistry.DEBUG)
                    FMLLog.fine("Found matching Block %s for ItemBlock %s at id %d, original id requested: %d", block, item, id, idHint);
                freeSlot(id, item); // temporarily free the slot occupied by the Block for the item registration
            }

            idHint = id;
        }

        int itemId = iItemRegistry.add(idHint, name, item, availabilityMap);

        if (item instanceof ItemBlock) // verify
        {
            if (itemId != idHint) throw new IllegalStateException(String.format("ItemBlock at block id %d insertion failed, got id %d.", idHint, itemId));
            verifyItemBlockName((ItemBlock) item);
        }

        // block the Block Registry slot with the same id
        useSlot(itemId);
        ((RegistryDelegate.Delegate<Item>) item.delegate).setName(name);
        return itemId;
    }

    int registerBlock(Block block, String name) // from GameRegistry
    {
        int index = name.indexOf(':');
        if (name.indexOf(':') != -1) FMLLog.bigWarning("Illegal extra prefix %s for name %s, invalid registry invocation/invalid name?", name.substring(0, index), name);

        name = addPrefix(name);
        return registerBlock(block, name, -1);
    }

    private int registerBlock(Block block, String name, int idHint)
    {
        // handle ItemBlock-before-Block registrations
        ItemBlock itemBlock = null;

        for (Item item : iItemRegistry.typeSafeIterable()) // find matching ItemBlock
        {
            if (item instanceof ItemBlock && ((ItemBlock) item).field_150939_a == block)
            {
                itemBlock = (ItemBlock) item;
                break;
            }
        }

        if (itemBlock != null) // has ItemBlock, adjust id and clear the slot already occupied by the corresponding item
        {
            idHint = iItemRegistry.getId(itemBlock);
            FMLLog.fine("Found matching ItemBlock %s for Block %s at id %d", itemBlock, block, idHint);
            freeSlot(idHint, block); // temporarily free the slot occupied by the Item for the block registration
        }

        // add
        int blockId = iBlockRegistry.add(idHint, name, block, availabilityMap);

        if (itemBlock != null) // verify
        {
            if (blockId != idHint) throw new IllegalStateException(String.format("Block at itemblock id %d insertion failed, got id %d.", idHint, blockId));
            verifyItemBlockName(itemBlock);
        }

        useSlot(blockId);
        ((RegistryDelegate.Delegate<Block>) block.delegate).setName(name);
        return blockId;
    }

    /**
     * Block the specified id from being reused.
     */
    private void block(int id)
    {
        blockedIds.add(id);
        useSlot(id);
    }

    private void useSlot(int id)
    {
        availabilityMap.set(id);
    }

    /**
     * Free the specified slot.
     *
     * The slot must not be occupied by something else than the specified object within the same type.
     * The same object is permitted for handling duplicate registrations.
     *
     * @param id id to free
     * @param obj object allowed besides different types (block vs item)
     */
    private void freeSlot(int id, Object obj)
    {
        FMLControlledNamespacedRegistry<?> registry = (obj instanceof Block) ? iBlockRegistry : iItemRegistry;
        Object thing = registry.getRaw(id);

        if (thing != null && thing != obj)
        {
            throw new IllegalStateException(String.format("Can't free registry slot %d occupied by %s", id, thing));
        }

        availabilityMap.clear(id);
    }

    /**
     * Prefix the supplied name with the current mod id.
     *
     * If no mod id can be determined, minecraft will be assumed.
     * The prefix is separated with a colon.
     *
     * If there's already a prefix, it'll be prefixed again if the new prefix
     * doesn't match the old prefix, as used by vanilla calls to addObject.
     *
     * @param name name to prefix.
     * @return prefixed name.
     */
    private String addPrefix(String name)
    {
        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index);
        String prefix;
        ModContainer mc = Loader.instance().activeModContainer();

        if (mc != null)
        {
            prefix = mc.getModId();
        }
        else // no mod container, assume minecraft
        {
            prefix = "minecraft";
        }

        if (!oldPrefix.equals(prefix))
        {
            name = prefix + ":" + name;
        }

        return name;
    }

    private void verifyItemBlockName(ItemBlock item)
    {
        String blockName = iBlockRegistry.getNameForObject(item.field_150939_a);
        String itemName = iItemRegistry.getNameForObject(item);

        if (blockName != null && !blockName.equals(itemName))
        {
            FMLLog.bigWarning("Block <-> ItemBlock name mismatch, block name %s, item name %s", blockName, itemName);
        }
    }

    private void testConsistency() {
        // test if there's an entry for every set bit in availabilityMap
        for (int i = availabilityMap.nextSetBit(0); i >= 0; i = availabilityMap.nextSetBit(i+1))
        {
            if (iBlockRegistry.getRaw(i) == null && iItemRegistry.getRaw(i) == null && !blockedIds.contains(i))
            {
                throw new IllegalStateException(String.format("availabilityMap references empty entries for id %d.", i));
            }
        }

        for (int pass = 0; pass < 2; pass++)
        {
            boolean isBlock = pass == 0;
            String type = isBlock ? "block" : "item";
            FMLControlledNamespacedRegistry<?> registry = isBlock ? iBlockRegistry : iItemRegistry;
            registry.validateContent((isBlock ? MAX_BLOCK_ID : MAX_ITEM_ID), type, availabilityMap, blockedIds, iBlockRegistry);
        }

        FMLLog.fine("Registry consistency check successful");
    }

    void registerSubstitutionAlias(String nameToSubstitute, Type type, Object toReplace) throws ExistingSubstitutionException
    {
        type.getRegistry().addSubstitutionAlias(Loader.instance().activeModContainer().getModId(),nameToSubstitute, toReplace);
        type.getRegistry().activateSubstitution(nameToSubstitute);
    }
    static <T> RegistryDelegate<T> buildDelegate(T referant, Class<T> type)
    {
        return new RegistryDelegate.Delegate<T>(referant, type);
    }

    private BiMap<String, Item> itemSubstitutions = HashBiMap.create();
    private BiMap<String, Block> blockSubstitutions = HashBiMap.create();
    @SuppressWarnings("unchecked")
    <T> BiMap<String, T> getPersistentSubstitutionMap(Class<T> type)
    {
        if (type.equals(Item.class))
        {
            return (BiMap<String, T>) itemSubstitutions;
        }
        else if (type.equals(Block.class))
        {
            return (BiMap<String, T>) blockSubstitutions;
        }
        else
        {
            throw new RuntimeException("WHAT?");
        }
    }
}
