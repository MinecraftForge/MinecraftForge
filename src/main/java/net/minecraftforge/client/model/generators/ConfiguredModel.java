package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ConfiguredModel {
    public final ModelFile name;
    public final int rotationX;
    public final int rotationY;
    public final boolean uvLock;
    public final int weight;

    public ConfiguredModel(ModelFile name, int rotationX, int rotationY, boolean uvLock, int weight) {
        this.name = name;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.uvLock = uvLock;
        this.weight = weight;
    }

    public ConfiguredModel(ModelFile name, int rotationX, int rotationY, boolean uvLock) {
        this(name, rotationX, rotationY, uvLock, 0);
    }

    public ConfiguredModel(ModelFile name) {
        this(name, 0, 0, false, 0);
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
        if (includeWeight)
            modelJson.addProperty("weight", weight);
        return modelJson;
    }

    public static Builder builder() {
        return new Builder();
    }

    static Builder builder(VariantBlockstate.Builder outer, VariantBlockstate.PartialBlockstate state) {
        return new Builder(outer, state, ImmutableList.of());
    }

    public static class Builder {
        private ModelFile name;
        @Nullable
        private final VariantBlockstate.Builder outer;
        @Nullable
        private final VariantBlockstate.PartialBlockstate state;
        private final List<ConfiguredModel> otherModels;
        private Integer rotationX;
        private Integer rotationY;
        private Boolean uvLock;
        private Integer weight;
        private Builder() {
            this(null, null, ImmutableList.of());
        }

        private Builder(@Nullable VariantBlockstate.Builder outer,
                        @Nullable VariantBlockstate.PartialBlockstate state,
                        List<ConfiguredModel> otherModels) {
            this.otherModels = otherModels;
            Preconditions.checkArgument((outer==null)==(state==null));
            this.outer = outer;
            this.state = state;
        }

        public Builder modelFile(ModelFile model) {
            Preconditions.checkState(name==null);
            name = model;
            return this;
        }

        public Builder rotationX(int value) {
            Preconditions.checkState(rotationX==null);
            rotationX = value;
            return this;
        }

        public Builder rotationY(int value) {
            Preconditions.checkState(rotationY==null);
            rotationY = value;
            return this;
        }

        public Builder uvLock(boolean value) {
            Preconditions.checkState(uvLock==null);
            uvLock = value;
            return this;
        }

        public Builder weight(int value) {
            Preconditions.checkState(weight==null);
            weight = value;
            return this;
        }

        public ConfiguredModel build() {
            Preconditions.checkNotNull(name);
            int rotX = rotationX!=null?rotationX:0;
            int rotY = rotationY!=null?rotationY:0;
            boolean uvLock = this.uvLock!=null?this.uvLock:false;
            int weight = this.weight!=null?this.weight:0;
            return new ConfiguredModel(name, rotX, rotY, uvLock, weight);
        }

        public VariantBlockstate.Builder addModel() {
            Preconditions.checkState(outer!=null);
            List<ConfiguredModel> allModels = new ArrayList<>(otherModels);
            allModels.add(this.build());
            return outer.setModel(state, allModels.toArray(new ConfiguredModel[0]));
        }

        public Builder nextModel() {
            List<ConfiguredModel> allModels = new ArrayList<>(otherModels);
            allModels.add(this.build());
            return new Builder(outer, state, allModels);
        }
   }
}
