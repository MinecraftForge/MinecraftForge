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

import net.minecraftforge.common.config.Property.Type;

/**
 * Abstracts the types of properties away. Higher level logic must prevent invalid data types.
 */
interface ITypeAdapter
{
    /**
     * Assigns the default value to the property
     * @param property the property whose default value will be assigned
     * @param value the default value
     */
    void setDefaultValue(Property property, Object value);

    /**
     * Sets the properties value.
     * @param property the property whose value will be set
     * @param value the set value
     */
	void setValue(Property property, Object value);

    /**
     * Retrieves the properties value
     * @param prop the property whose value will be retrieved
     * @return the properties value
     */
	Object getValue(Property prop);

    Type getType();

    boolean isArrayAdapter();
}
