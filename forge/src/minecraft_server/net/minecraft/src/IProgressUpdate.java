package net.minecraft.src;

public interface IProgressUpdate
{
    /**
     * Shows the 'Saving level' string.
     */
    void displaySavingString(String var1);

    void displayLoadingString(String var1);

    void setLoadingProgress(int var1);
}
