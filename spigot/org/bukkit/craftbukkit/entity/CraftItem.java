package org.bukkit.craftbukkit.entity;


import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.CraftServer;

public class CraftItem extends CraftEntity implements Item {
    private final net.minecraft.entity.item.EntityItem item;

    public CraftItem(CraftServer server, net.minecraft.entity.Entity entity, net.minecraft.entity.item.EntityItem item) {
        super(server, entity);
        this.item = item;
    }

    public CraftItem(CraftServer server, net.minecraft.entity.item.EntityItem entity) {
        this(server, entity, entity);
    }

    public ItemStack getItemStack() {
        return CraftItemStack.asCraftMirror(item.getEntityItem());
    }

    public void setItemStack(ItemStack stack) {
        item.setEntityItemStack(CraftItemStack.asNMSCopy(stack));
    }

    public int getPickupDelay() {
        return item.delayBeforeCanPickup;
    }

    public void setPickupDelay(int delay) {
        item.delayBeforeCanPickup = delay;
    }

    @Override
    public String toString() {
        return "CraftItem";
    }

    public EntityType getType() {
        return EntityType.DROPPED_ITEM;
    }
}
