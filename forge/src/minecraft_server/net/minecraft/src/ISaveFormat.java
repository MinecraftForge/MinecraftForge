package net.minecraft.src;

public interface ISaveFormat
{
    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    boolean isOldMapFormat(String var1);

    /**
     * converts the map to mcRegion
     */
    boolean convertMapFormat(String var1, IProgressUpdate var2);
}
