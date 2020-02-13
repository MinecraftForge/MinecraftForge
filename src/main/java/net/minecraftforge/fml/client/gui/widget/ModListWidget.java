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

package net.minecraftforge.fml.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import com.mojang.blaze3d.systems.RenderSystem;

public class ModListWidget extends ExtendedList<ModListWidget.ModEntry>
{
    private static String stripControlCodes(String value) { return net.minecraft.util.StringUtils.stripControlCodes(value); }
    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");
    private final int listWidth;

    private ModListScreen parent;

    public ModListWidget(ModListScreen parent, int listWidth, int top, int bottom)
    {
        super(parent.getMinecraftInstance(), listWidth, parent.height, top, bottom, parent.getFontRenderer().FONT_HEIGHT * 2 + 8);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.listWidth;
    }

    @Override
    public int getRowWidth()
    {
        return this.listWidth;
    }

    public void refreshList() {
        this.clearEntries();
        parent.buildModList(this::addEntry, mod->new ModEntry(mod, this.parent));
    }

    @Override
    protected void renderBackground()
    {
        this.parent.renderBackground();
    }

    public class ModEntry extends ExtendedList.AbstractListEntry<ModEntry> {
        private final ModInfo modInfo;
        private final ModListScreen parent;

        ModEntry(ModInfo info, ModListScreen parent) {
            this.modInfo = info;
            this.parent = parent;
        }

        @Override
        public void render(int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks)
        {
            String name = stripControlCodes(modInfo.getDisplayName());
            String version = stripControlCodes(MavenVersionStringHelper.artifactVersionToString(modInfo.getVersion()));
            VersionChecker.CheckResult vercheck = VersionChecker.getResult(modInfo);
            FontRenderer font = this.parent.getFontRenderer();
            font.drawString(font.trimStringToWidth(name, listWidth),left + 3, top + 2, 0xFFFFFF);
            font.drawString(font.trimStringToWidth(version, listWidth), left + 3 , top + 2 + font.FONT_HEIGHT, 0xCCCCCC);
            if (vercheck.status.shouldDraw())
            {
                //TODO: Consider adding more icons for visualization
                Minecraft.getInstance().getTextureManager().bindTexture(VERSION_CHECK_ICONS);
                RenderSystem.color4f(1, 1, 1, 1);
                RenderSystem.pushMatrix();
                AbstractGui.blit(getLeft() + width - 12, top + entryHeight / 4, vercheck.status.getSheetOffset() * 8, (vercheck.status.isAnimated() && ((System.currentTimeMillis() / 800 & 1)) == 1) ? 8 : 0, 8, 8, 64, 16);
                RenderSystem.popMatrix();
            }
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
        {
            parent.setSelected(this);
            ModListWidget.this.setSelected(this);
            return false;
        }

        public ModInfo getInfo()
        {
            return modInfo;
        }
    }
}
