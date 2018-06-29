package net.minecraftforge.debug;

import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = PortalSpawnEventTest.MOD_ID, name = "PortalSpawnEvent test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class PortalSpawnEventTest
{
    static final String MOD_ID = "portal_spawn_event_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onTrySpawnPortal(BlockEvent.PortalSpawnEvent event)
    {
        if (!ENABLED) return;

        World world = event.getWorld();
        if (world.provider.getDimension() == 0 && world.getBiome(event.getPos()) != Biomes.EXTREME_HILLS)
        {
            event.setCanceled(true);
        }
    }
}
