package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * LivingFindAmmoEvent is fired when a shootable item tries to find ammo on the player.
 * This event is fired whenever a shootable item attempts to find ammo using {@link PlayerEntity#findAmmo(ItemStack)}.<br>
 * <br>
 * This event fires after the original vanilla order, aka: Hand -> PlayerInventory -> This event.<br>
 * <br>
 * {@link #shootable} contains the {@link net.minecraft.item.ShootableItem} in it's ItemStack form.
 * {@link #ammoPredicate} contains the {@link ShootableItem#getAmmoPredicate()} instance.<br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@linkplain net.minecraftforge.eventbus.api.Event.HasResult}<br>
 * <br>
 * This event is fired from {@link LivingEntity#findAmmo(ItemStack)}.<br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 * <br>
 */
public class LivingFindAmmoEvent extends LivingEvent
{
    @Nonnull
    private final ItemStack shootable;
    @Nonnull
    private final Predicate<ItemStack> ammoPredicate;
    @Nonnull
    private ItemStack ammo = ItemStack.EMPTY;
    @Nonnull
    private Consumer<ItemStack> consumer;

    public LivingFindAmmoEvent(LivingEntity shooter, @Nonnull ItemStack shootable, @Nonnull Predicate<ItemStack> ammoPredicate)
    {
        super(shooter);
        this.shootable = shootable;
        this.ammoPredicate = ammoPredicate;
        consumer = stack -> {};
    }

    @Nonnull
    public ItemStack getShootable() { return shootable; }

    @Nonnull
    public Predicate<ItemStack> getAmmoPredicate() { return ammoPredicate; }

    public void setAmmo(@Nonnull ItemStack ammo, @Nonnull Consumer<ItemStack> consumer)
    {
        this.ammo = ammo;
        this.consumer = consumer;
    }

    @Nonnull
    public ItemStack getAmmo() { return ammo; }

    @Nonnull
    public Consumer<ItemStack> getConsumer() { return consumer; }
}
