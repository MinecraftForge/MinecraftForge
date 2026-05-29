/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model.data;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;

import java.util.IdentityHashMap;
import java.util.Map;

public class MultipartModelData {
    public static final ModelProperty<MultipartModelData> PROPERTY = new ModelProperty<>();

    private final Map<BlockStateModelPart, ModelData> partData;

    private MultipartModelData(Map<BlockStateModelPart, ModelData> partData) {
        this.partData = partData;
    }

    @Nullable
    public ModelData get(BlockStateModelPart model) {
        return partData.get(model);
    }

    /**
     * Helper to get the data from a {@link ModelData} instance.
     *
     * @param modelData The object to get data from
     * @param model     The model to get data for
     * @return The data for the part, or the one passed in if not found
     */
    public static ModelData resolve(ModelData modelData, BlockStateModelPart model) {
        var multipartData = modelData.get(PROPERTY);
        if (multipartData == null)
            return modelData;
        var partData = multipartData.get(model);
        return partData != null ? partData : modelData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<BlockStateModelPart, ModelData> partData = new IdentityHashMap<>();

        public Builder with(BlockStateModelPart model, ModelData data) {
            partData.put(model, data);
            return this;
        }

        public MultipartModelData build() {
            return new MultipartModelData(partData);
        }
    }
}
