package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingUpdateBlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="livingupdateblocktest", name="Living Update Block Event", version="0.0.0")
public class LivingUpdateBlockEventTest 
{
 
    //because this test can generate a large amount of output, by default it is turned off
    private static final boolean DEBUG_OUTPUT = false;
    
    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onEntityBlockChange(LivingUpdateBlockEvent event)
    {
        if(DEBUG_OUTPUT)
            System.out.printf("%s tried to change block to %s at %s\n", 
                    event.getEntityLiving().getName(), Block.blockRegistry.getNameForObject(event.getBlock().getBlock()).getResourcePath(), event.getPos());
        event.getEntity().worldObj.setBlockState(event.getPos(), Blocks.gold_block.getDefaultState());
        event.setCanceled(true);
    }
}
