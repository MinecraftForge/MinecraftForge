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

package net.minecraftforge.fml.client.config.element.category;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A category element represents a {@link ModConfig} or a {@link Config}.
 * It is not backed by a ConfigValue and is a bit of a hack
 * and bridge between NightConfig and Forge's config spec system.
 * See the package-info.java for more information.
 *
 * @author Cadiboo
 */
public abstract class CategoryElement<T> implements IConfigElement<T> {

	@Override
	public boolean isDefault() {
		for (final IConfigElement<?> configElement : getConfigElements())
			if (!configElement.isDefault())
				return false;
		return true;
	}

	@Override
	public void resetToDefault() {
		for (final IConfigElement<?> configElement : getConfigElements())
			configElement.resetToDefault();
	}

	@Override
	public boolean isChanged() {
		for (final IConfigElement<?> configElement : getConfigElements())
			if (configElement.isChanged())
				return true;
		return false;
	}

	@Override
	public void undoChanges() {
		for (final IConfigElement<?> configElement : getConfigElements())
			configElement.undoChanges();
	}

	@Override
	public boolean requiresWorldRestart() {
		for (final IConfigElement<?> configElement : getConfigElements())
			if (configElement.requiresWorldRestart())
				return true;
		return false;
	}

	@Override
	public boolean requiresGameRestart() {
		for (final IConfigElement<?> configElement : getConfigElements())
			if (configElement.requiresGameRestart())
				return true;
		return false;
	}

	@Override
	public void set(final T value) {
		// No op
	}

	@Override
	public void save() {
		for (final IConfigElement<?> configElement : getConfigElements())
			configElement.save();
	}

	@Override
	public boolean isValid(final Object o) {
		for (final IConfigElement configElement : getConfigElements())
			if (!configElement.isValid(configElement.get()))
				return false;
		return true;
	}

	@Override
	public boolean isCategory() {
		return true;
	}

	@Nullable
	@Override
	public Range<?> getRange() {
		return null;
	}

	@Nonnull
	public abstract List<? extends IConfigElement<?>> getConfigElements();

}
