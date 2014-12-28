package net.minecraftforge.client.model;

import java.util.Collection;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Function;

public interface IModel
{
    /*
     * Returns all model locations that this model depends on.
     * Returned collection should not be modified.
     */
    Collection<ResourceLocation> getDependencies();

    /*
     * Returns all texture locations that this model depends on.
     * Returned collection should not be modified.
     */
    Collection<ResourceLocation> getTextures();

    /*
     * All model texture coordinates should be resolved at this method. Returned model should be in
     * the most plane form possible (if it's not ISmartBlock/ItemModel, then it should be
     * represented by List<BakedQuad> internally).
     * Returned model's getFormat() can me less specific than the passed format argument (some attributes can be replaced with padding), if there's no such info in this model.
     */
    IFlexibleBakedModel bake(IModelTransformation transformation, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter);

    /*
     * returns the default transformation this model should be baked/renderer with
     */
    IModelTransformation getDefaultTransformation();
}
