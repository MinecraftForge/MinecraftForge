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

//=========================================================
// Run away thar' be dragons!
//=========================================================
import java.util.Arrays;

import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

import net.minecraftforge.common.config.Property.Type;

class TypeAdapters
{
   /*
    *    boolean, boolean[], Boolean, Boolean[]
    *    float, float[], Float, Float[]
    *    double, double[], Double, Double[]
    *    byte, byte[], Byte, Byte[]
    *    char, char[], Character, Character[]
    *    short, short[], Short, Short[]
    *    int, int[], Integer, Integer[]
    *    String, String[]
    */
    static ITypeAdapter bool = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return prop.getBoolean();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Boolean)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Boolean)value);
        }
        @Override
        public Type getType()
        {
            return Type.BOOLEAN;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter boolA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return prop.getBooleanList();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues((boolean[])value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues((boolean[])value);
        }
        @Override
        public Type getType()
        {
            return Type.BOOLEAN;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter Bool = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Boolean.valueOf(prop.getBoolean());
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Boolean)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Boolean)value);
        }
        @Override
        public Type getType()
        {
            return Type.BOOLEAN;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter BoolA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Booleans.asList(prop.getBooleanList()).toArray(new Boolean[prop.getBooleanList().length]);
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Booleans.toArray(Arrays.asList((Boolean[]) value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Booleans.toArray(Arrays.asList((Boolean[]) value)));
        }
        @Override
        public Type getType()
        {
            return Type.BOOLEAN;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter flt = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return (float)prop.getDouble();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Float)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Float)value);
        }
        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter fltA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Floats.toArray(Doubles.asList(prop.getDoubleList()));
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Doubles.toArray(Floats.asList((float[])value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Doubles.toArray(Floats.asList((float[])value)));
        }
        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter Flt = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Float.valueOf((float)prop.getDouble());
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Float)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Float)value);
        }
        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter FltA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Floats.asList(Floats.toArray(Doubles.asList(prop.getDoubleList()))).toArray(new Float[prop.getDoubleList().length]);
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Doubles.toArray(Arrays.asList((Float[])value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Doubles.toArray(Arrays.asList((Float[])value)));
        }
        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter dbl = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop)
        {
            return prop.getDouble();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Double)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Double)value);
        }
        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter dblA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return prop.getDoubleList();
        }

        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues((double[])value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues((double[])value);
        }

        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }

        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter Dbl = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Double.valueOf(prop.getDouble());
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Double)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Double) value);
        }
        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter DblA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Doubles.asList(prop.getDoubleList()).toArray(new Double[prop.getDoubleList().length]);
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Doubles.toArray(Arrays.asList((Double[])value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Doubles.toArray(Arrays.asList((Double[])value)));
        }
        @Override
        public Type getType()
        {
            return Type.DOUBLE;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter byt = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop)
        {
            return (byte)prop.getInt();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Byte)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Byte)value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter bytA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Bytes.toArray(Ints.asList(prop.getIntList()));
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Ints.toArray(Bytes.asList((byte[])value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Ints.toArray(Bytes.asList((byte[])value)));
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter Byt = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Byte.valueOf((byte)prop.getInt());
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Byte)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Byte)value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter BytA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Bytes.asList(Bytes.toArray(Ints.asList(prop.getIntList()))).toArray(new Byte[prop.getIntList().length]);
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Ints.toArray(Arrays.asList((Byte[]) value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Ints.toArray(Arrays.asList((Byte[]) value)));
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter chr = new ITypeAdapter() {
        
        @Override
        public Object getValue(Property prop) {
            return (char)prop.getInt();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Character)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Character)value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter chrA = new ITypeAdapter() {
        private int[] toPrim(char[] v) {
            if (v == null) return new int[0];
            int[] ret = new int[v.length];
            for (int x = 0; x < v.length; x++)
                ret[x] = v[x];
            return ret;
        }
        
        @Override
        public Object getValue(Property prop) {
            int[] v = prop.getIntList();
            char[] ret = new char[v.length];
            for (int x = 0; x < v.length; x++)
                ret[x] = (char)v[x];
            return ret;
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(toPrim((char[])value));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(toPrim((char[])value));
        }

        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }

        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter Chr = new ITypeAdapter() {
        
        @Override
        public Object getValue(Property prop) {
            return Character.valueOf((char)prop.getInt());
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Character)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Character)value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter ChrA = new ITypeAdapter() {
        private int[] toPrim(Character[] v) {
            if (v == null) return new int[0];
            int[] ret = new int[v.length];
            for (int x = 0; x < v.length; x++)
                ret[x] = v[x] == null ? 0 : v[x];
            return ret;
        }
        
        @Override
        public Object getValue(Property prop) {
            int[] v = prop.getIntList();
            Character[] ret = new Character[v.length];
            for (int x = 0; x < v.length; x++)
                ret[x] = Character.valueOf((char)v[x]);
            return ret;
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(toPrim((Character[])value));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(toPrim((Character[]) value));
        }

        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }

        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }

    };
    static ITypeAdapter shrt = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return (short)prop.getInt();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Short)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Short)value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter shrtA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Shorts.toArray(Ints.asList(prop.getIntList()));
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Ints.toArray(Shorts.asList((short[])value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Ints.toArray(Shorts.asList((short[])value)));
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
        
    };
    static ITypeAdapter Shrt = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Short.valueOf((short)prop.getInt());
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Short)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Short)value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter ShrtA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            int[] v = prop.getIntList();
            Short[] ret = new Short[v.length];
            for (int x = 0; x < ret.length; x++)
                ret[x] = Short.valueOf((short)v[x]);
            return ret;
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Ints.toArray(Arrays.asList((Short[])value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Ints.toArray(Arrays.asList((Short[])value)));
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter int_ = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return prop.getInt();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Integer)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Integer)value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter intA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return prop.getIntList();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues((int[])value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues((int[])value);
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter Int = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return (Integer)prop.getInt();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((Integer)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((Integer)value);            
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter IntA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return Ints.asList(prop.getIntList()).toArray(new Integer[prop.getIntList().length]);
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues(Ints.toArray(Arrays.asList((Integer[])value)));
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues(Ints.toArray(Arrays.asList((Integer[])value)));
        }
        @Override
        public Type getType()
        {
            return Type.INTEGER;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
    static ITypeAdapter Str = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return prop.getString();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValue((String)value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValue((String)value);
        }
        @Override
        public Type getType()
        {
            return Type.STRING;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return false;
        }
    };
    static ITypeAdapter StrA = new ITypeAdapter() {
        @Override
        public Object getValue(Property prop) {
            return prop.getStringList();
        }
        @Override
        public void setDefaultValue(Property property, Object value)
        {
            property.setDefaultValues((String[])value);
        }
        @Override
        public void setValue(Property property, Object value)
        {
            property.setValues((String[])value);
        }
        @Override
        public Type getType()
        {
            return Type.STRING;
        }
        @Override
        public boolean isArrayAdapter()
        {
            return true;
        }
    };
}
