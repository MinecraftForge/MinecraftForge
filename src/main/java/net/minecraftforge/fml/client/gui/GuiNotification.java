/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.StartupQuery;

public class GuiNotification extends Screen
{
    public GuiNotification(StartupQuery query)
    {
        super(new TranslationTextComponent("fml.menu.notification.title"));
        this.query = query;
    }

    @Override
    public void init()
    {
        this.buttons.add(new Button(this.width / 2 - 100, this.height - 38, 200, 20, I18n.format("gui.done"), b -> {
            GuiNotification.this.minecraft.displayGuiScreen(null);
            query.finish();
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();

        String[] lines = query.getText().split("\n");

        int spaceAvailable = this.height - 38 - 20;
        int spaceRequired = Math.min(spaceAvailable, 10 + 10 * lines.length);

        int offset = 10 + (spaceAvailable - spaceRequired) / 2; // vertically centered

        for (String line : lines)
        {
            if (offset >= spaceAvailable)
            {
                this.drawCenteredString(this.font, "...", this.width / 2, offset, 0xFFFFFF);
                break;
            }
            else
            {
                if (!line.isEmpty()) this.drawCenteredString(this.font, line, this.width / 2, offset, 0xFFFFFF);
                offset += 10;
            }
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    protected final StartupQuery query;
}
