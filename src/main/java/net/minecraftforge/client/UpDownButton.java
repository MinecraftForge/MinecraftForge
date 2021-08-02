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

package net.minecraftforge.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class UpDownButton extends Button {
    public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private final boolean isDown;

    public UpDownButton(int x, int y, int width, int height, boolean isDown, IPressable onPress) {
        super(x, y, width, height, new StringTextComponent(""), onPress);
        this.isDown = isDown;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(STATS_ICON_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = (isHovered) ? 1 : 0;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        matrixStack.pushPose();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(-1, 1, 1);
        matrixStack.pushPose();
        matrixStack.translate(5.5f, 8.5f, 0.0f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90f));
        matrixStack.translate(-8.5f, 5.5f, 0.0f);
        if (isDown) {
            blit(matrixStack, 0, 0, 1, 208 + i * 18, 11, 17);
        } else {
            blit(matrixStack, 0, 0, 15, 208 + i * 18, 11, 17);
        }
        matrixStack.popPose();
        matrixStack.popPose();
        RenderSystem.enableCull();
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
    }
}
