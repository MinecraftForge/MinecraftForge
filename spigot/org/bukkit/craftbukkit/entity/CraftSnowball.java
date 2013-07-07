package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;

public class CraftSnowball extends CraftProjectile implements Snowball {
    public CraftSnowball(CraftServer server, net.minecraft.entity.projectile.EntitySnowball entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.projectile.EntitySnowball getHandle() {
        return (net.minecraft.entity.projectile.EntitySnowball) entity;
    }

    @Override
    public String toString() {
        return "CraftSnowball";
    }

    public EntityType getType() {
        return EntityType.SNOWBALL;
    }
}
