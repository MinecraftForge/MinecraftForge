import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("testmod")
@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class TestMod {
    public static RecipeBookCategories test_category = RecipeBookCategories.create("testmod:test_category", new ItemStack(Items.IRON_AXE));
    public static RecipeBookCategories test_category2 = RecipeBookCategories.create("testmod:test_category2", RecipeBookCategories.SEARCH, new ItemStack(Items.PUMPKIN));

    public TestMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent t) {
        RecipeBookCategories.addAdditionalCategory(test_category, (recipe) -> {
            return recipe.getType() == TestRecipe.TYPE || recipe.getRecipeOutput().getItem() == Items.STICK;
        });
        RecipeBookCategories.addAdditionalCategory(test_category2, (recipe) -> {
            return recipe.getRecipeOutput().getItem() == Items.REDSTONE;
        });
        ClientRecipeBook.addCategoriesToType(RecipeBookCategories.Type.CRAFTING, test_category, test_category2);
        ClientRecipeBook.addCategoriesToType(RecipeBookCategories.Type.STONECUTTER, test_category2, test_category, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.CAMPFIRE, RecipeBookCategories.SEARCH, RecipeBookCategories.SMOKER_SEARCH);
    }

    @SubscribeEvent
    public static void onRecipeSerializerRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new TestRecipe.Serializer().setRegistryName("testmod:crafting_test"));
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new TestItem().setRegistryName("testmod:test_item"));
    }
}
