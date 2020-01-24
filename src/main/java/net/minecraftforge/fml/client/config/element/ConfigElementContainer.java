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

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A config value that can be saved and loaded.
 * A wrapper around a path + {@link ValueSpec} + {@link ModConfig}.
 * Pipes get and save through {@link ConfigValue}.
 *
 * @author Cadiboo
 */
public class ConfigElementContainer<T> implements IConfigElementContainer<T> {

	private final T initialValue;
	private final ModConfig modConfig;
	private final ValueSpec valueSpec;
	private final ConfigValue<T> configValue;
	private final String label;

	private T currentValue;
	private boolean isDirty;

	public ConfigElementContainer(final String path, final ModConfig modConfig, final ConfigValue<T> configValue) {
		this(ForgeConfigSpec.split(path), modConfig, configValue);
	}

	public ConfigElementContainer(final List<String> path, final ModConfig modConfig, final ConfigValue<T> configValue) {
		this.modConfig = modConfig;
		this.valueSpec = modConfig.getSpec().get(path);
		this.configValue = configValue;
		this.initialValue = this.configValue.get();
		this.label = I18n.format(valueSpec.getTranslationKey());

		this.currentValue = this.initialValue;
		this.isDirty = false;
	}

	@Override
	public T getInitialValue() {
		return initialValue;
	}

	@Override
	public ModConfig getModConfig() {
		return modConfig;
	}

	@Override
	public ValueSpec getValueSpec() {
		return valueSpec;
	}

	@Override
	public ConfigValue<T> getConfigValue() {
		return configValue;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public T getCurrentValue() {
		return currentValue;
	}

	@Override
	public void setCurrentValue(final T newValue) {
		this.currentValue = newValue;
		this.isDirty = true;
	}

	@Override
	public T getDefaultValue() {
		return (T) getValueSpec().getDefault();
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void save() {
		if (!isDirty())
			return;
		getConfigValue().set(this.getCurrentValue());
		// Could be uncommented to make elements save on modification instead of when the Config Screen is closed
		// See ModConfigCategoryElement#save()
		// See usages of ModConfigEvent.Reloading
//		getConfigValue().save();
		this.isDirty = false;
	}

	@Override
	public boolean requiresWorldRestart() {
		return getValueSpec().needsWorldRestart();
	}

	@Override
	public boolean requiresGameRestart() {
		// TODO: requiresGameRestart appears not to exist anymore?
//		return getValueSpec().needsGameRestart();
		return false;
	}

	@Override
	public String getComment() {
		return getValueSpec().getComment();
	}

	@Override
	public String getTranslationKey() {
		return getValueSpec().getTranslationKey();
	}

	@Override
	public boolean isValid(final Object o) {
		return getValueSpec().test(o);
	}

	@Override
	@Nullable
	public <V extends Comparable<? super V>> Range<V> getRange() {
		final ValueSpec valueSpec = getValueSpec();
		if (!Number.class.isAssignableFrom(valueSpec.getClazz()))
			return null;
		return valueSpec.getRange();
	}

}
