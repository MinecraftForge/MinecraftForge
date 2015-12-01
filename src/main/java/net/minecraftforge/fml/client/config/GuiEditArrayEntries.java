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

package net.minecraftforge.fml.client.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.common.FMLLog;

import org.lwjgl.input.Keyboard;

import static net.minecraftforge.fml.client.config.GuiUtils.INVALID;
import static net.minecraftforge.fml.client.config.GuiUtils.VALID;

/**
 * This class implements the scrolling list functionality of the GuiEditList screen. It also provides all the default controls
 * for editing array-type properties.
 */
public class GuiEditArrayEntries extends GuiListExtended
{
    protected GuiEditArray owningGui;
    public Minecraft mc;
    public IConfigElement configElement;
    public List<IArrayEntry> listEntries;
    public boolean isDefault;
    public boolean isChanged;
    public boolean canAddMoreEntries;
    public final int controlWidth;
    public final Object[] beforeValues;
    public Object[] currentValues;

    public GuiEditArrayEntries(GuiEditArray parent, Minecraft mc, IConfigElement configElement, Object[] beforeValues, Object[] currentValues)
    {
        super(mc, parent.width, parent.height, parent.titleLine2 != null ? (parent.titleLine3 != null ? 43 : 33) : 23, parent.height - 32, 20);
        this.owningGui = parent;
        this.mc = mc;
        this.configElement = configElement;
        this.beforeValues = beforeValues;
        this.currentValues = currentValues;
        this.setShowSelectionBox(false);
        this.isChanged = !Arrays.deepEquals(beforeValues, currentValues);
        this.isDefault = Arrays.deepEquals(currentValues, configElement.getDefaults());
        this.canAddMoreEntries = !configElement.isListLengthFixed() && (configElement.getMaxListLength() == -1 || currentValues.length < configElement.getMaxListLength());

        listEntries = new ArrayList<IArrayEntry>();

        controlWidth = (parent.width / 2) - (configElement.isListLengthFixed() ? 0 : 48);

        if (configElement.isList() && configElement.getArrayEntryClass() != null)
        {
            Class<? extends IArrayEntry> clazz = configElement.getArrayEntryClass();
            for (Object value : currentValues)
            {
                try
                {
                    listEntries.add(clazz.getConstructor(GuiEditArray.class, GuiEditArrayEntries.class, IConfigElement.class, Object.class)
                            .newInstance(this.owningGui, this, configElement, value.toString()));
                }
                catch (Throwable e)
                {
                    FMLLog.severe("There was a critical error instantiating the custom IGuiEditListEntry for property %s.", configElement.getName());
                    e.printStackTrace();
                }
            }
        }
        else if (configElement.isList() && configElement.getType().equals(ConfigGuiType.BOOLEAN))
            for (Object value : currentValues)
                listEntries.add(new BooleanEntry(this.owningGui, this, configElement, Boolean.valueOf(value.toString())));
        else if (configElement.isList() && configElement.getType().equals(ConfigGuiType.INTEGER))
            for (Object value : currentValues)
                listEntries.add(new IntegerEntry(this.owningGui, this, configElement, Integer.parseInt(value.toString())));
        else if (configElement.isList() && configElement.getType().equals(ConfigGuiType.DOUBLE))
            for (Object value : currentValues)
                listEntries.add(new DoubleEntry(this.owningGui, this, configElement, Double.parseDouble(value.toString())));
        else if (configElement.isList())
            for (Object value : currentValues)
                listEntries.add(new StringEntry(this.owningGui, this, configElement, value.toString()));

        if (!configElement.isListLengthFixed())
            listEntries.add(new BaseEntry(this.owningGui, this, configElement));

    }

    @Override
    protected int getScrollBarX()
    {
        return width - (width / 4);
    }

    /**
     * Gets the width of the list
     */
    @Override
    public int getListWidth()
    {
        return owningGui.width;
    }

    @Override
    public IArrayEntry getListEntry(int index)
    {
        return listEntries.get(index);
    }

    @Override
    protected int getSize()
    {
        return listEntries.size();
    }

