package net.minecraftforge.common.config;

import static java.util.Arrays.asList;
import static net.minecraftforge.fml.Logging.CORE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraftforge.common.config.api.ConfigDefinitions;
import net.minecraftforge.common.config.values.BooleanValue;
import net.minecraftforge.common.config.values.ConfigValue;
import net.minecraftforge.common.config.values.DoubleValue;
import net.minecraftforge.common.config.values.EnumValue;
import net.minecraftforge.common.config.values.IntValue;
import net.minecraftforge.common.config.values.ListValue;
import net.minecraftforge.common.config.values.LongValue;
import net.minecraftforge.common.config.values.ModValue;
import net.minecraftforge.common.config.values.StringValue;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.moddiscovery.NightConfigWrapper;
import net.minecraftforge.forgespi.language.IConfigurable;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Range;

public class ModConfigSpec extends AbstractConfigSpec implements IConfigurable, ConfigDefinitions
{
    private List<String> currentPath = new ArrayList<>();
    protected List<ConfigValue<?>> values = new ArrayList<>();
    private SpecContext context = new SpecContext();

    public ModConfigSpec(ModConfig<?> modConfig)
    {
        super(modConfig);
    }

    /**
     * Correct a given {@link Config}
     *
     * @param config the config to correct
     */
    public final void correctConfig(CommentedConfig config) {
        if (config != null) {
            String configName = config instanceof FileConfig ? ((FileConfig) config).getNioPath().toString() : config.toString();
            LogManager.getLogger().warn(CORE, "Configuration file {} is not correct. Correcting", configName);
            correctConfig(config, (action, path, incorrectValue, correctedValue) ->
                    LogManager.getLogger().warn(CORE, "Incorrect key {} was corrected from {} to {}", DOT_JOINER.join( path ), incorrectValue, correctedValue));
            if (config instanceof FileConfig) {
                ((FileConfig) config).save();
            }
        }
    }

    public CommentedConfig getConfig() {
        return getModConfig().getConfigData();
    }

    public CommentedConfig createSection() {
        if (getModConfig() == null || getConfig() == null) {
            return CommentedConfig.inMemory().createSubConfig();
        }
        return getConfig().createSubConfig();
    }

    //<editor-fold desc="Builder pattern methods">
    public ModConfigSpec comment(String comment)
    {
        context.setComment(comment);
        return this;
    }

    public ModConfigSpec comment(String... comment)
    {
        context.setComment(comment);
        return this;
    }

    public ModConfigSpec translation(String translationKey)
    {
        context.setTranslationKey(translationKey);
        return this;
    }

    public ModConfigSpec worldRestart()
    {
        context.worldRestart();
        return this;
    }

    /**
     * Create a new section in the config with the given name.
     *
     * @param path the name of the section which is also the path
     * @return the spec
     */
    public ModConfigSpec push(String path)
    {
        return push(split(path));
    }

    /**
     * Create new sections using the given path.
     * Each element in the list will be a section
     *
     * @param path the path used to create sections
     * @return the spec
     */
    public ModConfigSpec push(List<String> path)
    {
        currentPath.addAll(path);
        if (context.hasComment()) {

            levelComments.put(new ArrayList<String>(currentPath), context.buildComment());
            context.setComment(); // Set to empty
        }
        context.ensureEmpty();
        return this;
    }

    /**
     * Pop the latest section off the list.
     * This will remove the last section from the current path.
     * <i>This will <b>NOT</b> remove the last section from the actual configuration!</i>
     *
     * @return the spec
     */
    public ModConfigSpec pop()
    {
        return pop(1);
    }

    /**
     * Pop the latest 'n' sections off the list.
     * This will remove the last section from the current path.
     * <i>This will <b>NOT</b> remove the section(s) from the actual configuration!</i>
     *
     * @param count the number of sections to remove
     * @return the spec
     */
    public ModConfigSpec pop(int count)
    {
        if (count > currentPath.size())
            throw new IllegalArgumentException("Attempted to pop " + count + " elements when we only had: " + currentPath);
        for (int x = 0; x < count; x++)
            currentPath.remove(Math.min(0, currentPath.size() - 1));
        return this;
    }
    //</editor-fold>

