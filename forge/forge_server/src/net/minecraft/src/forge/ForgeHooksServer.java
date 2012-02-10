package net.minecraft.src.forge;

import java.util.Map;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityTracker;

public class ForgeHooksServer 
{
    /**
     * Called when a Entity is being added to a EntityTracker.
     * If we have valid info, register the entity. 
     * 
     * @param tracker The EntityTracker instance
     * @param entity The Entity to add
     * @return True if we registered the Entity
     */
    public static boolean OnTrackEntity(EntityTracker tracker, Entity entity)
    {
        EntityTrackerInfo info = MinecraftForge.getEntityTrackerInfo(entity, true);
        if (info != null)
        {
            tracker.trackEntity(entity, info.Range, info.UpdateFrequancy, info.SendVelocityInfo);
            return true;
        }
        return false;
    }
    

    private static boolean hasInit = false; 
    public static void init()
    {
        if (hasInit)
        {
            return;
        }
        hasInit = true;
        ForgeHooks.setPacketHandler(new PacketHandlerServer());
    }
    
    static 
    {
        init();
    }
}
