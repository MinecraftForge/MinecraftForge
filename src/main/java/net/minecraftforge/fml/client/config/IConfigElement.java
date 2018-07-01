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

package net.minecraftforge.fml.client.config;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;

/**
 * This interface provides the information needed by GuiConfig and GuiConfigEntries to display config elements for editing.
 * 
 * @author bspkrs
 */
public interface IConfigElement
{
    /**
     * [Property, Category] Is this object a property object?
     */
    boolean isProperty();
    
    /**
     * This method returns a class that implements {@link IConfigEntry} or null. This class MUST
     * provide a constructor with the following parameter types: {@link GuiConfig}, {@link GuiConfigEntries}, {@link IConfigElement}
     *
     * @see GuiConfigEntries.ListEntryBase
     * @see GuiConfigEntries.StringEntry
     * @see GuiConfigEntries.BooleanEntry
     * @see GuiConfigEntries.DoubleEntry
     * @see GuiConfigEntries.IntegerEntry
     */
    Class<? extends IConfigEntry> getConfigEntryClass();
    
    /**
     * This method returns a class that implements {@link IArrayEntry}. This class MUST provide a constructor with the
     * following parameter types: {@link GuiEditArray}, {@link GuiEditArrayEntries}, {@link IConfigElement}, {@link Object}
     *
     * @see GuiEditArrayEntries.BaseEntry
     * @see GuiEditArrayEntries.StringEntry
     * @see GuiEditArrayEntries.BooleanEntry
     * @see GuiEditArrayEntries.DoubleEntry
     * @see GuiEditArrayEntries.IntegerEntry
     */
    Class<? extends IArrayEntry> getArrayEntryClass();
    
    /**
     * [Property, Category] Gets the name of this object.
     */
    String getName();
    
    /**
     * [Category] Gets the qualified name of this object. This is typically only used for category objects.
     */
    String getQualifiedName();
    
    /**
     * [Property, Category] Gets a language key for localization of config GUI entry names. If the same key is specified with .tooltip
     * appended to the end, that key will return a localized tooltip when the mouse hovers over the property label/category button.
     */
    String getLanguageKey();
    
    /**
     * [Property, Category] Gets the comment for this object. Used for the tooltip if getLanguageKey() + ".tooltip" is not defined in the
     * .lang file.
     */
    String getComment();
    
    /**
     * [Category] Gets this category's child categories/properties.
     */
    List<IConfigElement> getChildElements();
    
    /**
     * [Property, Category] Gets the ConfigGuiType value corresponding to the type of this property object, or CONFIG_CATEGORY if this is a
     * category object.
     */
    ConfigGuiType getType();
    
    /**
     * [Property] Is this property object a list?
     */
    boolean isList();
    
    /**
     * [Property] Does this list property have to remain a fixed length?
     */
    boolean isListLengthFixed();
    
    /**
     * [Property] Gets the max length of this list property, or -1 if the length is unlimited.
     */
    int getMaxListLength();
    
    /**
     * [Property] Is this property value equal to the default value?
     */
    boolean isDefault();
    
    /**
     * [Property] Gets this property's default value. If this element is an array, this method should return a String
     * representation of that array using Arrays.toString()
     */
    Object getDefault();
    
    /**
     * [Property] Gets this property's default values.
     */
    Object[] getDefaults();
    
    /**
     * [Property] Sets this property's value to the default value.
     */
    void setToDefault();
    
    /**
     * [Property, Category] Whether or not this element is safe to modify while a world is running. For Categories return false if ANY properties
     * in the category are modifiable while a world is running, true if all are not.
     */
    boolean requiresWorldRestart();
    
    /**
     * [Property, Category] Whether or not this element should be allowed to show on config GUIs.
     */
    boolean showInGui();
    
    /**
     * [Property, Category] Whether or not this element requires Minecraft to be restarted when changed.
     */
    boolean requiresMcRestart();
    
    /**
     * [Property] Gets this property value.
     */
    Object get();
    
    /**
     * [Property] Gets this property value as a list. Generally you should be sure of whether the property is a list before calling this.
     */
    Object[] getList();
    
    /**
     * [Property] Sets this property's value.
     */
    void set(Object value);
    
    /**
     * [Property] Sets this property's value to the specified array.
     */
    void set(Object[] aVal);
    
    /**
     * [Property] Gets a String array of valid values for this property. This is generally used for String properties to allow the user to
     * select a value from a list of valid values.
     */
    String[] getValidValues();
    
    /**
     * [Property] Gets this property's minimum value.
     */
    Object getMinValue();
    
    /**
     * [Property] Gets this property's maximum value.
     */
    Object getMaxValue();
    
    /**
     * [Property] Gets a Pattern object used in String property input validation.
     */
    Pattern getValidationPattern();
}
