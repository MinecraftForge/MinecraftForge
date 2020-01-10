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

package net.minecraftforge.fml.client.config;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.entry.ConfigConfigListEntry;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.InfoText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A ConfigScreen that displays an editable visual representation of a {@link Config}.
 *
 * @author Cadiboo
 */
public class ConfigConfigScreen extends ConfigScreen {

	private final IConfigListEntryWidget.Callback<UnmodifiableConfig> callback;

	private final boolean isUnmodifiable;

	public ConfigConfigScreen(final ConfigScreen owningScreen, final IConfigListEntryWidget.Callback<UnmodifiableConfig> callback) {
		super(owningScreen.getTitle(), owningScreen);
		this.callback = callback;
		this.callback.set(this.cloneConfig(callback.get()));
		this.isUnmodifiable = isUnmodifiable(callback.get());
	}

	public static boolean isUnmodifiable(final UnmodifiableConfig config) {
		// Seems as though the config passed in is never unmodifiable.
		// Makes sense as this is a gui for modifying configs.
		return !(config instanceof Config);
	}

	/**
	 * Clones the Config.
	 * Config are mutable so care must be taken not to modify the original config so that
	 * resetToDefault and undoChanges work properly.
	 * Does NOT return the same object.
	 */
	public UnmodifiableConfig cloneConfig(UnmodifiableConfig toClone) {
		if (toClone instanceof AbstractConfig)
			return ((AbstractConfig) toClone).clone();
		else
			throw new IllegalStateException("Could not clone Config");
	}

	public boolean isUnmodifiable() {
		return isUnmodifiable;
	}

	@Override
	public void init() {
		super.init();

		final UnmodifiableConfig config = callback.get();
		final boolean isUnmodifiable = isUnmodifiable();

		final Map<String, ? extends Widget> widgets = makeWidgets(config, this, o -> callback.isValid());
		if (!isUnmodifiable && widgets.isEmpty()) {
			final Widget w = new InfoText<>(I18n.format("fml.configgui.noElements"));
			this.getEntryList().children().add(new ConfigConfigListEntry(this, w, false));
			return;
		}

		final List<ConfigListEntry<?>> configListEntries = this.getEntryList().children();
		final boolean isModifiable = !isUnmodifiable;
		widgets.forEach((path, w) -> {
			if (isUnmodifiable) { // Disable widgets if unmodifiable
				if (w instanceof TextFieldWidget)
					((TextFieldWidget) w).setEnabled(false);
				w.active = false;
			}
			configListEntries.add(new ConfigConfigListEntry(this, w, isModifiable));
		});
	}

	protected <W extends Widget & IConfigListEntryWidget<?>> Map<String, W> makeWidgets(final UnmodifiableConfig config, final ConfigScreen configScreen, final Predicate<Object> elementValidator) {
		final Map<String, W> elements = new HashMap<>();
		config.valueMap().forEach((path, obj) -> elements.put(path, ConfigTypesManager.makeConfigWidget(config, configScreen, elementValidator, path, obj)));
		return elements;
	}

}
