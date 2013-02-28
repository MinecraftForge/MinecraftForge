
package net.minecraftforge.liquids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LiquidContainerRegistry
{
    public static final int BUCKET_VOLUME = 1000;
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Item.bucketEmpty);

    private static Map<List, LiquidContainerData> mapFilledItemFromLiquid = new HashMap();
    private static Map<List, LiquidContainerData> mapLiquidFromFilledItem = new HashMap();
    private static Set<List> setContainerValidation = new HashSet();
    private static Set<List> setLiquidValidation = new HashSet();
    private static ArrayList<LiquidContainerData> liquids = new ArrayList();

    /**
     * Default registrations
     */
    static
    {
        registerLiquid(new LiquidContainerData(new LiquidStack(Block.waterStill, LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.bucketWater), new ItemStack(Item.bucketEmpty)));
        registerLiquid(new LiquidContainerData(new LiquidStack(Block.lavaStill, LiquidContainerRegistry.BUCKET_VOLUME),  new ItemStack(Item.bucketLava), new ItemStack(Item.bucketEmpty)));
        registerLiquid(new LiquidContainerData(new LiquidStack(Block.waterStill, LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.potion), new ItemStack(Item.glassBottle)));
        // registerLiquid(new LiquidContainerData(new LiquidStack(Item.bucketMilk, LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.bucketMilk), new ItemStack(Item.bucketEmpty)));
    }

    /**
     * To register a container with a non-bucket size, the LiquidContainerData entry simply needs to use a size other than LiquidManager.BUCKET_VOLUME
     */
    public static void registerLiquid(LiquidContainerData data)
    {
        mapFilledItemFromLiquid.put(Arrays.asList(data.container.itemID, data.container.getItemDamage(), data.stillLiquid.itemID, data.stillLiquid.itemMeta), data);
        mapLiquidFromFilledItem.put(Arrays.asList(data.filled.itemID, data.filled.getItemDamage()), data);
        setContainerValidation.add(Arrays.asList(data.container.itemID, data.container.getItemDamage()));
        setLiquidValidation.add(Arrays.asList(data.stillLiquid.itemID, data.stillLiquid.itemMeta));

        liquids.add(data);
    }

    public static LiquidStack getLiquidForFilledItem(ItemStack filledContainer)
    {
        if (filledContainer == null)
        {
            return null;
        }
        if (filledContainer.getItem().isLiquidContainer(filledContainer)) {
            return filledContainer.getItem().getLiquid(filledContainer);
        }
        LiquidContainerData ret = mapLiquidFromFilledItem.get(Arrays.asList(filledContainer.itemID, filledContainer.getItemDamage()));
        return ret == null ? null : ret.stillLiquid.copy();
    }

    public static ItemStack fillLiquidContainer(LiquidStack liquid, ItemStack emptyContainer)
    {
        if (emptyContainer == null || liquid == null)
        {
            return null;
        }
        if (emptyContainer.getItem().isLiquidContainer(emptyContainer)) {
            Item containerItem = emptyContainer.getItem();
            if (containerItem.getLiquid(emptyContainer) != null) return null;
            if (containerItem.fill(emptyContainer, liquid, false) < containerItem.getLiquidCapacity(emptyContainer)) return null;
            containerItem.fill(emptyContainer, liquid, true);
            return emptyContainer;
        }
        LiquidContainerData ret = mapFilledItemFromLiquid.get(Arrays.asList(emptyContainer.itemID, emptyContainer.getItemDamage(), liquid.itemID, liquid.itemMeta));

        if (ret != null && liquid.amount >= ret.stillLiquid.amount)
        {
            return ret.filled.copy();
        }

        return null;
    }

    public static boolean containsLiquid(ItemStack filledContainer, LiquidStack liquid)
    {
        if (filledContainer == null || liquid == null)
        {
            return false;
        }
        if (filledContainer.getItem().isLiquidContainer(filledContainer)) {
            Item containerItem = filledContainer.getItem();
            LiquidStack containedLiquid = containerItem.getLiquid(filledContainer);
            if (containedLiquid == null) return false;
            return containedLiquid.isLiquidEqual(liquid);
        }
        LiquidContainerData ret = mapLiquidFromFilledItem.get(Arrays.asList(filledContainer.itemID, filledContainer.getItemDamage()));

        return ret != null && ret.stillLiquid.isLiquidEqual(liquid);
    }

    public static boolean isBucket(ItemStack container)
    {
        if (container == null)
        {
            return false;
        }

        if (container.isItemEqual(EMPTY_BUCKET))
        {
            return true;
        }

        LiquidContainerData ret = mapLiquidFromFilledItem.get(Arrays.asList(container.itemID, container.getItemDamage()));
        return ret != null && ret.container.isItemEqual(EMPTY_BUCKET);
    }

    public static boolean isContainer(ItemStack container)
    {
        return isEmptyContainer(container) || isFilledContainer(container) || container.getItem().isLiquidContainer(container);
    }

    public static boolean isEmptyContainer(ItemStack emptyContainer)
    {
        if (emptyContainer == null) {
            return false;
        }
        if (emptyContainer.getItem().isLiquidContainer(emptyContainer)) {
            Item containerItem = emptyContainer.getItem();
            return containerItem.getLiquid(emptyContainer) == null;
        }
        return setContainerValidation.contains(Arrays.asList(emptyContainer.itemID, emptyContainer.getItemDamage()));
    }

    public static boolean isFilledContainer(ItemStack filledContainer)
    {
        if (filledContainer == null) {
            return false;
        }
        if (filledContainer.getItem().isLiquidContainer(filledContainer)) {
            Item containerItem = filledContainer.getItem();
            return containerItem.getLiquid(filledContainer) != null;
        }
        return getLiquidForFilledItem(filledContainer) != null;
    }

    public static boolean isLiquid(ItemStack item)
    {
        return item != null && setLiquidValidation.contains(Arrays.asList(item.itemID, item.getItemDamage()));
    }

    public static LiquidContainerData[] getRegisteredLiquidContainerData()
    {
        return liquids.toArray(new LiquidContainerData[liquids.size()]);
    }
}
