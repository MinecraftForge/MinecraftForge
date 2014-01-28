package net.minecraft.entity.ai.attributes;

import java.util.UUID;
import org.apache.commons.lang3.Validate;

public class AttributeModifier
{
    private final double amount;
    private final int operation;
    private final String name;
    private final UUID id;
    // JAVADOC FIELD $$ field_111171_e
    private boolean isSaved;
    private static final String __OBFID = "CL_00001564";

    public AttributeModifier(String par1Str, double par2, int par4)
    {
        this(UUID.randomUUID(), par1Str, par2, par4);
    }

    public AttributeModifier(UUID par1UUID, String par2Str, double par3, int par5)
    {
        this.isSaved = true;
        this.id = par1UUID;
        this.name = par2Str;
        this.amount = par3;
        this.operation = par5;
        Validate.notEmpty(par2Str, "Modifier name cannot be empty", new Object[0]);
        Validate.inclusiveBetween(Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(par5), "Invalid operation", new Object[0]);
    }

    public UUID getID()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public int getOperation()
    {
        return this.operation;
    }

    public double getAmount()
    {
        return this.amount;
    }

    // JAVADOC METHOD $$ func_111165_e
    public boolean isSaved()
    {
        return this.isSaved;
    }

    // JAVADOC METHOD $$ func_111168_a
    public AttributeModifier setSaved(boolean par1)
    {
        this.isSaved = par1;
        return this;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            AttributeModifier attributemodifier = (AttributeModifier)par1Obj;

            if (this.id != null)
            {
                if (!this.id.equals(attributemodifier.id))
                {
                    return false;
                }
            }
            else if (attributemodifier.id != null)
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public String toString()
    {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name=\'" + this.name + '\'' + ", id=" + this.id + ", serialize=" + this.isSaved + '}';
    }
}