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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Throwables;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.Tables;
import com.google.common.io.Files;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class GameData {
    private static Table<String, String, ItemStack> customItemStacks = HashBasedTable.create();
    private static boolean validated;
    public static final FMLControlledNamespacedRegistry<Block> blockRegistry = new FMLControlledNamespacedRegistry<Block>("air", 4095, 0, Block.class);
    public static final FMLControlledNamespacedRegistry<Item> itemRegistry = new FMLControlledNamespacedRegistry<Item>(null, 32000, 4096, Item.class);

    public static void newItemAdded(Item item)
    {
        ModContainer mc = Loader.instance().activeModContainer();
        if (mc == null)
        {
            mc = Loader.instance().getMinecraftModContainer();
            if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION) || validated)
            {
                FMLLog.severe("It appears something has tried to allocate an Item or Block outside of the preinitialization phase for mods. This will NOT work in 1.7 and beyond!");
            }
        }
        String itemType = item.getClass().getName();
        ItemData itemData = new ItemData(item, mc);
        if (idMap.containsKey(item.field_77779_bT))
        {
            ItemData id = idMap.get(item.field_77779_bT);
            FMLLog.log("fml.ItemTracker", Level.INFO, "The mod %s is overwriting existing item at %d (%s from %s) with %s", mc.getModId(), id.getItemId(), id.getItemType(), id.getModId(), itemType);
        }
        idMap.put(item.field_77779_bT, itemData);
        if (!"Minecraft".equals(mc.getModId()))
        {
            FMLLog.log("fml.ItemTracker",Level.FINE, "Adding item %s(%d) owned by %s", item.getClass().getName(), item.field_77779_bT, mc.getModId());
        }
    }

    public static void validateWorldSave(Set<ItemData> worldSaveItems)
    {
        isSaveValid = true;
        shouldContinue = true;
        // allow ourselves to continue if there's no saved data
        if (worldSaveItems == null)
        {
            serverValidationLatch.countDown();
            try
            {
                clientValidationLatch.await();
            }
            catch (InterruptedException e)
            {
            }
            return;
        }

        Function<? super ItemData, Integer> idMapFunction = new Function<ItemData, Integer>() {
            public Integer apply(ItemData input) {
                return input.getItemId();
            };
        };

        Map<Integer,ItemData> worldMap = Maps.uniqueIndex(worldSaveItems,idMapFunction);
        difference = Maps.difference(worldMap, idMap);
        FMLLog.log("fml.ItemTracker", Level.FINE, "The difference set is %s", difference);
        if (!difference.entriesDiffering().isEmpty() || !difference.entriesOnlyOnLeft().isEmpty())
        {
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "FML has detected item discrepancies");
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "Missing items : %s", difference.entriesOnlyOnLeft());
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "Mismatched items : %s", difference.entriesDiffering());
            boolean foundNonIgnored = false;
            for (ItemData diff : difference.entriesOnlyOnLeft().values())
            {
                if (!isModIgnoredForIdValidation(diff.getModId()))
                {
                    foundNonIgnored = true;
                }
            }
            for (ValueDifference<ItemData> diff : difference.entriesDiffering().values())
            {
                if (! ( isModIgnoredForIdValidation(diff.leftValue().getModId()) || isModIgnoredForIdValidation(diff.rightValue().getModId()) ) )
                {
                    foundNonIgnored = true;
                }
            }
            if (!foundNonIgnored)
            {
                FMLLog.log("fml.ItemTracker", Level.SEVERE, "FML is ignoring these ID discrepancies because of configuration. YOUR GAME WILL NOW PROBABLY CRASH. HOPEFULLY YOU WON'T HAVE CORRUPTED YOUR WORLD. BLAME %s", ignoredMods.keySet());
            }
            isSaveValid = !foundNonIgnored;
            serverValidationLatch.countDown();
        }
        else
        {
            isSaveValid = true;
            serverValidationLatch.countDown();
        }
        try
        {
            clientValidationLatch.await();
            if (!shouldContinue)
            {
                throw new RuntimeException("This server instance is going to stop abnormally because of a fatal ID mismatch");
            }
        }
        catch (InterruptedException e)
        {
        }
    }

    public static Map<String,Integer> buildItemDataList()
    {
        Map<String,Integer> idMapping = Maps.newHashMap();
        blockRegistry.serializeInto(idMapping);
        itemRegistry.serializeInto(idMapping);
        return idMapping;
    }

    public static Set<ItemData> buildWorldItemData(NBTTagList modList)
    {
        Set<ItemData> worldSaveItems = Sets.newHashSet();
        for (int i = 0; i < modList.func_74745_c(); i++)
        {
            NBTTagCompound mod = modList.func_150305_b(i);
            ItemData dat = new ItemData(mod);
            worldSaveItems.add(dat);
        }
        return worldSaveItems;
    }

    static Item findItem(String modId, String name)
    {
        return (Item) itemRegistry.func_82594_a(modId + ":" + name);
    }

    static Block findBlock(String modId, String name)
    {
        return (Block) blockRegistry.func_82594_a(modId+":"+name);
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
                FMLLog.log(Level.SEVERE, e, "Failed to write registry data to %s", f.getAbsolutePath());
            }
        }
    }

    static UniqueIdentifier getUniqueName(Block block)
    {
        if (block == null) return null;
        String name = blockRegistry.func_148750_c(block);
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
        String name = itemRegistry.func_148750_c(item);
        UniqueIdentifier ui = new UniqueIdentifier(name);
        if (customItemStacks.contains(ui.modId, ui.name))
        {
            return null;
        }

        return ui;
    }

    public static void validateRegistry()
    {
/*        for (int i = 0; i < Item.field_77698_e.length; i++)
        {
            if (Item.field_77698_e[i] != null)
            {
                ItemData itemData = idMap.get(i);
                if (itemData == null)
                {
                    FMLLog.severe("Found completely unknown item of class %s with ID %d, this will NOT work for a 1.7 upgrade", Item.field_77698_e[i].getClass().getName(), i);
                }
                else if (!itemData.isOveridden() && !"Minecraft".equals(itemData.getModId()))
                {
                    FMLLog.severe("Found anonymous item of class %s with ID %d owned by mod %s, this item will NOT survive a 1.7 upgrade!", Item.field_77698_e[i].getClass().getName(), i, itemData.getModId());
                }
            }
        }
*/        validated = true;
    }
    private static Map<UniqueIdentifier, ModContainer> customOwners = Maps.newHashMap();
    static Block registerBlockAndItem(Item item, Block block, String name, String modId)
    {

    }
    static Item registerItem(Item item, String name, String modId)
    {
        ModContainer mc = Loader.instance().activeModContainer();
        if (modId != null)
        {
            customOwners.put(new UniqueIdentifier(modId, name), mc);
        }
        itemRegistry.add(0, name, item);
        int itemId = itemRegistry.getId(item);
        blockRegistry.clearItem(itemId);
        return item;
    }
    static Block registerBlock(Block block, String name, String modId)
    {
        ModContainer mc = Loader.instance().activeModContainer();
        if (modId != null)
        {
            customOwners.put(new UniqueIdentifier(modId, name), mc);
        }
        blockRegistry.func_148756_a(0, name, block);
        return block;
    }
    public static ModContainer findModOwner(String string)
    {
        UniqueIdentifier ui = new UniqueIdentifier(string);
        if (customOwners.containsKey(ui))
        {
            return customOwners.get(ui);
        }
        return Loader.instance().getIndexedModList().get(ui.modId);
    }
}
