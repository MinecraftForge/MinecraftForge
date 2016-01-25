package net.minecraftforge.debug;

import net.minecraft.block.BlockFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.smelting.ItemSmeltEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ItemSmeltEventTest.MODID, version = ItemSmeltEventTest.VERSION)
public class ItemSmeltEventTest
{
    public static final String MODID = "ForgeSampleItemSmeltEvent";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        //fluid and filled container can be dummy.
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.WATER, new ItemStack(Items.slime_ball), new ItemStack(Items.slime_ball));
    }

    @SubscribeEvent
    public void preItemSmelt(ItemSmeltEvent.Pre event)
    {
        if(event.getCookTime() != event.getTotalCookTime() - 1) return;
        if(event.getItem(0) != null && event.getItem(0).getItem() == Item.getItemFromBlock(Blocks.sponge)) return;
        if(event.getItem(1) == null || event.getItem(1).getItem() != Items.slime_ball)
        {
            event.setCanceled(true);
            return;
        }
        event.setItem(1, new ItemStack(Items.magma_cream));
    }

    @SubscribeEvent
    public void postItemSmelt(ItemSmeltEvent.Post event)
    {
        if(event.getWorld().getTileEntity(event.getPos()) instanceof TileEntityFurnace)
        {
            TileEntityFurnace furnace = (TileEntityFurnace) event.getWorld().getTileEntity(event.getPos());
            if(!furnace.isBurning()) return;
            if(event.getItem(1) != null && event.getItem(1).getItem() != Items.water_bucket) return;
            event.setItem(1, new ItemStack(Items.bucket));
        }
    }

}
