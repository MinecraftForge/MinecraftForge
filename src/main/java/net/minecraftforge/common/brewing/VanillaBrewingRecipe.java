package net.minecraftforge.common.brewing;

import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.oredict.OreDictionary;

public class VanillaBrewingRecipe implements IBrewingRecipe {

    @Override
    public boolean isInput(ItemStack stack)
    {
        return stack.getItem() instanceof ItemPotion || stack.getItem() == Items.glass_bottle;
    }

    @Override
    public boolean isIngredient(ItemStack stack)
    {
        return stack.getItem().isPotionIngredient(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        if (ingredient != null && input != null && input.getItem() instanceof ItemPotion)
        {
            int inputMeta = input.getMetadata();
            int outputMeta = PotionHelper.applyIngredient(inputMeta, ingredient.getItem().getPotionEffect(ingredient));
            if (inputMeta == outputMeta)
            {
                return null;
            }

            List oldEffects = Items.potionitem.getEffects(inputMeta);
            List newEffects = Items.potionitem.getEffects(outputMeta);

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