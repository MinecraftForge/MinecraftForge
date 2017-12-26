package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.advancement.AdvancementAboutToLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "advancementremovaltest", name = "Advancement removal test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class AdvancementRemovalTest
{
    static final boolean ENABLED = false;
    private static Logger logger;

    @EventHandler
    public static void preInit(final FMLPreInitializationEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        logger = event.getModLog();
    }

    @SubscribeEvent
    public static void onAdvancementAboutToLoad(final AdvancementAboutToLoadEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (event.getLocation().getResourceDomain().equals("minecraft") && event.getLocation().getResourcePath().startsWith("nether"))
        {
            logger.info("Removing advancement: {}", event.getLocation().toString());
            event.setCanceled(true);
        }
    }
}
