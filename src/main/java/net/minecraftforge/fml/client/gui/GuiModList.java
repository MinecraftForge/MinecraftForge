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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.client.ResourcePackLoader;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

import static net.minecraft.util.StringUtils.stripControlCodes;

import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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



        GuiButton button;

        private int buttonID;
        private SortType(int buttonID)
        {
            this.buttonID = buttonID;
        }
        protected int compare(String name1, String name2){ return 0; }
        @Override
        public int compare(ModInfo o1, ModInfo o2)
        {
            String name1 = StringUtils.toLowerCase(stripControlCodes(o1.getDisplayName()));
            String name2 = StringUtils.toLowerCase(stripControlCodes(o2.getDisplayName()));
            return compare(name1, name2);
        }

        String getButtonText() {
            return I18n.format("fml.menu.mods."+StringUtils.toLowerCase(name()));
        }


    }
    private class SortButton extends GuiButton {
        private final SortType type;
        public SortButton(int buttonId, int x, int y, int width, int height, SortType type)
        {
            super(buttonId, x, y, width, height, type.getButtonText());
            this.type = type;
        }

        @Override
        public void func_194829_a(double p_194829_1_, double p_194829_3_)
        {
            GuiModList.this.sortType = this.type;
            resortMods();
        }
    }

    private GuiScreen mainMenu;

    private GuiSlotModList modList;
    private InfoPanel modInfo;
    private int selected = -1;
    private ModInfo selectedMod;
    private int listWidth;
    private List<ModInfo> mods;
    private final List<ModInfo> unsortedMods;
    private GuiButton configButton;

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
        this.mods = Collections.unmodifiableList(ModList.get().getMods());
        this.unsortedMods = Collections.unmodifiableList(this.mods);
    }

    class InfoPanel extends GuiListExtended<Info> {
        InfoPanel(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
        {
            super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        }

        @Override
        protected int getScrollBarX()
        {
            return this.width - 6;
        }

        void setInfo(Info info)
        {
            this.func_195086_c();
            this.func_195085_a(info);
        }

        public void clear()
        {
            this.func_195086_c();
        }
    }
    @Override
    public void initGui()
    {
        for (ModInfo mod : mods)
        {
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getDisplayName()) + 10);
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getVersion().getVersionString()) + 5);
        }
        listWidth = Math.min(listWidth, 150);
        this.modList = new GuiSlotModList(this, listWidth);
        this.modList.setSlotXBoundsFromLeft(6);
        this.modInfo = new InfoPanel(this.mc, this.width - this.listWidth - 30, this.height, 32, this.height - 88 + 4, 1);
        this.modInfo.setSlotXBoundsFromLeft(this.listWidth + 24);

        this.addButton(new GuiButton(6, ((modList.right + this.width) / 2) - 100, this.height - 38, I18n.format("gui.done")){
            @Override
            public void func_194829_a(double p_194829_1_, double p_194829_3_)
            {
                GuiModList.this.mc.displayGuiScreen(GuiModList.this.mainMenu);
            }
        });
        this.addButton(this.configButton = new GuiButton(20, 10, this.height - 49, this.listWidth, 20, I18n.format("fml.menu.mods.config")){
            @Override
            public void func_194829_a(double p_194829_1_, double p_194829_3_)
            {
                GuiModList.this.displayModConfig();
            }
        });

        search = new GuiTextField(0, getFontRenderer(), 12, modList.bottom + 17, modList.width - 4, 14);
        field_195124_j.add(search);
        field_195124_j.add(modList);
        search.setFocused(true);
        search.setCanLoseFocus(true);

        final int width = (modList.width / numButtons);
        int x = 10, y = 10;
        addButton(SortType.NORMAL.button = new SortButton(SortType.NORMAL.buttonID, x, y, width - buttonMargin, 20, SortType.NORMAL));
        x += width + buttonMargin;
        addButton(SortType.A_TO_Z.button = new SortButton(SortType.NORMAL.buttonID, x, y, width - buttonMargin, 20, SortType.A_TO_Z));
        x += width + buttonMargin;
        addButton(SortType.Z_TO_A.button = new SortButton(SortType.NORMAL.buttonID, x, y, width - buttonMargin, 20, SortType.Z_TO_A));
        resortMods();
        updateCache();
    }

    private void displayModConfig()
    {
        if (selectedMod == null) return;
        try
        {
            ConfigGuiHandler.getGuiFactoryFor(selectedMod).map(f->f.apply(this.mc, this)).ifPresent(newScreen -> this.mc.displayGuiScreen(newScreen));
        }
        catch (final Exception e)
        {
            LOGGER.error("There was a critical issue trying to build the config GUI for {}", selectedMod.getModId(), e);
        }
    }

    @Override
    public void updateScreen()
    {
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
            selected = mods.indexOf(selectedMod);
            modList.refreshList();
            sorted = true;
        }
    }

    public <T extends GuiListExtended.IGuiListEntry<T>> void buildModList(Consumer<T> modListViewConsumer, Function<ModInfo, T> newEntry)
    {
        mods.forEach(mod->modListViewConsumer.accept(newEntry.apply(mod)));
    }

    private void reloadMods()
    {
        this.mods = this.unsortedMods.stream().
                filter(mi->StringUtils.toLowerCase(stripControlCodes(mi.getDisplayName())).contains(StringUtils.toLowerCase(search.getText()))).collect(Collectors.toList());
        lastFilterText = search.getText();
    }

    private void resortMods()
    {
        for (GuiButton b : buttonList)
        {
            if (b instanceof SortButton) {
                b.enabled = sortType.button != b;
            }
        }
        sorted = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.modList.drawScreen(mouseX, mouseY, partialTicks);
        this.modInfo.drawScreen(mouseX, mouseY, partialTicks);

        int left = ((this.width - this.listWidth - 38) / 2) + this.listWidth + 30;
        super.drawScreen(mouseX, mouseY, partialTicks);

        String text = I18n.format("fml.menu.mods.search");
        int x = ((10 + modList.right) / 2) - (getFontRenderer().getStringWidth(text) / 2);
        getFontRenderer().func_211126_b(text, x, modList.bottom + 5, 0xFFFFFF);
        this.search.func_195608_a(mouseX, mouseY, partialTicks);
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
        if (index == this.selected) {
            this.selected = -1;
        } else
        {
            this.selected = index;
        }
        this.selectedMod = (this.selected >= 0 && this.selected< mods.size()) ? mods.get(this.selected) : null;

        updateCache();
    }

    public boolean modIndexSelected(int index)
    {
        return index == selected;
    }

    private void updateCache()
    {
        if (selectedMod == null) {
            modInfo.clear();
            return;
        }

        this.configButton.enabled = selectedMod.hasConfigUI();
        List<String> lines = new ArrayList<>();
        VersionChecker.CheckResult vercheck = VersionChecker.getResult(selectedMod);

        Pair<ResourceLocation, Dimension> logoData = selectedMod.getLogoFile().map(logoFile->
        {
            TextureManager tm = mc.getTextureManager();
            ResourcePackInfoClient pack = ResourcePackLoader.getResourcePackInfo(selectedMod.getModId());
            try
            {
                NativeImage logo = null;
                InputStream logoResource = getClass().getResourceAsStream(logoFile);
                if (logoResource != null)
                    logo = NativeImage.func_195713_a(logoResource);
                if (logo != null)
                {
                    return Pair.of(tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo)), new Dimension(logo.func_195702_a(), logo.func_195714_b()));
                }
            }
            catch (IOException e) { }
            return Pair.<ResourceLocation, Dimension>of(null, new Dimension(0, 0));
        }).orElse(Pair.of(null, new Dimension(0, 0)));

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
        if (selectedMod.getOwningFile() == null || selectedMod.getOwningFile().getMods().size()==1)
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

        modInfo.setInfo(new Info(modInfo, lines, logoData.getLeft(), logoData.getRight()));
    }

    class Info extends GuiListExtended.IGuiListEntry<Info>
    {
        private ResourceLocation logoPath;
        private Dimension logoDims;
        private List<ITextComponent> lines = null;

        public Info(GuiListExtended<Info> parent, List<String> lines, @Nullable ResourceLocation logoPath, Dimension logoDims)
        {
            this.field_195004_a = parent;
            this.lines    = resizeContent(lines);
            this.logoPath = logoPath;
            this.logoDims = logoDims;
        }

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

//                ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
                ITextComponent chat = new TextComponentString(line);
                int maxTextLength = this.func_195002_d() - 8;
                if (maxTextLength >= 0)
                {
                    ret.addAll(GuiUtilRenderComponents.splitText(chat, maxTextLength, GuiModList.this.fontRenderer, false, true));
                }
            }
            return ret;
        }


        @Override
        public void func_194999_a(int p_194999_1_, int p_194999_2_, int p_194999_3_, int p_194999_4_, boolean p_194999_5_, float p_194999_6_)
        {
            int top = this.func_195001_c();
            int left = this.func_195002_d();
/*
            int top = this.

            if (logoPath != null)
            {
                GlStateManager.enableBlend();
                GuiModList.this.mc.renderEngine.bindTexture(logoPath);
                GlStateManager.draw
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

*/
            for (ITextComponent line : lines)
            {
                if (line != null)
                {
                    GlStateManager.enableBlend();
                    GuiModList.this.fontRenderer.drawStringWithShadow(line.getFormattedText(), left + 4, top, 0xFFFFFF);
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
                top += fontRenderer.FONT_HEIGHT + 1;
            }

        }
    }
}
