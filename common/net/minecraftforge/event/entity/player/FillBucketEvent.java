package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class FillBucketEvent extends PlayerEvent
{
    public final ItemStack current;
    public final World world;
    public final MovingObjectPosition target;
    
    public ItemStack result;
    private boolean handeled = false;
    
    /**
     * Called when a Player Rightclicks with a bucket in hand, regardless of what kind of Block or Entity he clicked.
     * Setting this event Canceled will have the same effect as rightclicking stone - nothing happens.
     * Setting this event Handeled will make the result ItemStack appear in the Player's hands instead, for example
     * a new Bucket with your custom liquid in it.
     * @param player Player using the Bucket
     * @param current Bucket ItemStack being used
     * @param world World this is happening in
     * @param target MovingObjectPosition the Player is targeting, can be NULL, a Block, or an Entity
     */
    public FillBucketEvent(EntityPlayer player, ItemStack current, World world, MovingObjectPosition target)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }
    
    public boolean isHandeled()
    {
        return handeled;
    }
    
    public void setHandeled()
    {
        handeled = true;
    }
}
