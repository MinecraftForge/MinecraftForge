/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.loading;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.StartupMessageManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import java.util.Locale;

public class EarlyLoaderGUI {
    private final Minecraft minecraft;
    private final Window window;
    private boolean handledElsewhere;

    public EarlyLoaderGUI(final Minecraft minecraft) {
        this.minecraft = minecraft;
        this.window = minecraft.getWindow();
    }

    private void setupMatrix() {
        RenderSystem.clear(256, Minecraft.ON_OSX);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, window.getWidth() / window.getGuiScale(), window.getHeight() / window.getGuiScale(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public void handleElsewhere() {
        this.handledElsewhere = true;
    }

    void renderFromGUI() {
        renderMessages();
    }

    void renderTick() {
        if (handledElsewhere) return;
        // int guiScale = window.calculateScale(0, false);
        // window.setGuiScale(guiScale);
        //
        // RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
        // GL11.glPushMatrix();
        // setupMatrix();
        // renderBackground();
        // renderMessages();
        // window.updateDisplay();
        // GL11.glPopMatrix();
    }

    private void renderBackground() {
        GL11.glBegin(GL11.GL_QUADS);
        boolean isDarkBackground = minecraft.options.darkMojangStudiosBackground;
        GL11.glColor4f(isDarkBackground ? 0 : (239F / 255F), isDarkBackground ? 0 : (50F / 255F), isDarkBackground ? 0 : (61F / 255F), 1); //Color from LoadingOverlay
        GL11.glVertex3f(0, 0, -10);
        GL11.glVertex3f(0, window.getGuiScaledHeight(), -10);
        GL11.glVertex3f(window.getGuiScaledWidth(), window.getGuiScaledHeight(), -10);
        GL11.glVertex3f(window.getGuiScaledWidth(), 0, -10);
        GL11.glEnd();
    }

    private void renderMessages() {
//        List<Pair<Integer, StartupMessageManager.Message>> messages = StartupMessageManager.getMessages();
//        for (int i = 0; i < messages.size(); i++) {
//            boolean nofade = i == 0;
//            final Pair<Integer, StartupMessageManager.Message> pair = messages.get(i);
//            final float fade = MathHelper.clamp((4000.0f - (float) pair.getLeft() - ( i - 4 ) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
//            if (fade <0.01f && !nofade) continue;
//            StartupMessageManager.Message msg = pair.getRight();
//            renderMessage(msg.getText(), msg.getTypeColour(), ((window.getGuiScaledHeight() - 15) / 10) - i + 1, nofade ? 1.0f : fade);
//        }
//        List<Pair<Integer, StartupMessageManager.Message>> messages = StartupMessageManager.getMessages();
//        for (int i = 0; i < messages.size(); i++) {
//            boolean nofade = i == 0;
//            final Pair<Integer, StartupMessageManager.Message> pair = messages.get(i);
//            final float fade = Mth.clamp((4000.0f - (float) pair.getLeft() - ( i - 4 ) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
//            if (fade <0.01f && !nofade) continue;
//            StartupMessageManager.Message msg = pair.getRight();
//            renderMessage(msg.getText(), msg.getTypeColour(), ((window.getGuiScaledHeight() - 15) / 10) - i + 1, nofade ? 1.0f : fade);
//        }
//        renderMemoryInfo();
    }

    private static final float[] memorycolour = new float[] { 0.0f, 0.0f, 0.0f};

    private void renderMemoryInfo() {
        final MemoryUsage heapusage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final MemoryUsage offheapusage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final float pctmemory = (float) heapusage.getUsed() / heapusage.getMax();
        String memory = String.format(Locale.ENGLISH, "Memory Heap: %d / %d MB (%.1f%%)  OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, pctmemory * 100.0, offheapusage.getUsed() >> 20);

        final int i = Mth.hsvToRgb((1.0f - (float)Math.pow(pctmemory, 1.5f)) / 3f, 1.0f, 0.5f);
        memorycolour[2] = ((i) & 0xFF) / 255.0f;
        memorycolour[1] = ((i >> 8 ) & 0xFF) / 255.0f;
        memorycolour[0] = ((i >> 16 ) & 0xFF) / 255.0f;
        renderMessage(memory, memorycolour, 1, 1.0f);
    }

    void renderMessage(final String message, final float[] colour, int line, float alpha) {
        // GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        // ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
        // int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);
        // GL14.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);
        //
        // RenderSystem.enableBlend();
        // RenderSystem.disableTexture();
        // // STBEasyFont's quads are in reverse order or what OGGL expects, so it gets culled for facing the wrong way.
        // // So Disable culling https://github.com/MinecraftForge/MinecraftForge/pull/6824
        // RenderSystem.disableCull();
        // GL14.glBlendColor(0,0,0, alpha);
        // RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
        // GL11.glColor3f(colour[0],colour[1],colour[2]);
        // GL11.glPushMatrix();
        // GL11.glTranslatef(10, line * 10, 0);
        // GL11.glScalef(1, 1, 0);
        // GL11.glDrawArrays(GL11.GL_QUADS, 0, quads * 4);
        // GL11.glPopMatrix();
        //
        // RenderSystem.enableCull();
        // GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        // MemoryUtil.memFree(charBuffer);
    }
}
