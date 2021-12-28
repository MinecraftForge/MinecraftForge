package net.minecraftforge.client.renderer;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public final class ComplexRenderTypeBuilder
{
    private static final IBeforeChunkRenderCallback DEFAULT_BEFORE_CHUNK_CALLBACK = (levelRenderer, renderChunk, poseStack, cameraX, cameraY, cameraZ, projectionMatrix) -> {
        //Noop
    };

    private IBeforeChunkRenderCallback beforeChunkRenderCallback = DEFAULT_BEFORE_CHUNK_CALLBACK;

    ComplexRenderTypeBuilder()
    {
    }

    public ComplexRenderTypeBuilder beforeChunkRenderCallback(IBeforeChunkRenderCallback beforeChunkRenderCallback)
    {
        this.beforeChunkRenderCallback = beforeChunkRenderCallback;
        return this;
    }

    public ComplexRenderType build(
      final String name,
      final VertexFormat vertexFormat,
      final VertexFormat.Mode mode,
      final int bufferSize,
      final boolean affectsCrumbling,
      final boolean sortOnUpload,
      final RenderType.CompositeState compositeState
    ) {
        return new ComplexRenderType(
            name, vertexFormat, mode, bufferSize, affectsCrumbling, sortOnUpload, compositeState, beforeChunkRenderCallback
        );
    }
}
