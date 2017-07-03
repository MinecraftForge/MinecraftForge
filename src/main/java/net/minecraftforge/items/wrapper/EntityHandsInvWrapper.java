package net.minecraftforge.items.wrapper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.items.IItemHandler;

/**
 * Exposes the hands inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using {@link EntityLivingBase#getItemStackFromSlot} and
 * {@link EntityLivingBase#setItemStackToSlot}.
 */
public class EntityHandsInvWrapper extends EntityEquipmentInvWrapper
{
    public EntityHandsInvWrapper(EntityLivingBase entity)
    {
        super(entity, EntityEquipmentSlot.Type.HAND);
    }
}
