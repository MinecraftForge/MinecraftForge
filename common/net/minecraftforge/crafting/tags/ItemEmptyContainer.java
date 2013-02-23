package net.minecraftforge.crafting.tags;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class ItemEmptyContainer {
    protected boolean exact;
    protected ItemStack[] excludes;

    public ItemEmptyContainer()
    {
        this(false);
    }

    public ItemEmptyContainer(boolean exact)
    {
        this(exact, null);
    }

    public ItemEmptyContainer(ItemStack[] excludes)
    {
        this(false, excludes);
    }

    public ItemEmptyContainer(boolean exact, ItemStack[] excludes)
    {
        this.exact = exact;
        if (excludes != null)
        {
            this.excludes = new ItemStack[excludes.length];
            for (int i = 0; i < excludes.length; ++i)
            {
                this.excludes[i] = excludes[i].copy();
            }
        }
        else
        {
            this.excludes = null;
        }
    }

    public ItemStack getFilledContainer(LiquidStack liquid, ItemStack container)
    {
        if(container == null) return null;
        
        if(excludes != null)
        {
            for(ItemStack item : excludes)
            {
                if (item.getItemDamage() == -1 && item.itemID == container.itemID || item.isItemEqual(container))
                {
                    return null;
                }
            }
        }
        
        ItemStack filled = LiquidContainerRegistry.fillLiquidContainer(liquid, container);

        if (filled != null)
        {
            if (!exact)
            {
                return filled;
            }
            else
            {
                LiquidStack filledLiquid = LiquidContainerRegistry.getLiquidForFilledItem(filled);
                return filledLiquid.amount == liquid.amount ? filled : null;
            }
        }

        return null;
    }

    public ArrayList<ItemStack> getFilledContainers(LiquidStack liquid)
    {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>();

        if(liquid == null) return result;
        
        for (LiquidContainerData data : LiquidContainerRegistry.getRegisteredLiquidContainerData())
        {
            if (data.stillLiquid.isLiquidEqual(liquid)
                    && ((exact && data.stillLiquid.amount == liquid.amount) || (!exact && data.stillLiquid.amount <= liquid.amount)))
            {
                if (excludes != null)
                {
                    boolean include = true;;
                    for (ItemStack item : excludes)
                    {
                        if ((item.getItemDamage() == -1 && item.itemID == data.container.itemID) || item.isItemEqual(data.container))
                        {
                            include = false;
                        }
                    }
                    if (include) result.add(data.filled);
                }
                else
                {
                    result.add(data.filled);
                }
            }
        }

        return result;
    }

    public ArrayList<LiquidContainerData> getLiquidContainerData(LiquidStack liquid)
    {
        ArrayList<LiquidContainerData> result = new ArrayList<LiquidContainerData>();

        if(liquid == null) return result;
        
        for (LiquidContainerData data : LiquidContainerRegistry.getRegisteredLiquidContainerData())
        {
            if (data.stillLiquid.isLiquidEqual(liquid)
                    && ((exact && data.stillLiquid.amount == liquid.amount) || (!exact && data.stillLiquid.amount <= liquid.amount)))
            {
                if (excludes != null)
                {
                    boolean include = true;;
                    for (ItemStack item : excludes)
                    {
                        if ((item.getItemDamage() == -1 && item.itemID == data.container.itemID) || item.isItemEqual(data.container))
                        {
                            include = false;
                        }
                    }
                    if (include) result.add(data);
                }
                else
                {
                    result.add(data);
                }
            }
        }

        return result;
    }
}
