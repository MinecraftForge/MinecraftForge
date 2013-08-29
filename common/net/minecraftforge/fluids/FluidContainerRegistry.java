
package net.minecraftforge.fluids;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

/**
 * Register simple items that contain fluids here. Useful for buckets, bottles, and things that have
 * ID/metadata mappings.
 * 
 * For more complex items, use {@link IFluidContainerItem} instead.
 * 
 * @author King Lemming
 * 
 */
public abstract class FluidContainerRegistry
{
    private static Map<List, FluidContainerData> containerFluidMap = new HashMap();
    private static Map<List, FluidContainerData> filledContainerMap = new HashMap();
    private static Set<List> emptyContainers = new HashSet();

    public static final int BUCKET_VOLUME = 1000;
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Item.bucketEmpty);
    public static final ItemStack EMPTY_BOTTLE = new ItemStack(Item.glassBottle);
    private static final ItemStack NULL_EMPTYCONTAINER = new ItemStack(Item.bucketEmpty);

    static
    {
        registerFluidContainer(FluidRegistry.WATER, new ItemStack(Item.bucketWater), EMPTY_BUCKET);
        registerFluidContainer(FluidRegistry.LAVA,  new ItemStack(Item.bucketLava),  EMPTY_BUCKET);
        registerFluidContainer(FluidRegistry.WATER, new ItemStack(Item.potion),      EMPTY_BOTTLE);
    }

    private FluidContainerRegistry(){}

    /**
     * Register a new fluid containing item.
     * 
     * @param stack
     *            FluidStack containing the type and amount of the fluid stored in the item.
     * @param filledContainer
     *            ItemStack representing the container when it is full.
     * @param emptyContainer
     *            ItemStack representing the container when it is empty.
     * @return True if container was successfully registered; false if it already is.
     */
    public static boolean registerFluidContainer(FluidStack stack, ItemStack filledContainer, ItemStack emptyContainer)
    {
        return registerFluidContainer(new FluidContainerData(stack, filledContainer, emptyContainer));
    }

    /**
     * Register a new fluid containing item. The item is assumed to hold 1000 mB of fluid. Also
     * registers the Fluid if possible.
     * 
     * @param fluid
     *            Fluid type that is stored in the item.
     * @param filledContainer
     *            ItemStack representing the container when it is full.
     * @param emptyContainer
     *            ItemStack representing the container when it is empty.
     * @return True if container was successfully registered; false if it already is.
     */
    public static boolean registerFluidContainer(Fluid fluid, ItemStack filledContainer, ItemStack emptyContainer)
    {
        if (!FluidRegistry.isFluidRegistered(fluid))
        {
            FluidRegistry.registerFluid(fluid);
        }
        return registerFluidContainer(new FluidStack(fluid, BUCKET_VOLUME), filledContainer, emptyContainer);
    }

    /**
     * Register a new fluid containing item that does not have an empty container.
     * 
     * @param stack
     *            FluidStack containing the type and amount of the fluid stored in the item.
     * @param filledContainer
     *            ItemStack representing the container when it is full.
     * @return True if container was successfully registered; false if it already is.
     */
    public static boolean registerFluidContainer(FluidStack stack, ItemStack filledContainer)
    {
        return registerFluidContainer(new FluidContainerData(stack, filledContainer, null, true));
    }

    /**
     * Register a new fluid containing item that does not have an empty container. The item is
     * assumed to hold 1000 mB of fluid. Also registers the Fluid if possible.
     * 
     * @param fluid
     *            Fluid type that is stored in the item.
     * @param filledContainer
     *            ItemStack representing the container when it is full.
     * @return True if container was successfully registered; false if it already is.
     */
    public static boolean registerFluidContainer(Fluid fluid, ItemStack filledContainer)
    {
        if (!FluidRegistry.isFluidRegistered(fluid))
        {
            FluidRegistry.registerFluid(fluid);
        }
        return registerFluidContainer(new FluidStack(fluid, BUCKET_VOLUME), filledContainer);
    }

    /**
     * Register a new fluid containing item.
     * 
     * @param data
     *            See {@link FluidContainerData}.
     * @return True if container was successfully registered; false if it already is.
     */
    public static boolean registerFluidContainer(FluidContainerData data)
    {
        if (isFilledContainer(data.filledContainer))
        {
            return false;
        }
        containerFluidMap.put(Arrays.asList(data.filledContainer.itemID, data.filledContainer.getItemDamage()), data);

        if (data.emptyContainer != null && data.emptyContainer != NULL_EMPTYCONTAINER)
        {
            filledContainerMap.put(Arrays.asList(data.emptyContainer.itemID, data.emptyContainer.getItemDamage(), data.fluid.fluidID), data);
            emptyContainers.add(Arrays.asList(data.emptyContainer.itemID, data.emptyContainer.getItemDamage()));
        }

        MinecraftForge.EVENT_BUS.post(new FluidContainerRegisterEvent(data));
        return true;
    }

    /**
     * Determines the fluid type and amount inside a container.
     * 
     * @param container
     *            The fluid container.
     * @return FluidStack representing stored fluid.
     */
    public static FluidStack getFluidForFilledItem(ItemStack container)
    {
        if (container == null)
        {
            return null;
        }

        FluidContainerData data = containerFluidMap.get(Arrays.asList(container.itemID, container.getItemDamage()));
        return data == null ? null : data.fluid.copy();
    }

    /**
     * Attempts to fill an empty container with a fluid.
     * 
     * NOTE: Returns null on fail, NOT the empty container.
     * 
     * @param fluid
     *            FluidStack containing the type and amount of fluid to fill.
     * @param container
     *            ItemStack representing the empty container.
     * @return Filled container if successful, otherwise null.
     */
    public static ItemStack fillFluidContainer(FluidStack fluid, ItemStack container)
    {
        if (container == null || fluid == null)
        {
            return null;
        }

        FluidContainerData data = filledContainerMap.get(Arrays.asList(container.itemID, container.getItemDamage(), fluid.fluidID));
        if (data != null && fluid.amount >= data.fluid.amount)
        {
            return data.filledContainer.copy();
        }
        return null;
    }

    /**
     * Determines if a container holds a specific fluid.
     */
    public static boolean containsFluid(ItemStack container, FluidStack fluid)
    {
        if (container == null || fluid == null)
        {
            return false;
        }

        FluidContainerData data = filledContainerMap.get(Arrays.asList(container.itemID, container.getItemDamage(), fluid.fluidID));
        return data == null ? false : data.fluid.isFluidEqual(fluid);
    }

    public static boolean isBucket(ItemStack container)
    {
        if (container == null)
        {
            return false;
        }

        if (container.isItemEqual(EMPTY_BUCKET))
        {
            return true;
        }

        FluidContainerData data = containerFluidMap.get(Arrays.asList(container.itemID, container.getItemDamage()));
        return data != null && data.emptyContainer.isItemEqual(EMPTY_BUCKET);
    }

    public static boolean isContainer(ItemStack container)
    {
        return isEmptyContainer(container) || isFilledContainer(container);
    }

    public static boolean isEmptyContainer(ItemStack container)
    {
        return container != null && emptyContainers.contains(Arrays.asList(container.itemID, container.getItemDamage()));
    }

    public static boolean isFilledContainer(ItemStack container)
    {
        return container != null && getFluidForFilledItem(container) != null;
    }

    public static FluidContainerData[] getRegisteredFluidContainerData()
    {
        return containerFluidMap.values().toArray(new FluidContainerData[containerFluidMap.size()]);
    }

    /**
     * Wrapper class for the registry entries. Ensures that none of the attempted registrations
     * contain null references unless permitted.
     */
    public static class FluidContainerData
    {
        public final FluidStack fluid;
        public final ItemStack filledContainer;
        public final ItemStack emptyContainer;

        
        public FluidContainerData(FluidStack stack, ItemStack filledContainer, ItemStack emptyContainer)
        {
            this(stack, filledContainer, emptyContainer, false);
        }

        public FluidContainerData(FluidStack stack, ItemStack filledContainer, ItemStack emptyContainer, boolean nullEmpty)
        {
            this.fluid = stack;
            this.filledContainer = filledContainer;
            this.emptyContainer = emptyContainer == null ? NULL_EMPTYCONTAINER : emptyContainer;

            if (stack == null || filledContainer == null || emptyContainer == null && !nullEmpty)
            {
                throw new RuntimeException("Invalid FluidContainerData - a parameter was null.");
            }
        }

        public FluidContainerData copy()
        {
            return new FluidContainerData(fluid, filledContainer, emptyContainer, true);
        }
    }

    public static class FluidContainerRegisterEvent extends Event
    {
        public final FluidContainerData data;

        public FluidContainerRegisterEvent(FluidContainerData data)
        {
            this.data = data.copy();
        }
    }

}
