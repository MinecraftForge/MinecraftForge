/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

public abstract class RenderLivingEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event
{
    private final LivingEntity entity;
    private final LivingRenderer<T, M> renderer;
    private final float partialRenderTick;
    private final MatrixStack matrixStack;

    public RenderLivingEvent(LivingEntity entity, LivingRenderer<T, M> renderer, float partialRenderTick, MatrixStack matrixStack)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.matrixStack = matrixStack;
    }

    public LivingEntity getEntity() { return entity; }
    public LivingRenderer<T, M> getRenderer() { return renderer; }
    public float getPartialRenderTick() { return partialRenderTick; }
    public MatrixStack getMatrixStack() { return matrixStack; }

    @Cancelable
    public static class Pre<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Pre(LivingEntity entity, LivingRenderer<T, M> renderer, float partialRenderTick, MatrixStack matrixStack){ super(entity, renderer, partialRenderTick, matrixStack); }
    }
    public static class RenderModel<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public RenderModel(LivingEntity entity, LivingRenderer<T, M> renderer, float partialRenderTick, MatrixStack matrixStack) { super(entity, renderer, partialRenderTick, matrixStack);}
    }
    public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Post(LivingEntity entity, LivingRenderer<T, M> renderer, float partialRenderTick, MatrixStack matrixStack){ super(entity, renderer, partialRenderTick, matrixStack); }
    }

    // TODO: 1.15 moved all name rendering to EntityRenderer, such that there's not a Living-specific feature anymore
    public abstract static class Specials<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Specials(LivingEntity entity, LivingRenderer<T, M> renderer, MatrixStack matrixStack){ super(entity, renderer, 0, matrixStack); }

        @Cancelable
        public static class Pre<T extends LivingEntity, M extends EntityModel<T>> extends Specials<T, M>
        {
            public Pre(LivingEntity entity, LivingRenderer<T, M> renderer, MatrixStack matrixStack){ super(entity, renderer, matrixStack); }
        }
        public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends Specials<T, M>
        {
            public Post(LivingEntity entity, LivingRenderer<T, M> renderer, MatrixStack matrixStack){ super(entity, renderer,  matrixStack); }
        }
    }
}
