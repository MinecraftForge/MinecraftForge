package net.minecraftforge.client;


import com.mojang.blaze3d.platform.GlStateManager;
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
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(STATS_ICON_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = (isHovered) ? 1 : 0;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (isDown) {
            blit(this.x, this.y, 1, 208 + i * 18, 11, 17);
        } else {
            blit(this.x, this.y, 15, 208 + i * 18, 11, 17);
        }
        this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
    }

    public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_) {
        blit(p_blit_1_, p_blit_2_, this.blitOffset, (float)p_blit_3_, (float)p_blit_4_, p_blit_5_, p_blit_6_, 256, 256);
    }

    public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, float p_blit_3_, float p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_) {
        innerBlit(p_blit_0_, p_blit_0_ + p_blit_6_, p_blit_1_, p_blit_1_ + p_blit_5_, p_blit_2_, p_blit_5_, p_blit_6_, p_blit_3_, p_blit_4_, p_blit_8_, p_blit_7_);
    }

    private static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, int p_innerBlit_5_, int p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_, int p_innerBlit_9_, int p_innerBlit_10_) {
        innerBlit(p_innerBlit_0_, p_innerBlit_1_, p_innerBlit_2_, p_innerBlit_3_, p_innerBlit_4_, (p_innerBlit_7_ + 0.0F) / (float)p_innerBlit_9_, (p_innerBlit_7_ + (float)p_innerBlit_5_) / (float)p_innerBlit_9_, (p_innerBlit_8_ + 0.0F) / (float)p_innerBlit_10_, (p_innerBlit_8_ + (float)p_innerBlit_6_) / (float)p_innerBlit_10_);
    }

    protected static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, float p_innerBlit_5_, float p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_) {
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)p_innerBlit_0_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).tex((double)p_innerBlit_6_, (double)p_innerBlit_7_).endVertex();
        bufferbuilder.pos((double)p_innerBlit_1_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).tex((double)p_innerBlit_6_, (double)p_innerBlit_8_).endVertex();
        bufferbuilder.pos((double)p_innerBlit_1_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).tex((double)p_innerBlit_5_, (double)p_innerBlit_8_).endVertex();
        bufferbuilder.pos((double)p_innerBlit_0_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).tex((double)p_innerBlit_5_, (double)p_innerBlit_7_).endVertex();
        tessellator.draw();
    }
}
