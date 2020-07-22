package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * PlayerFindAmmoEvent is fired when a shootable item tries to find ammo on the player.
 * This event is fired whenever a shootable item attempts to find ammo using {@link PlayerEntity#findAmmo(ItemStack)}.<br>
 * <br>
 * This event fires after the original vanilla order, aka: Hand -> PlayerInventory -> This.<br>
 * <br>
 * {@link #shootable} contains the {@link net.minecraft.item.ShootableItem} in it's ItemStack form.
 * {@link #ammoPredicate} contains the {@link ShootableItem#getAmmoPredicate()} instance.<br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.
 * This event should be cancelled if an ammo has been supplied by an inventory.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.eventbus.api.Event.HasResult}<br>
 * <br>
 * This event is fired from {@link PlayerEntity#findAmmo(ItemStack)}.
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 * <br>
 */
@Cancelable
public class PlayerFindAmmoEvent extends PlayerEvent
{
    @Nonnull
    private final ItemStack shootable;
    @Nonnull
    private final Predicate<ItemStack> ammoPredicate;
    @Nonnull
    private ItemStack ammo = ItemStack.EMPTY;

    public PlayerFindAmmoEvent(PlayerEntity player, @Nonnull ItemStack shootable, @Nonnull Predicate<ItemStack> ammoPredicate)
    {
        super(player);
        this.shootable = shootable;
        this.ammoPredicate = ammoPredicate;
    }

    @Nonnull
    public ItemStack getShootable() { return shootable; }

    @Nonnull
    public Predicate<ItemStack> getAmmoPredicate() { return ammoPredicate; }

    public void setAmmo(@Nonnull ItemStack ammo) {
        this.ammo = ammo;
    }

    @Nonnull
    public ItemStack getAmmo() {
        return ammo;
    }
}