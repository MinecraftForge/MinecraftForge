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

import net.minecraftforge.common.ForgeConfigSpec.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementation of IConfigElement.
 * Pipes all calls to its {@link IConfigElementContainer}.
 *
 * @param <T> Type type of the element (e.g Boolean/Float)
 * @author Cadiboo
 */
public abstract class ConfigElement<T> implements IConfigElement<T> {

	@Nonnull
	private final IConfigElementContainer<T> configElementContainer;

	public ConfigElement(@Nonnull final IConfigElementContainer<T> configElementContainer) {
		this.configElementContainer = configElementContainer;
	}

	@Nonnull
	public IConfigElementContainer<T> getConfigElementContainer() {
		return configElementContainer;
	}

	@Override
	public String getLabel() {
		return getConfigElementContainer().getLabel();
	}

	@Override
	public String getTranslationKey() {
		return getConfigElementContainer().getTranslationKey();
	}

	@Nullable
	@Override
	public String getComment() {
		return getConfigElementContainer().getComment();
	}

	@Override
	public boolean isDefault() {
		return getConfigElementContainer().isDefault();
	}

	@Override
	public T getDefault() {
		return getConfigElementContainer().getDefaultValue();
	}

	@Override
	public void resetToDefault() {
		getConfigElementContainer().resetToDefault();
	}

	@Override
	public boolean isChanged() {
		return getConfigElementContainer().isChanged();
	}

	@Override
	public void undoChanges() {
		getConfigElementContainer().undoChanges();
	}

	@Override
	public boolean requiresWorldRestart() {
		return getConfigElementContainer().requiresWorldRestart();
	}

	@Override
	public boolean requiresGameRestart() {
		return getConfigElementContainer().requiresGameRestart();
	}

	@Override
	public T get() {
		return getConfigElementContainer().getCurrentValue();
	}

	@Override
	public void set(final T value) {
		getConfigElementContainer().setCurrentValue(value);
	}

	@Override
	public void save() {
		getConfigElementContainer().save();
	}

	@Override
	public boolean isValid(final Object o) {
		return getConfigElementContainer().isValid(o);
	}

	@Nullable
	@Override
	public Range<?> getRange() {
		return getConfigElementContainer().getRange();
	}

}
