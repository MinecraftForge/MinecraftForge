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

import java.util.List;
import java.util.Optional;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.MultipleModsErrored;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

@OnlyIn(Dist.CLIENT)
public class GuiMultipleModsErrored extends GuiErrorBase
{
    private final List<MissingModsException> missingModsExceptions;
    private GuiList list;

    public GuiMultipleModsErrored(MultipleModsErrored exception)
    {
        missingModsExceptions = exception.missingModsExceptions;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int additionalSize = missingModsExceptions.isEmpty() ? 20 : 55;
        for (MissingModsException exception : missingModsExceptions)
        {
            additionalSize += exception.getMissingModInfos().size() * 10;
        }
        list = new GuiList(missingModsExceptions.size() * 15 + additionalSize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        String missingMultipleModsText = I18n.format("fml.messages.mod.missing.multiple", missingModsExceptions.size());
        this.drawCenteredString(this.fontRenderer, missingMultipleModsText, this.width / 2, 10, 0xFFFFFF);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double scroll)
    {
        return this.list.mouseScrolled(scroll);
    }

    private class GuiList extends GuiScrollingList
    {
        public GuiList(int entryHeight)
        {
            super(GuiMultipleModsErrored.this.mc,
                GuiMultipleModsErrored.this.width - 20,
                GuiMultipleModsErrored.this.height - 30,
                30, GuiMultipleModsErrored.this.height - 50,
                10,
                entryHeight,
                GuiMultipleModsErrored.this.width,
                GuiMultipleModsErrored.this.height);
        }

        @Override
        protected int getSize()
        {
            return 1;
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick)
        {
        }

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }

        @Override
        protected void drawBackground()
        {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
        {
            int offset = slotTop;
            FontRenderer renderer = GuiMultipleModsErrored.this.fontRenderer;
            if (!missingModsExceptions.isEmpty())
            {
                renderer.drawString(I18n.format("fml.messages.mod.missing.dependencies.multiple.issues"), this.left, offset, 0xFFFFFF);
                offset += 15;
                for (MissingModsException exception : missingModsExceptions)
                {
                    renderer.drawString(exception.getModName() + ":", this.left, offset, 0xFFFFFF);
                    for (MissingModsException.MissingModInfo versionInfo : exception.getMissingModInfos())
                    {
                        ArtifactVersion acceptedVersion = versionInfo.getAcceptedVersion();
                        String acceptedModId = acceptedVersion.getLabel();
                        ArtifactVersion currentVersion = versionInfo.getCurrentVersion();
                        String missingReason;
                        if (currentVersion == null)
                        {
                            missingReason = I18n.format("fml.messages.mod.missing.dependencies.missing");
                        }
                        else
                        {
                            missingReason = I18n.format("fml.messages.mod.missing.dependencies.you.have", currentVersion.getVersionString());
                        }
                        String acceptedModVersionString = acceptedVersion.getRangeString();
                        if (acceptedVersion instanceof DefaultArtifactVersion)
                        {
                            DefaultArtifactVersion dav = (DefaultArtifactVersion) acceptedVersion;
                            if (dav.getRange() != null)
                            {
                                acceptedModVersionString = dav.getRange().toStringFriendly();
                            }
                        }
                        Optional<? extends ModContainer> acceptedMod = ModList.get().getModContainerById(acceptedModId);
                        String acceptedModName = acceptedMod.map((mod) -> mod.getModInfo().getDisplayName()).orElse(acceptedModId);
                        String versionInfoText = String.format(TextFormatting.BOLD + "%s " + TextFormatting.RESET + "%s (%s)", acceptedModName, acceptedModVersionString, missingReason);
                        String message;
                        if (versionInfo.isRequired())
                        {
                            message = I18n.format("fml.messages.mod.missing.dependencies.requires", versionInfoText);
                        }
                        else
                        {
                            message = I18n.format("fml.messages.mod.missing.dependencies.compatible.with", versionInfoText);
                        }
                        offset += 10;
                        renderer.drawString(message, this.left, offset, 0xEEEEEE);
                    }

                    offset += 15;
                }
            }
        }
    }
}
