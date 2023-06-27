/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer.transparency;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.ForgeConfig;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.blaze3d.platform.GlStateManager.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.glClearBufferfv;

@ApiStatus.Internal
public final class OITLevelRenderer
{
    private static final OITLevelRenderer INSTANCE = new OITLevelRenderer();

    public static OITLevelRenderer getInstance()
    {
        return INSTANCE;
    }

    private boolean levelRenderingInProgress;
    private OITRenderTarget transparentOITRenderTarget;
    private ShaderInstance blitToScreenShader;

    private final List<IRenderCall> queuedRenderCallList = new ArrayList<>();

    private OITLevelRenderer()
    {
    }

    /**
     * Sets the shader that is used to composite the OIT render target to the screen.
     *
     * @param shader The shader instance.
     */
    void setBlitToScreenShader(ShaderInstance shader)
    {
        this.blitToScreenShader = shader;
    }

    /**
     * Initializes the OIT system.
     */
    public void initialize(final int width, final int height, final boolean onOSX) {
        this.transparentOITRenderTarget = new OITRenderTarget(width, height);
        this.transparentOITRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.transparentOITRenderTarget.clear(onOSX);
    }

    /**
     * Gives access to the render target that is used for OIT rendering.
     *
     * @return The render target. This is a custom implementation.
     */
    public RenderTarget getTransparentOITRenderTarget()
    {
        return transparentOITRenderTarget;
    }

    /**
     * The final shader used to composite the OIT render target to the screen.
     *
     * @return The shader instance.
     */
    public ShaderInstance getCompositionShader()
    {
        return blitToScreenShader;
    }

    /**
     * Indicates if the OIT rendering for level rendering is being handled.
     * <p>
     * When true, then render calls can be captured for transparent render types which require sorting.
     *
     * @return True, when level rendering is active, false when not.
     */
    public boolean isLevelRenderingInProgress()
    {
        return levelRenderingInProgress;
    }

    /**
     * Sets the OIT rendering indicator for level rendering.
     *
     * @param levelRenderingInProgress True enables OIT rendering capture for level rendering, false disables it.
     */
    private void setLevelRenderingInProgress(boolean levelRenderingInProgress)
    {
        this.levelRenderingInProgress = levelRenderingInProgress;
    }

    /**
     * Performs a check if the given cpu bound buffer render type is used during level rendering and requires transparency sorting.
     * <p>
     * If this is the case then the render call is postponed and queued up to be rendered through OIT.
     * If not then the render is immediately queued on the GPU.
     *
     * @param renderType The render type with which the rendering should happen.
     * @param renderedBuffer The CPU side payload buffer.
     */
    public void checkHandlesThenQueueOrRender(RenderType renderType, BufferBuilder.RenderedBuffer renderedBuffer)
    {
        final IRenderTypeBasedRenderCall call = new RenderedBufferRenderCall(renderType, renderedBuffer, new Matrix4f(RenderSystem.getModelViewMatrix()), new Matrix4f(RenderSystem.getProjectionMatrix()));
        checkHandlesThenQueueOrRender(call);
    }

    /**
     * Performs a check if the given chunk geometry render type is used during level rendering and requires transparency sorting.
     * <p>
     * If this is the case then the render call is postponed and queued up to be rendered through OIT.
     * If not then the render is immediately queued on the GPU.
     *
     * @param renderType The render type with which the rendering should happen.
     * @param currentPoseStack The current pose stack.
     * @param cameraX The camera X position.
     * @param cameraY The camera Y position.
     * @param cameraZ The camera Z position.
     * @param projectionMatrix The projection matrix.
     */
    public void checkHandlesThenQueueOrRender(RenderType renderType, PoseStack currentPoseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix)
    {
        //We make a defensive copy so that the caller can't modify the matrix after we've captured it.
        final IRenderTypeBasedRenderCall call = new ChunkGeometryRenderCall(renderType, Util.make(() -> {
            final PoseStack stack = new PoseStack();
            stack.last().pose().set(currentPoseStack.last().pose());
            stack.last().normal().set(currentPoseStack.last().normal());
            return stack;
        }), cameraX, cameraY, cameraZ, Util.make(() -> {
            final Matrix4f matrix = new Matrix4f();
            matrix.set(projectionMatrix);
            return matrix;
        }));
        checkHandlesThenQueueOrRender(call);
    }

