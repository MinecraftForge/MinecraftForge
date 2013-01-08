package cpw.mods.fml.common.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringTranslate;

public class LanguageRegistry
{
    private static final LanguageRegistry INSTANCE = new LanguageRegistry();

    private Map<String,Properties> modLanguageData=new HashMap<String,Properties>();

    public static LanguageRegistry instance()
    {
        return INSTANCE;
    }

    public String getStringLocalization(String key)
    {
        return getStringLocalization(key, StringTranslate.func_74808_a().func_74811_c());
    }

    public String getStringLocalization(String key, String lang)
    {
        String localizedString = "";
        Properties langPack = modLanguageData.get(lang);

        if (langPack != null) {
            if (langPack.getProperty(key) != null) {
                localizedString = langPack.getProperty(key);
            }
        }

        return localizedString;
    }

    public void addStringLocalization(String key, String value)
    {
        addStringLocalization(key, "en_US", value);
    }
    public void addStringLocalization(String key, String lang, String value)
    {
        Properties langPack=modLanguageData.get(lang);
        if (langPack==null) {
            langPack=new Properties();
            modLanguageData.put(lang, langPack);
        }
        langPack.put(key,value);
    }

    public void addStringLocalization(Properties langPackAdditions) {
        addStringLocalization(langPackAdditions, "en_US");
    }

    public void addStringLocalization(Properties langPackAdditions, String lang) {
        Properties langPack = modLanguageData.get(lang);
        if (langPack == null) {
            langPack = new Properties();
            modLanguageData.put(lang, langPack);
        }
        if (langPackAdditions != null) {
            langPack.putAll(langPackAdditions);
        }
    }

    public static void reloadLanguageTable()
    {
        // reload language table by forcing lang to null and reloading the properties file
        String lang = StringTranslate.func_74808_a().func_74811_c();
        StringTranslate.func_74808_a().field_74813_d = null;
        StringTranslate.func_74808_a().func_74810_a(lang);
    }


    public void addNameForObject(Object objectToName, String lang, String name)
    {
        String objectName;
        if (objectToName instanceof Item) {
            objectName=((Item)objectToName).func_77658_a();
        } else if (objectToName instanceof Block) {
            objectName=((Block)objectToName).func_71917_a();
        } else if (objectToName instanceof ItemStack) {
            objectName=((ItemStack)objectToName).func_77973_b().func_77667_c((ItemStack)objectToName);
        } else {
            throw new IllegalArgumentException(String.format("Illegal object for naming %s",objectToName));
        }
        objectName+=".name";
        addStringLocalization(objectName, lang, name);
    }

    public static void addName(Object objectToName, String name)
    {
        instance().addNameForObject(objectToName, "en_US", name);
    }

    public void loadLanguageTable(Properties languagePack, String lang)
    {
        Properties usPack=modLanguageData.get("en_US");
        if (usPack!=null) {
            languagePack.putAll(usPack);
        }
        Properties langPack=modLanguageData.get(lang);
        if (langPack==null) {
            return;
        }
        languagePack.putAll(langPack);
    }

    public void loadLocalization(String localizationFile, String lang, boolean isXML)
    {
        loadLocalization(this.getClass().getResource(localizationFile), lang, isXML);
    }

    public void loadLocalization(URL localizationFile, String lang, boolean isXML)
    {
        InputStream langStream = null;
        Properties langPack = new Properties();

        try    {
            langStream = localizationFile.openStream();

            if (isXML) {
                langPack.loadFromXML(langStream);
            }
            else {
                langPack.load(new InputStreamReader(langStream,Charsets.UTF_8));
            }

            addStringLocalization(langPack, lang);
        }
        catch (IOException e) {
            FMLLog.getLogger().severe("Unable to load localization from file: " + localizationFile);
            e.printStackTrace();
        }
        finally    {
            try    {
                if (langStream != null)    {
                    langStream.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
