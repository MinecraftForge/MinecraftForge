package net.minecraftforge.debug;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.smelting.SmeltingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "forgesmeltingtest")
public class SmeltingRecipeTest
{

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        SmeltingRecipeRegistry.addSimpleRecipe(new ItemStack(Blocks.DIRT), new ItemStack(Items.DIAMOND), 50);
    }

}
