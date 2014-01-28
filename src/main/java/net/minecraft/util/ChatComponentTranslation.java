package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentTranslation extends ChatComponentStyle
{
    private final String field_150276_d;
    private final Object[] field_150277_e;
    private final Object field_150274_f = new Object();
    private long field_150275_g = -1L;
    List field_150278_b = Lists.newArrayList();
    public static final Pattern field_150279_c = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    private static final String __OBFID = "CL_00001270";

    public ChatComponentTranslation(String p_i45160_1_, Object ... p_i45160_2_)
    {
        this.field_150276_d = p_i45160_1_;
        this.field_150277_e = p_i45160_2_;
        Object[] aobject = p_i45160_2_;
        int i = p_i45160_2_.length;

        for (int j = 0; j < i; ++j)
        {
            Object object1 = aobject[j];

            if (object1 instanceof IChatComponent)
            {
                ((IChatComponent)object1).func_150256_b().func_150221_a(this.func_150256_b());
            }
        }
    }

    synchronized void func_150270_g()
    {
        Object object = this.field_150274_f;

        synchronized (this.field_150274_f)
        {
            long i = StatCollector.func_150827_a();

            if (i == this.field_150275_g)
            {
                return;
            }

            this.field_150275_g = i;
            this.field_150278_b.clear();
        }

        try
        {
            this.func_150269_b(StatCollector.translateToLocal(this.field_150276_d));
        }
        catch (ChatComponentTranslationFormatException chatcomponenttranslationformatexception1)
        {
            this.field_150278_b.clear();

            try
            {
                this.func_150269_b(StatCollector.func_150826_b(this.field_150276_d));
            }
            catch (ChatComponentTranslationFormatException chatcomponenttranslationformatexception)
            {
                throw chatcomponenttranslationformatexception1;
            }
        }
    }

    protected void func_150269_b(String p_150269_1_)
    {
        boolean flag = false;
        Matcher matcher = field_150279_c.matcher(p_150269_1_);
        int i = 0;
        int j = 0;

        try
        {
            int l;

            for (; matcher.find(j); j = l)
            {
                int k = matcher.start();
                l = matcher.end();

                if (k > j)
                {
                    ChatComponentText chatcomponenttext = new ChatComponentText(String.format(p_150269_1_.substring(j, k), new Object[0]));
                    chatcomponenttext.func_150256_b().func_150221_a(this.func_150256_b());
                    this.field_150278_b.add(chatcomponenttext);
                }

                String s3 = matcher.group(2);
                String s1 = p_150269_1_.substring(k, l);

                if ("%".equals(s3) && "%%".equals(s1))
                {
                    ChatComponentText chatcomponenttext2 = new ChatComponentText("%");
                    chatcomponenttext2.func_150256_b().func_150221_a(this.func_150256_b());
                    this.field_150278_b.add(chatcomponenttext2);
                }
                else
                {
                    if (!"s".equals(s3))
                    {
                        throw new ChatComponentTranslationFormatException(this, "Unsupported format: \'" + s1 + "\'");
                    }

                    String s2 = matcher.group(1);
                    int i1 = s2 != null ? Integer.parseInt(s2) - 1 : i++;
                    this.field_150278_b.add(this.func_150272_a(i1));
                }
            }

            if (j < p_150269_1_.length())
            {
                ChatComponentText chatcomponenttext1 = new ChatComponentText(String.format(p_150269_1_.substring(j), new Object[0]));
                chatcomponenttext1.func_150256_b().func_150221_a(this.func_150256_b());
                this.field_150278_b.add(chatcomponenttext1);
            }
        }
        catch (IllegalFormatException illegalformatexception)
        {
            throw new ChatComponentTranslationFormatException(this, illegalformatexception);
        }
    }

    private IChatComponent func_150272_a(int p_150272_1_)
    {
        if (p_150272_1_ >= this.field_150277_e.length)
        {
            throw new ChatComponentTranslationFormatException(this, p_150272_1_);
        }
        else
        {
            Object object = this.field_150277_e[p_150272_1_];
            Object object1;

            if (object instanceof IChatComponent)
            {
                object1 = (IChatComponent)object;
            }
            else
            {
                object1 = new ChatComponentText(object.toString());
                ((IChatComponent)object1).func_150256_b().func_150221_a(this.func_150256_b());
            }

            return (IChatComponent)object1;
        }
    }

    public IChatComponent func_150255_a(ChatStyle p_150255_1_)
    {
        super.func_150255_a(p_150255_1_);
        Object[] aobject = this.field_150277_e;
        int i = aobject.length;

        for (int j = 0; j < i; ++j)
        {
            Object object = aobject[j];

            if (object instanceof IChatComponent)
            {
                ((IChatComponent)object).func_150256_b().func_150221_a(this.func_150256_b());
            }
        }

        if (this.field_150275_g > -1L)
        {
            Iterator iterator = this.field_150278_b.iterator();

            while (iterator.hasNext())
            {
                IChatComponent ichatcomponent = (IChatComponent)iterator.next();
                ichatcomponent.func_150256_b().func_150221_a(p_150255_1_);
            }
        }

        return this;
    }

    public Iterator iterator()
    {
        this.func_150270_g();
        return Iterators.concat(func_150262_a(this.field_150278_b), func_150262_a(this.field_150264_a));
    }

    public String func_150261_e()
    {
        this.func_150270_g();
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = this.field_150278_b.iterator();

        while (iterator.hasNext())
        {
            IChatComponent ichatcomponent = (IChatComponent)iterator.next();
            stringbuilder.append(ichatcomponent.func_150261_e());
        }

        return stringbuilder.toString();
    }

    public ChatComponentTranslation func_150259_f()
    {
        Object[] aobject = new Object[this.field_150277_e.length];

        for (int i = 0; i < this.field_150277_e.length; ++i)
        {
            if (this.field_150277_e[i] instanceof IChatComponent)
            {
                aobject[i] = ((IChatComponent)this.field_150277_e[i]).func_150259_f();
            }
            else
            {
                aobject[i] = this.field_150277_e[i];
            }
        }

        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(this.field_150276_d, aobject);
        chatcomponenttranslation.func_150255_a(this.func_150256_b().func_150232_l());
        Iterator iterator = this.func_150253_a().iterator();

        while (iterator.hasNext())
        {
            IChatComponent ichatcomponent = (IChatComponent)iterator.next();
            chatcomponenttranslation.func_150257_a(ichatcomponent.func_150259_f());
        }

        return chatcomponenttranslation;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ChatComponentTranslation))
        {
            return false;
        }
        else
        {
            ChatComponentTranslation chatcomponenttranslation = (ChatComponentTranslation)par1Obj;
            return Arrays.equals(this.field_150277_e, chatcomponenttranslation.field_150277_e) && this.field_150276_d.equals(chatcomponenttranslation.field_150276_d) && super.equals(par1Obj);
        }
    }

    public int hashCode()
    {
        int i = super.hashCode();
        i = 31 * i + this.field_150276_d.hashCode();
        i = 31 * i + Arrays.hashCode(this.field_150277_e);
        return i;
    }

    public String toString()
    {
        return "TranslatableComponent{key=\'" + this.field_150276_d + '\'' + ", args=" + Arrays.toString(this.field_150277_e) + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
    }

    public String func_150268_i()
    {
        return this.field_150276_d;
    }

    public Object[] func_150271_j()
    {
        return this.field_150277_e;
    }
}