package net.minecraftforge.debug.block;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.SimpleTicket;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "farmlandwatertest", name = "Farmland Water Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class FarmlandWaterTest
{
    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(FarmlandWaterTest.class);
        }
    }

    //Sets a region of 16x16x16 watered at the pos where an arrow hit
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent.Arrow event)
    {
        World world = event.getEntity().world;
        if (!world.isRemote)
        {
            BlockPos pos = new BlockPos(event.getRayTraceResult().hitVec);
            AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(8D);
            //600 ticks = 30 seconds
            FarmlandWaterManager.addWateredRegion(world, aabb, 600);
            logger.info("Watering " + aabb + " for 30 seconds");
        }
    }
}
