package net.minecraftforge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;

/**
 * An implementation of {@link net.minecraft.client.renderer.RenderType.CompositeRenderType} with
 * the {@link IComplexRenderType} interface.
 * Create using {@link ComplexRenderTypeBuilder}.
 *
 * @see ComplexRenderTypeBuilder
 */
public final class ComplexRenderType extends RenderType.CompositeRenderType implements IComplexRenderType
{
    private final IBeforeChunkRenderCallback beforeChunkRenderCallback;

    public static ComplexRenderTypeBuilder builder()
    {
        return new ComplexRenderTypeBuilder();
    }

    ComplexRenderType(
      final String name,
      final VertexFormat vertexFormat,
      final VertexFormat.Mode mode,
      final int bufferSize,
      final boolean affectsCrumbling,
      final boolean sortOnUpload,
      final CompositeState compositeState,
      final IBeforeChunkRenderCallback beforeChunkRenderCallback
    ) {
        super(name, vertexFormat, mode, bufferSize, affectsCrumbling, sortOnUpload, compositeState);
        this.beforeChunkRenderCallback = beforeChunkRenderCallback;
    }

    @Override
    public void beforeChunkRender(
      final LevelRenderer levelRenderer,
      final ChunkRenderDispatcher.RenderChunk renderChunk,
      final PoseStack poseStack,
      final double cameraX,
      final double cameraY,
      final double cameraZ,
      final Matrix4f projectionMatrix
    ) {
        this.beforeChunkRenderCallback.beforeChunkRender(
          levelRenderer, renderChunk, poseStack, cameraX, cameraY, cameraZ, projectionMatrix
        );
    }
}
