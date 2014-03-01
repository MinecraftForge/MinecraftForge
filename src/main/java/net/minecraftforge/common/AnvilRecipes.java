package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class AnvilRecipes {
    private static final AnvilRecipes instance = new AnvilRecipes();
    
    private List<IAnvilRecipe> anvilRecipes = new ArrayList<IAnvilRecipe>();

    public static final AnvilRecipes getInstance()
    {
        return instance;
    }
    
    public interface IAnvilRecipe
    {
        boolean matches(ItemStack inputSlot1, ItemStack inputSlot2, boolean renaming, World world);
        ItemStack getResult(ItemStack inputSlot1, ItemStack inputSlot2, boolean renaming, World world);
        int getCost(ItemStack inputSlot1, ItemStack inputSlot2, boolean renaming, World world);
        void getOutput(ItemStack inputSlot1, ItemStack inputSlot2, ItemStack output, boolean renaming, World world);
    }
    
    public void addRepair(IAnvilRecipe fRecipe)
    {
        anvilRecipes.add(fRecipe);
    }
    
    public ItemStack getRepairResult(ItemStack inputSlot1, ItemStack inputSlot2, boolean renaming, World world)
    {
        for(IAnvilRecipe recipe : anvilRecipes)
        {
            if(recipe.matches(inputSlot1, inputSlot2, renaming, world))
                return recipe.getResult(inputSlot1, inputSlot2, renaming, world);
        }
        return null;
    }
    
    public int getRepairCost(ItemStack inputSlot1, ItemStack inputSlot2, boolean renaming, World world)
    {
        for(IAnvilRecipe recipe : anvilRecipes)
        {
            if(recipe.matches(inputSlot1, inputSlot2, renaming, world))
                return recipe.getCost(inputSlot1, inputSlot2, renaming, world);
        }
        return 0;
    }
    
    public void repair(ItemStack inputSlot1, ItemStack inputSlot2, ItemStack output, boolean renaming, World world)
    {
        for(IAnvilRecipe recipe : anvilRecipes)
        {
            if(recipe.matches(inputSlot1, inputSlot2, renaming, world))
                recipe.getOutput(inputSlot1, inputSlot2, output, renaming, world);
        }
    }
}
