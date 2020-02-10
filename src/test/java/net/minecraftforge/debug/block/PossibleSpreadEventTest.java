/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent.PossibleSpreadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This test mod:
 * - Will print before spreading of blocks, their blockstate, the outcome, and the affected positions to all players wearing leather helmets in the same world
 * - Will print after spreading of blocks, their blockstate, and the affected positions to all players wearing leather helmets in the same world
 * - 5 tests which you can enable or disable (debug prints for all players wearing leather helmets in the same world)
 *
 * dirt & mycelium test
 * - disable spreading to dirt blocks, where dirt is below
 * - force spreading to dirt blocks, where stone is below
 *
 * mushroom test
 * - force spreading mushrooms on dirt
 *
 * sea pickle test
 * - force spreading sea pickles under water on diamond blocks
 */
@Mod("possible_spread_event_test")
@Mod.EventBusSubscriber(modid = "possible_spread_event_test")
public class PossibleSpreadEventTest {

    private static final boolean SEND_MESSAGE_TO_PLAYERS = true;
    private static final boolean SEND_MESSAGE_TO_DEBUG_CONSOLE = false;

    private static final boolean SEND_PRE_MESSAGE = true;
    private static final boolean SEND_POST_MESSAGE = true;

    private static final boolean GRASS_TEST = true;
    private static final boolean MYCELIUM_TEST = true;
    private static final boolean MUSHROOM_TEST = true;
    private static final boolean SEA_PICKLE_TEST = true;

    private static Logger logger = LogManager.getLogger();

    private static boolean hasDebugItem(PlayerEntity player) {
        return player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.LEATHER_HELMET;
    }

    private static void sendMessageToAllPlayersWithDebugItem(IWorld world, String message) {
        world.getPlayers().forEach(player -> {
            if (hasDebugItem(player)) {
                player.sendMessage(new StringTextComponent(message));
            }
        });
    }

    private static void sendMessage(IWorld world, String message) {
        if (SEND_MESSAGE_TO_PLAYERS) {
            sendMessageToAllPlayersWithDebugItem(world, message);
        }
        if (SEND_MESSAGE_TO_DEBUG_CONSOLE) {
            logger.info(message);
        }
    }

    private static void sendPreSpreadMessage(PossibleSpreadEvent.Pre event) {
        if (!SEND_PRE_MESSAGE) return;
        sendMessage(event.getWorld(), "Pre-spread of " + event.getState() + " to position " + event.getSpreadPos().toString() + ", " + (event.willSpread() ? "will spread" : "will not spread"));
    }

    private static void sendPostSpreadMessage(PossibleSpreadEvent.Post event) {
        if (!SEND_POST_MESSAGE) return;
        sendMessage(event.getWorld(), "Post-spread of " + event.getState() + " to position " + event.getSpreadPos().toString());
    }

    @SubscribeEvent
    public static void onPossibleSpreadEventPre(PossibleSpreadEvent.Pre event) {
        IWorld world = event.getWorld();
        Block block = event.getState().getBlock();
        BlockPos spreadPos = event.getSpreadPos();
        Block spreadBlock = world.getBlockState(spreadPos).getBlock();
        Block blockBelowSpreadBlock = world.getBlockState(spreadPos.down()).getBlock();

        if ((GRASS_TEST && block == Blocks.GRASS_BLOCK) ||
                (MYCELIUM_TEST && block == Blocks.MYCELIUM)) {
            sendPreSpreadMessage(event);
            if (spreadBlock == Blocks.DIRT && world.getLight(spreadPos.up()) >= 9) {
                //disable spreading to dirt blocks, where dirt is below
                if (blockBelowSpreadBlock == Blocks.DIRT) {
                    sendMessageToAllPlayersWithDebugItem(world, "Blocking " + event.getState() + " spread onto dirt at " + spreadPos);
                    event.setResult(Result.DENY);
                }
                //force spreading to dirt blocks, where stone is below
                if (blockBelowSpreadBlock == Blocks.STONE) {
                    sendMessageToAllPlayersWithDebugItem(world, "Forcing " + event.getState() + " spread onto stone at " + spreadPos);
                    event.setResult(Result.ALLOW);
                }
            }
        }

        if (MUSHROOM_TEST && block instanceof MushroomBlock) {
            sendPreSpreadMessage(event);
            //force spreading mushrooms on dirt
            if (world.isAirBlock(spreadPos) && blockBelowSpreadBlock == Blocks.DIRT) {
                sendMessageToAllPlayersWithDebugItem(world, "Forcing " + event.getState() + " spread onto dirt at " + spreadPos);
                event.setResult(Result.ALLOW);
            }
        }

        if (SEA_PICKLE_TEST && block == Blocks.SEA_PICKLE) {
            sendPreSpreadMessage(event);
            //force spreading sea pickles on diamond blocks
            if (spreadBlock == Blocks.WATER && blockBelowSpreadBlock == Blocks.DIAMOND_BLOCK) {
                sendMessageToAllPlayersWithDebugItem(world, "Forcing " + event.getState() + " spread onto diamond block at " + spreadPos);
                event.setResult(Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public static void onPossibleSpreadEventPost(PossibleSpreadEvent.Post event) {
        Block block = event.getState().getBlock();
        if ((GRASS_TEST && block == Blocks.GRASS_BLOCK)
                || (MYCELIUM_TEST && block == Blocks.MYCELIUM)
                || (MUSHROOM_TEST && block instanceof MushroomBlock)
                || (SEA_PICKLE_TEST && block == Blocks.SEA_PICKLE)) {
            sendPostSpreadMessage(event);
        }
    }
}
