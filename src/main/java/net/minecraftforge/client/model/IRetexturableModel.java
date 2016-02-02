package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;

public interface IRetexturableModel<M extends IRetexturableModel<M>> extends IModel
{
    /**
     * Applies new textures to the model.
     * The returned model should be independent of the accessed one,
     * as a model should be able to be retextured multiple times producing
     * a separate model each time.
     *
     * The input map MAY map to an empty string "" which should be used
     * to indicate the texture was removed. Handling of that is up to
     * the model itself. Such as using default, missing texture, or
     * removing vertices.
     *
     * The input should be considered a DIFF of the old textures, not a
     * replacement as it may not contain everything.
     *
     * @param textures New
     * @return Model with textures applied.
     */
    // 1.9: change IModel to M
    IModel retexture(ImmutableMap<String, String> textures);
}
