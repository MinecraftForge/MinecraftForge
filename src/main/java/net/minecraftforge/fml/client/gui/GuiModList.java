/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import static net.minecraft.util.StringUtils.stripControlCodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;

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
        public void onClick(double mouseX, double mouseY)
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

    class InfoPanel extends GuiListExtended<InfoPanel.Info> {
        InfoPanel(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
        {
            super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        }

        @Override
        protected int getScrollBarX()
        {
            return this.right - 6;
        }

        @Override
        public int getListWidth() {
            return this.width;
        }

        void setInfo(Info info)
        {
            this.clearEntries();
            this.addEntry(info);
        }

        public void clear()
        {
            this.clearEntries();
        }
        class Info extends GuiListExtended.IGuiListEntry<Info>
        {
            private ResourceLocation logoPath;
            private Size2i logoDims;
            private List<ITextComponent> lines;

            public Info(GuiListExtended<Info> parent, List<String> lines, @Nullable ResourceLocation logoPath, Size2i logoDims)
            {
                this.list = parent;
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

                    ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
                    int maxTextLength = InfoPanel.this.width - 8;
                    if (maxTextLength >= 0)
                    {
                        ret.addAll(GuiUtilRenderComponents.splitText(chat, maxTextLength, GuiModList.this.fontRenderer, false, true));
                    }
                }
                return ret;
            }

            @Override
            public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks)
            {
                int top = this.getY();
                int left = this.getX();

                if (logoPath != null) {
                    mc.getTextureManager().bindTexture(logoPath);
                    GlStateManager.enableBlend();
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    // Draw the logo image inscribed in a rectangle with width entryWidth (minus some padding) and height 50
                    int headerHeight = 50;
                    GuiUtils.drawInscribedRect(left, top, entryWidth - 5, headerHeight, logoDims.width, logoDims.height, false, true);
                    top += headerHeight;
                }

                for (ITextComponent line : lines)
                {
                    if (line != null)
                    {
                        GlStateManager.enableBlend();
                        GuiModList.this.fontRenderer.drawStringWithShadow(line.getFormattedText(), left + 4, top, 0xFFFFFF);
                        GlStateManager.disableAlphaTest();
                        GlStateManager.disableBlend();
                    }
                    top += fontRenderer.FONT_HEIGHT;
                }

                final ITextComponent component = findTextLine(mouseX, mouseY, 0, 0);
                if (component!=null) {
                    GuiModList.this.handleComponentHover(component, mouseX, mouseY);
                }
            }

            private ITextComponent findTextLine(final int mouseX, final int mouseY, final int offX, final int offY) {
                int offset = mouseY - offY;
                if (logoPath != null) {
                    offset -= logoDims.height + 10;
                }
                if (offset <= 0)
                    return null;

                int lineIdx = offset / fontRenderer.FONT_HEIGHT;
                if (lineIdx >= lines.size() || lineIdx < 1)
                    return null;

                ITextComponent line = lines.get(lineIdx-1);
                if (line != null)
                {
                    int k = offX;
                    for (ITextComponent part : line) {
                        if (!(part instanceof TextComponentString))
                            continue;
                        k += GuiModList.this.fontRenderer.getStringWidth(((TextComponentString)part).getText());
                        if (k >= mouseX)
                        {
                            return part;
                        }
                    }
                }
                return null;
            }

            @Override
            public boolean mouseClicked(final double mouseX, final double mouseY, final int buttonmask) {
                final ITextComponent component = findTextLine((int) mouseX, (int) mouseY, InfoPanel.this.left, InfoPanel.this.top);
                if (component != null) {
                    GuiModList.this.handleComponentClick(component);
                    return true;
                }
                return false;
            }
        }
    }
    @Override
    public void initGui()
    {
        for (ModInfo mod : mods)
        {
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getDisplayName()) + 10);
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(MavenVersionStringHelper.artifactVersionToString(mod.getVersion())) + 5);
        }
        listWidth = Math.min(listWidth, 150);
        listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;
        this.modList = new GuiSlotModList(this, listWidth);
        this.modList.setSlotXBoundsFromLeft(6);
        this.modInfo = new InfoPanel(this.mc, this.width - this.listWidth - 20, this.height, 10, this.height - 30, this.height - 46);
        this.modInfo.setSlotXBoundsFromLeft(this.listWidth + 14);

        this.addButton(new GuiButtonClickConsumer(6, ((modList.right + this.width) / 2) - 100, this.height - 24,
                I18n.format("gui.done"), (x, y) -> GuiModList.this.mc.displayGuiScreen(GuiModList.this.mainMenu)));
        this.addButton(this.configButton = new GuiButtonClickConsumer(20, 6, this.height - 24, this.listWidth, 20,
                I18n.format("fml.menu.mods.config"), (x,y)-> GuiModList.this.displayModConfig()));

        search = new GuiTextField(0, getFontRenderer(), 8, modList.bottom + 17, listWidth - 4, 14);
        children.add(search);
        children.add(modList);
        children.add(modInfo);
        search.setFocused(true);
        search.setCanLoseFocus(true);

        final int width = listWidth / numButtons;
        int x = 6, y = 10;
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
    public void tick()
    {
        search.tick();

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
        for (GuiButton b : buttons)
        {
            if (b instanceof SortButton) {
                b.enabled = sortType.button != b;
            }
        }
        sorted = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.modList.drawScreen(mouseX, mouseY, partialTicks);
        this.modInfo.drawScreen(mouseX, mouseY, partialTicks);

        int left = ((this.width - this.listWidth - 38) / 2) + this.listWidth + 30;

        String text = I18n.format("fml.menu.mods.search");
        int x = ((10 + modList.right) / 2) - (getFontRenderer().getStringWidth(text) / 2);
        getFontRenderer().drawString(text, x, modList.bottom + 5, 0xFFFFFF);
        this.search.drawTextField(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
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

        Pair<ResourceLocation, Size2i> logoData = selectedMod.getLogoFile().map(logoFile->
        {
            TextureManager tm = mc.getTextureManager();
            final ModFileResourcePack resourcePack = ResourcePackLoader.getResourcePackFor(selectedMod.getModId())
                    .orElse(ResourcePackLoader.getResourcePackFor("forge").
                            orElseThrow(()->new RuntimeException("Can't find forge, WHAT!")));
            try
            {
                NativeImage logo = null;
                InputStream logoResource = resourcePack.getRootResourceStream(logoFile);
                if (logoResource != null)
                    logo = NativeImage.read(logoResource);
                if (logo != null)
                {
                    return Pair.of(tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo)), new Size2i(logo.getWidth(), logo.getHeight()));
                }
            }
            catch (IOException e) { }
            return Pair.<ResourceLocation, Size2i>of(null, new Size2i(0, 0));
        }).orElse(Pair.of(null, new Size2i(0, 0)));

        lines.add(selectedMod.getDisplayName());
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.version", MavenVersionStringHelper.artifactVersionToString(selectedMod.getVersion())));
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.idstate", selectedMod.getModId(), ModList.get().getModContainerById(selectedMod.getModId()).
                map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE")));

        selectedMod.getModConfig().getOptional("credits").ifPresent(credits->
                lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.credits", credits)));
        selectedMod.getModConfig().getOptional("authors").ifPresent(authors ->
                lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.authors", authors)));
        selectedMod.getModConfig().getOptional("displayURL").ifPresent(displayURL ->
                lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.displayurl", displayURL)));
        if (selectedMod.getOwningFile() == null || selectedMod.getOwningFile().getMods().size()==1)
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.nochildmods"));
        else
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.childmods", selectedMod.getOwningFile().getMods().stream().map(IModInfo::getDisplayName).collect(Collectors.joining(","))));

        if (vercheck.status == VersionChecker.Status.OUTDATED || vercheck.status == VersionChecker.Status.BETA_OUTDATED)
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.updateavailable", vercheck.url == null ? "" : vercheck.url));

        lines.add(null);
        lines.add(selectedMod.getDescription());

        if ((vercheck.status == VersionChecker.Status.OUTDATED || vercheck.status == VersionChecker.Status.BETA_OUTDATED) && vercheck.changes.size() > 0)
        {
            lines.add(null);
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.changelogheader"));
            for (Entry<ComparableVersion, String> entry : vercheck.changes.entrySet())
            {
                lines.add("  " + entry.getKey() + ":");
                lines.add(entry.getValue());
                lines.add(null);
            }
        }

        modInfo.setInfo(modInfo.new Info(modInfo, lines, logoData.getLeft(), logoData.getRight()));
    }
}
