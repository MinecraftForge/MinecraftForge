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

package net.minecraftforge.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

/**
 * A class containing constants for magic numbers used in the minecraft codebase.
 * Everything here should be checked each update, and have a comment relating to where to check it.
 */
public class Constants
{
    /**
     * NBT Tag type IDS, used when storing the nbt to disc, Should align with NBTBase.getId,
     * table used in NBTBase.func_150283_g
     *
     * Main use is checking tag type in NBTTagCompound.contains(String, int)
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
     * The world event IDS, used when calling {@link net.minecraft.world.World#playEvent(net.minecraft.entity.player.EntityPlayer, int, net.minecraft.util.math.BlockPos, int)}. <br>
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
        /**
         * {@code data} is the direction of the smoke, as a grid around the dispenser as follows (with 4 being the position of the dispenser):
         * <pre>
         *        N
         *    0 | 1 | 2
         *    ----------
         * W  3 | 4 | 5  E
         *    ----------
         *    6 | 7 | 8
         *        S
         * </pre>
         * Setting the {@code data} to 4 will mean the particles won't spawn
         */
        public static final int DISPENSER_SMOKE                 = 2000;

        /**
         * {@code data} is the {@link net.minecraft.block.Block#getStateId state id} of the block broken
         */
        public static final int BREAK_BLOCK_EFFECTS             = 2001;
        /**
         * {@code data} is the rgb color int that should be used for the potion particles
         */
        public static final int SPLASH_POTION_EFFECT            = 2002;
        public static final int ENDER_EYE_SHATTER               = 2003;
        public static final int MOB_SPAWNER_PARTICLES           = 2004;
        /**
         * {@code data} is the amount of particles to spawn. If {@code data} is 0 then there will be 15 particles spawned
         */
        public static final int BONEMEAL_PARTICLES              = 2005;
        public static final int DRAGON_FIREBALL_HIT             = 2006;
        /**
         * {@code data} is the rgb color int that should be used for the potion particles
         */
        public static final int LINGERING_POTION_EFFECT         = 2007;
        public static final int GATEWAY_SPAWN_EFFECTS           = 3000;
        public static final int ENDERMAN_GROWL_SOUND            = 3001;
    }


    /**
     * The flags used when calling
     * {@link World#setBlockState(BlockPos, IBlockState, int)}<br>
     * Can be found from {@link World#setBlockState(BlockPos, IBlockState, int)},
     * {@link World#markAndNotifyBlock}, and
     * {@link WorldRenderer#notifyBlockUpdate}<br>
     * Flags can be combined with bitwise OR
     */
    public static class BlockFlags {
        /**
         * Calls
         * {@link Block#neighborChanged(IBlockState, World, BlockPos, Block, BlockPos)
         * neighborChanged} on surrounding blocks. Also updates comparator output state.
         */
        public static final int NOTIFY_NEIGHBORS     = (1 << 0);
        /**
         * Calls
         * {@link IWorldEventListener#notifyBlockUpdate(IBlockReader, BlockPos, IBlockState, IBlockState, int)}
         * to be called on all of the world's {@link IWorldEventListener event
         * listeners}. As a secondary effect, sends the update to the client.
         */
        public static final int NOTIFY_LISTENERS     = (1 << 1);
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
         * {@link Block#updateDiagonalNeighbors(IBlockState, IWorld, BlockPos, int)
         * updateDiagonalNeighbors} on both old and new states, and
         * {@link Block#updateNeighbors(IBlockState, IWorld, BlockPos, int)
         * updateNeighbors} on the new state.
         */
        public static final int UPDATE_NEIGHBORS     = (1 << 4);

        /**
         * Prevents neighbor changes from spawning item drops, used by
         * {@link Block#replaceBlock(IBlockState, IBlockState, IWorld, BlockPos, int)}.
         */
        public static final int NO_NEIGHBOR_DROPS    = (1 << 5);

        /**
         * Tell the block being changed that it was moved, rather than removed/replaced,
         * the boolean value is eventually passed to
         * {@link Block#onReplaced(IBlockState, World, BlockPos, IBlockState, boolean)}
         * as the last parameter.
         */
        public static final int IS_MOVING            = (1 << 6);

        public static final int DEFAULT = NOTIFY_NEIGHBORS | NOTIFY_LISTENERS;
        public static final int DEFAULT_AND_RERENDER = DEFAULT | RERENDER_MAIN_THREAD;
    }

    /**
     * The mutex bits used for AI tasks {@link net.minecraft.entity.ai.EntityAIBase#setMutexBits(int)}
     * Bits can be combined with bitwise OR
     */
    public static class AiMutexBits {
        public static final int MOVE = 0b001;
        public static final int LOOK = 0b010;
        public static final int JUMP = 0b100;
    }

    /**
     * The flags used for {@link net.minecraft.entity.Entity#getFlag(int)} and {@link net.minecraft.entity.Entity#setFlag(int, boolean)}}<br>
     * Can be found by searching for the usages of getFlag
     */
    public static class EntityFlags {
        public static final int BURNING       = 0;
        public static final int SNEAKING      = 1;
        public static final int SPRINTING     = 3;
        public static final int INVISIBLE     = 5;
        public static final int GLOWING       = 6;
        public static final int ELYTRA_FLYING = 7;
    }

    /**
     * The operation used for Attribute modifier operations, {@link net.minecraft.entity.ai.attributes.AttributeModifier#AttributeModifier(String, double, int)}<br>
     * Can be found at {@link ModifiableAttributeInstance#computeValue()}<br>
     * The total value starts with the base value.<br>
     * Order of operations are {@link #ADD}, {@link #ADD_MULTIPLE}, {@link #MULTIPLY}
     * @see <a href="https://minecraft.gamepedia.com/Attribute#Operations">Minecraft Wiki</a>
     */
    public static class AttributeModifierOperation {
        /**
         * The modifier value is added onto the total value
         */
        public static final int ADD          = 0;
        /**
         * The modifier value is multiplied by the original base value then added onto the total value
         */
        public static final int ADD_MULTIPLE = 1;
        /**
         * The total value is multiplied by 1 + the modifier value
         */
        public static final int MULTIPLY     = 2;
    }

}
