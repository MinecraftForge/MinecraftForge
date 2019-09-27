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
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraftforge.client.model.generators.BlockstateProvider.ConfiguredModel;

import java.util.*;
import java.util.function.Predicate;

import static net.minecraftforge.client.model.generators.BlockstateProvider.*;

public class VariantBlockstate {
    private final Block owner;
    private final Map<PartialBlockstate, ConfiguredModelList> models;

    public VariantBlockstate(Block owner, Map<PartialBlockstate, ConfiguredModelList> models) {
        this.owner = owner;
        this.models = models;
        for (BlockState state:owner.getStateContainer().getValidStates()) {
            PartialBlockstate matching = null;
            for (PartialBlockstate partialState:models.keySet()) {
                if (partialState.test(state)) {
                    Preconditions.checkState(matching==null, "Both "+matching+" and "+partialState+" match "+state);
                    matching = partialState;
                }
            }
            Preconditions.checkState(matching!=null, "No partial state matches "+state);
        }
    }

    public Map<PartialBlockstate, ConfiguredModelList> getModels() {
        return models;
    }

    public Block getOwner() {
        return owner;
    }

    public static class Builder {
        private final Block owner;
        private final Map<PartialBlockstate, ConfiguredModelList> models = new HashMap<>();
        private final Set<BlockState> coveredStates = new HashSet<>();

        public Builder(Block owner) {
            this.owner = owner;
        }

        public Builder setModel(PartialBlockstate state, ConfiguredModel... model) {
            Preconditions.checkNotNull(state);
            Preconditions.checkArgument(model.length>0);
            Preconditions.checkArgument(state.getOwner() == owner);
            Preconditions.checkArgument(disjointToAll(state));
            models.put(state, new ConfiguredModelList(model));
            for (BlockState fullState: owner.getStateContainer().getValidStates()) {
                if (state.test(fullState)) {
                    coveredStates.add(fullState);
                }
            }
            return this;
        }

        private boolean disjointToAll(PartialBlockstate newState) {
            return coveredStates.stream().noneMatch(newState);
        }

        public VariantBlockstate build() {
            return new VariantBlockstate(owner, models);
        }
    }
    //TODO a weird compiler issue happened here, didn't work without the full name
    public static class PartialBlockstate implements java.util.function.Predicate<BlockState> {
        private final Block owner;
        private final Map<IProperty<?>, Comparable<?>> setStates;
        public PartialBlockstate(Block owner) {
           this(owner, ImmutableMap.of());
        }

        public PartialBlockstate(Block owner, Map<IProperty<?>, Comparable<?>> setStates) {
           this.owner = owner;
           for (Map.Entry<IProperty<?>, Comparable<?>> entry:setStates.entrySet()) {
               IProperty<?> prop = entry.getKey();
               Comparable<?> value = entry.getValue();
               Preconditions.checkArgument(owner.getStateContainer().getProperties().contains(prop), "Property "+entry+" not found on block "+ this.owner);
               Preconditions.checkArgument(prop.getAllowedValues().contains(value), value+" is not a valid value for "+prop);
           }
           this.setStates = setStates;
        }

        public <T extends Comparable<T>> PartialBlockstate with(IProperty<T> prop, T value) {
            Preconditions.checkArgument(!setStates.containsKey(prop), "Property "+prop+" has already been set");
            Map<IProperty<?>, Comparable<?>> newState = new HashMap<>(setStates);
            newState.put(prop, value);
            return new PartialBlockstate(owner, newState);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PartialBlockstate that = (PartialBlockstate) o;
            return owner.equals(that.owner) &&
                    setStates.equals(that.setStates);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, setStates);
        }

        public Block getOwner() {
            return owner;
        }

        public Map<IProperty<?>, Comparable<?>> getSetStates() {
            return setStates;
        }

        @Override
        public boolean test(BlockState blockState) {
           if (blockState.getBlock()!=getOwner()) {
               return false;
           }
           for (Map.Entry<IProperty<?>, Comparable<?>> entry:setStates.entrySet()) {
              if (blockState.get(entry.getKey())!=entry.getValue()) {
                  return false;
              }
           }
           return true;
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder();
            for (Map.Entry<IProperty<?>, Comparable<?>> entry:setStates.entrySet()) {
                if (ret.length()>0) {
                    ret.append(',');
                }
                ret.append(entry.getKey().getName())
                        .append('=')
                        .append(entry.getValue());
            }
            return ret.toString();
        }
    }
}
