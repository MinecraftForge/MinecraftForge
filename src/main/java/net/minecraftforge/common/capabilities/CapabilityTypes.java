package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Holder class for all of Forge's default capability types.
 */
public class CapabilityTypes
{

	/**
	 * The Energy Capability Type, defining the API that is Forge Energy.
	 * @see {@link IEnergyStorage} for the underlying interface.
	 * @see {@link EnergyStorage} for a default implementation
	 */
    public static final CapabilityType<IEnergyStorage> ENERGY = CapabilityManager.get(new ResourceLocation("forge", "energy"));

    /**
     * The Item Capability Type, defining a common API for game objects to transfer {@link ItemStack}s.
     * Many vanilla game objects will expose this capability.
     * @see {@link IItemHandler} for the underlying interface.
     * @see {@link IItemHandlerModifiable} for handlers that allow explicitly setting items.
     * @see {@link ItemStackHandler} for a default implementation.
     */
    public static final CapabilityType<IItemHandler> ITEMS = CapabilityManager.get(new ResourceLocation("forge", "items"));
    
    /**
     * The Fluid Capability Type, defining the API by which fluids are exchanged between game objects.
     * @see {@link IFluidHandler} for the underlying interface.
     * @see {@link FluidTank} for a default implementation.
     */
    public static final CapabilityType<IFluidHandler> FLUIDS = CapabilityManager.get(new ResourceLocation("forge", "fluids"));
    
    /**
     * The Fluid Item Capability Type, representing fluids that are stored within an item, such as a bucket.<br>
     * This is an extension of {@link IFluidHandler} with support for container items.
     * @see {@link IFluidHandlerItem} for the underlying interface.
     * @see {@link FluidHandlerItemStack} for a default implementation.
     * @see {@link FluidHandlerItemStackSimple} for a default implementation that only allows complete filling or emptying.
     */
    public static final CapabilityType<IFluidHandlerItem> FLUID_ITEMS = CapabilityManager.get(new ResourceLocation("forge", "fluid_items"));

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(ENERGY);
        event.register(ITEMS);
        event.register(FLUIDS);
        event.register(FLUID_ITEMS);
    }
}