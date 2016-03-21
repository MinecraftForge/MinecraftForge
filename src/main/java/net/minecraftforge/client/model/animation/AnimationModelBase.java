package net.minecraftforge.client.model.animation;

import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

/**
 * ModelBase that works with the Forge model system and animations.
 * Some quirks are still left, deprecated for the moment.
 */
@Deprecated
public class AnimationModelBase<T extends Entity> extends ModelBase implements IEventHandler<T>
{
    private final VertexLighterFlat lighter;
    private final ResourceLocation modelLocation;

    public AnimationModelBase(ResourceLocation modelLocation, VertexLighterFlat lighter)
    {
        this.modelLocation = modelLocation;
        this.lighter = lighter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingSpeed, float timeAlive, float yawHead, float rotationPitch, float scale)
    {
        if(!(entity.hasCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null)))
        {
            return;
        }

        Pair<IModelState, Iterable<Event>> pair = entity.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null).apply(timeAlive / 20);
        handleEvents((T)entity, timeAlive / 20, pair.getRight());
        IModel model = ModelLoaderRegistry.getModelOrMissing(modelLocation);
        IBakedModel bakedModel = model.bake(pair.getLeft(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());

        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.height, entity.posZ);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer VertexBuffer = tessellator.getBuffer();
        VertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        VertexBuffer.setTranslation(-0.5, -1.5, -0.5);

        lighter.setParent(new VertexBufferConsumer(VertexBuffer));
        lighter.setWorld(entity.worldObj);
        lighter.setState(Blocks.air.getDefaultState());
        lighter.setBlockPos(pos);
        boolean empty = true;
        List<BakedQuad> quads = bakedModel.getQuads(null, null, 0);
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
            quads = bakedModel.getQuads(null, side, 0);
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
        /*VertexBuffer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
        VertexBuffer.pos(0, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
        VertexBuffer.pos(1, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
        VertexBuffer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();*/

        VertexBuffer.setTranslation(0, 0, 0);

        tessellator.draw();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    public void handleEvents(T instance, float time, Iterable<Event> pastEvents) {}
}
