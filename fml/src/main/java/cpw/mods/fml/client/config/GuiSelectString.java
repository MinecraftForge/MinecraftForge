/*
 * Forge Mod Loader
 * Copyright (c) 2012-2014 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors (this class):
 *     bspkrs - implementation
 */

package cpw.mods.fml.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

import static cpw.mods.fml.client.config.GuiUtils.RESET_CHAR;
import static cpw.mods.fml.client.config.GuiUtils.UNDO_CHAR;

/**
 * This class provides a screen that allows the user to select a value from a list.
 * 
 * @author bspkrs
 */
public class GuiSelectString extends GuiScreen
{
    protected GuiScreen parentScreen;
    @SuppressWarnings("rawtypes")
    protected IConfigElement configElement;
    private GuiSelectStringEntries entriesList;
    private GuiButtonExt btnUndoChanges, btnDefault, btnDone;
    private String title;
    protected String titleLine2;
    protected String titleLine3;
    protected int slotIndex;
    private final Map<Object, String> selectableValues;
    public final Object beforeValue;
    public Object currentValue;
    private HoverChecker tooltipHoverChecker;
    @SuppressWarnings("rawtypes")
    private List toolTip;
    protected boolean enabled;

    @SuppressWarnings("rawtypes")
    public GuiSelectString(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Map<Object, String> selectableValues, Object currentValue, boolean enabled)
    {
        this.mc = Minecraft.getMinecraft();
        this.parentScreen = parentScreen;
        this.configElement = configElement;
        this.slotIndex = slotIndex;
        this.selectableValues = selectableValues;
        this.beforeValue = currentValue;
        this.currentValue = currentValue;
        this.toolTip = new ArrayList();
        this.enabled = enabled;
        String propName = I18n.format(configElement.getLanguageKey());
        String comment;

        comment = I18n.format(configElement.getLanguageKey() + ".tooltip",
                "\n" + EnumChatFormatting.AQUA, configElement.getDefault(), configElement.getMinValue(), configElement.getMaxValue());

        if (!comment.equals(configElement.getLanguageKey() + ".tooltip"))
            toolTip = mc.fontRenderer.listFormattedStringToWidth(
                    EnumChatFormatting.GREEN + propName + "\n" + EnumChatFormatting.YELLOW + comment, 300);
        else if (configElement.getComment() != null && !configElement.getComment().trim().isEmpty())
            toolTip = mc.fontRenderer.listFormattedStringToWidth(
                    EnumChatFormatting.GREEN + propName + "\n" + EnumChatFormatting.YELLOW + configElement.getComment(), 300);
        else
            toolTip = mc.fontRenderer.listFormattedStringToWidth(
                    EnumChatFormatting.GREEN + propName + "\n" + EnumChatFormatting.RED + "No tooltip defined.", 300);

        if (parentScreen instanceof GuiConfig)
        {
            this.title = ((GuiConfig) parentScreen).title;
            this.titleLine2 = ((GuiConfig) parentScreen).titleLine2;
            this.titleLine3 = I18n.format(configElement.getLanguageKey());
            this.tooltipHoverChecker = new HoverChecker(28, 37, 0, parentScreen.width, 800);

        }
        else
        {
            this.title = I18n.format(configElement.getLanguageKey());
            this.tooltipHoverChecker = new HoverChecker(8, 17, 0, parentScreen.width, 800);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.entriesList = new GuiSelectStringEntries(this, this.mc, this.configElement, this.selectableValues);

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
                this.entriesList.saveChanges();
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
            this.entriesList = new GuiSelectStringEntries(this, this.mc, this.configElement, this.selectableValues);
        }
        else if (button.id == 2002)
        {
            this.currentValue = beforeValue;
            this.entriesList = new GuiSelectStringEntries(this, this.mc, this.configElement, this.selectableValues);
        }
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int mouseEvent)
    {
        if (mouseEvent != 0 || !this.entriesList.func_148181_b(x, y, mouseEvent))
        {
            super.mouseMovedOrUp(x, y, mouseEvent);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.entriesList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);

        if (this.titleLine2 != null)
            this.drawCenteredString(this.fontRendererObj, this.titleLine2, this.width / 2, 18, 16777215);

        if (this.titleLine3 != null)
            this.drawCenteredString(this.fontRendererObj, this.titleLine3, this.width / 2, 28, 16777215);

        this.btnDone.enabled = currentValue != null;
        this.btnDefault.enabled = enabled && !this.entriesList.isDefault();
        this.btnUndoChanges.enabled = enabled && this.entriesList.isChanged();
        super.drawScreen(par1, par2, par3);

        if (this.tooltipHoverChecker != null && this.tooltipHoverChecker.checkHover(par1, par2))
            drawToolTip(this.toolTip, par1, par2);
    }

    @SuppressWarnings("rawtypes")
    public void drawToolTip(List stringList, int x, int y)
    {
        this.func_146283_a(stringList, x, y);
    }
}