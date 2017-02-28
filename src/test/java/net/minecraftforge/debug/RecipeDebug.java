package net.minecraftforge.debug;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = RecipeDebug.MOD_ID, name = "ForgeRecipeDebug", version = "1.0")
public class RecipeDebug
{
    public static final String MOD_ID = "recipedebug";

    public static final boolean ENABLE = false;

    public Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLE)
            return;

        logger = event.getModLog();

        // create a broken shapeless recipe
        CraftingManager.getInstance().addShapelessRecipe(new ItemStack(Items.DIAMOND, 64), Items.PAPER, Items.AIR, Items.PAPER);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!ENABLE)
            return;

        for (IRecipe recipe : CraftingManager.getInstance().getRecipeList())
        {
            if (recipe instanceof ShapelessRecipes)
            {
                ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;
                if (!isValidShapelessRecipe(shapelessRecipe))
                {
                    logInvalidShapelessRecipe(shapelessRecipe);
                }
            }
        }
    }

    private boolean isValidShapelessRecipe(ShapelessRecipes shapelessRecipe)
    {
        if (shapelessRecipe.getRecipeOutput().isEmpty())
        {
            return false;
        }
        for (ItemStack input : shapelessRecipe.recipeItems)
        {
            if (input == null || input.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    private void logInvalidShapelessRecipe(ShapelessRecipes shapelessRecipe)
    {
        ModContainer modContainer = CraftingManager.getInstance().getModContainer(shapelessRecipe);
        if (modContainer != null)
        {
            List<String> inputs = new ArrayList<String>();
            for (ItemStack input : shapelessRecipe.recipeItems)
            {
                inputs.add(input == null ? "null" : input.toString());
            }
            String name = modContainer.getName();
            String modId = modContainer.getModId();
            ItemStack recipeOutput = shapelessRecipe.getRecipeOutput();
            logger.info("Found invalid shapeless crafting recipe from mod: {} ({}).\nInputs: {}\nOutput: {}", name, modId, inputs, recipeOutput);
        }
    }
}
