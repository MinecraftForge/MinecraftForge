package net.minecraftforge.debug;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "kbhtest", name = "Knock Back Hook Test")
@Mod.EventBusSubscriber
public class KnockBackHookTest
{
	@SubscribeEvent
	public static void onKnockBack(LivingKnockBackEvent event)
	{
		if(event.getEntityLiving() instanceof EntitySheep)
		{
			event.setCanceled(true);
		}
	}
}