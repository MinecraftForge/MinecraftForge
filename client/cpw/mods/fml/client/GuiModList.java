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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

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
import net.minecraft.src.Tessellator;

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
        FMLClientHandler.instance().addSpecialModEntries(mods);
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
                    return;
            }
        }
        super.func_572_a(button);
    }

    public int drawLine(String line, int offset, int shifty)
    {
        this.field_6451_g.func_873_b(line, offset, shifty, 0xd7edea);
        return shifty + 10;
    }
    public void func_571_a(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        this.modList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        this.func_548_a(this.field_6451_g, "Mod List", this.field_951_c / 2, 16, 0xFFFFFF);
        int offset = this.listWidth  + 20;
        if (selectedMod != null) {
            if (selectedMod.getMetadata() != null) {
                int shifty = 35;
                if (!selectedMod.getMetadata().logoFile.isEmpty())
                {
                    int texture = this.field_945_b.field_6315_n.func_1070_a(selectedMod.getMetadata().logoFile);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.field_945_b.field_6315_n.func_1076_b(texture);
                    Dimension dim = FMLClientHandler.instance().getTextureDimensions(texture);
                    int top = 32;
                    Tessellator tess = Tessellator.field_1512_a;
                    tess.func_977_b();
                    tess.func_983_a(offset,             top + dim.height, field_923_k, 0, 1);
                    tess.func_983_a(offset + dim.width, top + dim.height, field_923_k, 1, 1);
                    tess.func_983_a(offset + dim.width, top,              field_923_k, 1, 0);
                    tess.func_983_a(offset,             top,              field_923_k, 0, 0);
                    tess.func_982_a();
                    
                    shifty += 65;
                }
                this.field_6451_g.func_50103_a(selectedMod.getMetadata().name, offset, shifty, 0xFFFFFF);
                shifty += 12;
                
                shifty = drawLine(String.format("Version: %s (%s)", selectedMod.getMetadata().version, selectedMod.getVersion()), offset, shifty);
                if (!selectedMod.getMetadata().credits.isEmpty()) {
                   shifty = drawLine(String.format("Credits: %s", selectedMod.getMetadata().credits), offset, shifty);
                }
                shifty = drawLine(String.format("Authors: %s", selectedMod.getMetadata().getAuthorList()), offset, shifty);
                shifty = drawLine(String.format("URL: %s", selectedMod.getMetadata().url), offset, shifty);
                shifty = drawLine(selectedMod.getMetadata().childMods.isEmpty() ? "No child mods for this mod" : String.format("Child mods: %s", selectedMod.getMetadata().getChildModList()), offset, shifty);
                this.getFontRenderer().func_27278_a(selectedMod.getMetadata().description, offset, shifty + 10, this.field_951_c - offset - 20, 0xDDDDDD);
            } else {
                offset = ( this.listWidth + this.field_951_c ) / 2;
                this.func_548_a(this.field_6451_g, selectedMod.getName(), offset, 35, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, String.format("Version: %s",selectedMod.getVersion()), offset, 45, 0xFFFFFF);
                this.func_548_a(this.field_6451_g, "No mod information found", offset, 55, 0xDDDDDD);
                this.func_548_a(this.field_6451_g, "Ask your mod author to provide a mod .info file", offset, 65, 0xDDDDDD);
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
