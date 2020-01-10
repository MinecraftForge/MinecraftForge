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

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import joptsimple.internal.Strings;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.ConfigTypesManager;
import net.minecraftforge.fml.client.config.ElementConfigScreen;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import net.minecraftforge.fml.client.config.entry.ScreenElementConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.ScreenButton;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraftforge.common.ForgeConfigSpec.DOT_JOINER;

/**
 * Element for a SubCategory (as opposed to a ConfigValue<Config>).
 * See the package-info.java for more information.
 *
 * @author Cadiboo
 */
public class ConfigCategoryElement extends CategoryElement<Config> {

	private final Config config;
	private final String path;
	private final String label;
	private final String translationKey;
	private final String comment;
	private final List<IConfigElement<?>> elements;

	public ConfigCategoryElement(final Config config, final ModConfig modConfig, final CommentedConfig parentConfig, final String path) {
		this.config = config;
		this.path = path;

		final List<String> pathList = ForgeConfigSpec.split(path);
		final String name = pathList.get(pathList.size() - 1);

		String translationKey = ""; // TODO: No clue how to get this for categories. Doesn't seem to exist currently?
		String label = I18n.format(translationKey);
		if (Objects.equals(translationKey, label))
			label = path;
		String comment = parentConfig == null ? "" : parentConfig.getComment(name);
		if (Strings.isNullOrEmpty(comment))
			comment = "";
		this.label = label;
		this.translationKey = translationKey;
		this.comment = comment;
		this.elements = this.makeChildElementsList(config, modConfig);
	}

	@Nonnull
	public List<IConfigElement<?>> getConfigElements() {
		return elements;
	}

	protected List<IConfigElement<?>> makeChildElementsList(final Config config, final ModConfig modConfig) {
		final List<IConfigElement<?>> list = new ArrayList<>();
		// obj will always be a ConfigValue or a Config object
		config.valueMap().forEach((name, obj) -> list.add(ConfigTypesManager.makeConfigElement(modConfig, DOT_JOINER.join(path, name), obj)));
		ConfigTypesManager.sortElements(list);
		return list;
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
		return comment;
	}

	@Override
	public Config getDefault() {
		return config;
	}

	@Override
	public Config get() {
		return config;
	}

	@Override
	public ConfigListEntry<Config> makeConfigListEntry(final ConfigScreen configScreen) {
		final IConfigListEntryWidget.Callback<Config> callback = new IConfigListEntryWidget.Callback<>(this::get, this::set, this::getDefault, this::isDefault, this::resetToDefault, this::isChanged, this::undoChanges, this::isValid, this::save);
		final ScreenButton<Config> widget = new ScreenButton<>(getLabel(), callback, makeScreen(configScreen));
		return new ScreenElementConfigListEntry<>(configScreen, widget, this);
	}

	protected ConfigScreen makeScreen(final ConfigScreen owningScreen) {
		final ConfigScreen configScreen = new ElementConfigScreen(owningScreen.getTitle(), owningScreen, getConfigElements());
		final ITextComponent subtitle;
		if (owningScreen.getSubtitle() == null)
			subtitle = new StringTextComponent(getLabel());
		else
			subtitle = owningScreen.getSubtitle().deepCopy().appendSibling(new StringTextComponent(ConfigScreen.CATEGORY_DIVIDER + getLabel()));
		configScreen.setSubtitle(subtitle);
		return configScreen;
	}

}
