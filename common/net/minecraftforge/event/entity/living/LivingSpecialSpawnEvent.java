package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingSpecialSpawnEvent extends LivingSpawnEvent
{
    public LivingSpecialSpawnEvent(EntityLiving entity, World world, float x, float y, float z)
    {
        super(entity, world, x, y, z);
    }
}
