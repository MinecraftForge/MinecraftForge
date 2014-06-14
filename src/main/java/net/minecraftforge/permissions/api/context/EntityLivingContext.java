package net.minecraftforge.permissions.api.context;

import net.minecraft.entity.EntityLiving;

public class EntityLivingContext extends EntityContext
{
    private final float max, current;

    public EntityLivingContext(EntityLiving entity)
    {
        super(entity);
        max = entity.getMaxHealth();
        current = entity.getHealth();
    }

    public float getMaxHealth()
    {
        return max;
    }

    public float getCurrentHealth()
    {
        return current;
    }
}
