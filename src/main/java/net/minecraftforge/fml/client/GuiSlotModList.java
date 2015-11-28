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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
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
        super(parent.getMinecraftInstance(), listWidth, parent.height, 32, parent.height - 88 + 4, 10, 35, parent.width, parent.height);
        this.parent = parent;
        this.mods = mods;
    }

    @Override
    protected int getSize()
    {
        return mods.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick)
    {
        this.parent.selectModIndex(index);
    }

    @Override
    protected boolean isSelected(int index)
    {
        return this.parent.modIndexSelected(index);
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
    protected void drawSlot(int idx, int right, int top, int height, Tessellator tess)
    {
        ModContainer mc       = mods.get(idx);
        String       name     = StringUtils.stripControlCodes(mc.getName());
        String       version  = StringUtils.stripControlCodes(mc.getDisplayVersion());
        FontRenderer font     = this.parent.getFontRenderer();
        CheckResult  vercheck = ForgeVersion.getResult(mc);

        if (Loader.instance().getModState(mc) == ModState.DISABLED)
        {
            font.drawString(font.trimStringToWidth(name,       listWidth - 10), this.left + 3 , top +  2, 0xFF2222);
            font.drawString(font.trimStringToWidth(version,    listWidth - 10), this.left + 3 , top + 12, 0xFF2222);
            font.drawString(font.trimStringToWidth("DISABLED", listWidth - 10), this.left + 3 , top + 22, 0xFF2222);
        }
        else
        {
            font.drawString(font.trimStringToWidth(name,    listWidth - 10), this.left + 3 , top +  2, 0xFFFFFF);
            font.drawString(font.trimStringToWidth(version, listWidth - 10), this.left + 3 , top + 12, 0xCCCCCC);
            font.drawString(font.trimStringToWidth(mc.getMetadata() != null ? mc.getMetadata().getChildModCountString() : "Metadata not found", listWidth - 10), this.left + 3 , top + 22, 0xCCCCCC);

            switch(vercheck.status) //TODO: Change to icons?
            {
                case BETA_OUTDATED:
                case OUTDATED:
                    font.drawString("U", right - font.getCharWidth('U') - 1, top+height-font.FONT_HEIGHT+2, 0x22FF22);
                    break;
                case AHEAD:
                case BETA:
                case FAILED:
                case PENDING:
                case UP_TO_DATE:
                    break;
            }
        }
    }
}
