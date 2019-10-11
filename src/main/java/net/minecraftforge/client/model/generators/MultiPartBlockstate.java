package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.state.IProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MultiPartBlockstate implements IGeneratedBlockstate {
    private final List<MultiPart> parts = new ArrayList<>();
    private final Block owner;

    public MultiPartBlockstate(Block owner) {
        this.owner = owner;
    }

    public void addPart(MultiPart part) {
        Preconditions.checkArgument(part.canApplyTo(owner));
        parts.add(part);
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

    public static class MultiPart {
        public final BlockstateProvider.ConfiguredModelList models;
        public final boolean useOr;
        public final List<PropertyWithValues> conditions;

        public MultiPart(BlockstateProvider.ConfiguredModelList models, boolean useOr, PropertyWithValues... conditionsArray) {
            this(models, useOr, Arrays.asList(conditionsArray));
        }

        public MultiPart(BlockstateProvider.ConfiguredModelList models, boolean useOr, List<PropertyWithValues> conditionsArray) {
            conditions = conditionsArray;
            Preconditions.checkArgument(conditions.size() == conditions.stream()
                    .map(pwv -> pwv.prop)
                    .distinct()
                    .count());
            Preconditions.checkArgument(conditions.stream().noneMatch(pwv -> pwv.values.isEmpty()));
            this.models = models;
            this.useOr = useOr;
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

        public PropertyWithValues(IProperty<T> prop, T... values) {
            this.prop = prop;
            this.values = Arrays.asList(values);
        }
    }
}
