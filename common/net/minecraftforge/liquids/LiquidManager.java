
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

public class LiquidManager {

    public static final int BUCKET_VOLUME = 1000;

    private static Map<List, ItemStack> mapItemFromLiquid = new HashMap();
    private static Map<List, LiquidStack> mapLiquidFromItem = new HashMap();
    private static Set<List> setLiquidValidation = new HashSet();
    private static ArrayList<LiquidData> liquids = new ArrayList();

    static {
        registerLiquid(new LiquidData(new LiquidStack(Block.waterStill, LiquidManager.BUCKET_VOLUME), new LiquidStack(Block.waterMoving, LiquidManager.BUCKET_VOLUME),
                new ItemStack(Item.bucketWater), new ItemStack(Item.bucketEmpty)));
        registerLiquid(new LiquidData(new LiquidStack(Block.lavaStill, LiquidManager.BUCKET_VOLUME), new LiquidStack(Block.lavaMoving, LiquidManager.BUCKET_VOLUME), new ItemStack(
                Item.bucketLava), new ItemStack(Item.bucketEmpty)));
        registerLiquid(new LiquidData(new LiquidStack(Block.waterStill, LiquidManager.BUCKET_VOLUME), new LiquidStack(Block.waterMoving, LiquidManager.BUCKET_VOLUME),
                new ItemStack(Item.potion), new ItemStack(Item.glassBottle)));
    }

    public static void registerLiquid(LiquidData data) {

        mapItemFromLiquid.put(Arrays.asList(data.container.itemID, data.container.getItemDamage(), data.stillLiquid.itemID, data.stillLiquid.itemMeta), data.filled);
        mapLiquidFromItem.put(Arrays.asList(data.filled.itemID, data.filled.getItemDamage()), data.stillLiquid);
        setLiquidValidation.add(Arrays.asList(data.stillLiquid.itemID, data.stillLiquid.itemMeta));

        liquids.add(data);
    }

    public static LiquidStack getLiquidForFilledItem(ItemStack filledItem) {

        if (filledItem == null) {
            return null;
        }
        return mapLiquidFromItem.get(Arrays.asList(filledItem.itemID, filledItem.getItemDamage()));
    }

    public static ItemStack fillLiquidContainer(int liquidId, int quantity, ItemStack emptyContainer) {

        return fillLiquidContainer(new LiquidStack(liquidId, quantity, 0), emptyContainer);
    }

    public static ItemStack fillLiquidContainer(LiquidStack liquid, ItemStack emptyContainer) {

        if (emptyContainer == null || liquid == null) {
            return null;
        }
        return mapItemFromLiquid.get(Arrays.asList(emptyContainer.itemID, emptyContainer.getItemDamage(), liquid.itemID, liquid.itemMeta));
    }

    public static boolean isLiquid(ItemStack block) {

        return setLiquidValidation.contains(Arrays.asList(block.itemID, block.getItemDamage()));
    }

    public static ArrayList<LiquidData> getRegisteredLiquids() {

        return liquids;
    }
}
