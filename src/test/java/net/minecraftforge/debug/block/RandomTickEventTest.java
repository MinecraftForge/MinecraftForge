package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This test mod:
 * - Will print before random tick of saplings, their blockstate and the position to all players wearing leather helmets in the same world
 * - Will prevent oak saplings from growing to trees automatically via random tick
 */
@Mod("random_tick_event_test")
@Mod.EventBusSubscriber
public class RandomTickEventTest {

    @SubscribeEvent
    public static void onRandomTick(BlockEvent.RandomTickEvent event) {
        Block block = event.getState().getBlock();
        if (block instanceof SaplingBlock) {
            event.getWorld().getPlayers().forEach(player -> {
                if (player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.LEATHER_HELMET) {
                    player.sendMessage(new StringTextComponent("Random tick of " + event.getState() + " at position " + event.getPos().toString()));
                    if (block == Blocks.OAK_SAPLING) {
                        player.sendMessage(new StringTextComponent("Canceled random tree growing of " + event.getState() + " at position " + event.getPos().toString()));
                    }
                }
            });
            event.setCanceled(block == Blocks.OAK_SAPLING);
        }
    }

}
