package net.minecraftforge.itemdict;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Joiner;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

/**
 * This is like the ore dictionary, but only one item will be created for each
 * entry.
 * 
 * To use the item dictionary, call ItemDictionary.register during pre-init if
 * you want to add a possible item or block.
 * 
 * Then during init, call ItemDictionary.getItem/getBlock to find the item or
 * block that was actually used.
 */
public class ItemDictionary {
    public static interface Factory {
        public void create(int id);
    }

    private static class ItemDictItem {
        Map<String, Factory> alternatives = new TreeMap<String, Factory>();
        Object object;
    }

    private static Map<String, ItemDictItem> items = new TreeMap<String, ItemDictItem>();
    private static Map<String, ItemDictItem> blocks = new TreeMap<String, ItemDictItem>();

    /**
     * Returns an item from the item dictionary.
     * 
     * @param itemName
     *            The item name.
     * @return The item, or null if no such item was registered.
     */
    public static Item getItem(String itemName)
    {
        if (!finalized) throw new IllegalStateException("Not finalized yet");
        ItemDictItem dictItem = items.get(itemName);
        return dictItem == null ? null : (Item) dictItem.object;
    }

    /**
     * Returns a block from the item dictionary.
     * 
     * @param itemName
     *            The item name.
     * @return The block, or null if no such block was registered.
     */
    public static Block getBlock(String itemName)
    {
        if (!finalized) throw new IllegalStateException("Not finalized yet");
        ItemDictItem dictItem = blocks.get(itemName);
        return dictItem == null ? null : (Block) dictItem.object;
    }

    /**
     * Registers an item/block alternative.
     * 
     * @param mod
     *            The mod making the registration.
     * @param itemName
     *            The name of the item.
     * @param isBlock
     *            True to register a block, false for an item.
     * @param factory
     *            The factory object used to actually create the block or item,
     *            if this alternative is chosen.
     */
    public static void register(Object mod, String itemName, boolean isBlock, Factory factory)
    {
        register(mod, null, itemName, isBlock, factory);
    }

    /**
     * Registers an item/block alternative.
     * 
     * @param mod
     *            The mod making the registration.
     * @param suffix
     *            A suffix which is used in the alternative name, to allow a
     *            single mod to provide multiple alternatives.
     * @param itemName
     *            The name of the item.
     * @param isBlock
     *            True to register a block, false for an item.
     * @param factory
     *            The factory object used to actually create the block or item,
     *            if this alternative is chosen.
     */
    public static void register(Object mod, String suffix, String itemName, boolean isBlock, Factory factory)
    {
        ModContainer modContainer = Loader.instance().getModObjectList().inverse().get(mod);
        if (modContainer == null) throw new IllegalArgumentException("Not a valid mod object");

        String modID = modContainer.getModId();
        String optionName = (suffix == null ? modID : modID + "-" + suffix);

        Map<String, ItemDictItem> itemMap = (isBlock ? blocks : items);

        ItemDictItem entry = itemMap.get(itemName);
        if (entry == null)
        {
            entry = new ItemDictItem();
            itemMap.put(itemName, entry);
        }

        if (entry.alternatives.containsKey(optionName)) throw new IllegalStateException("Option " + optionName + " already exists for item " + itemName);

        entry.alternatives.put(optionName, factory);
    }

    private static boolean finalized = false;

    public static void createItems()
    {
        if (finalized) throw new IllegalStateException("Already finalized");

        File configFile = new File(Loader.instance().getConfigDir(), "forge-item-dictionary.cfg");
        Configuration config = new Configuration(configFile);

        for (Map.Entry<String, ItemDictItem> entry : items.entrySet())
            createItem(entry.getKey(), entry.getValue(), false, config);
        for (Map.Entry<String, ItemDictItem> entry : blocks.entrySet())
            createItem(entry.getKey(), entry.getValue(), true, config);

        if (config.hasChanged()) config.save();

        finalized = true;
    }

    private static void createItem(String itemName, ItemDictItem item, boolean isBlock, Configuration config)
    {
        String comment = "Available choices: " + Joiner.on(", ").join(item.alternatives.keySet());

        int id;
        if (isBlock)
            id = config.getBlock(itemName + ".id", 3000).getInt();
        else
            id = config.getItem(Configuration.CATEGORY_ITEM, itemName + ".id", 20000, null, false).getInt();

        String defaultChoice = item.alternatives.keySet().iterator().next();
        Property choiceProperty = config.get(isBlock ? Configuration.CATEGORY_BLOCK : Configuration.CATEGORY_ITEM, itemName + ".alt", defaultChoice, comment);
        String choice = choiceProperty.getString();

        if (!item.alternatives.containsKey(choice))
        {
            choice = chooseRandom(item.alternatives.keySet());
            choiceProperty.set(choice);
        }

        Factory factory = item.alternatives.get(choice);

        factory.create(id);

        if (isBlock)
            item.object = Block.blocksList[id];
        else
            item.object = Item.itemsList[id];

        if (item.object == null)
            throw new RuntimeException("THIS IS A PROBLEM WITH THE MOD '" + choice + "'! " + (isBlock ? "Block" : "Item") + " '" + itemName
                    + "' was not correctly created at ID " + id + ". Factory instance: " + factory);
    }

    private static String chooseRandom(Collection<String> strings)
    {
        List<String> list = new ArrayList<String>(strings);
        return list.get(new Random().nextInt(list.size()));
    }
}
