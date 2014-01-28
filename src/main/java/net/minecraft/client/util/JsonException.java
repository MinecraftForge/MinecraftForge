package net.minecraft.client.util;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@SideOnly(Side.CLIENT)
public class JsonException extends IOException
{
    private final List field_151383_a = Lists.newArrayList();
    private final String field_151382_b;
    private static final String __OBFID = "CL_00001414";

    public JsonException(String p_i45279_1_)
    {
        this.field_151383_a.add(new JsonException.Entry(null));
        this.field_151382_b = p_i45279_1_;
    }

    public JsonException(String p_i45280_1_, Throwable p_i45280_2_)
    {
        super(p_i45280_2_);
        this.field_151383_a.add(new JsonException.Entry(null));
        this.field_151382_b = p_i45280_1_;
    }

    public void func_151380_a(String p_151380_1_)
    {
        ((JsonException.Entry)this.field_151383_a.get(0)).func_151373_a(p_151380_1_);
    }

    public void func_151381_b(String p_151381_1_)
    {
        ((JsonException.Entry)this.field_151383_a.get(0)).field_151376_a = p_151381_1_;
        this.field_151383_a.add(0, new JsonException.Entry(null));
    }

    public String getMessage()
    {
        return "Invalid " + ((JsonException.Entry)this.field_151383_a.get(this.field_151383_a.size() - 1)).toString() + ": " + this.field_151382_b;
    }

    public static JsonException func_151379_a(Exception p_151379_0_)
    {
        if (p_151379_0_ instanceof JsonException)
        {
            return (JsonException)p_151379_0_;
        }
        else
        {
            String s = p_151379_0_.getMessage();

            if (p_151379_0_ instanceof FileNotFoundException)
            {
                s = "File not found";
            }

            return new JsonException(s, p_151379_0_);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Entry
        {
            private String field_151376_a;
            private final List field_151375_b;
            private static final String __OBFID = "CL_00001416";

            private Entry()
            {
                this.field_151376_a = null;
                this.field_151375_b = Lists.newArrayList();
            }

            private void func_151373_a(String p_151373_1_)
            {
                this.field_151375_b.add(0, p_151373_1_);
            }

            public String func_151372_b()
            {
                return StringUtils.join(this.field_151375_b, "->");
            }

            public String toString()
            {
                return this.field_151376_a != null ? (!this.field_151375_b.isEmpty() ? this.field_151376_a + " " + this.func_151372_b() : this.field_151376_a) : (!this.field_151375_b.isEmpty() ? "(Unknown file) " + this.func_151372_b() : "(Unknown file)");
            }

            Entry(Object p_i45278_1_)
            {
                this();
            }
        }
}