package net.minecraft.network.rcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public interface IServer
{
    // JAVADOC METHOD $$ func_71327_a
    int getIntProperty(String var1, int var2);

    // JAVADOC METHOD $$ func_71330_a
    String getStringProperty(String var1, String var2);

    // JAVADOC METHOD $$ func_71328_a
    void setProperty(String var1, Object var2);

    // JAVADOC METHOD $$ func_71326_a
    void saveProperties();

    // JAVADOC METHOD $$ func_71329_c
    String getSettingsFilename();

    // JAVADOC METHOD $$ func_71277_t
    String getHostname();

    // JAVADOC METHOD $$ func_71234_u
    int getPort();

    // JAVADOC METHOD $$ func_71274_v
    String getServerMOTD();

    // JAVADOC METHOD $$ func_71249_w
    String getMinecraftVersion();

    // JAVADOC METHOD $$ func_71233_x
    int getCurrentPlayerCount();

    // JAVADOC METHOD $$ func_71275_y
    int getMaxPlayers();

    // JAVADOC METHOD $$ func_71213_z
    String[] getAllUsernames();

    String getFolderName();

    // JAVADOC METHOD $$ func_71258_A
    String getPlugins();

    String executeCommand(String var1);

    // JAVADOC METHOD $$ func_71239_B
    boolean isDebuggingEnabled();

    // JAVADOC METHOD $$ func_71244_g
    void logInfo(String var1);

    // JAVADOC METHOD $$ func_71236_h
    void logWarning(String var1);

    // JAVADOC METHOD $$ func_71201_j
    void logSevere(String var1);

    // JAVADOC METHOD $$ func_71198_k
    void logDebug(String var1);
}