package net.minecraft.src;

public class WorldType
{
    public static final WorldType[] field_48637_a = new WorldType[16];
    public static final WorldType field_48635_b = (new WorldType(0, "default", 1)).func_48631_f();
    public static final WorldType field_48636_c = new WorldType(1, "flat");
    public static final WorldType field_48634_d = (new WorldType(8, "default_1_1", 0)).func_48625_a(false);

    /** 'default' or 'flat' */
    private final String worldType;
    private final int field_48632_f;
    private boolean field_48633_g;
    private boolean field_48638_h;

    private WorldType(int par1, String par2Str)
    {
        this(par1, par2Str, 0);
    }

    private WorldType(int par1, String par2Str, int par3)
    {
        this.worldType = par2Str;
        this.field_48632_f = par3;
        this.field_48633_g = true;
        field_48637_a[par1] = this;
    }

    public String func_48628_a()
    {
        return this.worldType;
    }

    public String func_46136_a()
    {
        return "generator." + this.worldType;
    }

    public int func_48630_c()
    {
        return this.field_48632_f;
    }

    public WorldType func_48629_a(int par1)
    {
        return this == field_48635_b && par1 == 0 ? field_48634_d : this;
    }

    private WorldType func_48625_a(boolean par1)
    {
        this.field_48633_g = par1;
        return this;
    }

    public boolean func_48627_d()
    {
        return this.field_48633_g;
    }

    private WorldType func_48631_f()
    {
        this.field_48638_h = true;
        return this;
    }

    public boolean func_48626_e()
    {
        return this.field_48638_h;
    }

    public static WorldType parseWorldType(String par0Str)
    {
        for (int var1 = 0; var1 < field_48637_a.length; ++var1)
        {
            if (field_48637_a[var1] != null && field_48637_a[var1].worldType.equalsIgnoreCase(par0Str))
            {
                return field_48637_a[var1];
            }
        }

        return null;
    }
}
