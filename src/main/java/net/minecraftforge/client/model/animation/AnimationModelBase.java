/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.client.model.animation;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.IEventHandler;
import net.minecraftforge.common.model.animation.CapabilityAnimation;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

/**
 * ModelBase that works with the Forge model system and animations.
 * Some quirks are still left, deprecated for the moment.
 */
/*
@Deprecated
public class AnimationModelBase<T extends Entity> extends Model implements IEventHandler<T>
{
    private final VertexLighterFlat lighter;
    private final ResourceLocation modelLocation;

    public AnimationModelBase(ResourceLocation modelLocation, VertexLighterFlat lighter)
    {
        this.modelLocation = modelLocation;
        this.lighter = lighter;
    }

    @SuppressWarnings("unchecked")
    //@Override
    public void render(Entity entity, float limbSwing, float limbSwingSpeed, float timeAlive, float yawHead, float rotationPitch, float scale)
    {
        entity.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null)
            .map(cap -> cap.apply(timeAlive / 20))
            .map(pair -> {
                handleEvents((T) entity, timeAlive / 20, pair.getRight());
                IUnbakedModel unbaked = ModelLoaderRegistry.getModelOrMissing(modelLocation);
                // TODO where should uvlock data come from?
                return unbaked.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), new BasicState(pair.getLeft(), false), DefaultVertexFormats.ITEM);
            }).ifPresent(model -> drawModel(model, entity));
    }

    private void drawModel(IBakedModel bakedModel, Entity entity)
    {
        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.height, entity.posZ);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(180, 0, 0, 1);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        builder.setTranslation(-0.5, -1.5, -0.5);

        lighter.setParent(new VertexBufferConsumer(builder));
        lighter.setWorld(entity.world);
        lighter.setState(Blocks.AIR.getDefaultState());
        lighter.setBlockPos(pos);
        boolean empty = true;
        Random random = new Random();
        random.setSeed(42);
        List<BakedQuad> quads = bakedModel.getQuads(null, null, random);
        if(!quads.isEmpty())
        {
            lighter.updateBlockInfo();
            empty = false;
            for(BakedQuad quad : quads)
            {
                quad.pipe(lighter);
            }
        }
        for(Direction side : Direction.values())
        {
            random.setSeed(42);
            quads = bakedModel.getQuads(null, side, random);
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
        //VertexBuffer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
        //VertexBuffer.pos(0, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
        //VertexBuffer.pos(1, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
        //VertexBuffer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();

        builder.setTranslation(0, 0, 0);

        tessellator.draw();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public void handleEvents(T instance, float time, Iterable<Event> pastEvents) {}
}
*/
