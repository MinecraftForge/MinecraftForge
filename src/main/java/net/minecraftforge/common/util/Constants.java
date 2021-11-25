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

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * A class containing constants for magic numbers used in the minecraft codebase.
 * Everything here should be checked each update, and have a comment relating to where to check it.
 *
 * @deprecated No longer needed. See inner classes for replacements.
 * TODO Remove in 1.18
 */
@Deprecated(since = "1.17", forRemoval = true)
public class Constants
{
    /**
     * NBT Tag type IDS, used when storing the nbt to disc, Should align with {@link TagTypes#getType(int)}
     * and {@link TagType#getPrettyName()}
     *
     * Main use is checking tag type in {@link CompoundTag#contains(String, int)}
     *
     * @deprecated Replaced by the constants in {@link net.minecraft.nbt.Tag}.
     * TODO Remove in 1.18
     */
    @Deprecated(since = "1.17", forRemoval = true)
    public static class NBT
    {
        public static final int TAG_END         = Tag.TAG_END;
        public static final int TAG_BYTE        = Tag.TAG_BYTE;
        public static final int TAG_SHORT       = Tag.TAG_SHORT;
        public static final int TAG_INT         = Tag.TAG_INT;
        public static final int TAG_LONG        = Tag.TAG_LONG;
        public static final int TAG_FLOAT       = Tag.TAG_FLOAT;
        public static final int TAG_DOUBLE      = Tag.TAG_DOUBLE;
        public static final int TAG_BYTE_ARRAY  = Tag.TAG_BYTE_ARRAY;
        public static final int TAG_STRING      = Tag.TAG_STRING;
        public static final int TAG_LIST        = Tag.TAG_LIST;
        public static final int TAG_COMPOUND    = Tag.TAG_COMPOUND;
        public static final int TAG_INT_ARRAY   = Tag.TAG_INT_ARRAY;
        public static final int TAG_LONG_ARRAY  = Tag.TAG_LONG_ARRAY;
        public static final int TAG_ANY_NUMERIC = Tag.TAG_ANY_NUMERIC;
    }

