package net.minecraftforge.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class UpDownButton extends net.minecraft.client.gui.widget.button.Button {
    public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private final boolean isDown;

    public UpDownButton(int x, int y, int width, int height, boolean isDown, IPressable onPress) {
        super(x, y, width, height, "", onPress);
        this.isDown = isDown;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partial) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(STATS_ICON_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = (isHovered) ? 1 : 0;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (isDown) {
            blit(this.x, this.y, 1, 208 + i * 18, 11, 17);
        } else {
            blit(this.x, this.y, 15, 208 + i * 18, 11, 17);
        }
        this.renderBg(minecraft, mouseX, mouseY);
    }

    // re-implemented blit function for rotating and mirroring texture
    public void blit(int x, int y, int u, int v, int w, int h) {
        blit(x, y, 0, (float)u, (float)v, w, h, 256, 256);
    }

    public static void blit(int x, int y, int blitOffset, float u, float v, int w, int h, int textureW, int textureH) {
        innerBlit(x, x + h, y, y + w, blitOffset, w, h, u, v, textureH, textureW);
    }

    private static void innerBlit(int minX, int maxX, int minY, int maxY, int blitOffset, int w, int h, float u, float v, int textureW, int textureH) {
        innerBlit(minX, maxX, minY, maxY, blitOffset, u / textureW, (u + w) / textureW, v / textureH, (v + h) / textureH);
    }

    protected static void innerBlit(int minX, int maxX, int minY, int maxY, int blitOffset, float minU, float maxU, float minV, float maxV) {
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(minX, maxY, blitOffset).tex(maxU, minV).endVertex();
        bufferBuilder.pos(maxX, maxY, blitOffset).tex(maxU, maxV).endVertex();
        bufferBuilder.pos(maxX, minY, blitOffset).tex(minU, maxV).endVertex();
        bufferBuilder.pos(minX, minY, blitOffset).tex(minU, minV).endVertex();
        tessellator.draw();
    }
}
