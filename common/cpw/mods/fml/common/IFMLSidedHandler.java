package cpw.mods.fml.common;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import cpw.mods.fml.common.modloader.ModProperty;

public interface IFMLSidedHandler
{
    void profileStart(String profileLabel);

    void profileEnd();

    List<String> getAdditionalBrandingInformation();

    Side getSide();

    void haltGame(String message, Throwable exception);

    void showGuiScreen(Object clientGuiElement);
}
