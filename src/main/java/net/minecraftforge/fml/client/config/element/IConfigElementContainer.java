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
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A config value that can be saved and loaded.
 *
 * @param <T> Type type of the value (e.g Boolean/Float)
 * @author Cadiboo
 */
public interface IConfigElementContainer<T> {

	/**
	 * @return The value of this element before any changes were been made to it by the user.
	 */
	T getInitialValue();

	/**
	 * @return The {@link ModConfig}.
	 */
	ModConfig getModConfig();

	/**
	 * @return The {@link ValueSpec}.
	 */
	ValueSpec getValueSpec();

	/**
	 * @return The {@link ConfigValue}.
	 */
	ConfigValue<T> getConfigValue();

	/**
	 * @return The result of formatting this ValueSpec's translation key.
	 */
	String getLabel();

	/**
	 * @return The current value of this element.
	 */
	T getCurrentValue();

	/**
	 * Sets this config element's current value to the new value and marks
	 * this config value as needing to be saved.
	 *
	 * @param newValue The new value
	 */
	void setCurrentValue(T newValue);

	/**
	 * @return The default value of this element.
	 */
	T getDefaultValue();

	/**
	 * @return If this config element has been changed but not saved.
	 */
	boolean isDirty();

	/**
	 * Sets this config element's current value to its initial value.
	 */
	default void undoChanges() {
		setCurrentValue(getInitialValue());
	}

	/**
	 * @return If this config element's current value is NOT equal to its initial value.
	 */
	default boolean isChanged() {
		return !Objects.equals(getCurrentValue(), getInitialValue());
	}

	/**
	 * Sets this config element's current value to its default value.
	 */
	default void resetToDefault() {
		setCurrentValue(getDefaultValue());
	}

	/**
	 * @return If this config element's current value is equal to its default value.
	 */
	default boolean isDefault() {
		return Objects.equals(getCurrentValue(), getDefaultValue());
	}

	/**
	 * Saves the value if it has changed and has not yet been saved.
	 */
	void save();

	/**
	 * For Categories return false if ANY properties in the category are modifiable
	 * while a world is running, true if all are not.
	 *
	 * @return Whether or not this config element is safe to modify while a world is running.
	 */
	boolean requiresWorldRestart();

	/**
	 * @return If this config element requires Minecraft to be restarted when it is changed.
	 */
	boolean requiresGameRestart();

	/**
	 * @return The comment for this config element.
	 */
	String getComment();

	/**
	 * @return The comment for this config element.
	 */
	String getTranslationKey();

	/**
	 * @param o The object to test
	 * @return If the object is a valid object that can be used
	 */
	boolean isValid(Object o);

	/**
	 * @return The {@link Range} for this element ONLY if it is a number.
	 */
	@Nullable
	<V extends Comparable<? super V>> Range<V> getRange();

}
