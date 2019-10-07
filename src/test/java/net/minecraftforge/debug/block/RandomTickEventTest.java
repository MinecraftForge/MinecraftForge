package net.minecraftforge.debug.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This test mod:
 * - Will print before random tick of mushroom and saplings blocks, their blockstate and the position to all players wearing iron boots in the same world
 * - Will grow brown mushrooms at random tick to a huge mushroom
 * - Will prevent red mushrooms from spreading at random tick
 */
@Mod("random_tick_event_test")
@Mod.EventBusSubscriber
public class RandomTickEventTest {

    private static boolean hasDebugItem(PlayerEntity player) {
        return player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == Items.IRON_BOOTS;
    }

    private static void sendMessageToAllPlayersWithDebugItem(IWorld world, String message) {
        world.getPlayers().forEach(player -> {
            if (hasDebugItem(player)) {
                player.sendMessage(new StringTextComponent(message));
            }
        });
    }

    @SubscribeEvent
    public static void onRandomTick(BlockEvent.RandomTickEvent event) {
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!world.isAreaLoaded(pos, 7)) return; // Forge: prevent loading unloaded chunks
        BlockState state = event.getState();
        Block block = state.getBlock();
        if (block instanceof MushroomBlock || block instanceof SaplingBlock) {
            sendMessageToAllPlayersWithDebugItem(world, "Random tick of " + event.getState() + " at position " + event.getPos().toString());
            if (block instanceof MushroomBlock) {
                if (block == Blocks.BROWN_MUSHROOM) {
                    event.setCanceled(true);
                    ((MushroomBlock) block).grow(world.getWorld(), event.getRandom(), pos, state);
                    sendMessageToAllPlayersWithDebugItem(world, "Grow huge mushroom of " + event.getState() + " at position " + event.getPos().toString());
                } else if (block == Blocks.RED_MUSHROOM) {
                    event.setCanceled(true);
                    sendMessageToAllPlayersWithDebugItem(world, "Cancel random tick behaviour of " + event.getState() + " at position " + event.getPos().toString());
                }
            }
        }
    }

}
