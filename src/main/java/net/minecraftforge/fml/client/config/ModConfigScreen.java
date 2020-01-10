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

import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.element.category.ModConfigCategoryElement;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A ConfigScreen that displays a list of {@link ModConfigCategoryElement}s for each ModConfig that the mod has.
 *
 * @author Cadiboo
 */
public class ModConfigScreen extends ElementConfigScreen {

	/**
	 * The mod that this config is for.
	 */
	public final ModContainer modContainer;

	public ModConfigScreen(final ITextComponent titleIn, final Screen parentScreen, final ModContainer modContainer) {
		super(titleIn, parentScreen, makeElementsForMod(modContainer));
		this.modContainer = modContainer;
	}

	/**
	 * Makes a Runnable that will register a config gui factory for the ModContainer
	 * BUT ONLY IF a config gui factory does not already exist for the ModContainer.
	 *
	 * @param modContainer The ModContainer to possibly register a config gui factory for
	 * @return The runnable
	 * @see ModContainer#addConfig(ModConfig)
	 */
	@SuppressWarnings("UnstableApiUsage") // Runnables is marked as unstable
	public static Runnable makeConfigGuiExtensionPoint(final ModContainer modContainer) {
		if (modContainer.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY).isPresent())
			return Runnables.doNothing();
		return () -> modContainer.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
				() -> (minecraft, screen) -> new ModConfigScreen(new StringTextComponent(modContainer.getModInfo().getDisplayName()), screen, modContainer));
	}

	public static List<IConfigElement<?>> makeElementsForMod(final ModContainer modContainer) {
		final List<IConfigElement<?>> list = new ArrayList<>();
		for (final ModConfig.Type type : ModConfig.Type.values())
			makeConfigElementForModConfigType(modContainer, type).ifPresent(list::add);
		return list;
	}

	public static Optional<ModConfigCategoryElement> makeConfigElementForModConfigType(final ModContainer modContainer, final ModConfig.Type type) {
		// TODO: @Config classes?
		// Probably don't need special handling for them as they will be gotten from getConfig
		return ConfigTracker.INSTANCE.getConfig(modContainer.getModId(), type)
				.map(ModConfigCategoryElement::new);
	}

}
