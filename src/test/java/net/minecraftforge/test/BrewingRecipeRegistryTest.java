package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid="BrewingRecipeRegistryTest", name="BrewingRecipeRegistryTest", version="0.0.0")
public class BrewingRecipeRegistryTest 
{

    public static final boolean ENABLE = false;
    
    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
        if(!ENABLE) return;
        
        // The following adds a recipe that brews a piece of rotten flesh "into" a diamond sword resulting in a diamond hoe
        BrewingRecipeRegistry.addRecipe(new ItemStack(Items.diamond_sword), new ItemStack(Items.rotten_flesh), new ItemStack(Items.diamond_hoe));

        ItemStack output0 = BrewingRecipeRegistry.getOutput(new ItemStack(Items.diamond_sword), new ItemStack(Items.rotten_flesh));
        if(output0.getItem() == Items.diamond_hoe)
            System.out.println("Recipe succefully registered and working. Diamond Hoe obtained.");
        
        // Testing if OreDictionary support is working. Register a recipe that brews a gemDiamond into a gold sword resulting in a diamond sword
        BrewingRecipeRegistry.addRecipe(new ItemStack(Items.golden_sword), "gemDiamond", new ItemStack(Items.diamond_sword));
        
        ItemStack output1 = BrewingRecipeRegistry.getOutput(new ItemStack(Items.golden_sword), new ItemStack(Items.diamond));
        if(output1.getItem() == Items.diamond_sword)
            System.out.println("Recipe succefully registered and working. Diamond Sword obtained.");
        
        // In vanilla, brewing netherwart into a water bottle results in an awkward potion (with metadata 16). The following tests if that still happens
        ItemStack output2 = BrewingRecipeRegistry.getOutput(new ItemStack(Items.potionitem, 1, 0), new ItemStack(Items.nether_wart));
        if(output2 != null && output2.getItem() == Items.potionitem && output2.getItemDamage() == 16)
            System.out.println("Vanilla behaviour still in place. Brewed Water Bottle with Nether Wart and got Awkward Potion");
    }
}
