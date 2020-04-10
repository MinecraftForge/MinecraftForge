/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import org.apache.commons.lang3.StringUtils;

import static net.minecraftforge.common.config.ConfigManager.*;

public abstract class FieldWrapper implements IFieldWrapper
{
    protected String category, name;

    protected Field field;
    protected Object instance;

    public FieldWrapper(String category, Field field, Object instance)
    {
        this.instance = instance;
        this.field = field;
        this.category = category;
        this.name = field.getName();

        if (field.isAnnotationPresent(Config.Name.class))
            this.name = field.getAnnotation(Config.Name.class).value();

        this.field.setAccessible(true); // Just in case
    }

    public static IFieldWrapper get(Object instance, Field field, String category)
    {
        if (ADAPTERS.get(field.getType()) != null)
            return new PrimitiveWrapper(category, field, instance);
        else if (Enum.class.isAssignableFrom(field.getType()))
            return new EnumWrapper(category, field, instance);
        else if (Map.class.isAssignableFrom(field.getType()))
            return new MapWrapper(category, field, instance);
        else if (field.getType().getSuperclass().equals(Object.class))
            throw new RuntimeException("Objects should not be handled by field wrappers");
        else
            throw new IllegalArgumentException(String.format("Fields of type '%s' are not supported!", field.getType().getCanonicalName()));
    }

    public static boolean hasWrapperFor(Field field)
    {
        if (ADAPTERS.get(field.getType()) != null)
            return true;
        else if (Enum.class.isAssignableFrom(field.getType()))
            return true;
        else if (Map.class.isAssignableFrom(field.getType()))
            return true;
        return false;
    }

    private static class MapWrapper extends FieldWrapper
    {
        private Map<String, Object> theMap = null;
        private Type mType;
        private final String baseName;
        ITypeAdapter adapter;

        @SuppressWarnings("unchecked")
        private MapWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);

            this.baseName = (this.category == null) ? "" : (this.category + ".") + this.name.toLowerCase(Locale.ENGLISH) + ".";

            try
            {
                theMap = (Map<String, Object>) field.get(instance);
            }
            catch (ClassCastException cce)
            {
                throw new IllegalArgumentException(String.format("The map '%s' of class '%s' must have the key type String!", field.getName(),
                        field.getDeclaringClass().getCanonicalName()), cce);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }

            ParameterizedType type = (ParameterizedType) field.getGenericType();
            mType = type.getActualTypeArguments()[1];

            this.adapter = ADAPTERS.get(mType);
            if (this.adapter == null && mType instanceof GenericArrayType)
            {
                this.adapter = ADAPTERS.get(ARRAY_REMAP.get(((GenericArrayType)mType).getGenericComponentType())); //J6 seems to have issues, Need to find a better way to translate this. We don't have access to array depth.
            }

            if (mType instanceof Class && Enum.class.isAssignableFrom((Class<?>)mType))
            {
                this.adapter = TypeAdapters.Str;
            }

