/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

/**
 * This class provides a button that shows a string glyph at the beginning. The glyph can be scaled using the glyphScale parameter.
 *
 * @author bspkrs
 */
public class UnicodeGlyphButton extends ExtendedButton
{
    public String glyph;
    public float  glyphScale;

    public UnicodeGlyphButton(int xPos, int yPos, int width, int height, ITextComponent displayString, String glyph, float glyphScale, IPressable handler)
    {
        super(xPos, yPos, width, height, displayString, handler);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
    }

    @Override
    public void func_230430_a_(MatrixStack mStack, int mouseX, int mouseY, float partial)
    {
        if (this.field_230694_p_)
        {
            Minecraft mc = Minecraft.getInstance();
            this.field_230692_n_ = mouseX >= this.field_230690_l_ && mouseY >= this.field_230691_m_ && mouseX < this.field_230690_l_ + this.field_230688_j_ && mouseY < this.field_230691_m_ + this.field_230689_k_;
            int k = this.func_230989_a_(this.field_230692_n_);
            GuiUtils.drawContinuousTexturedBox(mStack, Button.field_230687_i_, this.field_230690_l_, this.field_230691_m_, 0, 46 + k * 20, this.field_230688_j_, this.field_230689_k_, 200, 20, 2, 3, 2, 2, this.func_230927_p_());
            this.func_230441_a_(mStack, mc, mouseX, mouseY);

            ITextComponent buttonText = this.func_230442_c_();
            int glyphWidth = (int) (mc.fontRenderer.getStringWidth(glyph) * glyphScale);
            int strWidth = mc.fontRenderer.func_238414_a_(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
            int totalWidth = strWidth + glyphWidth;

            if (totalWidth > field_230688_j_ - 6 && totalWidth > ellipsisWidth)
                buttonText = new StringTextComponent(mc.fontRenderer.func_238417_a_(buttonText, field_230688_j_ - 6 - ellipsisWidth).getString().trim() + "...") ;

            strWidth = mc.fontRenderer.func_238414_a_(buttonText);
            totalWidth = glyphWidth + strWidth;

            mStack.push();
            mStack.scale(glyphScale, glyphScale, 1.0F);
            this.func_238472_a_(mStack, mc.fontRenderer, new StringTextComponent(glyph),
                    (int) (((this.field_230690_l_ + (this.field_230688_j_ / 2) - (strWidth / 2)) / glyphScale) - (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.field_230691_m_ + ((this.field_230689_k_ - 8) / glyphScale) / 2) - 1) / glyphScale), getFGColor());
            mStack.pop();

            this.func_238472_a_(mStack, mc.fontRenderer, buttonText, (int) (this.field_230690_l_ + (this.field_230688_j_ / 2) + (glyphWidth / glyphScale)),
                    this.field_230691_m_ + (this.field_230689_k_ - 8) / 2, getFGColor());

        }
    }
}
