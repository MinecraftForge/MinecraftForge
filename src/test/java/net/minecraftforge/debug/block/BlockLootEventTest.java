package net.minecraftforge.debug.block;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("block_loot_test")
@Mod.EventBusSubscriber
public class BlockLootEventTest {

    public BlockLootEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(BlockLootEventTest::onLootGenerate);
        MinecraftForge.EVENT_BUS.addListener(BlockLootEventTest::onLootDrop);
    }

    public static void onLootGenerate(BlockEvent.GenerateLootEvent event)
    {
        if(event.getState().getBlock() == Blocks.DIRT)
        {
            // Cancels loot generation for dirt blocks, we add our own loot manually in the drop event.
            event.setCanceled(true);
        }
    }

    public static void onLootDrop(BlockEvent.DropLootEvent event)
    {
        if(event.getState().getBlock() == Blocks.DIRT)
        {
            // Remove the default dirt drop from the dirt block.
            event.getDrops().add(Items.DIAMOND.getDefaultInstance());
        }
    }
}