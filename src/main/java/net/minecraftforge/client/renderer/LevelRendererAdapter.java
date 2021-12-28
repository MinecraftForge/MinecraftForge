package net.minecraftforge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfig;

import java.util.Collection;

/**
 * Adapter class which is used to manage the custom render types when they are rendered in the world.
 */
public final class LevelRendererAdapter
{
    private static final LevelRendererAdapter INSTANCE = new LevelRendererAdapter();

    public static LevelRendererAdapter getInstance()
    {
        return INSTANCE;
    }

    private LevelRendererAdapter()
    {
    }

    /**
     * Invoked by the renderer when it is time to render the blocks which are solid.
     *
     * @return {@code true} when the renderer should not render the blocks, {@code false} otherwise.
     */
    public boolean onRenderSolids(
      final LevelRenderer levelRenderer,
      final PoseStack poseStack,
      final Camera camera,
      final Matrix4f projectionMatrix,
      final ProfilerFiller profilerFiller
    ) {
        if (!ForgeConfig.CLIENT.useLevelRendererAdapter.get())
            return false;

        doRenderPhases(LevelRenderPhaseManager.getInstance().getSolidPhases(), camera, levelRenderer, poseStack, projectionMatrix, profilerFiller);

        return true;
    }

    /**
     * Invoked by the renderer when it is time to render the blocks which are translucent.
     *
     * @return {@code true} when the renderer should not render the blocks, {@code false} otherwise.
     */
    public boolean onRenderTranslucent(
      final LevelRenderer levelRenderer,
      final PoseStack poseStack,
      final Camera camera,
      final Matrix4f projectionMatrix,
      final ProfilerFiller profilerFiller
    ) {
        if (!ForgeConfig.CLIENT.useLevelRendererAdapter.get())
            return false;

        doRenderPhases(LevelRenderPhaseManager.getInstance().getTranslucentPhases(), camera, levelRenderer, poseStack, projectionMatrix, profilerFiller);

        return true;
    }

    /**
     * Invoked by the renderer when it is time to render the blocks which are tripwire.
     *
     * @return {@code true} when the renderer should not render the blocks, {@code false} otherwise.
     */
    public boolean onRenderTripwire(
      final LevelRenderer levelRenderer,
      final PoseStack poseStack,
      final Camera camera,
      final Matrix4f projectionMatrix,
      final ProfilerFiller profilerFiller
    ) {
        if (!ForgeConfig.CLIENT.useLevelRendererAdapter.get())
            return false;

        doRenderPhases(LevelRenderPhaseManager.getInstance().getTripwirePhases(), camera, levelRenderer, poseStack, projectionMatrix, profilerFiller);

        return true;
    }

    private void doRenderPhases(
      final Collection<RenderType> phases,
      final Camera camera,
      final LevelRenderer levelRenderer,
      final PoseStack poseStack,
      final Matrix4f projectionMatrix,
      final ProfilerFiller profilerFiller
    ) {
        final Vec3 cameraPos = camera.getPosition();
        final double cameraX = cameraPos.x;
        final double cameraY = cameraPos.y;
        final double cameraZ = cameraPos.z;

        for (final RenderType renderType : phases)
        {
            profilerFiller.push(renderType.name);
            levelRenderer.renderChunkLayer(
              renderType,
              poseStack,
              cameraX,
              cameraY,
              cameraZ,
              projectionMatrix
            );
            profilerFiller.pop();
        }
    }
}
