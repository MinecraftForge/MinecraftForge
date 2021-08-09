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

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before a selection highlight is rendered.
 * See the two subclasses to listen for blocks or entities.
 *
 * @see DrawSelectionEvent.HighlightBlock
 * @see DrawSelectionEvent.HighlightEntity
 * @see net.minecraftforge.client.ForgeHooksClient#onDrawHighlight(LevelRenderer, Camera, HitResult, float, PoseStack, MultiBufferSource)
 */
@Cancelable
public class DrawSelectionEvent extends Event
{
    private final LevelRenderer levelRenderer;
    private final Camera camera;
    private final HitResult hitResult;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;

    public DrawSelectionEvent(LevelRenderer levelRenderer, Camera camera, HitResult hitResult, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
    {
        this.levelRenderer = levelRenderer;
        this.camera = camera;
        this.hitResult = hitResult;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
    }

    /**
     * {@return the level renderer}
     */
    public LevelRenderer getLevelRenderer()
    {
        return levelRenderer;
    }

    /**
     * {@return the camera information}
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * {@return the hit result which triggered the selection highlight}
     */
    public HitResult getHitResult()
    {
        return hitResult;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    /**
     * Fired before a block's selection highlight is rendered.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. <br/>
     * If the event is cancelled, then the selection highlight will not be rendered. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    @Cancelable
    public static class HighlightBlock extends DrawSelectionEvent
    {
        public HighlightBlock(LevelRenderer levelRenderer, Camera camera, BlockHitResult hitResult, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, hitResult, partialTick, poseStack, bufferSource);
        }

        /**
         * {@return the block hit result}
         */
        @Override
        public BlockHitResult getHitResult()
        {
            return (BlockHitResult) super.hitResult;
        }
    }

    /**
     * Fired before a block's selection highlight is rendered.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. <br/>
     * Cancelling this event has no effect. </p>
     *
     * TODO: this event cannot be fired because of where the hook is called; remove this event or move the hook
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    @Cancelable
    public static class HighlightEntity extends DrawSelectionEvent
    {
        public HighlightEntity(LevelRenderer levelRenderer, Camera camera, EntityHitResult hitResult, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
        {
            super(levelRenderer, camera, hitResult, partialTick, poseStack, bufferSource);
        }

        /**
         * {@return the entity hit result}
         */
        @Override
        public EntityHitResult getHitResult()
        {
            return (EntityHitResult) super.hitResult;
        }
    }
}
