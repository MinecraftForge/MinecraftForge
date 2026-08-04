/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.gui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.StartupQuery;

public class GuiConfirmation extends GuiNotification
{
    public GuiConfirmation(StartupQuery query)
    {
        super(query);
    }

    @Override
    public void init()
    {
        this.addButton(new Button(this.width / 2 - 104, this.height - 38, 100, 20, ForgeI18n.parseMessage("gui.yes"), b ->
            {
                GuiConfirmation.this.minecraft.currentScreen = null;
                query.setResult(true);
                query.finish();
            }
        ));
        this.addButton(new Button(this.width / 2 + 4, this.height - 38, 100, 20, ForgeI18n.parseMessage("gui.no"), b ->
            {
                GuiConfirmation.this.minecraft.currentScreen = null;
                query.setResult(false);
                query.finish();
            }
        ));
    }
}
