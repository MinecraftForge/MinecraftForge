/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.client.gui.screen;

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

import com.mojang.blaze3d.matrix.MatrixStack;
import cpw.mods.modlauncher.Environment;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ModListWidget;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;

public class ModListScreen extends Screen
{
    private static String stripControlCodes(String value) { return net.minecraft.util.StringUtils.stripControlCodes(value); }
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

        ITextComponent getButtonText() {
            return new TranslationTextComponent("fml.menu.mods." + StringUtils.toLowerCase(name()));
        }
    }

    private static final int PADDING = 6;

    private Screen parentScreen;

    private ModListWidget modList;
    private InfoPanel modInfo;
    private ModListWidget.ModEntry selected = null;
    private int listWidth;
    private List<ModInfo> mods;
    private final List<ModInfo> unsortedMods;
    private Button configButton, openModsFolderButton;

    private int buttonMargin = 1;
    private int numButtons = SortType.values().length;
    private String lastFilterText = "";

    private TextFieldWidget search;

    private boolean sorted = false;
    private SortType sortType = SortType.NORMAL;

    /**
     * @param parentScreen
     */
    public ModListScreen(Screen parentScreen)
    {
        super(new TranslationTextComponent("fml.menu.mods.title"));
        this.parentScreen = parentScreen;
        this.mods = Collections.unmodifiableList(ModList.get().getMods());
        this.unsortedMods = Collections.unmodifiableList(this.mods);
    }

    class InfoPanel extends ScrollPanel {
        private ResourceLocation logoPath;
        private Size2i logoDims = new Size2i(0, 0);
        private List<IReorderingProcessor> lines = Collections.emptyList();

        InfoPanel(Minecraft mcIn, int widthIn, int heightIn, int topIn)
        {
            super(mcIn, widthIn, heightIn, topIn, modList.getRight() + PADDING);
        }

        void setInfo(List<String> lines, ResourceLocation logoPath, Size2i logoDims)
        {
            this.logoPath = logoPath;
            this.logoDims = logoDims;
            this.lines = resizeContent(lines);
        }

        void clearInfo()
        {
            this.logoPath = null;
            this.logoDims = new Size2i(0, 0);
            this.lines = Collections.emptyList();
        }

        private List<IReorderingProcessor> resizeContent(List<String> lines)
        {
            List<IReorderingProcessor> ret = new ArrayList<>();
            for (String line : lines)
            {
                if (line == null)
                {
                    ret.add(null);
                    continue;
                }

                ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
                int maxTextLength = this.width - 12;
                if (maxTextLength >= 0)
                {
                    ret.addAll(LanguageMap.getInstance().func_244260_a(font.getCharacterManager().func_238362_b_(chat, maxTextLength, Style.EMPTY)));
                }
            }
            return ret;
        }

        @Override
        public int getContentHeight()
        {
            int height = 50;
            height += (lines.size() * font.FONT_HEIGHT);
            if (height < this.bottom - this.top - 8)
                height = this.bottom - this.top - 8;
            return height;
        }

        @Override
        protected int getScrollAmount()
        {
            return font.FONT_HEIGHT * 3;
        }

        @Override
        protected void drawPanel(MatrixStack mStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY)
        {
            if (logoPath != null) {
                Minecraft.getInstance().getTextureManager().bindTexture(logoPath);
                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                // Draw the logo image inscribed in a rectangle with width entryWidth (minus some padding) and height 50
                int headerHeight = 50;
                GuiUtils.drawInscribedRect(mStack, left + PADDING, relativeY, width - (PADDING * 2), headerHeight, logoDims.width, logoDims.height, false, true);
                relativeY += headerHeight + PADDING;
            }

            for (IReorderingProcessor line : lines)
            {
                if (line != null)
                {
                    RenderSystem.enableBlend();
                    ModListScreen.this.font.func_238407_a_(mStack, line, left + PADDING, relativeY, 0xFFFFFF);
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                }
                relativeY += font.FONT_HEIGHT;
            }

            final Style component = findTextLine(mouseX, mouseY);
            if (component!=null) {
                ModListScreen.this.renderComponentHoverEffect(mStack, component, mouseX, mouseY);
            }
        }

        private Style findTextLine(final int mouseX, final int mouseY) {
            double offset = (mouseY - top) + border + scrollDistance + 1;
            if (logoPath != null) {
                offset -= 50;
            }
            if (offset <= 0)
                return null;

            int lineIdx = (int) (offset / font.FONT_HEIGHT);
            if (lineIdx >= lines.size() || lineIdx < 1)
                return null;

            IReorderingProcessor line = lines.get(lineIdx-1);
            if (line != null)
            {
                return font.getCharacterManager().func_243239_a(line, mouseX);
            }
            return null;
        }

        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            final Style component = findTextLine((int) mouseX, (int) mouseY);
            if (component != null) {
                ModListScreen.this.handleComponentClicked(component);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        protected void drawBackground() {
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
        listWidth = Math.max(Math.min(listWidth, width/3), 100);
        listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;

        int modInfoWidth = this.width - this.listWidth - (PADDING * 3);
        int doneButtonWidth = Math.min(modInfoWidth, 200);
        int y = this.height - 20 - PADDING;
        this.addButton(new Button(((listWidth + PADDING + this.width - doneButtonWidth) / 2), y, doneButtonWidth, 20,
                new TranslationTextComponent("gui.done"), b -> ModListScreen.this.closeScreen()));
        this.addButton(this.openModsFolderButton = new Button(6, y, this.listWidth, 20,
                new TranslationTextComponent("fml.menu.mods.openmodsfolder"), b -> Util.getOSType().openFile(FMLPaths.MODSDIR.get().toFile())));
        y -= 20 + PADDING;
        this.addButton(this.configButton = new Button(6, y, this.listWidth, 20,
                new TranslationTextComponent("fml.menu.mods.config"), b -> ModListScreen.this.displayModConfig()));
        this.configButton.active = false;

        y -= 14 + PADDING + 1;
        search = new TextFieldWidget(getFontRenderer(), PADDING + 1, y, listWidth - 2, 14, new TranslationTextComponent("fml.menu.mods.search"));

        int fullButtonHeight = PADDING + 20 + PADDING;
        this.modList = new ModListWidget(this, listWidth, fullButtonHeight, search.y - getFontRenderer().FONT_HEIGHT - PADDING);
        this.modList.setLeftPos(6);

        this.modInfo = new InfoPanel(this.minecraft, modInfoWidth, this.height - PADDING - fullButtonHeight, PADDING);

        children.add(search);
        children.add(modList);
        children.add(modInfo);
        search.setFocused2(false);
        search.setCanLoseFocus(true);

        final int width = listWidth / numButtons;
        int x = PADDING;
        addButton(SortType.NORMAL.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.NORMAL.getButtonText(), b -> resortMods(SortType.NORMAL)));
        x += width + buttonMargin;
        addButton(SortType.A_TO_Z.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.A_TO_Z.getButtonText(), b -> resortMods(SortType.A_TO_Z)));
        x += width + buttonMargin;
        addButton(SortType.Z_TO_A.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.Z_TO_A.getButtonText(), b -> resortMods(SortType.Z_TO_A)));
        resortMods(SortType.NORMAL);
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
        modList.setSelected(selected);

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
            {
                selected = modList.getEventListeners().stream().filter(e -> e.getInfo() == selected.getInfo()).findFirst().orElse(null);
                updateCache();
            }
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

    private void resortMods(SortType newSort)
    {
        this.sortType = newSort;

        for (SortType sort : SortType.values())
        {
            if (sort.button != null)
                sort.button.active = sortType != sort;
        }
        sorted = false;
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks)
    {
        this.modList.render(mStack, mouseX, mouseY, partialTicks);
        if (this.modInfo != null)
            this.modInfo.render(mStack, mouseX, mouseY, partialTicks);

        ITextComponent text = new TranslationTextComponent("fml.menu.mods.search");
        int x = modList.getLeft() + ((modList.getRight() - modList.getLeft()) / 2) - (getFontRenderer().getStringPropertyWidth(text) / 2);
        getFontRenderer().func_238422_b_(mStack, text.func_241878_f(), x, search.y - getFontRenderer().FONT_HEIGHT, 0xFFFFFF);
        this.search.render(mStack, mouseX , mouseY, partialTicks);
        super.render(mStack, mouseX, mouseY, partialTicks);
    }

    public Minecraft getMinecraftInstance()
    {
        return minecraft;
    }

    public FontRenderer getFontRenderer()
    {
        return font;
    }

    public void setSelected(ModListWidget.ModEntry entry)
    {
        this.selected = entry == this.selected ? null : entry;
        updateCache();
    }

    private void updateCache()
    {
        if (selected == null) {
            this.configButton.active = false;
            this.modInfo.clearInfo();
            return;
        }
        ModInfo selectedMod = selected.getInfo();
        this.configButton.active = ConfigGuiHandler.getGuiFactoryFor(selectedMod).isPresent();
        List<String> lines = new ArrayList<>();
        VersionChecker.CheckResult vercheck = VersionChecker.getResult(selectedMod);

        @SuppressWarnings("resource")
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

                    return Pair.of(tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo) {

                        @Override
                        public void updateDynamicTexture() {
                            this.bindTexture();
                            NativeImage td = this.getTextureData();
                            // Use custom "blur" value which controls texture filtering (nearest-neighbor vs linear)
                            this.getTextureData().uploadTextureSub(0, 0, 0, 0, 0, td.getWidth(), td.getHeight(), selectedMod.getLogoBlur(), false, false, false);
                        }
                    }), new Size2i(logo.getWidth(), logo.getHeight()));
                }
            }
            catch (IOException e) { }
            return Pair.<ResourceLocation, Size2i>of(null, new Size2i(0, 0));
        }).orElse(Pair.of(null, new Size2i(0, 0)));

        lines.add(selectedMod.getDisplayName());
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.version", MavenVersionStringHelper.artifactVersionToString(selectedMod.getVersion())));
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.idstate", selectedMod.getModId(), ModList.get().getModContainerById(selectedMod.getModId()).
                map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE")));

        selectedMod.getConfigElement("credits").ifPresent(credits->
                lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.credits", credits)));
        selectedMod.getConfigElement("authors").ifPresent(authors ->
                lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.authors", authors)));
        selectedMod.getConfigElement("displayURL").ifPresent(displayURL ->
                lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.displayurl", displayURL)));
        if (selectedMod.getOwningFile() == null || selectedMod.getOwningFile().getMods().size()==1)
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.nochildmods"));
        else
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.childmods", selectedMod.getOwningFile().getMods().stream().map(IModInfo::getDisplayName).collect(Collectors.joining(","))));

        if (vercheck.status == VersionChecker.Status.OUTDATED || vercheck.status == VersionChecker.Status.BETA_OUTDATED)
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.updateavailable", vercheck.url == null ? "" : vercheck.url));
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.license", selectedMod.getOwningFile().getLicense()));
        lines.add(null);
        lines.add(selectedMod.getDescription());
        lines.add(null);
        if (FMLEnvironment.secureJarsEnabled) {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.signature", selectedMod.getOwningFile().getCodeSigningFingerprint().orElse(ForgeI18n.parseMessage("fml.menu.mods.info.signature.unsigned"))));
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.trust", selectedMod.getOwningFile().getTrustData().orElse(ForgeI18n.parseMessage("fml.menu.mods.info.trust.noauthority"))));
        } else {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.securejardisabled"));
        }

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

        modInfo.setInfo(lines, logoData.getLeft(), logoData.getRight());
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        String s = this.search.getText();
        SortType sort = this.sortType;
        ModListWidget.ModEntry selected = this.selected;
        this.init(mc, width, height);
        this.search.setText(s);
        this.selected = selected;
        if (!this.search.getText().isEmpty())
            reloadMods();
        if (sort != SortType.NORMAL)
            resortMods(sort);
        updateCache();
    }

    @Override
    public void closeScreen()
    {
        this.minecraft.displayGuiScreen(this.parentScreen);
    }
}
