package net.minecraftforge.fluids;

import net.minecraft.nbt.NBTTagCompound;

/**
 * FluidColored is an implementation of Fluid to support dynamic fluid colors.
 * 
 * @author ItsMeElConquistador
 * 
 */
public class FluidColored extends Fluid {

    public FluidColored(String fluidName)
    {
        super(fluidName);
    }

    @Override
    public int getColor(FluidStack stack)
    {
        return stack.tag != null && stack.tag.hasKey("Color") ? stack.tag.getInteger("Color") : getColor();
    }

    public void setColor(FluidStack stack, int color)
    {
        if (stack.tag == null)
        {
            stack.tag = new NBTTagCompound();
        }
        stack.tag.setInteger("Color", color);
    }
}
