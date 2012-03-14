package net.minecraft.src;

import java.util.List;

public interface ISaveFormat
{
    String getFormatName();

    /**
     * Returns back a loader for the specified save directory
     */
    ISaveHandler getSaveLoader(String var1, boolean var2);

    List getSaveList();

    void flushCache();

    WorldInfo getWorldInfo(String var1);

    /**
     * @args: Takes one argument - the name of the directory of the world to delete. @desc: Delete the world by deleting
     * the associated directory recursively.
     */
    void deleteWorldDirectory(String var1);

    /**
     * @args: Takes two arguments - first the name of the directory containing the world and second the new name for
     * that world. @desc: Renames the world by storing the new name in level.dat. It does *not* rename the directory
     * containing the world data.
     */
    void renameWorld(String var1, String var2);

    /**
     * Checks if the save directory uses the old map format
     */
    boolean isOldMapFormat(String var1);

    /**
     * Converts the specified map to the new map format. Args: worldName, loadingScreen
     */
    boolean convertMapFormat(String var1, IProgressUpdate var2);
}