    /**
     * Performs a check if the given particle render type is used during level rendering and requires transparency sorting.
     * <p>
     * If this is the case then the render call is postponed and queued up to be rendered through OIT.
     * If not then the render is immediately queued on the GPU.
     *
     * @param particleRenderType The render type with which the rendering should happen.
     * @param toRender The particles to render.
     * @param clippingHelper The clipping helper.
     * @param camera The camera.
     * @param partialTickTime The partial tick time.
     */
    public void checkHandlesThenQueueOrRender(final ParticleRenderType particleRenderType, final Iterable<Particle> toRender, @Nullable final Frustum clippingHelper, Camera camera, float partialTickTime) {
        final IParticleRenderCall call = new ParticleRenderCall(particleRenderType, toRender, clippingHelper, camera, partialTickTime);
        checkHandlesThenQueueOrRender(call);
    }

    /**
     * Internal method to check if the given render call based on a render type is handled by the OIT Handler.
     * <p>
     * If handled, the call will be queued up for later rendering.
     * If not handled, the call will be rendered directly.
     *
     * @param renderCall The render call.
     */
    private void checkHandlesThenQueueOrRender(IRenderTypeBasedRenderCall renderCall)
    {
        if (handles(renderCall.renderType()))
        {
            queuedRenderCallList.add(renderCall);
        }
        else
        {
            renderCall.drawDirect();
        }
    }

    /**
     * Internal method to check if the given render call based on a particle render type is handled by the OIT Handler.
     * <p>
     * If handled, the call will be queued up for later rendering.
     * If not handled, the call will be rendered directly.
     *
     * @param renderCall The render call.
     */
    private void checkHandlesThenQueueOrRender(IParticleRenderCall renderCall)
    {
        if (handles(renderCall.particleRenderType()))
        {
            queuedRenderCallList.add(renderCall);
        }
        else
        {
            renderCall.drawDirect();
        }
    }

    /**
     * Indicates whether the OIT Handler handles the given render type's render call.
     * This will only return true if {@link #willHandle(RenderType)} and {@link #isLevelRenderingInProgress()} are both true.
     *
     * @param renderType The render type to check.
     * @return True when the handler will queue the render type's render call for OIT handler, false when rendering is immediately queued on the GPU.
     */
    public boolean handles(RenderType renderType)
    {
        return willHandle(renderType) && isLevelRenderingInProgress();
    }

    /**
     * Indicates whether the OIT Handler handles the given render type's render call.
     * This will only return true if {@link #willHandle(RenderType)} and {@link #isLevelRenderingInProgress()} are both true.
     *
     * @param renderType The render type to check.
     * @return True when the handler will queue the render type's render call for OIT handler, false when rendering is immediately queued on the GPU.
     */
    public boolean handles(ParticleRenderType renderType)
    {
        return willHandle(renderType) && isLevelRenderingInProgress();
    }

