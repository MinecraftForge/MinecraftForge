package net.minecraftforge.debug.block;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FarmlandTrampleEventTest.MOD_ID, name = "Farmland Trample Event Test", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class FarmlandTrampleEventTest
{
    static final String MOD_ID = "farmland_trample_test";
    static final boolean ENABLED = true;

    @SubscribeEvent
    public static void onFarmlandTrampled(BlockEvent.FarmlandTrampleEvent event)
    {
        if (!ENABLED)
        {
            return;
        }
        if(event.getEntity().isSneaking())
        {
            event.setCanceled(true);
        }
    }
}
