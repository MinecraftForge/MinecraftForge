package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "projectile_event_test", name = "ProjectileImpactEvent test mod", version = "1.0", acceptableRemoteVersions = "*")
public class ProjectileImpactEventTest
{
    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(ProjectileImpactEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event)
    {
        logger.info("projectile: {}, impact: {}", event.getEntity().getName(), event.getRayTraceResult());
    }
}
