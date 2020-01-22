/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.StartupQuery;

public class NotificationScreen extends Screen
{
    public NotificationScreen(StartupQuery query)
    {
        super(new TranslationTextComponent("fml.menu.notification.title"));
        this.query = query;
    }

    @Override
    public void init()
    {
        this.buttons.add(new Button(this.width / 2 - 100, this.height - 38, 200, 20, I18n.format("gui.done"), b -> {
            NotificationScreen.this.minecraft.displayGuiScreen(null);
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
