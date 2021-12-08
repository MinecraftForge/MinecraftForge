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

public class BakedRenderable implements IRenderable<IModelData>
{
    public static final ImmutableList<Direction> ALL_AND_NULL = Util.make(ImmutableList.<Direction>builder(), builder -> {
        builder.add(Direction.values());
        builder.add((Direction)null);
    }).build();

    public static BakedRenderable of(ResourceLocation model)
    {
        return of(Minecraft.getInstance().getModelManager().getModel(model));
    }

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
        for(Direction direction : ALL_AND_NULL)
        {
            for (BakedQuad quad : model.getQuads(null, direction, rand, renderValues))
            {
                bb.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, lightmapCoord, overlayCoord, true);
            }
        }
    }
}
