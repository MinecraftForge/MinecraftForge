package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class CraftProjectile extends AbstractProjectile implements Projectile { // MCPC - concrete
    public CraftProjectile(CraftServer server, net.minecraft.entity.Entity entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().getThrower() != null) {
            return (LivingEntity) getHandle().getThrower().getBukkitEntity();
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().thrower = (net.minecraft.entity.EntityLivingBase) ((CraftLivingEntity) shooter).entity;
            if (shooter instanceof CraftHumanEntity) {
                getHandle().throwerName = ((CraftHumanEntity) shooter).getName();
            }
        }
    }

    @Override
    public net.minecraft.entity.projectile.EntityThrowable getHandle() {
        return (net.minecraft.entity.projectile.EntityThrowable) entity;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }

    // MCPC start
    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
    // MCPC end
}
