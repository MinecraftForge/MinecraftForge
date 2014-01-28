package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public class HoverEvent
{
    private final HoverEvent.Action field_150704_a;
    private final IChatComponent field_150703_b;
    private static final String __OBFID = "CL_00001264";

    public HoverEvent(HoverEvent.Action p_i45158_1_, IChatComponent p_i45158_2_)
    {
        this.field_150704_a = p_i45158_1_;
        this.field_150703_b = p_i45158_2_;
    }

    public HoverEvent.Action func_150701_a()
    {
        return this.field_150704_a;
    }

    public IChatComponent func_150702_b()
    {
        return this.field_150703_b;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            HoverEvent hoverevent = (HoverEvent)par1Obj;

            if (this.field_150704_a != hoverevent.field_150704_a)
            {
                return false;
            }
            else
            {
                if (this.field_150703_b != null)
                {
                    if (!this.field_150703_b.equals(hoverevent.field_150703_b))
                    {
                        return false;
                    }
                }
                else if (hoverevent.field_150703_b != null)
                {
                    return false;
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return "HoverEvent{action=" + this.field_150704_a + ", value=\'" + this.field_150703_b + '\'' + '}';
    }

    public int hashCode()
    {
        int i = this.field_150704_a.hashCode();
        i = 31 * i + (this.field_150703_b != null ? this.field_150703_b.hashCode() : 0);
        return i;
    }

    public static enum Action
    {
        SHOW_TEXT("show_text", true),
        SHOW_ACHIEVEMENT("show_achievement", true),
        SHOW_ITEM("show_item", true);
        private static final Map field_150690_d = Maps.newHashMap();
        private final boolean field_150691_e;
        private final String field_150688_f;

        private static final String __OBFID = "CL_00001265";

        private Action(String p_i45157_3_, boolean p_i45157_4_)
        {
            this.field_150688_f = p_i45157_3_;
            this.field_150691_e = p_i45157_4_;
        }

        public boolean func_150686_a()
        {
            return this.field_150691_e;
        }

        public String func_150685_b()
        {
            return this.field_150688_f;
        }

        public static HoverEvent.Action func_150684_a(String p_150684_0_)
        {
            return (HoverEvent.Action)field_150690_d.get(p_150684_0_);
        }

        static
        {
            HoverEvent.Action[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                HoverEvent.Action var3 = var0[var2];
                field_150690_d.put(var3.func_150685_b(), var3);
            }
        }
    }
}