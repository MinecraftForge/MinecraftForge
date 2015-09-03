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

package net.minecraftforge.fml.client;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ModContainer;

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
        super(parent.getMinecraftInstance(), listWidth, parent.height, 32, parent.height - 88 + 4, 10, 35);
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
        this.parent.drawDefaultBackground();
    }

    @Override
    protected int getContentHeight()
    {
        return (this.getSize()) * 35 + 1;
    }

    ArrayList<ModContainer> getMods()
    {
        return mods;
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        ModContainer mc=mods.get(listIndex);
        String name = StringUtils.stripControlCodes(mc.getName());
        String version = StringUtils.stripControlCodes(mc.getDisplayVersion());
        if (Loader.instance().getModState(mc)==ModState.DISABLED)
        {
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(name, listWidth - 10), this.left + 3 , var3 + 2, 0xFF2222);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(version, listWidth - 10), this.left + 3 , var3 + 12, 0xFF2222);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth("DISABLED", listWidth - 10), this.left + 3 , var3 + 22, 0xFF2222);
        }
        else
        {
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(name, listWidth - 10), this.left + 3 , var3 + 2, 0xFFFFFF);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(version, listWidth - 10), this.left + 3 , var3 + 12, 0xCCCCCC);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(mc.getMetadata() !=null ? mc.getMetadata().getChildModCountString() : "Metadata not found", listWidth - 10), this.left + 3 , var3 + 22, 0xCCCCCC);
        }
    }

}
