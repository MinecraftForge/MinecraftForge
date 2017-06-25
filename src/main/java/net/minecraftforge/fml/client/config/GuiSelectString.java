/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.client.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import static net.minecraftforge.fml.client.config.GuiUtils.RESET_CHAR;
import static net.minecraftforge.fml.client.config.GuiUtils.UNDO_CHAR;

/**
 * This class provides a screen that allows the user to select a value from a list.
 *
 * @author bspkrs
 */
public class GuiSelectString extends GuiScreen
{
    protected GuiScreen parentScreen;
    protected IConfigElement configElement;
    protected GuiSelectStringEntries entryList;
    protected GuiButtonExt btnUndoChanges, btnDefault, btnDone;
    protected String title;
    protected String titleLine2;
    protected String titleLine3;
    protected int slotIndex;
    protected final Map<Object, String> selectableValues;
    public final Object beforeValue;
    public Object currentValue;
    protected HoverChecker tooltipHoverChecker;
    protected List<String> toolTip;
    protected boolean enabled;

    public GuiSelectString(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Map<Object, String> selectableValues, Object currentValue, boolean enabled)
    {
        this.mc = Minecraft.getMinecraft();
        this.parentScreen = parentScreen;
        this.configElement = configElement;
        this.slotIndex = slotIndex;
        this.selectableValues = selectableValues;
        this.beforeValue = currentValue;
        this.currentValue = currentValue;
        this.toolTip = new ArrayList<String>();
        this.enabled = enabled;
        String propName = I18n.format(configElement.getLanguageKey());
        String comment;

        comment = I18n.format(configElement.getLanguageKey() + ".tooltip",
                "\n" + TextFormatting.AQUA, configElement.getDefault(), configElement.getMinValue(), configElement.getMaxValue());

        if (!comment.equals(configElement.getLanguageKey() + ".tooltip"))
            Collections.addAll(toolTip, (TextFormatting.GREEN + propName + "\n" + TextFormatting.YELLOW + comment).split("\n"));
        else if (configElement.getComment() != null && !configElement.getComment().trim().isEmpty())
            Collections.addAll(toolTip, (TextFormatting.GREEN + propName + "\n" + TextFormatting.YELLOW + configElement.getComment()).split("\n"));
        else
            Collections.addAll(toolTip, (TextFormatting.GREEN + propName + "\n" + TextFormatting.RED + "No tooltip defined.").split("\n"));

        if (parentScreen instanceof GuiConfig)
        {
            this.title = ((GuiConfig) parentScreen).title;
            this.titleLine2 = ((GuiConfig) parentScreen).titleLine2;
            this.titleLine3 = I18n.format(configElement.getLanguageKey());
            this.tooltipHoverChecker = new HoverChecker(28, 37, 0, parentScreen.width, 800);
            if(titleLine3 != null && titleLine2 == null)
            {
                ((GuiConfig) parentScreen).titleLine2 = "";
                this.titleLine2 = "";
            }
        }
        else
        {
            this.title = I18n.format(configElement.getLanguageKey());
            this.tooltipHoverChecker = new HoverChecker(8, 17, 0, parentScreen.width, 800);
        }
    }

    @Override
    public void initGui()
    {
        this.entryList = new GuiSelectStringEntries(this, this.mc, this.configElement, this.selectableValues);

        int undoGlyphWidth = mc.fontRenderer.getStringWidth(UNDO_CHAR) * 2;
        int resetGlyphWidth = mc.fontRenderer.getStringWidth(RESET_CHAR) * 2;
        int doneWidth = Math.max(mc.fontRenderer.getStringWidth(I18n.format("gui.done")) + 20, 100);
        int undoWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.undoChanges")) + undoGlyphWidth + 20;
        int resetWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.resetToDefault")) + resetGlyphWidth + 20;
        int buttonWidthHalf = (doneWidth + 5 + undoWidth + 5 + resetWidth) / 2;
        this.buttonList.add(btnDone = new GuiButtonExt(2000, this.width / 2 - buttonWidthHalf, this.height - 29, doneWidth, 20, I18n.format("gui.done")));
        this.buttonList.add(btnDefault = new GuiUnicodeGlyphButton(2001, this.width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 5,
                this.height - 29, resetWidth, 20, " " + I18n.format("fml.configgui.tooltip.resetToDefault"), RESET_CHAR, 2.0F));
        this.buttonList.add(btnUndoChanges = new GuiUnicodeGlyphButton(2002, this.width / 2 - buttonWidthHalf + doneWidth + 5,
                this.height - 29, undoWidth, 20, " " + I18n.format("fml.configgui.tooltip.undoChanges"), UNDO_CHAR, 2.0F));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 2000)
        {
            try
            {
                this.entryList.saveChanges();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (button.id == 2001)
        {
            this.currentValue = configElement.getDefault();
            this.entryList = new GuiSelectStringEntries(this, this.mc, this.configElement, this.selectableValues);
        }
        else if (button.id == 2002)
        {
            this.currentValue = beforeValue;
            this.entryList = new GuiSelectStringEntries(this, this.mc, this.configElement, this.selectableValues);
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.entryList.handleMouseInput();
    }

    @Override
    protected void mouseReleased(int x, int y, int mouseEvent)
    {
        if (mouseEvent != 0 || !this.entryList.mouseReleased(x, y, mouseEvent))
        {
            super.mouseReleased(x, y, mouseEvent);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.entryList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);

        if (this.titleLine2 != null)
            this.drawCenteredString(this.fontRenderer, this.titleLine2, this.width / 2, 18, 16777215);

        if (this.titleLine3 != null)
            this.drawCenteredString(this.fontRenderer, this.titleLine3, this.width / 2, 28, 16777215);

        this.btnDone.enabled = currentValue != null;
        this.btnDefault.enabled = enabled && !this.entryList.isDefault();
        this.btnUndoChanges.enabled = enabled && this.entryList.isChanged();
        super.drawScreen(par1, par2, par3);

        if (this.tooltipHoverChecker != null && this.tooltipHoverChecker.checkHover(par1, par2))
            drawToolTip(this.toolTip, par1, par2);
    }

    public void drawToolTip(List<String> stringList, int x, int y)
    {
        GuiUtils.drawHoveringText(stringList, x, y, width, height, 300, fontRenderer);
    }
}
