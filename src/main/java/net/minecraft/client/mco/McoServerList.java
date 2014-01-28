package net.minecraft.client.mco;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class McoServerList
{
    private static final Logger field_148490_a = LogManager.getLogger();
    private volatile boolean field_148488_b;
    private McoServerList.UpdateTask field_148489_c = new McoServerList.UpdateTask(null);
    private Timer field_148486_d = new Timer();
    private Set field_148487_e = Sets.newHashSet();
    private List field_148484_f = Lists.newArrayList();
    private int field_148485_g;
    private boolean field_148492_h;
    private Session field_148493_i;
    private int field_148491_j;
    private static final String __OBFID = "CL_00000803";

    public McoServerList()
    {
        this.field_148486_d.schedule(this.field_148489_c, 0L, 10000L);
        this.field_148493_i = Minecraft.getMinecraft().getSession();
    }

    public synchronized void func_148475_a(Session p_148475_1_)
    {
        this.field_148493_i = p_148475_1_;

        if (this.field_148488_b)
        {
            this.field_148488_b = false;
            this.field_148489_c = new McoServerList.UpdateTask(null);
            this.field_148486_d = new Timer();
            this.field_148486_d.schedule(this.field_148489_c, 0L, 10000L);
        }
    }

    public synchronized boolean func_148472_a()
    {
        return this.field_148492_h;
    }

    public synchronized void func_148479_b()
    {
        this.field_148492_h = false;
    }

    public synchronized List func_148473_c()
    {
        return Lists.newArrayList(this.field_148484_f);
    }

    public int func_148468_d()
    {
        return this.field_148485_g;
    }

    public int func_148469_e()
    {
        return this.field_148491_j;
    }

    public synchronized void func_148476_f()
    {
        this.field_148488_b = true;
        this.field_148489_c.cancel();
        this.field_148486_d.cancel();
    }

    private synchronized void func_148474_a(List p_148474_1_)
    {
        int i = 0;
        Iterator iterator = this.field_148487_e.iterator();

        while (iterator.hasNext())
        {
            McoServer mcoserver = (McoServer)iterator.next();

            if (p_148474_1_.remove(mcoserver))
            {
                ++i;
            }
        }

        if (i == 0)
        {
            this.field_148487_e.clear();
        }

        this.field_148484_f = p_148474_1_;
        this.field_148492_h = true;
    }

    public synchronized void func_148470_a(McoServer p_148470_1_)
    {
        this.field_148484_f.remove(p_148470_1_);
        this.field_148487_e.add(p_148470_1_);
    }

    private void func_148471_a(int p_148471_1_)
    {
        this.field_148485_g = p_148471_1_;
    }

    @SideOnly(Side.CLIENT)
    class UpdateTask extends TimerTask
    {
        private McoClient field_148498_b;
        private static final String __OBFID = "CL_00000805";

        private UpdateTask() {}

        public void run()
        {
            if (!McoServerList.this.field_148488_b)
            {
                this.func_148496_c();
                this.func_148494_a();
                this.func_148495_b();
            }
        }

        private void func_148494_a()
        {
            try
            {
                if (McoServerList.this.field_148493_i != null)
                {
                    this.field_148498_b = new McoClient(McoServerList.this.field_148493_i.getSessionID(), McoServerList.this.field_148493_i.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
                    List list = this.field_148498_b.func_148703_a().field_148772_a;

                    if (list != null)
                    {
                        this.func_148497_a(list);
                        McoServerList.this.func_148474_a(list);
                    }
                }
            }
            catch (ExceptionMcoService exceptionmcoservice)
            {
                McoServerList.field_148490_a.error("Couldn\'t get server list", exceptionmcoservice);
            }
            catch (IOException ioexception)
            {
                McoServerList.field_148490_a.error("Couldn\'t parse response from server getting list");
            }
        }

        private void func_148495_b()
        {
            try
            {
                if (McoServerList.this.field_148493_i != null)
                {
                    int i = this.field_148498_b.func_148701_f();
                    McoServerList.this.func_148471_a(i);
                }
            }
            catch (ExceptionMcoService exceptionmcoservice)
            {
                McoServerList.field_148490_a.error("Couldn\'t get pending invite count", exceptionmcoservice);
            }
        }

        private void func_148496_c()
        {
            try
            {
                if (McoServerList.this.field_148493_i != null)
                {
                    McoClient mcoclient = new McoClient(McoServerList.this.field_148493_i.getSessionID(), McoServerList.this.field_148493_i.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
                    McoServerList.this.field_148491_j = mcoclient.func_148702_d();
                }
            }
            catch (ExceptionMcoService exceptionmcoservice)
            {
                McoServerList.field_148490_a.error("Couldn\'t get token count", exceptionmcoservice);
                McoServerList.this.field_148491_j = 0;
            }
        }

        private void func_148497_a(List p_148497_1_)
        {
            Collections.sort(p_148497_1_, new McoServerList.UpdateTask.Comparator(McoServerList.this.field_148493_i.getUsername(), null));
        }

        UpdateTask(Object par2McoServerListEmptyAnon)
        {
            this();
        }

        @SideOnly(Side.CLIENT)
        class Comparator implements java.util.Comparator
        {
            private final String field_148504_b;
            private static final String __OBFID = "CL_00000806";

            private Comparator(String par2Str)
            {
                this.field_148504_b = par2Str;
            }

            public int compare(McoServer p_148503_1_, McoServer p_148503_2_)
            {
                if (p_148503_1_.field_148809_e.equals(p_148503_2_.field_148809_e))
                {
                    return p_148503_1_.field_148812_a < p_148503_2_.field_148812_a ? 1 : (p_148503_1_.field_148812_a > p_148503_2_.field_148812_a ? -1 : 0);
                }
                else if (p_148503_1_.field_148809_e.equals(this.field_148504_b))
                {
                    return -1;
                }
                else if (p_148503_2_.field_148809_e.equals(this.field_148504_b))
                {
                    return 1;
                }
                else
                {
                    if (p_148503_1_.field_148808_d.equals("CLOSED") || p_148503_2_.field_148808_d.equals("CLOSED"))
                    {
                        if (p_148503_1_.field_148808_d.equals("CLOSED"))
                        {
                            return 1;
                        }

                        if (p_148503_2_.field_148808_d.equals("CLOSED"))
                        {
                            return 0;
                        }
                    }

                    return p_148503_1_.field_148812_a < p_148503_2_.field_148812_a ? 1 : (p_148503_1_.field_148812_a > p_148503_2_.field_148812_a ? -1 : 0);
                }
            }

            public int compare(Object par1Obj, Object par2Obj)
            {
                return this.compare((McoServer)par1Obj, (McoServer)par2Obj);
            }

            Comparator(String par2Str, Object par3McoServerListEmptyAnon)
            {
                this(par2Str);
            }
        }
    }
}