    public void addNewEntry(int index)
    {
        if (configElement.isList() && configElement.getType() == ConfigGuiType.BOOLEAN)
            listEntries.add(index, new BooleanEntry(this.owningGui, this, this.configElement, Boolean.valueOf(true)));
        else if (configElement.isList() && configElement.getType() == ConfigGuiType.INTEGER)
            listEntries.add(index, new IntegerEntry(this.owningGui, this, this.configElement, 0));
        else if (configElement.isList() && configElement.getType() == ConfigGuiType.DOUBLE)
            listEntries.add(index, new DoubleEntry(this.owningGui, this, this.configElement, 0.0D));
        else if (configElement.isList())
            listEntries.add(index, new StringEntry(this.owningGui, this, this.configElement, ""));
        this.canAddMoreEntries = !configElement.isListLengthFixed()
                && (configElement.getMaxListLength() == -1 || this.listEntries.size() - 1 < configElement.getMaxListLength());
        keyTyped((char) Keyboard.CHAR_NONE, Keyboard.KEY_END);
    }

    public void removeEntry(int index)
    {
        this.listEntries.remove(index);
        this.canAddMoreEntries = !configElement.isListLengthFixed()
                && (configElement.getMaxListLength() == -1 || this.listEntries.size() - 1 < configElement.getMaxListLength());
        keyTyped((char) Keyboard.CHAR_NONE, Keyboard.KEY_END);
    }

    public boolean isChanged()
    {
        return isChanged;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void recalculateState()
    {
        isDefault = true;
        isChanged = false;

        int listLength = configElement.isListLengthFixed() ? listEntries.size() : listEntries.size() - 1;

        if (listLength != configElement.getDefaults().length)
        {
            isDefault = false;
        }

        if (listLength != beforeValues.length)
        {
            isChanged = true;
        }

        if (isDefault)
            for (int i = 0; i < listLength; i++)
                if (!configElement.getDefaults()[i].equals(listEntries.get(i).getValue()))
                    isDefault = false;

        if (!isChanged)
            for (int i = 0; i < listLength; i++)
                if (!beforeValues[i].equals(listEntries.get(i).getValue()))
                    isChanged = true;
    }

    protected void keyTyped(char eventChar, int eventKey)
    {
        for (IArrayEntry entry : this.listEntries)
            entry.keyTyped(eventChar, eventKey);

        recalculateState();
    }

    protected void updateScreen()
    {
        for (IArrayEntry entry : this.listEntries)
            entry.updateCursorCounter();
    }

    protected void mouseClickedPassThru(int x, int y, int mouseEvent)
    {
        for (IArrayEntry entry : this.listEntries)
            entry.mouseClicked(x, y, mouseEvent);
    }

    protected boolean isListSavable()
    {
        for (IArrayEntry entry : this.listEntries)
            if (!entry.isValueSavable())
                return false;

        return true;
    }

    protected void saveListChanges()
    {
        int listLength = configElement.isListLengthFixed() ? listEntries.size() : listEntries.size() - 1;

        if (owningGui.slotIndex != -1 && owningGui.parentScreen != null
                && owningGui.parentScreen instanceof GuiConfig
                && ((GuiConfig) owningGui.parentScreen).entryList.getListEntry(owningGui.slotIndex) instanceof ArrayEntry)
        {
            ArrayEntry entry = (ArrayEntry) ((GuiConfig) owningGui.parentScreen).entryList.getListEntry(owningGui.slotIndex);

            Object[] ao = new Object[listLength];
            for (int i = 0; i < listLength; i++)
                ao[i] = listEntries.get(i).getValue();

            entry.setListFromChildScreen(ao);
        }
        else
        {
            if (configElement.isList() && configElement.getType() == ConfigGuiType.BOOLEAN)
            {
                Boolean[] abol = new Boolean[listLength];
                for (int i = 0; i < listLength; i++)
                    abol[i] = Boolean.valueOf(listEntries.get(i).getValue().toString());

                configElement.set(abol);
            }
            else if (configElement.isList() && configElement.getType() == ConfigGuiType.INTEGER)
            {
                Integer[] ai = new Integer[listLength];
                for (int i = 0; i < listLength; i++)
                    ai[i] = Integer.valueOf(listEntries.get(i).getValue().toString());

                configElement.set(ai);
            }
            else if (configElement.isList() && configElement.getType() == ConfigGuiType.DOUBLE)
            {
                Double[] ad = new Double[listLength];
                for (int i = 0; i < listLength; i++)
                    ad[i] = Double.valueOf(listEntries.get(i).getValue().toString());

                configElement.set(ad);
            }
            else if (configElement.isList())
            {
                String[] as = new String[listLength];
                for (int i = 0; i < listLength; i++)
                    as[i] = listEntries.get(i).getValue().toString();

                configElement.set(as);
            }
        }
    }

    protected void drawScreenPost(int mouseX, int mouseY, float f)
    {
        for (IArrayEntry entry : this.listEntries)
            entry.drawToolTip(mouseX, mouseY);
    }

    /**
     * IGuiListEntry Inner Classes
     */

    public static class DoubleEntry extends StringEntry
    {
        public DoubleEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Double value)
        {
            super(owningScreen, owningEntryList, configElement, value);
            this.isValidated = true;
        }

        @Override
        public void keyTyped(char eventChar, int eventKey)
        {
            if (owningScreen.enabled || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT
                    || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END)
            {
                String validChars = "0123456789";
                String before = this.textFieldValue.getText();
                if (validChars.contains(String.valueOf(eventChar)) ||
                        (!before.startsWith("-") && this.textFieldValue.getCursorPosition() == 0 && eventChar == '-')
                        || (!before.contains(".") && eventChar == '.')
                        || eventKey == Keyboard.KEY_BACK || eventKey == Keyboard.KEY_DELETE || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT
                        || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END)
                    this.textFieldValue.textboxKeyTyped((owningScreen.enabled ? eventChar : Keyboard.CHAR_NONE), eventKey);

                if (!textFieldValue.getText().trim().isEmpty() && !textFieldValue.getText().trim().equals("-"))
                {
                    try
                    {
                        double value = Double.parseDouble(textFieldValue.getText().trim());
                        if (value < Double.valueOf(configElement.getMinValue().toString()) || value > Double.valueOf(configElement.getMaxValue().toString()))
                            this.isValidValue = false;
                        else
                            this.isValidValue = true;
                    }
                    catch (Throwable e)
                    {
                        this.isValidValue = false;
                    }
                }
                else
                    this.isValidValue = false;
            }
        }

        @Override
        public Double getValue()
        {
            try
            {
                return Double.valueOf(this.textFieldValue.getText().trim());
            }
            catch (Throwable e)
            {
                return Double.MAX_VALUE;
            }
        }
    }

