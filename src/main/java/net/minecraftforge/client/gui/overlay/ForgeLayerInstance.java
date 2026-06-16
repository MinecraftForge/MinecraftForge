/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import java.util.Objects;
import java.util.Stack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;

import org.jetbrains.annotations.ApiStatus;

// This should be accessed via Gui
public abstract class ForgeLayerInstance {
    public void pushLayer(Screen screen) {
        if (screen() != null)
            guiLayers.push(screen());
        setScreenInternal(Objects.requireNonNull(screen));
        screen.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        minecraft.getNarrator().saySystemNow(screen.getNarrationMessage());
    }

    public void popLayer() {
        if (guiLayers.isEmpty()) {
            minecraft.gui.setScreen(null);
            return;
        }

        popLayerInternal();
        if (screen() != null)
            minecraft.getNarrator().saySystemNow(screen().getNarrationMessage());
    }

    // Below here is private implementation details

    /**
     * Contains the *extra* GUI layers.
     * The current top layer stays in Minecraft#currentScreen, and the rest serve as a background for it.
     */
    private final Stack<Screen> guiLayers = new Stack<>();
    private final Minecraft minecraft;

    @ApiStatus.Internal
    protected ForgeLayerInstance(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @ApiStatus.Internal
    protected abstract Screen screen();
    @ApiStatus.Internal
    protected abstract void setScreenInternal(Screen value);

    @ApiStatus.Internal
    public void resizeLayers(int width, int height) {
        guiLayers.forEach(screen -> screen.resize(width, height));
    }

    @ApiStatus.Internal
    protected void clearLayers() {
        while (!guiLayers.isEmpty())
            popLayerInternal();
    }

    @ApiStatus.Internal
    private void popLayerInternal() {
        if (screen() != null)
            screen().removed();
        setScreenInternal(guiLayers.pop());
    }

    @ApiStatus.Internal
    protected void drawScreen(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushMatrix();
        for (Screen layer : guiLayers) {
            // Prevent the background layers from thinking the mouse is over their controls and showing them as highlighted.
            drawScreenInternal(layer, guiGraphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTick);
            //guiGraphics.pose().translate(0, 0, 10000);
        }
        drawScreenInternal(screen(), guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popMatrix();
    }

    @ApiStatus.Internal
    private static void drawScreenInternal(Screen screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!ScreenEvent.Render.Pre.BUS.post(new ScreenEvent.Render.Pre(screen, guiGraphics, mouseX, mouseY, partialTick)))
            screen.extractRenderStateWithTooltipAndSubtitles(guiGraphics, mouseX, mouseY, partialTick);
        ScreenEvent.Render.Post.BUS.post(new ScreenEvent.Render.Post(screen, guiGraphics, mouseX, mouseY, partialTick));
    }
}
