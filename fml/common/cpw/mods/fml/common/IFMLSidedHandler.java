package cpw.mods.fml.common;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public interface IFMLSidedHandler
{
    Logger getMinecraftLogger();
    File getMinecraftRootDirectory();
    boolean isModLoaderMod(Class<?> clazz);
    ModContainer loadBaseModMod(Class<?> clazz, File canonicalFile);
    boolean isServer();
    boolean isClient();
    Object getMinecraftInstance();
    String getCurrentLanguage();
    Properties getCurrentLanguageTable();
    String getObjectName(Object minecraftObject);
    ModMetadata readMetadataFrom(InputStream input, ModContainer mod) throws Exception;
}
