/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MultipartModelData implements IModelData
{
    public static final ModelProperty<MultipartModelData> MULTIPART_DATA = new ModelProperty<>();

    public static IModelData create(List<Pair<Predicate<BlockState>, BakedModel>> selectors, BlockAndTintGetter level, BlockPos pos, BlockState state, IModelData tileData)
    {
        MultipartModelData multipartData = new MultipartModelData(tileData);
        for (Pair<Predicate<BlockState>, BakedModel> selector : selectors)
        {
            if (selector.getLeft().test(state))
            {
                BakedModel part = selector.getRight();
                IModelData partData = part.getModelData(level, pos, state, tileData);
                multipartData.setPartData(part, partData);
            }
        }
        return multipartData;
    }

    public static IModelData resolve(BakedModel part, IModelData modelData)
    {
        MultipartModelData multipartData = modelData.getData(MultipartModelData.MULTIPART_DATA);
        if (multipartData != null)
            return multipartData.getPartData(part, modelData);
        return modelData;
    }

    private final IModelData tileData;
    private final Map<BakedModel, IModelData> partData = new HashMap<>();

    public MultipartModelData(IModelData tileData)
    {
        this.tileData = tileData;
    }

    public void setPartData(BakedModel part, IModelData data)
    {
        partData.put(part, data);
    }

    @Nullable
    public IModelData getPartData(BakedModel part, final IModelData defaultData)
    {
        return partData.getOrDefault(part, defaultData);
    }

    @Override
    public boolean hasProperty(ModelProperty<?> prop)
    {
        return prop == MULTIPART_DATA || tileData.hasProperty(prop);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getData(ModelProperty<T> prop)
    {
        if (prop == MULTIPART_DATA)
            return (T)this;
        return tileData.getData(prop);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T setData(ModelProperty<T> prop, T data)
    {
        if (prop == MULTIPART_DATA)
            return (T)this;
        return tileData.setData(prop, data);
    }
}
