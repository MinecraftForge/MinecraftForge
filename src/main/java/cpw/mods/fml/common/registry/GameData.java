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
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.io.Files;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.registry.GameRegistry.Type;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class GameData {
    private static final GameData mainData = new GameData();

    /**
     * @deprecated use {@link getBlockRegistry()} instead.
     */
    @Deprecated
    public static final FMLControlledNamespacedRegistry<Block> blockRegistry = getBlockRegistry();
    /**
     * @deprecated use {@link getItemRegistry()} instead.
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

    public static Map<String,Integer> buildItemDataList()
    {
        Map<String,Integer> idMapping = Maps.newHashMap();
        getMain().iBlockRegistry.serializeInto(idMapping);
        getMain().iItemRegistry.serializeInto(idMapping);
        return idMapping;
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
        return (Item) getMain().iItemRegistry.func_82594_a(modId + ":" + name);
    }

    static Block findBlock(String modId, String name)
    {
        String key = modId + ":" + name;
        return getMain().iBlockRegistry.contains(key) ? getMain().iBlockRegistry.func_82594_a(key) : null;
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
        String name = getMain().iBlockRegistry.func_148750_c(block);
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
        String name = getMain().iItemRegistry.func_148750_c(item);
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
    public static void fixBrokenIds(Map<String, Integer> dataList)
    {
        BitSet availabilityMap = new BitSet(32000);

        // reserve all ids occupied by blocks
        for (Entry<String, Integer> entry : dataList.entrySet())
        {
            String itemName = entry.getKey();

            if (itemName.charAt(0) == '\u0001') // is a block
            {
                availabilityMap.set(entry.getValue());
            }
        }

        Set<String> itemsToAllocate = new HashSet<String>();

        // check all ids occupied by items
        for (Entry<String, Integer> entry : dataList.entrySet())
        {
            String itemName = entry.getKey();

            if (itemName.charAt(0) != '\u0001') // is an item
            {
                int oldId = entry.getValue();

                if (availabilityMap.get(oldId)) // id is already occupied
                {
                    String realName = itemName.substring(1);
                    String blockName = '\u0001' + realName;

                    if (!dataList.containsKey(blockName) ||
                            !(getMain().iItemRegistry.getRaw(realName) instanceof ItemBlock)) // the slot is occupied by something else and this item is no ItemBlock
                    {
                        // relocate the item later, after all correct ids have been claimed
                        itemsToAllocate.add(itemName);
                    }
                    else if (dataList.get(blockName) != oldId) // occupied, but this is an ItemBlock for a different block than whatever may use its id
                    {
                        // relocate to the matching block
                        int newId = dataList.get(blockName);
                        entry.setValue(newId);

                        FMLLog.warning("Fixed ItemBlock %s not using the id of its block, old id %d, new id %d.", realName, oldId, newId);
                    }
                }
                else // unused id, occupy
                {
                    availabilityMap.set(oldId);
                }
            }
        }

        for (String itemName : itemsToAllocate)
        {
            int oldId = dataList.get(itemName);
            int newId = availabilityMap.nextClearBit(4096);

            dataList.put(itemName, newId);

            FMLLog.warning("Fixed Item %s conflicting with another block/item, old id %d, new id %d.", itemName.substring(1), oldId, newId);
        }
    }

    public static List<String> injectWorldIDMap(Map<String, Integer> dataList, boolean injectFrozenData, boolean isLocalWorld)
    {
        return injectWorldIDMap(dataList, new int[0], new HashMap<String, String>(), new HashMap<String, String>(), injectFrozenData, isLocalWorld);
    }

    public static List<String> injectWorldIDMap(Map<String, Integer> dataList, int[] blockedIds, Map<String, String> blockAliases, Map<String, String> itemAliases, boolean injectFrozenData, boolean isLocalWorld)
    {
        FMLLog.info("Injecting existing block and item data into this %s instance", FMLCommonHandler.instance().getEffectiveSide().isServer() ? "server" : "client");
        Map<String, Integer[]> remaps = Maps.newHashMap();
        LinkedHashMap<String, Integer> missingMappings = new LinkedHashMap<String, Integer>();
        getMain().testConsistency();
        getMain().iBlockRegistry.dump();
        getMain().iItemRegistry.dump();

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
                    FMLLog.info("Found %s id mismatch %s : %d (was %d)", isBlock ? "block" : "item", itemName, currId, newId);
                    remaps.put(itemName, new Integer[] { currId, newId });
                }

                // register
                FMLControlledNamespacedRegistry<?> srcRegistry = isBlock ? getMain().iBlockRegistry : getMain().iItemRegistry;
                currId = newData.register(srcRegistry.getRaw(itemName), itemName, newId);

                if (currId != newId)
                {
                    throw new IllegalStateException(String.format("Can't map %s %s to id %d, already occupied by %s",
                            isBlock ? "block" : "item",
                                    itemName,
                                    newId,
                                    isBlock ? newData.iBlockRegistry.get(newId) : newData.iItemRegistry.get(newId)));
                }
            }
        }

        List<String> missedMappings = Loader.instance().fireMissingMappingEvent(missingMappings, isLocalWorld, newData, remaps);
        if (!missedMappings.isEmpty()) return missedMappings;

        if (injectFrozenData) // add blocks + items missing from the map
        {
            FMLLog.info("Injecting new block and item data into this server instance");
            Map<String, Integer> missingBlocks = frozen.iBlockRegistry.getEntriesNotIn(newData.iBlockRegistry);
            Map<String, Integer> missingItems = frozen.iItemRegistry.getEntriesNotIn(newData.iItemRegistry);

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
                        newId = newData.registerBlock(frozen.iBlockRegistry.get(itemName), itemName, null, currId);
                    }
                    else
                    {
                        newId = newData.registerItem(frozen.iItemRegistry.get(itemName), itemName, null, currId);
                    }

                    FMLLog.info("Injected new block/item %s : %d (was %d)", itemName, newId, currId);

                    if (newId != currId) // a new id was assigned
                    {
                        remaps.put(itemName, new Integer[] { entry.getValue(), newId });
                    }
                }
            }
        }

        newData.testConsistency();
        getMain().set(newData);

        getMain().iBlockRegistry.dump();
        getMain().iItemRegistry.dump();
        Loader.instance().fireRemapEvent(remaps);
        return ImmutableList.of();
    }

    public static List<String> processIdRematches(Iterable<MissingMapping> missedMappings, boolean isLocalWorld, GameData gameData, Map<String, Integer[]> remaps)
    {
        List<String> failed = Lists.newArrayList();
        List<String> ignored = Lists.newArrayList();
        List<String> warned = Lists.newArrayList();

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
                    newName = getMain().iBlockRegistry.func_148750_c(remap.getTarget());
                    FMLLog.fine("The Block %s is being remapped to %s.", remap.name, newName);

                    newId = gameData.registerBlock((Block) remap.getTarget(), newName, null, remap.id);
                    gameData.iBlockRegistry.addAlias(remap.name, newName);
                }
                else
                {
                    currId = getMain().iItemRegistry.getId((Item) remap.getTarget());
                    newName = getMain().iItemRegistry.func_148750_c(remap.getTarget());
                    FMLLog.fine("The Item %s is being remapped to %s.", remap.name, newName);

                    newId = gameData.registerItem((Item) remap.getTarget(), newName, null, remap.id);
                    gameData.iItemRegistry.addAlias(remap.name, newName);
                }

                if (newId != remap.id) throw new IllegalStateException();

                if (currId != newId)
                {
                    FMLLog.info("Found %s id mismatch %s : %d (was %d)", remap.type == Type.BLOCK ? "block" : "item", newName, currId, newId);
                    remaps.put(newName, new Integer[] { currId, newId });
                }
            }
            else
            {
                // block item missing, warn as requested and block the id
                if (action == FMLMissingMappingsEvent.Action.DEFAULT)
                {
                    action = FMLCommonHandler.instance().getDefaultMissingAction();
                }

                if (action == FMLMissingMappingsEvent.Action.IGNORE)
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
                else
                {
                    throw new RuntimeException(String.format("Invalid default missing id action specified: %s", action.name()));
                }

                gameData.block(remap.id); // prevent the id from being reused later
            }
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
        iBlockRegistry = new FMLControlledNamespacedRegistry<Block>("air", 4095, 0, Block.class,'\u0001');
        iItemRegistry = new FMLControlledNamespacedRegistry<Item>(null, 32000, 4096, Item.class,'\u0002');
        availabilityMap = new BitSet(32000);
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
        blockedIds.addAll(data.blockedIds);
    }

    int register(Object obj, String name, int idHint)
    {
        if (obj instanceof Block)
        {
            return registerBlock((Block) obj, name, null, idHint);
        }
        else if (obj instanceof Item)
        {
            return registerItem((Item) obj, name, null, idHint);
        }
        else
        {
            throw new IllegalArgumentException("An invalid registry object is to be added, only instances of Block or Item are allowed.");
        }
    }

    int registerItem(Item item, String name, String modId)
    {
        return registerItem(item, name, modId, 0);
    }

    int registerItem(Item item, String name, String modId, int idHint)
    {
        if (modId != null)
        {
            ModContainer mc = Loader.instance().activeModContainer();
            customOwners.put(new UniqueIdentifier(modId, name), mc);
        }
        if (item instanceof ItemBlock)
        {
            // ItemBlock, clear the item slot already occupied by the corresponding block
            idHint = iBlockRegistry.getId(((ItemBlock) item).field_150939_a);

            if (idHint == -1)
            {
                throw new RuntimeException("Cannot register an itemblock before its block");
            }

            if (iItemRegistry.get(idHint) != null)
            {
                throw new IllegalStateException(String.format("The Item Registry slot %d is already used by %s", idHint, iItemRegistry.get(idHint)));
            }

            if (!freeSlot(idHint)) // temporarily free the slot occupied by the Block for the ItemBlock registration
            {
                throw new IllegalStateException(String.format("The Registry slot %d is supposed to be blocked by the ItemBlock's Block's blockId at this point.", idHint));
            }
        }

        int itemId = iItemRegistry.add(idHint, name, item, availabilityMap);

        if (item instanceof ItemBlock)
        {
            if (itemId != idHint) // just in case of bugs...
            {
                throw new IllegalStateException("ItemBlock insertion failed.");
            }
        }

        // normal item, block the Block Registry slot with the same id
        if (useSlot(itemId))
        {
            throw new IllegalStateException(String.format("Registry slot %d is supposed to be empty when adding a non-ItemBlock with the same id.", itemId));
        }

        return itemId;
    }

    int registerBlock(Block block, String name, String modId)
    {
        return registerBlock(block, name, modId, 0);
    }

    int registerBlock(Block block, String name, String modId, int idHint)
    {
        if (modId != null)
        {
            ModContainer mc = Loader.instance().activeModContainer();
            customOwners.put(new UniqueIdentifier(modId, name), mc);
        }
        int blockId = iBlockRegistry.add(idHint, name, block, availabilityMap);

        if (useSlot(blockId))
        {
            throw new IllegalStateException(String.format("Registry slot %d is supposed to be empty when adding a Block with the same id.", blockId));
        }

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

    private boolean useSlot(int id)
    {
        boolean oldValue = availabilityMap.get(id);
        availabilityMap.set(id);
        return oldValue;
    }

    private boolean freeSlot(int id)
    {
        boolean oldValue = availabilityMap.get(id);
        availabilityMap.clear(id);
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    private void testConsistency() {
        // test if there's an entry for every set bit in availabilityMap
        for (int i = availabilityMap.nextSetBit(0); i >= 0; i = availabilityMap.nextSetBit(i+1))
        {
            if (iBlockRegistry.getRaw(i) == null && iItemRegistry.getRaw(i) == null && !blockedIds.contains(i))
            {
                throw new IllegalStateException(String.format("availabilityMap references empty entries for id %d.", i));
            }
        }

        // test if there's a bit in availabilityMap set for every entry in the block registry, make sure it's not a blocked id
        for (Block block : (Iterable<Block>) iBlockRegistry)
        {
            int id = iBlockRegistry.getId(block);

            if (!availabilityMap.get(id))
            {
                throw new IllegalStateException(String.format("Registry entry for block %s, id %d, marked as empty.", block, id));
            }
            if (blockedIds.contains(id))
            {
                throw new IllegalStateException(String.format("Registry entry for block %s, id %d, marked as dangling.", block, id));
            }
        }

        // test if there's a bit in availabilityMap set for every entry in the item registry, make sure it's not a blocked id,
        // check if ItemBlocks have blocks with matching ids in the block registry
        for (Item item : (Iterable<Item>) iItemRegistry)
        {
            int id = iItemRegistry.getId(item);

            if (!availabilityMap.get(id))
            {
                throw new IllegalStateException(String.format("Registry entry for item %s, id %d, marked as empty.", item, id));
            }
            if (blockedIds.contains(id))
            {
                throw new IllegalStateException(String.format("Registry entry for item %s, id %d, marked as dangling.", item, id));
            }

            if (item instanceof ItemBlock)
            {
                Block block = ((ItemBlock) item).field_150939_a;

                if (iBlockRegistry.getId(block) != id)
                {
                    throw new IllegalStateException(String.format("Registry entry for ItemBlock %s, id %d, is missing or uses the non-matching id %d.", item, id, iBlockRegistry.getId(block)));
                }
            }
        }

        FMLLog.fine("Registry consistency check successful");
    }
}
