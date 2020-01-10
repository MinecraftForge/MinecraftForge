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

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.config.element.BooleanConfigElement;
import net.minecraftforge.fml.client.config.element.ConfigConfigElement;
import net.minecraftforge.fml.client.config.element.ConfigElementContainer;
import net.minecraftforge.fml.client.config.element.EnumConfigElement;
import net.minecraftforge.fml.client.config.element.IConfigElement;
import net.minecraftforge.fml.client.config.element.IConfigElementContainer;
import net.minecraftforge.fml.client.config.element.InfoTextConfigElement;
import net.minecraftforge.fml.client.config.element.ListConfigElement;
import net.minecraftforge.fml.client.config.element.LocalDateConfigElement;
import net.minecraftforge.fml.client.config.element.LocalDateTimeConfigElement;
import net.minecraftforge.fml.client.config.element.LocalTimeConfigElement;
import net.minecraftforge.fml.client.config.element.OffsetDateTimeConfigElement;
import net.minecraftforge.fml.client.config.element.StringConfigElement;
import net.minecraftforge.fml.client.config.element.category.ConfigCategoryElement;
import net.minecraftforge.fml.client.config.element.number.ByteConfigElement;
import net.minecraftforge.fml.client.config.element.number.DoubleConfigElement;
import net.minecraftforge.fml.client.config.element.number.FloatConfigElement;
import net.minecraftforge.fml.client.config.element.number.IntegerConfigElement;
import net.minecraftforge.fml.client.config.element.number.LongConfigElement;
import net.minecraftforge.fml.client.config.element.number.ShortConfigElement;
import net.minecraftforge.fml.client.config.entry.widget.BooleanButton;
import net.minecraftforge.fml.client.config.entry.widget.ConfigButton;
import net.minecraftforge.fml.client.config.entry.widget.EnumButton;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.InfoText;
import net.minecraftforge.fml.client.config.entry.widget.ListButton;
import net.minecraftforge.fml.client.config.entry.widget.LocalDateTextField;
import net.minecraftforge.fml.client.config.entry.widget.LocalDateTimeTextField;
import net.minecraftforge.fml.client.config.entry.widget.LocalTimeTextField;
import net.minecraftforge.fml.client.config.entry.widget.OffsetDateTimeTextField;
import net.minecraftforge.fml.client.config.entry.widget.StringTextField;
import net.minecraftforge.fml.client.config.entry.widget.number.ByteTextField;
import net.minecraftforge.fml.client.config.entry.widget.number.DoubleTextField;
import net.minecraftforge.fml.client.config.entry.widget.number.FloatTextField;
import net.minecraftforge.fml.client.config.entry.widget.number.IntegerTextField;
import net.minecraftforge.fml.client.config.entry.widget.number.LongTextField;
import net.minecraftforge.fml.client.config.entry.widget.number.ShortTextField;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import static net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

/**
 * Run away, here be dragons.
 * RUN AWAY!!!
 * <p>
 * All the hacks necessary for the ConfigScreen to work.
 * Handles creating {@link IConfigElement}s and {@link IConfigListEntryWidget}s.
 * <p>
 * Lots of unchecked casts, lots of raw uses of generic classes.
 * <p>
 * Also has the method for sorting element lists and widget lists.
 *
 * @author Cadiboo
 */
public class ConfigTypesManager {

	private static final Function<?, ?> NO_FACTORY = o -> null;
	private static final Map<Class<?>, Function<IConfigElementContainer<?>, IConfigElement<?>>> CONFIG_ELEMENTS = new HashMap<>();
	private static final Map<Class<?>, Function<IConfigListEntryWidget.Callback<?>, IConfigListEntryWidget<?>>> WIDGETS = new HashMap<>();
	private static Comparator<IConfigElement<?>> configElementsComparator = null;
	private static Comparator<?> widgetsComparator = null;
	static {
		register();
	}

