package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "localization_fallback_test", clientSideOnly = true, version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class LocalizationFallbackTest
{
    public static final boolean ENABLED = false;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED) return;

        Minecraft mc = Minecraft.getMinecraft();
        LanguageManager mgr = mc.getLanguageManager();
        IResourceManager resourceManager = mc.getResourceManager();

        Language originalLanguage = mgr.getCurrentLanguage();
        String string;

        // test en_gb
        mgr.setCurrentLanguage(mgr.getLanguage("en_gb"));
        mgr.onResourceManagerReload(resourceManager);
        string = I18n.format("localization_fallback_test.test_string");
        expectString("Test String en_GB.lang", string);
        string = I18n.format("localization_fallback_test.test_string_only_en");
        expectString("Test String only in en.lang", string);
        string = I18n.format("localization_fallback_test.test_string_only_en_us");
        expectString("Test String only in en_US.lang", string);

        // test en_us, which is the default fallback in vanilla
        mgr.setCurrentLanguage(mgr.getLanguage("en_us"));
        mgr.onResourceManagerReload(resourceManager);
        string = I18n.format("localization_fallback_test.test_string");
        expectString("Test String en_US.lang", string);
        string = I18n.format("localization_fallback_test.test_string_only_en");
        expectString("Test String only in en.lang", string);
        string = I18n.format("localization_fallback_test.test_string_only_en_us");
        expectString("Test String only in en_US.lang", string);

        // test a language with no localization defined, it should fall back on "es", "en_us", and "en"
        mgr.setCurrentLanguage(mgr.getLanguage("es_es"));
        mgr.onResourceManagerReload(resourceManager);
        string = I18n.format("localization_fallback_test.test_string");
        expectString("Test String es.lang", string);
        string = I18n.format("localization_fallback_test.test_string_only_en");
        expectString("Test String only in en.lang", string);
        string = I18n.format("localization_fallback_test.test_string_only_en_us");
        expectString("Test String only in en_US.lang", string);

        mgr.setCurrentLanguage(originalLanguage);
        mgr.onResourceManagerReload(resourceManager);
    }

    private static void expectString(String expectedString, String string)
    {
        if (!expectedString.equals(string))
        {
            throw new IllegalStateException("Localization fallback failed, expected '" + expectedString + "' but got '" + string + "'.");
        }
    }
}
