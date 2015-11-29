package net.minecraftforge.common.brewing;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;

/**
 * Used in BrewingRecipeRegistry to maintain the vanilla behaviour.
 *
 * Most of the code was simply adapted from net.minecraft.tileentity.TileEntityBrewingStand
 */
public class VanillaBrewingRecipe implements IBrewingRecipe {

    /**
     * Code adapted from TileEntityBrewingStand.isItemValidForSlot(int index, ItemStack stack)
     */
    @Override
    public boolean isInput(ItemStack stack)
    {
        return stack.getItem() instanceof ItemPotion || stack.getItem() == Items.glass_bottle;
    }

    /**
     * Code adapted from TileEntityBrewingStand.isItemValidForSlot(int index, ItemStack stack)
     */
    @Override
    public boolean isIngredient(ItemStack stack)
    {
        return stack.getItem().isPotionIngredient(stack);
    }

    /**
     * Code copied from TileEntityBrewingStand.brewPotions()
     * It brews the potion by doing the bit-shifting magic and then checking if the new PotionEffect list is different to the old one,
     * or if the new potion is a splash potion when the old one wasn't.
     */
    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        if (ingredient != null && input != null && input.getItem() instanceof ItemPotion && isIngredient(ingredient))
        {
            int inputMeta = input.getMetadata();
            int outputMeta = PotionHelper.applyIngredient(inputMeta, ingredient.getItem().getPotionEffect(ingredient));
            if (inputMeta == outputMeta)
            {
                return null;
            }

            List<PotionEffect> oldEffects = Items.potionitem.getEffects(inputMeta);
            List<PotionEffect> newEffects = Items.potionitem.getEffects(outputMeta);

            boolean hasResult = false;
            if ((inputMeta <= 0 || oldEffects != newEffects) && (oldEffects == null || !oldEffects.equals(newEffects) && newEffects != null))
            {
                hasResult = true;
            }
            else if (!ItemPotion.isSplash(inputMeta) && ItemPotion.isSplash(outputMeta))
            {
                hasResult = true;
            }

            if (hasResult)
            {
                ItemStack output = input.copy();
                output.setItemDamage(outputMeta);
                return output;
            }
        }

        return null;
    }
}
