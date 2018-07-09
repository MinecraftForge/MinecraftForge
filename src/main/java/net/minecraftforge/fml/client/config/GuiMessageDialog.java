/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.client.config;

import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class GuiMessageDialog extends GuiDisconnected
{
    protected String buttonText;

    public GuiMessageDialog(@Nullable GuiScreen nextScreen, String title, ITextComponent message, String buttonText)
    {
        super(nextScreen, title, message);
        this.buttonText = buttonText;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.get(0).displayString = I18n.format(buttonText);
    }
}
