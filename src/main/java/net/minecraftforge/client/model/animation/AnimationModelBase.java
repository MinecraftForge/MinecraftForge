package net.minecraftforge.client.model.animation;

import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.WorldRendererConsumer;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

/**
 * ModelBase that works with the Forge model system and animations.
 * Some quirks are still left, deprecated for the moment.
 */
@Deprecated
public class AnimationModelBase<T extends Entity & IAnimationProvider> extends ModelBase implements IEventHandler<T>
{
    private final VertexLighterFlat lighter;
    private final IModel model;

    public AnimationModelBase(IModel model, VertexLighterFlat lighter)
    {
        this.model = model;
        this.lighter = lighter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingSpeed, float timeAlive, float yawHead, float rotationPitch, float scale)
    {
        if(!(entity instanceof IAnimationProvider))
        {
            throw new ClassCastException("AnimationModelBase expects IAnimationProvider");
        }

        Pair<IModelState, Iterable<Event>> pair = ((IAnimationProvider)entity).asm().apply(timeAlive / 20);
        handleEvents((T)entity, timeAlive / 20, pair.getRight());
        IBakedModel bakedModel = model.bake(pair.getLeft(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());

        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.height, entity.posZ);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        worldRenderer.setTranslation(-0.5, -1.5, -0.5);

        lighter.setParent(new WorldRendererConsumer(worldRenderer));
        lighter.setWorld(entity.worldObj);
        lighter.setBlock(Blocks.air);
        lighter.setBlockPos(pos);
        boolean empty = true;
        List<BakedQuad> quads = bakedModel.getGeneralQuads();
        if(!quads.isEmpty())
        {
            lighter.updateBlockInfo();
            empty = false;
            for(BakedQuad quad : quads)
            {
                quad.pipe(lighter);
            }
        }
        for(EnumFacing side : EnumFacing.values())
        {
            quads = bakedModel.getFaceQuads(side);
            if(!quads.isEmpty())
            {
                if(empty) lighter.updateBlockInfo();
                empty = false;
                for(BakedQuad quad : quads)
                {
                    quad.pipe(lighter);
                }
            }
        }

        // debug quad
        /*worldRenderer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
        worldRenderer.pos(0, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
        worldRenderer.pos(1, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
        worldRenderer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();*/

        worldRenderer.setTranslation(0, 0, 0);

        tessellator.draw();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    public void handleEvents(T instance, float time, Iterable<Event> pastEvents) {}
}
