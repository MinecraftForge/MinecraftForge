package net.minecraft.src;

public interface ICommandListener
{
    /**
     * Logs the message with a level of INFO.
     */
    void log(String var1);

    /**
     * Gets the players username.
     */
    String getUsername();
}
