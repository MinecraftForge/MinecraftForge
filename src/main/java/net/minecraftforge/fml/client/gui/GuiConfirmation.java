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

package net.minecraftforge.fml.client.gui;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.StartupQuery;

public class GuiConfirmation extends GuiNotification
{
    public GuiConfirmation(StartupQuery query)
    {
        super(query);
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height - 38, I18n.format("gui.yes"))
        {
            public void func_194829_a(double p_194829_1_, double p_194829_3_)
            {
                GuiConfirmation.this.mc.displayGuiScreen(null);
                query.setResult(true);
                query.finish();
            }
        });
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.no"))
        {
            public void func_194829_a(double p_194829_1_, double p_194829_3_)
            {
                GuiConfirmation.this.mc.displayGuiScreen(null);
                query.setResult(false);
                query.finish();
            }
        });
    }
}
