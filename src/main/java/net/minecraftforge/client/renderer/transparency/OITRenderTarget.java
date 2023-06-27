/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer.transparency;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

import java.nio.IntBuffer;

import static com.mojang.blaze3d.platform.GlConst.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL30.GL_R8;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

public class OITRenderTarget extends RenderTarget
{
    public OITRenderTarget(int width, int height)
    {
        super(false);
        createBuffers(width, height, Minecraft.ON_OSX);
    }

    @Override
    public void createBuffers(int width, int height, boolean onOSX)
    {
        RenderSystem.assertOnRenderThreadOrInit();
        this.frameBufferId = GlStateManager.glGenFramebuffers();
        MainTarget.Dimension maintarget$dimension = this.allocateAttachments(width, height);
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, this.frameBufferId);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.colorTextureId, 0);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, this.depthBufferId, 0);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, Minecraft.getInstance().getMainRenderTarget().getDepthTextureId(), 0); // opaque framebuffer's depth texture
        this.viewWidth = maintarget$dimension.width;
        this.viewHeight = maintarget$dimension.height;
        this.width = maintarget$dimension.width;
        this.height = maintarget$dimension.height;
        glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1});
        this.checkStatus();
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private MainTarget.Dimension allocateAttachments(int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.colorTextureId = TextureUtil.generateTextureId();
        this.depthBufferId = TextureUtil.generateTextureId();
        AttachmentState maintarget$attachmentstate = AttachmentState.NONE;


        for(MainTarget.Dimension maintarget$dimension : MainTarget.Dimension.listWithFallback(width, height)) {
            maintarget$attachmentstate = AttachmentState.NONE;
            if (this.allocateAccumulatorTexture(maintarget$dimension)) {
                maintarget$attachmentstate = maintarget$attachmentstate.with(AttachmentState.ACCUMULATOR);
            }

            if (this.allocateRevealAttachment(maintarget$dimension)) {
                maintarget$attachmentstate = maintarget$attachmentstate.with(AttachmentState.REVEAL);
            }

            if (maintarget$attachmentstate == AttachmentState.ACCUMULATOR_REVEAL) {
                return maintarget$dimension;
            }
        }

        throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (allocated attachments = " + maintarget$attachmentstate.name() + ")");
    }

    private boolean allocateAccumulatorTexture(MainTarget.Dimension dimension) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        GlStateManager._bindTexture(this.colorTextureId);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, dimension.width, dimension.height, 0, GL_RGBA, GL_HALF_FLOAT, (IntBuffer)null);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GlStateManager._bindTexture(0);
        return GlStateManager._getError() != 1285;
    }

    private boolean allocateRevealAttachment(MainTarget.Dimension dimensions) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        GlStateManager._bindTexture(this.depthBufferId);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_R8, dimensions.width, dimensions.height, 0, GL_RED, GL_FLOAT, (IntBuffer)null);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GlStateManager._bindTexture(0);
        return GlStateManager._getError() != 1285;
    }

    enum AttachmentState {
        NONE,
        ACCUMULATOR,
        REVEAL,
        ACCUMULATOR_REVEAL;

        private static final AttachmentState[] VALUES = values();

        AttachmentState with(AttachmentState p_166164_) {
            return VALUES[this.ordinal() | p_166164_.ordinal()];
        }
    }

    @Override
    public void blitToScreen(int width, int height) {
        this.blitToScreen(width, height, true);
    }

    @Override
    public void blitToScreen(int width, int height, boolean blendingDisabled) {
        RenderSystem.assertOnGameThreadOrInit();
        if (!RenderSystem.isInInitPhase()) {
            RenderSystem.recordRenderCall(() -> {
                this._blitToScreen(width, height);
            });
        } else {
            this._blitToScreen(width, height);
        }

    }

    private void _blitToScreen(int width, int height) {
        RenderSystem.assertOnRenderThread();

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);

        ShaderInstance shaderinstance = OITLevelRenderer.getInstance().getCompositionShader();
        shaderinstance.setSampler("accum", this.colorTextureId);
        shaderinstance.setSampler("reveal", this.depthBufferId);
        Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)width, (float)height, 0.0F, 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
        if (shaderinstance.MODEL_VIEW_MATRIX != null) {
            shaderinstance.MODEL_VIEW_MATRIX.set((new Matrix4f()).translation(0.0F, 0.0F, -2000.0F));
        }

        if (shaderinstance.PROJECTION_MATRIX != null) {
            shaderinstance.PROJECTION_MATRIX.set(matrix4f);
        }

        shaderinstance.apply();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA);

        float f = (float)width;
        float f1 = (float)height;
        float f2 = (float)this.viewWidth / (float)this.width;
        float f3 = (float)this.viewHeight / (float)this.height;
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, (double)f1, 0.0D).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex((double)f, (double)f1, 0.0D).uv(f2, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex((double)f, 0.0D, 0.0D).uv(f2, f3).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, f3).color(255, 255, 255, 255).endVertex();
        BufferUploader.draw(bufferbuilder.end());
        shaderinstance.clear();
        GlStateManager._enableDepthTest();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
        glDepthFunc(GL_LEQUAL);
    }
}
