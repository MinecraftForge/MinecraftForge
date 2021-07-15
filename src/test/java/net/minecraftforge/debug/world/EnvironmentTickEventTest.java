package net.minecraftforge.debug.world;

import com.google.common.eventbus.Subscribe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("environmenttickeventtest")
public class EnvironmentTickEventTest
{
    private final Logger LOGGER;
    
    public EnvironmentTickEventTest()
    {
        LOGGER = LogManager.getLogger();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void environmentTickEvent(final TickEvent.EnvironmentTickEvent event)
    {
        LOGGER.info(event.phase + " Environment tick event at " + event.chunk.getPos());
    }
}
