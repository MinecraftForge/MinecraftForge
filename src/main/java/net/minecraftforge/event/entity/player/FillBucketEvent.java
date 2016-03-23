package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@Cancelable
@Event.HasResult
public class FillBucketEvent extends PlayerEvent
{
    /**
     * This event is fired when a player attempts to use a Empty bucket, it
     * can be canceled to completely prevent any further processing.
     *
     * If you set the result to 'ALLOW', it means that you have processed
     * the event and wants the basic functionality of adding the new
     * ItemStack to your inventory and reducing the stack size to process.
     * setResult(ALLOW) is the same as the old setHandled();
     */

    private final ItemStack current;
    private final World world;
    private final RayTraceResult target;

    private ItemStack result;

    public FillBucketEvent(EntityPlayer player, ItemStack current, World world, RayTraceResult target)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }

    public ItemStack getEmptyBucket() { return this.current; }
    public World getWorld(){ return this.world; }
    public RayTraceResult getTarget() { return this.target; }
    public ItemStack getFilledBucket() { return this.result; }
    public void setFilledBucket(ItemStack bucket) { this.result = bucket; }
}
