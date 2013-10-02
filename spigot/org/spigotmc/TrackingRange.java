package org.spigotmc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayerMP;;

public class TrackingRange
{

    /**
     * Gets the range an entity should be 'tracked' by players and visible in
     * the client.
     *
     * @param entity
     * @param defaultRange Default range defined by Mojang
     * @return
     */
    public static int getEntityTrackingRange(Entity entity, int defaultRange)
    {
        SpigotWorldConfig config = entity.worldObj.spigotConfig;
        int range = defaultRange;
        if ( entity instanceof EntityPlayerMP )
        {
            range = config.playerTrackingRange;
        } else if ( entity.defaultActivationState || entity instanceof EntityGhast )
        {
            range = defaultRange;
        } else if ( entity.activationType == 1 )
        {
            range = config.monsterTrackingRange;
        } else if ( entity.activationType == 2 )
        {
            range = config.animalTrackingRange;
        } else if ( entity instanceof EntityItemFrame || entity instanceof EntityPainting || entity instanceof EntityItem || entity instanceof EntityXPOrb )
        {
            range = config.miscTrackingRange;
        }
        // MCPC+ start - allow for 0 to disable tracking ranges
        if (range == 0)
        {
            return defaultRange;
        }
        // MCPC+ end

        return Math.min( config.maxTrackingRange, range );
    }
}
