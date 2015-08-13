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

package net.minecraftforge.fml.client;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModContainer.Disableable;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

/**
 * @author cpw
 *
 */
public class GuiModList extends GuiScreen
{
    private enum SortType implements Comparator<ModContainer>
    {
        NORMAL(24),
        A_TO_Z(25){ @Override protected int compare(String name1, String name2){ return name1.compareTo(name2); }},
        Z_TO_A(26){ @Override protected int compare(String name1, String name2){ return name2.compareTo(name1); }};

        private int buttonID;

        private SortType(int buttonID)
        {
            this.buttonID = buttonID;
        }

        public static SortType getTypeForButton(GuiButton button)
        {
            for (SortType t : values())
            {
                if (t.buttonID == button.id)
                {
                    return t;
                }
            }
            return null;
        }

        protected int compare(String name1, String name2){ return 0; }

        @Override
        public int compare(ModContainer o1, ModContainer o2)
        {
            String name1 = StringUtils.stripControlCodes(o1.getName()).toLowerCase();
            String name2 = StringUtils.stripControlCodes(o2.getName()).toLowerCase();
            return compare(name1, name2);
        }
    }

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

    private int buttonMargin = 1;
    private int numButtons = SortType.values().length;

    private String lastFilterText = "";

    private GuiTextField search;
    private boolean sorted = false;
    private SortType sortType = SortType.NORMAL;

