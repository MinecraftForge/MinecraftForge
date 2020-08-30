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

/*
package net.minecraftforge.fml.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;

public class NotificationScreen extends Screen
{
    private class TextPanel extends ScrollPanel
    {
        TextPanel(Minecraft client, int width, int height, int top, int left)
        {
            super(client, width, height, top, left);
        }

        @Override
        protected int getContentHeight()
        {
            int height = 0;
            height += (textLines.length * field_230712_o_.FONT_HEIGHT) + 4;
            if (height < this.height - 50)
                height = this.height - 50;
            return height;
        }

        @Override
        protected void drawPanel(MatrixStack mStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY)
        {
            drawCenteredLines(mStack, relativeY, textLines);
        }

        @Override
        protected int getScrollAmount()
        {
            return field_230712_o_.FONT_HEIGHT * 3;
        }
    }

    protected static final int PADDING = 6;

    protected final StartupQuery query;

    private final String[] headerLines;
    private final String[] textLines;
    private final String action;

    private ScrollPanel textPanel;

    public NotificationScreen(StartupQuery query)
    {
        super(new TranslationTextComponent("fml.menu.notification.title"));
        this.query = query;
        this.headerLines = query.getHeader().isEmpty() ? new String[0] : query.getHeader().split("\n");
        this.textLines = query.getText().split("\n");
        this.action = query.getAction();
    }

    @Override
    public void func_231160_c_()
    {
        super.func_231160_c_();
        int panelY = PADDING + headerLines.length * field_230712_o_.FONT_HEIGHT + PADDING;
        int panelHeight = this.field_230709_l_ - PADDING - 20 - panelY;
        if (!action.isEmpty()) {
            panelHeight = panelHeight - field_230712_o_.FONT_HEIGHT - PADDING;
        }
        textPanel = new TextPanel(this.field_230706_i_, this.field_230708_k_ - (PADDING * 2), panelHeight, panelY, PADDING);
        this.field_230705_e_.add(textPanel);
        addConfirmationButtons();
    }

    protected void addConfirmationButtons() {
        this.field_230710_m_.add(new Button(this.field_230708_k_ / 2 - 100, this.field_230709_l_ - PADDING - 20, 200, 20, new TranslationTextComponent("gui.done"), b -> {
            NotificationScreen.this.field_230706_i_.displayGuiScreen(null);
            query.finish();
        }));
    }

    @Override
    public void func_230430_a_(MatrixStack mStack, int mouseX, int mouseY, float partialTicks)
    {
        this.func_230446_a_(mStack);

        drawCenteredLines(mStack, PADDING, headerLines);

        if (textPanel != null)
        {
            textPanel.func_230430_a_(mStack, mouseX, mouseY, partialTicks);
        }

        if (!action.isEmpty())
        {
            func_238471_a_(mStack, field_230712_o_, action, this.field_230708_k_ / 2, this.field_230709_l_ - PADDING - 20 - field_230712_o_.FONT_HEIGHT, -1);
        }

        super.func_230430_a_(mStack, mouseX, mouseY, partialTicks);
    }

    protected void drawCenteredLines(MatrixStack mStack, int yStart, String... lines)
    {
        for (String line : lines)
        {
            if (!line.isEmpty())
                this.func_238471_a_(mStack, field_230712_o_, line, this.field_230708_k_ / 2, yStart, 0xFFFFFF);
            yStart += field_230712_o_.FONT_HEIGHT;
        }
    }
}
*/
