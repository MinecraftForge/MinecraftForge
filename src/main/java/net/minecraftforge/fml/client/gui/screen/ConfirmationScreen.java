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
