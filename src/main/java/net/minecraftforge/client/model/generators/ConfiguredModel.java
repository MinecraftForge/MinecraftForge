package net.minecraftforge.client.model.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraftforge.client.model.generators.MultiPartBlockstate.MultiPart;

public final class ConfiguredModel {
    
    static final int DEFAULT_WEIGHT = 1;
    
    public final ModelFile name;
    public final int rotationX;
    public final int rotationY;
    public final boolean uvLock;
    public final int weight;

    public ConfiguredModel(ModelFile name, int rotationX, int rotationY, boolean uvLock, int weight) {
        this.name = name;
        checkRotation(rotationX, rotationY);
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.uvLock = uvLock;
        checkWeight(weight);
        this.weight = weight;
    }

    public ConfiguredModel(ModelFile name, int rotationX, int rotationY, boolean uvLock) {
        this(name, rotationX, rotationY, uvLock, DEFAULT_WEIGHT);
    }

    public ConfiguredModel(ModelFile name) {
        this(name, 0, 0, false);
    }

    static void checkRotation(int rotationX, int rotationY) {
        Preconditions.checkNotNull(ModelRotation.getModelRotation(rotationX, rotationY), "Invalid model rotation x=" + rotationX + ", y=" + rotationY);
    }

    static void checkWeight(int weight) {
        Preconditions.checkArgument(weight >= 1, "Model weight must be greater than or equal to 1. Found: " + weight);
    }

    public JsonObject toJSON(boolean includeWeight) {
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("model", name.getLocation().toString());
        if (rotationX != 0)
            modelJson.addProperty("x", rotationX);
        if (rotationY != 0)
            modelJson.addProperty("y", rotationY);
        if (uvLock && (rotationX != 0 || rotationY != 0))
            modelJson.addProperty("uvlock", uvLock);
        if (includeWeight && weight != DEFAULT_WEIGHT)
            modelJson.addProperty("weight", weight);
        return modelJson;
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    static Builder<VariantBlockstate> builder(VariantBlockstate outer, VariantBlockstate.PartialBlockstate state) {
        return new Builder<>(models -> outer.setModel(state, models), ImmutableList.of());
    }

    static Builder<MultiPart> builder(MultiPartBlockstate outer) {
        return new Builder<MultiPart>(models -> {
            MultiPart ret = outer.new MultiPart(new BlockstateProvider.ConfiguredModelList(models));
            outer.addPart(ret);
            return ret;
        }, ImmutableList.of());
    }

    public static class Builder<T> {
        private ModelFile name;
        private final Function<ConfiguredModel[], T> callback;
        private final List<ConfiguredModel> otherModels;
        private int rotationX;
        private int rotationY;
        private boolean uvLock;
        private int weight = DEFAULT_WEIGHT;
        private Builder() {
            this($ -> null, ImmutableList.of());
        }

        private Builder(Function<ConfiguredModel[], T> callback, List<ConfiguredModel> otherModels) {
            this.callback = callback;
            this.otherModels = otherModels;
        }

        public Builder<T> modelFile(ModelFile model) {
            name = model;
            return this;
        }

        public Builder<T> rotationX(int value) {
            checkRotation(value, rotationY);
            rotationX = value;
            return this;
        }

        public Builder<T> rotationY(int value) {
            checkRotation(rotationX, value);
            rotationY = value;
            return this;
        }

        public Builder<T> uvLock(boolean value) {
            uvLock = value;
            return this;
        }

        public Builder<T> weight(int value) {
            checkWeight(value);
            weight = value;
            return this;
        }

        public ConfiguredModel build() {
            Preconditions.checkNotNull(name);
            return new ConfiguredModel(name, rotationX, rotationY, uvLock, weight);
        }

        public T addModel() {
            List<ConfiguredModel> allModels = new ArrayList<>(otherModels);
            allModels.add(this.build());
            return callback.apply(allModels.toArray(new ConfiguredModel[0]));
        }

        public Builder<T> nextModel() {
            List<ConfiguredModel> allModels = new ArrayList<>(otherModels);
            allModels.add(this.build());
            return new Builder<>(callback, allModels);
        }
    }
}
