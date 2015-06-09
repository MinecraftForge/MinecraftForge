package net.minecraftforge.common.util;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by user on 2015/05/26.
 */
public abstract class NBTEvidence<T>
{
    public abstract void write(NBTTagCompound tagCompound, String key, T value);

    public abstract T get(NBTTagCompound tagCompound, String key);

    public abstract String getTypeString();


    public static final NBTEvidence<Integer> INT = new NBTEvidence<Integer>() {
        @Override
        public void write(NBTTagCompound tagCompound, String key, Integer value) {
            tagCompound.setInteger(key, value);
        }

        @Override
        public Integer get(NBTTagCompound tagCompound, String key) {
            return tagCompound.getInteger(key);
        }

        @Override
        public String getTypeString()
        {
            return "int";
        }
    };

    public static final NBTEvidence<Short> SHORT = new NBTEvidence<Short>()
    {
        @Override
        public void write(NBTTagCompound tagCompound, String key, Short value)
        {
            tagCompound.setShort(key, value);
        }

        @Override
        public Short get(NBTTagCompound tagCompound, String key)
        {
            return tagCompound.getShort(key);
        }

        @Override
        public String getTypeString()
        {
            return "short";
        }
    };

    public static final NBTEvidence<Byte> BYTE = new NBTEvidence<Byte>() {
        @Override
        public void write(NBTTagCompound tagCompound, String key, Byte value) {
            tagCompound.setByte(key, value);
        }

        @Override
        public Byte get(NBTTagCompound tagCompound, String key) {
            return tagCompound.getByte(key);
        }

        @Override
        public String getTypeString()
        {
            return "byte";
        }
    };

    public static final NBTEvidence<Long> LONG = new NBTEvidence<Long>()
    {
        @Override
        public void write(NBTTagCompound tagCompound, String key, Long value)
        {
            tagCompound.setLong(key, value);
        }

        @Override
        public Long get(NBTTagCompound tagCompound, String key)
        {
            return tagCompound.getLong(key);
        }

        @Override
        public String getTypeString()
        {
            return "long";
        }
    };

    public static final NBTEvidence<String> STRING = new NBTEvidence<String>() {
        @Override
        public void write(NBTTagCompound tagCompound, String key, String value) {
            tagCompound.setString(key, value);
        }

        @Override
        public String get(NBTTagCompound tagCompound, String key) {
            return tagCompound.getString(key);
        }

        @Override
        public String getTypeString()
        {
            return "String";
        }
    };

    public static final NBTEvidence<Boolean> BOOLEAN = new NBTEvidence<Boolean>()
    {
        @Override
        public void write(NBTTagCompound tagCompound, String key, Boolean value)
        {
            tagCompound.setBoolean(key, value);
        }

        @Override
        public Boolean get(NBTTagCompound tagCompound, String key)
        {
            return tagCompound.getBoolean(key);
        }

        @Override
        public String getTypeString()
        {
            return "boolean";
        }
    };

}
