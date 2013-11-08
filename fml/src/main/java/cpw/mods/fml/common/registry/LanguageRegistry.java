/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.registry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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
        return getStringLocalization(key, FMLCommonHandler.instance().getCurrentLanguage());
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
//        // reload language table by forcing lang to null and reloading the properties file
//        String lang = StringTranslate.func_74808_a().func_74811_c();
//        StringTranslate.func_74808_a().func_74810_a(lang, true);
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

    public void loadLanguageTable(Map field_135032_a, String lang)
    {
        Properties usPack=modLanguageData.get("en_US");
        if (usPack!=null) {
            field_135032_a.putAll(usPack);
        }
        Properties langPack=modLanguageData.get(lang);
        if (langPack==null) {
            return;
        }
        field_135032_a.putAll(langPack);
    }

    public void loadLocalization(String localizationFile, String lang, boolean isXML)
    {
        URL urlResource = this.getClass().getResource(localizationFile);
        if (urlResource != null)
        {
            loadLocalization(urlResource, lang, isXML);
        }
        else
        {
            ModContainer activeModContainer = Loader.instance().activeModContainer();
            if (activeModContainer!=null)
            {
                FMLLog.log(activeModContainer.getModId(), Level.SEVERE, "The language resource %s cannot be located on the classpath. This is a programming error.", localizationFile);
            }
            else
            {
                FMLLog.log(Level.SEVERE, "The language resource %s cannot be located on the classpath. This is a programming error.", localizationFile);
            }
        }
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
            FMLLog.log(Level.SEVERE, e, "Unable to load localization from file %s", localizationFile);
        }
        finally    {
            try    {
                if (langStream != null)    {
                    langStream.close();
                }
            }
            catch (IOException ex) {
                // HUSH
            }
        }
    }
}
