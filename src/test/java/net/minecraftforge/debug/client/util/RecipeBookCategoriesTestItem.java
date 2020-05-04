package net.minecraftforge.debug.client.util;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class RecipeBookCategoriesTestItem extends Item {
    public RecipeBookCategoriesTestItem() {
        super(new Item.Properties().maxStackSize(1));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        if(worldIn.isRemote)
        {
            List<RecipeList> listRecipeList = ((ClientPlayerEntity) playerIn).getRecipeBook().getRecipes(RecipeBookCategoriesTest.test_category);
            listRecipeList.forEach((recipeList) -> {
                playerIn.sendMessage(new StringTextComponent("new listRecipes:"));
                List<IRecipe<?>> listRecipes = recipeList.getRecipes();
                listRecipes.forEach((recipe) -> {
                    playerIn.sendMessage(new StringTextComponent(recipe.toString() + " unlocked: " + ((ClientPlayerEntity)playerIn).getRecipeBook().isUnlocked(recipe)));
                });
            });
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
