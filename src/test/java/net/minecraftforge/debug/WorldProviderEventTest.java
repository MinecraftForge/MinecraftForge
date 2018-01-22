package net.minecraftforge.debug;

import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.event.world.CreateWorldProviderEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = WorldProviderEventTest.MOD_ID, name = "CreateWorldProviderEvent test mod", version = "1.0")
@Mod.EventBusSubscriber
public class WorldProviderEventTest
{
    static final String MOD_ID = "world_provider_event_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void setWorldProvider(CreateWorldProviderEvent event)
    {
        if (!ENABLED) return;

        if (event.getDimension() == 0)
        {
            event.setProvider(new TestWorldProvider());
        }
    }

    private static final class TestWorldProvider extends WorldProviderSurface
    {
        @Override
        public void getLightmapColors(float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors)
        {
            colors[0] = 0.22F + blockLight * 0.75F;
            colors[1] = 0.28F + blockLight * 0.75F;
            colors[2] = 0.25F + blockLight * 0.75F;
        }
    }
}
