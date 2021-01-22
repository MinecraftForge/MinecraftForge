/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import java.util.List;

public class EarlyLoaderGUI {
    private final MainWindow window;
    private boolean handledElsewhere;

    public EarlyLoaderGUI(final MainWindow window) {
        this.window = window;
    }

    @SuppressWarnings("deprecation")
    private void setupMatrix() {
        RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, window.getFramebufferWidth() / window.getGuiScaleFactor(), window.getFramebufferHeight() / window.getGuiScaleFactor(), 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
    }

    public void handleElsewhere() {
        this.handledElsewhere = true;
    }

    void renderFromGUI() {
        renderMessages();
    }

    @SuppressWarnings("deprecation")
    void renderTick() {
        if (handledElsewhere) return;
        int guiScale = window.calcGuiScale(0, false);
        window.setGuiScale(guiScale);

        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.pushMatrix();
        setupMatrix();
        renderBackground();
        renderMessages();
        window.flipFrame();
        RenderSystem.popMatrix();
    }

    private void renderBackground() {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(239F / 255F, 50F / 255F, 61F / 255F, 255F / 255F); //Color from ResourceLoadProgressGui
        GL11.glVertex3f(0, 0, -10);
        GL11.glVertex3f(0, window.getScaledHeight(), -10);
        GL11.glVertex3f(window.getScaledWidth(), window.getScaledHeight(), -10);
        GL11.glVertex3f(window.getScaledWidth(), 0, -10);
        GL11.glEnd();
    }

    private void renderMessages() {
        List<Pair<Integer, StartupMessageManager.Message>> messages = StartupMessageManager.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            boolean nofade = i == 0;
            final Pair<Integer, StartupMessageManager.Message> pair = messages.get(i);
            final float fade = MathHelper.clamp((4000.0f - (float) pair.getLeft() - ( i - 4 ) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
            if (fade <0.01f && !nofade) continue;
            StartupMessageManager.Message msg = pair.getRight();
            renderMessage(msg.getText(), msg.getTypeColour(), ((window.getScaledHeight() - 15) / 10) - i + 1, nofade ? 1.0f : fade);
        }
        renderMemoryInfo();
    }

    private static final float[] memorycolour = new float[] { 0.0f, 0.0f, 0.0f};

    private void renderMemoryInfo() {
        final MemoryUsage heapusage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final MemoryUsage offheapusage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final float pctmemory = (float) heapusage.getUsed() / heapusage.getMax();
        String memory = String.format("Memory Heap: %d / %d MB (%.1f%%)  OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, pctmemory * 100.0, offheapusage.getUsed() >> 20);

        final int i = MathHelper.hsvToRGB((1.0f - (float)Math.pow(pctmemory, 1.5f)) / 3f, 1.0f, 0.5f);
        memorycolour[2] = ((i) & 0xFF) / 255.0f;
        memorycolour[1] = ((i >> 8 ) & 0xFF) / 255.0f;
        memorycolour[0] = ((i >> 16 ) & 0xFF) / 255.0f;
        renderMessage(memory, memorycolour, 1, 1.0f);
    }

    @SuppressWarnings("deprecation")
    void renderMessage(final String message, final float[] colour, int line, float alpha) {
        GlStateManager.enableClientState(GL11.GL_VERTEX_ARRAY);
        ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);
        GL14.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        // STBEasyFont's quads are in reverse order or what OGGL expects, so it gets culled for facing the wrong way.
        // So Disable culling https://github.com/MinecraftForge/MinecraftForge/pull/6824
        RenderSystem.disableCull();
        GL14.glBlendColor(0,0,0, alpha);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
        RenderSystem.color3f(colour[0],colour[1],colour[2]);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(10, line * 10, 0);
        RenderSystem.scalef(1, 1, 0);
        RenderSystem.drawArrays(GL11.GL_QUADS, 0, quads * 4);
        RenderSystem.popMatrix();

        RenderSystem.enableCull();
        GlStateManager.disableClientState(GL11.GL_VERTEX_ARRAY);
        MemoryUtil.memFree(charBuffer);
    }
}
