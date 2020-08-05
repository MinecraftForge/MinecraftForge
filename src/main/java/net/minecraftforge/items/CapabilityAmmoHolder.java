package net.minecraftforge.items;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityAmmoHolder
{
    @CapabilityInject(IAmmoHolder.class)
    public static Capability<IAmmoHolder> AMMO_HOLDER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IAmmoHolder.class, new IStorage<IAmmoHolder>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IAmmoHolder> capability, IAmmoHolder instance, Direction side) { return new CompoundNBT(); }

            @Override
            public void readNBT(Capability<IAmmoHolder> capability, IAmmoHolder instance, Direction side, INBT nbt) {}
        }, AmmoHolderHandler::new);
    }
}
