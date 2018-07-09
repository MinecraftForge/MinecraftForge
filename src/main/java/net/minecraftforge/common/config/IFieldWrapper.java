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

package net.minecraftforge.common.config;

/**
 * The objects are expected to get their wrapped field, the owning class, instance and category string on initialization.
 * In general: The key is the fully qualified property name, where each subcategory is appended with a dot.
 * i.e: general.map.isPresent
 */
public interface IFieldWrapper
{

    /**
     * @return The type adapter to serialize the values returned by getValue. Null if non-primitive.
     */
    ITypeAdapter getTypeAdapter();
    
    /**
     * @return a list of fully qualified property keys handled by this field
     */
    String[] getKeys();

    /**
     * @param key the fully qualified property key
     * @return the value the wrapped field associates with the given key
     */
    Object getValue(String key);

    /**
     * @param key the fully qualified property key
     * @param value the target value of the property associated with the key
     */
    void setValue(String key, Object value);

    /**
     * @param key a fully qualified property key
     * @return true if the wrapped field contains a property associated with the given key
     */
    boolean hasKey(String key);

    /**
     * @param key a fully qualified property key
     * @return true if the wrapped field can save information associated with the given key, false otherwise
     */
    boolean handlesKey(String key);

    /**
     * @param cfg The configuration object holding the properties
     * @param desc The properties description
     * @param langKey The languageKey of the property, used in GUI
     * @param reqMCRestart True, if a change in this property requires a restart of Minecraft
     * @param reqWorldRestart True, if the world needs to be reloaded after changes to this property
     */
    void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart);
    
    /**
     * i.e. general.map in the example above
     * @return the category name in which the entries should be saved. This includes the parent categories
     */
    String getCategory();
}
