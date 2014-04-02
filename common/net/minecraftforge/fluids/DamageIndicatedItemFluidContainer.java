package net.minecraftforge.fluids;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.item.DamageIndicatedItemComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This extension on {@link ItemFluidContainer} will show a damage indicator depending on how full
 * the container is. This can be used to hold certain amounts of Fluids in an Item.
 * When this item is available in a CreativeTab, it will add itself as a full and an empty container.
 * 
 * This container ONLY allows the fluid from the given type.
 * 
 * @author rubensworks
 *
 */
public abstract class DamageIndicatedItemFluidContainer extends ItemFluidContainer {

    private DamageIndicatedItemComponent component;
    private Fluid fluid;
    
    /**
     * Create a new DamageIndicatedItemFluidContainer.
     * 
     * @param itemID
     *          The ID for this container.
     * @param capacity
     *          The capacity this container will have.
     * @param fluid
     *          The Fluid instance this container must hold.
     */
    public DamageIndicatedItemFluidContainer(int itemID, int capacity, Fluid fluid)
    {
        super(itemID, capacity);
        this.fluid = fluid;
        component = new DamageIndicatedItemComponent(this, this.capacity);
    }
    
    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        FluidStack fluidStack = super.drain(container, maxDrain, doDrain);
        int newAmount = getFluid(container) == null ? 0 : getFluid(container).amount;
        component.updateAmount(container, newAmount);
        return fluidStack;
    }
    
    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill)
    {
        int filled = super.fill(container, resource, doFill);
        component.updateAmount(container, getFluid(container).amount);
        return filled;
    }
    
    /**
     * Make sure the full and empty container is available is the CreativeTab.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List itemList)
    {
        // Add the 'full' container.
        ItemStack itemStackFull = new ItemStack(this, 1);
        fill(itemStackFull, new FluidStack(fluid, component.capacity), true);
        itemList.add(itemStackFull);
        
        // Add the 'empty' container.
        ItemStack itemStackEmpty = new ItemStack(this, 1);
        fill(itemStackEmpty, new FluidStack(fluid, 0), true);
        itemList.add(itemStackEmpty);
    }

}
