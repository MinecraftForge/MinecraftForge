package net.minecraft.src;

import java.net.URL;

public class SoundPoolEntry
{
    public String soundName;
    public URL soundUrl;

    public SoundPoolEntry(String par1Str, URL par2URL)
    {
        this.soundName = par1Str;
        this.soundUrl = par2URL;
    }
}
