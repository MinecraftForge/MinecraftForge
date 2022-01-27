/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model.renderable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;
import java.util.function.Function;

/**
 * Wrapper to make it easier to draw baked models in renderers.
 */
public class BakedRenderable implements IRenderable<IModelData>
{
    private static final ImmutableList<Direction> MODEL_FACINGS = Util.make(ImmutableList.<Direction>builder(), builder -> {
        builder.add(Direction.values());
        builder.add((Direction)null);
    }).build();

    /**
     * Constructs a BakedRenderable from the given model location.
     * The model is expected to have been baked ahead of time.
     * You can ensure the model is baked by calling {@link net.minecraftforge.client.model.ForgeModelBakery#addSpecialModel(ResourceLocation)}
     * from within {@link net.minecraftforge.client.event.ModelRegistryEvent}.
     */
    public static BakedRenderable of(ResourceLocation model)
    {
        return of(Minecraft.getInstance().getModelManager().getModel(model));
    }

    /**
     * Constructs a BakedRenderable from the given model.
     */
    public static BakedRenderable of(BakedModel model)
    {
        return new BakedRenderable(model);
    }

    private final Random rand = new Random();
    private final BakedModel model;

    private BakedRenderable(BakedModel model)
    {
        this.model = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Function<ResourceLocation, RenderType> renderTypeFunction, int lightmapCoord, int overlayCoord, float partialTicks, IModelData renderValues)
    {
        var rt = renderTypeFunction.apply(InventoryMenu.BLOCK_ATLAS);
        VertexConsumer bb = bufferSource.getBuffer(rt);
        for(Direction direction : MODEL_FACINGS)
        {
            for (BakedQuad quad : model.getQuads(null, direction, rand, renderValues))
            {
                bb.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, lightmapCoord, overlayCoord, true);
            }
        }
    }
}
