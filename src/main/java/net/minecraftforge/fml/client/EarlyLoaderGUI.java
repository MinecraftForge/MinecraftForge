package net.minecraftforge.fml.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class EarlyLoaderGUI {
    private final MainWindow window;
    private String message;
    private boolean handledElsewhere;

    public EarlyLoaderGUI(final MainWindow window) {
        this.window = window;
        GlStateManager.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        window.update(false);
    }

    public Consumer<String> getStatusConsumer() {
        return this::update;
    }

    private void update(final String message) {
        this.message = message;
        if (handledElsewhere) return;
        doMatrix();

        GlStateManager.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        renderMessage();
        window.update(false);
    }

    public void handleElsewhere() {
        this.handledElsewhere = true;
    }

    void renderFromGUI() {
        doMatrix();
        renderMessage();
    }
    void renderMessage() {
        GlStateManager.enableClientState(GL11.GL_VERTEX_ARRAY);
        ByteBuffer charBuffer = MemoryUtil.memAlloc(this.message.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(0, 0, this.message, null, charBuffer);
        GlStateManager.vertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);

        GlStateManager.color3f(0,0,0);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(10, window.getFramebufferHeight() - 50, 0);
        GlStateManager.scalef(3, 3, 0);
        GlStateManager.drawArrays(GL11.GL_QUADS, 0, quads * 4);
        GlStateManager.popMatrix();

        MemoryUtil.memFree(charBuffer);
    }

    private void doMatrix() {
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0,  window.getFramebufferWidth(), window.getFramebufferHeight(), 0.0, -1.0, 1.0);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
    }
}
