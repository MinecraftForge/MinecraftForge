package net.minecraftforge.debug;

import net.minecraftforge.event.entity.EntityEvent.SplashEffectEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(name = EntitySplashEventTest.MODNAME, modid = EntitySplashEventTest.MODID, version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class EntitySplashEventTest
{

    public static final String MODID = "entitysplasheventtest";
    public static final String MODNAME = "Entity Splash Event Test";
    public static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onEntitySplashEffectEvent(SplashEffectEvent event)
    {
        if (ENABLED)
        {
            event.setCanceled(true);
        }
    }

}
