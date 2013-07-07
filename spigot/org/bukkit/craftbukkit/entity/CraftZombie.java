package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

public class CraftZombie extends CraftMonster implements Zombie {

    public CraftZombie(CraftServer server, net.minecraft.entity.monster.EntityZombie entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityZombie getHandle() {
        return (net.minecraft.entity.monster.EntityZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftZombie";
    }

    public EntityType getType() {
        return EntityType.ZOMBIE;
    }

    public boolean isBaby() {
        return getHandle().isChild();
    }

    public void setBaby(boolean flag) {
        getHandle().setChild(flag);
    }

    public boolean isVillager() {
        return getHandle().isVillager();
    }

    public void setVillager(boolean flag) {
        getHandle().setVillager(flag);
    }
}
