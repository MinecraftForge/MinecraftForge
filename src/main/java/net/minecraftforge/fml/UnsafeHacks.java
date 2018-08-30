package net.minecraftforge.fml;

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
    public static <T> T getField(Field field, Object object) {
        final long l = UNSAFE.objectFieldOffset(field);
        return (T) UNSAFE.getObject(object, l);
    }

    public static <T> T getField(Object object) {
        UNSAFE.staticFieldBase()
    }
}
