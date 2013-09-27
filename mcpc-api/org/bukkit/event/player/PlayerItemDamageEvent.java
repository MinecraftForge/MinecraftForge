package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerItemDamageEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ItemStack item;
    private int damage;
    private boolean cancelled = false;

    public PlayerItemDamageEvent(Player player, ItemStack what, int damage) {
        super(player);
        this.item = what;
        this.damage = damage;
    }

    public ItemStack getItem() {
        return item;
    }

    /**
     * Gets the amount of durability damage this item will be taking.
     *
     * @return durability change
     */
    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
