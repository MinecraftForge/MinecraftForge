package net.minecraft.client.mco;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SideOnly(Side.CLIENT)
public class McoServer
{
    public long field_148812_a;
    public String field_148810_b;
    public String field_148811_c;
    public String field_148808_d;
    public String field_148809_e;
    public List field_148806_f;
    public String field_148807_g;
    public boolean field_148819_h;
    public int field_148820_i;
    public int field_148817_j;
    public int field_148818_k;
    public int field_148815_l;
    public String field_148816_m = "";
    public String field_148813_n = "\u00a770";
    public boolean field_148814_o = false;
    private static final String __OBFID = "CL_00001166";

    public String func_148800_a()
    {
        return this.field_148811_c;
    }

    public String func_148801_b()
    {
        return this.field_148810_b;
    }

    public void func_148803_a(String p_148803_1_)
    {
        this.field_148810_b = p_148803_1_;
    }

    public void func_148804_b(String p_148804_1_)
    {
        this.field_148811_c = p_148804_1_;
    }

    public void func_148799_a(McoServer p_148799_1_)
    {
        this.field_148816_m = p_148799_1_.field_148816_m;
        this.field_148815_l = p_148799_1_.field_148815_l;
    }

    public static McoServer func_148802_a(JsonObject p_148802_0_)
    {
        McoServer mcoserver = new McoServer();

        try
        {
            mcoserver.field_148812_a = !p_148802_0_.get("id").isJsonNull() ? p_148802_0_.get("id").getAsLong() : -1L;
            mcoserver.field_148810_b = !p_148802_0_.get("name").isJsonNull() ? p_148802_0_.get("name").getAsString() : null;
            mcoserver.field_148811_c = !p_148802_0_.get("motd").isJsonNull() ? p_148802_0_.get("motd").getAsString() : null;
            mcoserver.field_148808_d = !p_148802_0_.get("state").isJsonNull() ? p_148802_0_.get("state").getAsString() : McoServer.State.CLOSED.name();
            mcoserver.field_148809_e = !p_148802_0_.get("owner").isJsonNull() ? p_148802_0_.get("owner").getAsString() : null;

            if (p_148802_0_.get("invited").isJsonArray())
            {
                mcoserver.field_148806_f = func_148798_a(p_148802_0_.get("invited").getAsJsonArray());
            }
            else
            {
                mcoserver.field_148806_f = new ArrayList();
            }

            mcoserver.field_148818_k = !p_148802_0_.get("daysLeft").isJsonNull() ? p_148802_0_.get("daysLeft").getAsInt() : 0;
            mcoserver.field_148807_g = !p_148802_0_.get("ip").isJsonNull() ? p_148802_0_.get("ip").getAsString() : null;
            mcoserver.field_148819_h = !p_148802_0_.get("expired").isJsonNull() && p_148802_0_.get("expired").getAsBoolean();
            mcoserver.field_148820_i = !p_148802_0_.get("difficulty").isJsonNull() ? p_148802_0_.get("difficulty").getAsInt() : 0;
            mcoserver.field_148817_j = !p_148802_0_.get("gameMode").isJsonNull() ? p_148802_0_.get("gameMode").getAsInt() : 0;
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            ;
        }

        return mcoserver;
    }

    private static List func_148798_a(JsonArray p_148798_0_)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = p_148798_0_.iterator();

        while (iterator.hasNext())
        {
            arraylist.add(((JsonElement)iterator.next()).getAsString());
        }

        return arraylist;
    }

    public static McoServer func_148805_c(String p_148805_0_)
    {
        McoServer mcoserver = new McoServer();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_148805_0_).getAsJsonObject();
            mcoserver = func_148802_a(jsonobject);
        }
        catch (JsonIOException jsonioexception)
        {
            ;
        }
        catch (JsonSyntaxException jsonsyntaxexception)
        {
            ;
        }

        return mcoserver;
    }

    public int hashCode()
    {
        return (new HashCodeBuilder(17, 37)).append(this.field_148812_a).append(this.field_148810_b).append(this.field_148811_c).append(this.field_148808_d).append(this.field_148809_e).append(this.field_148819_h).toHashCode();
    }

    public boolean equals(Object par1Obj)
    {
        if (par1Obj == null)
        {
            return false;
        }
        else if (par1Obj == this)
        {
            return true;
        }
        else if (par1Obj.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            McoServer mcoserver = (McoServer)par1Obj;
            return (new EqualsBuilder()).append(this.field_148812_a, mcoserver.field_148812_a).append(this.field_148810_b, mcoserver.field_148810_b).append(this.field_148811_c, mcoserver.field_148811_c).append(this.field_148808_d, mcoserver.field_148808_d).append(this.field_148809_e, mcoserver.field_148809_e).append(this.field_148819_h, mcoserver.field_148819_h).isEquals();
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum State
    {
        CLOSED,
        OPEN,
        ADMIN_LOCK;

        private static final String __OBFID = "CL_00001167";
    }
}