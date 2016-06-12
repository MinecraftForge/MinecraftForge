package net.minecraftforge.fmp.capabilities;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fmp.multipart.PartSlot;

public interface ISlottedCapabilityProvider
{
    
    public boolean hasCapability(Capability<?> capability, PartSlot slot, EnumFacing facing);

    public <T> T getCapability(Capability<T> capability, PartSlot slot, EnumFacing facing);

}