package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EventCancelTest.MODID, name = "EventCancelTest", version = "1.0")
public class EventCancelTest
{
    public static final String MODID = "eventcanceltest";
    public static final boolean ENABLED = false;
    public static Logger log;
    
    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {       
        if (ENABLED) 
        {         
            log = event.getModLog();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void canDimensionChangeFirst(EntityTravelToDimensionEvent event) 
    {       
        event.setCanceled(true);
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void canDimensionChangeLast(EntityTravelToDimensionEvent event) 
    {       
        if (event.isCanceled())
        {
            log.info("EntityTravelToDimensionEvent was canceled by {}", event.getCanceler().getModId());
        }
    }
}
