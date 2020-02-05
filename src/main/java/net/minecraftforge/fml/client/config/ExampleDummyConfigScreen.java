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

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.config.screen.ConfigScreen;
import net.minecraftforge.fml.client.config.element.BooleanConfigElement;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.element.IConfigElementContainer;
import net.minecraftforge.fml.client.config.element.ListConfigElement;
import net.minecraftforge.fml.client.config.element.StringConfigElement;
import net.minecraftforge.fml.client.config.element.category.CategoryElement;
import net.minecraftforge.fml.client.config.element.number.ByteConfigElement;
import net.minecraftforge.fml.client.config.element.number.DoubleConfigElement;
import net.minecraftforge.fml.client.config.element.number.FloatConfigElement;
import net.minecraftforge.fml.client.config.element.number.IntegerConfigElement;
import net.minecraftforge.fml.client.config.element.number.LongConfigElement;
import net.minecraftforge.fml.client.config.element.number.ShortConfigElement;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import net.minecraftforge.fml.client.config.entry.ScreenElementConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.ScreenButton;
import net.minecraftforge.fml.client.config.screen.ElementConfigScreen;
import net.minecraftforge.fml.client.config.screen.ModConfigScreen;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TODO: Is this ded? Where would this be used from?
// This works perfectly when Minecraft#displayGuiScreen(new DummyConfigScreen(currentScreen)) is called.
// It needs translations though
public class ExampleDummyConfigScreen extends ElementConfigScreen {

	private final Random RAND = new Random(0);

	public ExampleDummyConfigScreen(Screen parent) {
		super(new StringTextComponent(I18n.format("fml.config.sample.title")), parent, makeConfigElements());
	}

	/**
	 * Makes a Runnable that will register a config gui factory for the ModContainer.
	 * Intended to be used by the Minecraft mod container
	 *
	 * @param modContainer The ModContainer to register a config gui factory for
	 * @return The runnable
	 */
	public static Runnable makeConfigGuiExtensionPoint(final ModContainer modContainer) {
		return () -> modContainer.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
				() -> (minecraft, screen) -> new ExampleDummyConfigScreen(screen));
	}

	private static List<IConfigElement<?>> makeConfigElements() {
		List<IConfigElement<?>> list = new ArrayList<>();
		List<IConfigElement<List<?>>> listsList = new ArrayList<>();
		List<IConfigElement<String>> stringsList = new ArrayList<>();
		List<IConfigElement<? extends Number>> numbersList = new ArrayList<>();
		Pattern commaDelimitedPattern = Pattern.compile("([A-Za-z]+((,){1}( )*|$))+?");

		// Top Level Settings
		list.add(new BooleanConfigElement(new DummyConfigElementContainer<>("imABoolean", "fml.config.sample.imABoolean", true).gameRestart()));
		list.add(new IntegerConfigElement(new RangedDummyConfigElementContainer<>("imAnInteger", "fml.config.sample.imAnInteger", 42, -1, 256).gameRestart()));
		list.add(new DoubleConfigElement(new RangedDummyConfigElementContainer<>("imADouble", "fml.config.sample.imADouble", 42.4242D, -1.0D, 256.256D).gameRestart()));
		list.add(new StringConfigElement(new DummyConfigElementContainer<>("imAString", "fml.config.sample.imAString", "http://www.montypython.net/scripts/string.php").gameRestart()));

		// Lists category
		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("booleanList", "fml.config.sample.booleanList", Lists.newArrayList(true, false, true, false, true, false, true, false))));
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("booleanListFixed", "fml.config.sample.booleanListFixed", Lists.newArrayList(true, false, true, false, true, false, true, false)))); // TODO: Fixed size
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("booleanListMax", Lists.newArrayList(true, false, true, false, true, false, true, false), "fml.config.sample.booleanListMax", 10)));
		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("doubleList", "fml.config.sample.doubleList", Lists.newArrayList(0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D))));
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("doubleListFixed", Lists.newArrayList(0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D), "fml.config.sample.doubleListFixed", true))); // TODO: Fixed size
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("doubleListMax", Lists.newArrayList(0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D), "fml.config.sample.doubleListMax", 15)));
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("doubleListBounded", Lists.newArrayList(0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D), "fml.config.sample.doubleListBounded", -1.0D, 10.0D)));
		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("integerList", "fml.config.sample.integerList", Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))));
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("integerListFixed", Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), "fml.config.sample.integerListFixed", true))); // TODO: Fixed size
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("integerListMax", Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), "fml.config.sample.integerListMax", 15)));
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("integerListBounded", Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), "fml.config.sample.integerListBounded", -1, 10)));
		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("stringList", "fml.config.sample.stringList", Lists.newArrayList("A", "list", "of", "string", "values"))));
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("stringListFixed", Lists.newArrayList("A", "fixed", "length", "array", "of", "string", "values"), "fml.config.sample.stringListFixed", true))); // TODO: Fixed size
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("stringListMax", Lists.newArrayList("An", "array", "of", "string", "values", "with", "a", "max", "length", "of", "15"), "fml.config.sample.stringListMax", 15)));
//		listsList.add(new ListConfigElement<>(new DummyConfigElementContainer<>("stringListPattern", Lists.newArrayList("Valid", "Not Valid", "Is, Valid", "Comma, Separated, Value"), "fml.config.sample.stringListPattern", commaDelimitedPattern)));
		list.add(new DummyListCategoryElement<>("lists", "fml.config.sample.ctgy.lists", listsList));

		// Strings category
		stringsList.add(new StringConfigElement(new DummyConfigElementContainer<>("basicString", "fml.config.sample.basicString", "Just a regular String value, anything goes.")));
