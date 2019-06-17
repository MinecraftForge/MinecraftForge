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

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

public class GuiModList extends Screen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private enum SortType implements Comparator<ModInfo>
    {
        NORMAL,
        A_TO_Z{ @Override protected int compare(String name1, String name2){ return name1.compareTo(name2); }},
        Z_TO_A{ @Override protected int compare(String name1, String name2){ return name2.compareTo(name1); }};

        Button button;
        protected int compare(String name1, String name2){ return 0; }
        @Override
        public int compare(ModInfo o1, ModInfo o2) {
            String name1 = StringUtils.toLowerCase(stripControlCodes(o1.getDisplayName()));
            String name2 = StringUtils.toLowerCase(stripControlCodes(o2.getDisplayName()));
            return compare(name1, name2);
        }

        String getButtonText() {
            return I18n.format("fml.menu.mods."+StringUtils.toLowerCase(name()));
        }
    }

    private Screen mainMenu;

    private GuiSlotModList modList;
    private InfoPanel modInfo;
    private GuiSlotModList.ModEntry selected = null;
    private int listWidth;
    private List<ModInfo> mods;
    private final List<ModInfo> unsortedMods;
    private Button configButton;

    private int buttonMargin = 1;
    private int numButtons = SortType.values().length;
    private String lastFilterText = "";

    private TextFieldWidget search;

    private boolean sorted = false;
    private SortType sortType = SortType.NORMAL;

    /**
     * @param mainMenu
     */
    public GuiModList(Screen mainMenu)
    {
        super(new TranslationTextComponent("fml.menu.mods.title"));
        this.mainMenu = mainMenu;
        this.mods = Collections.unmodifiableList(ModList.get().getMods());
        this.unsortedMods = Collections.unmodifiableList(this.mods);
    }

    class InfoPanel extends ExtendedList<InfoPanel.Info> {
        InfoPanel(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
        {
            super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        }

        @Override
        protected int getScrollbarPosition()
        {
            return this.getRight() - 6;
        }

        @Override
        public int getRowWidth() {
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
        class Info extends ExtendedList.AbstractListEntry<Info>
        {
            private ResourceLocation logoPath;
            private Size2i logoDims;
            private List<ITextComponent> lines;

            public Info(ExtendedList<Info> parent, List<String> lines, @Nullable ResourceLocation logoPath, Size2i logoDims)
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
                        ret.addAll(RenderComponentsUtil.splitText(chat, maxTextLength, GuiModList.this.font, false, true));
                    }
                }
                return ret;
            }

            @Override
            public void render(int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks)
            {
                if (logoPath != null) {
                    Minecraft.getInstance().getTextureManager().bindTexture(logoPath);
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
                        GuiModList.this.font.drawStringWithShadow(line.getFormattedText(), left + 4, top, 0xFFFFFF);
                        GlStateManager.disableAlphaTest();
                        GlStateManager.disableBlend();
                    }
                    top += font.FONT_HEIGHT;
                }

                final ITextComponent component = findTextLine(mouseX, mouseY, 0, 0);
                if (component!=null) {
                    GuiModList.this.renderComponentHoverEffect(component, mouseX, mouseY);
                }
            }

            private ITextComponent findTextLine(final int mouseX, final int mouseY, final int offX, final int offY) {
                int offset = mouseY - offY;
                if (logoPath != null) {
                    offset -= logoDims.height + 10;
                }
                if (offset <= 0)
                    return null;

                int lineIdx = offset / font.FONT_HEIGHT;
                if (lineIdx >= lines.size() || lineIdx < 1)
                    return null;

                ITextComponent line = lines.get(lineIdx-1);
                if (line != null)
                {
                    int k = offX;
                    for (ITextComponent part : line) {
                        if (!(part instanceof StringTextComponent))
                            continue;
                        k += GuiModList.this.font.getStringWidth(((StringTextComponent)part).getText());
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
                final ITextComponent component = findTextLine((int) mouseX, (int) mouseY, InfoPanel.this.getLeft(), InfoPanel.this.getTop());
                if (component != null) {
                    GuiModList.this.handleComponentClicked(component);
                    return true;
                }
                return false;
            }
        }
    }
    @Override
    public void init()
    {
        for (ModInfo mod : mods)
        {
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(mod.getDisplayName()) + 10);
            listWidth = Math.max(listWidth,getFontRenderer().getStringWidth(MavenVersionStringHelper.artifactVersionToString(mod.getVersion())) + 5);
        }
        listWidth = Math.min(listWidth, 150);
        listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;
        this.modList = new GuiSlotModList(this, listWidth);
        this.modList.setLeftPos(6);
        this.modInfo = new InfoPanel(this.minecraft, this.width - this.listWidth - 20, this.height, 10, this.height - 30, this.height - 46);
        this.modInfo.setLeftPos(this.listWidth + 14);

        this.addButton(new Button(((modList.getRight() + this.width) / 2) - 100, this.height - 24, 200, 20,
                I18n.format("gui.done"), b -> GuiModList.this.minecraft.displayGuiScreen(GuiModList.this.mainMenu)));
        this.addButton(this.configButton = new Button(6, this.height - 24, this.listWidth, 20,
                I18n.format("fml.menu.mods.config"), b -> GuiModList.this.displayModConfig()));

        search = new TextFieldWidget(getFontRenderer(), 8, modList.getBottom() + 17, listWidth - 4, 14, I18n.format("fml.menu.mods.search"));
        children.add(search);
        children.add(modList);
        children.add(modInfo);
        search.setFocused2(true);
        search.setCanLoseFocus(true);

        final int width = listWidth / numButtons;
        int x = 6, y = 10;
        addButton(SortType.NORMAL.button = new Button(x, y, width - buttonMargin, 20, SortType.NORMAL.getButtonText(), b -> this.sortType = SortType.NORMAL));
        x += width + buttonMargin;
        addButton(SortType.A_TO_Z.button = new Button(x, y, width - buttonMargin, 20, SortType.A_TO_Z.getButtonText(), b -> this.sortType = SortType.A_TO_Z));
        x += width + buttonMargin;
        addButton(SortType.Z_TO_A.button = new Button(x, y, width - buttonMargin, 20, SortType.Z_TO_A.getButtonText(), b -> this.sortType = SortType.Z_TO_A));
        resortMods();
        updateCache();
    }

    private void displayModConfig()
    {
        if (selected == null) return;
        try
        {
            ConfigGuiHandler.getGuiFactoryFor(selected.getInfo()).map(f->f.apply(this.minecraft, this)).ifPresent(newScreen -> this.minecraft.displayGuiScreen(newScreen));
        }
        catch (final Exception e)
        {
            LOGGER.error("There was a critical issue trying to build the config GUI for {}", selected.getInfo().getModId(), e);
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
            modList.refreshList();
            if (selected != null)
                selected = modList.children().stream().filter(e -> e.getInfo() == selected.getInfo()).findFirst().orElse(null);
            sorted = true;
        }
    }

    public <T extends ExtendedList.AbstractListEntry<T>> void buildModList(Consumer<T> modListViewConsumer, Function<ModInfo, T> newEntry)
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
        for (SortType sort : SortType.values())
        {
            if (sort.button != null)
                sort.button.active = sortType != sort;
        }
        sorted = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.modList.render(mouseX, mouseY, partialTicks);
        this.modInfo.render(mouseX, mouseY, partialTicks);

        int left = ((this.width - this.listWidth - 38) / 2) + this.listWidth + 30;

        String text = I18n.format("fml.menu.mods.search");
        int x = ((10 + modList.getRight()) / 2) - (getFontRenderer().getStringWidth(text) / 2);
        getFontRenderer().drawString(text, x, modList.getBottom() + 5, 0xFFFFFF);
        this.search.render(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }

    Minecraft getMinecraftInstance()
    {
        return minecraft;
    }

    FontRenderer getFontRenderer()
    {
        return font;
    }

    public void setSelected(GuiSlotModList.ModEntry entry)
    {
        this.selected = entry == this.selected ? null : entry;
        updateCache();
    }

    private void updateCache()
    {
        if (selected == null) {
            modInfo.clear();
            return;
        }
        ModInfo selectedMod = selected.getInfo();

        this.configButton.active = selectedMod.hasConfigUI();
        List<String> lines = new ArrayList<>();
        VersionChecker.CheckResult vercheck = VersionChecker.getResult(selectedMod);

        Pair<ResourceLocation, Size2i> logoData = selectedMod.getLogoFile().map(logoFile->
        {
            TextureManager tm = this.minecraft.getTextureManager();
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