            if (this.adapter == null)
                throw new IllegalArgumentException(String.format("The map '%s' of class '%s' has target values which are neither primitive nor an enum!",
                        field.getName(), field.getDeclaringClass().getCanonicalName()));
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return adapter;
        }

        @Override
        public String[] getKeys()
        {
            Set<String> keys = theMap.keySet();
            String[] keyArray = new String[keys.size()];

            Iterator<String> it = keys.iterator();
            for (int i = 0; i < keyArray.length; i++)
            {
                keyArray[i] = this.baseName + it.next();
            }

            return keyArray;
        }

        @Override
        public Object getValue(String key)
        {
            return theMap.get(getSuffix(key));
        }

        @Override
        public void setValue(String key, Object value)
        {
            theMap.put(getSuffix(key), value);
        }

        @Override
        public boolean hasKey(String key)
        {
            return theMap.containsKey(getSuffix(key));
        }

        @Override
        public boolean handlesKey(String key)
        {
            if (key == null)
                return false;
            return key.startsWith(this.baseName);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart)
        {
            this.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart, false);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            ConfigCategory confCat = cfg.getCategory(getCategory());
            confCat.setComment(desc);
            confCat.setLanguageKey(langKey);
            confCat.setRequiresMcRestart(reqMCRestart);
            confCat.setRequiresWorldRestart(reqWorldRestart);
        }

        @Override
        public String getCategory()
        {
            return (this.category == null) ? "" : (this.category + ".") + this.name.toLowerCase(Locale.ENGLISH);
        }

        /**
         * Removes the {@code this.baseName} prefix from the key
         * @param key the key to be edited
         * @return the keys suffix
         */
        private String getSuffix(String key)
        {
            return StringUtils.replaceOnce(key, this.baseName, "");
        }

    }

    private static class EnumWrapper extends SingleValueFieldWrapper
    {

        private EnumWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return TypeAdapters.Str;
        }

        @Override
        public Object getValue(String key)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unsupported Key!");

            try
            {
                @SuppressWarnings("rawtypes")
                Enum enu = (Enum) field.get(instance);
                return enu.name();
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(String key, Object value)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unsupported Key!");
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Enum enu = Enum.valueOf((Class<? extends Enum>) field.getType(), (String) value);
            try
            {
                field.set(instance, enu);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart) {
            this.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart,false);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            super.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart, hasSlidingControl);

            Property prop = cfg.getCategory(this.category).get(this.name); // Will be setup in general by ConfigManager

            List<String> listValidValues = Lists.newArrayList();
            List<String> listValidValuesDisplay = Lists.newArrayList();
            for (Enum e : ((Class<? extends Enum>) field.getType()).getEnumConstants())
            {
                listValidValues.add(e.name());
                listValidValuesDisplay.add(e.toString());
            }

            prop.setValidationPattern(Pattern.compile(PIPE.join(listValidValues)));
            prop.setValidValues(listValidValues.toArray(new String[0]));
            prop.setValidValuesDisplay(listValidValuesDisplay.toArray(new String[0]));

            String validValues = NEW_LINE.join(listValidValues);

            if (desc != null)
                prop.setComment(NEW_LINE.join(new String[] { desc, "Valid values:" }) + "\n" + validValues);
            else
                prop.setComment("Valid values:" + "\n" + validValues);
        }
    }

    private static class PrimitiveWrapper extends SingleValueFieldWrapper
    {

        private PrimitiveWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return ADAPTERS.get(field.getType());
        }

        @Override
        public Object getValue(String key)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unknown key!");
            try
            {
                return field.get(instance);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setValue(String key, Object value)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unknown key: " + key);
            try
            {
                field.set(instance, value);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart) {
            this.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart,false);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            super.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart, hasSlidingControl);

            Property prop = cfg.getCategory(this.category).get(this.name);

            RangeInt ia = field.getAnnotation(RangeInt.class);
            if (ia != null)
            {
                prop.setMinValue(ia.min());
                prop.setMaxValue(ia.max());
                if (desc != null)
                    prop.setComment(NEW_LINE.join(new String[] { desc, "Min: " + ia.min(), "Max: " + ia.max() }));
                else
                    prop.setComment(NEW_LINE.join(new String[] { "Min: " + ia.min(), "Max: " + ia.max() }));
                prop.setHasSlidingControl(hasSlidingControl);
            }

            RangeDouble da = field.getAnnotation(RangeDouble.class);
            if (da != null)
            {
                prop.setMinValue(da.min());
                prop.setMaxValue(da.max());
                if (desc != null)
                    prop.setComment(NEW_LINE.join(new String[] { desc, "Min: " + da.min(), "Max: " + da.max() }));
                else
                    prop.setComment(NEW_LINE.join(new String[] { "Min: " + da.min(), "Max: " + da.max() }));
                prop.setHasSlidingControl(hasSlidingControl);
            }
        }
    }

    private static abstract class SingleValueFieldWrapper extends FieldWrapper
    {
        private SingleValueFieldWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);
        }

        @Override
        public String[] getKeys()
        {
            return asArray(this.category + "." + this.name);
        }

        @Override
        public boolean hasKey(String key)
        {
            return (this.category + "." + this.name).equals(key);
        }

        @Override
        public boolean handlesKey(String key)
        {
            return hasKey(key);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart, boolean hasSlidingControl)
        {
            Property prop = cfg.getCategory(this.category).get(this.name); // Will be setup in general by ConfigManager

            prop.setComment(desc);
            prop.setLanguageKey(langKey);
            prop.setRequiresMcRestart(reqMCRestart);
            prop.setRequiresWorldRestart(reqWorldRestart);
            prop.setHasSlidingControl(hasSlidingControl);
        }

        @Override
        public String getCategory()
        {
            return this.category;
        }

    }

    private static <T> T[] asArray(T... in)
    {
        return in;
    }

    public static class BeanEntry<K, V> implements Entry<K, V>
    {
        private K key;
        private V value;

        public BeanEntry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }

        @Override
        public V setValue(V value)
        {
            throw new UnsupportedOperationException("This is a static bean.");
        }

    }
}
