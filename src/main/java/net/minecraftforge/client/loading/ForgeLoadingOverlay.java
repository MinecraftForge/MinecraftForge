/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.loading;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraftsingularity.client.singularityRenderTypes;
import net.minecraftsingularity.fml.StartupMessageManager;
import net.minecraftsingularity.fml.earlydisplay.DisplayWindow;
import net.minecraftsingularity.fml.loading.progress.ProgressMeter;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This is an implementation of the LoadingOverlay that calls back into the early window rendering, as part of the
 * game loading cycle. We completely replace the {@link #render(GuiGraphics, int, int, float)} call from the parent
 * with one of our own, that allows us to blend our early loading screen into the main window, in the same manner as
 * the Mojang screen. It also allows us to see and tick appropriately as the later stages of the loading system run.
 *
 * It is somewhat a copy of the superclass render method.
 */
@SuppressWarnings("unused")
public class singularityLoadingOverlay extends LoadingOverlay {
    private static final boolean ENABLE = false; //Boolean.parseBoolean("singularity.enableForgeLoadingOverlay");
    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final DisplayWindow displayWindow;
    private final ProgressMeter progress;
    private final RenderType earlyBuffer;

    public singularityLoadingOverlay(final Minecraft mc, final ReloadInstance reloader, final Consumer<Optional<Throwable>> errorConsumer, DisplayWindow displayWindow) {
        super(mc, reloader, errorConsumer, false);
        this.minecraft = mc;
        this.reload = reloader;
        this.displayWindow = displayWindow;
        var texture = mc.getTextureManager().getTexture(MOJANG_STUDIOS_LOGO_LOCATION);
        var glTexture = (GlTexture)texture.getTexture();
        displayWindow.addMojangTexture(glTexture.glId());
        this.progress = StartupMessageManager.prependProgressBar("Minecraft Progress", 100);
        this.earlyBuffer = singularityRenderTypes.getLoadingOverlay(displayWindow);
    }

    public static Supplier<LoadingOverlay> newInstance(Supplier<Minecraft> mc, Supplier<ReloadInstance> ri, Consumer<Optional<Throwable>> handler, DisplayWindow window) {
        return ()->new singularityLoadingOverlay(mc.get(), ri.get(), handler, window);
    }

    @Override
    protected boolean extractContentRenderState(final GuiGraphicsExtractor graphics, float fade) {
        if (!ENABLE)
            return true;
        /* This should render the framebuffer but it doesnt work in 1.21.6's rendering changes.
         * The proper way to fix this is to just kill off the display window when Vanilla gets to this phase, and render our extra elements normally.
         * TODO: [singularity][Rendering] Render only out elements from the loading screen
         *
        progress.setAbsolute(Mth.clamp((int)(this.reload.getActualProgress() * 100f), 0, 100));

        int alpha = (int)(fade * 255);
        this.displayWindow.render(alpha);

        int width = gui.guiWidth();
        int height = gui.guiHeight();

        var fbWidth = this.minecraft.getWindow().getWidth();
        var fbHeight = this.minecraft.getWindow().getHeight();
        GL30C.glViewport(0, 0, fbWidth, fbHeight);

        var buf = gui.getBufferSource().getBuffer(earlyBuffer);
        buf.addVertex(pos, 0,     0,      0f).setUv(0, 0).setColor(1f, 1f, 1f, fade);
        buf.addVertex(pos, 0,     height, 0f).setUv(0, 1).setColor(1f, 1f, 1f, fade);
        buf.addVertex(pos, width, height, 0f).setUv(1, 1).setColor(1f, 1f, 1f, fade);
        buf.addVertex(pos, width, 0,      0f).setUv(1, 0).setColor(1f, 1f, 1f, fade);
        */

        return false;
    }
}
