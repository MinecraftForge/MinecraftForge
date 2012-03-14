package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeMap;

public class StringTranslate
{
    /** Is the private singleton instance of StringTranslate. */
    private static StringTranslate instance = new StringTranslate();

    /**
     * Contains all key/value pairs to be translated - is loaded from '/lang/en_US.lang' when the StringTranslate is
     * created.
     */
    private Properties translateTable = new Properties();
    private TreeMap languageList;
    private String currentLanguage;
    private boolean isUnicode;

    private StringTranslate()
    {
        this.loadLanguageList();
        this.setLanguage("en_US");
    }

    /**
     * Return the StringTranslate singleton instance
     */
    public static StringTranslate getInstance()
    {
        return instance;
    }

    private void loadLanguageList()
    {
        TreeMap var1 = new TreeMap();

        try
        {
            BufferedReader var2 = new BufferedReader(new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/languages.txt"), "UTF-8"));

            for (String var3 = var2.readLine(); var3 != null; var3 = var2.readLine())
            {
                String[] var4 = var3.split("=");

                if (var4 != null && var4.length == 2)
                {
                    var1.put(var4[0], var4[1]);
                }
            }
        }
        catch (IOException var5)
        {
            var5.printStackTrace();
            return;
        }

        this.languageList = var1;
    }

    public TreeMap getLanguageList()
    {
        return this.languageList;
    }

    private void loadLanguage(Properties par1Properties, String par2Str) throws IOException
    {
        BufferedReader var3 = new BufferedReader(new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/" + par2Str + ".lang"), "UTF-8"));

        for (String var4 = var3.readLine(); var4 != null; var4 = var3.readLine())
        {
            var4 = var4.trim();

            if (!var4.startsWith("#"))
            {
                String[] var5 = var4.split("=");

                if (var5 != null && var5.length == 2)
                {
                    par1Properties.setProperty(var5[0], var5[1]);
                }
            }
        }
    }

    public void setLanguage(String par1Str)
    {
        if (!par1Str.equals(this.currentLanguage))
        {
            Properties var2 = new Properties();

            try
            {
                this.loadLanguage(var2, "en_US");
            }
            catch (IOException var8)
            {
                ;
            }

            this.isUnicode = false;

            if (!"en_US".equals(par1Str))
            {
                try
                {
                    this.loadLanguage(var2, par1Str);
                    Enumeration var3 = var2.propertyNames();

                    while (var3.hasMoreElements() && !this.isUnicode)
                    {
                        Object var4 = var3.nextElement();
                        Object var5 = var2.get(var4);

                        if (var5 != null)
                        {
                            String var6 = var5.toString();

                            for (int var7 = 0; var7 < var6.length(); ++var7)
                            {
                                if (var6.charAt(var7) >= 256)
                                {
                                    this.isUnicode = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (IOException var9)
                {
                    var9.printStackTrace();
                    return;
                }
            }

            this.currentLanguage = par1Str;
            this.translateTable = var2;
        }
    }

    public String getCurrentLanguage()
    {
        return this.currentLanguage;
    }

    public boolean isUnicode()
    {
        return this.isUnicode;
    }

    /**
     * Translate a key to current language.
     */
    public String translateKey(String par1Str)
    {
        return this.translateTable.getProperty(par1Str, par1Str);
    }

    /**
     * Translate a key to current language applying String.format()
     */
    public String translateKeyFormat(String par1Str, Object ... par2ArrayOfObj)
    {
        String var3 = this.translateTable.getProperty(par1Str, par1Str);
        return String.format(var3, par2ArrayOfObj);
    }

    /**
     * Translate a key with a extra '.name' at end added, is used by blocks and items.
     */
    public String translateNamedKey(String par1Str)
    {
        return this.translateTable.getProperty(par1Str + ".name", "");
    }

    public static boolean isBidrectional(String par0Str)
    {
        return "ar_SA".equals(par0Str) || "he_IL".equals(par0Str);
    }
}
