package net.minecraftforge.debug;

import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "kbhtest", name = "Knock Back Hook Test", version = "1.0")
@Mod.EventBusSubscriber
public class KnockBackHookTest
{
	private static final boolean ENABLED = false;

	@SubscribeEvent
	public static void onKnockBack(LivingKnockBackEvent event)
	{
		if(ENABLED)
		{
			if(event.getEntityLiving() instanceof EntitySheep)
			{
				event.setStrength(0.2F);
			}
			else if(event.getEntityLiving() instanceof EntityCow)
			{
				event.setCanceled(true);
			}
		}
	}
}