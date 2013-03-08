/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
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
