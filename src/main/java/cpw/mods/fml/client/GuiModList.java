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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModContainer.Disableable;

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
    private GuiButton configModButton;
    private GuiButton disableModButton;
    private ResourceLocation cachedLogo;
    private Dimension cachedLogoDimensions;

    /**
     * @param mainMenu
     */
    public GuiModList(GuiScreen mainMenu)
    {
        this.mainMenu=mainMenu;
        this.mods=new ArrayList<ModContainer>();
        FMLClientHandler.instance().addSpecialModEntries(mods);
        for (ModContainer mod : Loader.instance().getModList()) {
            if (mod.getMetadata()!=null && mod.getMetadata().parentMod==null && !Strings.isNullOrEmpty(mod.getMetadata().parent)) {
                String parentMod = mod.getMetadata().parent;
                ModContainer parentContainer = Loader.instance().getIndexedModList().get(parentMod);
                if (parentContainer != null)
                {
                    mod.getMetadata().parentMod = parentContainer;
                    parentContainer.getMetadata().childMods.add(mod);
                    continue;
                }
            }
            else if (mod.getMetadata()!=null && mod.getMetadata().parentMod!=null)
            {
            	 continue;
            }
            mods.add(mod);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void func_73866_w_()
    {
        for (ModContainer mod : mods) {
            listWidth=Math.max(listWidth,getFontRenderer().func_78256_a(mod.getName()) + 10);
            listWidth=Math.max(listWidth,getFontRenderer().func_78256_a(mod.getVersion()) + 10);
        }
        listWidth=Math.min(listWidth, 150);
        this.field_146292_n.add(new GuiButton(6, this.field_146294_l / 2 - 75, this.field_146295_m - 38, I18n.func_135052_a("gui.done")));
        configModButton = new GuiButton(20, 10, this.field_146295_m - 60, this.listWidth, 20, "Config");
        disableModButton = new GuiButton(21, 10, this.field_146295_m - 38, this.listWidth, 20, "Disable");
        this.field_146292_n.add(configModButton);
        this.field_146292_n.add(disableModButton);
        this.modList=new GuiSlotModList(this, mods, listWidth);
        this.modList.registerScrollButtons(this.field_146292_n, 7, 8);
    }

    @Override
    protected void func_146284_a(GuiButton button) {
        if (button.field_146124_l)
        {
            switch (button.field_146127_k)
            {
                case 6:
                    this.field_146297_k.func_147108_a(this.mainMenu);
                    return;
                case 20:
                    try
                    {
                        IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(selectedMod);
                        GuiScreen newScreen = guiFactory.mainConfigGuiClass().getConstructor(GuiScreen.class).newInstance(this);
                        this.field_146297_k.func_147108_a(newScreen);
                    }
                    catch (Exception e)
                    {
                        FMLLog.log(Level.ERROR, e, "There was a critical issue trying to build the config GUI for %s", selectedMod.getModId());
                    }
                    return;
            }
        }
        super.func_146284_a(button);
    }

    public int drawLine(String line, int offset, int shifty)
    {
        this.field_146289_q.func_78276_b(line, offset, shifty, 0xd7edea);
        return shifty + 10;
    }

    @Override
    public void func_73863_a(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        this.modList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        this.func_73732_a(this.field_146289_q, "Mod List", this.field_146294_l / 2, 16, 0xFFFFFF);
        int offset = this.listWidth  + 20;
        if (selectedMod != null) {
            GL11.glEnable(GL11.GL_BLEND);
            if (!selectedMod.getMetadata().autogenerated) {
                configModButton.field_146125_m = true;
                disableModButton.field_146125_m = true;
                disableModButton.packedFGColour = 0xFF3377;
                configModButton.field_146124_l = false;
                int shifty = 35;
                String logoFile = selectedMod.getMetadata().logoFile;
                if (!logoFile.isEmpty())
                {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    TextureManager tm = field_146297_k.func_110434_K();
                    IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(selectedMod.getModId());
                    try
                    {
                        if (cachedLogo == null)
                        {
                            BufferedImage logo = null;
                            if (pack!=null)
                            {
                                logo = pack.func_110586_a();
                            }
                            else
                            {
                                InputStream logoResource = getClass().getResourceAsStream(logoFile);
                                if (logoResource != null)
                                {
                                    logo = ImageIO.read(logoResource);
                                }
                            }
                            if (logo != null)
                            {
                                cachedLogo = tm.func_110578_a("modlogo", new DynamicTexture(logo));
                                cachedLogoDimensions = new Dimension(logo.getWidth(), logo.getHeight());
                            }
                        }
                        if (cachedLogo != null)
                        {
                            this.field_146297_k.field_71446_o.func_110577_a(cachedLogo);
                            double scaleX = cachedLogoDimensions.width / 200.0;
                            double scaleY = cachedLogoDimensions.height / 65.0;
                            double scale = 1.0;
                            if (scaleX > 1 || scaleY > 1)
                            {
                                scale = 1.0 / Math.max(scaleX, scaleY);
                            }
                            cachedLogoDimensions.width *= scale;
                            cachedLogoDimensions.height *= scale;
                            int top = 32;
                            Tessellator tess = Tessellator.field_78398_a;
                            tess.func_78382_b();
                            tess.func_78374_a(offset,                               top + cachedLogoDimensions.height,  field_73735_i, 0, 1);
                            tess.func_78374_a(offset + cachedLogoDimensions.width,  top + cachedLogoDimensions.height,  field_73735_i, 1, 1);
                            tess.func_78374_a(offset + cachedLogoDimensions.width,  top,                                field_73735_i, 1, 0);
                            tess.func_78374_a(offset,                               top,                                field_73735_i, 0, 0);
                            tess.func_78381_a();

                            shifty += 65;
                        }
                    }
                    catch (IOException e)
                    {
                        ;
                    }
                }
                this.field_146289_q.func_78261_a(selectedMod.getMetadata().name, offset, shifty, 0xFFFFFF);
                shifty += 12;

                shifty = drawLine(String.format("Version: %s (%s)", selectedMod.getDisplayVersion(), selectedMod.getVersion()), offset, shifty);
                shifty = drawLine(String.format("Mod ID: '%s' Mod State: %s", selectedMod.getModId(), Loader.instance().getModState(selectedMod)), offset, shifty);
                if (!selectedMod.getMetadata().credits.isEmpty()) {
                   shifty = drawLine(String.format("Credits: %s", selectedMod.getMetadata().credits), offset, shifty);
                }
                shifty = drawLine(String.format("Authors: %s", selectedMod.getMetadata().getAuthorList()), offset, shifty);
                shifty = drawLine(String.format("URL: %s", selectedMod.getMetadata().url), offset, shifty);
                shifty = drawLine(selectedMod.getMetadata().childMods.isEmpty() ? "No child mods for this mod" : String.format("Child mods: %s", selectedMod.getMetadata().getChildModList()), offset, shifty);
                int rightSide = this.field_146294_l - offset - 20;
                if (rightSide > 20)
                {
                    this.getFontRenderer().func_78279_b(selectedMod.getMetadata().description, offset, shifty + 10, rightSide, 0xDDDDDD);
                }
                Disableable disableable = selectedMod.canBeDisabled();
                if (disableable == Disableable.RESTART)
                {
                    disableModButton.field_146124_l = true;
                    disableModButton.field_146125_m = true;
                    disableModButton.packedFGColour = 0xFF3377;
                }
                else if (disableable == Disableable.YES)
                {
                    disableModButton.field_146124_l = true;
                    disableModButton.field_146125_m = true;
                    disableModButton.packedFGColour = 0;
                }
                else
                {
                    disableModButton.packedFGColour = 0;
                    disableModButton.field_146125_m = true;
                    disableModButton.field_146124_l = false;
                }
                IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(selectedMod);
                if (guiFactory == null || guiFactory.mainConfigGuiClass() == null)
                {
                    configModButton.field_146125_m = true;
                    configModButton.field_146124_l = false;
                }
                else
                {
                    configModButton.field_146125_m = true;
                    configModButton.field_146124_l = true;
                }
            } else {
                offset = ( this.listWidth + this.field_146294_l ) / 2;
                this.func_73732_a(this.field_146289_q, selectedMod.getName(), offset, 35, 0xFFFFFF);
                this.func_73732_a(this.field_146289_q, String.format("Version: %s",selectedMod.getVersion()), offset, 45, 0xFFFFFF);
                this.func_73732_a(this.field_146289_q, String.format("Mod State: %s",Loader.instance().getModState(selectedMod)), offset, 55, 0xFFFFFF);
                this.func_73732_a(this.field_146289_q, "No mod information found", offset, 65, 0xDDDDDD);
                this.func_73732_a(this.field_146289_q, "Ask your mod author to provide a mod mcmod.info file", offset, 75, 0xDDDDDD);
                configModButton.field_146125_m = false;
                disableModButton.field_146125_m = false;
            }
            GL11.glDisable(GL11.GL_BLEND);
        }
        else
        {
            configModButton.field_146125_m = false;
            disableModButton.field_146125_m = false;
        }
        super.func_73863_a(p_571_1_, p_571_2_, p_571_3_);
    }

    Minecraft getMinecraftInstance() {
        return field_146297_k;
    }

    FontRenderer getFontRenderer() {
        return field_146289_q;
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
        cachedLogo = null;
    }

    public boolean modIndexSelected(int var1)
    {
        return var1==selected;
    }
}
