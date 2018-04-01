package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CanSleepAtTest.MODID, name = "CanSleepAtTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class CanSleepAtTest
{
    public static final String MODID = "can_sleep_at_test";
    public static final boolean ENABLED = false;
    public static DimensionType dimType = null;
    public static int dimId;
    private static Logger logger;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            dimId = DimensionManager.getNextFreeDimId();
            dimType = DimensionType.register("CanSleepAtTest", "_cansleepattest", dimId, WorldProviderTest.class, false);
            DimensionManager.registerDimension(dimId, dimType);
            logger.info("Registered CanSleepAtTest dimension as DIM {}", dimId);
        }
    }

    public static class WorldProviderTest extends WorldProvider
    {
        @Override
        public DimensionType getDimensionType()
        {
            return CanSleepAtTest.dimType;
        }

        @Override
        public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos)
        {
            // Creates a 5x5 blocks wide grid of the different sleep results
            return WorldSleepResult.values()[((pos.getX() / 5) + (pos.getZ() / 5)) % WorldSleepResult.values().length];
        }
    }
}
