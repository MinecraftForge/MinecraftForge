/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.client.gui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.ConnectionData.ModMismatchData;
import net.minecraftforge.network.NetworkRegistry;

public class ModMismatchDisconnectedScreen extends Screen
{
    private final Component reason;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Screen parent;
    private int textHeight;
    private int listHeight;
    private final ModMismatchData modMismatchData;
    private final Path logFile;
    private Map<ResourceLocation, String> presentChannels;
    private Map<ResourceLocation, String> missingChannels;
    private Map<ResourceLocation, Pair<String, String>> mismatchedChannels;
    private Map<String, String> missingRegistryMods;
    private boolean mismatchedDataFromServer;
    private String mismatchedDataOrigin;

    public ModMismatchDisconnectedScreen(Screen parentScreen, Component title, Component reason, ModMismatchData modMismatchData)
    {
        super(title);
        this.parent = parentScreen;
        this.reason = reason;
        this.modMismatchData = modMismatchData;
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs","latest.log"));
    }

    @Override
    protected void init()
    {
        this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
        this.textHeight = this.message.getLineCount() * 9;
        this.listHeight = 140;

        this.mismatchedDataFromServer = modMismatchData.mismatchedDataFromServer();
        this.mismatchedDataOrigin = modMismatchData.mismatchedDataFromServer() ? "server" : "client";
        this.presentChannels = modMismatchData.presentChannelData();
        this.missingChannels = modMismatchData.mismatchedChannelData().entrySet().stream().filter(e -> e.getValue().getRight().equals(NetworkRegistry.ABSENT)).map(e -> Pair.of(e.getKey(), e.getValue().getLeft())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        this.mismatchedChannels = modMismatchData.mismatchedChannelData().entrySet().stream().filter(e -> !e.getValue().getRight().equals(NetworkRegistry.ABSENT)).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        this.missingRegistryMods = modMismatchData.missingRegistryModData();

        if (modMismatchData.containsMismatches())
            this.addRenderableWidget(new MismatchInfoPanel(minecraft, Math.min(420, this.width - 16), listHeight, this.height / 2 - this.listHeight / 2, Math.max(8, this.width / 2 - 210)));

        int buttonWidth = Math.min(200, this.width / 2 - 20);
        this.addRenderableWidget(new Button(this.width / 4 - buttonWidth / 2, Math.min(this.height / 2 + this.listHeight / 2 + this.textHeight / 2 + 18, this.height - 30), buttonWidth, 20, new TextComponent(ForgeI18n.parseMessage("fml.button.open.file", logFile.getFileName())), button -> Util.getPlatform().openFile(logFile.toFile())));
        this.addRenderableWidget(new Button(this.width * 3 / 4 - buttonWidth / 2, Math.min(this.height / 2 + this.listHeight / 2 + this.textHeight / 2 + 18, this.height - 30), buttonWidth, 20, new TranslatableComponent("gui.toMenu"), button -> this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title, this.width / 2, this.height / 2 - this.listHeight / 2 - this.textHeight / 2 - 9 * 4, 0xAAAAAA);
        this.message.renderCentered(stack, this.width / 2, this.height / 2 - this.listHeight / 2 - this.textHeight / 2 - 9 * 2);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    class MismatchInfoPanel extends ScrollPanel
    {
        private final List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> lineTable;
        private final int contentSize;
        private final int nameIndent = 10;
        private final int tableWidth = width - border * 2 - 6 - nameIndent;
        private final int nameWidth = tableWidth * 3 / 5;
        private final int versionWidth = (tableWidth - nameWidth) / 2;

        public MismatchInfoPanel(Minecraft client, int width, int height, int top, int left)
        {
            super(client, width, height, top, left);

            Map<MutableComponent, Pair<String, String>> rawTable = new LinkedHashMap<>();
            rawTable.put(new TextComponent("Mod name"), Pair.of("Client version", "Server version"));
            rawTable.put(new TextComponent(" "), null); //padding
            if (!missingChannels.isEmpty())
            {
                rawTable.put(new TextComponent("The " + mismatchedDataOrigin + " is missing the following mods, consider " + (mismatchedDataFromServer ? "removing" : "downloading") + " them to play on this server:"), null);
                int i = 0;
                for (Map.Entry<ResourceLocation, String> channel : missingChannels.entrySet()) {
                    rawTable.put(new TextComponent(channel.getValue()).withStyle(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent(channel.getKey().toString())))),
                            Pair.of(mismatchedDataFromServer ? presentChannels.getOrDefault(channel.getKey(), "") : "", mismatchedDataFromServer ? "" : presentChannels.getOrDefault(channel.getKey(), "")));
                    if (++i > 10) {
                        rawTable.put(new TextComponent("[" + (missingChannels.size() - i) + " additional, see latest.log for the full list]"), Pair.of("", ""));
                        break;
                    }
                }
            }
            if (!mismatchedChannels.isEmpty())
            {
                rawTable.put(new TextComponent("The following mod versions do not match, consider downloading a matching version of these mods:").withStyle(ChatFormatting.GRAY), null);
                int i = 0;
                for (Map.Entry<ResourceLocation, Pair<String, String>> channel : mismatchedChannels.entrySet()) {
                    rawTable.put(new TextComponent(channel.getValue().getLeft()).withStyle(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent(channel.getKey().toString())))),
                            Pair.of(presentChannels.getOrDefault(channel.getKey(), ""), channel.getValue().getRight()));
                    if (++i > 10) {
                        rawTable.put(new TextComponent("[" + (mismatchedChannels.size() - i) + " additional, see latest.log for the full list]"), Pair.of("", ""));
                        break;
                    }
                }
            }
            if (!missingRegistryMods.isEmpty())
            {
                rawTable.put(new TextComponent("The following mods are either not installed, or the installed mod version does not match with the server:").withStyle(ChatFormatting.GRAY), null);
                int i = 0;
                for (Map.Entry<String, String> mod : missingRegistryMods.entrySet()) {
                    rawTable.put(new TextComponent(mod.getValue()).withStyle(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent(mod.getKey())))), Pair.of("", ""));
                    if (++i > 10) {
                        rawTable.put(new TextComponent("[" + (missingRegistryMods.size() - i) + " additional, see latest.log for the full list]"), Pair.of("", ""));
                        break;
                    }
                }
            }

            this.lineTable = rawTable.entrySet().stream().flatMap(e -> getFormattedLines(e.getKey(), e.getValue()).stream()).collect(Collectors.toList());
            this.contentSize = lineTable.size();
        }

        private List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> getFormattedLines(MutableComponent name, Pair<String, String> versions)
        {
            List<FormattedCharSequence> nameLines = font.split(name, versions == null ? width - border * 2 - 6 : nameWidth);
            List<FormattedCharSequence> clientVersionLines = font.split(new TextComponent(versions != null ? versions.getLeft() : ""), versionWidth);
            List<FormattedCharSequence> serverVersionLines = font.split(new TextComponent(versions != null ? versions.getRight() : ""), versionWidth);
            List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLines = new ArrayList<>();
            int lineCount = Math.max(nameLines.size(), Math.max(clientVersionLines.size(), serverVersionLines.size()));
            for (int i = 0; i < lineCount; i++) {
                splitLines.add(Pair.of(i < nameLines.size() ? nameLines.get(i) : FormattedCharSequence.EMPTY, versions == null ? null : Pair.of(i < clientVersionLines.size() ? clientVersionLines.get(i) : FormattedCharSequence.EMPTY, i < serverVersionLines.size() ? serverVersionLines.get(i) : FormattedCharSequence.EMPTY)));
            }
            return splitLines;
        }

        @Override
        protected int getContentHeight()
        {
            int height = contentSize * (font.lineHeight + 3);

            if (height < bottom - top - 4)
                height = bottom - top - 4;

            return height;
        }

        @Override
        protected void drawPanel(PoseStack stack, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY)
        {
            int i = 0;

            for (Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>> line : lineTable) {
                FormattedCharSequence name = line.getLeft();
                Pair<FormattedCharSequence, FormattedCharSequence> versions = line.getRight();
                font.draw(stack, name, left + border + (versions == null ? 0 : nameIndent), relativeY + i * 12, versions == null ? 0xAAAAAA : 0xFFFFFF);
                if (versions != null)
                {
                    font.draw(stack, versions.getLeft(), left + border + nameIndent + nameWidth, relativeY + i * 12, 0xFFFFFF);
                    font.draw(stack, versions.getRight(), left + border + nameIndent + nameWidth + versionWidth, relativeY + i * 12, 0xFFFFFF);
                }

                i++;
            }
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
        {
            super.render(stack, mouseX, mouseY, partialTicks);
            Style style = getComponentStyleAt(mouseX, mouseY);
            if (style != null && style.getHoverEvent() != null)
            {
                ModMismatchDisconnectedScreen.this.renderComponentHoverEffect(stack, style, mouseX, mouseY);
            }
        }

        public Style getComponentStyleAt(double x, double y)
        {
            if (this.isMouseOver(x, y))
            {
                double relativeY = y - this.top + this.scrollDistance - border;
                int slotIndex = (int)(relativeY + (border / 2)) / 12;
                if (slotIndex < contentSize)
                {
                    double relativeX = x - left - border - (lineTable.get(slotIndex).getRight() == null ? 0 : nameIndent);
                    if (relativeX >= 0)
                        return font.getSplitter().componentStyleAtWidth(lineTable.get(slotIndex).getLeft(), (int)relativeX);
                }
            }

            return null;
        }

        @Override
        public NarrationPriority narrationPriority()
        {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
        }
    }
}
