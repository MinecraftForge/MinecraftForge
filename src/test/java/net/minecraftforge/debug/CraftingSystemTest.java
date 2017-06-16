package net.minecraftforge.debug;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.BooleanSupplier;

@Mod(modid = CraftingSystemTest.MOD_ID, name = "CraftingTestMod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class CraftingSystemTest
{
    static final String MOD_ID = "crafting_system_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        FMLLog.info("Registering Test Recipes:");
    }

    public static class IngredientFactory implements IIngredientFactory
    {
        @Override
        public Ingredient parse(JsonContext context, JsonObject json)
        {
            return null;
        }
    }

    public static class RecipeFactory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return null;
        }

    }

    public static class ConditionFactory implements IConditionFactory
    {
        @Override
        public BooleanSupplier parse(JsonContext context, JsonObject json)
        {
            return () -> true;
        }
    }
}
