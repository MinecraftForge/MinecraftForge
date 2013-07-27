package net.minecraftforge.liquids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Reference implementation of ILiquidTank. Use this or implement your own.
 */
@Deprecated //See new net.minecraftforge.fluids
public class LiquidTank implements ILiquidTank {
    private LiquidStack liquid;
    private int capacity;
    private int tankPressure;
    private TileEntity tile;

    public LiquidTank(int capacity)
    {
        this(null, capacity);
    }

    public LiquidTank(int liquidId, int quantity, int capacity)
    {
        this(new LiquidStack(liquidId, quantity), capacity);
    }

    public LiquidTank(int liquidId, int quantity, int capacity, TileEntity tile)
    {
        this(liquidId, quantity, capacity);
        this.tile = tile;
    }

    public LiquidTank(LiquidStack liquid, int capacity)
    {
        this.liquid = liquid;
        this.capacity = capacity;
    }

    public LiquidTank(LiquidStack liquid, int capacity, TileEntity tile)
    {
        this(liquid, capacity);
        this.tile = tile;
    }

    @Override
    public LiquidStack getLiquid()
    {
        return this.liquid;
    }

    @Override
    public int getCapacity()
    {
        return this.capacity;
    }

    public void setLiquid(LiquidStack liquid)
    {
        this.liquid = liquid;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    @Override
    public int fill(LiquidStack resource, boolean doFill)
    {
        if (resource == null || resource.itemID <= 0) return 0;

        if (liquid == null || liquid.itemID <= 0)
        {
            if (resource.amount <= capacity)
            {
                if (doFill) this.liquid = resource.copy();
                return resource.amount;
            }
            else
            {
                if (doFill)
                {
                    this.liquid = resource.copy();
                    this.liquid.amount = capacity;
                    if (tile != null)
                        LiquidEvent.fireEvent(new LiquidEvent.LiquidFillingEvent(liquid, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, this));
                }
                return capacity;
            }
        }

        if (!liquid.isLiquidEqual(resource)) return 0;

        int space = capacity - liquid.amount;
        if (resource.amount <= space)
        {
            if (doFill) this.liquid.amount += resource.amount;
            return resource.amount;
        }
        else
        {

            if (doFill) this.liquid.amount = capacity;
            return space;
        }

    }

    @Override
    public LiquidStack drain(int maxDrain, boolean doDrain)
    {
        if (liquid == null || liquid.itemID <= 0) return null;
        if (liquid.amount <= 0) return null;

        int used = maxDrain;
        if (liquid.amount < used) used = liquid.amount;

        if (doDrain)
        {
            liquid.amount -= used;
        }

        LiquidStack drained = new LiquidStack(liquid.itemID, used, liquid.itemMeta);

        // Reset liquid if emptied
        if (liquid.amount <= 0) liquid = null;

        if (doDrain && tile != null)
            LiquidEvent.fireEvent(new LiquidEvent.LiquidDrainingEvent(drained, tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, this));

        return drained;
    }

    @Override
    public int getTankPressure()
    {
        return tankPressure;
    }

    public void setTankPressure(int pressure)
    {
        this.tankPressure = pressure;
    }


    public String getLiquidName()
    {
        return liquid!= null ? LiquidDictionary.findLiquidName(liquid) : null;
    }

    public boolean containsValidLiquid()
    {
        return LiquidDictionary.findLiquidName(liquid) != null;
    }


    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (containsValidLiquid())
        {
            liquid.writeToNBT(nbt);
        }
        else
        {
            nbt.setString("emptyTank", "");
        }
        return nbt;
    }

    public LiquidTank readFromNBT(NBTTagCompound nbt)
    {
        if (!nbt.hasKey("emptyTank"))
        {
            LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbt);
            if (liquid != null)
            {
                setLiquid(liquid);
            }
        }
        return this;
    }
}
