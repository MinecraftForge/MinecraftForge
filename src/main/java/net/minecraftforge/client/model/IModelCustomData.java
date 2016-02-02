package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;

public interface IModelCustomData<M extends IModelCustomData<M>> extends IModel
{
    /**
     * Allows the model to process custom data from the variant definition.
     * If unknown data is encountered it should be skipped.
     * @return a new model, with data applied.
     */
    // 1.9: change IModel to M
    IModel process(ImmutableMap<String, String> customData);
}
