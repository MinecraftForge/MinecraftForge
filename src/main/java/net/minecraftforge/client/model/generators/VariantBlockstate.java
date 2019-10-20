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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraftforge.client.model.generators.BlockstateProvider.ConfiguredModelList;

public class VariantBlockstate implements IGeneratedBlockstate {

    private final Block owner;
    private final Map<PartialBlockstate, ConfiguredModelList> models = new LinkedHashMap<>();
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
        List<BlockState> missingStates = Lists.newArrayList(owner.getStateContainer().getValidStates());
        missingStates.removeAll(coveredStates);
        Preconditions.checkState(missingStates.isEmpty(), "Blockstate for block %s does not cover all states. Missing: %s", owner, missingStates);
        JsonObject variants = new JsonObject();
        for (Map.Entry<PartialBlockstate, ConfiguredModelList> entry : getModels().entrySet()) {
            variants.add(entry.getKey().toString(), entry.getValue().toJSON());
        }
        JsonObject main = new JsonObject();
        main.add("variants", variants);
        return main;
    }

    public VariantBlockstate addModels(PartialBlockstate state, ConfiguredModel... model) {
        Preconditions.checkNotNull(state, "state must not be null");
        Preconditions.checkArgument(model.length > 0, "Cannot set models to empty array");
        Preconditions.checkArgument(state.getOwner() == owner, "Cannot set models for a different block. Found: %s, Current: %s", state.getOwner(), owner);
        if (!models.containsKey(state)) {
            Preconditions.checkArgument(disjointToAll(state), "Cannot set models for a state for which a partial match has already been configured");
            models.put(state, new ConfiguredModelList(model));
            for (BlockState fullState : owner.getStateContainer().getValidStates()) {
                if (state.test(fullState)) {
                    coveredStates.add(fullState);
                }
            }
        } else {
            models.compute(state, ($, cml) -> cml.append(model));
        }
        return this;
    }

    public VariantBlockstate setModels(PartialBlockstate state, ConfiguredModel... model) {
        Preconditions.checkArgument(!models.containsKey(state), "Cannot set models for a state that has already been configured");
        addModels(state, model);
        return this;
    }

    private boolean disjointToAll(PartialBlockstate newState) {
        return coveredStates.stream().noneMatch(newState);
    }

    public PartialBlockstate partialState() {
        return new PartialBlockstate(owner, this);
    }

    public VariantBlockstate forAllStates(Function<BlockState, ConfiguredModel[]> mapper) {
        for (BlockState fullState : owner.getStateContainer().getValidStates()) {
            setModels(new PartialBlockstate(owner, fullState.getValues(), this), mapper.apply(fullState));
        }
        return this;
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
                Preconditions.checkArgument(owner.getStateContainer().getProperties().contains(prop), "Property %s not found on block %s", entry, this.owner);
                Preconditions.checkArgument(prop.getAllowedValues().contains(value), "%s is not a valid value for %s", value, prop);
            }
            this.setStates = setStates;
        }

        public <T extends Comparable<T>> PartialBlockstate with(IProperty<T> prop, T value) {
            Preconditions.checkArgument(!setStates.containsKey(prop), "Property %s has already been set", prop);
            Map<IProperty<?>, Comparable<?>> newState = new HashMap<>(setStates);
            newState.put(prop, value);
            return new PartialBlockstate(owner, newState, outerBuilder);
        }

        private void checkValidOwner() {
            Preconditions.checkNotNull(outerBuilder, "Partial blockstate must have a valid owner to perform this action");
        }

        public ConfiguredModel.Builder<VariantBlockstate> modelForState() {
            checkValidOwner();
            return ConfiguredModel.builder(outerBuilder, this);
        }

        /**
         * Add models to the current state's variant. For use when it is more convenient
         * to add multiple sets of models, as a replacement for
         * {@link #setModels(ConfiguredModel...)}.
         * 
         * @param models The models to add.
         * @return {@code this}
         * @throws NullPointerException If the parent builder is null
         */
        public PartialBlockstate addModels(ConfiguredModel... models) {
            checkValidOwner();
            outerBuilder.addModels(this, models);
            return this;
        }

        /**
         * Set this variant's models, and return the parent builder.
         * 
         * @param models The models to set
         * @return The parent builder instance
         * @throws NullPointerException If the parent builder is null
         */
        public VariantBlockstate setModels(ConfiguredModel... models) {
            checkValidOwner();
            return outerBuilder.setModels(this, models);
        }

        /**
         * Complete this state without adding any new models, and return a new partial
         * state via the parent builder. For use after calling
         * {@link #addModels(ConfiguredModel...)}.
         * 
         * @return A fresh partial state as specified by
         *         {@link VariantBlockstate#partialState()}.
         * @throws NullPointerException If the parent builder is null
         */
        public PartialBlockstate partialState() {
            checkValidOwner();
            return outerBuilder.partialState();
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