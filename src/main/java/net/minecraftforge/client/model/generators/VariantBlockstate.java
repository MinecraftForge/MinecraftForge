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
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static net.minecraftforge.client.model.generators.BlockstateProvider.ConfiguredModelList;

public class VariantBlockstate implements IGeneratedBlockstate {
    private final Block owner;
    private final Map<PartialBlockstate, ConfiguredModelList> models = new HashMap<>();
    private final Set<BlockState> coveredStates = new HashSet<>();

    public VariantBlockstate(Block owner) {
        this.owner = owner;
    }

    public Map<PartialBlockstate, ConfiguredModelList> getModels() {
        return models;
    }

    public Block getOwner() {
        return owner;
    }

    @Override
    public JsonObject toJson() {
        Preconditions.checkState(coveredStates.size()==owner.getStateContainer().getValidStates().size());
        JsonObject variants = new JsonObject();
        for (Map.Entry<PartialBlockstate, ConfiguredModelList> entry : getModels().entrySet()) {
            variants.add(entry.getKey().toString(), entry.getValue().toJSON());
        }
        JsonObject main = new JsonObject();
        main.add("variants", variants);
        return main;
    }
    
    public VariantBlockstate addModels(PartialBlockstate state, ConfiguredModel... model) {
        if (!models.containsKey(state)) {
            return setModels(state, model);
        }
        Preconditions.checkNotNull(state);
        Preconditions.checkArgument(model.length > 0);
        Preconditions.checkArgument(state.getOwner() == owner);
        models.compute(state, ($, cml) -> cml.append(model));
        return this;
    }

    public VariantBlockstate setModels(PartialBlockstate state, ConfiguredModel... model) {
        Preconditions.checkNotNull(state);
        Preconditions.checkArgument(model.length > 0);
        Preconditions.checkArgument(state.getOwner() == owner);
        Preconditions.checkArgument(disjointToAll(state));
        models.put(state, new ConfiguredModelList(model));
        for (BlockState fullState : owner.getStateContainer().getValidStates()) {
            if (state.test(fullState)) {
                coveredStates.add(fullState);
            }
        }
        return this;
    }

    private boolean disjointToAll(PartialBlockstate newState) {
        return coveredStates.stream().noneMatch(newState);
    }

    public PartialBlockstate partialState() {
        return new PartialBlockstate(owner, this);
    }

    public static class PartialBlockstate implements Predicate<BlockState> {
        private final Block owner;
        private final Map<IProperty<?>, Comparable<?>> setStates;
        @Nullable
        private final VariantBlockstate outerBuilder;

        public PartialBlockstate(Block owner, @Nullable VariantBlockstate outerBuilder) {
            this(owner, ImmutableMap.of(), outerBuilder);
        }

        public PartialBlockstate(Block owner, Map<IProperty<?>, Comparable<?>> setStates, @Nullable VariantBlockstate outerBuilder) {
            this.owner = owner;
            this.outerBuilder = outerBuilder;
            for (Map.Entry<IProperty<?>, Comparable<?>> entry : setStates.entrySet()) {
                IProperty<?> prop = entry.getKey();
                Comparable<?> value = entry.getValue();
                Preconditions.checkArgument(owner.getStateContainer().getProperties().contains(prop), "Property " + entry + " not found on block " + this.owner);
                Preconditions.checkArgument(prop.getAllowedValues().contains(value), value + " is not a valid value for " + prop);
            }
            this.setStates = setStates;
        }

        public <T extends Comparable<T>> PartialBlockstate with(IProperty<T> prop, T value) {
            Preconditions.checkArgument(!setStates.containsKey(prop), "Property " + prop + " has already been set");
            Map<IProperty<?>, Comparable<?>> newState = new HashMap<>(setStates);
            newState.put(prop, value);
            return new PartialBlockstate(owner, newState, outerBuilder);
        }

        public ConfiguredModel.Builder<VariantBlockstate> modelForState() {
            return ConfiguredModel.builder(outerBuilder, this);
        }
        
        public PartialBlockstate addModels(ConfiguredModel... models) {
            Preconditions.checkNotNull(outerBuilder);
            outerBuilder.addModels(this, models);
            return this;
        }

        public VariantBlockstate setModels(ConfiguredModel... models) {
            Preconditions.checkNotNull(outerBuilder);
            return outerBuilder.setModels(this, models);
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
            if (blockState.getBlock() != getOwner()) {
                return false;
            }
            for (Map.Entry<IProperty<?>, Comparable<?>> entry : setStates.entrySet()) {
                if (blockState.get(entry.getKey()) != entry.getValue()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder();
            for (Map.Entry<IProperty<?>, Comparable<?>> entry : setStates.entrySet()) {
                if (ret.length() > 0) {
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