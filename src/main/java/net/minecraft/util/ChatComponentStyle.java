package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent
{
    protected List field_150264_a = Lists.newArrayList();
    private ChatStyle field_150263_b;
    private static final String __OBFID = "CL_00001257";

    public IChatComponent func_150257_a(IChatComponent p_150257_1_)
    {
        p_150257_1_.func_150256_b().func_150221_a(this.func_150256_b());
        this.field_150264_a.add(p_150257_1_);
        return this;
    }

    public List func_150253_a()
    {
        return this.field_150264_a;
    }

    public IChatComponent func_150258_a(String p_150258_1_)
    {
        return this.func_150257_a(new ChatComponentText(p_150258_1_));
    }

    public IChatComponent func_150255_a(ChatStyle p_150255_1_)
    {
        this.field_150263_b = p_150255_1_;
        Iterator iterator = this.field_150264_a.iterator();

        while (iterator.hasNext())
        {
            IChatComponent ichatcomponent = (IChatComponent)iterator.next();
            ichatcomponent.func_150256_b().func_150221_a(this.func_150256_b());
        }

        return this;
    }

    public ChatStyle func_150256_b()
    {
        if (this.field_150263_b == null)
        {
            this.field_150263_b = new ChatStyle();
            Iterator iterator = this.field_150264_a.iterator();

            while (iterator.hasNext())
            {
                IChatComponent ichatcomponent = (IChatComponent)iterator.next();
                ichatcomponent.func_150256_b().func_150221_a(this.field_150263_b);
            }
        }

        return this.field_150263_b;
    }

    public Iterator iterator()
    {
        return Iterators.concat(Iterators.forArray(new ChatComponentStyle[] {this}), func_150262_a(this.field_150264_a));
    }

    public final String func_150260_c()
    {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.iterator();

        while (iterator.hasNext())
        {
            IChatComponent ichatcomponent = (IChatComponent)iterator.next();
            stringbuilder.append(ichatcomponent.func_150261_e());
        }

        return stringbuilder.toString();
    }

    @SideOnly(Side.CLIENT)
    public final String func_150254_d()
    {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.iterator();

        while (iterator.hasNext())
        {
            IChatComponent ichatcomponent = (IChatComponent)iterator.next();
            stringbuilder.append(ichatcomponent.func_150256_b().func_150218_j());
            stringbuilder.append(ichatcomponent.func_150261_e());
            stringbuilder.append(EnumChatFormatting.RESET);
        }

        return stringbuilder.toString();
    }

    public static Iterator func_150262_a(Iterable p_150262_0_)
    {
        Iterator iterator = Iterators.concat(Iterators.transform(p_150262_0_.iterator(), new Function()
        {
            private static final String __OBFID = "CL_00001258";
            public Iterator apply(IChatComponent p_150665_1_)
            {
                return p_150665_1_.iterator();
            }
            public Object apply(Object par1Obj)
            {
                return this.apply((IChatComponent)par1Obj);
            }
        }));
        iterator = Iterators.transform(iterator, new Function()
        {
            private static final String __OBFID = "CL_00001259";
            public IChatComponent apply(IChatComponent p_150666_1_)
            {
                IChatComponent ichatcomponent1 = p_150666_1_.func_150259_f();
                ichatcomponent1.func_150255_a(ichatcomponent1.func_150256_b().func_150206_m());
                return ichatcomponent1;
            }
            public Object apply(Object par1Obj)
            {
                return this.apply((IChatComponent)par1Obj);
            }
        });
        return iterator;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ChatComponentStyle))
        {
            return false;
        }
        else
        {
            ChatComponentStyle chatcomponentstyle = (ChatComponentStyle)par1Obj;
            return this.field_150264_a.equals(chatcomponentstyle.field_150264_a) && this.func_150256_b().equals(chatcomponentstyle.func_150256_b());
        }
    }

    public int hashCode()
    {
        return 31 * this.field_150263_b.hashCode() + this.field_150264_a.hashCode();
    }

    public String toString()
    {
        return "BaseComponent{style=" + this.field_150263_b + ", siblings=" + this.field_150264_a + '}';
    }
}