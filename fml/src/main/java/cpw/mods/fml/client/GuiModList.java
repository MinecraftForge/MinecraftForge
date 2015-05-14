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
    public void initGui()
    {
        for (ModContainer mod : mods) {
            listWidth=Math.max(listWidth,getFontRenderer().getStringWidth(mod.getName()) + 10);
            listWidth=Math.max(listWidth,getFontRenderer().getStringWidth(mod.getVersion()) + 10);
        }
        listWidth=Math.min(listWidth, 150);
        this.buttonList.add(new GuiButton(6, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
        configModButton = new GuiButton(20, 10, this.height - 60, this.listWidth, 20, "Config");
        disableModButton = new GuiButton(21, 10, this.height - 38, this.listWidth, 20, "Disable");
        this.buttonList.add(configModButton);
        this.buttonList.add(disableModButton);
        this.modList=new GuiSlotModList(this, mods, listWidth);
        this.modList.registerScrollButtons(this.buttonList, 7, 8);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled)
        {
            switch (button.id)
            {
                case 6:
                    this.mc.displayGuiScreen(this.mainMenu);
                    return;
                case 20:
                    try
                    {
                        IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(selectedMod);
                        GuiScreen newScreen = guiFactory.mainConfigGuiClass().getConstructor(GuiScreen.class).newInstance(this);
                        this.mc.displayGuiScreen(newScreen);
                    }
                    catch (Exception e)
                    {
                        FMLLog.log(Level.ERROR, e, "There was a critical issue trying to build the config GUI for %s", selectedMod.getModId());
                    }
                    return;
            }
        }
        super.actionPerformed(button);
    }

    public int drawLine(String line, int offset, int shifty)
    {
        this.fontRendererObj.drawString(line, offset, shifty, 0xd7edea);
        return shifty + 10;
    }

    @Override
    public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        this.modList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        this.drawCenteredString(this.fontRendererObj, "Mod List", this.width / 2, 16, 0xFFFFFF);
        int offset = this.listWidth  + 20;
        if (selectedMod != null) {
            GL11.glEnable(GL11.GL_BLEND);
            if (!selectedMod.getMetadata().autogenerated) {
                configModButton.visible = true;
                disableModButton.visible = true;
                disableModButton.packedFGColour = 0xFF3377;
                configModButton.enabled = false;
                int shifty = 35;
                String logoFile = selectedMod.getMetadata().logoFile;
                if (!logoFile.isEmpty())
                {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    TextureManager tm = mc.getTextureManager();
                    IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(selectedMod.getModId());
                    try
                    {
                        if (cachedLogo == null)
                        {
                            BufferedImage logo = null;
                            if (pack!=null)
                            {
                                logo = pack.getPackImage();
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
                                cachedLogo = tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo));
                                cachedLogoDimensions = new Dimension(logo.getWidth(), logo.getHeight());
                            }
                        }
                        if (cachedLogo != null)
                        {
                            this.mc.renderEngine.bindTexture(cachedLogo);
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
                            Tessellator tess = Tessellator.instance;
                            tess.startDrawingQuads();
                            tess.addVertexWithUV(offset,                               top + cachedLogoDimensions.height,  zLevel, 0, 1);
                            tess.addVertexWithUV(offset + cachedLogoDimensions.width,  top + cachedLogoDimensions.height,  zLevel, 1, 1);
                            tess.addVertexWithUV(offset + cachedLogoDimensions.width,  top,                                zLevel, 1, 0);
                            tess.addVertexWithUV(offset,                               top,                                zLevel, 0, 0);
                            tess.draw();

                            shifty += 65;
                        }
                    }
                    catch (IOException e)
                    {
                        ;
                    }
                }
                this.fontRendererObj.drawStringWithShadow(selectedMod.getMetadata().name, offset, shifty, 0xFFFFFF);
                shifty += 12;

                shifty = drawLine(String.format("Version: %s (%s)", selectedMod.getDisplayVersion(), selectedMod.getVersion()), offset, shifty);
                shifty = drawLine(String.format("Mod ID: '%s' Mod State: %s", selectedMod.getModId(), Loader.instance().getModState(selectedMod)), offset, shifty);
                if (!selectedMod.getMetadata().credits.isEmpty()) {
                   shifty = drawLine(String.format("Credits: %s", selectedMod.getMetadata().credits), offset, shifty);
                }
                shifty = drawLine(String.format("Authors: %s", selectedMod.getMetadata().getAuthorList()), offset, shifty);
                shifty = drawLine(String.format("URL: %s", selectedMod.getMetadata().url), offset, shifty);
                shifty = drawLine(selectedMod.getMetadata().childMods.isEmpty() ? "No child mods for this mod" : String.format("Child mods: %s", selectedMod.getMetadata().getChildModList()), offset, shifty);
                int rightSide = this.width - offset - 20;
                if (rightSide > 20)
                {
                    this.getFontRenderer().drawSplitString(selectedMod.getMetadata().description, offset, shifty + 10, rightSide, 0xDDDDDD);
                }
                Disableable disableable = selectedMod.canBeDisabled();
                if (disableable == Disableable.RESTART)
                {
                    disableModButton.enabled = true;
                    disableModButton.visible = true;
                    disableModButton.packedFGColour = 0xFF3377;
                }
                else if (disableable == Disableable.YES)
                {
                    disableModButton.enabled = true;
                    disableModButton.visible = true;
                    disableModButton.packedFGColour = 0;
                }
                else
                {
                    disableModButton.packedFGColour = 0;
                    disableModButton.visible = true;
                    disableModButton.enabled = false;
                }
                IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(selectedMod);
                if (guiFactory == null || guiFactory.mainConfigGuiClass() == null)
                {
                    configModButton.visible = true;
                    configModButton.enabled = false;
                }
                else
                {
                    configModButton.visible = true;
                    configModButton.enabled = true;
                }
            } else {
                offset = ( this.listWidth + this.width ) / 2;
                this.drawCenteredString(this.fontRendererObj, selectedMod.getName(), offset, 35, 0xFFFFFF);
                this.drawCenteredString(this.fontRendererObj, String.format("Version: %s",selectedMod.getVersion()), offset, 45, 0xFFFFFF);
                this.drawCenteredString(this.fontRendererObj, String.format("Mod State: %s",Loader.instance().getModState(selectedMod)), offset, 55, 0xFFFFFF);
                this.drawCenteredString(this.fontRendererObj, "No mod information found", offset, 65, 0xDDDDDD);
                this.drawCenteredString(this.fontRendererObj, "Ask your mod author to provide a mod mcmod.info file", offset, 75, 0xDDDDDD);
                configModButton.visible = false;
                disableModButton.visible = false;
            }
            GL11.glDisable(GL11.GL_BLEND);
        }
        else
        {
            configModButton.visible = false;
            disableModButton.visible = false;
        }
        super.drawScreen(p_571_1_, p_571_2_, p_571_3_);
    }

    Minecraft getMinecraftInstance() {
        return mc;
    }

    FontRenderer getFontRenderer() {
        return fontRendererObj;
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
