package net.minecraftforge.debug;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.GrindstoneResultEvent;
import net.minecraftforge.event.GrindstoneUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("grindstone_event_test")
@Mod.EventBusSubscriber
public class GrindstoneEventTest {

	@SubscribeEvent
    public static void onGrindstoneUpdate(GrindstoneUpdateEvent event)
    {
        if (event.getTop().is(Items.LAPIS_LAZULI) && event.getBottom().is(Items.NETHERITE_INGOT))
        {
        	event.setOutput(new ItemStack(Items.DIAMOND, 1));
        	event.setXp(5);
        }
        
        if (event.getTop().is(Items.IRON_ORE) && event.getBottom().is(Items.FLINT))
        {
        	event.setOutput(new ItemStack(Items.RAW_IRON, 3));
        	event.setXp(0);
        }
        
        if (event.getTop().is(Items.IRON_AXE) && event.getBottom().is(Items.AIR))
        {
        	event.setOutput(event.getTop().copy());
        	event.setXp(-1);
        }
        
        if (event.getTop().is(Items.IRON_SHOVEL) && event.getBottom().is(Items.AIR))
        {
        	event.setOutput(ItemStack.EMPTY);
        }
        
        if (event.getTop().is(Items.IRON_SWORD) && event.getBottom().is(Items.AIR))
        {
        	event.setCanceled(true);
        }
    }
	
    @SubscribeEvent
    public static void onGrindstoneResult(GrindstoneResultEvent event) {
        if (event.getTop().is(Items.LAPIS_LAZULI) && event.getBottom().is(Items.NETHERITE_INGOT))
	    {
	        ItemStack top = event.getTop().copy();
	        ItemStack bottom = event.getBottom().copy();
	        bottom.shrink(1);
	        top.shrink(1);
			event.setnewBottom(bottom);
            event.setnewTop(top);
        }
    }
}
