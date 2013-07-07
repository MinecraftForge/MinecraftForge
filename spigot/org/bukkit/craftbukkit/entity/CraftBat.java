package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

public class CraftBat extends CraftAmbient implements Bat {
    public CraftBat(CraftServer server, net.minecraft.entity.passive.EntityBat entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityBat getHandle() {
        return (net.minecraft.entity.passive.EntityBat) entity;
    }

    @Override
    public String toString() {
        return "CraftBat";
    }

    public EntityType getType() {
        return EntityType.BAT;
    }
}
