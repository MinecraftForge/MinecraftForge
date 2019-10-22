package net.minecraftforge.client.model.generators;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.MultiPart;

public final class ConfiguredModel {

    public static final int DEFAULT_WEIGHT = 1;

    public final ModelFile name;
    public final int rotationX;
    public final int rotationY;
    public final boolean uvLock;
    public final int weight;

    private static IntStream validRotations() {
        return IntStream.range(0, 4).map(i -> i * 90);
    }

    public static ConfiguredModel[] allYRotations(ModelFile model, int x, boolean uvlock) {
        return allYRotations(model, x, uvlock, DEFAULT_WEIGHT);
    }

    public static ConfiguredModel[] allYRotations(ModelFile model, int x, boolean uvlock, int weight) {
        return validRotations()
                .mapToObj(y -> new ConfiguredModel(model, x, y, uvlock, weight))
                .toArray(ConfiguredModel[]::new);
    }

    public static ConfiguredModel[] allRotations(ModelFile model, boolean uvlock) {
        return allRotations(model, uvlock, DEFAULT_WEIGHT);
    }

    public static ConfiguredModel[] allRotations(ModelFile model, boolean uvlock, int weight) {
        return validRotations()
                .mapToObj(x -> allYRotations(model, x, uvlock, weight))
                .flatMap(Arrays::stream)
                .toArray(ConfiguredModel[]::new);
    }

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

    static Builder<VariantBlockStateBuilder> builder(VariantBlockStateBuilder outer, VariantBlockStateBuilder.PartialBlockstate state) {
        return new Builder<>(models -> outer.setModels(state, models), ImmutableList.of());
    }

    static Builder<MultiPart> builder(MultiPartBlockStateBuilder outer) {
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

        public ConfiguredModel buildLast() {
            Preconditions.checkNotNull(name);
            return new ConfiguredModel(name, rotationX, rotationY, uvLock, weight);
        }

        public ConfiguredModel[] build() {
            return ObjectArrays.concat(otherModels.toArray(new ConfiguredModel[0]), buildLast());
        }

        public T addModel() {
            return callback.apply(build());
        }

        public Builder<T> nextModel() {
            return new Builder<>(callback, Arrays.asList(build()));
        }
    }
}
