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
            height += (textLines.length * font.FONT_HEIGHT) + 4;
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
            return font.FONT_HEIGHT * 3;
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
    public void init()
    {
        super.init();
        int panelY = PADDING + headerLines.length * font.FONT_HEIGHT + PADDING;
        int panelHeight = this.height - PADDING - 20 - panelY;
        if (!action.isEmpty()) {
            panelHeight = panelHeight - font.FONT_HEIGHT - PADDING;
        }
        textPanel = new TextPanel(this.minecraft, this.width - (PADDING * 2), panelHeight, panelY, PADDING);
        this.children.add(textPanel);
        addConfirmationButtons();
    }

    protected void addConfirmationButtons() {
        this.buttons.add(new Button(this.width / 2 - 100, this.height - PADDING - 20, 200, 20, new TranslationTextComponent("gui.done"), b -> {
            NotificationScreen.this.minecraft.displayGuiScreen(null);
            query.finish();
        }));
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(mStack);

        drawCenteredLines(mStack, PADDING, headerLines);

        if (textPanel != null)
        {
            textPanel.render(mStack, mouseX, mouseY, partialTicks);
        }

        if (!action.isEmpty())
        {
            drawCenteredString(mStack, font, action, this.width / 2, this.height - PADDING - 20 - font.FONT_HEIGHT, -1);
        }

        super.render(mStack, mouseX, mouseY, partialTicks);
    }

    protected void drawCenteredLines(MatrixStack mStack, int yStart, String... lines)
    {
        for (String line : lines)
        {
            if (!line.isEmpty())
                this.drawCenteredString(mStack, font, line, this.width / 2, yStart, 0xFFFFFF);
            yStart += font.FONT_HEIGHT;
        }
    }
}
*/
