/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import static net.minecraft.util.StringUtils.stripControlCodes;

/**
 * @author cpw
 *
 */
public class GuiSlotModList extends GuiListExtended<GuiSlotModList.ModEntry>
{
    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");
    private final int listWidth;

    private GuiModList parent;

    public GuiSlotModList(GuiModList parent, int listWidth)
    {
        super(parent.getMinecraftInstance(), listWidth, parent.height, 32, parent.height - 88 + 4, parent.getFontRenderer().FONT_HEIGHT * 2 + 8);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    @Override
    protected int getScrollBarX()
    {
        return this.listWidth + 6;
    }

    @Override
    public int getListWidth()
    {
        return this.listWidth;
    }

    void refreshList() {
        this.func_195086_c();
        parent.buildModList(this::func_195085_a, mod->new ModEntry(mod, this.parent));
    }

    @Override
    protected boolean isSelected(int index)
    {
        return this.parent.modIndexSelected(index);
    }

    @Override
    protected void drawBackground()
    {
        this.parent.drawDefaultBackground();
    }

    class ModEntry extends GuiListExtended.IGuiListEntry<ModEntry> {
        private final ModInfo modInfo;
        private final GuiModList parent;

        ModEntry(ModInfo info, GuiModList parent) {
            this.modInfo = info;
            this.parent = parent;
        }

        @Override
        public void func_194999_a(int p_194999_1_, int p_194999_2_, int p_194999_3_, int p_194999_4_, boolean p_194999_5_, float p_194999_6_)
        {
            int top = this.func_195001_c();
            int left = this.func_195002_d();
            String name = stripControlCodes(modInfo.getDisplayName());
            String version = stripControlCodes(modInfo.getVersion().getVersionString());
            VersionChecker.CheckResult vercheck = VersionChecker.getResult(modInfo);
            FontRenderer font = this.parent.getFontRenderer();
            font.func_211126_b(font.trimStringToWidth(name, listWidth),left + 3, top + 2, 0xFFFFFF);
            font.func_211126_b(font.trimStringToWidth(version, listWidth), left + 3 , top + 2 + font.FONT_HEIGHT, 0xCCCCCC);
            if (vercheck.status.shouldDraw())
            {
                //TODO: Consider adding more icons for visualization
                Minecraft.getMinecraft().getTextureManager().bindTexture(VERSION_CHECK_ICONS);
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.pushMatrix();
                Gui.drawModalRectWithCustomSizedTexture(right - (height / 2 + 4), GuiSlotModList.this.top + (height / 2 - 4), vercheck.status.getSheetOffset() * 8, (vercheck.status.isAnimated() && ((System.currentTimeMillis() / 800 & 1)) == 1) ? 8 : 0, 8, 8, 64, 16);
                GlStateManager.popMatrix();
            }
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
        {
            parent.selectModIndex(this.func_195003_b());
            return false;
        }
    }
}
