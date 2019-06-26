package net.minecraftforge.debug.block;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("harvest_drops_event_test")
@Mod.EventBusSubscriber
public class HarvestDropsEventTest {

    public HarvestDropsEventTest() {
        MinecraftForge.EVENT_BUS.addListener(HarvestDropsEventTest::onBlockHarvest);
    }

    public static void onBlockHarvest(BlockEvent.HarvestDropsEvent event) {
        if (event.getState().getBlock() == Blocks.DIRT) {
            event.getDrops().set(0, Items.DIAMOND.getDefaultInstance());
        }
    }
}