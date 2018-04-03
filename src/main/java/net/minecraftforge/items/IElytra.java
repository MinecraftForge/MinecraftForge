package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implement this on your items to make them act as an Elytra
 */
public interface IElytra{
    
     /**
     * True if this Elytra slos down when flying upwards
     * @param stack The itemstack
     * @param world The world
     * @return
     */
    public boolean doesSlowOnUpwardsFlight(ItemStack stack, World world);
    
    /**
     * True if this Elytra takes damage whilst flying
     * @param stack The itemstack
     * @param world The world
     * @return
     */
    public boolean doesDamageWhileFlying(ItemStack stack, World world);
    
    /**
     * True if this Elytra deals damage to it's wearer when hitting a wall
     * @param stack The itemstack
     * @param world The world
     * @return
     */
    public boolean doesDamageUponHittingWall(ItemStack stack, World world);
    
    /**
     * Called every tick to check if this Elytra can continue flying
     * @param stack The itemstack
     * @param world The world
     * @return
     */
    public boolean checkFlight(ItemStack stack, World world);
    
    /**
     * Multiplied by the user's motion when moving. Defaults to 1
     * @param stack The itemstack
     * @param world The world
     * @return
     */
    public float getAccelerationMultiplier(ItemStack stack, World world);
    
    /**
     * Called when the user hits the wall
     * @param stack The itemstack
     * @param world The world
     * @param pos The position of the user when hitting the wall
     */
    public void onHitWall(ItemStack stack, World world, BlockPos pos);
}