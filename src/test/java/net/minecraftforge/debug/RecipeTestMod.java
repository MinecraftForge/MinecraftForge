package net.minecraftforge.debug;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oreregistry.ShapedForgeRecipe;

@Mod(modid = "recipetest", version = "1.0", acceptableRemoteVersions = "*")
public class RecipeTestMod
{
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        ResourceLocation location1 = new ResourceLocation("recipetest", "dirt");
        ShapedForgeRecipe recipe1 = new ShapedForgeRecipe(location1, new ItemStack(Blocks.DIAMOND_BLOCK), "DDD", 'D', new ItemStack(Blocks.DIRT));
        recipe1.setRegistryName(location1);
        event.getRegistry().register(recipe1);

        ResourceLocation location2 = new ResourceLocation("recipetest", "stone");
        CraftingHelper.ShapedPrimer primer1 = CraftingHelper.parseShaped("SSS", 'S', new ItemStack(Blocks.IRON_BLOCK));
        ShapedRecipes recipe2 = new ShapedRecipes(location2.getResourcePath(), primer1.width, primer1.height, primer1.input, new ItemStack(Blocks.GOLD_BLOCK));
        recipe2.setRegistryName(location2);
        event.getRegistry().register(recipe2);
    }
}