    /**
     * The world event IDS, used when calling {@link Level#globalLevelEvent(int, BlockPos, int)}. <br>
     * Can be found from {@link LevelRenderer#globalLevelEvent(int, BlockPos, int)}<br>
     * Some of the events use the {@code data} parameter. If this is the case, an explanation of what {@code data} does is also provided
     *
     * @deprecated Replaced by the constants in {@link net.minecraft.world.level.block.LevelEvent}.
     * TODO Remove in 1.18
     */
    @Deprecated(since = "1.17", forRemoval = true)
    public static class WorldEvents {
        public static final int DISPENSER_DISPENSE_SOUND        = LevelEvent.SOUND_DISPENSER_DISPENSE;
        public static final int DISPENSER_FAIL_SOUND            = LevelEvent.SOUND_DISPENSER_FAIL;
        /**
         * Like DISPENSER_DISPENSE_SOUND, but for items that are fired (arrows, eggs, snowballs)
         */
        public static final int DISPENSER_LAUNCH_SOUND          = LevelEvent.SOUND_DISPENSER_PROJECTILE_LAUNCH;
        public static final int ENDEREYE_LAUNCH_SOUND           = LevelEvent.SOUND_ENDER_EYE_LAUNCH;
        public static final int FIREWORK_SHOOT_SOUND            = LevelEvent.SOUND_FIREWORK_SHOOT;
        public static final int IRON_DOOR_OPEN_SOUND            = LevelEvent.SOUND_OPEN_IRON_DOOR;
        public static final int WOODEN_DOOR_OPEN_SOUND          = LevelEvent.SOUND_OPEN_WOODEN_DOOR;
        public static final int WOODEN_TRAPDOOR_OPEN_SOUND      = LevelEvent.SOUND_OPEN_WOODEN_TRAP_DOOR;
        public static final int FENCE_GATE_OPEN_SOUND           = LevelEvent.SOUND_OPEN_FENCE_GATE;
        public static final int FIRE_EXTINGUISH_SOUND           = LevelEvent.SOUND_EXTINGUISH_FIRE;
        /**
         * {@code data} is the item ID of the record you want to play
         */
        public static final int PLAY_RECORD_SOUND               = LevelEvent.SOUND_PLAY_RECORDING;
        public static final int IRON_DOOR_CLOSE_SOUND           = LevelEvent.SOUND_CLOSE_IRON_DOOR;
        public static final int WOODEN_DOOR_CLOSE_SOUND         = LevelEvent.SOUND_CLOSE_WOODEN_DOOR;
        public static final int WOODEN_TRAPDOOR_CLOSE_SOUND     = LevelEvent.SOUND_CLOSE_WOODEN_TRAP_DOOR;
        public static final int FENCE_GATE_CLOSE_SOUND          = LevelEvent.SOUND_CLOSE_FENCE_GATE;
        public static final int GHAST_WARN_SOUND                = LevelEvent.SOUND_GHAST_WARNING;
        public static final int GHAST_SHOOT_SOUND               = LevelEvent.SOUND_GHAST_FIREBALL;
        public static final int ENDERDRAGON_SHOOT_SOUND         = LevelEvent.SOUND_DRAGON_FIREBALL;
        public static final int BLAZE_SHOOT_SOUND               = LevelEvent.SOUND_BLAZE_FIREBALL;
        public static final int ZOMBIE_ATTACK_DOOR_WOOD_SOUND   = LevelEvent.SOUND_ZOMBIE_WOODEN_DOOR;
        public static final int ZOMBIE_ATTACK_DOOR_IRON_SOUND   = LevelEvent.SOUND_ZOMBIE_IRON_DOOR;
        public static final int ZOMBIE_BREAK_DOOR_WOOD_SOUND    = LevelEvent.SOUND_ZOMBIE_DOOR_CRASH;
        public static final int WITHER_BREAK_BLOCK_SOUND        = LevelEvent.SOUND_WITHER_BLOCK_BREAK;
        public static final int WITHER_BREAK_BLOCK              = LevelEvent.SOUND_WITHER_BOSS_SPAWN;
        public static final int WITHER_SHOOT_SOUND              = LevelEvent.SOUND_WITHER_BOSS_SHOOT;
        public static final int BAT_TAKEOFF_SOUND               = LevelEvent.SOUND_BAT_LIFTOFF;
        public static final int ZOMBIE_INFECT_SOUND             = LevelEvent.SOUND_ZOMBIE_INFECTED;
        public static final int ZOMBIE_VILLAGER_CONVERTED_SOUND = LevelEvent.SOUND_ZOMBIE_CONVERTED;
        public static final int ANVIL_DESTROYED_SOUND           = LevelEvent.SOUND_ANVIL_BROKEN;
        public static final int ANVIL_USE_SOUND                 = LevelEvent.SOUND_ANVIL_USED;
        public static final int ANVIL_LAND_SOUND                = LevelEvent.SOUND_ANVIL_LAND;
        public static final int PORTAL_TRAVEL_SOUND             = LevelEvent.SOUND_PORTAL_TRAVEL;
        public static final int CHORUS_FLOWER_GROW_SOUND        = LevelEvent.SOUND_CHORUS_GROW;
        public static final int CHORUS_FLOWER_DEATH_SOUND       = LevelEvent.SOUND_CHORUS_DEATH;
        public static final int BREWING_STAND_BREW_SOUND        = LevelEvent.SOUND_BREWING_STAND_BREW;
        public static final int IRON_TRAPDOOR_CLOSE_SOUND       = LevelEvent.SOUND_CLOSE_IRON_TRAP_DOOR;
        public static final int IRON_TRAPDOOR_OPEN_SOUND        = LevelEvent.SOUND_OPEN_IRON_TRAP_DOOR;
        public static final int PHANTOM_BITE_SOUND              = LevelEvent.SOUND_PHANTOM_BITE;
        public static final int ZOMBIE_CONVERT_TO_DROWNED_SOUND = LevelEvent.SOUND_ZOMBIE_TO_DROWNED;
        public static final int HUSK_CONVERT_TO_ZOMBIE_SOUND    = LevelEvent.SOUND_HUSK_TO_ZOMBIE;
        public static final int GRINDSTONE_USE_SOUND            = LevelEvent.SOUND_GRINDSTONE_USED;
        public static final int ITEM_BOOK_TURN_PAGE_SOUND       = LevelEvent.SOUND_PAGE_TURN;
        /**
         * Spawns the composter particles and plays the sound event sound event<br>
         * {@code data} is bigger than 0 when the composter can still be filled up, and is smaller or equal to 0 when the composter is full. (This only effects the sound event)
         */
        public static final int COMPOSTER_FILLED_UP             = LevelEvent.COMPOSTER_FILL;
        public static final int LAVA_EXTINGUISH                 = LevelEvent.LAVA_FIZZ;
        public static final int REDSTONE_TORCH_BURNOUT          = LevelEvent.REDSTONE_TORCH_BURNOUT;
        public static final int END_PORTAL_FRAME_FILL           = LevelEvent.END_PORTAL_FRAME_FILL;
        /**
         * {@code data} is the {@link Direction#get3DDataValue()} of the direction the smoke is to come out of.
         */
        public static final int DISPENSER_SMOKE                 = LevelEvent.PARTICLES_SHOOT;

