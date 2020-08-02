package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * LivingFindAmmoEvent is fired when an attempt is made to find ammo.
 * This event is fired whenever an attempt is made to find ammo using {@link net.minecraftforge.common.ForgeHooks#findAmmo(ItemStack, LivingEntity)}.<br>
 * <br>
 * This event fires as the first point in the findAmmo method logic after the initial check.
 * If the event returns with a found ammo, the findAmmo method will return the ammo/consumer pair from the event as the result.
 * If the event doesn't provide a result, then it defaults to vanilla behaviour.
 * <br>
 * {@link #shootable} contains the {@link net.minecraft.item.ShootableItem} in it's ItemStack form.
 * {@link #ammoPredicate} contains the {@link ShootableItem#getAmmoPredicate()} instance.<br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@linkplain net.minecraftforge.eventbus.api.Event.HasResult}<br>
 * <br>
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
