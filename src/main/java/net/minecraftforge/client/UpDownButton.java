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
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0);
        RenderSystem.scalef(-1, 1, 1);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(5.5f, 8.5f, 0.0f);
        RenderSystem.rotatef(90, 0, 0, 1);
        RenderSystem.translatef(-8.5f, 5.5f, 0.0f);
        if (isDown) {
            blit(0, 0, 1, 208 + i * 18, 11, 17);
        } else {
            blit(0, 0, 15, 208 + i * 18, 11, 17);
        }
        RenderSystem.popMatrix();
        RenderSystem.popMatrix();
        this.renderBg(minecraft, mouseX, mouseY);
    }
}
