package net.minecraft.src;

public interface IProgressUpdate
{
    void displaySavingString(String var1);

    /**
     * Displays a string on the loading screen supposed to indicate what is being done currently.
     */
    void displayLoadingString(String var1);

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    void setLoadingProgress(int var1);
}
