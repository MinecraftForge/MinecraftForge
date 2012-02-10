package net.minecraft.src.forge;

import net.minecraft.src.BaseMod;

public class EntityTrackerInfo 
{
    public final BaseMod Mod;
    public final int ID;
    public final int Range;
    public final int UpdateFrequancy;
    public final boolean SendVelocityInfo;    
    
    public EntityTrackerInfo(BaseMod mod, int ID, int range, int updateFrequancy, boolean sendVelocityInfo)
    {
        Mod = mod;
        this.ID = ID;
        Range = range;
        UpdateFrequancy = updateFrequancy;
        SendVelocityInfo = sendVelocityInfo;
    }    
}
