/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common.extensions;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IForgeWorld extends ICapabilityProvider
{
    /**
     * The maximum radius to scan for entities when trying to check bounding boxes. Vanilla's default is
     * 2.0D But mods that add larger entities may increase this.
     */
    public double getMaxEntityRadius();
    /**
     * Increases the max entity radius, this is safe to call with any value.
     * The setter will verify the input value is larger then the current setting.
     *
     * @param value New max radius to set.
     * @return The new max radius
     */
    public double increaseMaxEntityRadius(double value);
}
