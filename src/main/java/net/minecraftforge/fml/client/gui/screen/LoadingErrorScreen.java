/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ErrorScreen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoadingErrorScreen extends ErrorScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modsDir;
    private final Path logFile;
    private final List<ModLoadingException> modLoadErrors;
    private final List<ModLoadingWarning> modLoadWarnings;
    private final Path dumpedLocation;
    private LoadingEntryList entryList;
    private ITextComponent errorHeader;
    private ITextComponent warningHeader;

    public LoadingErrorScreen(LoadingFailedException loadingException, List<ModLoadingWarning> warnings, final File dumpedLocation)
    {
        super(new StringTextComponent("Loading Error"), null);
        this.modLoadWarnings = warnings;
        this.modLoadErrors = loadingException == null ? Collections.emptyList() : loadingException.getErrors();
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs","latest.log"));
        this.dumpedLocation = dumpedLocation != null ? dumpedLocation.toPath() : null;
    }

    @Override
    public void init()
    {
        super.init();
        this.buttons.clear();
        this.children.clear();

        this.errorHeader = new StringTextComponent(TextFormatting.RED + ForgeI18n.parseMessage("fml.loadingerrorscreen.errorheader", this.modLoadErrors.size()) + TextFormatting.RESET);
        this.warningHeader = new StringTextComponent(TextFormatting.YELLOW + ForgeI18n.parseMessage("fml.loadingerrorscreen.warningheader", this.modLoadErrors.size()) + TextFormatting.RESET);

        int yOffset = 46;
        this.addButton(new ExtendedButton(50, this.height - yOffset, this.width / 2 - 55, 20, new StringTextComponent(ForgeI18n.parseMessage("fml.button.open.mods.folder")), b -> Util.getOSType().openFile(modsDir.toFile())));
        this.addButton(new ExtendedButton(this.width / 2 + 5, this.height - yOffset, this.width / 2 - 55, 20, new StringTextComponent(ForgeI18n.parseMessage("fml.button.open.file", logFile.getFileName())), b -> Util.getOSType().openFile(logFile.toFile())));
        if (this.modLoadErrors.isEmpty()) {
            this.addButton(new ExtendedButton(this.width / 4, this.height - 24, this.width / 2, 20, new StringTextComponent(ForgeI18n.parseMessage("fml.button.continue.launch")), b -> {
                ClientHooks.logMissingTextureErrors();
                this.minecraft.displayGuiScreen(null);
            }));
        } else {
            this.addButton(new ExtendedButton(this.width / 4, this.height - 24, this.width / 2, 20, new StringTextComponent(ForgeI18n.parseMessage("fml.button.open.file", dumpedLocation.getFileName())), b -> Util.getOSType().openFile(dumpedLocation.toFile())));
        }

        this.entryList = new LoadingEntryList(this, this.modLoadErrors, this.modLoadWarnings);
        this.children.add(this.entryList);
        this.setListener(this.entryList);
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(mStack);
        this.entryList.render(mStack, mouseX, mouseY, partialTicks);
        drawMultiLineCenteredString(mStack, font, this.modLoadErrors.isEmpty() ? warningHeader : errorHeader, this.width / 2, 10);
        this.buttons.forEach(button -> button.render(mStack, mouseX, mouseY, partialTicks));
    }

    private void drawMultiLineCenteredString(MatrixStack mStack, FontRenderer fr, ITextComponent str, int x, int y) {
        for (IReorderingProcessor s : fr.trimStringToWidth(str, this.width)) {
            fr.func_238407_a_(mStack, s, (float) (x - fr.func_243245_a(s) / 2.0), y, 0xFFFFFF);
            y+=fr.FONT_HEIGHT;
        }
    }
    public static class LoadingEntryList extends ExtendedList<LoadingEntryList.LoadingMessageEntry> {
        LoadingEntryList(final LoadingErrorScreen parent, final List<ModLoadingException> errors, final List<ModLoadingWarning> warnings) {
            super(parent.minecraft, parent.width, parent.height, 35, parent.height - 50, 2 * parent.minecraft.fontRenderer.FONT_HEIGHT + 8);
            boolean both = !errors.isEmpty() && !warnings.isEmpty();
            if (both)
                addEntry(new LoadingMessageEntry(parent.errorHeader, true));
            errors.forEach(e->addEntry(new LoadingMessageEntry(new StringTextComponent(e.formatToString()))));
            if (both) {
                int maxChars = (this.width - 10) / parent.minecraft.fontRenderer.getStringWidth("-");
                addEntry(new LoadingMessageEntry(new StringTextComponent("\n" + Strings.repeat("-", maxChars) + "\n")));
                addEntry(new LoadingMessageEntry(parent.warningHeader, true));
            }
            warnings.forEach(w->addEntry(new LoadingMessageEntry(new StringTextComponent(w.formatToString()))));
        }

        @Override
        protected int getScrollbarPosition()
        {
            return this.getRight() - 6;
        }

        @Override
        public int getRowWidth()
        {
            return this.width;
        }

        public class LoadingMessageEntry extends ExtendedList.AbstractListEntry<LoadingMessageEntry> {
            private final ITextComponent message;
            private final boolean center;

            LoadingMessageEntry(final ITextComponent message) {
                this(message, false);
            }

            LoadingMessageEntry(final ITextComponent message, final boolean center) {
                this.message = Objects.requireNonNull(message);
                this.center = center;
            }

            @Override
            public void render(MatrixStack mStack, int entryIdx, int top, int left, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean p_194999_5_, final float partialTicks) {
                FontRenderer font = Minecraft.getInstance().fontRenderer;
                final List<IReorderingProcessor> strings = font.trimStringToWidth(message, LoadingEntryList.this.width);
                int y = top + 2;
                for (int i = 0; i < Math.min(strings.size(), 2); i++) {
                    if (center)
                        font.func_238422_b_(mStack, strings.get(i), left + (width) - font.func_243245_a(strings.get(i)) / 2F, y, 0xFFFFFF);
                    else
                        font.func_238422_b_(mStack, strings.get(i), left + 5, y, 0xFFFFFF);
                    y += font.FONT_HEIGHT;
                }
            }
        }

    }
}
