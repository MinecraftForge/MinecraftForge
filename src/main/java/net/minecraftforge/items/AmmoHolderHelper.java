package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Consumer;

public class AmmoHolderHelper {

    public static boolean hasStoredAmmo(ItemStack stack)
    {
        LazyOptional<IAmmoHolder> holder = stack.getCapability(CapabilityAmmoHolder.AMMO_HOLDER_CAPABILITY);
        if (holder.isPresent())
            return holder.map(iAmmoHolder -> iAmmoHolder.getAmmo().isEmpty()).orElse(false);
        return false;
    }

    public static ItemStack getStoredAmmo(ItemStack stack)
    {
        LazyOptional<IAmmoHolder> holder = stack.getCapability(CapabilityAmmoHolder.AMMO_HOLDER_CAPABILITY);
        if (holder.isPresent())
            return holder.map(IAmmoHolder::getAmmo).orElse(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    public static Consumer<ItemStack> getStoredConsumer(ItemStack stack)
    {
        LazyOptional<IAmmoHolder> holder = stack.getCapability(CapabilityAmmoHolder.AMMO_HOLDER_CAPABILITY);
        if (holder.isPresent())
            return holder.map(IAmmoHolder::getAmmoConsumer).orElse(ammo -> {});
        return ammo -> {};
    }
}
