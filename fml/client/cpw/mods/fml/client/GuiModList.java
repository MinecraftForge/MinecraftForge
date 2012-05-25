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
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.FMLModLoaderContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
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
    private int selected = -1;
    private ModContainer selectedMod;
    private int listWidth;
    private ArrayList<ModContainer> mods;

    /**
     * @param guiMainMenu
     */
    public GuiModList(GuiScreen mainMenu)
    {
        this.mainMenu=mainMenu;
        this.mods=new ArrayList<ModContainer>();
        mods.add(new FMLModLoaderContainer());
        for (ModContainer mod : Loader.getModList()) {
            if (mod.getMetadata()!=null && mod.getMetadata().parentMod != null) {
                continue;
            }
            mods.add(mod);
        }
    }
    
    public void func_6448_a()
    {
        for (ModContainer mod : mods) {
            listWidth=Math.max(listWidth,getFontRenderer().func_871_a(mod.getName()) + 10);
            listWidth=Math.max(listWidth,getFontRenderer().func_871_a(mod.getVersion()) + 10);
        }
        listWidth=Math.min(listWidth, 150);
        StringTranslate translations = StringTranslate.func_20162_a();
        this.field_949_e.add(new GuiSmallButton(6, this.field_951_c / 2 - 75, this.field_950_d - 38, translations.func_20163_a("gui.done")));
        this.modList=new GuiSlotModList(this, mods, listWidth);
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
        this.func_548_a(this.field_6451_g, "Mod List", this.field_951_c / 2, 16, 0xFFFFFF);
        int detailCentre = this.listWidth / 2 + this.field_951_c / 2;
        if (selectedMod!=null) {
            if (selectedMod.getMetadata()!=null) {
                this.func_548_a(this.field_6451_g, selectedMod.getMetadata().name, detailCentre, 35, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, String.format("Version: %s", selectedMod.getMetadata().version), detailCentre, 45, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, String.format("Credits: %s", selectedMod.getMetadata().credits), detailCentre, 55, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, String.format("Authors: %s", selectedMod.getMetadata().getAuthorList()), detailCentre, 65, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, String.format("URL: %s", selectedMod.getMetadata().url), detailCentre, 75, 0xFFFFFF);
                if (selectedMod.getMetadata().childMods.isEmpty()) {
                    this.func_548_a(this.field_6451_g, "No child mods for this mod", detailCentre, 85, 0xFFFFFF);
                } else {
                    this.func_548_a(this.field_6451_g, String.format("Child mods: %s", selectedMod.getMetadata().getChildModList()), detailCentre, 85, 0xFFFFFF);
                }
                this.getFontRenderer().func_27278_a(selectedMod.getMetadata().description, this.listWidth + 15, 100, this.field_951_c - this.listWidth - 30, 0xDDDDDD);
            } else {
                this.func_548_a(this.field_6451_g, selectedMod.getName(), detailCentre, 35, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, String.format("Version: %s",selectedMod.getVersion()), detailCentre, 45, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, "No mod information found", detailCentre, 55, 0xDDDDDD);
                this.func_548_a(this.field_6451_g, "Ask your mod author to provide a mod .info file", detailCentre, 65, 0xDDDDDD);
            }
        }
        super.func_571_a(p_571_1_, p_571_2_, p_571_3_);
    }

    Minecraft getMinecraftInstance() {
        return field_945_b;
    }
    
    FontRenderer getFontRenderer() {
        return field_6451_g;
    }

    /**
     * @param var1
     */
    public void selectModIndex(int var1)
    {
        this.selected=var1;
        if (var1>=0 && var1<=mods.size()) {
            this.selectedMod=mods.get(selected);
        } else {
            this.selectedMod=null;
        }
    }

    /**
     * @param var1
     * @return
     */
    public boolean modIndexSelected(int var1)
    {
        return var1==selected;
    }
}
