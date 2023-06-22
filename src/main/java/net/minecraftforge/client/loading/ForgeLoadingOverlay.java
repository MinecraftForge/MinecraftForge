/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.loading;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.earlydisplay.ColourScheme;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.mojang.blaze3d.platform.GlConst.*;
import static net.minecraft.util.Mth.clamp;
import static org.lwjgl.opengl.GL30C.glViewport;
import static org.lwjgl.opengl.GL30C.glTexParameterIi;

/**
 * This is an implementation of the LoadingOverlay that calls back into the early window rendering, as part of the
 * game loading cycle. We completely replace the {@link #render(GuiGraphics, int, int, float)} call from the parent
 * with one of our own, that allows us to blend our early loading screen into the main window, in the same manner as
 * the Mojang screen. It also allows us to see and tick appropriately as the later stages of the loading system run.
 *
 * It is somewhat a copy of the superclass render method.
 */
public class ForgeLoadingOverlay extends LoadingOverlay {
    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final Consumer<Optional<Throwable>> onFinish;
    private final DisplayWindow displayWindow;
    private final ProgressMeter progress;
    private long fadeOutStart = -1L;

    public ForgeLoadingOverlay(final Minecraft mc, final ReloadInstance reloader, final Consumer<Optional<Throwable>> errorConsumer, DisplayWindow displayWindow) {
        super(mc, reloader, errorConsumer, false);
        this.minecraft = mc;
        this.reload = reloader;
        this.onFinish = errorConsumer;
        this.displayWindow = displayWindow;
        displayWindow.addMojangTexture(mc.getTextureManager().getTexture(new ResourceLocation("textures/gui/title/mojangstudios.png")).getId());
        this.progress = StartupMessageManager.prependProgressBar("Minecraft Progress", 100);
    }

    public static Supplier<LoadingOverlay> newInstance(Supplier<Minecraft> mc, Supplier<ReloadInstance> ri, Consumer<Optional<Throwable>> handler, DisplayWindow window) {
        return ()->new ForgeLoadingOverlay(mc.get(), ri.get(), handler, window);
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        long millis = Util.getMillis();
        float fadeouttimer = this.fadeOutStart > -1L ? (float)(millis - this.fadeOutStart) / 1000.0F : -1.0F;
        progress.setAbsolute(clamp((int)(this.reload.getActualProgress() * 100f), 0, 100));
        var fade = 1.0F - clamp(fadeouttimer - 1.0F, 0.0F, 1.0F);
        var colour = this.displayWindow.context().colourScheme().background();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fade);
        if (fadeouttimer >= 1.0F) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.render(graphics, 0, 0, partialTick);
            }
            displayWindow.render(0xff);
        } else {
            GlStateManager._clearColor(colour.redf(), colour.greenf(), colour.bluef(), 1f);
            GlStateManager._clear(GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
            displayWindow.render(0xFF);
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        var fbWidth = this.minecraft.getWindow().getWidth();
        var fbHeight = this.minecraft.getWindow().getHeight();
        glViewport(0, 0, fbWidth, fbHeight);
        final var twidth = this.displayWindow.context().width();
        final var theight = this.displayWindow.context().height();
        var wscale = (float)fbWidth / twidth;
        var hscale = (float)fbHeight / theight;
        var scale = this.displayWindow.context().scale() * Math.min(wscale, hscale) / 2f;
        var wleft = clamp(fbWidth * 0.5f - scale * twidth, 0, fbWidth);
        var wtop = clamp(fbHeight * 0.5f - scale * theight, 0, fbHeight);
        var wright = clamp(fbWidth * 0.5f + scale * twidth, 0, fbWidth);
        var wbottom = clamp(fbHeight * 0.5f + scale * theight, 0, fbHeight);
        GlStateManager.glActiveTexture(GL_TEXTURE0);
        RenderSystem.disableCull();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fade);
        RenderSystem.getModelViewMatrix().identity();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, fbWidth, 0.0F, fbHeight, 0.1f, -0.1f), VertexSorting.ORTHOGRAPHIC_Z);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        // This is fill in around the edges - it's empty solid colour
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // top box from hpos
        addQuad(bufferbuilder, 0, fbWidth, wtop, fbHeight, colour, fade);
        // bottom box to hpos
        addQuad(bufferbuilder, 0, fbWidth, 0, wtop, colour, fade);
        // left box to wpos
        addQuad(bufferbuilder, 0, wleft, wtop, wbottom, colour, fade);
        // right box from wpos
        addQuad(bufferbuilder, wright, fbWidth, wtop, wbottom, colour, fade);
        BufferUploader.drawWithShader(bufferbuilder.end());

        // This is the actual screen data from the loading screen
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, displayWindow.getFramebufferTextureId());
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(wleft, wbottom, 0f).uv(0, 0).color(1f, 1f, 1f, fade).endVertex();
        bufferbuilder.vertex(wright, wbottom, 0f).uv(1, 0).color(1f, 1f, 1f, fade).endVertex();
        bufferbuilder.vertex(wright, wtop, 0f).uv(1, 1).color(1f, 1f, 1f, fade).endVertex();
        bufferbuilder.vertex(wleft, wtop, 0f).uv(0, 1).color(1f, 1f, 1f, fade).endVertex();
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);

        if (fadeouttimer >= 2.0F) {
            this.minecraft.setOverlay(null);
            this.displayWindow.close();
        }

        if (this.fadeOutStart == -1L && this.reload.isDone()) {
            progress.complete();
            this.fadeOutStart = Util.getMillis();
            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.onFinish.accept(Optional.of(throwable));
            }

            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            }
        }
    }

    private static void addQuad(BufferVertexConsumer bufferbuilder, float x0, float x1, float y0, float y1, ColourScheme.Colour colour, float fade) {
        bufferbuilder.vertex(x0, y0, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
        bufferbuilder.vertex(x0, y1, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
        bufferbuilder.vertex(x1, y1, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
        bufferbuilder.vertex(x1, y0, 0f).color(colour.redf(), colour.greenf(), colour.bluef(), fade).endVertex();
    }
}
