/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*
package net.minecraftforge.fml.client.gui.screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.StartupQuery;

public class ConfirmationScreen extends NotificationScreen
{
    public ConfirmationScreen(StartupQuery query)
    {
        super(query);
    }

    @Override
    protected void addConfirmationButtons()
    {
        this.addButton(new Button(this.width / 2 - 104, this.height - PADDING - 20, 100, 20, new StringTextComponent(ForgeI18n.parseMessage("gui.yes")), b ->
            {
                ConfirmationScreen.this.minecraft.currentScreen = null;
                query.setResult(true);
                query.finish();
            }
        ));
        this.addButton(new Button(this.width / 2 + 4, this.height - PADDING - 20, 100, 20, new StringTextComponent(ForgeI18n.parseMessage("gui.no")), b ->
            {
                ConfirmationScreen.this.minecraft.currentScreen = null;
                query.setResult(false);
                query.finish();
            }
        ));
    }
}
*/
