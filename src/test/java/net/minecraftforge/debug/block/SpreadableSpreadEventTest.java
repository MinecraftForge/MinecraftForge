package net.minecraftforge.debug.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.SpreadableSpreadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This test mod blocks pistons from moving cobblestone at all except indirectly
 * This test mod adds a block that moves upwards when pushed by a piston
 * This test mod informs the user what will happen the piston and affected blocks when changes are made
 * This test mod makes black wool pushed by a piston drop after being pushed.
 */
@Mod.EventBusSubscriber(modid = SpreadableSpreadEventTest.MODID)
@Mod(value = SpreadableSpreadEventTest.MODID)
public class SpreadableSpreadEventTest {
	
    public static final String MODID = "spreadable_spread_event_test";
    
    @SubscribeEvent
    public static void spreadableSpreadPre(SpreadableSpreadEvent.Pre event)
    {
    	/* MYCELIUM spreading is cancelled */
    	BlockState blockstate = event.getState();
    	if (blockstate.getBlock() == Blocks.MYCELIUM) 
    	{
    		event.setCanceled(true);
    	}
    }
    
    @SubscribeEvent
    public static void spreadableSpreadPost(SpreadableSpreadEvent.Post event)
    {
    	/* GRASS spreads green wool instead of GRASS */
    	BlockState blockstate = event.getOriginalState();
    	if (blockstate.getBlock() == Blocks.DIRT) 
    	{
    		World world = event.getWorld().getWorld();
    		BlockPos pos = event.getPos();
            world.setBlockState(pos, Blocks.GREEN_WOOL.getDefaultState());
    	}
    }
}
