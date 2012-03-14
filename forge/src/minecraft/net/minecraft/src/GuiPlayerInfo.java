package net.minecraft.src;

public class GuiPlayerInfo
{
    /** The string value of the object */
    public String name;

    /** Player response time to server in milliseconds */
    public int responseTime;

    public GuiPlayerInfo(String par1Str)
    {
        this.name = par1Str;
    }
}
