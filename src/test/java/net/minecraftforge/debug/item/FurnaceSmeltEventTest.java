package net.minecraftforge.debug.item;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceSmeltEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FurnaceSmeltEventTest.MODID, name = "Furnace Smelt Event Test", version = "1.0", acceptableRemoteVersions = "*")
public class FurnaceSmeltEventTest {

    public static final String MODID = "furnacesmelteventtest";

    private static final boolean ENABLED = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (ENABLED) {
            MinecraftForge.EVENT_BUS.register(FurnaceSmeltEventTest.class);
        }
        FurnaceRecipes.instance().addSmelting(Items.POTIONITEM, new ItemStack(Items.LINGERING_POTION), 0.1f);
    }

    @SubscribeEvent
    public static void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack inputStack = event.getInputStack();
        //If the input item is a potion item, and the furnace is on a diamond block, set the output item to be a lingering potion with the same effects as the input potion
        if(inputStack.getItem() == Items.POTIONITEM && event.getFurnace().getWorld().getBlockState(event.getFurnace().getPos().down()).getBlock() == Blocks.DIAMOND_BLOCK) {
            ItemStack out = new ItemStack(Items.LINGERING_POTION);
            PotionUtils.addPotionToItemStack(out, PotionUtils.getPotionFromItem(inputStack));
            PotionUtils.appendEffects(out, PotionUtils.getEffectsFromStack(inputStack));
            event.setOutputStack(out);
        }
    }

}
