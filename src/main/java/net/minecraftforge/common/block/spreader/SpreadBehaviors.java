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

package net.minecraftforge.common.block.spreader;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.extensions.IForgeSpreadingBlock;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SpreadBehaviors
{

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<IRegistryDelegate<Block>, Map<SpreaderType, ISpreadingBehavior>> SPREADERS = new HashMap<>();

    static
    {
        setupVanillaBehavior();
    }

    /**
     * This method allows you to add spreading behavior, which is used by {@link IForgeSpreadingBlock}
     *
     * @param block
     * @param type
     * @param behavior
     */
    public static void addSpreaderBehavior(Block block, SpreaderType type, ISpreadingBehavior behavior)
    {
        Map<SpreaderType, ISpreadingBehavior> blockSpecificSpreaders = SPREADERS.computeIfAbsent(block.delegate, key -> new HashMap<>());
        if (blockSpecificSpreaders.containsKey(type))
            LOGGER.info("Replacing spreading behavior for block '{}' and spreader type '{}'", block.getRegistryName(), type.getName());
        blockSpecificSpreaders.put(type, behavior);
    }

    public static boolean canSpread(BlockState state, SpreaderType type)
    {
        return getSpreadingBehavior(state.getBlock(), type) != null;
    }

    public static BlockState getSpreadState(BlockState state, Level level, BlockPos pos, SpreaderType type)
    {
        ISpreadingBehavior behavior = getSpreadingBehavior(state.getBlock(), type);
        if (behavior == null) return state;
        return behavior.getSpreadingState(state, level, pos);
    }

    @Nullable
    private static ISpreadingBehavior getSpreadingBehavior(Block block, SpreaderType type)
    {
        return SPREADERS.containsKey(block.delegate) ? SPREADERS.get(block.delegate).get(type) : null;
    }

    private static void setupVanillaBehavior()
    {
        addSpreaderBehavior(Blocks.DIRT, SpreaderType.GRASS, ((state, level, pos) ->
            Blocks.GRASS_BLOCK.defaultBlockState()
                .setValue(BlockStateProperties.SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW))));

        addSpreaderBehavior(Blocks.GRASS_BLOCK, SpreaderType.REVERT, ((state, level, pos) ->
            Blocks.DIRT.defaultBlockState()));

        addSpreaderBehavior(Blocks.DIRT, SpreaderType.MYCELIUM, ((state, level, pos) ->
            Blocks.MYCELIUM.defaultBlockState()
                .setValue(BlockStateProperties.SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW))));

        addSpreaderBehavior(Blocks.MYCELIUM, SpreaderType.REVERT, ((state, level, pos) ->
            Blocks.DIRT.defaultBlockState()));

        addSpreaderBehavior(Blocks.NETHERRACK, SpreaderType.CRIMSON, ((state, level, pos) ->
            Blocks.CRIMSON_NYLIUM.defaultBlockState()));

        addSpreaderBehavior(Blocks.CRIMSON_NYLIUM, SpreaderType.REVERT, ((state, level, pos) ->
            Blocks.NETHERRACK.defaultBlockState()));

        addSpreaderBehavior(Blocks.NETHERRACK, SpreaderType.WARPED, ((state, level, pos) ->
            Blocks.WARPED_NYLIUM.defaultBlockState()));

        addSpreaderBehavior(Blocks.WARPED_NYLIUM, SpreaderType.REVERT, ((state, level, pos) ->
            Blocks.NETHERRACK.defaultBlockState()));
    }
}