        /**
         * {@code data} is the {@link Block#getId(BlockState)}  state id} of the block broken
         */
        public static final int BREAK_BLOCK_EFFECTS             = LevelEvent.PARTICLES_DESTROY_BLOCK;
        /**
         * {@code data} is the rgb color int that should be used for the potion particles<br>
         * This is the same as {@link Constants.WorldEvents#POTION_IMPACT} but uses the particle type {@link ParticleTypes#EFFECT}
         */
        public static final int POTION_IMPACT_INSTANT           = LevelEvent.PARTICLES_SPELL_POTION_SPLASH;
        public static final int ENDER_EYE_SHATTER               = LevelEvent.PARTICLES_EYE_OF_ENDER_DEATH;
        public static final int MOB_SPAWNER_PARTICLES           = LevelEvent.PARTICLES_MOBBLOCK_SPAWN;
        /**
         * {@code data} is the amount of particles to spawn. If {@code data} is 0 then there will be 15 particles spawned
         */
        public static final int BONEMEAL_PARTICLES              = LevelEvent.PARTICLES_PLANT_GROWTH;
        public static final int DRAGON_FIREBALL_HIT             = LevelEvent.PARTICLES_DRAGON_FIREBALL_SPLASH;
        /**
         * {@code data} is the rgb color int that should be used for the potion particles<br>
         * This is the same as {@link Constants.WorldEvents#POTION_IMPACT_INSTANT} but uses the particle type {@link ParticleTypes#INSTANT_EFFECT}
         */
        public static final int POTION_IMPACT                   = LevelEvent.PARTICLES_INSTANT_POTION_SPLASH;
        public static final int SPAWN_EXPLOSION_PARTICLE        = LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK;
        public static final int GATEWAY_SPAWN_EFFECTS           = LevelEvent.ANIMATION_END_GATEWAY_SPAWN;
        public static final int ENDERMAN_GROWL_SOUND            = LevelEvent.ANIMATION_DRAGON_SUMMON_ROAR;
    }


    /**
     * The flags used when calling
     * {@link LevelWriter#setBlock(BlockPos, BlockState, int)}<br>
     * Can be found from {@link Level#setBlock(BlockPos, BlockState, int)} ,
     * {@link Level#markAndNotifyBlock(BlockPos, LevelChunk, BlockState, BlockState, int, int)}, and
     * {@link LevelRenderer#blockChanged(BlockGetter, BlockPos, BlockState, BlockState, int)}<br>
     * Flags can be combined with bitwise OR
     *
     * @deprecated Replaced by the constants in {@link net.minecraft.world.level.block.Block}.
     * TODO Remove in 1.18
     */
    @Deprecated(since = "1.17", forRemoval = true)
    public static class BlockFlags {
        /**
         * Calls {@link Block#neighborChanged(BlockState, Level, BlockPos, Block, BlockPos, boolean)
         * neighborChanged} on surrounding blocks (with isMoving as false). Also updates comparator output state.
         */
        public static final int NOTIFY_NEIGHBORS     = Block.UPDATE_NEIGHBORS;
        /**
         * Calls {@link Level#sendBlockUpdated(BlockPos, BlockState, BlockState, int)}.<br>
         * Server-side, this updates all the path-finding navigators.
         */
        public static final int BLOCK_UPDATE         = Block.UPDATE_CLIENTS;
        /**
         * Stops the blocks from being marked for a render update
         */
        public static final int NO_RERENDER          = Block.UPDATE_INVISIBLE;
        /**
         * Makes the block be re-rendered immediately, on the main thread.
         * If NO_RERENDER is set, then this will be ignored
         */
        public static final int RERENDER_MAIN_THREAD = Block.UPDATE_IMMEDIATE;
        /**
         * Causes neighbor updates to be sent to all surrounding blocks (including
         * diagonals). This in turn will call
         * {@link Block#updateIndirectNeighbourShapes(BlockState, LevelAccessor, BlockPos, int, int)}
         * on both old and new states, and
         * {@link Block#updateOrDestroy(BlockState, BlockState, LevelAccessor, BlockPos, int, int)} on the new state.
         */
        public static final int UPDATE_NEIGHBORS     = Block.UPDATE_KNOWN_SHAPE;

        /**
         * Prevents neighbor changes from spawning item drops, used by
         * {@link Block#updateOrDestroy(BlockState, BlockState, LevelAccessor, BlockPos, int)}.
         */
        public static final int NO_NEIGHBOR_DROPS    = Block.UPDATE_SUPPRESS_DROPS;

        /**
         * Tell the block being changed that it was moved, rather than removed/replaced,
         * the boolean value is eventually passed to
         * {@link Block#onRemove(BlockState, Level, BlockPos, BlockState, boolean)}
         * as the last parameter.
         */
        public static final int IS_MOVING            = Block.UPDATE_MOVE_BY_PISTON;

        public static final int DEFAULT = Block.UPDATE_ALL;
        public static final int DEFAULT_AND_RERENDER = Block.UPDATE_ALL_IMMEDIATE;
    }
}
