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

package net.minecraftforge.fml.common.discovery.json;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;
import org.objectweb.asm.Type;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation.EnumHolder;

//Package private, modders shouldn't access this. Do it through ASMDataTable.
class ASMInfo
{
    String name;
    String[] interfaces;
    List<Annotation> annotations;
    private Map<Integer, Annotation> byID;

    public Annotation getSubAnnotation(int id)
    {
        if (byID == null)
        {
            byID = Maps.newHashMap();
            annotations.forEach(a -> { if (a.id != null) byID.put(a.id, a); });
        }
        return byID.get(id);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper("")
        .add("name", name)
        .add("itf", interfaces)
        .add("ann", annotations)
        .toString();
    }

    public enum TargetType { CLASS, FIELD, METHOD, SUBTYPE };
    public enum ValueType
    {
        BOOL(Boolean::valueOf,         v -> {boolean[] ret = new boolean[v.length]; for (int x = 0; x < v.length; x++) ret[x] = Boolean.parseBoolean(v[x]); return ret; }),
        BYTE(Byte::valueOf,            v -> {byte[]    ret = new byte[v.length];    for (int x = 0; x < v.length; x++) ret[x] = Byte.parseByte(v[x]);       return ret; }),
        CHAR(x -> x.charAt(0),         v -> {char[]    ret = new char[v.length];    for (int x = 0; x < v.length; x++) ret[x] = v[x].charAt(0);             return ret; }),
        SHORT(Short::valueOf,          v -> {short[]   ret = new short[v.length];   for (int x = 0; x < v.length; x++) ret[x] = Short.parseShort(v[x]);     return ret; }),
        INT(Integer::valueOf,          v -> {int[]     ret = new int[v.length];     for (int x = 0; x < v.length; x++) ret[x] = Integer.parseInt(v[x]);     return ret; }),
        LONG(Long::valueOf,            v -> {long[]    ret = new long[v.length];    for (int x = 0; x < v.length; x++) ret[x] = Long.parseLong(v[x]);       return ret; }),
        FLOAT(Float::valueOf,          v -> {float[]   ret = new float[v.length];   for (int x = 0; x < v.length; x++) ret[x] = Float.parseFloat(v[x]);     return ret; }),
        DOUBLE(Double::valueOf,        v -> {double[]  ret = new double[v.length];  for (int x = 0; x < v.length; x++) ret[x] = Double.parseDouble(v[x]);   return ret; }),
        STRING(x -> x, x -> x),
        CLASS(Type::getType,           v -> {Type[]       ret = new Type[v.length];       for (int x = 0; x < v.length; x++) ret[x] = Type.getType(v[x]);            return ret; }),
        ENUM(ValueType::getEnumHolder, v -> {List<EnumHolder> ret = Lists.newArrayList(); for (int x = 0; x < v.length; x++) ret.add(ValueType.getEnumHolder(v[x])); return ret; }),
        ANNOTATION(null, null),
        NULL(x -> null, x -> null);

        public final Function<String, Object> single;
        public final Function<String[], Object> array;

        private ValueType(Function<String, Object> single, Function<String[], Object> array)
        {
            this.single = single;
            this.array = array;
        }

        private static EnumHolder getEnumHolder(String value)
        {
            int idx = value.lastIndexOf('/');
            if (idx <= 1)
                throw new IllegalArgumentException("Can not create a EnumHolder for value: " + value);
            String field = value.substring(idx + 1);
            value = value.substring(0, idx);
            idx = value.indexOf(';'); //Legacy internal name.
            value = value.substring(1, idx);
            return new EnumHolder(value, field);
        }
    };

    static class Annotation
    {
        TargetType type;
        String name;
        String target;
        Integer id;
        ValueHolder value;
        Map<String, ValueHolder> values;
        private Map<String, Object> _values;

        public Map<String, Object> getValues(ASMInfo pool)
        {
            if (_values == null)
            {
                _values = Maps.newHashMap();
                if (values != null)
                    values.forEach((k, v) -> _values.put(k, v.get(pool)));
                else
                    _values.put("value", value == null ? null : value.get(pool));
            }

            return _values;
        }

        @Override
        public String toString()
        {
            return MoreObjects.toStringHelper("")
            .add("type", type)
            .add("name", name)
            .add("target", target)
            .add("id", id)
            .add("value", value)
            .toString();
        }
    }

    static class ValueHolder
    {
        ValueType type;
        String value;
        String[] values;

        private Object _value;

        private ValueType getType()
        {
            return type == null ? ValueType.STRING : type;
        }

        public Object get(ASMInfo pool)
        {
            if (_value == null)
            {
                if (values != null)
                {
                    if (type == ValueType.ANNOTATION)
                    {
                        List<Map<String, Object>> list = Lists.newArrayList();
                        _value = list;

                        for (String s : values)
                        {
                            Annotation sub = pool.getSubAnnotation(Integer.parseInt(s));
                            if (sub == null)
                                FMLLog.log.error("Invalid Sub-Annotation in Annotation JSON: " + s);
                            else
                                list.add(sub.getValues(pool));
                        }
                    }
                    else
                        _value = getType().array.apply(values);
                }
                else
                {
                    if (type == ValueType.ANNOTATION)
                    {
                        Annotation sub = pool.getSubAnnotation(Integer.parseInt(value));
                        if (sub == null)
                            FMLLog.log.error("Invalid Sub-Annotation in Annotation JSON: " + value);
                        else
                            _value = sub.getValues(pool);
                    }
                    else
                        _value = getType().single.apply(value);
                }
            }
            return _value;
        }
    }
}
