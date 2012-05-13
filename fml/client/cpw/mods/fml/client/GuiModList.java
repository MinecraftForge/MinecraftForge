/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package cpw.mods.fml.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSmallButton;
import net.minecraft.src.StringTranslate;

/**
 * @author cpw
 *
 */
public class GuiModList extends GuiScreen
{

    private GuiScreen mainMenu;
    private GuiSlotModList modList;

    /**
     * @param guiMainMenu
     */
    public GuiModList(GuiScreen mainMenu)
    {
        this.mainMenu=mainMenu;
    }
    
    public void func_6448_a()
    {
        StringTranslate translations = StringTranslate.func_20162_a();
        this.field_949_e.add(new GuiSmallButton(6, this.field_951_c / 2 - 75, this.field_950_d - 38, translations.func_20163_a("gui.done")));
        this.modList=new GuiSlotModList(this);
        this.modList.registerScrollButtons(this.field_949_e, 7, 8);
    }

    protected void func_572_a(GuiButton button) {
        if (button.field_937_g)
        {
            switch (button.field_938_f)
            {
                case 6:
                    this.field_945_b.func_6272_a(this.mainMenu);
                   
            }
        }

    }

    public void func_571_a(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        this.modList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        this.func_548_a(this.field_6451_g, "Mod List", this.field_951_c / 2, 16, 16777215);
        super.func_571_a(p_571_1_, p_571_2_, p_571_3_);
    }

    Minecraft getMinecraftInstance() {
        return field_945_b;
    }
    
    FontRenderer getFontRenderer() {
        return field_6451_g;
    }
}
