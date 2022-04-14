/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IForgeBakedModel
{
    private BakedModel self()
    {
        return (BakedModel) this;
    }

    @NotNull
    default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData)
    {
        return self().getQuads(state, side, rand);
    }

    default boolean useAmbientOcclusion(BlockState state) { return self().useAmbientOcclusion(); }

    /**
     * Override to tell the new model loader that it shouldn't wrap this model
     */
    default boolean doesHandlePerspectives() { return false; }

    /*
     * Returns the pair of the model for the given perspective, and the matrix that
     * should be applied to the GL state before rendering it (matrix may be null).
     */
    default BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack)
    {
        return net.minecraftforge.client.ForgeHooksClient.handlePerspective(self(), cameraTransformType, poseStack);
    }

    default @NotNull IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData modelData)
    {
        return modelData;
    }

    default TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return self().getParticleIcon();
    }

    /**
     * Override to true, to tell forge to call the getLayerModels method below.
     */
    default boolean isLayered()
    {
        return false;
    }

    /**
     * If {@see isLayered()} returns true, this is called to get the list of layers to draw.
     */
    default List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous)
    {
        return Collections.singletonList(Pair.of(self(), ItemBlockRenderTypes.getRenderType(itemStack, fabulous)));
    }
}