    /**
     * Indicates whether the OIT Handler will handle the given render type's render call.
     * For now this is only true when the render type uses translucent transparency and requires sorting.
     * We are still researching options for other transparency types.
     *
     * @param renderType The render type to check.
     * @return True when the handler will queue the render type's render call for OIT handler, false when rendering is immediately queued on the GPU.
     */
    public boolean willHandle(RenderType renderType) {
        if (!ForgeConfig.CLIENT.orderIndependentTransparentRendering.get())
            return false;

        if (this.transparentOITRenderTarget == null)
            return false;

        if (!renderType.sortOnUpload)
            return false;

        if (!(renderType instanceof RenderType.CompositeRenderType compositeRenderType))
            return false;

        //We can only handle ALPHA Transparency for now.
        if(compositeRenderType.state().transparencyState != RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            return false;

        //We need an adapted shader, which is indicated by the presence of the OIT_ENABLE uniform.
        return compositeRenderType.state().shaderState.shader.filter(shaderInstanceSupplier -> shaderInstanceSupplier.get().OIT_ENABLE != null).isPresent();
    }

    /**
     * Indicates whether the OIT Handler will handle the given render type's render call.
     * For now this is only true when the render type uses translucent transparency and requires sorting.
     * We are still researching options for other transparency types.
     *
     * @param renderType The render type to check.
     * @return True when the handler will queue the render type's render call for OIT handler, false when rendering is immediately queued on the GPU.
     */
    public boolean willHandle(ParticleRenderType renderType) {
        if (!ForgeConfig.CLIENT.orderIndependentTransparentRendering.get())
            return false;

        //We can only handle the types for which OIT is explicitly enabled.
        //Additionally check for the particle shader to be adapted.
        return this.transparentOITRenderTarget != null && renderType.requiresTransparencySorting() &&
                GameRenderer.getParticleShader() != null && GameRenderer.getParticleShader().OIT_ENABLE != null;
    }

    /**
     * Called to start the level rendering process.
     * Initializes the OIT rendering process.
     */
    public void startLevelRendering(ProfilerFiller profilerfiller)
    {
        if (isLevelRenderingInProgress())
            return;

        if (!ForgeConfig.CLIENT.orderIndependentTransparentRendering.get())
            return;

        profilerfiller.popPush("oit_rendering_setup");

        setLevelRenderingInProgress(true);

        transparentOITRenderTarget.bindWrite(true);
        glClearBufferfv(GL_COLOR, 0, new float[]{0f, 0f, 0f, 1f});
        glClearBufferfv(GL_COLOR, 1, new float[]{0f, 0f, 0f, 1f});
        transparentOITRenderTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    /**
     * Called to end the level rendering process.
     * Finalizes the OIT rendering process.
     * Should be called immediately after the transparent rendering is done.
     */
    public void endLevelRendering(ProfilerFiller profilerfiller)
    {
        if (!isLevelRenderingInProgress())
            return;

        if (!ForgeConfig.CLIENT.orderIndependentTransparentRendering.get())
            return;

        profilerfiller.popPush("oit_rendering_finalize");

        setLevelRenderingInProgress(false);
        renderQueue(profilerfiller);
        queuedRenderCallList.clear();
    }

    /**
     * Renders the queued render calls.
     *
     * @param profilerfiller The profiler to use.
     */
    private void renderQueue(ProfilerFiller profilerfiller) {
        if (queuedRenderCallList.isEmpty()) {
            return;
        }

        profilerfiller.push("oit_rendering_collect");

        transparentOITRenderTarget.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());

        for (IRenderCall renderCall : queuedRenderCallList) {
            renderCall.drawOIT();
        }

        profilerfiller.popPush("oit_rendering_compose");

        transparentOITRenderTarget.unbindWrite();

        transparentOITRenderTarget.blitToScreen(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());

        profilerfiller.pop();
    }

    /**
     * Callback to set up rendering to the OIT render target.
     *
     * @param shaderInstance The shader instance to set up.
     * @param shouldUpload Whether the uniform values should be uploaded.
     */
    private void setupTransparentRendering(ShaderInstance shaderInstance, boolean shouldUpload) {
        if (shaderInstance.OIT_ENABLE != null) {
            shaderInstance.OIT_ENABLE.set(1);
            if (shouldUpload) {
                shaderInstance.OIT_ENABLE.upload();
            }
        }
        else {
            return;
        }

        transparentOITRenderTarget.bindWrite(true);

        RenderSystem.enableDepthTest();
        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_ONE, GL_ONE, GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Callback to clean up rendering to the OIT render target.
     *
     * @param shaderInstance The shader instance to clean up.
     */
    private void cleanUpTransparentRendering(ShaderInstance shaderInstance) {
        if (shaderInstance.OIT_ENABLE != null) {
            shaderInstance.OIT_ENABLE.set(0);
        }

        glDepthMask(true);
        RenderSystem.defaultBlendFunc();
        glDisable(GL_BLEND);
    }

    /**
     * Defines a render call that can be queued up.
     */
    private interface IRenderCall {
        void drawDirect();

        void drawOIT();
    }

    /**
     * Defines a render call that can be queued up, based on a render type.
     */
    private interface IRenderTypeBasedRenderCall extends IRenderCall {
        RenderType renderType();
    }

    /**
     * Defines a render call that can be queued up, based on a particle render type.
     */
    private interface IParticleRenderCall extends IRenderCall {
        ParticleRenderType particleRenderType();
    }

    /**
     * A render call for a CPU rendered buffer.
     * Captures all related information to render the buffer.
     *
     * @param renderType The render type to use.
     * @param renderedBuffer The rendered buffer to render.
     * @param modelViewMatrix The model view matrix to use.
     * @param projectionMatrix The projection matrix to use.
     */
    private record RenderedBufferRenderCall(RenderType renderType, BufferBuilder.RenderedBuffer renderedBuffer, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) implements IRenderTypeBasedRenderCall {
        @Override
        public void drawDirect()
        {
            renderType.doRender(renderedBuffer, (shader) -> {}, (shader) -> {});
        }

        @Override
        public void drawOIT()
        {
            final Matrix4f currentModelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
            final Matrix4f currentProjectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
            renderType.doRender(renderedBuffer, shader ->
            {
                RenderSystem.getModelViewMatrix().set(modelViewMatrix);
                RenderSystem.getProjectionMatrix().set(projectionMatrix);
                getInstance().setupTransparentRendering(shader, false);
            }, getInstance()::cleanUpTransparentRendering);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.getModelViewMatrix().set(currentModelViewMatrix);
            RenderSystem.getProjectionMatrix().set(currentProjectionMatrix);
        }
    }

    /**
     * A render call for GPU bound chunk geometry.
     * Captures all related information to render the chunk geometry.
     *
     * @param renderType The render type to use.
     * @param currentPoseStack The current pose stack to use.
     * @param cameraX The camera X position.
     * @param cameraY The camera Y position.
     * @param cameraZ The camera Z position.
     * @param projectionMatrix The projection matrix to use.
     */
    private record ChunkGeometryRenderCall(RenderType renderType, PoseStack currentPoseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix) implements IRenderTypeBasedRenderCall {
        @Override
        public void drawDirect()
        {
            Minecraft.getInstance().levelRenderer.renderChunkGeometryForLayer(renderType, currentPoseStack, cameraX, cameraY, cameraZ, projectionMatrix, (shader) -> {}, (shader) -> {});
        }

        @Override
        public void drawOIT()
        {
            Minecraft.getInstance().levelRenderer.renderChunkGeometryForLayer(renderType, currentPoseStack, cameraX, cameraY, cameraZ, projectionMatrix, shader -> getInstance().setupTransparentRendering(shader, true), getInstance()::cleanUpTransparentRendering);
        }
    }

    /**
     * A render call to render particles.
     * Captures all related information to render the particles.
     *
     * @param particleRenderType The particle render type to use.
     * @param toRender The particles to render.
     * @param clippingHelper The clipping helper to use.
     * @param camera The camera to use.
     * @param partialTickTime The partial tick time to use.
     */
    private record ParticleRenderCall(ParticleRenderType particleRenderType, Iterable<Particle> toRender, @Nullable Frustum clippingHelper, Camera camera, float partialTickTime) implements IParticleRenderCall {

        @Override
        public void drawDirect()
        {
            Minecraft.getInstance().particleEngine.renderParticlesWithType(particleRenderType, toRender, clippingHelper, camera, partialTickTime, (shader) -> {}, (shader) -> {});
        }

        @Override
        public void drawOIT()
        {
            Minecraft.getInstance().particleEngine.renderParticlesWithType(particleRenderType, toRender, clippingHelper, camera, partialTickTime, shader ->
            {
                //We need to explicitly enable the light layer, as it is enabled by default when rendering particles.
                Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                getInstance().setupTransparentRendering(shader, false);
            }, getInstance()::cleanUpTransparentRendering);
        }
    }
}
