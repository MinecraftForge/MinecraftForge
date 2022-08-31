package net.minecraftforge.debug.world;

import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

@Mod("block_grow_feature_test")
@Mod.EventBusSubscriber
public class BlockGrowFeatureTest
{
    @SubscribeEvent
    public static void onGrowReplaceFeature(SaplingGrowTreeEvent event)
    {
        event.setFeature(TreeFeatures.BIRCH_BEES_005);
        if (event.getFeature().equals(TreeFeatures.BIRCH_BEES_005)) {
            LogManager.getLogger().info("BlockGrowFeatureTest replaced feature");
        }
    }
}
