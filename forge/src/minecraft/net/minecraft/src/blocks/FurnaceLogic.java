package net.minecraft.src.blocks;

import net.minecraft.src.forge.ISidedInventory;
import java.util.Random;
import net.minecraft.src.*;

public class FurnaceLogic extends BlockLogicMachine
    implements ISidedInventory
{
    public short fuel;
    public short fuelGague;
    public short progress;
    public Random rand;

    public FurnaceLogic()
    {
        super(3);
        fuel = 0;
        fuelGague = 0;
        progress = 0;
        rand = new Random();
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        fuel = nbttagcompound.getShort("fuel");
        fuelGague = nbttagcompound.getShort("fuelGague");
        progress = nbttagcompound.getShort("progress");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("fuel", fuel);
        nbttagcompound.setShort("fuelGague", fuelGague);
        nbttagcompound.setShort("progress", progress);
    }

    public int gaugeProgressScaled(int i)
    {
        return (progress * i) / 200;
    }

    public int gaugeFuelScaled(int i)
    {
        if (fuelGague == 0)
        {
            fuelGague = fuel;
            if (fuelGague == 0)
            {
                fuelGague = 200;
            }
        }
        return (fuel * i) / fuelGague;
    }

    public void updateEntity()
    {
        super.updateEntity();
        boolean flag = isBurning();
        boolean flag1 = false;
        if (fuel <= 0 && canOperate())
        {
            fuel = fuelGague = getFuelValueFor(inventory[1]);
            if (fuel > 0)
            {
                if (inventory[1].getItem().hasContainerItem())
                {
                    inventory[1] = new ItemStack(inventory[1].getItem().getContainerItem());
                }
                else
                {
                    inventory[1].stackSize--;
                }
                if (inventory[1].stackSize <= 0)
                {
                    inventory[1] = null;
                }
                flag1 = true;
            }
        }
        if (isBurning() && canOperate())
        {
            progress++;
            if (progress >= 200)
            {
                progress = 0;
                operate();
                flag1 = true;
            }
        }
        else
        {
            progress = 0;
        }
        if (fuel > 0)
        {
            fuel--;
        }
        if (flag != isBurning())
        {
            setActive(isBurning());
            flag1 = true;
        }
        if (flag1)
        {
            onInventoryChanged();
        }
    }

    public void operate()
    {
        if (!canOperate())
        {
            return;
        }
        ItemStack itemstack = getResultFor(inventory[0]);
        if (inventory[2] == null)
        {
            inventory[2] = itemstack.copy();
        }
        else
        {
            inventory[2].stackSize += itemstack.stackSize;
        }
        if (inventory[0].getItem().hasContainerItem())
        {
            inventory[0] = new ItemStack(inventory[0].getItem().getContainerItem());
        }
        else
        {
            inventory[0].stackSize--;
        }
        if (inventory[0].stackSize <= 0)
        {
            inventory[0] = null;
        }
    }

    public boolean isBurning()
    {
        return fuel > 0;
    }

    public boolean canOperate()
    {
        if (inventory[0] == null)
        {
            return false;
        }
        ItemStack itemstack = getResultFor(inventory[0]);
        if (itemstack == null)
        {
            return false;
        }
        if (inventory[2] == null)
        {
            return true;
        }
        if (!inventory[2].isItemEqual(itemstack))
        {
            return false;
        }
        else
        {
            return inventory[2].stackSize + itemstack.stackSize <= inventory[2].getMaxStackSize();
        }
    }

    public static short getFuelValueFor(ItemStack itemstack)
    {
        if (itemstack == null)
        {
            return 0;
        }
        int i = itemstack.getItem().shiftedIndex;
        if (i < 256 && Block.blocksList[i].blockMaterial == Material.wood)
        {
            return 300;
        }
        if (i == Item.stick.shiftedIndex)
        {
            return 100;
        }
        if (i == Item.coal.shiftedIndex)
        {
            return 1600;
        }
        if (i == Item.bucketLava.shiftedIndex)
        {
            return 2000;
        }
        if (i == Block.sapling.blockID)
        {
            return 100;
        }
        else
        {
            return (short)ModLoader.addAllFuel(i, itemstack.getItemDamage());
        }
    }

    public ItemStack getResultFor(ItemStack itemstack)
    {
        return FurnaceRecipes.smelting().getSmeltingResult(itemstack);
    }

    public String getInvName()
    {
        return "Steam Furnace";
    }

    public Container getGuiContainer(InventoryPlayer inventoryplayer)
    {
        return new FurnaceContainer(inventoryplayer, this);
    }

    public int getStartInventorySide(int i)
    {
        switch (i)
        {
            case 0:
                return 1;

            case 1:
                return 0;
        }
        return 2;
    }

    public int getSizeInventorySide(int i)
    {
        return 1;
    }

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		// TODO Auto-generated method stub
		return null;
	}
}
