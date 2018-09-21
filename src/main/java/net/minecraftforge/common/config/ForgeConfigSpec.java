/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common.config;

import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.ADD;
import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.REMOVE;
import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.REPLACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.InMemoryFormat;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionListener;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

/*
 * Like {@link com.electronwill.nightconfig.core.ConfigSpec} except in builder format, and extended to acept comments, language keys,
 * and other things Forge configs would find useful.
 */

public class ForgeConfigSpec
{
    private final Config storage;
    private Map<List<String>, String> levelComments = new HashMap<>();
    private ForgeConfigSpec(Config storage, Map<List<String>, String> levelComments) {
        this.storage = storage;
        this.levelComments = levelComments;
    }

    public boolean isCorrect(CommentedConfig config) {
        return correct(storage, config, null, null, null, true) == 0;
    }

    public int correct(CommentedConfig config) {
        return correct(config, (action, path, incorrectValue, correctedValue) -> {});
    }
    public int correct(CommentedConfig config, CorrectionListener listener) {
        LinkedList<String> parentPath = new LinkedList<>(); //Linked list for fast add/removes
        return correct(storage, config, parentPath, Collections.unmodifiableList(parentPath), listener, false);
    }

    private int correct(Config spec, CommentedConfig config, LinkedList<String> parentPath, List<String> parentPathUnmodifiable, CorrectionListener listener, boolean dryRun)
    {
        int count = 0;

        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = config.valueMap();

        for (Map.Entry<String, Object> specEntry : specMap.entrySet())
        {
            final String key = specEntry.getKey();
            final Object specValue = specEntry.getValue();
            final Object configValue = configMap.get(key);
            final CorrectionAction action = configValue == null ? ADD : REPLACE;

            if (!dryRun)
                parentPath.addLast(key);

            if (specValue instanceof Config)
            {
                if (configValue instanceof CommentedConfig)
                {
                    count += correct((Config)specValue, (CommentedConfig)configValue, parentPath, parentPathUnmodifiable, listener, dryRun);
                    if (count > 0 && dryRun)
                        return count;
                }
                else if (dryRun)
                {
                    return 1;
                }
                else
                {
                    CommentedConfig newValue = config.createSubConfig();
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                    count += correct((Config)specValue, newValue, parentPath, parentPathUnmodifiable, listener, dryRun);
                }

                String newComment = levelComments.get(parentPath);
                String oldComment = config.getComment(key);
                if (!Objects.equals(oldComment, newComment))
                {
                    if (dryRun)
                        return 1;

                    //TODO: Comment correction listener?
                    config.setComment(key, newComment);
                }
            }
            else
            {
                ValueSpec valueSpec = (ValueSpec)specValue;
                if (!valueSpec.test(configValue))
                {
                    if (dryRun)
                        return 1;

                    Object newValue = valueSpec.getDefault();
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                }
                String oldComment = config.getComment(key);
                if (!Objects.equals(oldComment, valueSpec.getComment()))
                {
                    if (dryRun)
                        return 1;

                    //TODO: Comment correction listener?
                    config.setComment(key, valueSpec.getComment());
                }
            }
            if (!dryRun)
                parentPath.removeLast();
        }

        // Second step: removes the unspecified values
        for (Iterator<Map.Entry<String, Object>> ittr = configMap.entrySet().iterator(); ittr.hasNext();)
        {
            Map.Entry<String, Object> entry = ittr.next();
            if (!specMap.containsKey(entry.getKey()))
            {
                if (dryRun)
                    return 1;

                ittr.remove();
                parentPath.addLast(entry.getKey());
                listener.onCorrect(REMOVE, parentPathUnmodifiable, entry.getValue(), null);
                parentPath.removeLast();
                count++;
            }
        }
        return count;
    }

    public static class Builder
    {
        private final Config storage = InMemoryFormat.withUniversalSupport().createConfig();
        private BuilderContext context = new BuilderContext();
        private Map<List<String>, String> levelComments = new HashMap<>();
        private List<String> currentPath = new ArrayList<>();

