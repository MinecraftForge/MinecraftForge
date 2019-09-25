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

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraftforge.client.model.generators.BlockstateProvider.ConfiguredModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class VariantBlockstate implements BlockstateProvider.IVariantModelGenerator {
    private final Map<BlockState, ConfiguredModel> models;

    private VariantBlockstate(Map<BlockState, ConfiguredModel> models) {
        this.models = models;
    }

    @Override
    public ConfiguredModel getModel(BlockState state) {
        return models.get(state);
    }

    public static class Builder {
        private final Block b;
        private final Map<BlockState, ConfiguredModel> models = new HashMap<>();

        public Builder(Block b) {
            this.b = b;
        }

        public Builder setModel(BlockState state, ConfiguredModel model) {
            Preconditions.checkNotNull(state);
            Preconditions.checkNotNull(model);
            Preconditions.checkArgument(state.getBlock() == b);
            Preconditions.checkArgument(!models.containsKey(state));
            models.put(state, model);
            return this;
        }

        public Builder setForAllMatching(Predicate<BlockState> matches, ConfiguredModel model) {
            Preconditions.checkNotNull(matches);
            for (BlockState state : b.getStateContainer().getValidStates())
                if (matches.test(state))
                    setModel(state, model);
            return this;
        }

        public <T extends Comparable<T>> Builder setForAllWithState(Map<IProperty<?>, ?> partialState, ConfiguredModel model) {
            Preconditions.checkNotNull(partialState);
            Preconditions.checkArgument(b.getStateContainer().getProperties().containsAll(partialState.keySet()));
            return setForAllMatching(blockState -> {
                for (IProperty<?> prop : partialState.keySet())
                    if (blockState.get(prop) != partialState.get(prop))
                        return false;
                return true;
            }, model);
        }

        public VariantBlockstate build() {
            for (BlockState state : b.getStateContainer().getValidStates())
                Preconditions.checkArgument(models.containsKey(state));
            return new VariantBlockstate(models);
        }
    }
}
