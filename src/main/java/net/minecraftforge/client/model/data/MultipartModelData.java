/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MultipartModelData implements IModelData
{
    public static final ModelProperty<MultipartModelData> MULTIPART_DATA = new ModelProperty<>();

    public static IModelData create(List<Pair<Predicate<BlockState>, IBakedModel>> selectors, IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData tileData)
    {
        MultipartModelData multipartData = new MultipartModelData(tileData);
        for (Pair<Predicate<BlockState>, IBakedModel> selector : selectors)
        {
            if (selector.getLeft().test(state))
            {
                IBakedModel part = selector.getRight();
                IModelData partData = part.getModelData(world, pos, state, tileData);
                multipartData.setPartData(part, partData);
            }
        }
        return multipartData;
    }

    public static IModelData resolve(IBakedModel part, IModelData modelData)
    {
        MultipartModelData multipartData = modelData.getData(MultipartModelData.MULTIPART_DATA);
        if (multipartData != null)
            return multipartData.getPartData(part, modelData);
        return modelData;
    }

    private final IModelData tileData;
    private final Map<IBakedModel, IModelData> partData = new HashMap<>();

    public MultipartModelData(IModelData tileData)
    {
        this.tileData = tileData;
    }

    public void setPartData(IBakedModel part, IModelData data)
    {
        partData.put(part, data);
    }

    @Nullable
    public IModelData getPartData(IBakedModel part, final IModelData defaultData)
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
