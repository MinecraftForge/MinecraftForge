package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.state.IProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MultiPartBlockStateBuilder implements IGeneratedBlockstate {

    private final List<MultiPart> parts = new ArrayList<>();
    private final Block owner;

    public MultiPartBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    public ConfiguredModel.Builder<MultiPart> part() {
        return ConfiguredModel.builder(this);
    }

    MultiPartBlockStateBuilder addPart(MultiPart part) {
        this.parts.add(part);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonArray variants = new JsonArray();
        for (MultiPart part : parts) {
            variants.add(part.toJson());
        }
        JsonObject main = new JsonObject();
        main.add("multipart", variants);
        return main;
    }

    public class MultiPart {
        public BlockstateProvider.ConfiguredModelList models;
        public boolean useOr;
        public final List<PropertyWithValues<?>> conditions;

        public MultiPart(BlockstateProvider.ConfiguredModelList models, PropertyWithValues<?>... conditionsArray) {
            this(models, false);
        }

        public MultiPart(BlockstateProvider.ConfiguredModelList models, boolean useOr, PropertyWithValues<?>... conditionsArray) {
            this(models, useOr, Lists.newArrayList(conditionsArray));
        }

        public MultiPart(BlockstateProvider.ConfiguredModelList models, boolean useOr, List<PropertyWithValues<?>> conditionsArray) {
            conditions = conditionsArray;
            Preconditions.checkArgument(conditions.size() == conditions.stream()
                    .map(pwv -> pwv.prop)
                    .distinct()
                    .count(), "Condition list must not have duplicates");
            Preconditions.checkArgument(conditions.stream().noneMatch(pwv -> pwv.values.isEmpty()), "Conditions must not have empty model lists");
            this.models = models;
            this.useOr = useOr;
        }

        public MultiPart useOr() {
            this.useOr = true;
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> MultiPart condition(IProperty<T> prop, T... values) {
            this.conditions.add(new PropertyWithValues<>(prop, values));
            Preconditions.checkArgument(canApplyTo(owner), "IProperty %s is not valid for the block %s", prop, owner);
            return this;
        }

        public MultiPartBlockStateBuilder build() {
            return MultiPartBlockStateBuilder.this;
        }

        public JsonObject toJson() {
            JsonObject out = new JsonObject();
            if (!conditions.isEmpty()) {
                JsonObject when = new JsonObject();
                for (PropertyWithValues<?> prop : conditions) {
                    StringBuilder activeString = new StringBuilder();
                    for (Object val : prop.values) {
                        if (activeString.length() > 0)
                            activeString.append("|");
                        activeString.append(val.toString());
                    }
                    when.addProperty(prop.prop.getName(), activeString.toString());
                }
                if (useOr) {
                    JsonObject innerWhen = when;
                    when = new JsonObject();
                    when.add("OR", innerWhen);
                }
                out.add("when", when);
            }
            out.add("apply", models.toJSON());
            return out;
        }

        public boolean canApplyTo(Block b) {
            for (PropertyWithValues<?> p : conditions)
                if (!b.getStateContainer().getProperties().contains(p.prop))
                    return false;
            return true;
        }
    }

    public static final class PropertyWithValues<T extends Comparable<T>> {
        public final IProperty<T> prop;
        public final List<T> values;

        @SafeVarargs
        public PropertyWithValues(IProperty<T> prop, T... values) {
            this.prop = prop;
            this.values = Arrays.asList(values);
        }
    }
}
