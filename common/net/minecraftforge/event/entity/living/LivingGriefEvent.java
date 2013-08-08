/**
 * 
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.Cancelable;

/**
 * Event for when a mob tries to 'grief' be it explosion or taking blocks.
 * @author Katrina
 *
 */
@Cancelable
public class LivingGriefEvent extends LivingEvent {

    
    public final double targetX;
    public final double targetY;
    public final double targetZ;
    public final boolean place;
    public final int blockID;
    public final int metaData;
    
    /**
     * Constructor for the event
     * @param entity Entity trying to grief
     * @param targetX X Block trying to grief
     * @param targetY Y Block trying to grief
     * @param targetZ Z Block trying to grief
     * @param place Is it trying to place or take?
     * @param blockID ID of the block
     * @param metaData any metadata on the block
     */
    public LivingGriefEvent(EntityLivingBase entity,double targetX, double targetY, double targetZ,boolean place,int blockID,int metaData)
    {
        super(entity);
        this.targetX=targetX;
        this.targetY=targetY;
        this.targetZ=targetZ;
        this.place=place;
        this.blockID=blockID;
        this.metaData=metaData;
        
    }

}
