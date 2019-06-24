package net.minecraftforge.fml.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EarlyLoaderGUI {
    private final MainWindow window;
    private final List<Triple<String, String, Long>> messages = new ArrayList<>();
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
        // FIXME: needs improving
        if (!message.equals(this.message))
        {
            if (message.length() > 0)
                queueMessage(message, message);
            this.message = message;
        }

        // Rest of the processing
        if (handledElsewhere) return;
        int guiScale = window.func_216521_a(0, false);
        window.func_216525_a(guiScale);

        GlStateManager.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        window.func_216522_a(Minecraft.IS_RUNNING_ON_MAC);
        renderMessages();
        window.update(false);
    }

    private void queueMessage(final String id, final String message) {
        messages.removeIf(t -> t.getLeft().equals(id));
        messages.add(Triple.of(id, message, System.currentTimeMillis()));
    }

    public void handleElsewhere() {
        this.handledElsewhere = true;
    }

    void renderFromGUI() {
        renderMessages();
    }

    void renderMessages() {
        long now = System.currentTimeMillis();
        for(int index = 0;index < messages.size();) {
            Triple<String, String, Long> current = messages.get(index);
            double age = (now - current.getRight()) / 1000.0;
            if (age < 5) {
                renderMessage(current.getMiddle(), messages.size() - index - 1, MathHelper.clamp(4 - (float)age, 0, 1));
                index++;
            } else {
                messages.remove(index);
            }
        }
    }

    void renderMessage(String message, int index, float alpha) {
        GlStateManager.enableClientState(GL11.GL_VERTEX_ARRAY);
        ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);
        GlStateManager.vertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);

        GlStateManager.enableBlend();
        GL14.glBlendColor(0,0,0, alpha);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);

        GlStateManager.color3f(0,0, 0);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(10, window.getScaledHeight() - 20 - index * 10, 0);
        GlStateManager.scalef(1, 1, 0);
        GlStateManager.drawArrays(GL11.GL_QUADS, 0, quads * 4);
        GlStateManager.popMatrix();

        MemoryUtil.memFree(charBuffer);
    }
}
