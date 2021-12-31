package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * on {@link net.minecraftforge.api.distmarker.Dist#CLIENT} when geometry
 * is being collected from its respective sources.
 *
 * @see ChunkSectionStatic
 * @see LevelDynamic
 */
public abstract class GatherGeometryEvent extends Event
{
    private final MultiBufferSource bufferSource;

    protected GatherGeometryEvent(MultiBufferSource bufferSource)
    {
        this.bufferSource = bufferSource;
    }

    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    /**
     * Fired when the client is gathering static geometry and
     * occlusion data for a 16x16x16 section of a chunk.
     */
    public static class ChunkSectionStatic extends GatherGeometryEvent
    {
        @Nullable
        private final RenderChunkRegion region;
        private final BlockPos.MutableBlockPos origin;
        private final VisGraph visibilityGraph;

        public ChunkSectionStatic(MultiBufferSource bufferSource, @Nullable RenderChunkRegion region, BlockPos.MutableBlockPos origin, VisGraph visibilityGraph)
        {
            super(bufferSource);
            this.region = region;
            this.origin = origin;
            this.visibilityGraph = visibilityGraph;
        }

        @Nullable
        public RenderChunkRegion getRegion()
        {
            return region;
        }

        public BlockPos.MutableBlockPos getOrigin()
        {
            return origin;
        }

        /**
         * The visibility graph for this 16x16x16 section of a chunk.
         *
         * This is used to mark areas of a chunk as opaque, and is used by
         * Minecraft to determine whether other chunks are being occluded
         * by this one or not, and thus whether they need to be rendered.
         */
        public VisGraph getVisibilityGraph()
        {
            return visibilityGraph;
        }
    }

    /**
     * Fired when the client is gathering dynamic level geometry.
     */
    public static class LevelDynamic extends GatherGeometryEvent
    {
        private final LevelRenderer levelRenderer;
        private final PoseStack poseStack;
        private final float partialTicks;
        private final Camera camera;

        public LevelDynamic(MultiBufferSource bufferSource, LevelRenderer levelRenderer, PoseStack poseStack,
                            float partialTicks, Camera camera)
        {
            super(bufferSource);
            this.levelRenderer = levelRenderer;
            this.camera = camera;
            this.partialTicks = partialTicks;
            this.poseStack = poseStack;
        }

        public LevelRenderer getLevelRenderer()
        {
            return levelRenderer;
        }

        public PoseStack getPoseStack()
        {
            return poseStack;
        }

        public float getPartialTicks()
        {
            return partialTicks;
        }

        public Camera getCamera()
        {
            return camera;
        }
    }
}
