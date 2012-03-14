package net.minecraft.src;

public interface IServer
{
    /**
     * Returns the specified property value as an int, or a default if the property doesn't exist
     */
    int getIntProperty(String var1, int var2);

    /**
     * Returns the specified property value as a String, or a default if the property doesn't exist
     */
    String getStringProperty(String var1, String var2);

    /**
     * Saves an Object with the given property name
     */
    void setProperty(String var1, Object var2);

    /**
     * Saves all of the server properties to the properties file
     */
    void saveProperties();

    /**
     * Returns the filename where server properties are stored
     */
    String getSettingsFilename();

    /**
     * Returns the server hostname
     */
    String getHostname();

    /**
     * Returns the server port
     */
    int getPort();

    /**
     * Returns the server message of the day
     */
    String getMotd();

    /**
     * Returns the server version string
     */
    String getVersionString();

    /**
     * Returns the number of players on the server
     */
    int playersOnline();

    /**
     * Returns the maximum number of players allowed on the server
     */
    int getMaxPlayers();

    /**
     * Returns a list of usernames of all connected players
     */
    String[] getPlayerNamesAsList();

    /**
     * Returns the name of the currently loaded world
     */
    String getWorldName();

    String getPlugin();

    void func_40010_o();

    /**
     * Handle a command received by an RCon instance
     */
    String handleRConCommand(String var1);

    /**
     * Returns true if debugging is enabled, false otherwise
     */
    boolean isDebuggingEnabled();

    /**
     * Logs the message with a level of INFO.
     */
    void log(String var1);

    /**
     * logs the warning same as: logger.warning(String);
     */
    void logWarning(String var1);

    /**
     * Log severe error message
     */
    void logSevere(String var1);

    void logIn(String var1);
}
