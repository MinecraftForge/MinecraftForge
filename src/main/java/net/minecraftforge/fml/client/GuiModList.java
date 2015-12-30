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
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModContainer.Disableable;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import static net.minecraft.util.EnumChatFormatting.*;

import org.apache.logging.log4j.Level;

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
    private GuiScrollingList modInfo;
    private int selected = -1;
    private ModContainer selectedMod;
    private int listWidth;
    private ArrayList<ModContainer> mods;
    private GuiButton configModButton;
    private GuiButton disableModButton;

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

        updateCache();
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
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            SortType type = SortType.getTypeForButton(button);

            if (type != null)
            {
                for (GuiButton b : (List<GuiButton>)buttonList)
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
                    {
                        this.mc.displayGuiScreen(this.mainMenu);
                        return;
                    }
                    case 20:
                    {
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
        }
        super.actionPerformed(button);
    }

    public int drawLine(String line, int offset, int shifty)
    {
        this.fontRendererObj.drawString(line, offset, shifty, 0xd7edea);
        return shifty + 10;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.modList.drawScreen(mouseX, mouseY, partialTicks);
        if (this.modInfo != null)
            this.modInfo.drawScreen(mouseX, mouseY, partialTicks);

        int left = ((this.width - this.listWidth - 38) / 2) + this.listWidth + 30;
        this.drawCenteredString(this.fontRendererObj, "Mod List", left, 16, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);

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
        if (index == this.selected)
            return;
        this.selected = index;
        this.selectedMod = (index >= 0 && index <= mods.size()) ? mods.get(selected) : null;

        updateCache();
    }

    public boolean modIndexSelected(int index)
    {
        return index == selected;
    }

    private void updateCache()
    {
        configModButton.visible = false;
        disableModButton.visible = false;
        modInfo = null;

        if (selectedMod == null)
            return;

        ResourceLocation logoPath = null;
        Dimension logoDims = new Dimension(0, 0);
        List<String> lines = new ArrayList<String>();
        CheckResult vercheck = ForgeVersion.getResult(selectedMod);

        String logoFile = selectedMod.getMetadata().logoFile;
        if (!logoFile.isEmpty())
        {
            TextureManager tm = mc.getTextureManager();
            IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(selectedMod.getModId());
            try
            {
                BufferedImage logo = null;
                if (pack != null)
                {
                    logo = pack.getPackImage();
                }
                else
                {
                    InputStream logoResource = getClass().getResourceAsStream(logoFile);
                    if (logoResource != null)
                        logo = ImageIO.read(logoResource);
                }
                if (logo != null)
                {
                    logoPath = tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo));
                    logoDims = new Dimension(logo.getWidth(), logo.getHeight());
                }
            }
            catch (IOException e) { }
        }

        if (!selectedMod.getMetadata().autogenerated)
        {
            disableModButton.visible = true;
            disableModButton.enabled = true;
            disableModButton.packedFGColour = 0;
            Disableable disableable = selectedMod.canBeDisabled();
            if (disableable == Disableable.RESTART)
            {
                disableModButton.packedFGColour = 0xFF3377;
            }
            else if (disableable != Disableable.YES)
            {
                disableModButton.enabled = false;
            }

            IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(selectedMod);
            configModButton.visible = true;
            configModButton.enabled = guiFactory != null && guiFactory.mainConfigGuiClass() != null;

            lines.add(selectedMod.getMetadata().name);
            lines.add(String.format("Version: %s (%s)", selectedMod.getDisplayVersion(), selectedMod.getVersion()));
            lines.add(String.format("Mod ID: '%s' Mod State: %s", selectedMod.getModId(), Loader.instance().getModState(selectedMod)));

            if (!selectedMod.getMetadata().credits.isEmpty())
            {
                lines.add("Credits: " + selectedMod.getMetadata().credits);
            }

            lines.add("Authors: " + selectedMod.getMetadata().getAuthorList());
            lines.add("URL: " + selectedMod.getMetadata().url);

            if (selectedMod.getMetadata().childMods.isEmpty())
                lines.add("No child mods for this mod");
            else
                lines.add("Child mods: " + selectedMod.getMetadata().getChildModList());

            if (vercheck.status == Status.OUTDATED || vercheck.status == Status.BETA_OUTDATED)
                lines.add("Update Avalible: " + (vercheck.url == null ? "" : vercheck.url));

            lines.add(null);
            lines.add(selectedMod.getMetadata().description);
        }
        else
        {
            lines.add(WHITE + selectedMod.getName());
            lines.add(WHITE + "Version: " + selectedMod.getVersion());
            lines.add(WHITE + "Mod State: " + Loader.instance().getModState(selectedMod));
            if (vercheck.status == Status.OUTDATED || vercheck.status == Status.BETA_OUTDATED)
                lines.add("Update Available: " + (vercheck.url == null ? "" : vercheck.url));

            lines.add(null);
            lines.add(RED + "No mod information found");
            lines.add(RED + "Ask your mod author to provide a mod mcmod.info file");
        }

        if ((vercheck.status == Status.OUTDATED || vercheck.status == Status.BETA_OUTDATED) && vercheck.changes.size() > 0)
        {
            lines.add(null);
            lines.add("Changes:");
            for (Entry<ComparableVersion, String> entry : vercheck.changes.entrySet())
            {
                lines.add("  " + entry.getKey() + ":");
                lines.add(entry.getValue());
                lines.add(null);
            }
        }

        modInfo = new Info(this.width - this.listWidth - 30, lines, logoPath, logoDims);
    }

    private class Info extends GuiScrollingList
    {
        private ResourceLocation logoPath;
        private Dimension logoDims;
        private List<IChatComponent> lines = null;

        public Info(int width, List<String> lines, ResourceLocation logoPath, Dimension logoDims)
        {
            super(GuiModList.this.getMinecraftInstance(),
                  width,
                  GuiModList.this.height,
                  32, GuiModList.this.height - 88 + 4,
                  GuiModList.this.listWidth + 20, 60,
                  GuiModList.this.width,
                  GuiModList.this.height);
            this.lines    = resizeContent(lines);
            this.logoPath = logoPath;
            this.logoDims = logoDims;

            this.setHeaderInfo(true, getHeaderHeight());
        }

        @Override protected int getSize() { return 0; }
        @Override protected void elementClicked(int index, boolean doubleClick) { }
        @Override protected boolean isSelected(int index) { return false; }
        @Override protected void drawBackground() {}
        @Override protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) { }

        private List<IChatComponent> resizeContent(List<String> lines)
        {
            List<IChatComponent> ret = new ArrayList<IChatComponent>();
            for (String line : lines)
            {
                if (line == null)
                {
                    ret.add(null);
                    continue;
                }

                IChatComponent chat = ForgeHooks.newChatWithLinks(line, false);
                ret.addAll(GuiUtilRenderComponents.func_178908_a(chat, this.listWidth-8, GuiModList.this.fontRendererObj, false, true));
            }
            return ret;
        }

        private int getHeaderHeight()
        {
          int height = 0;
          if (logoPath != null)
          {
              double scaleX = logoDims.width / 200.0;
              double scaleY = logoDims.height / 65.0;
              double scale = 1.0;
              if (scaleX > 1 || scaleY > 1)
              {
                  scale = 1.0 / Math.max(scaleX, scaleY);
              }
              logoDims.width *= scale;
              logoDims.height *= scale;

              height += logoDims.height;
              height += 10;
          }
          height += (lines.size() * 10);
          if (height < this.bottom - this.top - 8) height = this.bottom - this.top - 8;
          return height;
        }


        protected void drawHeader(int entryRight, int relativeY, Tessellator tess)
        {
            int top = relativeY;

            if (logoPath != null)
            {
                GlStateManager.enableBlend();
                GuiModList.this.mc.renderEngine.bindTexture(logoPath);
                WorldRenderer wr = tess.getWorldRenderer();
                int offset = (this.left + this.listWidth/2) - (logoDims.width / 2);
                wr.begin(7, DefaultVertexFormats.POSITION_TEX);
                wr.pos(offset,                  top + logoDims.height, zLevel).tex(0, 1).endVertex();
                wr.pos(offset + logoDims.width, top + logoDims.height, zLevel).tex(1, 1).endVertex();
                wr.pos(offset + logoDims.width, top,                   zLevel).tex(1, 0).endVertex();
                wr.pos(offset,                  top,                   zLevel).tex(0, 0).endVertex();
                tess.draw();
                GlStateManager.disableBlend();
                top += logoDims.height + 10;
            }

            for (IChatComponent line : lines)
            {
                if (line != null)
                {
                    GlStateManager.enableBlend();
                    GuiModList.this.fontRendererObj.drawStringWithShadow(line.getFormattedText(), this.left + 4, top, 0xFFFFFF);
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
                top += 10;
            }
        }

        @Override
        protected void clickHeader(int x, int y)
        {
            int offset = y;
            if (logoPath != null) {
              offset -= logoDims.height + 10;
            }
            if (offset <= 0)
                return;

            int lineIdx = offset / 10;
            if (lineIdx >= lines.size())
                return;

            IChatComponent line = lines.get(lineIdx);
            if (line != null)
            {
                int k = -4;
                for (IChatComponent part : (Iterable<IChatComponent>)line) {
                    if (!(part instanceof ChatComponentText))
                        continue;
                    k += GuiModList.this.fontRendererObj.getStringWidth(((ChatComponentText)part).getChatComponentText_TextValue());
                    if (k >= x)
                    {
                        GuiModList.this.handleComponentClick(part);
                        break;
                    }
                }
            }
        }
    }
}