    public static class IntegerEntry extends StringEntry
    {
        public IntegerEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Integer value)
        {
            super(owningScreen, owningEntryList, configElement, value);
            this.isValidated = true;
        }

        @Override
        public void keyTyped(char eventChar, int eventKey)
        {
            if (owningScreen.enabled || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT
                    || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END)
            {
                String validChars = "0123456789";
                String before = this.textFieldValue.getText();
                if (validChars.contains(String.valueOf(eventChar))
                        || (!before.startsWith("-") && this.textFieldValue.getCursorPosition() == 0 && eventChar == '-')
                        || eventKey == Keyboard.KEY_BACK || eventKey == Keyboard.KEY_DELETE
                        || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END)
                    this.textFieldValue.textboxKeyTyped((owningScreen.enabled ? eventChar : Keyboard.CHAR_NONE), eventKey);

                if (!textFieldValue.getText().trim().isEmpty() && !textFieldValue.getText().trim().equals("-"))
                {
                    try
                    {
                        long value = Long.parseLong(textFieldValue.getText().trim());
                        if (value < Integer.valueOf(configElement.getMinValue().toString()) || value > Integer.valueOf(configElement.getMaxValue().toString()))
                            this.isValidValue = false;
                        else
                            this.isValidValue = true;
                    }
                    catch (Throwable e)
                    {
                        this.isValidValue = false;
                    }
                }
                else
                    this.isValidValue = false;
            }
        }

