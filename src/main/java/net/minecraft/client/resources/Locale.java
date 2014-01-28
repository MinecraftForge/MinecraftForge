package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

@SideOnly(Side.CLIENT)
public class Locale
{
    // JAVADOC FIELD $$ field_135030_b
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    Map field_135032_a = Maps.newHashMap();
    private boolean field_135029_d;
    private static final String __OBFID = "CL_00001097";

    // JAVADOC METHOD $$ func_135022_a
    public synchronized void loadLocaleDataFiles(IResourceManager par1ResourceManager, List par2List)
    {
        this.field_135032_a.clear();
        Iterator iterator = par2List.iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            String s1 = String.format("lang/%s.lang", new Object[] {s});
            Iterator iterator1 = par1ResourceManager.getResourceDomains().iterator();

            while (iterator1.hasNext())
            {
                String s2 = (String)iterator1.next();

                try
                {
                    this.loadLocaleData(par1ResourceManager.getAllResources(new ResourceLocation(s2, s1)));
                }
                catch (IOException ioexception)
                {
                    ;
                }
            }
        }

        this.checkUnicode();
    }

    public boolean isUnicode()
    {
        return this.field_135029_d;
    }

    private void checkUnicode()
    {
        this.field_135029_d = false;
        int i = 0;
        int j = 0;
        Iterator iterator = this.field_135032_a.values().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            int k = s.length();
            j += k;

            for (int l = 0; l < k; ++l)
            {
                if (s.charAt(l) >= 256)
                {
                    ++i;
                }
            }
        }

        float f = (float)i / (float)j;
        this.field_135029_d = (double)f > 0.1D;
    }

    // JAVADOC METHOD $$ func_135028_a
    private void loadLocaleData(List par1List) throws IOException
    {
        Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            IResource iresource = (IResource)iterator.next();
            this.loadLocaleData(iresource.getInputStream());
        }
    }

    private void loadLocaleData(InputStream par1InputStream) throws IOException
    {
        Iterator iterator = IOUtils.readLines(par1InputStream, Charsets.UTF_8).iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            if (!s.isEmpty() && s.charAt(0) != 35)
            {
                String[] astring = (String[])Iterables.toArray(splitter.split(s), String.class);

                if (astring != null && astring.length == 2)
                {
                    String s1 = astring[0];
                    String s2 = field_135031_c.matcher(astring[1]).replaceAll("%$1s");
                    this.field_135032_a.put(s1, s2);
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_135026_c
    private String translateKeyPrivate(String par1Str)
    {
        String s1 = (String)this.field_135032_a.get(par1Str);
        return s1 == null ? par1Str : s1;
    }

    // JAVADOC METHOD $$ func_135023_a
    public String formatMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        String s1 = this.translateKeyPrivate(par1Str);

        try
        {
            return String.format(s1, par2ArrayOfObj);
        }
        catch (IllegalFormatException illegalformatexception)
        {
            return "Format error: " + s1;
        }
    }
}