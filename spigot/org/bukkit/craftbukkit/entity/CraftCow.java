package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;

public class CraftCow extends CraftAnimals implements Cow {

    public CraftCow(CraftServer server, net.minecraft.entity.passive.EntityCow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityCow getHandle() {
        return (net.minecraft.entity.passive.EntityCow) entity;
    }

    @Override
    public String toString() {
        return "CraftCow";
    }

    public EntityType getType() {
        return EntityType.COW;
    }
}