        @Override
        public Integer getValue()
        {
            try
            {
                return Integer.valueOf(this.textFieldValue.getText().trim());
            }
            catch (Throwable e)
            {
                return Integer.MAX_VALUE;
            }
        }
    }

    public static class StringEntry extends BaseEntry
    {
        protected final GuiTextField textFieldValue;

        public StringEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Object value)
        {
            super(owningScreen, owningEntryList, configElement);
            this.textFieldValue = new GuiTextField(0, owningEntryList.mc.fontRendererObj, owningEntryList.width / 4 + 1, 0, owningEntryList.controlWidth - 3, 16);
            this.textFieldValue.setMaxStringLength(10000);
            this.textFieldValue.setText(value.toString());
            this.isValidated = configElement.getValidationPattern() != null;

            if (configElement.getValidationPattern() != null)
            {
                if (configElement.getValidationPattern().matcher(this.textFieldValue.getText().trim()).matches())
                    isValidValue = true;
                else
                    isValidValue = false;
            }
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
            if (configElement.isListLengthFixed() || slotIndex != owningEntryList.listEntries.size() - 1)
            {
                this.textFieldValue.setVisible(true);
                this.textFieldValue.yPosition = y + 1;
                this.textFieldValue.drawTextBox();
            }
            else
                this.textFieldValue.setVisible(false);
        }

        @Override
        public void keyTyped(char eventChar, int eventKey)
        {
            if (owningScreen.enabled || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT
                    || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END)
            {
                this.textFieldValue.textboxKeyTyped((owningScreen.enabled ? eventChar : Keyboard.CHAR_NONE), eventKey);

                if (configElement.getValidationPattern() != null)
                {
                    if (configElement.getValidationPattern().matcher(this.textFieldValue.getText().trim()).matches())
                        isValidValue = true;
                    else
                        isValidValue = false;
                }
            }
        }

        @Override
        public void updateCursorCounter()
        {
            this.textFieldValue.updateCursorCounter();
        }

        @Override
        public void mouseClicked(int x, int y, int mouseEvent)
        {
            this.textFieldValue.mouseClicked(x, y, mouseEvent);
        }

        @Override
        public Object getValue()
        {
            return this.textFieldValue.getText().trim();
        }

    }

    public static class BooleanEntry extends BaseEntry
    {
        protected final GuiButtonExt btnValue;
        private boolean value;

        public BooleanEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, boolean value)
        {
            super(owningScreen, owningEntryList, configElement);
            this.value = value;
            this.btnValue = new GuiButtonExt(0, 0, 0, owningEntryList.controlWidth, 18, I18n.format(String.valueOf(value)));
            this.btnValue.enabled = owningScreen.enabled;
            this.isValidated = false;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
            this.btnValue.xPosition = listWidth / 4;
            this.btnValue.yPosition = y;

            String trans = I18n.format(String.valueOf(value));
            if (!trans.equals(String.valueOf(value)))
                this.btnValue.displayString = trans;
            else
                this.btnValue.displayString = String.valueOf(value);
            btnValue.packedFGColour = value ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);

            this.btnValue.drawButton(owningEntryList.mc, mouseX, mouseY);
        }

        @Override
        public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.btnValue.mousePressed(owningEntryList.mc, x, y))
            {
                btnValue.playPressSound(owningEntryList.mc.getSoundHandler());
                value = !value;
                owningEntryList.recalculateState();
                return true;
            }

            return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
        }

        @Override
        public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            this.btnValue.mouseReleased(x, y);
            super.mouseReleased(index, x, y, mouseEvent, relativeX, relativeY);
        }

        @Override
        public Object getValue()
        {
            return Boolean.valueOf(value);
        }
    }

    public static class BaseEntry implements IArrayEntry
    {
        protected final GuiEditArray owningScreen;
        protected final GuiEditArrayEntries owningEntryList;
        protected final IConfigElement configElement;
        protected final GuiButtonExt btnAddNewEntryAbove;
        private final HoverChecker addNewEntryAboveHoverChecker;
        protected final GuiButtonExt btnRemoveEntry;
        private final HoverChecker removeEntryHoverChecker;
        private List<String> addNewToolTip, removeToolTip;
        protected boolean isValidValue = true;
        protected boolean isValidated = false;

        public BaseEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement)
        {
            this.owningScreen = owningScreen;
            this.owningEntryList = owningEntryList;
            this.configElement = configElement;
            this.btnAddNewEntryAbove = new GuiButtonExt(0, 0, 0, 18, 18, "+");
            this.btnAddNewEntryAbove.packedFGColour = GuiUtils.getColorCode('2', true);
            this.btnAddNewEntryAbove.enabled = owningScreen.enabled;
            this.btnRemoveEntry = new GuiButtonExt(0, 0, 0, 18, 18, "x");
            this.btnRemoveEntry.packedFGColour = GuiUtils.getColorCode('c', true);
            this.btnRemoveEntry.enabled = owningScreen.enabled;
            this.addNewEntryAboveHoverChecker = new HoverChecker(this.btnAddNewEntryAbove, 800);
            this.removeEntryHoverChecker = new HoverChecker(this.btnRemoveEntry, 800);
            this.addNewToolTip = new ArrayList<String>();
            this.removeToolTip = new ArrayList<String>();
            addNewToolTip.add(I18n.format("fml.configgui.tooltip.addNewEntryAbove"));
            removeToolTip.add(I18n.format("fml.configgui.tooltip.removeEntry"));
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            if (this.getValue() != null && this.isValidated)
                owningEntryList.mc.fontRendererObj.drawString(
                        isValidValue ? EnumChatFormatting.GREEN + VALID : EnumChatFormatting.RED + INVALID,
                        listWidth / 4 - owningEntryList.mc.fontRendererObj.getStringWidth(VALID) - 2,
                        y + slotHeight / 2 - owningEntryList.mc.fontRendererObj.FONT_HEIGHT / 2,
                        16777215);

            int half = listWidth / 2;
            if (owningEntryList.canAddMoreEntries)
            {
                this.btnAddNewEntryAbove.visible = true;
                this.btnAddNewEntryAbove.xPosition = half + ((half / 2) - 44);
                this.btnAddNewEntryAbove.yPosition = y;
                this.btnAddNewEntryAbove.drawButton(owningEntryList.mc, mouseX, mouseY);
            }
            else
                this.btnAddNewEntryAbove.visible = false;

            if (!configElement.isListLengthFixed() && slotIndex != owningEntryList.listEntries.size() - 1)
            {
                this.btnRemoveEntry.visible = true;
                this.btnRemoveEntry.xPosition = half + ((half / 2) - 22);
                this.btnRemoveEntry.yPosition = y;
                this.btnRemoveEntry.drawButton(owningEntryList.mc, mouseX, mouseY);
            }
            else
                this.btnRemoveEntry.visible = false;
        }

        @Override
        public void drawToolTip(int mouseX, int mouseY)
        {
            boolean canHover = mouseY < owningEntryList.bottom && mouseY > owningEntryList.top;
            if (this.btnAddNewEntryAbove.visible && this.addNewEntryAboveHoverChecker.checkHover(mouseX, mouseY, canHover))
                owningScreen.drawToolTip(this.addNewToolTip, mouseX, mouseY);
            if (this.btnRemoveEntry.visible && this.removeEntryHoverChecker.checkHover(mouseX, mouseY, canHover))
                owningScreen.drawToolTip(this.removeToolTip, mouseX, mouseY);
        }

        @Override
        public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.btnAddNewEntryAbove.mousePressed(owningEntryList.mc, x, y))
            {
                btnAddNewEntryAbove.playPressSound(owningEntryList.mc.getSoundHandler());
                owningEntryList.addNewEntry(index);
                owningEntryList.recalculateState();
                return true;
            }
            else if (this.btnRemoveEntry.mousePressed(owningEntryList.mc, x, y))
            {
                btnRemoveEntry.playPressSound(owningEntryList.mc.getSoundHandler());
                owningEntryList.removeEntry(index);
                owningEntryList.recalculateState();
                return true;
            }

            return false;
        }

        @Override
        public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            this.btnAddNewEntryAbove.mouseReleased(x, y);
            this.btnRemoveEntry.mouseReleased(x, y);
        }

        @Override
        public void keyTyped(char eventChar, int eventKey)
        {}

        @Override
        public void updateCursorCounter()
        {}

        @Override
        public void mouseClicked(int x, int y, int mouseEvent)
        {}

        @Override
        public boolean isValueSavable()
        {
            return isValidValue;
        }

        @Override
        public Object getValue()
        {
            return null;
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_){}
    }

    public static interface IArrayEntry extends GuiListExtended.IGuiListEntry
    {
        public void keyTyped(char eventChar, int eventKey);

        public void updateCursorCounter();

        public void mouseClicked(int x, int y, int mouseEvent);

        public void drawToolTip(int mouseX, int mouseY);

        public boolean isValueSavable();

        public Object getValue();
    }
}
