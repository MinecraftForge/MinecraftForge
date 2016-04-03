package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.FireTickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="FireTickTest", name="FireTickTest", version="0.0.0")
public class FireTickTest {

	public static final boolean ENABLE = false;
	
	@EventHandler
	public void init(FMLInitializationEvent event) 
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onFireBurn(FireTickEvent event)
	{
		if(ENABLE)
		{
			System.out.println("Something is burning at "+event.pos.toString());
			event.setCanceled(true);
		}
	}

}
