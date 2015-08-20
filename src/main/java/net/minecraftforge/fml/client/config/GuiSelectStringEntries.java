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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraftforge.fml.client.config.GuiConfigEntries.SelectValueEntry;

/**
 * This class implements the scrolling list functionality of the GuiSelectString screen.
 *
 * @author bspkrs
 */
public class GuiSelectStringEntries extends GuiListExtended
{
    public GuiSelectString owningScreen;
    public Minecraft mc;
    public IConfigElement configElement;
    public List<IGuiSelectStringListEntry> listEntries;
    public final Map<Object, String> selectableValues;
    public int selectedIndex = -1;
    public int maxEntryWidth = 0;

    public GuiSelectStringEntries(GuiSelectString owningScreen, Minecraft mc, IConfigElement configElement, Map<Object, String> selectableValues)
    {
        super(mc, owningScreen.width, owningScreen.height, owningScreen.titleLine2 != null ? (owningScreen.titleLine3 != null ? 43 : 33) : 23,
                owningScreen.height - 32, 11);
        this.owningScreen = owningScreen;
        this.mc = mc;
        this.configElement = configElement;
        this.selectableValues = selectableValues;
        this.setShowSelectionBox(true);

        listEntries = new ArrayList<IGuiSelectStringListEntry>();

        int index = 0;
        List<Entry<Object, String>> sortedList = new ArrayList<Entry<Object, String>>(selectableValues.entrySet());
        Collections.sort(sortedList, new EntryComparator());

        for (Entry<Object, String> entry : sortedList)
        {
            listEntries.add(new ListEntry(this, entry));
            if (mc.fontRendererObj.getStringWidth(entry.getValue()) > maxEntryWidth)
                maxEntryWidth = mc.fontRendererObj.getStringWidth(entry.getValue());

            if (owningScreen.currentValue.equals(entry.getKey()))
            {
                this.selectedIndex = index;
            }

            index++;
        }
    }

    public static class EntryComparator implements Comparator<Entry<Object, String>>
    {
        @Override
        public int compare(Entry<Object, String> o1, Entry<Object, String> o2)
        {
            int compare = o1.getValue().toLowerCase(Locale.US).compareTo(o2.getValue().toLowerCase(Locale.US));

            if (compare == 0)
                compare = o1.getKey().toString().toLowerCase(Locale.US).compareTo(o2.getKey().toString().toLowerCase(Locale.US));

            return compare;
        }
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    @Override
    protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY)
    {
        selectedIndex = index;
        owningScreen.currentValue = listEntries.get(index).getValue();
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    @Override
    protected boolean isSelected(int index)
    {
        return index == selectedIndex;
    }

    @Override
    protected int getScrollBarX()
    {
        return width / 2 + this.maxEntryWidth / 2 + 5;
    }

    /**
     * Gets the width of the list
     */
    @Override
    public int getListWidth()
    {
        return maxEntryWidth + 5;
    }

    @Override
    public IGuiSelectStringListEntry getListEntry(int index)
    {
        return listEntries.get(index);
    }

    @Override
    protected int getSize()
    {
        return listEntries.size();
    }

    public boolean isChanged()
    {
        return owningScreen.beforeValue != null ? !owningScreen.beforeValue.equals(owningScreen.currentValue) : owningScreen.currentValue != null;
    }

    public boolean isDefault()
    {
        return owningScreen.currentValue != null ? owningScreen.currentValue.equals(configElement.getDefault()) : configElement.getDefault() == null;
    }

    public void saveChanges()
    {
        if (owningScreen.slotIndex != -1 && owningScreen.parentScreen != null
                && owningScreen.parentScreen instanceof GuiConfig
                && ((GuiConfig) owningScreen.parentScreen).entryList.getListEntry(owningScreen.slotIndex) instanceof SelectValueEntry)
        {
            SelectValueEntry entry = (SelectValueEntry) ((GuiConfig) owningScreen.parentScreen).entryList.getListEntry(owningScreen.slotIndex);

            entry.setValueFromChildScreen(owningScreen.currentValue);
        }
        else
            configElement.set(owningScreen.currentValue);
    }

    public static class ListEntry implements IGuiSelectStringListEntry
    {
        protected final GuiSelectStringEntries owningList;
        protected final Entry<Object, String> value;

        public ListEntry(GuiSelectStringEntries owningList, Entry<Object, String> value)
        {
            this.owningList = owningList;
            this.value = value;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            owningList.mc.fontRendererObj.drawString(value.getValue(), x + 1, y, slotIndex == owningList.selectedIndex ? 16777215 : 14737632);
        }

        @Override
        public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            return false;
        }

        @Override
        public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {}

        @Override
        public Object getValue()
        {
            return value.getKey();
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_){}
    }

    public static interface IGuiSelectStringListEntry extends GuiListExtended.IGuiListEntry
    {
        public Object getValue();
    }
}
