package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.EntityType;

public class CraftAmbient extends CraftLivingEntity implements Ambient {
    public CraftAmbient(CraftServer server, net.minecraft.entity.passive.EntityAmbientCreature entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityAmbientCreature getHandle() {
        return (net.minecraft.entity.passive.EntityAmbientCreature) entity;
    }

    @Override
    public String toString() {
        return "CraftAmbient";
    }

    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
}
