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

import net.minecraft.block.Block;
import net.minecraft.state.Property;

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

    public class PartBuilder implements IConditionGroup {
        public BlockStateProvider.ConfiguredModelList models;
        public boolean useOr;
        public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        public final List<ConditionGroup> nestedConditionGroups = new ArrayList<>();

        PartBuilder(BlockStateProvider.ConfiguredModelList models) {
            this.models = models;
        }

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
         * @throws IllegalStateException    if {@code nestedConditionGroups.size() != 0}
         */
        @SafeVarargs
        public final <T extends Comparable<T>> PartBuilder condition(Property<T> prop, T... values) {
            Preconditions.checkNotNull(prop, "Property must not be null");
            Preconditions.checkNotNull(values, "Value list must not be null");
            Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
            Preconditions.checkArgument(!conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
            Preconditions.checkArgument(canApplyTo(owner), "IProperty %s is not valid for the block %s", prop, owner);
            Preconditions.checkState(nestedConditionGroups.size() == 0, "Can't have normal conditions if there are already nested condition groups");
            this.conditions.putAll(prop, Arrays.asList(values));
            return this;
        }

        /**
         * Allows having nested groups of conditions if the current condition group is empty.
         * @throws IllegalStateException if there are any normal conditions in the current condition group
         */
        public final ConditionGroup nestedGroup()
        {
            Preconditions.checkState(conditions.size() == 0, "Can't have nested condition groups if there are already normal conditions");
            ConditionGroup group = new ConditionGroup();
            this.nestedConditionGroups.add(group);
            return group;
        }

        public MultiPartBlockStateBuilder end() { return MultiPartBlockStateBuilder.this; }

        public IConditionGroup endGroup() { return this; }

        JsonObject toJson() {
            JsonObject out = new JsonObject();
            if (!conditions.isEmpty()) {
                JsonObject when = new JsonObject();
                for (Entry<Property<?>, Collection<Comparable<?>>> e : conditions.asMap().entrySet()) {
                    StringBuilder activeString = new StringBuilder();
                    for (Comparable<?> val : e.getValue()) {
                        if (activeString.length() > 0)
                            activeString.append("|");
                        activeString.append(((Property) e.getKey()).getName(val));
                    }
                    when.addProperty(e.getKey().getName(), activeString.toString());
                }
                if (useOr) {
                    JsonArray innerWhen = new JsonArray();
                    for (Entry<String, JsonElement> entry : when.entrySet())
                    {
                        JsonObject obj = new JsonObject();
                        obj.add(entry.getKey(), entry.getValue());
                        innerWhen.add(obj);
                    }
                    when = new JsonObject();
                    when.add("OR", innerWhen);
                }
                out.add("when", when);
            }
            else if (!nestedConditionGroups.isEmpty())
            {
                JsonObject when = new JsonObject();
                JsonArray innerWhen = new JsonArray();
                when.add(useOr ? "OR" : "AND", innerWhen);
                for (ConditionGroup group : nestedConditionGroups)
                {
                    innerWhen.add(group.toJson());
                }
                out.add("when", when);
            }
            out.add("apply", models.toJSON());
            return out;
        }

        public boolean canApplyTo(Block b) {
            return b.getStateDefinition().getProperties().containsAll(conditions.keySet());
        }

        public class ConditionGroup implements IConditionGroup
        {
            public boolean useOr;
            private ConditionGroup parent = null;
            public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
            public final List<ConditionGroup> nestedConditionGroups = new ArrayList<>();

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
             * @throws IllegalStateException    if {@code nestedConditionGroups.size() != 0}
             */
            @SafeVarargs
            public final <T extends Comparable<T>> ConditionGroup condition(Property<T> prop, T... values)
            {
                Preconditions.checkNotNull(prop, "Property must not be null");
                Preconditions.checkNotNull(values, "Value list must not be null");
                Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
                Preconditions.checkArgument(!conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
                Preconditions.checkArgument(canApplyTo(owner), "IProperty %s is not valid for the block %s", prop, owner);
                Preconditions.checkState(nestedConditionGroups.size() == 0, "Can't have normal conditions if there are already nested condition groups");
                this.conditions.putAll(prop, Arrays.asList(values));
                return this;
            }

            /**
             * Allows having nested groups of conditions if the current condition group is empty.
             * @throws IllegalStateException if there are any normal conditions in the current condition group
             */
            public final ConditionGroup nestedGroup()
            {
                Preconditions.checkState(conditions.size() == 0, "Can't have nested condition groups if there are already normal conditions");
                ConditionGroup group = new ConditionGroup();
                group.parent = this;
                this.nestedConditionGroups.add(group);
                return group;
            }

            public IConditionGroup endGroup() 
            {
                if (parent == null)
                {
                    return MultiPartBlockStateBuilder.PartBuilder.this; 
                }
                else {
                    return parent;
                }
            }

            public MultiPartBlockStateBuilder end()
            {
                if (this.parent != null)
                {
                    return this.parent.end();
                }
                else
                {
                    return MultiPartBlockStateBuilder.PartBuilder.this.end();
                }
            }

            public ConditionGroup useOr()
            {
                this.useOr = true;
                return this;
            }

            JsonObject toJson()
            {
                if (!this.conditions.isEmpty()) {
                    JsonObject groupJson = new JsonObject();
                    for (Entry<Property<?>, Collection<Comparable<?>>> e : this.conditions.asMap().entrySet()) {
                        StringBuilder activeString = new StringBuilder();
                        for (Comparable<?> val : e.getValue()) {
                            if (activeString.length() > 0)
                                activeString.append("|");
                            activeString.append(((Property) e.getKey()).getName(val));
                        }
                        groupJson.addProperty(e.getKey().getName(), activeString.toString());
                    }
                    if (useOr) {
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
                else if (!this.nestedConditionGroups.isEmpty())
                {
                    JsonObject groupJson = new JsonObject();
                    JsonArray innerGroupJson = new JsonArray();
                    groupJson.add(useOr ? "OR" : "AND", innerGroupJson);
                    for (ConditionGroup group : this.nestedConditionGroups)
                    {

                        innerGroupJson.add(group.toJson());
                    }
                    return groupJson;
                }
                return new JsonObject();
            }
        }
    }

    public interface IConditionGroup
    {
        IConditionGroup nestedGroup();
        /**
         * If this is a {@link PartBuilder}, returns itself.
         */
        IConditionGroup endGroup();
        IConditionGroup useOr();
        <T extends Comparable<T>> IConditionGroup condition(Property<T> prop, T... values);
        MultiPartBlockStateBuilder end();
    }
}
