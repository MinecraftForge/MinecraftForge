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
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.entry.InfoTextConfigListEntry;

import javax.annotation.Nullable;

/**
 * A dummy IConfigElement that just displays text (Usually error text) and does nothing else.
 *
 * @author Cadiboo
 */
public class InfoTextConfigElement<T> implements IConfigElement<T> {

	private final String label;
	private final String translationKey;

	public InfoTextConfigElement(final String translationKey, final String... translateArgs) {
		this.translationKey = translationKey;
		this.label = I18n.format(translationKey, (Object[]) translateArgs);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}

	@Override
	public String getComment() {
		return label;
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public T getDefault() {
		return null;
	}

	@Override
	public void resetToDefault() {
	}

	@Override
	public boolean isChanged() {
		return false;
	}

	@Override
	public void undoChanges() {
	}

	@Override
	public boolean requiresWorldRestart() {
		return false;
	}

	@Override
	public boolean requiresGameRestart() {
		return false;
	}

	@Override
	public T get() {
		return null;
	}

	@Override
	public void set(final T value) {
	}

	@Override
	public void save() {
	}

	@Override
	public boolean isValid(final T o) {
		return true;
	}

	@Override
	public InfoTextConfigListEntry<T> makeConfigListEntry(final ConfigScreen configScreen) {
		return new InfoTextConfigListEntry<>(configScreen, this.getLabel());
	}

	@Nullable
	@Override
	public Range<?> getRange() {
		return null;
	}

}
