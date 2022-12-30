/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.renderable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.Arrays;

/**
 * {@linkplain IRenderable Renderable} wrapper for {@linkplain BakedModel baked models}.
 * <p>
 * The context can provide the {@link BlockState}, faces to be rendered, a {@link RandomSource} and seed,
 * a {@link ModelData} instance, and a {@link Vector4f tint}.
 *
 * @see Context
 */
public class BakedModelRenderable implements IRenderable<BakedModelRenderable.Context>
{
    /**
     * Constructs a {@link BakedModelRenderable} from the given model location.
     * The model is expected to have been baked ahead of time.
     *
     * @see net.minecraftforge.client.event.ModelEvent.RegisterAdditional
     */
    public static BakedModelRenderable of(ResourceLocation model)
    {
        return of(Minecraft.getInstance().getModelManager().getModel(model));
    }

    /**
     * Constructs a {@link BakedModelRenderable} from the given baked model.
     */
    public static BakedModelRenderable of(BakedModel model)
    {
        return new BakedModelRenderable(model);
    }

    private final BakedModel model;

    private BakedModelRenderable(BakedModel model)
    {
        this.model = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, Context context)
    {
        var buffer = bufferSource.getBuffer(textureRenderTypeLookup.get(InventoryMenu.BLOCK_ATLAS));
        var tint = context.tint();
        var randomSource = context.randomSource();
        for (Direction direction : context.faces())
        {
            randomSource.setSeed(context.seed());
            // Given the lack of context, the requested render type has to be null to ensure the model renders all of its geometry
            for (BakedQuad quad : model.getQuads(context.state(), direction, randomSource, context.data(), null))
                buffer.putBulkData(poseStack.last(), quad, tint.x(), tint.y(), tint.z(), tint.w(), lightmap, overlay, true);
        }
    }

    public IRenderable<Unit> withContext(ModelData modelData)
    {
        return withContext(new Context(modelData));
    }

    public IRenderable<ModelData> withModelDataContext()
    {
        return (poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, context) ->
                render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, new Context(context));
    }

    public record Context(@Nullable BlockState state, Direction[] faces, RandomSource randomSource, long seed, ModelData data, Vector4f tint)
    {
        private static final Direction[] ALL_FACES_AND_NULL = Arrays.copyOf(Direction.values(), Direction.values().length + 1);
        private static final Vector4f WHITE = new Vector4f(1, 1, 1, 1);

        public Context(ModelData data)
        {
            this(null, ALL_FACES_AND_NULL, RandomSource.create(), 42, data, WHITE);
        }
    }
}
