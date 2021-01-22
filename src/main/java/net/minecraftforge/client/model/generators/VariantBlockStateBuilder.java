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

package net.minecraftforge.client.model.generators;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider.ConfiguredModelList;

/**
 * Builder for variant-type blockstates, i.e. non-multipart blockstates. Should
 * not be manually instantiated, instead use
 * {@link BlockStateProvider#getVariantBuilder(Block)}.
 * <p>
 * Variants can either be set via
 * {@link #setModels(PartialBlockstate, ConfiguredModel...)} or
 * {@link #addModels(PartialBlockstate, ConfiguredModel...)}, where model(s) can
 * be assigned directly to {@link PartialBlockstate partial states}, or builder
 * style via {@link #partialState()} and its subsequent methods.
 * <p>
 * This class also provides the convenience methods
 * {@link #forAllStates(Function)} and
 * {@link #forAllStatesExcept(Function, Property...)} for cases where the model
 * for each variant can be decided dynamically based on the state's property
 * values.
 * 
 * @see BlockStateProvider
 */
public class VariantBlockStateBuilder implements IGeneratedBlockstate {

    private final Block owner;
    private final Map<PartialBlockstate, ConfiguredModelList> models = new LinkedHashMap<>();
    private final Set<BlockState> coveredStates = new HashSet<>();

    VariantBlockStateBuilder(Block owner) {
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
        getModels().entrySet().stream()
            .sorted(Entry.comparingByKey(PartialBlockstate.comparingByProperties()))
            .forEach(entry -> variants.add(entry.getKey().toString(), entry.getValue().toJSON()));
        JsonObject main = new JsonObject();
        main.add("variants", variants);
        return main;
    }

    /**
     * Assign some models to a given {@link PartialBlockstate partial state}.
     * 
     * @param state  The {@link PartialBlockstate partial state} for which to add
     *               the models
     * @param models A set of models to add to this state
     * @return this builder
     * @throws NullPointerException     if {@code state} is {@code null}
     * @throws IllegalArgumentException if {@code models} is empty
     * @throws IllegalArgumentException if {@code state}'s owning block differs from
     *                                  the builder's
     * @throws IllegalArgumentException if {@code state} partially matches another
     *                                  state which has already been configured
     */
    public VariantBlockStateBuilder addModels(PartialBlockstate state, ConfiguredModel... models) {
        Preconditions.checkNotNull(state, "state must not be null");
        Preconditions.checkArgument(models.length > 0, "Cannot set models to empty array");
        Preconditions.checkArgument(state.getOwner() == owner, "Cannot set models for a different block. Found: %s, Current: %s", state.getOwner(), owner);
        if (!this.models.containsKey(state)) {
            Preconditions.checkArgument(disjointToAll(state), "Cannot set models for a state for which a partial match has already been configured");
            this.models.put(state, new ConfiguredModelList(models));
            for (BlockState fullState : owner.getStateContainer().getValidStates()) {
                if (state.test(fullState)) {
                    coveredStates.add(fullState);
                }
            }
        } else {
            this.models.compute(state, ($, cml) -> cml.append(models));
        }
        return this;
    }

    /**
     * Assign some models to a given {@link PartialBlockstate partial state},
     * throwing an exception if the state has already been configured. Otherwise,
     * simply calls {@link #addModels(PartialBlockstate, ConfiguredModel...)}.
     * 
     * @param state  The {@link PartialBlockstate partial state} for which to set
     *               the models
     * @param models A set of models to assign to this state
     * @return this builder
     * @throws IllegalArgumentException if {@code state} has already been configured
     * @see #addModels(PartialBlockstate, ConfiguredModel...)
     */
    public VariantBlockStateBuilder setModels(PartialBlockstate state, ConfiguredModel... model) {
        Preconditions.checkArgument(!models.containsKey(state), "Cannot set models for a state that has already been configured: %s", state);
        addModels(state, model);
        return this;
    }

    private boolean disjointToAll(PartialBlockstate newState) {
        return coveredStates.stream().noneMatch(newState);
    }

    public PartialBlockstate partialState() {
        return new PartialBlockstate(owner, this);
    }

    public VariantBlockStateBuilder forAllStates(Function<BlockState, ConfiguredModel[]> mapper) {
        return forAllStatesExcept(mapper);
    }