//		stringsList.add(new StringConfigElement(new DummyConfigElementContainer<>("cycleString", "fml.config.sample.cycleString","this", Lists.newArrayList("this", "property", "cycles", "through", "a", "list", "of", "valid", "choices"))));
		stringsList.add(new StringConfigElement(new DummyConfigElementContainer<String>("patternString", "fml.config.sample.patternString", "only, comma, separated, words, can, be, entered, in, this, box") {
			@Override
			public boolean isValid(final Object o) {
				return o instanceof String && commaDelimitedPattern.asPredicate().test((String) o);
			}
		}));
//		stringsList.add(new StringConfigElement(new DummyConfigElementContainer<>("chatColorPicker", "fml.config.sample.chatColorPicker","c", Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"))));
		stringsList.add(new StringConfigElement(new DummyConfigElementContainer<String>("modIDSelector", "fml.config.sample.modIDSelector", "forge") {
			@Override
			public boolean isValid(final Object o) {
				return o instanceof String && ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toSet()).contains(o);
			}
		}));
		list.add(new DummyListCategoryElement<>("strings", "fml.config.sample.ctgy.strings", stringsList));

		// Numbers category
		numbersList.add(new ByteConfigElement(new DummyConfigElementContainer<>("basicByte", "fml.config.sample.basicByte", (byte) 42)));
		numbersList.add(new ByteConfigElement(new RangedDummyConfigElementContainer<>("boundedByte", "fml.config.sample.boundedByte", (byte) 42, (byte) -1, (byte) 128)));
		numbersList.add(new ByteConfigElement(new RangedDummyConfigElementContainer<>("sliderByte", "fml.config.sample.sliderByte", (byte) 20, (byte) -120, (byte) 120)) {
			@Override
			public boolean hasSlidingControl() {
				return true;
			}
		});
		numbersList.add(new ShortConfigElement(new DummyConfigElementContainer<>("basicShort", "fml.config.sample.basicShort", (short) 42)));
		numbersList.add(new ShortConfigElement(new RangedDummyConfigElementContainer<>("boundedShort", "fml.config.sample.boundedShort", (short) 42, (short) -1, (short) 256)));
		numbersList.add(new ShortConfigElement(new RangedDummyConfigElementContainer<>("sliderShort", "fml.config.sample.sliderShort", (short) 200, (short) 10, (short) 1000)) {
			@Override
			public boolean hasSlidingControl() {
				return true;
			}
		});
		numbersList.add(new IntegerConfigElement(new DummyConfigElementContainer<>("basicInteger", "fml.config.sample.basicInteger", 42)));
		numbersList.add(new IntegerConfigElement(new RangedDummyConfigElementContainer<>("boundedInteger", "fml.config.sample.boundedInteger", 42, -1, 256)));
		numbersList.add(new IntegerConfigElement(new RangedDummyConfigElementContainer<>("sliderInteger", "fml.config.sample.sliderInteger", 2000, 100, 10000)) {
			@Override
			public boolean hasSlidingControl() {
				return true;
			}
		});
		numbersList.add(new FloatConfigElement(new DummyConfigElementContainer<>("basicFloat", "fml.config.sample.basicFloat", 42.42F)));
		numbersList.add(new FloatConfigElement(new RangedDummyConfigElementContainer<>("boundedFloat", "fml.config.sample.boundedFloat", 42.42F, -1.0F, 256.256F)));
		numbersList.add(new FloatConfigElement(new RangedDummyConfigElementContainer<>("sliderFloat", "fml.config.sample.sliderFloat", 2000.0F, 100.0F, 10000.0F)) {
			@Override
			public boolean hasSlidingControl() {
				return true;
			}
		});
		numbersList.add(new DoubleConfigElement(new DummyConfigElementContainer<>("basicDouble", "fml.config.sample.basicDouble", 42.4242D)));
		numbersList.add(new DoubleConfigElement(new RangedDummyConfigElementContainer<>("boundedDouble", "fml.config.sample.boundedDouble", 42.4242D, -1.0D, 256.256D)));
		numbersList.add(new DoubleConfigElement(new RangedDummyConfigElementContainer<>("sliderDouble", "fml.config.sample.sliderDouble", 42.4242D, -1.0D, 256.256D)) {
			@Override
			public boolean hasSlidingControl() {
				return true;
			}
		});
		numbersList.add(new LongConfigElement(new DummyConfigElementContainer<>("basicLong", "fml.config.sample.basicLong", 42L)));
		numbersList.add(new LongConfigElement(new RangedDummyConfigElementContainer<>("boundedLong", "fml.config.sample.boundedLong", 42L, -1L, 256L)));
		numbersList.add(new LongConfigElement(new RangedDummyConfigElementContainer<>("sliderLong", "fml.config.sample.sliderLong", 2000L, 100L, 10000L)) {
			@Override
			public boolean hasSlidingControl() {
				return true;
			}
		});
		list.add(new DummyListCategoryElement<>("numbers", "fml.config.sample.ctgy.numbers", numbersList));

		return list;
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		RAND.setSeed(System.currentTimeMillis() / 100);
		getEntryList().getListEntries().forEach(o -> {
			final Widget widget = o.getWidget();
			final int color = Color.HSBtoRGB(RAND.nextFloat(), 1.0F, 1.0F);
			if (o.isCategory() || widget instanceof ScreenButton) {
				widget.setFGColor(color);
			} else if (widget instanceof TextFieldWidget) {
				((TextFieldWidget) widget).setTextColor(color);
			}
		});
		super.render(mouseX, mouseY, partialTicks);
	}

	private static class DummyConfigElementContainer<T> implements IConfigElementContainer<T> {

		private final String label;
		private final String translationKey;
		private final String comment;
		private final T defaultValue;
		private final T initialValue;
		private T currentValue;
		private boolean worldRestart;
		private boolean gameRestart;
		private boolean dirty;

		public DummyConfigElementContainer(final String label, final String translationKey, final T defaultValue) {
			this(label, translationKey, label, defaultValue, defaultValue);
		}

		public DummyConfigElementContainer(final String label, final String translationKey, final T defaultValue, final T initialValue) {
			this(label, translationKey, label, defaultValue, initialValue);
		}

		public DummyConfigElementContainer(final String label, final String translationKey, final String comment, final T defaultValue, final T initialValue) {
			this.label = label;
			this.translationKey = translationKey;
			this.comment = comment;
			this.defaultValue = defaultValue;
			this.currentValue = this.initialValue = initialValue;
		}

		public DummyConfigElementContainer<T> worldRestart() {
			this.worldRestart = true;
			return this;
		}

		public DummyConfigElementContainer<T> gameRestart() {
			this.gameRestart = true;
			return this;
		}

		@Override
		public T getInitialValue() {
			return initialValue;
		}

		@Override
		public ModConfig getModConfig() {
			return null;
		}

		@Override
		public ValueSpec getValueSpec() {
			return null;
		}

		@Override
		public ConfigValue<T> getConfigValue() {
			return null;
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
			currentValue = newValue;
			dirty = true;
		}

		@Override
		public T getDefaultValue() {
			return defaultValue;
		}

		@Override
		public boolean isDirty() {
			return dirty;
		}

		@Override
		public void save() {
		}

		@Override
		public boolean requiresWorldRestart() {
			return worldRestart;
		}

		@Override
		public boolean requiresGameRestart() {
			return gameRestart;
		}

		@Override
		public String getComment() {
			return comment;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}

		@Override
		public boolean isValid(final Object o) {
			return true;
		}

		@Nullable
		@Override
		public <V extends Comparable<? super V>> Range<V> getRange() {
			return null;
		}

	}

	private static class RangedDummyConfigElementContainer<T extends Number & Comparable<? super T>> extends DummyConfigElementContainer<T> {

		private final Range<T> range;

		public RangedDummyConfigElementContainer(final String label, final String translationKey, final T value, final T min, final T max) {
			super(label, translationKey, value);
			this.range = new Range<>((Class<T>) value.getClass(), min, max);
		}

		@Nullable
		@Override
		public Range<T> getRange() {
			return range;
		}

	}

	private static class DummyListCategoryElement<T extends List<? extends IConfigElement<?>>> extends CategoryElement<T> {

		private final String label;
		private final String translationKey;
		private final T elements;

		public DummyListCategoryElement(final String label, final String translationKey, final T elements) {
			this.label = label;
			this.translationKey = translationKey;
			this.elements = elements;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}

		@Nullable
		@Override
		public String getComment() {
			return null;
		}

		@Override
		public T getDefault() {
			return elements;
		}

		@Override
		public T get() {
			return elements;
		}

		@Override
		public ConfigListEntry<T> makeConfigListEntry(final ConfigScreen configScreen) {
			final IConfigListEntryWidget.Callback<T> callback = new IConfigListEntryWidget.Callback<>(this::get, this::set, this::getDefault, this::isDefault, this::resetToDefault, this::isChanged, this::undoChanges, this::isValid, this::save);
			final ScreenButton<T> widget = new ScreenButton<>(getLabel(), callback, makeScreen(configScreen));
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
		public List<? extends IConfigElement<?>> getConfigElements() {
			return elements;
		}

	}

}
