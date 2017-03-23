package net.minecraftforge.debug;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModTrackingList;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        {
            return;
        }

        logger = event.getModLog();

        // create a broken shapeless recipe
        CraftingManager.getInstance().addShapelessRecipe(new ItemStack(Items.DIAMOND, 64), Items.PAPER, Items.AIR, Items.PAPER);
    }

    /**
     * Run tests after {@link ForgeModContainer#onAvailable(FMLLoadCompleteEvent)} has baked the recipe sorter and sorted the recipes.
     */
    @Mod.EventHandler
    public void onAvailable(FMLLoadCompleteEvent evt)
    {
        if (!ENABLE)
        {
            return;
        }

        checkShapelessRecipes();

        runRecipeTest(Lists.<IRecipe>newArrayList(), "Vanilla");
        runRecipeTest(new ModTrackingList<IRecipe>(Lists.<IRecipe>newArrayList()), "ModTracking");
    }

    private void checkShapelessRecipes() {
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

    private void runRecipeTest(List<IRecipe> listImpl, String listName)
    {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < 10000; i++)
        {
            addShapelessRecipe(listImpl, new ItemStack(Items.IRON_SHOVEL), Blocks.DIRT, Items.NETHER_STAR, new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getMetadata()));
            addRecipe(listImpl, new ItemStack(Items.GOLD_INGOT), "DDD", "A A", "NNN", 'D', Blocks.DIRT, 'A', Items.APPLE, 'N', Items.NETHER_STAR);
        }
        Collections.sort(listImpl, RecipeSorter.INSTANCE);
        stopwatch.stop();
        logger.info("10000 {} recipe list registration and sorting took {} ms", listName, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Copied from {@link CraftingManager#addShapelessRecipe(ItemStack, Object...)}
     * for testing performance of list implementations
     */
    public static void addShapelessRecipe(List<IRecipe> recipes, ItemStack stack, Object... recipeComponents)
    {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (Object object : recipeComponents)
        {
            if (object instanceof ItemStack)
            {
                list.add(((ItemStack) object).copy());
            }
            else if (object instanceof Item)
            {
                list.add(new ItemStack((Item) object));
            }
            else
            {
                if (!(object instanceof Block))
                {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                list.add(new ItemStack((Block) object));
            }
        }

        recipes.add(new ShapelessRecipes(stack, list));
    }

    /**
     * Copied from {@link CraftingManager#addRecipe(ItemStack, Object...)}
     * for testing performance of list implementations
     */
    public static void addRecipe(List<IRecipe> recipes, ItemStack stack, Object... recipeComponents)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = (String[]) ((String[]) recipeComponents[i++]);

            for (String s2 : astring)
            {
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String) recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character) recipeComponents[i];
            ItemStack itemstack = ItemStack.EMPTY;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item) recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block) recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack) recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int l = 0; l < j * k; ++l)
        {
            char c0 = s.charAt(l);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[l] = ((ItemStack) map.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[l] = ItemStack.EMPTY;
            }
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, stack);
        recipes.add(shapedrecipes);
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
