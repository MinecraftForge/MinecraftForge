
package net.minecraftforge.liquids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class LiquidContainerRegistry {

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
    static {
        registerLiquid(new LiquidContainerData(new LiquidStack(Block.waterStill, LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.bucketWater), new ItemStack(Item.bucketEmpty)));
        registerLiquid(new LiquidContainerData(new LiquidStack(Block.lavaStill, LiquidContainerRegistry.BUCKET_VOLUME),  new ItemStack(Item.bucketLava), new ItemStack(Item.bucketEmpty)));
        registerLiquid(new LiquidContainerData(new LiquidStack(Block.waterStill, LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.potion), new ItemStack(Item.glassBottle)));
        // registerLiquid(new LiquidContainerData(new LiquidStack(Item.bucketMilk, LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(Item.bucketMilk), new ItemStack(Item.bucketEmpty)));
    }

    /**
     * To register a container with a non-bucket size, the LiquidContainerData entry simply needs to use a size other than LiquidManager.BUCKET_VOLUME
     */
    public static void registerLiquid(LiquidContainerData data) {

        mapFilledItemFromLiquid.put(Arrays.asList(data.container.itemID, data.container.getItemDamage(), data.stillLiquid.itemID, data.stillLiquid.itemMeta), data);
        mapLiquidFromFilledItem.put(Arrays.asList(data.filled.itemID, data.filled.getItemDamage()), data);
        setContainerValidation.add(Arrays.asList(data.container.itemID, data.container.getItemDamage()));
        setLiquidValidation.add(Arrays.asList(data.stillLiquid.itemID, data.stillLiquid.itemMeta));

        liquids.add(data);
    }

    public static LiquidStack getLiquidForFilledItem(ItemStack filledContainer) {

        if (filledContainer == null) {
            return null;
        }
        LiquidContainerData ret = mapLiquidFromFilledItem.get(Arrays.asList(filledContainer.itemID, filledContainer.getItemDamage()));
        if (ret != null) {
            return ret.stillLiquid.copy();
        }
        return null;
    }

    public static ItemStack fillLiquidContainer(LiquidStack liquid, ItemStack emptyContainer) {

        if (emptyContainer == null || liquid == null) {
            return null;
        }
        LiquidContainerData ret = mapFilledItemFromLiquid.get(Arrays.asList(emptyContainer.itemID, emptyContainer.getItemDamage(), liquid.itemID, liquid.itemMeta));
        if (ret != null) {
            if (liquid.amount >= ret.stillLiquid.amount) {
                return ret.filled.copy();
            }
        }
        return null;
    }

    public static boolean containsLiquid(ItemStack filledContainer, LiquidStack liquid) {

        if (filledContainer == null || liquid == null) {
            return false;
        }
        LiquidContainerData ret = mapLiquidFromFilledItem.get(Arrays.asList(filledContainer.itemID, filledContainer.getItemDamage()));
        if (ret != null) {
            return ret.stillLiquid.isLiquidEqual(liquid);
        }
        return false;
    }

    public static boolean isBucket(ItemStack container) {

        if (container == null) {
            return false;
        }

        if (container.isItemEqual(EMPTY_BUCKET)) {
            return true;
        }

        LiquidContainerData ret = mapLiquidFromFilledItem.get(Arrays.asList(container.itemID, container.getItemDamage()));
        if (ret != null) {
            return ret.container.isItemEqual(EMPTY_BUCKET);
        }
        return false;
    }

    public static boolean isContainer(ItemStack container) {

        return isEmptyContainer(container) || isFilledContainer(container);
    }

    public static boolean isEmptyContainer(ItemStack emptyContainer) {

        if (emptyContainer == null) {
            return false;
        }
        return setContainerValidation.contains(Arrays.asList(emptyContainer.itemID, emptyContainer.getItemDamage()));
    }

    public static boolean isFilledContainer(ItemStack filledContainer) {

        if (filledContainer == null) {
            return false;
        }
        return getLiquidForFilledItem(filledContainer) != null;
    }

    public static boolean isLiquid(ItemStack item) {

        if (item == null) {
            return false;
        }
        return setLiquidValidation.contains(Arrays.asList(item.itemID, item.getItemDamage()));
    }

    public static LiquidContainerData[] getRegisteredLiquidContainerData() {

        return liquids.toArray(new LiquidContainerData[0]);
    }
}
