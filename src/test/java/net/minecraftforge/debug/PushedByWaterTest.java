package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityPushedByWaterEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "pushedbywatertest", name = "PushedByWaterTest", version = "1.0", acceptableRemoteVersions = "*")
public class PushedByWaterTest
{
    public static final boolean ENABLE = false;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(PushedByWaterTest.class);
        }
    }

    @SubscribeEvent
    public static void onPushedByWater(EntityPushedByWaterEvent event)
    {
        switch (event.getEntity().getCustomNameTag())
        {
        case "alwaysPushed":
            event.setResult(Result.ALLOW);
            break;
        case "neverPushed":
            event.setResult(Result.DENY);
        }
    }
}
