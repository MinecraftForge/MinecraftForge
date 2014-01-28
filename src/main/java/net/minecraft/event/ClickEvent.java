package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent
{
    private final ClickEvent.Action field_150671_a;
    private final String field_150670_b;
    private static final String __OBFID = "CL_00001260";

    public ClickEvent(ClickEvent.Action p_i45156_1_, String p_i45156_2_)
    {
        this.field_150671_a = p_i45156_1_;
        this.field_150670_b = p_i45156_2_;
    }

    public ClickEvent.Action func_150669_a()
    {
        return this.field_150671_a;
    }

    public String func_150668_b()
    {
        return this.field_150670_b;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (par1Obj != null && this.getClass() == par1Obj.getClass())
        {
            ClickEvent clickevent = (ClickEvent)par1Obj;

            if (this.field_150671_a != clickevent.field_150671_a)
            {
                return false;
            }
            else
            {
                if (this.field_150670_b != null)
                {
                    if (!this.field_150670_b.equals(clickevent.field_150670_b))
                    {
                        return false;
                    }
                }
                else if (clickevent.field_150670_b != null)
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
        return "ClickEvent{action=" + this.field_150671_a + ", value=\'" + this.field_150670_b + '\'' + '}';
    }

    public int hashCode()
    {
        int i = this.field_150671_a.hashCode();
        i = 31 * i + (this.field_150670_b != null ? this.field_150670_b.hashCode() : 0);
        return i;
    }

    public static enum Action
    {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        SUGGEST_COMMAND("suggest_command", true);
        private static final Map field_150679_e = Maps.newHashMap();
        private final boolean field_150676_f;
        private final String field_150677_g;

        private static final String __OBFID = "CL_00001261";

        private Action(String p_i45155_3_, boolean p_i45155_4_)
        {
            this.field_150677_g = p_i45155_3_;
            this.field_150676_f = p_i45155_4_;
        }

        public boolean func_150674_a()
        {
            return this.field_150676_f;
        }

        public String func_150673_b()
        {
            return this.field_150677_g;
        }

        public static ClickEvent.Action func_150672_a(String p_150672_0_)
        {
            return (ClickEvent.Action)field_150679_e.get(p_150672_0_);
        }

        static
        {
            ClickEvent.Action[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                ClickEvent.Action var3 = var0[var2];
                field_150679_e.put(var3.func_150673_b(), var3);
            }
        }
    }
}