    @Override
    public <T> Optional<T> getConfigElement(String... key)
    {
        if (getConfig() == null) {
            return Optional.empty();
        }
        return getConfig().getOptional(Arrays.asList(key));
    }

    @Override
    public List<? extends IConfigurable> getConfigList(String... key)
    {
        final List<String> path = asList(key);
        if (this.getConfig().contains(path) && !(this.getConfig().get(path) instanceof Collection)) {
            throw new IllegalArgumentException("The configuration path "+ path +" is invalid. Expecting a collection!");
        }
        final ArrayList<UnmodifiableConfig> nestedConfigs = this.getConfig().getOrElse(path, ArrayList::new);
        return nestedConfigs.stream()
                .map(NightConfigWrapper::new)
                .map(cw -> cw.setFile(getModConfig().getContainer().getModInfo().getOwningFile())) // Match ModFileParser behavior
                .collect(Collectors.toList());
    }

    /** The root define method. All methods end here eventually. */
    //TODO: Better name? 'define' conflicts with night-config's define method
    protected <T> ConfigValue<T> _define(List<String> path, ValueSpec value, Supplier<T> defaultSupplier) {
        if (!currentPath.isEmpty()) {
            List<String> tmp = new ArrayList<>(currentPath.size() + path.size());
            tmp.addAll(currentPath);
            tmp.addAll(path);
            path = tmp;
        }
        storage.set(path, value);
        context = new SpecContext();
        ConfigValue<T> configValue = new ConfigValue<>(this, path, defaultSupplier);
        values.add(configValue);
        return configValue;
    }

    @Override
    public <T> ConfigValue<T> defineObject(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz)
    {
        ValueSpec valueSpec = new ValueSpec(defaultSupplier, validator, context);
        return _define(path, valueSpec, defaultSupplier);
    }

