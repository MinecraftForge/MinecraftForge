/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiAccessDenied extends Screen
{
    private Screen parent;
    private ServerData data;
    public GuiAccessDenied(Screen parent, ServerData data)
    {
        super(new TranslationTextComponent("fml.menu.accessdenied.title"));
        this.parent = parent;
        this.data = data;
    }

    @Override
    public void init()
    {
        this.buttons.add(new Button(this.width / 2 - 75, this.height - 38, 200, 20, I18n.format("gui.done"), b -> GuiAccessDenied.this.minecraft.displayGuiScreen(parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        String[] lines = I18n.format("fml.menu.accessdenied.message", data.serverName).split("\n");
        int offset = Math.max(85 - lines.length * 10, 10);
        for (String line : lines) {
            this.drawCenteredString(this.font, line, this.width / 2, offset, 0xFFFFFF);
            offset += 10;
        }
        super.render(mouseX, mouseY, partialTicks);
    }
}