    /**
     * @param mainMenu
     */
    public GuiModList(GuiScreen mainMenu)
    {
        this.mainMenu = mainMenu;
        this.mods = new ArrayList<ModContainer>();
        FMLClientHandler.instance().addSpecialModEntries(mods);
        // Add child mods to their parent's list
        for (ModContainer mod : Loader.instance().getModList())
        {
            if (mod.getMetadata() != null && mod.getMetadata().parentMod == null && !Strings.isNullOrEmpty(mod.getMetadata().parent))
            {
                String parentMod = mod.getMetadata().parent;
                ModContainer parentContainer = Loader.instance().getIndexedModList().get(parentMod);
                if (parentContainer != null)
                {
                    mod.getMetadata().parentMod = parentContainer;
                    parentContainer.getMetadata().childMods.add(mod);
                    continue;
                }
            }
            else if (mod.getMetadata() != null && mod.getMetadata().parentMod != null)
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
        for (ModContainer mod : mods)
        {
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getName()) + 10);
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getVersion()) + 10);
        }
        listWidth = Math.min(listWidth, 150);
        this.modList = new GuiSlotModList(this, mods, listWidth);
        this.modList.registerScrollButtons(this.buttonList, 7, 8);

        this.buttonList.add(new GuiButton(6, ((modList.right + this.width) / 2) - 100, this.height - 38, I18n.format("gui.done")));
        configModButton = new GuiButton(20, 10, this.height - 49, this.listWidth, 20, "Config");
        disableModButton = new GuiButton(21, 10, this.height - 27, this.listWidth, 20, "Disable");
        this.buttonList.add(configModButton);
        this.buttonList.add(disableModButton);

        search = new GuiTextField(0, getFontRenderer(), 12, modList.bottom + 17, modList.listWidth - 4, 14);
        search.setFocused(true);
        search.setCanLoseFocus(true);

        int width = (modList.listWidth / numButtons);
        int x = 10, y = 10;
        GuiButton normalSort = new GuiButton(SortType.NORMAL.buttonID, x, y, width - buttonMargin, 20, I18n.format("fml.menu.mods.normal"));
        normalSort.enabled = false;
        buttonList.add(normalSort);
        x += width + buttonMargin;
        buttonList.add(new GuiButton(SortType.A_TO_Z.buttonID, x, y, width - buttonMargin, 20, "A-Z"));
        x += width + buttonMargin;
        buttonList.add(new GuiButton(SortType.Z_TO_A.buttonID, x, y, width - buttonMargin, 20, "Z-A"));
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        super.mouseClicked(x, y, button);
        search.mouseClicked(x, y, button);
        if (button == 1 && x >= search.xPosition && x < search.xPosition + search.width && y >= search.yPosition && y < search.yPosition + search.height) {
            search.setText("");
        }
    }

    @Override
    protected void keyTyped(char c, int keyCode) throws IOException
    {
        super.keyTyped(c, keyCode);
        search.textboxKeyTyped(c, keyCode);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        search.updateCursorCounter();

        if (!search.getText().equals(lastFilterText))
        {
            reloadMods();
            sorted = false;
        }

        if (!sorted)
        {
            reloadMods();
            Collections.sort(mods, sortType);
            selected = modList.selectedIndex = mods.indexOf(selectedMod);
            sorted = true;
        }
    }

    private void reloadMods()
    {
        ArrayList<ModContainer> mods = modList.getMods();
        mods.clear();
        for (ModContainer m : Loader.instance().getActiveModList())
        {
            // If it passes the filter, and is not a child mod
            if (m.getName().toLowerCase().contains(search.getText().toLowerCase()) && m.getMetadata().parentMod == null)
            {
                mods.add(m);
            }
        }
        this.mods = mods;
        lastFilterText = search.getText();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled)
        {
            SortType type = SortType.getTypeForButton(button);

            if (type != null)
            {
                for (GuiButton b : (List<GuiButton>) buttonList)
                {
                    if (SortType.getTypeForButton(b) != null)
                    {
                        b.enabled = true;
                    }
                }
                button.enabled = false;
                sorted = false;
                sortType = type;
                this.mods = modList.getMods();
            }
            else
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
        if (selectedMod != null)
        {
            GlStateManager.enableBlend();
            if (!selectedMod.getMetadata().autogenerated)
            {
                configModButton.visible = true;
                disableModButton.visible = true;
                disableModButton.packedFGColour = 0xFF3377;
                configModButton.enabled = false;
                int shifty = 35;
                String logoFile = selectedMod.getMetadata().logoFile;
                if (!logoFile.isEmpty())
                {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
                            Tessellator tess = Tessellator.getInstance();
                            WorldRenderer world = tess.getWorldRenderer();
                            world.startDrawingQuads();
                            world.addVertexWithUV(offset,                               top + cachedLogoDimensions.height,  zLevel, 0, 1);
                            world.addVertexWithUV(offset + cachedLogoDimensions.width,  top + cachedLogoDimensions.height,  zLevel, 1, 1);
                            world.addVertexWithUV(offset + cachedLogoDimensions.width,  top,                                zLevel, 1, 0);
                            world.addVertexWithUV(offset,                               top,                                zLevel, 0, 0);
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
                if (!selectedMod.getMetadata().credits.isEmpty())
                {
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
            }
            else
            {
                offset = ( this.listWidth + this.width ) / 2;
                this.drawCenteredString(this.fontRendererObj, selectedMod.getName(), offset, 35, 0xFFFFFF);
                this.drawCenteredString(this.fontRendererObj, String.format("Version: %s",selectedMod.getVersion()), offset, 45, 0xFFFFFF);
                this.drawCenteredString(this.fontRendererObj, String.format("Mod State: %s",Loader.instance().getModState(selectedMod)), offset, 55, 0xFFFFFF);
                this.drawCenteredString(this.fontRendererObj, "No mod information found", offset, 65, 0xDDDDDD);
                this.drawCenteredString(this.fontRendererObj, "Ask your mod author to provide a mod mcmod.info file", offset, 75, 0xDDDDDD);
                configModButton.visible = false;
                disableModButton.visible = false;
            }
            GlStateManager.disableBlend();
        }
        else
        {
            configModButton.visible = false;
            disableModButton.visible = false;
        }
        super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

        String text = I18n.format("fml.menu.mods.search");
        int x = ((10 + modList.right) / 2) - (getFontRenderer().getStringWidth(text) / 2);
        getFontRenderer().drawString(text, x, modList.bottom + 5, 0xFFFFFF);
        search.drawTextBox();
    }

    Minecraft getMinecraftInstance()
    {
        return mc;
    }

    FontRenderer getFontRenderer()
    {
        return fontRendererObj;
    }

    public void selectModIndex(int index)
    {
        this.selected = index;
        this.selectedMod = (index >= 0 && index <= mods.size()) ? mods.get(selected) : null;
        cachedLogo = null;
    }

    public boolean modIndexSelected(int index)
    {
        return index == selected;
    }
}
