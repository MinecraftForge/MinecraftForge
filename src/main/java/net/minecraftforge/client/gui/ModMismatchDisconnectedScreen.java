/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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

import net.minecraftforge.client.gui.widget.ScrollPanel;
import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    private final ModMismatchData modMismatchData;
    private final Path modsDir;
    private final Path logFile;
    private final int listHeight;
    private final Map<ResourceLocation, Pair<String, String>> presentModData;
    private final List<ResourceLocation> missingModData;
    private final Map<ResourceLocation, String> mismatchedModData;
    private final List<String> allModIds;
    private final Map<String, String> presentModUrls;
    private final boolean mismatchedDataFromServer;

    public ModMismatchDisconnectedScreen(Screen parentScreen, Component title, Component reason, ModMismatchData modMismatchData)
    {
        super(title);
        this.parent = parentScreen;
        this.reason = reason;
        this.modMismatchData = modMismatchData;
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs","latest.log"));
        this.listHeight = modMismatchData.containsMismatches() ? 140 : 0;
        this.mismatchedDataFromServer = modMismatchData.mismatchedDataFromServer();
        this.presentModData = modMismatchData.presentModData();
        this.missingModData = modMismatchData.mismatchedModData().entrySet().stream().filter(e -> e.getValue().equals(NetworkRegistry.ABSENT.version())).map(Entry::getKey).collect(Collectors.toList());
        this.mismatchedModData = modMismatchData.mismatchedModData().entrySet().stream().filter(e -> !e.getValue().equals(NetworkRegistry.ABSENT.version())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        this.allModIds = presentModData.keySet().stream().map(ResourceLocation::getNamespace).distinct().collect(Collectors.toList());
        this.presentModUrls = ModList.get().getMods().stream().filter(info -> allModIds.contains(info.getModId())).map(info -> Pair.of(info.getModId(), (String)info.getConfig().getConfigElement("displayURL").orElse(""))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    @Override
    protected void init()
    {
        this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
        this.textHeight = this.message.getLineCount() * 9;

        int listLeft = Math.max(8, this.width / 2 - 220);
        int listWidth = Math.min(440, this.width - 16);
        int upperButtonHeight = Math.min((this.height + this.listHeight + this.textHeight) / 2 + 10, this.height - 50);
        int lowerButtonHeight = Math.min((this.height + this.listHeight + this.textHeight) / 2 + 35, this.height - 25);
        if (modMismatchData.containsMismatches())
            this.addRenderableWidget(new MismatchInfoPanel(minecraft, listWidth, listHeight, (this.height - this.listHeight) / 2, listLeft));

        int buttonWidth = Math.min(210, this.width / 2 - 20);
        this.addRenderableWidget(Button.builder(Component.literal(ForgeI18n.parseMessage("fml.button.open.file", logFile.getFileName())), button -> Util.getPlatform().openFile(logFile.toFile()))
                .bounds(Math.max(this.width / 4 - buttonWidth / 2, listLeft), upperButtonHeight, buttonWidth, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(ForgeI18n.parseMessage("fml.button.open.mods.folder")), button -> Util.getPlatform().openFile(modsDir.toFile()))
                .bounds(Math.min(this.width * 3 / 4 - buttonWidth / 2, listLeft + listWidth - buttonWidth), upperButtonHeight, buttonWidth, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.toMenu"), button -> this.minecraft.setScreen(this.parent))
                .bounds((this.width - buttonWidth) / 2, lowerButtonHeight, buttonWidth, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        int textYOffset = modMismatchData.containsMismatches() ? 18 : 0;
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, (this.height - this.listHeight - this.textHeight) / 2 - textYOffset - 9 * 2, 0xAAAAAA);
        this.message.renderCentered(guiGraphics, this.width / 2, (this.height - this.listHeight - this.textHeight) / 2 - textYOffset);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
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

            //The raw list of the strings in a table row, the components may still be too long for the final table and will be split up later. The first row element may have a style assigned to it that will be used for the whole content row.
            List<Pair<MutableComponent, Pair<String, String>>> rawTable = new ArrayList<>();
            if (!missingModData.isEmpty())
            {
                //The header of the section, colored in gray
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.missingmods.server" : "fml.modmismatchscreen.missingmods.client")).withStyle(ChatFormatting.GRAY), null));
                //This table section contains the mod name and mod version of each mod that has a missing remote counterpart (if the mod is missing on the server, the client mod version is displayed, and vice versa)
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname")).withStyle(ChatFormatting.UNDERLINE), Pair.of("", ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.youneed"))));
                int i = 0;
                for (ResourceLocation mod : missingModData) {
                    rawTable.add(Pair.of(toModNameComponent(mod, presentModData.get(mod).getLeft(), i), Pair.of("", presentModData.getOrDefault(mod, Pair.of("", "")).getRight())));
                    if (++i >= 10) {
                        //If too many missing mod entries are present, append a line referencing how to see the full list and stop rendering any more entries
                        rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.additional", missingModData.size() - i)).withStyle(ChatFormatting.ITALIC), Pair.of("", "")));
                        break;
                    }
                }
                rawTable.add(Pair.of(Component.literal(" "), null)); //padding
            }
            if (!mismatchedModData.isEmpty())
            {
                //The header of the table section, colored in gray
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.mismatchedmods")).withStyle(ChatFormatting.GRAY), null));
                //This table section contains the mod name and both mod versions of each mod that has a mismatching client and server version
                rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname")).withStyle(ChatFormatting.UNDERLINE), Pair.of(ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.serverhas"), ForgeI18n.parseMessage(mismatchedDataFromServer ? "fml.modmismatchscreen.table.serverhas" : "fml.modmismatchscreen.table.youhave"))));
                int i = 0;
                for (Map.Entry<ResourceLocation,  String> modData : mismatchedModData.entrySet()) {
                    rawTable.add(Pair.of(toModNameComponent(modData.getKey(), presentModData.get(modData.getKey()).getLeft(), i), Pair.of(presentModData.getOrDefault(modData.getKey(), Pair.of("", "")).getRight(), modData.getValue())));
                    if (++i >= 10) {
                        //If too many mismatched mod entries are present, append a line referencing how to see the full list and stop rendering any more entries
                        rawTable.add(Pair.of(Component.literal(ForgeI18n.parseMessage("fml.modmismatchscreen.additional", mismatchedModData.size() - i)).withStyle(ChatFormatting.ITALIC), Pair.of("", "")));
                        break;
                    }
                }
                rawTable.add(Pair.of(Component.literal(" "), null)); //padding
            }

            this.lineTable = rawTable.stream().flatMap(p -> splitLineToWidth(p.getKey(), p.getValue()).stream()).collect(Collectors.toList());
            this.contentSize = lineTable.size();
        }

        /**
         * Splits the raw name and version strings, making them use multiple lines if needed, to fit within the table dimensions.
         * The style assigned to the name element is then applied to the entire content row.
         * @param name The first element of the content row, usually representing a table section header or the name of a mod entry
         * @param versions The last two elements of the content row, usually representing the mod versions. If either one or both of them are not given, the first element may take up more space within the table.
         * @return A list of table rows consisting of 3 elements each which consist of the same content as was given by the parameters, but split up to fit within the table dimensions.
         */
        private List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLineToWidth(MutableComponent name, Pair<String, String> versions)
        {
            Style style = name.getStyle();
            int versionColumns = versions == null ? 0 : (versions.getLeft().isEmpty() ? (versions.getRight().isEmpty() ? 0 : 1) : 2);
            int adaptedNameWidth = nameWidth + versionWidth * (2 - versionColumns) - 4; //the name width may be expanded when the version column string is missing
            List<FormattedCharSequence> nameLines = font.split(name, adaptedNameWidth);
            List<FormattedCharSequence> clientVersionLines = font.split(Component.literal(versions != null ? versions.getLeft() : "").setStyle(style), versionWidth - 4);
            List<FormattedCharSequence> serverVersionLines = font.split(Component.literal(versions != null ? versions.getRight() : "").setStyle(style), versionWidth - 4);
            List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLines = new ArrayList<>();
            int rowsOccupied = Math.max(nameLines.size(), Math.max(clientVersionLines.size(), serverVersionLines.size()));
            for (int i = 0; i < rowsOccupied; i++) {
                splitLines.add(Pair.of(i < nameLines.size() ? nameLines.get(i) : FormattedCharSequence.EMPTY, versions == null ? null : Pair.of(i < clientVersionLines.size() ? clientVersionLines.get(i) : FormattedCharSequence.EMPTY, i < serverVersionLines.size() ? serverVersionLines.get(i) : FormattedCharSequence.EMPTY)));
            }
            return splitLines;
        }

        /**
         * Adds a style information to the given mod name string. The style assigned to the returned component contains the color of the mod name,
         * a hover event containing the given id, and an optional click event, which opens the homepage of mod, if present.
         * @param id An id that gets displayed in the hover event. Depending on the origin it may only consist of a namespace (the mod id) or a namespace + path (a channel id associated with the mod).
         * @param modName The name of the mod. It will be rendered as the main text component.
         * @param color Defines the color of the returned style information. An odd number will result in a yellow, an even one in a gold color. This color variation makes it easier for users to distinguish different mod entries.
         * @return A component with the mod name as the main text component, and an assigned style which will be used for the whole content row.
         */
        private MutableComponent toModNameComponent(ResourceLocation id, String modName, int color)
        {
            String modId = id.getNamespace();
            String tooltipId = id.getPath().isEmpty() ? id.getNamespace() : id.toString();
            return Component.literal(modName).withStyle(color % 2 == 0 ? ChatFormatting.GOLD : ChatFormatting.YELLOW)
                    .withStyle(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.literal(tooltipId + (!presentModUrls.getOrDefault(modId, "").isEmpty() ? "\n" + ForgeI18n.parseMessage("fml.modmismatchscreen.homepage") : "")))))
                    .withStyle(s -> s.withClickEvent(!presentModUrls.getOrDefault(modId, "").isEmpty() ? new ClickEvent(ClickEvent.Action.OPEN_URL, presentModUrls.get(modId)) : null));
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
        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY)
        {
            int i = 0;

            for (Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>> line : lineTable) {
                FormattedCharSequence name = line.getLeft();
                Pair<FormattedCharSequence, FormattedCharSequence> versions = line.getRight();
                //Since font#draw does not respect the color of the given component, we have to read it out here and then use it as the last parameter
                int color = Optional.ofNullable(font.getSplitter().componentStyleAtWidth(name, 0)).map(Style::getColor).map(TextColor::getValue).orElse(0xFFFFFF);
                //Only indent the given name if a version string is present. This makes it easier to distinguish table section headers and mod entries
                int nameLeft = left + border + (versions == null ? 0 : nameIndent);
                guiGraphics.drawString(font, name, nameLeft, relativeY + i * 12, color, false);
                if (versions != null)
                {
                    guiGraphics.drawString(font, versions.getLeft(), left + border + nameIndent + nameWidth, relativeY + i * 12, color, false);
                    guiGraphics.drawString(font, versions.getRight(), left + border + nameIndent + nameWidth + versionWidth, relativeY + i * 12, color, false);
                }

                i++;
            }
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
        {
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
            Style style = getComponentStyleAt(mouseX, mouseY);
            if (style != null && style.getHoverEvent() != null)
            {
                guiGraphics.renderComponentHoverEffect(font, style, mouseX, mouseY);
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
                    //The relative x needs to take the potentially missing indent of the row into account. It does that by checking if the line has a version associated to it
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
