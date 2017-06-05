package net.minecraftforge.items.wrapper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.items.IItemHandler;

/**
 * Exposes the armor inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using {@link EntityLivingBase#getItemStackFromSlot} and
 * {@link EntityLivingBase#setItemStackToSlot}.
 */
public class EntityArmorInvWrapper extends EntityEquipmentInvWrapper
{
    public EntityArmorInvWrapper(final EntityLivingBase entity)
    {
        super(entity, EntityEquipmentSlot.Type.ARMOR);
    }
}
