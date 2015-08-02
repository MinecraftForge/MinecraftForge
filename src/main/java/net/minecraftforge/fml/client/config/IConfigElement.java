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
    public boolean isProperty();
    
    /**
     * This method returns a class that implements {@code IConfigEntry} or null. This class MUST
     * provide a constructor with the following parameter types: {@code GuiConfig}, {@code GuiConfigEntries}, {@code IConfigElement}
     */
    public Class<? extends IConfigEntry> getConfigEntryClass();
    
    /**
     * This method returns a class that implements {@code IArrayEntry}. This class MUST provide a constructor with the 
     * following parameter types: {@code GuiEditArray}, {@code GuiEditArrayEntries}, {@code IConfigElement}, {@code Object}
     */
    public Class<? extends IArrayEntry> getArrayEntryClass();
    
    /**
     * [Property, Category] Gets the name of this object.
     */
    public String getName();
    
    /**
     * [Category] Gets the qualified name of this object. This is typically only used for category objects.
     */
    public String getQualifiedName();
    
    /**
     * [Property, Category] Gets a language key for localization of config GUI entry names. If the same key is specified with .tooltip
     * appended to the end, that key will return a localized tooltip when the mouse hovers over the property label/category button.
     */
    public String getLanguageKey();
    
    /**
     * [Property, Category] Gets the comment for this object. Used for the tooltip if getLanguageKey() + ".tooltip" is not defined in the
     * .lang file.
     */
    public String getComment();
    
    /**
     * [Category] Gets this category's child categories/properties.
     */
    public List<IConfigElement> getChildElements();
    
    /**
     * [Property, Category] Gets the ConfigGuiType value corresponding to the type of this property object, or CONFIG_CATEGORY if this is a
     * category object.
     */
    public ConfigGuiType getType();
    
    /**
     * [Property] Is this property object a list?
     */
    public boolean isList();
    
    /**
     * [Property] Does this list property have to remain a fixed length?
     */
    public boolean isListLengthFixed();
    
    /**
     * [Property] Gets the max length of this list property, or -1 if the length is unlimited.
     */
    public int getMaxListLength();
    
    /**
     * [Property] Is this property value equal to the default value?
     */
    public boolean isDefault();
    
    /**
     * [Property] Gets this property's default value. If this element is an array, this method should return a String
     * representation of that array using Arrays.toString()
     */
    public Object getDefault();
    
    /**
     * [Property] Gets this property's default values.
     */
    public Object[] getDefaults();
    
    /**
     * [Property] Sets this property's value to the default value.
     */
    public void setToDefault();
    
    /**
     * [Property, Category] Whether or not this element is safe to modify while a world is running. For Categories return false if ANY properties
     * in the category are modifiable while a world is running, true if all are not.
     */
    public boolean requiresWorldRestart();
    
    /**
     * [Property, Category] Whether or not this element should be allowed to show on config GUIs.
     */
    public boolean showInGui();
    
    /**
     * [Property, Category] Whether or not this element requires Minecraft to be restarted when changed.
     */
    public boolean requiresMcRestart();
    
    /**
     * [Property] Gets this property value.
     */
    public Object get();
    
    /**
     * [Property] Gets this property value as a list. Generally you should be sure of whether the property is a list before calling this.
     */
    public Object[] getList();
    
    /**
     * [Property] Sets this property's value.
     */
    public void set(Object value);
    
    /**
     * [Property] Sets this property's value to the specified array.
     */
    public void set(Object[] aVal);
    
    /**
     * [Property] Gets a String array of valid values for this property. This is generally used for String properties to allow the user to
     * select a value from a list of valid values.
     */
    public String[] getValidValues();
    
    /**
     * [Property] Gets this property's minimum value.
     */
    public Object getMinValue();
    
    /**
     * [Property] Gets this property's maximum value.
     */
    public Object getMaxValue();
    
    /**
     * [Property] Gets a Pattern object used in String property input validation.
     */
    public Pattern getValidationPattern();
}
