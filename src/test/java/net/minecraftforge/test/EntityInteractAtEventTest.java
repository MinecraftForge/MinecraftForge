package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractAtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="EntityInteractAtEventTest", name="EntityInteractAtEventTest", version="0.0.0")
public class EntityInteractAtEventTest
{
    public static final boolean ENABLED = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onEntityInteractAt(EntityInteractAtEvent event)
    {
        System.out.println("EntityInteractAtEvent: Interacting with entity '" + event.target + "' at hit position " + event.hitVec);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event)
    {
        System.out.println("EntityInteractEvent: Interacting with entity '" + event.target);
        //event.setCanceled(true);
    }
}
