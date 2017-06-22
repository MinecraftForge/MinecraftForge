package net.minecraftforge.debug;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "recipetest", version = "1.0", acceptableRemoteVersions = "*")
public class RecipeTestMod
{
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        ResourceLocation location1 = new ResourceLocation("recipetest", "dirt");
        ShapedOreRecipe recipe1 = new ShapedOreRecipe(location1, new ItemStack(Blocks.DIAMOND_BLOCK), "DDD", 'D', new ItemStack(Blocks.DIRT));
        recipe1.setRegistryName(location1);
        GameRegistry.register(recipe1);

        ResourceLocation location2 = new ResourceLocation("recipetest", "stone");
        CraftingHelper.ShapedPrimer primer1 = CraftingHelper.parseShaped("SSS", 'S', new ItemStack(Blocks.IRON_BLOCK));
        ShapedRecipes recipe2 = new ShapedRecipes(location2.getResourcePath(), primer1.width, primer1.height, primer1.input, new ItemStack(Blocks.GOLD_BLOCK));
        recipe2.setRegistryName(location2);
        GameRegistry.register(recipe2);
    }
}