    public VariantBlockStateBuilder forAllStatesExcept(Function<BlockState, ConfiguredModel[]> mapper, Property<?>... ignored) {
        Set<PartialBlockstate> seen = new HashSet<>();
        for (BlockState fullState : owner.getStateContainer().getValidStates()) {
            Map<Property<?>, Comparable<?>> propertyValues = Maps.newLinkedHashMap(fullState.getValues());
            for (Property<?> p : ignored) {
                propertyValues.remove(p);
            }
            PartialBlockstate partialState = new PartialBlockstate(owner, propertyValues, this);
            if (seen.add(partialState)) {
                setModels(partialState, mapper.apply(fullState));
            }
        }
        return this;
    }

    public static class PartialBlockstate implements Predicate<BlockState> {
        private final Block owner;
        private final SortedMap<Property<?>, Comparable<?>> setStates;
        @Nullable
        private final VariantBlockStateBuilder outerBuilder;

        PartialBlockstate(Block owner, @Nullable VariantBlockStateBuilder outerBuilder) {
            this(owner, ImmutableMap.of(), outerBuilder);
        }

        PartialBlockstate(Block owner, Map<Property<?>, Comparable<?>> setStates, @Nullable VariantBlockStateBuilder outerBuilder) {
            this.owner = owner;
            this.outerBuilder = outerBuilder;
            for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
                Property<?> prop = entry.getKey();
                Comparable<?> value = entry.getValue();
                Preconditions.checkArgument(owner.getStateContainer().getProperties().contains(prop), "Property %s not found on block %s", entry, this.owner);
                Preconditions.checkArgument(prop.getAllowedValues().contains(value), "%s is not a valid value for %s", value, prop);
            }
            this.setStates = Maps.newTreeMap(Comparator.comparing(Property::getName));
            this.setStates.putAll(setStates);
        }

        public <T extends Comparable<T>> PartialBlockstate with(Property<T> prop, T value) {
            Preconditions.checkArgument(!setStates.containsKey(prop), "Property %s has already been set", prop);
            Map<Property<?>, Comparable<?>> newState = new HashMap<>(setStates);
            newState.put(prop, value);
            return new PartialBlockstate(owner, newState, outerBuilder);
        }

        private void checkValidOwner() {
            Preconditions.checkNotNull(outerBuilder, "Partial blockstate must have a valid owner to perform this action");
        }

        /**
         * Creates a builder for models to assign to this state, which when completed
         * via {@link ConfiguredModel.Builder#addModel()} will assign the resultant set
         * of models to this state.
         * 
         * @return the model builder
         * @see ConfiguredModel.Builder
         */
        public ConfiguredModel.Builder<VariantBlockStateBuilder> modelForState() {
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
         * @throws NullPointerException If the parent builder is {@code null}
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
         * @throws NullPointerException If the parent builder is {@code null}
         */
        public VariantBlockStateBuilder setModels(ConfiguredModel... models) {
            checkValidOwner();
            return outerBuilder.setModels(this, models);
        }

        /**
         * Complete this state without adding any new models, and return a new partial
         * state via the parent builder. For use after calling
         * {@link #addModels(ConfiguredModel...)}.
         * 
         * @return A fresh partial state as specified by
         *         {@link VariantBlockStateBuilder#partialState()}.
         * @throws NullPointerException If the parent builder is {@code null}
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

        public SortedMap<Property<?>, Comparable<?>> getSetStates() {
            return setStates;
        }

        @Override
        public boolean test(BlockState blockState) {
            if (blockState.getBlock() != getOwner()) {
                return false;
            }
            for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
                if (blockState.get(entry.getKey()) != entry.getValue()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder();
            for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
                if (ret.length() > 0) {
                    ret.append(',');
                }
                ret.append(entry.getKey().getName())
                        .append('=')
                        .append(((Property) entry.getKey()).getName(entry.getValue()));
            }
            return ret.toString();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public static Comparator<PartialBlockstate> comparingByProperties() {
            // Sort variants inversely by property values, to approximate vanilla style
            return (s1, s2) -> {
                SortedSet<Property<?>> propUniverse = new TreeSet<>(s1.getSetStates().comparator().reversed());
                propUniverse.addAll(s1.getSetStates().keySet());
                propUniverse.addAll(s2.getSetStates().keySet());
                for (Property<?> prop : propUniverse) {
                    Comparable val1 = s1.getSetStates().get(prop);
                    Comparable val2 = s2.getSetStates().get(prop);
                    if (val1 == null && val2 != null) {
                        return -1;
                    } else if (val2 == null && val1 != null) {
                        return 1;
                    } else if (val1 != null && val2 != null){
                        int cmp = val1.compareTo(val2);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }
                return 0;
            };
        }
    }
}
