package net.minecraftforge.debug;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.smelting.SmeltingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = "forgesmeltingtest")
public class SmeltingRecipeTest
{

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // test general mechanic
        SmeltingRecipeRegistry.addSimpleRecipe(new ItemStack(Blocks.DIRT), new ItemStack(Items.DIAMOND), 2, 50);

        // test metadata inputs
        SmeltingRecipeRegistry.addSimpleRecipe(new ItemStack(Blocks.DIRT, 1, 1), new ItemStack(Items.DIAMOND_AXE));

        // test wildcard experience
        SmeltingRecipeRegistry.addSimpleRecipe(new ItemStack(Blocks.DIAMOND_BLOCK), new ItemStack(Blocks.DIRT, 1, 1));
        SmeltingRecipeRegistry.setExperience(new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE), 4);
    }

}
