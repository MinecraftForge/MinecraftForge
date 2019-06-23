package net.minecraftforge.fml.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.client.gui.EarlyFontRenderer;

import java.util.function.Consumer;

public class EarlyLoaderGUI {
    private final MainWindow window;
    private String message;
    private boolean handledElsewhere;

    public EarlyLoaderGUI(final MainWindow window) {
        this.window = window;
        EarlyFontRenderer.get().loadEarlyTexture();
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
        GlStateManager.pushMatrix();
        GlStateManager.translatef(10, window.getFramebufferHeight() - 50, 0);
        GlStateManager.scalef(3, 3, 0);
        EarlyFontRenderer.get().drawString(0, 0, message, 0xff000000);
        GlStateManager.popMatrix();
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
