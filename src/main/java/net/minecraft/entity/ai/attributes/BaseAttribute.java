package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute
{
    private final String field_111115_a;
    private final double defaultValue;
    private boolean shouldWatch;
    private static final String __OBFID = "CL_00001565";

    protected BaseAttribute(String par1Str, double par2)
    {
        this.field_111115_a = par1Str;
        this.defaultValue = par2;

        if (par1Str == null)
        {
            throw new IllegalArgumentException("Name cannot be null!");
        }
    }

    public String getAttributeUnlocalizedName()
    {
        return this.field_111115_a;
    }

    public double getDefaultValue()
    {
        return this.defaultValue;
    }

    public boolean getShouldWatch()
    {
        return this.shouldWatch;
    }

    public BaseAttribute setShouldWatch(boolean par1)
    {
        this.shouldWatch = par1;
        return this;
    }

    public int hashCode()
    {
        return this.field_111115_a.hashCode();
    }
}