	private static void register() {
		CONFIG_ELEMENTS.clear();
		registerElementFactory(Boolean.class, BooleanConfigElement::new);
		registerElementFactory(Byte.class, ByteConfigElement::new);
		registerElementFactory(Short.class, ShortConfigElement::new);
		registerElementFactory(Integer.class, IntegerConfigElement::new);
		registerElementFactory(Float.class, FloatConfigElement::new);
		registerElementFactory(Long.class, LongConfigElement::new);
		registerElementFactory(Double.class, DoubleConfigElement::new);
		registerElementFactory(String.class, StringConfigElement::new);
		registerElementFactory(Enum.class, EnumConfigElement::new);
		registerElementFactory(List.class, ListConfigElement::new);
		registerElementFactory(LocalTime.class, LocalTimeConfigElement::new);
		registerElementFactory(LocalDate.class, LocalDateConfigElement::new);
		registerElementFactory(LocalDateTime.class, LocalDateTimeConfigElement::new);
		registerElementFactory(OffsetDateTime.class, OffsetDateTimeConfigElement::new);
		registerElementFactory(UnmodifiableConfig.class, ConfigConfigElement::new);

		WIDGETS.clear();
		registerWidgetFactory(Boolean.class, BooleanButton::new);
		registerWidgetFactory(Byte.class, ByteTextField::new);
		registerWidgetFactory(Short.class, ShortTextField::new);
		registerWidgetFactory(Integer.class, IntegerTextField::new);
		registerWidgetFactory(Float.class, FloatTextField::new);
		registerWidgetFactory(Long.class, LongTextField::new);
		registerWidgetFactory(Double.class, DoubleTextField::new);
		registerWidgetFactory(String.class, StringTextField::new);
		registerWidgetFactory(Enum.class, EnumButton::new);
		registerWidgetFactory(List.class, callback -> new ListButton<>(((ScreenedCallback<?>) callback).screen, callback));
		registerWidgetFactory(LocalTime.class, LocalTimeTextField::new);
		registerWidgetFactory(LocalDate.class, LocalDateTextField::new);
		registerWidgetFactory(LocalDateTime.class, LocalDateTimeTextField::new);
		registerWidgetFactory(OffsetDateTime.class, OffsetDateTimeTextField::new);
		registerWidgetFactory(UnmodifiableConfig.class, callback -> new ConfigButton(((ScreenedCallback<UnmodifiableConfig>) callback).screen, callback));
		// ModConfig should NEVER be an element in a config list.
		registerWidgetFactory(ModConfig.class, $ -> new InfoText<>("fml.configgui.error.unsupportedTypeUseConfig", "ModConfig"));

		// Alphabetical A-Z Sorting
		setElementComparator(Comparator.comparing(IConfigElement::getLabel));

		// No Sorting (Leave list/config items in their default order on screen)
		setWidgetComparator(null);
	}

	/**
	 * Registers a factory for making config elements for the specific class.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <E> void registerElementFactory(final Class<E> clazz, final Function<IConfigElementContainer<E>, IConfigElement<E>> factory) {
		CONFIG_ELEMENTS.put(clazz, (Function) factory);
	}

	/**
	 * Registers a factory for making widgets for the specific class.
	 *
	 * @param <C> The type of the class
	 * @param <W> The type of the widget
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <C, W extends Widget & IConfigListEntryWidget<C>> void registerWidgetFactory(final Class<C> clazz, final Function<IConfigListEntryWidget.Callback<C>, W> factory) {
		WIDGETS.put(clazz, (Function) factory);
	}

	/**
	 * Set the comparator that will be used to sort lists of config elements before displaying them on the screen.
	 *
	 * @param comparator The comparator or null if no sorting should be done
	 */
	public static void setElementComparator(final Comparator<IConfigElement<?>> comparator) {
		configElementsComparator = comparator;
	}

	/**
	 * Set the comparator that will be used to sort lists of widgets before displaying them on the screen.
	 *
	 * @param comparator The comparator or null if no sorting should be done
	 * @param <W>        The type of the widget
	 */
	public static <W extends Widget & IConfigListEntryWidget<?>> void setWidgetComparator(@Nullable final Comparator<W> comparator) {
		widgetsComparator = comparator;
	}

	/**
	 * @param configElements The list of config elements to sort
	 */
	public static void sortElements(final List<IConfigElement<?>> configElements) {
		final Comparator<IConfigElement<?>> comparator = getConfigElementsComparator();
		if (comparator != null)
			configElements.sort(comparator);
	}

	/**
	 * @param widgets The list of widgets to sort
	 * @param <W>     The type of the widget
	 */
	public static <W extends Widget & IConfigListEntryWidget<?>> void sortWidgets(final List<W> widgets) {
		final Comparator<W> comparator = getWidgetsComparator();
		if (comparator != null)
			widgets.sort(comparator);
	}

	/**
	 * @return The comparator for sorting config element lists. Returns null if no sorting should be done.
	 */
	@Nullable
	public static Comparator<IConfigElement<?>> getConfigElementsComparator() {
		return configElementsComparator;
	}

