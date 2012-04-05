package cpw.mods.fml.common;

import java.util.logging.Logger;

public interface IFMLSidedHandler
{
    Logger getMinecraftLogger();
    boolean isModLoaderMod(Class<?> clazz);
    ModContainer loadBaseModMod(Class<?> clazz, String canonicalPath);
    boolean isServer();
    boolean isClient();
}
