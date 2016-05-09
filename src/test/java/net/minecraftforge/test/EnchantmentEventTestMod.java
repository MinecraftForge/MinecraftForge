package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEnchantItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Simple mod to test fov modifier. */
@Mod(modid="enchantmenteventtest", name="Enchantment Event Test", version="0.0.0")
public class EnchantmentEventTestMod {

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onItemEnchanted(PlayerEnchantItemEvent event)
    {
    	if (event.getItemStack() != null && event.getFuelStack() != null)
    	{
    		System.out.println("Enchanted Item: " + event.getItemStack().getDisplayName());
    		System.out.println("Fuel Item: " + event.getFuelStack().getDisplayName());  		
    		if (event.getItemStack().getItem() == Items.stone_sword)
    			event.setCanceled(true);
    	}
    }
}
