package net.minecraftforge.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Implement this on your items to make them act as an Elytra
 */
public interface IElytra
{

    /**
     * True if this Elytra slos down when flying upwards
     * 
     * @param stack
     *            The itemstack
     * @param entity
     *            The user
     * @return
     */
    public boolean doesSlowOnUpwardsFlight(ItemStack stack, EntityLivingBase entity);

    /**
     * True if this Elytra takes damage whilst flying
     * 
     * @param stack
     *            The itemstack
     * @param entity
     *            The user
     * @return
     */
    public boolean doesDamageWhileFlying(ItemStack stack, EntityLivingBase entity);

    /**
     * True if this Elytra deals damage to it's wearer when hitting a wall
     * 
     * @param stack
     *            The itemstack
     * @param entity
     *            The user
     * @return
     */
    public boolean doesDamageUponHittingWall(ItemStack stack, EntityLivingBase entity);

    /**
     * Called every tick to check if this Elytra can continue flying
     * 
     * @param stack
     *            The itemstack
     * @param entity
     *            The user
     * @return
     */
    public boolean checkFlight(ItemStack stack, EntityLivingBase entity);

    /**
     * If this elytra takes damage when flying, this returns how many ticks there is between each damage point. Don't set this to zero!
     * 
     * @return
     */
    public int getDamageRate(ItemStack stack, EntityLivingBase entity);

    /**
     * Called every tick to change the user's acceleration.
     * 
     * @param stack
     *            The itemstack
     * @param entity
     *            The user
     */
    public void changeAcceleration(ItemStack stack, EntityLivingBase entity, double motionX, double motionY, double motionZ);

    /**
     * Called when the user hits the wall
     * 
     * @param stack
     *            The itemstack
     * @param entity
     *            The user
     */
    public void onHitWall(ItemStack stack, EntityLivingBase entity);
}