        //Object
        public Builder define(String path, Object defaultValue) {
            return define(split(path), defaultValue);
        }
        public Builder define(List<String> path, Object defaultValue) {
            return define(path, defaultValue, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
        }
        public Builder define(String path, Object defaultValue, Predicate<Object> validator) {
            return define(split(path), defaultValue, validator);
        }
        public Builder define(List<String> path, Object defaultValue, Predicate<Object> validator) {
            Objects.requireNonNull(defaultValue, "Default value can not be null");
            return define(path, () -> defaultValue, validator);
        }
        public Builder define(String path, Supplier<?> defaultSupplier, Predicate<Object> validator) {
            return define(split(path), defaultSupplier, validator);
        }
        public Builder define(List<String> path, Supplier<?> defaultSupplier, Predicate<Object> validator) {
            return define(path, defaultSupplier, validator, Object.class);
        }
        public Builder define(List<String> path, Supplier<?> defaultSupplier, Predicate<Object> validator, Class<?> clazz) { // This is the root where everything at the end of the day ends up.
            if (!currentPath.isEmpty()) {
                List<String> tmp = new ArrayList<>(currentPath.size() + path.size());
                tmp.addAll(currentPath);
                tmp.addAll(path);
                path = tmp;
            }
            context.setClazz(clazz);
            storage.set(path, new ValueSpec(defaultSupplier, validator, context));
            context = new BuilderContext();
            return this;
        }
        public <V extends Comparable<? super V>> Builder defineInRange(String path, V defaultValue, V min, V max, Class<V> clazz) {
            return defineInRange(split(path), defaultValue, min, max, clazz);
        }
        public <V extends Comparable<? super V>> Builder defineInRange(List<String> path,  V defaultValue, V min, V max, Class<V> clazz) {
            return defineInRange(path, (Supplier<V>)() -> defaultValue, min, max, clazz);
        }
        public <V extends Comparable<? super V>> Builder defineInRange(String path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
            return defineInRange(split(path), defaultSupplier, min, max, clazz);
        }
        public <V extends Comparable<? super V>> Builder defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz)
        {
            Range<V> range = new Range<>(clazz, min, max);
            context.setRange(range);
            if (min.compareTo(max) > 0)
                throw new IllegalArgumentException("Range min most be less then max.");
            define(path, defaultSupplier, range);
            return this;
        }
        public void defineInList(String path, Object defaultValue, Collection<?> acceptableValues) {
            defineInList(split(path), defaultValue, acceptableValues);
        }
        public void defineInList(String path, Supplier<?> defaultSupplier, Collection<?> acceptableValues) {
            defineInList(split(path), defaultSupplier, acceptableValues);
        }
        public void defineInList(List<String> path, Object defaultValue, Collection<?> acceptableValues) {
            defineInList(path, () -> defaultValue, acceptableValues);
        }
        public void defineInList(List<String> path, Supplier<?> defaultSupplier, Collection<?> acceptableValues) {
            define(path, defaultSupplier, acceptableValues::contains);
        }

        //Enum
        public <V extends Enum<V>> Builder defineEnum(String path, V defaultValue) {
            return defineEnum(split(path), defaultValue);
        }
        public <V extends Enum<V>> Builder defineEnum(List<String> path, V defaultValue) {
            return defineEnum(path, defaultValue, defaultValue.getDeclaringClass().getEnumConstants());
        }
        public <V extends Enum<V>> Builder defineEnum(String path, V defaultValue, @SuppressWarnings("unchecked") V... acceptableValues) {
            return defineEnum(split(path), defaultValue, acceptableValues);
        }
        public <V extends Enum<V>> Builder defineEnum(List<String> path, V defaultValue, @SuppressWarnings("unchecked") V... acceptableValues) {
            return defineEnum(path, defaultValue, Arrays.asList(acceptableValues));
        }
        public <V extends Enum<V>> Builder defineEnum(String path, V defaultValue, Collection<V> acceptableValues) {
            return defineEnum(split(path), defaultValue, acceptableValues);
        }
        public <V extends Enum<V>> Builder defineEnum(List<String> path, V defaultValue, Collection<V> acceptableValues) {
            return defineEnum(path, defaultValue, acceptableValues::contains);
        }
        public <V extends Enum<V>> Builder defineEnum(String path, V defaultValue, Predicate<Object> validator) {
            return defineEnum(split(path), defaultValue, validator);
        }
        public <V extends Enum<V>> Builder defineEnum(List<String> path, V defaultValue, Predicate<Object> validator) {
            return defineEnum(path, () -> defaultValue, validator, defaultValue.getDeclaringClass());
        }
        public <V extends Enum<V>> Builder defineEnum(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
            return defineEnum(split(path), defaultSupplier, validator, clazz);
        }
        public <V extends Enum<V>> Builder defineEnum(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
            return define(path, defaultSupplier, validator, clazz);
        }


