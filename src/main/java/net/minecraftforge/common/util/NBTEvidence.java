package net.minecraftforge.common.util;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by user on 2015/05/26.
 */
public abstract class NBTEvidence<T>
{
    public abstract void write (NBTTagCompound tagCompound, String key, T value);

    public abstract T get (NBTTagCompound tagCompound, String key);

    public abstract String getTypeString();


    public static final NBTEvidence<Integer> INT = new NBTEvidence<Integer>() {
        @Override
        public void write (NBTTagCompound tagCompound, String key, Integer value) {
            tagCompound.setInteger(key, value);
        }

        @Override
        public Integer get (NBTTagCompound tagCompound, String key) {
            return tagCompound.getInteger(key);
        }

        @Override
        public String getTypeString()
        {
            return "int";
        }
    };

    public static final NBTEvidence<Byte> BYTE = new NBTEvidence<Byte>() {
        @Override
        public void write (NBTTagCompound tagCompound, String key, Byte value) {
            tagCompound.setByte(key, value);
        }

        @Override
        public Byte get (NBTTagCompound tagCompound, String key) {
            return tagCompound.getByte(key);
        }

        @Override
        public String getTypeString()
        {
            return "byte";
        }
    };

    public static final NBTEvidence<String> STRING = new NBTEvidence<String>() {
        @Override
        public void write (NBTTagCompound tagCompound, String key, String value) {
            tagCompound.setString(key, value);
        }

        @Override
        public String get (NBTTagCompound tagCompound, String key) {
            return tagCompound.getString(key);
        }

        @Override
        public String getTypeString()
        {
            return "String";
        }
    };

}
