/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * A class containing constants for magic numbers used in the minecraft codebase.
 * Everything here should be checked each update, and have a comment relating to where to check it.
 */
public class Constants
{
    /**
     * NBT Tag type IDS, used when storing the nbt to disc, Should align with {@link net.minecraft.nbt.INBT#create}
     * and {@link net.minecraft.nbt.INBT#getTypeName}
     *
     * Main use is checking tag type in {@link net.minecraft.nbt.CompoundNBT#contains(String, int)}
     *
     */
    public static class NBT
    {
        public static final int TAG_END         = 0;
        public static final int TAG_BYTE        = 1;
        public static final int TAG_SHORT       = 2;
        public static final int TAG_INT         = 3;
        public static final int TAG_LONG        = 4;
        public static final int TAG_FLOAT       = 5;
        public static final int TAG_DOUBLE      = 6;
        public static final int TAG_BYTE_ARRAY  = 7;
        public static final int TAG_STRING      = 8;
        public static final int TAG_LIST        = 9;
        public static final int TAG_COMPOUND    = 10;
        public static final int TAG_INT_ARRAY   = 11;
        public static final int TAG_LONG_ARRAY  = 12;
        public static final int TAG_ANY_NUMERIC = 99;
    }

    /**
     * The world event IDS, used when calling {@link IWorld#playEvent(int, BlockPos, int)}. <br>
     * Can be found from {@link net.minecraft.client.renderer.WorldRenderer#playEvent}<br>
     * Some of the events use the {@code data} parameter. If this is the case, an explanation of what {@code data} does is also provided
     */
    public static class WorldEvents {
        public static final int DISPENSER_DISPENSE_SOUND        = 1000;
        public static final int DISPENSER_FAIL_SOUND            = 1001;
        /**
         * Like DISPENSER_DISPENSE_SOUND, but for items that are fired (arrows, eggs, snowballs)
         */
        public static final int DISPENSER_LAUNCH_SOUND          = 1002;
        public static final int ENDEREYE_LAUNCH_SOUND           = 1003;
        public static final int FIREWORK_SHOOT_SOUND            = 1004;
        public static final int IRON_DOOR_OPEN_SOUND            = 1005;
        public static final int WOODEN_DOOR_OPEN_SOUND          = 1006;
        public static final int WOODEN_TRAPDOOR_OPEN_SOUND      = 1007;
        public static final int FENCE_GATE_OPEN_SOUND           = 1008;
        public static final int FIRE_EXTINGUISH_SOUND           = 1009;
        /**
         * {@code data} is the item ID of the record you want to play
         */
        public static final int PLAY_RECORD_SOUND               = 1010;
        public static final int IRON_DOOR_CLOSE_SOUND           = 1011;
        public static final int WOODEN_DOOR_CLOSE_SOUND         = 1012;
        public static final int WOODEN_TRAPDOOR_CLOSE_SOUND     = 1013;
        public static final int FENCE_GATE_CLOSE_SOUND          = 1014;
        public static final int GHAST_WARN_SOUND                = 1015;
        public static final int GHAST_SHOOT_SOUND               = 1016;
        public static final int ENDERDRAGON_SHOOT_SOUND         = 1017;
        public static final int BLAZE_SHOOT_SOUND               = 1018;
        public static final int ZOMBIE_ATTACK_DOOR_WOOD_SOUND   = 1019;
        public static final int ZOMBIE_ATTACK_DOOR_IRON_SOUND   = 1020;
        public static final int ZOMBIE_BREAK_DOOR_WOOD_SOUND    = 1021;
        public static final int WITHER_BREAK_BLOCK_SOUND        = 1022;
        public static final int WITHER_BREAK_BLOCK              = 1023;
        public static final int WITHER_SHOOT_SOUND              = 1024;
        public static final int BAT_TAKEOFF_SOUND               = 1025;
        public static final int ZOMBIE_INFECT_SOUND             = 1026;
        public static final int ZOMBIE_VILLAGER_CONVERTED_SOUND = 1027;
        public static final int ANVIL_DESTROYED_SOUND           = 1029;
        public static final int ANVIL_USE_SOUND                 = 1030;
        public static final int ANVIL_LAND_SOUND                = 1031;
        public static final int PORTAL_TRAVEL_SOUND             = 1032;
        public static final int CHORUS_FLOWER_GROW_SOUND        = 1033;
        public static final int CHORUS_FLOWER_DEATH_SOUND       = 1034;
        public static final int BREWING_STAND_BREW_SOUND        = 1035;
        public static final int IRON_TRAPDOOR_CLOSE_SOUND       = 1036;
        public static final int IRON_TRAPDOOR_OPEN_SOUND        = 1037;
        public static final int PHANTOM_BITE_SOUND              = 1039;
        public static final int ZOMBIE_CONVERT_TO_DROWNED_SOUND = 1040;
        public static final int HUSK_CONVERT_TO_ZOMBIE_SOUND    = 1041;
        public static final int GRINDSTONE_USE_SOUND            = 1042;
        public static final int ITEM_BOOK_TURN_PAGE_SOUND       = 1043;
        /**
         * Spawns the composter particles and plays the sound event sound event<br>
         * {@code data} is bigger than 0 when the composter can still be filled up, and is smaller or equal to 0 when the composter is full. (This only effects the sound event)
         */
        public static final int COMPOSTER_FILLED_UP             = 1500;
        public static final int LAVA_EXTINGUISH                 = 1501;
        public static final int REDSTONE_TORCH_BURNOUT          = 1502;
        public static final int END_PORTAL_FRAME_FILL           = 1503;
        /**
         * {@code data} is the {@link Direction#getIndex()} of the direction the smoke is to come out of.
         */
        public static final int DISPENSER_SMOKE                 = 2000;

