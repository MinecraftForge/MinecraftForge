package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.junit.Assert;

@Mod(modid = "forge.brewingstandfueltest")
public class BrewingStandFuelTest
{
    private static final boolean ENABLED = true;

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        if (!ENABLED)
            return;
        ItemStack blazeRod = new ItemStack(Items.blaze_rod);
        IFuelHandler fuelHandler = new BlazeRodFuel();
        BrewingRecipeRegistry.addFuelHandler(fuelHandler);
        Assert.assertEquals("The blaze rod was not registered", 10, BrewingRecipeRegistry.getFuelValue(blazeRod));
        Assert.assertEquals("Cannot unregister blaze powder", true, BrewingRecipeRegistry.removeFuelHandler(BrewingRecipeRegistry.BlazePowderFuelHandler.INSTANCE));
        Assert.assertEquals("Blaze powder was not unregistered", false, BrewingRecipeRegistry.isFuel(new ItemStack(Items.blaze_powder)));

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onCapAttach(AttachCapabilitiesEvent.Item e)
    {
        ItemStack stack = e.getItemStack();
    }

    private static final class BlazeRodFuel implements IFuelHandler
    {
        @Override
        public int getBurnTime(ItemStack fuel)
        {
            return fuel.getItem() == Items.blaze_rod ? 10 : 0;
        }
    }
}
