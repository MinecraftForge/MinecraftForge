package net.minecraftforge.fml;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.ICustomPacket;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeHacks
{
    private static final Unsafe UNSAFE;
    static {
        try
        {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe)theUnsafe.get(null);
        }
        catch (IllegalAccessException | NoSuchFieldException e)
        {
            throw new RuntimeException("BARF!", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> packetClass)
    {
        try
        {
            return (T) UNSAFE.allocateInstance(packetClass);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Field field, Object object) {
        final long l = UNSAFE.objectFieldOffset(field);
        return (T) UNSAFE.getObject(object, l);
    }

    public static void setField(Field data, Object object, Object buffer)
    {
        long offset = UNSAFE.objectFieldOffset(data);
        UNSAFE.putObject(object, offset, buffer);
    }

    public static int getIntField(Field f, Object obj)
    {
        long offset = UNSAFE.objectFieldOffset(f);
        return UNSAFE.getInt(obj, offset);
    }

    public static void setIntField(Field data, Object object, int value)
    {
        long offset = UNSAFE.objectFieldOffset(data);
        UNSAFE.putInt(object, offset, value);
    }
}
