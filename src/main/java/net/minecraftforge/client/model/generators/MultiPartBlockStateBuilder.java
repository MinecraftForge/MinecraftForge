/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public final class MultiPartBlockStateBuilder implements IGeneratedBlockstate {

    private final List<PartBuilder> parts = new ArrayList<>();
    private final Block owner;

    public MultiPartBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    /**
     * Creates a builder for models to assign to a {@link PartBuilder}, which when
     * completed via {@link ConfiguredModel.Builder#addModel()} will assign the
     * resultant set of models to the part and return it for further processing.
     * 
     * @return the model builder
     * @see ConfiguredModel.Builder
     */
    public ConfiguredModel.Builder<PartBuilder> part() {
        return ConfiguredModel.builder(this);
    }

    MultiPartBlockStateBuilder addPart(PartBuilder part) {
        this.parts.add(part);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonArray variants = new JsonArray();
        for (PartBuilder part : parts) {
            variants.add(part.toJson());
        }
        JsonObject main = new JsonObject();
        main.add("multipart", variants);
        return main;
    }

    public class PartBuilder {
        public BlockStateProvider.ConfiguredModelList models;
        public boolean useOr;
        public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        public final List<ConditionGroup> nestedConditionGroups = new ArrayList<>();

        PartBuilder(BlockStateProvider.ConfiguredModelList models) {
            this.models = models;
        }

        /**
         * Makes this part get applied if any of the conditions/condition groups are true, instead of all of them needing to be true.
         */
        public PartBuilder useOr() {
            this.useOr = true;
            return this;
        }

        /**
         * Set a condition for this part, which consists of a property and a set of
         * valid values. Can be called multiple times for multiple different properties.
         * 
         * @param <T>    the type of the property value
         * @param prop   the property
         * @param values a set of valid values
         * @return this builder
         * @throws NullPointerException     if {@code prop} is {@code null}
         * @throws NullPointerException     if {@code values} is {@code null}
         * @throws IllegalArgumentException if {@code values} is empty
         * @throws IllegalArgumentException if {@code prop} has already been configured
         * @throws IllegalArgumentException if {@code prop} is not applicable to the
         *                                  current block's state
         * @throws IllegalStateException    if {@code !nestedConditionGroups.isEmpty()}
         */
        @SafeVarargs
        public final <T extends Comparable<T>> PartBuilder condition(Property<T> prop, T... values) {
            Preconditions.checkNotNull(prop, "Property must not be null");
            Preconditions.checkNotNull(values, "Value list must not be null");
            Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
            Preconditions.checkArgument(!conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
            Preconditions.checkArgument(canApplyTo(owner), "IProperty %s is not valid for the block %s", prop, owner);
            Preconditions.checkState(nestedConditionGroups.isEmpty(), "Can't have normal conditions if there are already nested condition groups");
            this.conditions.putAll(prop, Arrays.asList(values));
            return this;
        }

        /**
         * Allows having nested groups of conditions if there are not any normal conditions.
         * @throws IllegalStateException if {@code !conditions.isEmpty()}
         */
        public final ConditionGroup nestedGroup()
        {
            Preconditions.checkState(conditions.isEmpty(), "Can't have nested condition groups if there are already normal conditions");
            ConditionGroup group = new ConditionGroup();
            this.nestedConditionGroups.add(group);
            return group;
        }

        public MultiPartBlockStateBuilder end() { return MultiPartBlockStateBuilder.this; }

        JsonObject toJson() {
            JsonObject out = new JsonObject();
            if (!conditions.isEmpty()) {
                out.add("when", MultiPartBlockStateBuilder.toJson(this.conditions, this.useOr));
            }
            else if (!nestedConditionGroups.isEmpty())
            {
                out.add("when", MultiPartBlockStateBuilder.toJson(this.nestedConditionGroups, this.useOr));
            }
            out.add("apply", models.toJSON());
            return out;
        }

        public boolean canApplyTo(Block b) {
            return b.getStateDefinition().getProperties().containsAll(conditions.keySet());
        }

        public class ConditionGroup
        {
            public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
            public final List<ConditionGroup> nestedConditionGroups = new ArrayList<>();
            private ConditionGroup parent = null;
            public boolean useOr;

            /**
             * Set a condition for this part, which consists of a property and a set of
             * valid values. Can be called multiple times for multiple different properties.
             *
             * @param <T>    the type of the property value
             * @param prop   the property
             * @param values a set of valid values
             * @return this builder
             * @throws NullPointerException     if {@code prop} is {@code null}
             * @throws NullPointerException     if {@code values} is {@code null}
             * @throws IllegalArgumentException if {@code values} is empty
             * @throws IllegalArgumentException if {@code prop} has already been configured
             * @throws IllegalArgumentException if {@code prop} is not applicable to the
             *                                  current block's state
             * @throws IllegalStateException    if {@code !nestedConditionGroups.isEmpty()}
             */
            @SafeVarargs
            public final <T extends Comparable<T>> ConditionGroup condition(Property<T> prop, T... values)
            {
                Preconditions.checkNotNull(prop, "Property must not be null");
                Preconditions.checkNotNull(values, "Value list must not be null");
                Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
                Preconditions.checkArgument(!conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
                Preconditions.checkArgument(canApplyTo(owner), "IProperty %s is not valid for the block %s", prop, owner);
                Preconditions.checkState(nestedConditionGroups.isEmpty(), "Can't have normal conditions if there are already nested condition groups");
                this.conditions.putAll(prop, Arrays.asList(values));
                return this;
            }

            /**
             * Allows having nested groups of conditions if there are not any normal conditions.
             * @throws IllegalStateException if {@code !conditions.isEmpty()}
             */
            public ConditionGroup nestedGroup()
            {
                Preconditions.checkState(conditions.isEmpty(), "Can't have nested condition groups if there are already normal conditions");
                ConditionGroup group = new ConditionGroup();
                group.parent = this;
                this.nestedConditionGroups.add(group);
                return group;
            }

            /**
             * Ends this nested condition group and returns the parent condition group
             * 
             * @throws IllegalStateException If this is not a nested condition group
             */
            public ConditionGroup endNestedGroup() 
            {
                if (parent == null)
                    throw new IllegalStateException("This condition group is not nested, use end() instead"); 
                return parent;
            }

            /**
             * Ends this condition group and returns the part builder
             * 
             * @throws IllegalStateException If this is a nested condition group
             */
            public MultiPartBlockStateBuilder.PartBuilder end()
            {
                if (this.parent != null)
                    throw new IllegalStateException("This is a nested condition group, use endNestedGroup() instead");
                return MultiPartBlockStateBuilder.PartBuilder.this;
            }

            /**
             * Makes this part get applied if any of the conditions/condition groups are true, instead of all of them needing to be true.
             */
            public ConditionGroup useOr()
            {
                this.useOr = true;
                return this;
            }

            JsonObject toJson()
            {
                if (!this.conditions.isEmpty())
                {
                    return MultiPartBlockStateBuilder.toJson(this.conditions, this.useOr);
                } 
                else if (!this.nestedConditionGroups.isEmpty())
                {
                    return MultiPartBlockStateBuilder.toJson(this.nestedConditionGroups, this.useOr);
                }
                return new JsonObject();
            }
        }
    }

    private static JsonObject toJson(List<PartBuilder.ConditionGroup> conditions, boolean useOr)
    {
        JsonObject groupJson = new JsonObject();
        JsonArray innerGroupJson = new JsonArray();
        groupJson.add(useOr ? "OR" : "AND", innerGroupJson);
        for (PartBuilder.ConditionGroup group : conditions)
        {
            innerGroupJson.add(group.toJson());
        }
        return groupJson;
    }

    private static JsonObject toJson(Multimap<Property<?>, Comparable<?>> conditions, boolean useOr)
    {
        JsonObject groupJson = new JsonObject();
        for (Entry<Property<?>, Collection<Comparable<?>>> e : conditions.asMap().entrySet())
        {
            StringBuilder activeString = new StringBuilder();
            for (Comparable<?> val : e.getValue())
            {
                if (activeString.length() > 0)
                    activeString.append("|");
                activeString.append(((Property) e.getKey()).getName(val));
            }
            groupJson.addProperty(e.getKey().getName(), activeString.toString());
        }
        if (useOr)
        {
            JsonArray innerWhen = new JsonArray();
            for (Entry<String, JsonElement> entry : groupJson.entrySet())
            {
                JsonObject obj = new JsonObject();
                obj.add(entry.getKey(), entry.getValue());
                innerWhen.add(obj);
            }
            groupJson = new JsonObject();
            groupJson.add("OR", innerWhen);
        }
        return groupJson;
    }
}
