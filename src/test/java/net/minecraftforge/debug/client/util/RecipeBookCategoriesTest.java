package net.minecraftforge.debug.client.util;

import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.minecraftforge.debug.client.util.RecipeBookCategoriesTest.MODID;

@Mod(MODID)
@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class RecipeBookCategoriesTest {
    public static final String MODID = "recipe_book_categories_test";

    public static RecipeBookCategories test_category = RecipeBookCategories.create(MODID + ":test_category", new ItemStack(Items.IRON_AXE));
    public static RecipeBookCategories test_category2 = RecipeBookCategories.create(MODID + ":test_category2", RecipeBookCategories.SEARCH, new ItemStack(Items.PUMPKIN));

    public RecipeBookCategoriesTest() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent t) {
        RecipeBookCategories.addAdditionalCategory(test_category, (recipe) -> {
            return recipe.getType() == RecipeBookCategoriesTestRecipe.TYPE || recipe.getRecipeOutput().getItem() == Items.STICK;
        });
        RecipeBookCategories.addAdditionalCategory(test_category2, (recipe) -> {
            return recipe.getRecipeOutput().getItem() == Items.REDSTONE;
        });
        ClientRecipeBook.setCategoriesOfType(RecipeBookCategories.Type.CRAFTING, test_category2, test_category, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.CAMPFIRE, RecipeBookCategories.SEARCH, RecipeBookCategories.SMOKER_SEARCH);
    }

    @SubscribeEvent
    public static void onRecipeSerializerRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new RecipeBookCategoriesTestRecipe.Serializer().setRegistryName(MODID + ":crafting_test"));
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new RecipeBookCategoriesTestItem().setRegistryName(MODID + ":test_item"));
    }
}