        /**
         * {@code data} is the {@link net.minecraft.block.Block#getStateId state id} of the block broken
         */
        public static final int BREAK_BLOCK_EFFECTS             = 2001;
        /**
         * {@code data} is the rgb color int that should be used for the potion particles<br>
         * This is the same as {@link Constants.WorldEvents#POTION_IMPACT} but uses the particle type {@link ParticleTypes#EFFECT}
         */
        public static final int POTION_IMPACT_INSTANT           = 2002;
        public static final int ENDER_EYE_SHATTER               = 2003;
        public static final int MOB_SPAWNER_PARTICLES           = 2004;
        /**
         * {@code data} is the amount of particles to spawn. If {@code data} is 0 then there will be 15 particles spawned
         */
        public static final int BONEMEAL_PARTICLES              = 2005;
        public static final int DRAGON_FIREBALL_HIT             = 2006;
        /**
         * {@code data} is the rgb color int that should be used for the potion particles<br>
         * This is the same as {@link Constants.WorldEvents#POTION_IMPACT_INSTANT} but uses the particle type {@link ParticleTypes#INSTANT_EFFECT}
         */
        public static final int POTION_IMPACT                   = 2007;
        public static final int SPAWN_EXPLOSION_PARTICLE        = 2008;
        public static final int GATEWAY_SPAWN_EFFECTS           = 3000;
        public static final int ENDERMAN_GROWL_SOUND            = 3001;
    }


    /**
     * The flags used when calling
     * {@link net.minecraft.world.IWorldWriter#setBlockState(BlockPos, BlockState, int)}<br>
     * Can be found from {@link World#setBlockState(BlockPos, BlockState, int)},
     * {@link World#markAndNotifyBlock}, and
     * {@link WorldRenderer#notifyBlockUpdate}<br>
     * Flags can be combined with bitwise OR
     */
    public static class BlockFlags {
        /**
         * Calls
         * {@link Block#neighborChanged(BlockState, World, BlockPos, Block, BlockPos, boolean)
         * neighborChanged} on surrounding blocks (with isMoving as false). Also updates comparator output state.
         */
        public static final int NOTIFY_NEIGHBORS     = (1 << 0);
        /**
         * Calls {@link World#notifyBlockUpdate(BlockPos, BlockState, BlockState, int)}.<br>
         * Server-side, this updates all the path-finding navigators.
         */
        public static final int BLOCK_UPDATE         = (1 << 1);
        /**
         * Stops the blocks from being marked for a render update
         */
        public static final int NO_RERENDER          = (1 << 2);
        /**
         * Makes the block be re-rendered immediately, on the main thread.
         * If NO_RERENDER is set, then this will be ignored
         */
        public static final int RERENDER_MAIN_THREAD = (1 << 3);
        /**
         * Causes neighbor updates to be sent to all surrounding blocks (including
         * diagonals). This in turn will call
         * {@link Block#updateDiagonalNeighbors(BlockState, IWorld, BlockPos, int)
         * updateDiagonalNeighbors} on both old and new states, and
         * {@link Block#updateNeighbors(BlockState, IWorld, BlockPos, int)
         * updateNeighbors} on the new state.
         */
        public static final int UPDATE_NEIGHBORS     = (1 << 4);

        /**
         * Prevents neighbor changes from spawning item drops, used by
         * {@link Block#replaceBlock(BlockState, BlockState, IWorld, BlockPos, int)}.
         */
        public static final int NO_NEIGHBOR_DROPS    = (1 << 5);

        /**
         * Tell the block being changed that it was moved, rather than removed/replaced,
         * the boolean value is eventually passed to
         * {@link Block#onReplaced(BlockState, World, BlockPos, BlockState, boolean)}
         * as the last parameter.
         */
        public static final int IS_MOVING            = (1 << 6);

        public static final int DEFAULT = NOTIFY_NEIGHBORS | BLOCK_UPDATE;
        public static final int DEFAULT_AND_RERENDER = DEFAULT | RERENDER_MAIN_THREAD;
    }
}
