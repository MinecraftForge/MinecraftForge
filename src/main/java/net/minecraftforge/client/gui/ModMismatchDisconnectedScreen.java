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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
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
    private final Path modsDir;
    private final Path logFile;
    private Map<ResourceLocation, Pair<String, String>> presentChannelData;
    private List<ResourceLocation> missingChannelData;
    private Map<ResourceLocation, String> mismatchedChannelData;
    private Map<String, String> missingRegistryMods;
    private List<String> modIds;
    private Map<String, String> modUrls;
    private boolean mismatchedDataFromServer;
    private MismatchInfoPanel mismatchInfoPanel;

    public ModMismatchDisconnectedScreen(Screen parentScreen, Component title, Component reason, ModMismatchData modMismatchData)
    {
        super(title);
        this.parent = parentScreen;
        this.reason = reason;
        this.modMismatchData = modMismatchData;
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs","latest.log"));
    }

    @Override
    protected void init()
    {
        this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
        this.textHeight = this.message.getLineCount() * 9;
        this.listHeight = 140;

        this.mismatchedDataFromServer = modMismatchData.mismatchedDataFromServer();
        this.presentChannelData = modMismatchData.presentChannelData();
        this.missingChannelData = modMismatchData.mismatchedChannelData().entrySet().stream().filter(e -> e.getValue().equals(NetworkRegistry.ABSENT)).map(Entry::getKey).collect(Collectors.toList());
        this.mismatchedChannelData = modMismatchData.mismatchedChannelData().entrySet().stream().filter(e -> !e.getValue().equals(NetworkRegistry.ABSENT)).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        this.missingRegistryMods = modMismatchData.missingRegistryModData();
        this.modIds = presentChannelData.keySet().stream().map(ResourceLocation::getNamespace).distinct().collect(Collectors.toList());

        this.modIds.addAll(missingRegistryMods.keySet());
        this.modUrls = ModList.get().getMods().stream().filter(info -> modIds.contains(info.getModId())).map(info -> Pair.of(info.getModId(), (String)info.getConfig().getConfigElement("displayURL").orElse(""))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        if (modMismatchData.containsMismatches())
            this.addRenderableWidget(mismatchInfoPanel = new MismatchInfoPanel(minecraft, Math.min(440, this.width - 16), listHeight, this.height / 2 - this.listHeight / 2, Math.max(8, this.width / 2 - 220)));

        int buttonWidth = Math.min(210, this.width / 2 - 20);
        this.addRenderableWidget(new Button(Math.max(this.width / 4 - buttonWidth / 2, mismatchInfoPanel.left), Math.min(this.height / 2 + this.listHeight / 2 + this.textHeight / 2 + 10, this.height - 50), buttonWidth, 20, new TextComponent(ForgeI18n.parseMessage("fml.button.open.file", logFile.getFileName())), button -> Util.getPlatform().openFile(logFile.toFile())));
        this.addRenderableWidget(new Button(Math.min(this.width * 3 / 4 - buttonWidth / 2, mismatchInfoPanel.left + mismatchInfoPanel.width - buttonWidth), Math.min(this.height / 2 + this.listHeight / 2 + this.textHeight / 2 + 10, this.height - 50), buttonWidth, 20, new TextComponent(ForgeI18n.parseMessage("fml.button.open.mods.folder")), b -> Util.getPlatform().openFile(modsDir.toFile())));
        this.addRenderableWidget(new Button(this.width / 2 - buttonWidth / 2, Math.min(this.height / 2 + this.listHeight / 2 + this.textHeight / 2 + 35, this.height - 25), buttonWidth, 20, new TranslatableComponent("gui.toMenu"), button -> this.minecraft.setScreen(this.parent)));
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

            List<Pair<MutableComponent, Pair<String, String>>> rawTable = new ArrayList<>();
            if (!missingChannelData.isEmpty())
            {
                rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.missingmods.server" : "fml.modmismatchscreen.missingmods.client")).withStyle(ChatFormatting.GRAY), null));
                rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname")).withStyle(ChatFormatting.UNDERLINE), Pair.of("", ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.youneed"))));
                int i = 0;
                for (ResourceLocation channel : missingChannelData) {
                    rawTable.add(Pair.of(getModComponent(channel.toString(), presentChannelData.get(channel).getLeft(), i), Pair.of("", presentChannelData.getOrDefault(channel, Pair.of("", "")).getRight())));
                    if (++i >= 10) {
                        rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage("fml.modmismatchscreen.additional", missingChannelData.size() - i)).withStyle(ChatFormatting.ITALIC), Pair.of("", "")));
                        break;
                    }
                }
                rawTable.add(Pair.of(new TextComponent(" "), null)); //padding
            }
            if (!mismatchedChannelData.isEmpty())
            {
                rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage("fml.modmismatchscreen.mismatchedmods")).withStyle(ChatFormatting.GRAY), null));
                rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname")).withStyle(ChatFormatting.UNDERLINE), Pair.of(ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.serverhas"), ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.table.serverhas" : "fml.modmismatchscreen.table.youhave"))));
                int i = 0;
                for (Map.Entry<ResourceLocation,  String> channelData : mismatchedChannelData.entrySet()) {
                    rawTable.add(Pair.of(getModComponent(channelData.getKey().toString(), presentChannelData.get(channelData.getKey()).getLeft(), i), Pair.of(presentChannelData.getOrDefault(channelData.getKey(), Pair.of("", "")).getRight(), channelData.getValue())));
                    if (++i >= 10) {
                        rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage("fml.modmismatchscreen.additional", mismatchedChannelData.size() - i)).withStyle(ChatFormatting.ITALIC), Pair.of("", "")));
                        break;
                    }
                }
                rawTable.add(Pair.of(new TextComponent(" "), null)); //padding
            }
            if (!missingRegistryMods.isEmpty())
            {
                rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage("fml.modmismatchscreen.registrymismatch")).withStyle(ChatFormatting.GRAY), null));
                int i = 0;
                boolean logMessage = false;
                TextComponent mods = new TextComponent("");
                for (Map.Entry<String, String> mod : missingRegistryMods.entrySet()) {
                    mods.append(getModComponent(mod.getKey(), mod.getValue(), i));
                    if (++i >= 10) {
                        logMessage = true;
                        break;
                    }
                    else if (missingRegistryMods.size() > i)
                        mods.append(new TextComponent(", ").withStyle(ChatFormatting.WHITE));
                }
                rawTable.add(Pair.of(mods, Pair.of("", "")));
                if (logMessage)
                    rawTable.add(Pair.of(new TextComponent(ForgeI18n.parseMessage("fml.modmismatchscreen.additional", missingRegistryMods.size() - i)).withStyle(ChatFormatting.ITALIC), Pair.of("", "")));
                rawTable.add(Pair.of(new TextComponent(" "), null)); //padding
            }

            this.lineTable = rawTable.stream().flatMap(p -> getFormattedLines(p.getKey(), p.getValue()).stream()).collect(Collectors.toList());
            this.contentSize = lineTable.size();
        }

        private List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> getFormattedLines(MutableComponent name, Pair<String, String> versions)
        {
            Style style = name.getStyle();
            int versionColumns = versions == null ? 0 : (versions.getLeft().isEmpty() ? (versions.getRight().isEmpty() ? 0 : 1) : 2);
            List<FormattedCharSequence> nameLines = font.split(name,nameWidth + versionWidth * (2 - versionColumns) - 4);
            List<FormattedCharSequence> clientVersionLines = font.split(new TextComponent(versions != null ? versions.getLeft() : "").setStyle(style), versionWidth - 4);
            List<FormattedCharSequence> serverVersionLines = font.split(new TextComponent(versions != null ? versions.getRight() : "").setStyle(style), versionWidth - 4);
            List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLines = new ArrayList<>();
            int lineCount = Math.max(nameLines.size(), Math.max(clientVersionLines.size(), serverVersionLines.size()));
            for (int i = 0; i < lineCount; i++) {
                splitLines.add(Pair.of(i < nameLines.size() ? nameLines.get(i) : FormattedCharSequence.EMPTY, versions == null ? null : Pair.of(i < clientVersionLines.size() ? clientVersionLines.get(i) : FormattedCharSequence.EMPTY, i < serverVersionLines.size() ? serverVersionLines.get(i) : FormattedCharSequence.EMPTY)));
            }
            return splitLines;
        }

        private MutableComponent getModComponent(String id, String name, int color)
        {
            String modid = id.split(":")[0];
            return new TextComponent(name).withStyle(color % 2 == 0 ? ChatFormatting.GOLD : ChatFormatting.YELLOW)
                    .withStyle(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent(id + (modUrls.get(modid) != null ? "\n" + ForgeI18n.parseMessage("fml.modmismatchscreen.homepage") : "")))))
                    .withStyle(s -> s.withClickEvent(modUrls.get(modid) != null ? new ClickEvent(ClickEvent.Action.OPEN_URL, modUrls.get(modid)) : null));
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
                int color = Optional.ofNullable(font.getSplitter().componentStyleAtWidth(name, 0)).map(Style::getColor).map(TextColor::getValue).orElse(0xFFFFFF);
                font.draw(stack, name, left + border + (versions == null ? 0 : nameIndent), relativeY + i * 12, color);
                if (versions != null)
                {
                    font.draw(stack, versions.getLeft(), left + border + nameIndent + nameWidth, relativeY + i * 12, color);
                    font.draw(stack, versions.getRight(), left + border + nameIndent + nameWidth + versionWidth, relativeY + i * 12, color);
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
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button)
        {
            Style style = getComponentStyleAt(mouseX, mouseY);
            if (style != null) {
                handleComponentClicked(style);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
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
