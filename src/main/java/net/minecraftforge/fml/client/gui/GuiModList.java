/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.client.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.client.ResourcePackLoader;
import net.minecraftforge.fml.language.ModContainer;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

import static net.minecraft.util.StringUtils.stripControlCodes;

import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.loading.ModList;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;

import org.lwjgl.opengl.GL11;

/**
 * @author cpw
 *
 */
public class GuiModList extends GuiScreen
{
    private static final Logger LOGGER = LogManager.getLogger();

    private enum SortType implements Comparator<ModInfo>
    {
        NORMAL(24),
        A_TO_Z(25){ @Override protected int compare(String name1, String name2){ return name1.compareTo(name2); }},
        Z_TO_A(26){ @Override protected int compare(String name1, String name2){ return name2.compareTo(name1); }};


        private int buttonID;
        private SortType(int buttonID)
        {
            this.buttonID = buttonID;
        }

        @Nullable
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
        public int compare(ModInfo o1, ModInfo o2)
        {
            String name1 = StringUtils.toLowerCase(stripControlCodes(o1.getDisplayName()));
            String name2 = StringUtils.toLowerCase(stripControlCodes(o2.getDisplayName()));
            return compare(name1, name2);
        }

    }

    private GuiScreen mainMenu;
    private GuiSlotModList modList;
    private GuiScrollingList modInfo;
    private int selected = -1;
    private ModInfo selectedMod;
    private int listWidth;
    private List<ModInfo> mods;
    private GuiButton configModButton;

