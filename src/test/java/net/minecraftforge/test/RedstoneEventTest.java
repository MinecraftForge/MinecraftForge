package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.RedstoneEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Simple mod to test redstone events. */
@Mod(modid = "redstoneeventtest", name = "Redstone Event Test", version = "0.0.0")
public class RedstoneEventTest
{

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /*
     * ==== Weak power output ====
     * 
     * An iron block will emit a weak redstone signal of exactly 10 on the east
     * 
     * A redstone torch with a gold block opposite the side tested will emit a redstone signal of at least 3
     * 
     * A dispenser with a dark oak fence on top of it will power itself with a weak signal of 15
     * 
     * A redstone block above a redstone torch will only have a signal strength of 8
     * 
     * ==== Strong power output ====
     * 
     * An iron block will emit a strong redstone signal of exactly 5 on the north
     * 
     * A redstone torch will emit a strong redstone signal of at least 5 to any emerald blocks above it
     * 
     * A dispenser with an oak fence on top of it will power itself with a strong power of 15
     */

    @SubscribeEvent
    public void weakPowerOutput(RedstoneEvent.WeakOutput event)
    {
        if(event.getState().getBlock() == Blocks.DISPENSER && event.getWorld().getBlockState(event.getPos().up()).getBlock() == Blocks.DARK_OAK_FENCE) {
            event.setNewPower(15);
        }
        if (event.getState().getBlock() instanceof BlockRedstoneTorch && event.getWorld().getBlockState(event.getPos().offset(event.getSide().getOpposite())).getBlock() == Blocks.GOLD_BLOCK)
        {
            event.increaseNewPowerTo(3);
        }
        
        if (event.getState().getBlock() == Blocks.REDSTONE_BLOCK && event.getWorld().getBlockState(event.getPos().down()).getBlock()  instanceof BlockRedstoneTorch )
        {
            event.setNewPower(8);
        }
        
        if(event.getState().getBlock() == Blocks.IRON_BLOCK && event.getSide() == EnumFacing.EAST) {
            event.setNewPower(10);
        }
    }

    @SubscribeEvent
    public void strongPowerOutput(RedstoneEvent.StrongOutput event)
    {
        if(event.getState().getBlock() == Blocks.DISPENSER && event.getWorld().getBlockState(event.getPos().up()).getBlock() == Blocks.OAK_FENCE) {
            event.setNewPower(15);
        }
        if(event.getState().getBlock() == Blocks.IRON_BLOCK && event.getSide() == EnumFacing.NORTH) {
            event.setNewPower(5);
        }
        
        if(event.getState().getBlock() instanceof BlockRedstoneTorch && event.getSide() == EnumFacing.UP && event.getWorld().getBlockState(event.getPos().up()).getBlock() == Blocks.EMERALD_BLOCK) {
            event.increaseNewPowerTo(5);
        }
    }
}