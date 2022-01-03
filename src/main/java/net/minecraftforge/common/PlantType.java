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

package net.minecraftforge.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class PlantType
{
    private static final Pattern INVALID_CHARACTERS = Pattern.compile("[^a-z_]"); //Only a-z and _ are allowed, meaning names must be lower case. And use _ to separate words.
    private static final Map<String, PlantType> VALUES = new ConcurrentHashMap<>();

    public static final PlantType PLAINS = get("plains", (state, world, pos, facing, plantable) ->
    {
        return state.is(Blocks.GRASS_BLOCK) || state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND);
    });
    public static final PlantType DESERT = get("desert", (state, world, pos, facing, plantable) ->
    {
        return state.is(Blocks.SAND) || state.is(Blocks.TERRACOTTA) || state.getBlock() instanceof GlazedTerracottaBlock;
    });
    public static final PlantType BEACH = get("beach", (state, world, pos, facing, plantable) ->
    {
        boolean isBeach = state.is(Blocks.GRASS_BLOCK) || state.is(Tags.Blocks.DIRT) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
        boolean hasWater = false;
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockState blockState = world.getBlockState(pos.relative(face));
            net.minecraft.world.level.material.FluidState fluidState = world.getFluidState(pos.relative(face));
            hasWater |= blockState.is(Blocks.FROSTED_ICE);
            hasWater |= fluidState.is(net.minecraft.tags.FluidTags.WATER);
            if (hasWater)
                break;
        }
        return isBeach && hasWater;
    });
    public static final PlantType CAVE = get("cave", (state, world, pos, facing, plantable) ->
    {
        return state.isFaceSturdy(world, pos, Direction.UP);
    });
    public static final PlantType WATER = get("water", (state, world, pos, facing, plantable) ->
    {
        return state.getMaterial() == Material.WATER;
    });
    public static final PlantType NETHER = get("nether", (state, world, pos, facing, plantable) ->
    {
        return state.getBlock() == Blocks.SOUL_SAND;
    });
    public static final PlantType CROP = get("crop", (state, world, pos, facing, plantable) ->
    {
        return state.is(Blocks.FARMLAND);
    });
    public static final PlantType BUSH = get("bush", (state, world, pos, facing, plantable) ->
    {
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND);
    });
    public static final PlantType AZELEA = get("azelea", (state, world, pos, facing, plantable) ->
    {
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND) || BUSH.mayPlace(state, world, pos, facing, plantable);
    });
    public static final PlantType DEAD_BUSH = get("dead_bush", (state, world, pos, facing, plantable) ->
    {
        // Changed whatever was in DeadBushBlock to BlockTags.TERRACOTTA
        return state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(BlockTags.TERRACOTTA);
    });
    public static final PlantType FUNGUS = get("fungus", (state, world, pos, facing, plantable) ->
    {
        return state.is(BlockTags.NYLIUM) || state.is(Blocks.MYCELIUM) || state.is(Blocks.SOUL_SOIL) || BUSH.mayPlace(state, world, pos, facing, plantable);
    });
    public static final PlantType MUSHROOM = get("mushroom", (state, world, pos, facing, plantable) ->
    {
        return state.isSolidRender(world, pos);
    });
    public static final PlantType NETHER_PLANTS = get("nether_sprout", (state, world, pos, facing, plantable) ->
    {
        return state.is(BlockTags.NYLIUM) || state.is(Blocks.SOUL_SOIL) || BUSH.mayPlace(state, world, pos, facing, plantable);
    });
    public static final PlantType SEAGRASS = get("seagrass", (state, world, pos, facing, plantable) ->
    {
        return state.isFaceSturdy(world, pos, Direction.UP) && !state.is(Blocks.MAGMA_BLOCK);
    });
    public static final PlantType SEA_PICKLE = get("sea_pickle", (state, world, pos, facing, plantable) ->
    {
        return !state.getCollisionShape(world, pos).getFaceShape(Direction.UP).isEmpty() || state.isFaceSturdy(world, pos, Direction.UP);
    });
    public static final PlantType SMALL_DRIPLEAF = get("small_dripleaf", (state, world, pos, facing, plantable) ->
    {
        return state.is(BlockTags.SMALL_DRIPLEAF_PLACEABLE) || world.getFluidState(pos.above()).isSourceOfType(Fluids.WATER) && BUSH.mayPlace(state, world, pos, facing, plantable);
    });
    public static final PlantType WATERLILY = get("waterlily", (state, world, pos, facing, plantable) ->
    {
        FluidState fluidstate = world.getFluidState(pos);
        FluidState fluidstate1 = world.getFluidState(pos.above());
        return (fluidstate.getType() == Fluids.WATER || state.getMaterial() == Material.ICE) && fluidstate1.getType() == Fluids.EMPTY;
    });
    public static final PlantType WITHER_ROSE = get("wither_rose", (state, world, pos, facing, plantable) ->
    {
        return PLAINS.mayPlace(state, world, pos, facing, plantable) || state.is(Blocks.NETHERRACK) || state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL);
    });
    public static final PlantType CACTUS = get("cactus", (state, world, pos, facing, plantable) ->
    {
        return state.is(Blocks.CACTUS) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
    });
    public static final PlantType SUGAR_CANE = get("sugar_cane", (state, world, pos, facing, plantable) ->
    {
        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        return BEACH.mayPlace(state, world, pos, facing, plantable) || (plant.is(Blocks.SUGAR_CANE) && state.is(Blocks.SUGAR_CANE));
    });
    /**
     * Getting a custom {@link PlantType}, or an existing one if it has the same name as that one. Your plant should implement {@link IPlantable}
     * and return this custom type in {@link IPlantable#getPlantType(BlockGetter, BlockPos)}.
     *
     * <p>If your new plant grows on blocks like any one of them above, never create a new {@link PlantType}.
     * This Type is only functioning in
     * {@link Block#canSustainPlant(BlockState, BlockGetter, BlockPos, Direction, IPlantable)},
     * which you are supposed to override this function in your new block and create a new plant type to grow on that block.
     *
     * This method can be called during parallel loading
     * @param name the name of the type of plant, you had better follow the style above
     * @return the acquired {@link PlantType}, a new one if not found.
     */
    public static PlantType get(String name)
    {
        return VALUES.computeIfAbsent(name, e ->
        {
            if (INVALID_CHARACTERS.matcher(e).find())
                throw new IllegalArgumentException("PlantType.get() called with invalid name: " + name);
            return new PlantType(e);
        });
    }

    public static PlantType get(String name, PlantTypePredicate mayPlace)
    {
        return VALUES.computeIfAbsent(name, e ->
        {
            if (INVALID_CHARACTERS.matcher(e).find())
                throw new IllegalArgumentException("PlantType.get() called with invalid name: " + name);
            return new PlantType(e, mayPlace);
        });
    }

    private final String name;
    public final PlantTypePredicate mayPlace;

    private PlantType(String name)
    {
        this.name = name;
        this.mayPlace = (t, u, v, w, x) -> true;
    }

    private PlantType(String name, PlantTypePredicate predicate)
    {
        this.name = name;
        this.mayPlace = predicate;
    }

    public String getName()
    {
        return name;
    }

    public boolean mayPlace(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable)
    {
        return mayPlace.test(state, world, pos, facing, plantable);
    }

    @FunctionalInterface
    public interface PlantTypePredicate
    {
        boolean test(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable);

        default PlantTypePredicate and(PlantTypePredicate other) {
            Objects.requireNonNull(other);
            return (state, world, pos, facing, plantable) -> test(state, world, pos, facing, plantable) && other.test(state, world, pos, facing, plantable);
        }

        default PlantTypePredicate negate() {
            return (BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) -> !test(state, world, pos, facing, plantable);
        }

        default PlantTypePredicate or(PlantTypePredicate other) {
            Objects.requireNonNull(other);
            return (state, world, pos, facing, plantable) -> test(state, world, pos, facing, plantable) || other.test(state, world, pos, facing, plantable);
        }
    }

}
