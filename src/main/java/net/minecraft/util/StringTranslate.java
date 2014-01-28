package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate
{
    private static final Pattern field_111053_a = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Splitter field_135065_b = Splitter.on('=').limit(2);
    private final Map languageList;
    // JAVADOC FIELD $$ field_74817_a
    private static StringTranslate instance = new StringTranslate();
    private long field_150511_e;
    private static final String __OBFID = "CL_00001212";

    public StringTranslate()
    {
        InputStream inputstream = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
        languageList = Maps.newHashMap();
        inject(this, inputstream);
    }

    public static void inject(InputStream inputstream)
    {
        inject(instance, inputstream);
    }

    private static void inject(StringTranslate inst, InputStream inputstream)
    {
        HashMap<String, String> map = parseLangFile(inputstream);
        inst.languageList.putAll(map);
        inst.field_150511_e = System.currentTimeMillis();
    }

    public static HashMap<String,String> parseLangFile(InputStream inputstream)
    {
        HashMap<String,String> table = Maps.newHashMap();
        try
        {
            Iterator iterator = IOUtils.readLines(inputstream, Charsets.UTF_8).iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();

                if (!s.isEmpty() && s.charAt(0) != 35)
                {
                    String[] astring = (String[])Iterables.toArray(field_135065_b.split(s), String.class);

                    if (astring != null && astring.length == 2)
                    {
                        String s1 = astring[0];
                        String s2 = field_111053_a.matcher(astring[1]).replaceAll("%$1s");
                        table.put(s1, s2);
                    }
                }
            }

        }
        catch (Exception ioexception)
        {
            ;
        }
        return table;
    }

    // JAVADOC METHOD $$ func_74808_a
    static StringTranslate getInstance()
    {
        // JAVADOC FIELD $$ field_74817_a
        return instance;
    }

    @SideOnly(Side.CLIENT)

    public static synchronized void func_135063_a(Map par0Map)
    {
        instance.languageList.clear();
        instance.languageList.putAll(par0Map);
        instance.field_150511_e = System.currentTimeMillis();
    }

    // JAVADOC METHOD $$ func_74805_b
    public synchronized String translateKey(String par1Str)
    {
        return this.func_135064_c(par1Str);
    }

    // JAVADOC METHOD $$ func_74803_a
    public synchronized String translateKeyFormat(String par1Str, Object ... par2ArrayOfObj)
    {
        String s1 = this.func_135064_c(par1Str);

        try
        {
            return String.format(s1, par2ArrayOfObj);
        }
        catch (IllegalFormatException illegalformatexception)
        {
            return "Format error: " + s1;
        }
    }

    private String func_135064_c(String par1Str)
    {
        String s1 = (String)this.languageList.get(par1Str);
        return s1 == null ? par1Str : s1;
    }

    public synchronized boolean containsTranslateKey(String par1Str)
    {
        return this.languageList.containsKey(par1Str);
    }

    public long func_150510_c()
    {
        return this.field_150511_e;
    }
}