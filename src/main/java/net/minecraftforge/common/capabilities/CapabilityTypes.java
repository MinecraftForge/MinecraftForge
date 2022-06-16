package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;

public class CapabilityTypes {

    public static final CapabilityType<IEnergyStorage> ENERGY = CapabilityManager.get(new ResourceLocation("forge", "energy"));
    public static final CapabilityType<IItemHandler> ITEMS = CapabilityManager.get(new ResourceLocation("forge", "items"));
    public static final CapabilityType<IFluidHandler> FLUIDS = CapabilityManager.get(new ResourceLocation("forge", "fluids"));
    public static final CapabilityType<IFluidHandlerItem> FLUID_ITEMS = CapabilityManager.get(new ResourceLocation("forge", "fluid_items"));

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(ENERGY);
        event.register(ITEMS);
        event.register(FLUIDS);
        event.register(FLUID_ITEMS);
    }
}