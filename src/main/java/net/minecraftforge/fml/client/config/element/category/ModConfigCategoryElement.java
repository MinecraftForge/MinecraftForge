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
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.ConfigScreen;
import net.minecraftforge.fml.client.config.ConfigTypesManager;
import net.minecraftforge.fml.client.config.ElementConfigScreen;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import net.minecraftforge.fml.client.config.entry.ScreenElementConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.ScreenButton;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Category element for a {@link ModConfig}.
 * Has some hacks because it is a top level element and isn't actually written to/read from the config.
 * (ModConfig is just a wrapper around the NightConfig library + ForgeConfigSpec).
 * See the package-info.java for more information.
 *
 * @author Cadiboo
 */
public class ModConfigCategoryElement extends CategoryElement<ModConfig> {

	// TODO: Move these to translation keys once Forge's API stabilizes a bit more and the PLAYER config type is implemented.
	@Deprecated
	private static final Map<ModConfig.Type, String> COMMENTS = makeTypeComments();
	private final ModConfig modConfig;
	private final String label;
	private final String translationKey;
	private final String comment;
	private final List<IConfigElement<?>> configElements;

	public ModConfigCategoryElement(final ModConfig modConfig) {
		this.modConfig = modConfig;
		final ModConfig.Type type = modConfig.getType();
		final String str = type.name().toLowerCase();
		this.label = StringUtils.capitalize(str);
		this.translationKey = "fml.configgui.modConfigType." + str;
		String tempComment = COMMENTS.get(type);
		// For Server Configs:
		// ConfigData is null unless we are in world
		// and ConfigData is not an instanceof FileConfig when connected to a multiplayer server.
		final CommentedConfig configData = modConfig.getConfigData();
		if (configData != null && configData instanceof FileConfig)
			tempComment += "\n" + TextFormatting.GRAY + modConfig.getFullPath().toAbsolutePath();
		this.comment = tempComment;
		this.configElements = makeConfigElements(modConfig);
	}

	// TODO: Move these to translation keys once Forge's API stabilizes a bit more and the PLAYER config type is implemented.
	@Deprecated
	@Nonnull
	private static Map<ModConfig.Type, String> makeTypeComments() {
		final Map<ModConfig.Type, String> map = new HashMap<>();

		String commonComment = Strings.join(new String[]{
				"Common config is for configuration that needs to be loaded on both environments.",
				"Loaded on both server and client environments during startup (after registry events and before setup events).",
				"Stored in the global config directory.",
				"Not synced.",
		}, "\n");
		String clientComment = Strings.join(new String[]{
				"Client config is for configuration affecting the ONLY client state such as graphical options.",
				"Loaded on the client environment during startup (after registry events and before setup events).",
				"Stored in the global config directory.",
				"Not synced.",
		}, "\n");
		String playerComment = Strings.join(new String[]{
				"Player config is for configuration that is associated with a player.",
				"Preferences around machine states, for example.",
				"Not Implemented (yet).",
		}, "\n");
		String serverComment = Strings.join(new String[]{
				"Server config is for configuration that is associated with a logical server instance.",
				"Loaded during server startup (right before the FMLServerAboutToStartEvent is fired.)",
				"Stored in a server/save specific \"serverconfig\" directory",
				"Synced to clients during connection.",
		}, "\n");

		serverComment += "\nRequires you to be in your singleplayer world to change its values from the config gui";

		map.put(ModConfig.Type.COMMON, commonComment);
		map.put(ModConfig.Type.CLIENT, clientComment);
//		map.put(ModConfig.Type.PLAYER, playerComment);
		map.put(ModConfig.Type.SERVER, serverComment);
		return map;
	}

	/**
	 * @return True if in singleplayer and not open to LAN
	 */
	public static boolean canPlayerEditServerConfig() {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.getIntegratedServer() == null)
			return false;
		if (!minecraft.isSingleplayer())
			return false;
		return !minecraft.getIntegratedServer().getPublic();
	}

	protected List<IConfigElement<?>> makeConfigElements(final ModConfig modConfig) {
		if (!canDisplay())
			return Lists.newArrayList();
		final List<IConfigElement<?>> configElements = new ArrayList<>();
		// name -> ConfigValue|SimpleConfig
		final Map<String, Object> specConfigValues = ConfigTypesManager.getSpecConfigValues(modConfig);
		specConfigValues.forEach((name, obj) -> configElements.add(ConfigTypesManager.makeConfigElement(modConfig, name, obj)));
		ConfigTypesManager.sortElements(configElements);
		return configElements;
	}

	public boolean canDisplay() {
		if (modConfig.getType() == ModConfig.Type.SERVER)
			return canPlayerEditServerConfig();
		return true;
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
	public ModConfig getDefault() {
		return modConfig;
	}

	@Override
	public ModConfig get() {
		return modConfig;
	}

	@Override
	public ConfigListEntry<ModConfig> makeConfigListEntry(final ConfigScreen configScreen) {
		final IConfigListEntryWidget.Callback<ModConfig> callback = new IConfigListEntryWidget.Callback<>(this::get, this::set, this::getDefault, this::isDefault, this::resetToDefault, this::isChanged, this::undoChanges, this::isValid, this::save);
		final ScreenButton<ModConfig> widget;
		if (canDisplay())
			widget = new ScreenButton<>(getLabel(), callback, makeScreen(configScreen));
		else // Do nothing when pressed & render greyed out.
			widget = new ScreenButton<ModConfig>(getLabel(), callback, b -> {
			}) {
				@Override
				public void render(final int mouseX, final int mouseY, final float partialTicks) {
					this.active = false;
					super.render(mouseX, mouseY, partialTicks);
				}
			};
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

	@Nonnull
	@Override
	public List<IConfigElement<?>> getConfigElements() {
		return configElements;
	}

}