	/**
	 * @param <W> The type of the widget
	 * @return The comparator for sorting widget lists. Returns null if no sorting should be done.
	 */
	@Nullable
	public static <W extends Widget & IConfigListEntryWidget<?>> Comparator<W> getWidgetsComparator() {
		return (Comparator<W>) widgetsComparator;
	}

	/**
	 * @param obj Either a ConfigValue or a Config
	 * @see #getSpecConfigValues(ModConfig)
	 */
	@SuppressWarnings({"unchecked"})
	public static IConfigElement<?> makeConfigElement(final ModConfig modConfig, final String path, final Object obj) {
		if (obj instanceof ConfigValue) {
			final ConfigValue<?> configValue = (ConfigValue<?>) obj;
			// Because the obj is a ConfigValue the corresponding object in the ValueSpec map must be a ValueSpec
			final ValueSpec valueSpec = (ValueSpec) getValueSpec(modConfig, configValue.getPath());

			Class<?> clazz = valueSpec.getClazz();
			if (clazz == Object.class) {
				final Object actualValue = configValue.get();
				final Class<?> valueClass = actualValue.getClass();
				if (valueClass != Object.class)
					clazz = valueClass;
				else {
					final Object defaultValue = valueSpec.getDefault();
					if (defaultValue != null) // Should NEVER happen
						clazz = defaultValue.getClass();
				}
			}

			if (clazz == null || clazz == Object.class)
				return new InfoTextConfigElement<>("fml.configgui.error.nullTypeUseConfig");

			Function<IConfigElementContainer<?>, IConfigElement<?>> factory = recursiveGetFactory(clazz, clazz, (Map) CONFIG_ELEMENTS);
			if (factory != null && factory != NO_FACTORY) {
				try {
					return factory.apply(new ConfigElementContainer(configValue.getPath(), modConfig, configValue));
				} catch (Exception e) {
					e.printStackTrace();
					return new InfoTextConfigElement<>("fml.configgui.error.errorForTypeUseConfig", clazz.getSimpleName());
				}
			} else {
				return new InfoTextConfigElement<>("fml.configgui.error.unsupportedTypeUseConfig", clazz.getSimpleName());
			}
		} else if (obj instanceof Config) { // Subcategory
			final Config config = (Config) obj;
			final List<String> split = ForgeConfigSpec.split(path);
			split.remove(split.size() - 1);
			final @Nullable Object parentConfig;
			if (split.isEmpty())
				parentConfig = modConfig.getConfigData();
			else
				parentConfig = getValue(modConfig, split);
			if (parentConfig instanceof CommentedConfig)
				return new ConfigCategoryElement(config, modConfig, ((CommentedConfig) parentConfig), path);
			else
				return new ConfigCategoryElement(config, modConfig, null, path);
		} else {
			throw new IllegalStateException("How? " + path + ", " + obj);
//			return new InfoTextConfigElement<>("Uh, this is an error... " + path + ", " + obj);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes", "UnstableApiUsage"})
	public static <W extends Widget & IConfigListEntryWidget<?>> W makeListWidget(@Nonnull final List list, final int index, @Nonnull final ConfigScreen configScreen, @Nonnull final Predicate<Object> isValidPredicate, @Nonnull final Object obj) {
		final Class<?> clazz = obj.getClass();

		if (clazz == null || clazz == Object.class)
			return (W) new InfoText("fml.configgui.error.nullTypeUseConfig");

		Function<IConfigListEntryWidget.Callback<?>, IConfigListEntryWidget<?>> factory = recursiveGetFactory(clazz, clazz, (Map) WIDGETS);
		if (factory != null && factory != NO_FACTORY) {
			try {
				final Supplier getter = () -> list.get(index);
				final Consumer setter = newObj -> list.set(index, newObj);
				final Supplier defaultValueGetter = () -> obj;
				final BooleanSupplier isDefault = () -> true;
				final Runnable resetToDefault = Runnables.doNothing();
				final BooleanSupplier isChanged = () -> !Objects.equals(getter.get(), obj);
				final Runnable undoChanges = () -> setter.accept(obj);
				// Ew
				final Predicate isValid = o -> isValidPredicate.test(Lists.newArrayList(getter.get()));
				return (W) factory.apply(new ScreenedCallback<>(getter, setter, defaultValueGetter, isDefault, resetToDefault, isChanged, undoChanges, isValid, configScreen));
			} catch (Exception e) {
				e.printStackTrace();
				return (W) new InfoText("fml.configgui.error.errorForTypeUseConfig", clazz.getSimpleName());
			}
		} else {
			return (W) new InfoText("fml.configgui.error.unsupportedTypeUseConfig", clazz.getSimpleName());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes", "UnstableApiUsage"})
	public static <W extends Widget & IConfigListEntryWidget<?>> W makeConfigWidget(@Nonnull final UnmodifiableConfig config, @Nonnull final ConfigScreen configScreen, @Nonnull final Predicate<Object> isValidPredicate, @Nonnull final String path, @Nonnull final Object obj) {
		final Class<?> clazz = obj.getClass();

		if (clazz == null || clazz == Object.class)
			return (W) new InfoText("fml.configgui.error.nullTypeUseConfig");

		Function<IConfigListEntryWidget.Callback<?>, IConfigListEntryWidget<?>> factory = recursiveGetFactory(clazz, clazz, (Map) WIDGETS);
		if (factory != null && factory != NO_FACTORY) {
			try {
				final Supplier getter = () -> config.get(path);
				final Consumer setter;
				if (config instanceof Config)
					setter = newObj -> ((Config) config).set(path, newObj);
				else
					setter = newObj -> {
					};
				final Supplier defaultValueGetter = () -> obj;
				final BooleanSupplier isDefault = () -> true;
				final Runnable resetToDefault = Runnables.doNothing();
				final BooleanSupplier isChanged = () -> !Objects.equals(getter.get(), obj);
				final Runnable undoChanges = () -> setter.accept(obj);
				// Ew
				final Predicate isValid = o -> {
					final Config temp = config.configFormat().createConfig();
					temp.add(path, getter.get());
					return isValidPredicate.test(config);
				};
				return (W) factory.apply(new ScreenedCallback<>(getter, setter, defaultValueGetter, isDefault, resetToDefault, isChanged, undoChanges, isValid, configScreen));
			} catch (Exception e) {
				e.printStackTrace();
				return (W) new InfoText("fml.configgui.error.errorForTypeUseConfig", clazz.getSimpleName());
			}
		} else {
			return (W) new InfoText("fml.configgui.error.unsupportedTypeUseConfig", clazz.getSimpleName());
		}
	}

	@Nullable
	private static <T extends Function<A, B>, A, B> T recursiveGetFactory(final Class<?> clazz, final Class<?> originalClass, final Map<Class<?>, Function> factories) {
		if (clazz == null || clazz == Object.class)
			return null;
		Function factory = factories.get(clazz);
		if (factory == null)
			factory = recursiveGetFactory(clazz.getSuperclass(), clazz, factories);
		if (factory == null) {
			for (final Class<?> anInterface : clazz.getInterfaces()) {
				factory = recursiveGetFactory(anInterface, clazz, factories);
				if (factory != null)
					break;
			}
		}
		if (factory == null)
			factories.put(clazz, NO_FACTORY); // A factory does NOT exist, avoid checking hierarchy as extensively in the future.
		else
			factories.put(originalClass, factory); // A factory DOES exist, avoid checking the class' entire hierarchy in the future.
		return (T) factory;
	}

	/**
	 * Returns the map of the spec's {@link ValueSpec}s.
	 * This map's values are either ValueSpecs (config element specs) or Configs (subcategories).
	 *
	 * @see #getValueSpec(ModConfig, List)
	 */
	public static Map<String, Object> getSpecValueSpecs(final ModConfig modConfig) {
		// name -> Object (ValueSpec|SimpleConfig)
		return modConfig.getSpec().valueMap();
	}

	/**
	 * Returns the map of the spec's {@link ConfigValue}s.
	 * This map's values are either ConfigValues (config elements) or Configs (subcategories).
	 *
	 * @see #getConfigValue(ModConfig, List)
	 */
	public static Map<String, Object> getSpecConfigValues(final ModConfig modConfig) {
		// name -> Object (ConfigValue|SimpleConfig)
		return modConfig.getSpec().getValues().valueMap();
	}

	/**
	 * Returns the map of the modConfig's raw runtime values.
	 * This map's values can be a mix of
	 * Booleans, Bytes, Shorts, Integers, Floats, Doubles, Longs, Enums, Strings, Lists or Configs.
	 * These values are NOT type safe.
	 * <p>
	 * For example:
	 * Bytes, Shorts and Longs can be stored as Integers.
	 * Floats can be stored as Doubles.
	 * Enums can be stored as Strings.
	 * <p>
	 * Therefore, It is usually better to use {@link #getSpecConfigValues(ModConfig)} to
	 * get a {@link ConfigValue} and then call {@link ConfigValue#get()} to get the
	 * type safe and correct value.
	 *
	 * @see #getValue(ModConfig, List)
	 */
	public static Map<String, Object> getConfigValues(final ModConfig modConfig) {
		// name -> Object (Boolean|Byte|Short|Integer|Float|Double|Long|Enum|Strings|Lists|Config)
		return modConfig.getConfigData().valueMap();
	}

	/**
	 * Iteratively gets a ValueSpec (config element spec) or Config (subcategory) by path.
	 *
	 * @see #getSpecValueSpecs(ModConfig)
	 */
	public static Object getValueSpec(final ModConfig modConfig, final List<String> path) {
		// name -> Object (ValueSpec|SimpleConfig)
		final Map<String, Object> specValueSpecs = getSpecValueSpecs(modConfig);

		// Either a ValueSpec or a SimpleConfig
		Object ret = specValueSpecs;

		for (final String s : path) {
			if (ret instanceof Map) // First iteration
				ret = ((Map<String, Object>) ret).get(s);
			else if (ret instanceof ValueSpec)
				return ret; // Uh, shouldn't happen? TODO: Throw error?
			else if (ret instanceof Config)
				ret = ((Config) ret).get(s);
		}
		return ret;
	}

	/**
	 * Iteratively gets a ConfigValue (config element) or Configs (subcategory) by path.
	 *
	 * @see #getSpecConfigValues(ModConfig)
	 */
	public static Object getConfigValue(final ModConfig modConfig, final List<String> path) {
		// name -> Object (ConfigValue|SimpleConfig)
		final Map<String, Object> specConfigVales = getSpecConfigValues(modConfig);

		// Either a ConfigValue or a SimpleConfig
		Object ret = specConfigVales;

		for (final String s : path) {
			if (ret instanceof Map) // First iteration
				ret = ((Map<String, Object>) ret).get(s);
			else if (ret instanceof ConfigValue)
				return ret; // Uh, shouldn't happen? TODO: Throw error?
			else if (ret instanceof Config)
				ret = ((Config) ret).get(s);
		}
		return ret;
	}

	/**
	 * Iteratively gets a raw runtime value by path.
	 * <p>
	 * The returned value may not be of the type you expect!
	 *
	 * @see #getSpecConfigValues(ModConfig)
	 */
	public static Object getValue(final ModConfig modConfig, final List<String> path) {
		// name -> Object (Boolean|Byte|Short|Integer|Float|Double|Long|Enum|Strings|Lists|Config)
		final Map<String, Object> specConfigVales = getConfigValues(modConfig);

		// Either a ConfigValue or a SimpleConfig
		Object ret = specConfigVales;

		for (final String s : path) {
			if (ret instanceof Map) // First iteration
				ret = ((Map<String, Object>) ret).get(s);
//			else if (ret instanceof Something)
//				return ret; // Uh, shouldn't happen? TODO: Throw error?
			else if (ret instanceof Config)
				ret = ((Config) ret).get(s);
		}
		return ret;
	}

	/**
	 * Just a callback that also has a screen.
	 * Used for the {@link ListButton} and {@link ConfigButton}
	 *
	 * @param <T> Type type of the value (e.g Boolean/Float)
	 */
	public static class ScreenedCallback<T> extends IConfigListEntryWidget.Callback<T> {

		public final ConfigScreen screen;

		public ScreenedCallback(final Supplier<T> getter, final Consumer<T> setter, final Supplier<T> defaultValueGetter, final BooleanSupplier isDefault, final Runnable resetToDefault, final BooleanSupplier isChanged, final Runnable undoChanges, final Predicate<Object> isValid, final ConfigScreen screen) {
			super(getter, setter, defaultValueGetter, isDefault, resetToDefault, isChanged, undoChanges, isValid);
			this.screen = screen;
		}

		public ScreenedCallback(final Supplier<T> getter, final Consumer<T> setter, final Supplier<T> defaultValueGetter, final BooleanSupplier isDefault, final Runnable resetToDefault, final BooleanSupplier isChanged, final Runnable undoChanges, final Predicate<Object> isValid, @Nullable final Runnable save, final ConfigScreen screen) {
			super(getter, setter, defaultValueGetter, isDefault, resetToDefault, isChanged, undoChanges, isValid, save);
			this.screen = screen;
		}

	}

}
