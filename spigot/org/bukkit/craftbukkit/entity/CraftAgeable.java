package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ageable;

public class CraftAgeable extends CraftCreature implements Ageable {
    public CraftAgeable(CraftServer server, net.minecraft.entity.EntityAgeable entity) {
        super(server, entity);
    }

    public int getAge() {
        return getHandle().getGrowingAge();
    }

    public void setAge(int age) {
        getHandle().setGrowingAge(age);
    }

    public void setAgeLock(boolean lock) {
        getHandle().ageLocked = lock;
    }

    public boolean getAgeLock() {
        return getHandle().ageLocked;
    }

    public void setBaby() {
        if (isAdult()) {
            setAge(-24000);
        }
    }

    public void setAdult() {
        if (!isAdult()) {
            setAge(0);
        }
    }

    public boolean isAdult() {
        return getAge() >= 0;
    }


    public boolean canBreed() {
        return getAge() == 0;
    }

    public void setBreed(boolean breed) {
        if (breed) {
            setAge(0);
        } else if (isAdult()) {
            setAge(6000);
        }
    }

    @Override
    public net.minecraft.entity.EntityAgeable getHandle() {
        return (net.minecraft.entity.EntityAgeable) entity;
    }

    @Override
    public String toString() {
        return "CraftAgeable";
    }
}
