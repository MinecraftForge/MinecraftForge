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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.element.InfoTextConfigElement;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * A ConfigScreen that displays a list of {@link IConfigElement}s
 *
 * @author bspkrs
 * @author Cadiboo
 */
public class ElementConfigScreen extends ConfigScreen {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * A list of elements on this screen.
	 */
	private final List<? extends IConfigElement<?>> configElements;

	public ElementConfigScreen(final ITextComponent titleIn, final Screen parentScreen, @Nonnull final List<? extends IConfigElement<?>> configElements) {
		super(titleIn, parentScreen);
		this.configElements = configElements;
	}

	@Override
	protected void onDoneButtonClicked(final Button button) {
		boolean canClose = true;
		try {
			final ConfigEntryListWidget entryList = this.getEntryList();
			if (entryList != null && entryList.areAnyEntriesChanged(true)) {
				if (parentScreen != null && parentScreen instanceof ElementConfigScreen) {
					// No op, Only save when the final screen is closed.
				} else {
					entryList.save();
					boolean requiresGameRestart = this.anyRequireGameRestart();
					boolean requiresWorldRestart = this.anyRequireWorldRestart();
					if (requiresGameRestart) {
						canClose = false;
						getMinecraft().displayGuiScreen(new MessageDialogScreen(parentScreen, new TranslationTextComponent("fml.configgui.gameRestartTitle"), new TranslationTextComponent("fml.configgui.gameRestartRequired"), new TranslationTextComponent("fml.configgui.confirmMessage")));
					} else if (requiresWorldRestart && Minecraft.getInstance().world != null) {
						canClose = false;
						getMinecraft().displayGuiScreen(new MessageDialogScreen(parentScreen, new TranslationTextComponent("fml.configgui.worldRestartTitle"), new TranslationTextComponent("fml.configgui.worldRestartRequired"), new TranslationTextComponent("fml.configgui.confirmMessage")));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error performing ConfigScreen action:", e);
		}
		if (canClose)
			this.onClose();
	}

	@Override
	public void init() {
		super.init();
		final ConfigEntryListWidget entryList = Objects.requireNonNull(getEntryList());
		final List<ConfigListEntry<?>> children = entryList.children();
		final List<? extends IConfigElement<?>> configElements = getConfigElements();
		if (configElements.isEmpty()) {
			children.add(new InfoTextConfigElement<>("fml.configgui.noElements")
					.makeConfigListEntry(this));
			return;
		}
		configElements.stream()
				.filter(IConfigElement::showInGui)
				.map(element -> element.makeConfigListEntry(this))
				.map(configListEntry -> Objects.requireNonNull(configListEntry, "ConfigListEntry (Widget)"))
				.forEach(children::add);
	}

	public boolean anyRequireGameRestart() {
		for (IConfigElement<?> entry : this.getConfigElements())
			if (entry.requiresGameRestart())
				return true;
		return false;
	}

	public boolean anyRequireWorldRestart() {
		for (IConfigElement<?> entry : this.getConfigElements())
			if (entry.requiresWorldRestart())
				return true;
		return false;
	}

	public List<? extends IConfigElement<?>> getConfigElements() {
		return configElements;
	}

}
