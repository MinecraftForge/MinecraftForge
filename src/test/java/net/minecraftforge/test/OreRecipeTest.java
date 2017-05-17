package net.minecraftforge.test;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid="orerecipetest", name="OreRecipeTest", version="0.0.0")
public class OreRecipeTest
{

    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if(!ENABLE) return;

        // Create a shapeless recipe to fill a Bottle from a Bucket
        ShapelessOreRecipe shapelessOreRecipe = new ShapelessOreRecipe(new ItemStack(Items.POTIONITEM),
                new ItemStack(Items.GLASS_BOTTLE),
                new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

        InventoryCrafting inventoryCrafting = new TestInventoryCrafting();
        inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.GLASS_BOTTLE));
        inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.WATER_BUCKET));
        if (shapelessOreRecipe.matches(inventoryCrafting, null))
        {
            System.out.println("Shapeless Ore Recipe FluidStack matching successful");
        }

        // Create a shaped recipe to fill a Bottle from a Bucket
        ShapedOreRecipe shapedOreRecipe = new ShapedOreRecipe(new ItemStack(Items.POTIONITEM),
                "B",
                "F",
                'B', new ItemStack(Items.GLASS_BOTTLE),
                'F', new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

        inventoryCrafting = new TestInventoryCrafting();
        inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.GLASS_BOTTLE));
        inventoryCrafting.setInventorySlotContents(4, new ItemStack(Items.WATER_BUCKET));
        if (shapedOreRecipe.matches(inventoryCrafting, null))
        {
            System.out.println("Shaped Ore Recipe FluidStack matching successful");
        }
    }

    private class TestInventoryCrafting extends InventoryCrafting
    {
        TestInventoryCrafting() {
            super(new Container() {
                @Override
                public boolean canInteractWith(EntityPlayer playerIn)
                {
                    return true;
                }
            }, 3, 3);
        }
    }
}