    @Override
    public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, Range<V> range, Class<V> clazz)
    {
        ValueRange<V> valueRange = new ValueRange<>(clazz, range);
        context.setRange(valueRange);
        context.setComment(ObjectArrays.concat(context.getComment(), "Range: " + range.toString()));
        ValueSpec valueSpec = new ValueSpec(defaultSupplier, valueRange, context);
        return _define(path, valueSpec, defaultSupplier);
    }

    @Override
    public BooleanValue defineBoolean(List<String> path, Supplier<Boolean> defaultSupplier)
    {
        Predicate<Object> isBoolean = o -> o instanceof String ? ("true".equalsIgnoreCase((String)o) || "false".equalsIgnoreCase((String)o)) : o instanceof Boolean;
        ConfigValue<?> value = _define(path, new ValueSpec(defaultSupplier, isBoolean, context), defaultSupplier);
        return new BooleanValue(this, value.getPath(), defaultSupplier);
    }

    private <T extends Comparable<T>> ConfigValue<?> createNumberConfigValue(List<String> path, Supplier<T> supplier, Predicate<Object> validator) {
        ValueSpec valueSpec = new ValueSpec(supplier, validator, context);
        return _define(path, valueSpec, supplier);
    }
    @Override public IntValue defineNumber(List<String> path, int defaultValue, Predicate<Object> validator)
    {
        Supplier<Integer> defaultSupplier = () -> defaultValue;
        ConfigValue<?> value = createNumberConfigValue(path, defaultSupplier, validator);
        return new IntValue(this, value.getPath(), defaultSupplier);
    }

    @Override public DoubleValue defineNumber(List<String> path, double defaultValue, Predicate<Object> validator)
    {
        Supplier<Double> defaultSupplier = () -> defaultValue;
        ConfigValue<?> value = createNumberConfigValue(path, defaultSupplier, validator);
        return new DoubleValue(this, value.getPath(), defaultSupplier);
    }

    @Override public LongValue defineNumber(List<String> path, long defaultValue, Predicate<Object> validator)
    {
        Supplier<Long> defaultSupplier = () -> defaultValue;
        ConfigValue<?> value = createNumberConfigValue(path, defaultSupplier, validator);
        return new LongValue(this, value.getPath(), defaultSupplier);
    }

    @Override
    public DoubleValue defineInRange(List<String> path, double defaultValue, Range<Double> range)
    {
        ConfigValue<?> value = defineInRange(path, () -> defaultValue, range, Double.class);
        return new DoubleValue(this, value.getPath(), () -> defaultValue);
    }

    @Override
    public IntValue defineInRange(List<String> path, int defaultValue, Range<Integer> range)
    {
        ConfigValue<?> value = defineInRange(path, () -> defaultValue, range, Integer.class);
        return new IntValue(this, value.getPath(), () -> defaultValue);
    }

    @Override
    public LongValue defineInRange(List<String> path, long defaultValue, Range<Long> range)
    {
        ConfigValue<?> value = defineInRange(path, () -> defaultValue, range, Long.class);
        return new LongValue(this, value.getPath(), () -> defaultValue);
    }

    @Override
    public <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz)
    {
        context.setClazz(clazz);
        V[] allowedValues = clazz.getEnumConstants();
        context.setComment(ObjectArrays.concat(context.getComment(), "Allowed Values: " + Arrays.stream(allowedValues).filter(validator).map(Enum::name).collect(Collectors.joining(", "))));
        ValueSpec valueSpec = new ValueSpec(defaultSupplier, validator, context);
        ConfigValue<?> value = _define(path, valueSpec, defaultSupplier);
        return new EnumValue<V>(this, value.getPath(), defaultSupplier, converter, clazz);

    }

    @Override
    public StringValue defineString(List<String> path, Supplier<String> defaultSupplier)
    {
        ValueSpec spec = new ValueSpec(defaultSupplier, Objects::nonNull, context);
        ConfigValue<?> value = _define(path, spec, defaultSupplier);
        return new StringValue(this, value.getPath(), defaultSupplier);
    }

    @Override
    public <T> ListValue<T> defineList(List<String> path, Supplier<List<T>> defaultSupplier, Predicate<Object> elementValidator, Function<Object, T> elementConverter)
    {
        context.setClazz(List.class);
        Predicate<Object> configValidator = x -> x instanceof List && ((List<?>)x).stream().allMatch(elementValidator);
        ValueSpec spec = new ValueSpec(defaultSupplier, configValidator, context) {
            @Override
            public Object correct(Object value)
            {
                if (!(value instanceof List) || ((List<?>)value).isEmpty()) {
                    return getDefault();
                }
                List<?> rawList = Lists.newArrayList((List<?>)value);
                rawList.removeIf(elementValidator.negate());
                if(rawList.isEmpty()) {
                    return getDefault();
                }
                return rawList.stream().map(elementConverter).collect(Collectors.toList());
            }
        };
        ConfigValue<?> value = _define(path, spec, defaultSupplier);
        return new ListValue<>(this, value.getPath(), defaultSupplier, elementConverter);
    }

    @Override
    public <T> ModValue<T> defineModObject(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, ModObjectConverter<T> converter)
    {
        return _defineMod(path, defaultSupplier, converter, validator);
    }

    protected <T> ModValue<T> _defineMod(List<String> path, Supplier<T> defaultSupplier, ModObjectConverter<T> converter, Predicate<Object> validator)
    {
        if (!currentPath.isEmpty())
        {
            List<String> tmp = new ArrayList<>(currentPath.size() + path.size());
            tmp.addAll(currentPath);
            tmp.addAll(path);
            path = tmp;
        }
        Config section = storage.createSubConfig(); // all we need is a sub-config, this does not *need* to depend on the mod's config file
        converter.saveToConfig(section, path, defaultSupplier.get(), defaultSupplier);
        ValueSpec value = new ValueSpec(() -> section, validator, context);
        storage.set(path, value);
        ConfigValue<T> configValue = new ConfigValue<>(this, path, defaultSupplier);
        values.add(configValue);
        return new ModValue<>(this, path, defaultSupplier, converter);
    }
}
