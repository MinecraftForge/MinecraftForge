package net.minecraftforge.fluids;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.item.DamageIndicatedItemComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This ItemFluidContainer will show a damage indicator depending on how full the container is.
 * When this item is available in the CreativeTab, it will add itself as a full and an empty container.
 * This container ONLY allows fluids from the given type.
 * @author rubensworks
 *
 */
public abstract class DamageIndicatedItemFluidContainer extends ItemFluidContainer{

    private DamageIndicatedItemComponent component;
    private Fluid fluid;
    
    /**
     * Create a new DamageIndicatedItemFluidContainer.
     * @param itemID The ID for this container.
     * @param capacity The capacity this container will have.
     * @param fluid The Fluid instance this container must hold.
     */
    public DamageIndicatedItemFluidContainer(int itemID, int capacity, Fluid fluid)
    {
        super(itemID, capacity);
        this.fluid = fluid;
        init();
    }
    
    private void init()
    {
        component = new DamageIndicatedItemComponent(this, this.capacity);
    }
    
    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        FluidStack fluidStack = super.drain(container, maxDrain, doDrain);
        component.updateAmount(container, fluidStack.amount);
        return fluidStack;
    }
    
    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill)
    {
        int filled = super.fill(container, resource, doFill);
        component.updateAmount(container, this.getFluid(container).amount);
        return filled;
    }
    
    /**
     * Make sure the full and empty container is available is the CreativeTab
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int id, CreativeTabs tab, List itemList)
    {
        ItemStack itemStackFull = new ItemStack(this, 1);
        this.fill(itemStackFull, new FluidStack(fluid, component.capacity), true);
        itemList.add(itemStackFull);
        
        ItemStack itemStackEmpty = new ItemStack(this, 1);
        this.fill(itemStackEmpty, new FluidStack(fluid, 0), true);
        itemList.add(itemStackEmpty);
    }

}
