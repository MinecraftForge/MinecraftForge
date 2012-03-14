package net.minecraft.src;

class OsMap
{
    static final int[] field_1193_a = new int[EnumOS1.values().length];

    static
    {
        try
        {
            field_1193_a[EnumOS1.linux.ordinal()] = 1;
        }
        catch (NoSuchFieldError var4)
        {
            ;
        }

        try
        {
            field_1193_a[EnumOS1.solaris.ordinal()] = 2;
        }
        catch (NoSuchFieldError var3)
        {
            ;
        }

        try
        {
            field_1193_a[EnumOS1.windows.ordinal()] = 3;
        }
        catch (NoSuchFieldError var2)
        {
            ;
        }

        try
        {
            field_1193_a[EnumOS1.macos.ordinal()] = 4;
        }
        catch (NoSuchFieldError var1)
        {
            ;
        }
    }
}
