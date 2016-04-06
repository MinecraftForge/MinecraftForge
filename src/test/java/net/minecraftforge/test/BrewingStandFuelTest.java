package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.junit.Assert;

@Mod(modid = "brewingstandfueltest")
public class BrewingStandFuelTest
{
    private static final boolean ENABLED = true;

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        if (!ENABLED)
            return;
        ItemStack blazeRod = new ItemStack(Items.blaze_rod);
        BrewingRecipeRegistry.addFuel(blazeRod, 80);
        Assert.assertEquals("The blaze rod was not registered", 80, BrewingRecipeRegistry.getFuelValue(blazeRod));

        BrewingRecipeRegistry.addFuel(blazeRod, 60);
        Assert.assertEquals("Incorrect fuel value for blaze rod", 80, BrewingRecipeRegistry.getFuelValue(blazeRod));

        ItemStack blazePowder = new ItemStack(Items.blaze_powder);
        Assert.assertEquals("Cannot unregister blaze powder", true, BrewingRecipeRegistry.removeFuel(blazePowder));
        Assert.assertEquals("Blaze powder was not unregistered", false, BrewingRecipeRegistry.isFuel(blazePowder));

        ItemStack lexHead = new ItemStack(Items.skull, 1, 3);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("SkullOwner", "LexManos");
        lexHead.setTagCompound(tag);
        BrewingRecipeRegistry.addFuel(lexHead, 2);

        ItemStack head2 = new ItemStack(Items.skull, 1, 3);
        tag = new NBTTagCompound();
        tag.setString("SkullOwner", "LexManos");
        head2.setTagCompound(tag);
        Assert.assertEquals("NBT matching error", true, BrewingRecipeRegistry.isFuel(head2));
    }
}