        //boolean
        public Builder define(String path, boolean defaultValue) {
            return define(split(path), defaultValue);
        }
        public Builder define(List<String> path, boolean defaultValue) {
            return define(path, (Supplier<Boolean>)() -> defaultValue);
        }
        public Builder define(String path, Supplier<Boolean> defaultSupplier) {
            return define(split(path), defaultSupplier);
        }
        public Builder define(List<String> path, Supplier<Boolean> defaultSupplier) {
            return define(path, defaultSupplier, o -> {
                if (o instanceof String) return ((String)o).equalsIgnoreCase("true") || ((String)o).equalsIgnoreCase("false");
                return o instanceof Boolean;
            }, Boolean.class);
        }

        //Double
        public Builder defineInRange(String path, double defaultValue, double min, double max) {
            return defineInRange(split(path), defaultValue, min, max);
        }
        public Builder defineInRange(List<String> path, double defaultValue, double min, double max) {
            return defineInRange(path, (Supplier<Double>)() -> defaultValue, min, max);
        }
        public Builder defineInRange(String path, Supplier<Double> defaultSupplier, double min, double max) {
            return defineInRange(split(path), defaultSupplier, min, max);
        }
        public Builder defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
            return defineInRange(path, defaultSupplier, min, max, Double.class);
        }
        //Ints
        public Builder defineInRange(String path, int defaultValue, int min, int max) {
            return defineInRange(split(path), defaultValue, min, max);
        }
        public Builder defineInRange(List<String> path, int defaultValue, int min, int max) {
            return defineInRange(path, (Supplier<Integer>)() -> defaultValue, min, max);
        }
        public Builder defineInRange(String path, Supplier<Integer> defaultSupplier, int min, int max) {
            return defineInRange(split(path), defaultSupplier, min, max);
        }
        public Builder defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
            return defineInRange(path, defaultSupplier, min, max, Integer.class);
        }
        //Longs
        public Builder defineInRange(String path, long defaultValue, long min, long max) {
            return defineInRange(split(path), defaultValue, min, max);
        }
        public Builder defineInRange(List<String> path, long defaultValue, long min, long max) {
            return defineInRange(path, (Supplier<Long>)() -> defaultValue, min, max);
        }
        public Builder defineInRange(String path, Supplier<Long> defaultSupplier, long min, long max) {
            return defineInRange(split(path), defaultSupplier, min, max);
        }
        public Builder defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
            return defineInRange(path, defaultSupplier, min, max, Long.class);
        }

        public Builder comment(String comment)
        {
            context.setComment(comment);
            return this;
        }
        public Builder comment(String... comment)
        {
            context.setComment(comment);
            return this;
        }

        public Builder translation(String translationKey)
        {
            context.setTranslationKey(translationKey);
            return this;
        }

        public Builder worldRestart()
        {
            context.worldRestart();
            return this;
        }

        public Builder push(String path) {
            return push(split(path));
        }

        public Builder push(List<String> path) {
            currentPath.addAll(path);
            if (context.getComment() != null) {

                levelComments.put(new ArrayList<String>(currentPath), LINE_JOINER.join(context.getComment()));
                context.setComment((String[])null);
            }
            context.ensureEmpty();
            return this;
        }

        public Builder pop() {
            return pop(1);
        }

        public Builder pop(int count) {
            if (count > currentPath.size())
                throw new IllegalArgumentException("Attempted to pop " + count + " elements when we only had: " + currentPath);
            for (int x = 0; x < count; x++)
                currentPath.remove(currentPath.size() - 1);
            return this;
        }

        public ForgeConfigSpec build()
        {
            context.ensureEmpty();
            return new ForgeConfigSpec(storage, levelComments);
        }
    }

    private static class BuilderContext
    {
        private String[] comment;
        private String langKey;
        private Range<?> range;
        private boolean worldRestart = false;
        private Class<?> clazz;

        public void setComment(String... value) { this.comment = value; }
        public String[] getComment() { return this.comment; }
        public void setTranslationKey(String value) { this.langKey = value; }
        public String getTranslationKey() { return this.langKey; }
        public <V extends Comparable<? super V>> void setRange(Range<V> value)
        {
            this.range = value;
            this.setClazz(value.getClazz());
        }
        @SuppressWarnings("unchecked")
        public <V extends Comparable<? super V>> Range<V> getRange() { return (Range<V>)this.range; }
        public void worldRestart() { this.worldRestart = true; }
        public boolean needsWorldRestart() { return this.worldRestart; }
        public void setClazz(Class<?> clazz) { this.clazz = clazz; }
        public Class<?> getClazz(){ return this.clazz; }

        public void ensureEmpty()
        {
            validate(comment, "Non-null comment when null expected");
            validate(langKey, "Non-null translation key when null expected");
            validate(range, "Non-null range when null expected");
            validate(worldRestart, "Dangeling world restart value set to true");
        }

        private void validate(Object value, String message)
        {
            if (value != null)
                throw new IllegalStateException(message);
        }
        private void validate(boolean value, String message)
        {
            if (value)
                throw new IllegalStateException(message);
        }
    }

    @SuppressWarnings("unused")
    private static class Range<V extends Comparable<? super V>> implements Predicate<Object>
    {
        private final  Class<V> clazz;
        private final V min;
        private final V max;

        private Range(Class<V> clazz, V min, V max)
        {
            this.clazz = clazz;
            this.min = min;
            this.max = max;
        }

        public Class<V> getClazz() { return clazz; }
        public V getMin() { return min; }
        public V getMax() { return max; }

        @Override
        public boolean test(Object t)
        {
            if (!clazz.isInstance(t)) return false;
            V c = clazz.cast(t);
            return c.compareTo(min) >= 0 && c.compareTo(max) <= 0;
        }
    }

    public static class ValueSpec
    {
        private final String comment;
        private final String langKey;
        private final Range<?> range;
        private final boolean worldRestart;
        private final Class<?> clazz;
        private final Supplier<?> supplier;
        private final Predicate<Object> validator;
        private Object _default = null;

        private ValueSpec(Supplier<?> supplier, Predicate<Object> validator, BuilderContext context)
        {
            Objects.requireNonNull(supplier, "Default supplier can not be null");
            Objects.requireNonNull(validator, "Validator can not be null");

            this.comment = LINE_JOINER.join(context.getComment());
            this.langKey = context.getTranslationKey();
            this.range = context.getRange();
            this.worldRestart = context.needsWorldRestart();
            this.clazz = context.getClazz();
            this.supplier = supplier;
            this.validator = validator;
        }

        public String getComment() { return comment; }
        public String getTranslationKey() { return langKey; }
        @SuppressWarnings("unchecked")
        public <V extends Comparable<? super V>> Range<V> getRange() { return (Range<V>)this.range; }
        public boolean needsWorldRestart() { return this.worldRestart; }
        public Class<?> getClazz(){ return this.clazz; }
        public boolean test(Object value) { return validator.test(value); }

        public Object getDefault()
        {
            if (_default == null)
                _default = supplier.get();
            return _default;
        }
    }

    private static final Joiner LINE_JOINER = Joiner.on("\n");
    private static final Splitter DOT_SPLITTER = Splitter.on(".");
    private static List<String> split(String path)
    {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }
}
