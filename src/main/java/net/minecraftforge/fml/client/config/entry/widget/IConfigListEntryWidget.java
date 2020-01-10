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

package net.minecraftforge.fml.client.config.entry.widget;

import net.minecraft.client.gui.widget.TextFieldWidget;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Provides an interface for defining Widgets that are backed by actual values.
 * <p>
 * It's also a hacky use of inheritance to delegate everything to the callback.
 *
 * @param <T> Type type of the value (e.g Boolean/Float)
 * @author Cadiboo
 */
public interface IConfigListEntryWidget<T> {

	Callback<T> getCallback();

	/**
	 * Calls {@link TextFieldWidget#tick()} if this is a TextFieldWidget.
	 */
	default void tick() {
	}

	/**
	 * Handles rendering any tooltips that apply to this widget.
	 * Only called for visible widgets.
	 */
	default void renderToolTip(int mouseX, int mouseY, float partialTicks) {
	}

	/**
	 * By default delegates to the callback.
	 *
	 * @return The default value
	 */
	default T getDefault() {
		return getCallback().getDefault();
	}

	/**
	 * By default delegates to the callback.
	 *
	 * @return If this value is equal to the default value
	 */
	default boolean isDefault() {
		return getCallback().isDefault();
	}

	/**
	 * By default delegates to the callback.
	 * <p>
	 * Sets this value to the default value.
	 */
	default void resetToDefault() {
		getCallback().resetToDefault();
		updateWidgetValue();
	}

	/**
	 * By default delegates to the callback.
	 *
	 * @return If this value is different from the initial value
	 */
	default boolean isChanged() {
		return getCallback().isChanged();
	}

	/**
	 * By default delegates to the callback.
	 * <p>
	 * Sets this value to the initial value.
	 */
	default void undoChanges() {
		getCallback().undoChanges();
		updateWidgetValue();
	}

	/**
	 * By default delegates to the callback.
	 * <p>
	 * Handles saving any changes that have been made to this entry back to the underlying object.
	 * It is a good practice to check isChanged() before performing the save action.
	 */
	default void save() {
		getCallback().save();
	}

	/**
	 * By default delegates to the callback.
	 *
	 * @return If the value this backs is a valid value AND the displayed value is a valid value.
	 */
	default boolean isValid() {
		return getCallback().isValid() && isWidgetValueValid();
	}

	/**
	 * For example "Hello, World" is not a valid value for a widget that is backed by a number.
	 *
	 * @return If the value of the widget is a valid value
	 */
	boolean isWidgetValueValid();

	/**
	 * Updates the widgets value to reflect the stored value.
	 */
	void updateWidgetValue();

	/**
	 * @param <T> Type type of the value (e.g Boolean/Float)
	 * @author Cadiboo
	 */
	class Callback<T> {

		private final Supplier<T> getter;
		private final Consumer<T> setter;
		private final Supplier<T> defaultValueGetter;
		private final BooleanSupplier isDefault;
		private final Runnable resetToDefault;
		private final BooleanSupplier isChanged;
		private final Runnable undoChanges;
		private final Predicate<Object> isValid;
		@Nullable
		private final Runnable save;

		public Callback(final Supplier<T> getter, final Consumer<T> setter, final Supplier<T> defaultValueGetter, final BooleanSupplier isDefault, final Runnable resetToDefault, final BooleanSupplier isChanged, final Runnable undoChanges, final Predicate<Object> isValid) {
			this(getter, setter, defaultValueGetter, isDefault, resetToDefault, isChanged, undoChanges, isValid, null);
		}

		public Callback(final Supplier<T> getter, final Consumer<T> setter, final Supplier<T> defaultValueGetter, final BooleanSupplier isDefault, final Runnable resetToDefault, final BooleanSupplier isChanged, final Runnable undoChanges, final Predicate<Object> isValid, @Nullable final Runnable save) {
			this.getter = getter;
			this.setter = setter;
			this.defaultValueGetter = defaultValueGetter;
			this.isDefault = isDefault;
			this.resetToDefault = resetToDefault;
			this.isChanged = isChanged;
			this.undoChanges = undoChanges;
			this.isValid = isValid;
			this.save = save;
		}

		public T get() {
			return getter.get();
		}

		public void set(T newValue) {
			setter.accept(newValue);
		}

		public T getDefault() {
			return defaultValueGetter.get();
		}

		public boolean isDefault() {
			return isDefault.getAsBoolean();
		}

		public void resetToDefault() {
			resetToDefault.run();
		}

		public void save() {
			if (save != null)
				save.run();
		}

		public boolean isChanged() {
			return isChanged.getAsBoolean();
		}

		public void undoChanges() {
			undoChanges.run();
		}

		public boolean isValid() {
			final T o = get();
			return isValid(o);
		}

		public boolean isValid(final Object o) {
			return isValid.test(o);
		}

	}

}
