package net.minecraftforge.debug;

import net.minecraftforge.client.event.CameraDistanceUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ThirdPersonDistanceTest.MOD_ID, name = "Third-person distance test mod", version = "1.0.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public class ThirdPersonDistanceTest
{
    static final String MOD_ID = "thirdperson_distance_test";
    private static final boolean ENABLED = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(ThirdPersonDistanceTest.class);
        }
    }

    @SubscribeEvent
    public static void updateCameraDistance(CameraDistanceUpdateEvent event)
    {
        if (event.getEntity().isRiding())
        {
            event.multiplyDistance(2f);
        }
    }
}
