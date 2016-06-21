package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="CriticalHitEventTest", name="CriticalHitEventTest", version="0.0.0")
public class CriticalHitEventTest
{
    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event)
    {
        if(ENABLE)
        {
            System.out.println(event.getTarget() + " got hit by " + event.getEntityPlayer);
            event.setDamageModifier(2.0F);
        }
    }
}
