package net.minecraft.src.forge;

import net.minecraft.src.BaseMod;

public class EntityTrackerInfo
{
    public final NetworkMod Mod;
    public final int ID;
    public final int Range;
    public final int UpdateFrequancy;
    public final boolean SendVelocityInfo;

    public EntityTrackerInfo(NetworkMod mod, int ID, int range, int updateFrequancy, boolean sendVelocityInfo)
    {
        Mod = mod;
        this.ID = ID;
        Range = range;
        UpdateFrequancy = updateFrequancy;
        SendVelocityInfo = sendVelocityInfo;
    }
}
