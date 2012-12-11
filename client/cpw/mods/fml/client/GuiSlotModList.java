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

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModContainer;

/**
 * @author cpw
 *
 */
public class GuiSlotModList extends GuiScrollingList
{
    private GuiModList parent;
    private ArrayList<ModContainer> mods;

    public GuiSlotModList(GuiModList parent, ArrayList<ModContainer> mods, int listWidth)
    {
        super(parent.getMinecraftInstance(), listWidth, parent.field_73881_g, 32, parent.field_73881_g - 65 + 4, 10, 35);
        this.parent=parent;
        this.mods=mods;
    }

    @Override
    protected int getSize()
    {
        return mods.size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2)
    {
        this.parent.selectModIndex(var1);
    }

    @Override
    protected boolean isSelected(int var1)
    {
        return this.parent.modIndexSelected(var1);
    }

    @Override
    protected void drawBackground()
    {
        this.parent.func_73873_v_();
    }

    @Override
    protected int getContentHeight()
    {
        return (this.getSize()) * 35 + 1;
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        ModContainer mc=mods.get(listIndex);
        if (Loader.instance().getModState(mc)==ModState.DISABLED)
        {
            this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a(mc.getName(), listWidth - 10), this.left + 3 , var3 + 2, 0xFF2222);
            this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a(mc.getDisplayVersion(), listWidth - 10), this.left + 3 , var3 + 12, 0xFF2222);
            this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a("DISABLED", listWidth - 10), this.left + 3 , var3 + 22, 0xFF2222);
        }
        else
        {
            this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a(mc.getName(), listWidth - 10), this.left + 3 , var3 + 2, 0xFFFFFF);
            this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a(mc.getDisplayVersion(), listWidth - 10), this.left + 3 , var3 + 12, 0xCCCCCC);
            this.parent.getFontRenderer().func_78276_b(this.parent.getFontRenderer().func_78269_a(mc.getMetadata() !=null ? mc.getMetadata().getChildModCountString() : "Metadata not found", listWidth - 10), this.left + 3 , var3 + 22, 0xCCCCCC);
        }
    }

}
