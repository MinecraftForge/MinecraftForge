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

package net.minecraftforge.client.extensions;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;

import javax.annotation.Nullable;

public interface IForgeGeneratorType
{
    /**
     * Override to specify that this GeneratorType provides a Screen for editing its settings.
     * @return True if this GeneratorType provides a Screen for editing its settings.
     */
    default boolean hasEditScreen()
    {
        return false;
    }

    /**
     * Override to provide a Screen for editing this GeneratorType's settings.
     * @return A factory for creating new instances of this GeneratorType's settings editor Screen or null.
     */
    @Nullable
    default BiomeGeneratorTypeScreens.IFactory getEditScreenFactory()
    {
        return null;
    }
}
