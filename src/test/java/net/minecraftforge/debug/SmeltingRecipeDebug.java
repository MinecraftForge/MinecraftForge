package net.minecraftforge.debug;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.smelting.AbstractSmeltingRecipe;
import net.minecraftforge.common.smelting.SmeltingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SmeltingRecipeDebug.MODID, name = "SmeltingRecipeDebug", version = SmeltingRecipeDebug.VERSION)
public class SmeltingRecipeDebug
{
    public static final String MODID = "smeltingrecipedebug";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        SmeltingRecipeRegistry.addRecipe(new AbstractSmeltingRecipe(50)
        {
            @Override
            protected ItemStack getOutput0(ItemStack input)
            {
                return new ItemStack(Blocks.DIAMOND_BLOCK);
            }

            @Override
            public boolean matches(ItemStack input)
            {
                return input.getItem() == Item.getItemFromBlock(Blocks.DIRT);
            }
        });

        SmeltingRecipeRegistry.addOreRecipe("sugarcane", new ItemStack(Blocks.BRICK_BLOCK));
    }

}