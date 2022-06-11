package net.minecraftforge.common.extensions;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public interface IForgePotion {
    private Potion self() {
        return (Potion)this;
    }

    /**
     * Determines whether the potion-related item [bottle, tipped arrow, or a modded item] should appear in a creative tab.
     * 
     * @param item The item being added to a creative tab
     * @param tab The tab items are being added to
     * @param isDefaultTab The tab is the default tab for this item [e.g. Brewing for bottles, Combat for tipped arrows]
     * @return true to put the item variant in the tab.
     */
    default boolean allowedInCreativeTab(Item item, CreativeModeTab tab, boolean isDefaultTab) {
        return isDefaultTab;
    }

    /**
     * Determines whether the potion bottle item should be enchanted.
     *
     * Not called for tipped arrows or if the item is already enchanted.
     *
     * @param stack The potion bottle
     * @return true for enchanted appearance
     */
    default boolean isFoil(ItemStack stack) {
        return !PotionUtils.getMobEffects(stack).isEmpty();
    }
}
