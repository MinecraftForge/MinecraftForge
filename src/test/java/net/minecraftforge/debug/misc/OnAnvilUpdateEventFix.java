package net.minecraftforge.debug.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(OnAnvilUpdateEventFix.MOD_ID)
@Mod.EventBusSubscriber(modid = OnAnvilUpdateEventFix.MOD_ID)
public class OnAnvilUpdateEventFix 
{
    private static final boolean ENABLED = false;
    private static final Logger LOGGER = LogManager.getLogger();
    static final String MOD_ID = "anvil_update_event_fix";
    public OnAnvilUpdateEventFix() 
    {
    
    }

    @SubscribeEvent
    public static void anvilUpdate(AnvilUpdateEvent event) 
    {
        if (ENABLED) 
        {
            LOGGER.warn("Anvil input or name changed");
        }
    }
}
