package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CraftArrow extends AbstractProjectile implements Arrow {

    public CraftArrow(CraftServer server, net.minecraft.entity.projectile.EntityArrow entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().shootingEntity != null) {
            return (LivingEntity) getHandle().shootingEntity.getBukkitEntity();
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().shootingEntity = ((CraftLivingEntity) shooter).getHandle();
        }
    }

    @Override
    public net.minecraft.entity.projectile.EntityArrow getHandle() {
        return (net.minecraft.entity.projectile.EntityArrow) entity;
    }

    @Override
    public String toString() {
        return "CraftArrow";
    }

    public EntityType getType() {
        return EntityType.ARROW;
    }

    // Spigot start
    private final Spigot spigot = new Spigot()
    {
        @Override
        public double getDamage()
        {
            return getHandle().getDamage();
        }

        @Override
        public void setDamage(double damage)
        {
            getHandle().setDamage( damage );
        }
    };

    public Spigot spigot()
    {
        return spigot;
    }
    // Spigot end
}
