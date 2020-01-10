/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.client.config.element;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.fml.client.config.ConfigEntryListWidget;
import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import net.minecraftforge.fml.client.config.entry.ElementConfigListEntry;

import javax.annotation.Nullable;

/**
 * Not the actual widget that is displayed on the {@link ConfigEntryListWidget}.
 * Holds the actual value of the {@link ConfigValue}.
 * The widget updates the value by calling these methods.
 *
 * @param <T> Type type of the element (e.g Boolean/Float)
 * @author Cadiboo
 */
public interface IConfigElement<T> {

	/**
	 * @return The result of formatting the translation key
	 */
	String getLabel();

	/**
	 * The translation key for this element is also used as a lookup for the localised comment.
	 * Can return null or empty.
	 *
	 * @return The translation key for this element
	 * @see ElementConfigListEntry#makeTooltip()
	 */
	@Nullable
	String getTranslationKey();

	/**
	 * @return The comment for this element
	 */
	@Nullable
	String getComment();

	/**
	 * @return If this value is equal to the default value
	 */
	boolean isDefault();

	/**
	 * @return The default value
	 */
	T getDefault();

	/**
	 * Sets this value to the default value
	 */
	void resetToDefault();

	/**
	 * Has the value of this element changed?
	 *
	 * @return If changes have been made to this element's value, false otherwise
	 */
	boolean isChanged();

	/**
	 * Handles reverting any changes that have occurred to this element.
	 */
	void undoChanges();

	/**
	 * For Categories/Lists return false if ANY children of the element cannot be
	 * changed while a world is running, true if ALL children can be changed.
	 *
	 * @return If this is requires the world to be restarted when changed
	 */
	boolean requiresWorldRestart();

	/**
	 * For Categories/Lists return false if ANY children of the element cannot be
	 * changed while a world is running, true if ALL children can be changed.
	 *
	 * @return If this is requires Minecraft to be restarted when changed
	 */
	boolean requiresGameRestart();

	/**
	 * @return The value of this element
	 */
	T get();

	/**
	 * Sets the value of this element.
	 */
	void set(T value);

	/**
	 * Handles saving any changes that have been made to this entry back to the underlying object.
	 * It is a good practice to check {@link #isChanged()} before performing the save action.
	 * <p>
	 * Currently only called when the main ConfigScreen is closed, but this could be easily changed
	 * to make config elements save on modification.
	 */
	void save();

	/**
	 * @param o The object to check
	 * @return If the object is valid for this element
	 */
	boolean isValid(T o);

	/**
	 * @return If this should be allowed to show on ConfigScreens
	 */
	default boolean showInGui() {
		return true;
	}

	/**
	 * @return If this element is going to have a slider attached (Only for numbers)
	 */
	default boolean hasSlidingControl() {
		return false;
	}

	/**
	 * @return A new ConfigListEntry backed by this element
	 */
	ConfigListEntry<T> makeConfigListEntry(ConfigScreen configScreen);

	/**
	 * @return If this is backed by a ModConfig or ConfigCategory
	 */
	default boolean isCategory() {
		return false;
	}

	/**
	 * @return The range of this element (Only for numbers)
	 */
	@Nullable
	Range<?> getRange();

}
