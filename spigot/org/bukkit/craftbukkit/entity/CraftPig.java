package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

public class CraftPig extends CraftAnimals implements Pig {
    public CraftPig(CraftServer server, net.minecraft.entity.passive.EntityPig entity) {
        super(server, entity);
    }

    public boolean hasSaddle() {
        return getHandle().getSaddled();
    }

    public void setSaddle(boolean saddled) {
        getHandle().setSaddled(saddled);
    }

    public net.minecraft.entity.passive.EntityPig getHandle() {
        return (net.minecraft.entity.passive.EntityPig) entity;
    }

    @Override
    public String toString() {
        return "CraftPig";
    }

    public EntityType getType() {
        return EntityType.PIG;
    }
}
