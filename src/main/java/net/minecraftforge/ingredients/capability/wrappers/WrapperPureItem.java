package net.minecraftforge.ingredients.capability.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.ingredients.IngredientStack;
import net.minecraftforge.ingredients.capability.templates.IngredientHandlerItemStackSimple;

public class WrapperPureItem extends IngredientHandlerItemStackSimple.Consumable
{
    public WrapperPureItem(ItemStack container, IngredientStack stack)
    {
        super(container, stack.amount);
        setIngredient(stack);
        setCanAdd(false);
    }
}