    private final List<ModInfo> unsortedMods;
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
        this.mods = ModList.get().getModFiles().stream().
                map(mf -> mf.getMods().stream().findFirst()).
                map(Optional::get).
                map(ModInfo.class::cast).
                collect(Collectors.toList());
        this.unsortedMods = Collections.unmodifiableList(this.mods);
    }

    @Override
    public void initGui()
    {
        int slotHeight = 35;
        for (ModInfo mod : mods)
        {
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getDisplayName()) + 10);
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getVersion().getVersionString()) + 5 + slotHeight);
        }
        listWidth = Math.min(listWidth, 150);
        this.modList = new GuiSlotModList(this, mods, listWidth, slotHeight);

        this.buttonList.add(new GuiButton(6, ((modList.right + this.width) / 2) - 100, this.height - 38, I18n.format("gui.done")));
        configModButton = new GuiButton(20, 10, this.height - 49, this.listWidth, 20, "Config");
        this.buttonList.add(configModButton);

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
        if (button == 1 && x >= search.x && x < search.x + search.width && y >= search.y && y < search.y + search.height) {
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
            mods.sort(sortType);
            selected = modList.selectedIndex = mods.indexOf(selectedMod);
            sorted = true;
        }
    }

    private void reloadMods()
    {
        this.mods = this.unsortedMods.stream().
                filter(mi->StringUtils.toLowerCase(stripControlCodes(mi.getDisplayName())).contains(StringUtils.toLowerCase(search.getText()))).collect(Collectors.toList());
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
                for (GuiButton b : buttonList)
                {
                    if (SortType.getTypeForButton(b) != null)
                    {
                        b.enabled = true;
                    }
                }
                button.enabled = false;
                sorted = false;
                sortType = type;
                Collections.copy(this.mods, this.unsortedMods);
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
                            ConfigGuiHandler.getGuiFactoryFor(selectedMod).
                                    map(f->f.apply(this.mc, this)).
                                    ifPresent(newScreen -> this.mc.displayGuiScreen(newScreen));
                        }
                        catch (final Exception e)
                        {
                            LOGGER.error("There was a critical issue trying to build the config GUI for {}", selectedMod.getModId(), e);
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
        this.fontRenderer.drawString(line, offset, shifty, 0xd7edea);
        return shifty + 10;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.modList.drawScreen(mouseX, mouseY, partialTicks);
        if (this.modInfo != null)
            this.modInfo.drawScreen(mouseX, mouseY, partialTicks);

        int left = ((this.width - this.listWidth - 38) / 2) + this.listWidth + 30;
        this.drawCenteredString(this.fontRenderer, "Mod List", left, 16, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);

        String text = I18n.format("fml.menu.mods.search");
        int x = ((10 + modList.right) / 2) - (getFontRenderer().getStringWidth(text) / 2);
        getFontRenderer().drawString(text, x, modList.bottom + 5, 0xFFFFFF);
        search.drawTextBox();
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        super.handleMouseInput();
        if (this.modInfo != null)
            this.modInfo.handleMouseInput(mouseX, mouseY);
        this.modList.handleMouseInput(mouseX, mouseY);
    }

    Minecraft getMinecraftInstance()
    {
        return mc;
    }

    FontRenderer getFontRenderer()
    {
        return fontRenderer;
    }

    public void selectModIndex(int index)
    {
        if (index == this.selected)
            return;
        this.selected = index;
        this.selectedMod = (index >= 0 && index < mods.size()) ? mods.get(selected) : null;

        updateCache();
    }

    public boolean modIndexSelected(int index)
    {
        return index == selected;
    }

    private void updateCache()
    {
        configModButton.visible = false;
        modInfo = null;

        if (selectedMod == null)
            return;

        List<String> lines = new ArrayList<>();
        VersionChecker.CheckResult vercheck = VersionChecker.getResult(selectedMod);

        Pair<ResourceLocation, Dimension> logoData = selectedMod.getLogoFile().map(logoFile->
        {
            TextureManager tm = mc.getTextureManager();
            IResourcePack pack = ResourcePackLoader.getResourcePackFor(selectedMod.getModId());
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
                    return Pair.of(tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo)), new Dimension(logo.getWidth(), logo.getHeight()));
                }
            }
            catch (IOException e) { }
            return Pair.<ResourceLocation, Dimension>of(null, new Dimension(0, 0));
        }).orElse(Pair.of(null, new Dimension(0, 0)));

        configModButton.visible = true;
        configModButton.enabled = true;
        lines.add(selectedMod.getDisplayName());
        lines.add(String.format("Version: %s", selectedMod.getVersion().getVersionString()));
        lines.add(String.format("Mod ID: '%s' Mod State: %s", selectedMod.getModId(), ModList.get().getModContainerById(selectedMod.getModId()).
                map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE")));

        selectedMod.getModConfig().getOptional("credits").ifPresent(credits->
                lines.add("Credits: " + credits));
        selectedMod.getModConfig().getOptional("authors").ifPresent(authors ->
                lines.add("Authors: " + authors));
        selectedMod.getModConfig().getOptional("displayURL").ifPresent(displayURL ->
                lines.add("URL: " + displayURL));
        if (selectedMod.getOwningFile().getMods().size()==1)
            lines.add("No child mods for this mod");
        else
            lines.add("Child mods: " + selectedMod.getOwningFile().getMods().stream().map(IModInfo::getDisplayName).collect(Collectors.joining(",")));

        if (vercheck.status == VersionChecker.Status.OUTDATED || vercheck.status == VersionChecker.Status.BETA_OUTDATED)
            lines.add("Update Available: " + (vercheck.url == null ? "" : vercheck.url));

        lines.add(null);
        lines.add(selectedMod.getDescription());

        if ((vercheck.status == VersionChecker.Status.OUTDATED || vercheck.status == VersionChecker.Status.BETA_OUTDATED) && vercheck.changes.size() > 0)
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

        modInfo = new Info(this.width - this.listWidth - 30, lines, logoData.getLeft(), logoData.getRight());
    }

    private class Info extends GuiScrollingList
    {
        @Nullable
        private ResourceLocation logoPath;
        private Dimension logoDims;
        private List<ITextComponent> lines = null;

        public Info(int width, List<String> lines, @Nullable ResourceLocation logoPath, Dimension logoDims)
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

        private List<ITextComponent> resizeContent(List<String> lines)
        {
            List<ITextComponent> ret = new ArrayList<ITextComponent>();
            for (String line : lines)
            {
                if (line == null)
                {
                    ret.add(null);
                    continue;
                }

                ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
                int maxTextLength = this.listWidth - 8;
                if (maxTextLength >= 0)
                {
                    ret.addAll(GuiUtilRenderComponents.splitText(chat, maxTextLength, GuiModList.this.fontRenderer, false, true));
                }
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


        @Override
        protected void drawHeader(int entryRight, int relativeY, Tessellator tess)
        {
            int top = relativeY;

            if (logoPath != null)
            {
                GlStateManager.enableBlend();
                GuiModList.this.mc.renderEngine.bindTexture(logoPath);
                BufferBuilder wr = tess.getBuffer();
                int offset = (this.left + this.listWidth/2) - (logoDims.width / 2);
                wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                wr.pos(offset,                  top + logoDims.height, zLevel).tex(0, 1).endVertex();
                wr.pos(offset + logoDims.width, top + logoDims.height, zLevel).tex(1, 1).endVertex();
                wr.pos(offset + logoDims.width, top,                   zLevel).tex(1, 0).endVertex();
                wr.pos(offset,                  top,                   zLevel).tex(0, 0).endVertex();
                tess.draw();
                GlStateManager.disableBlend();
                top += logoDims.height + 10;
            }

            for (ITextComponent line : lines)
            {
                if (line != null)
                {
                    GlStateManager.enableBlend();
                    GuiModList.this.fontRenderer.drawStringWithShadow(line.getFormattedText(), this.left + 4, top, 0xFFFFFF);
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

            ITextComponent line = lines.get(lineIdx);
            if (line != null)
            {
                int k = -4;
                for (ITextComponent part : line) {
                    if (!(part instanceof TextComponentString))
                        continue;
                    k += GuiModList.this.fontRenderer.getStringWidth(((TextComponentString)part).getText());
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
