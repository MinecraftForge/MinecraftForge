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

import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiLanguage;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.Tessellator;

/**
 * @author cpw
 *
 */
public class GuiSlotModList extends GuiSlot
{
    private GuiModList parent;

    public GuiSlotModList(GuiModList parent)
    {
        super(parent.getMinecraftInstance(), parent.field_951_c, parent.field_950_d, 32, parent.field_950_d - 65 + 4, 18);
        this.parent=parent;
    }

    @Override
    protected int func_22249_a()
    {
        return Loader.getModList().size();
    }

    @Override
    protected void func_22247_a(int var1, boolean var2)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected boolean func_22246_a(int var1)
    {
        return false;
    }

    @Override
    protected void func_22248_c()
    {
        this.parent.func_578_i();
    }

    @Override
    protected int func_22245_b()
    {
        return this.func_22249_a() * 18;
    }
    
    @Override
    protected void func_22242_a(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        this.parent.func_548_a(this.parent.getFontRenderer(), Loader.getModList().get(listIndex).getName(), this.parent.field_951_c / 2, var3 + 1, 16777215);
    }

}
