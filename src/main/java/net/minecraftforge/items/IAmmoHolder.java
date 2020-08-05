package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface IAmmoHolder
{
    /**
     * Returns The currently set "Ammo ItemStack"
     * @return The currently set "Ammo ItemStack"
     */
    @Nonnull
    ItemStack getAmmo();

    /**
     * Returns The currently set "Ammo Consumer"
     * @return The currently set "Ammo Consumer"
     */
    @Nonnull
    Consumer<ItemStack> getAmmoConsumer();

    /**
     * This method sets the Ammo and AmmoConsumer pair to the Internal Storage
     * @param ammo The Ammo being set to the Internal Storage
     * @param ammoConsumer The Ammo Consumer being set to the Internal Storage
     */
    void setAmmoConsumerPair(ItemStack ammo, Consumer<ItemStack> ammoConsumer);

    /**
     * This function basically handles the consumption of Ammo.
     * It uses the set ItemStack Ammo and passes that into the set ItemStack Consumer.
     */
    void consumeAmmo();
}
