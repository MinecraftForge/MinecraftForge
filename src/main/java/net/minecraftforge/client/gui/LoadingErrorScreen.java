/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.client.gui.widget.ExtendedButton;
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
    private Component errorHeader;
    private Component warningHeader;

    public LoadingErrorScreen(LoadingFailedException loadingException, List<ModLoadingWarning> warnings, final File dumpedLocation)
    {
        super(new TextComponent("Loading Error"), null);
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
        this.clearWidgets();

        this.errorHeader = new TextComponent(ChatFormatting.RED + ForgeI18n.parseMessage("fml.loadingerrorscreen.errorheader", this.modLoadErrors.size()) + ChatFormatting.RESET);
        this.warningHeader = new TextComponent(ChatFormatting.YELLOW + ForgeI18n.parseMessage("fml.loadingerrorscreen.warningheader", this.modLoadErrors.size()) + ChatFormatting.RESET);

        int yOffset = 46;
        this.addRenderableWidget(new ExtendedButton(50, this.height - yOffset, this.width / 2 - 55, 20, new TextComponent(ForgeI18n.parseMessage("fml.button.open.mods.folder")), b -> Util.getPlatform().openFile(modsDir.toFile())));
        this.addRenderableWidget(new ExtendedButton(this.width / 2 + 5, this.height - yOffset, this.width / 2 - 55, 20, new TextComponent(ForgeI18n.parseMessage("fml.button.open.file", logFile.getFileName())), b -> Util.getPlatform().openFile(logFile.toFile())));
        if (this.modLoadErrors.isEmpty()) {
            this.addRenderableWidget(new ExtendedButton(this.width / 4, this.height - 24, this.width / 2, 20, new TextComponent(ForgeI18n.parseMessage("fml.button.continue.launch")), b -> {
                this.minecraft.setScreen(null);
            }));
        } else {
            this.addRenderableWidget(new ExtendedButton(this.width / 4, this.height - 24, this.width / 2, 20, new TextComponent(ForgeI18n.parseMessage("fml.button.open.file", dumpedLocation.getFileName())), b -> Util.getPlatform().openFile(dumpedLocation.toFile())));
        }

        this.entryList = new LoadingEntryList(this, this.modLoadErrors, this.modLoadWarnings);
        this.addWidget(this.entryList);
        this.setFocused(this.entryList);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(poseStack);
        this.entryList.render(poseStack, mouseX, mouseY, partialTick);
        drawMultiLineCenteredString(poseStack, font, this.modLoadErrors.isEmpty() ? warningHeader : errorHeader, this.width / 2, 10);
        this.renderables.forEach(button -> button.render(poseStack, mouseX, mouseY, partialTick));
    }

    private void drawMultiLineCenteredString(PoseStack poseStack, Font fr, Component str, int x, int y) {
        for (FormattedCharSequence s : fr.split(str, this.width)) {
            fr.drawShadow(poseStack, s, (float) (x - fr.width(s) / 2.0), y, 0xFFFFFF);
            y+=fr.lineHeight;
        }
    }
    public static class LoadingEntryList extends ObjectSelectionList<LoadingEntryList.LoadingMessageEntry> {
        LoadingEntryList(final LoadingErrorScreen parent, final List<ModLoadingException> errors, final List<ModLoadingWarning> warnings) {
            super(parent.minecraft, parent.width, parent.height, 35, parent.height - 50, 2 * parent.minecraft.font.lineHeight + 8);
            boolean both = !errors.isEmpty() && !warnings.isEmpty();
            if (both)
                addEntry(new LoadingMessageEntry(parent.errorHeader, true));
            errors.forEach(e->addEntry(new LoadingMessageEntry(new TextComponent(e.formatToString()))));
            if (both) {
                int maxChars = (this.width - 10) / parent.minecraft.font.width("-");
                addEntry(new LoadingMessageEntry(new TextComponent("\n" + Strings.repeat("-", maxChars) + "\n")));
                addEntry(new LoadingMessageEntry(parent.warningHeader, true));
            }
            warnings.forEach(w->addEntry(new LoadingMessageEntry(new TextComponent(w.formatToString()))));
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

        public class LoadingMessageEntry extends ObjectSelectionList.Entry<LoadingMessageEntry> {
            private final Component message;
            private final boolean center;

            LoadingMessageEntry(final Component message) {
                this(message, false);
            }

            LoadingMessageEntry(final Component message, final boolean center) {
                this.message = Objects.requireNonNull(message);
                this.center = center;
            }

            @Override
            public Component getNarration() {
                return new TranslatableComponent("narrator.select", message);
            }

            @Override
            public void render(PoseStack poseStack, int entryIdx, int top, int left, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean p_194999_5_, final float partialTick) {
                Font font = Minecraft.getInstance().font;
                final List<FormattedCharSequence> strings = font.split(message, LoadingEntryList.this.width);
                int y = top + 2;
                for (int i = 0; i < Math.min(strings.size(), 2); i++) {
                    if (center)
                        font.draw(poseStack, strings.get(i), left + (width) - font.width(strings.get(i)) / 2F, y, 0xFFFFFF);
                    else
                        font.draw(poseStack, strings.get(i), left + 5, y, 0xFFFFFF);
                    y += font.lineHeight;
                }
            }
        }

    }
}
