package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class AmmoHolderHandler implements IAmmoHolder
{
    public ItemStack ammo = ItemStack.EMPTY;
    public Consumer<ItemStack> ammoConsumer = ammo -> {};

    @Override
    public ItemStack getAmmo() { return this.ammo; }

    @Override
    public Consumer<ItemStack> getAmmoConsumer() { return this.ammoConsumer; }

    @Override
    public void setAmmoConsumerPair(ItemStack ammo, Consumer<ItemStack> ammoConsumer) {
        this.ammo = ammo;
        this.ammoConsumer = ammoConsumer;
    }

    @Override
    public void consumeAmmo() { this.ammoConsumer.accept(this.ammo); }
}
