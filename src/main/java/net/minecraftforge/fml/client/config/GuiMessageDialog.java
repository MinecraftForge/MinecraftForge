/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.config;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class GuiMessageDialog extends DisconnectedScreen
{
    protected String buttonText;

    public GuiMessageDialog(@Nullable Screen nextScreen, String title, ITextComponent message, String buttonText)
    {
        super(nextScreen, title, message);
        this.buttonText = I18n.format(buttonText);
    }
}
