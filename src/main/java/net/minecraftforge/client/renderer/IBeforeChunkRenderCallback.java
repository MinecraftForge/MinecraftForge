package net.minecraftforge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;

/**
 * Interface which represents a callback that is invoked by the {@link LevelRenderer} before
 * a non-empty chunk is rendered. This is useful for uploading additional uniforms to the GPU,
 * or modifying the current GPU state based on what chunk is being rendered.
 *
 * For use with render types, must be passed to a {@link ComplexRenderTypeBuilder}.
 * This render type must be registered via a {@link net.minecraftforge.client.event.LayerRenderTypeRegisterEvent}
 * of the appropriate phase.
 */
@FunctionalInterface
public interface IBeforeChunkRenderCallback
{
    /**
     * Invoked by the level renderer to indicate that a chunk is about to be rendered.
     *
     * @param levelRenderer The renderer.
     * @param renderChunk The chunk that is about to be rendered.
     * @param poseStack The current pose stack.
     * @param cameraX The camera x position.
     * @param cameraY The camera y position.
     * @param cameraZ The camera z position.
     * @param projectionMatrix The projection matrix.
     */
    void beforeChunkRender(
      LevelRenderer levelRenderer,
      ChunkRenderDispatcher.RenderChunk renderChunk,
      PoseStack poseStack,
      double cameraX,
      double cameraY,
      double cameraZ,
      Matrix4f projectionMatrix
    );
}
