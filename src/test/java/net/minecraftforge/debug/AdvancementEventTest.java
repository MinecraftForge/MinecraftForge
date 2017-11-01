package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = AdvancementEventTest.MOD_ID, name = "AdvancementEvent test mod", version = "1.0.0", acceptableRemoteVersions = "*")
public class AdvancementEventTest
{
    static final String MOD_ID = "advancement_event_test";
    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(AdvancementEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onAdvancementEvent(AdvancementEvent event)
    {
        if (event.getAdvancement().getDisplay() != null && event.getAdvancement().getDisplay().shouldAnnounceToChat())
        {
            logger.info("{} got the {} advancement", event.getEntityPlayer().getDisplayNameString(), event.getAdvancement().getDisplayText().getUnformattedText());
        }
    }
}
