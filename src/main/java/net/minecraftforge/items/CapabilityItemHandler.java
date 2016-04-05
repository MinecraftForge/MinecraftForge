package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

public class CapabilityItemHandler
{
    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Capability.IStorage<IItemHandler>()
        {
            @Override
            public NBTBase writeNBT(Capability<IItemHandler> capability, IItemHandler instance, EnumFacing side)
            {
                NBTTagList nbtTagList = new NBTTagList();
                int size = instance.getSlots();
                for (int i = 0; i < size; i++)
                {
                    ItemStack stack = instance.getStackInSlot(i);
                    if (stack != null)
                    {
                        NBTTagCompound itemTag = new NBTTagCompound();
                        itemTag.setInteger("Slot", i);
                        stack.writeToNBT(itemTag);
                        nbtTagList.appendTag(itemTag);
                    }
                }
                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, EnumFacing side, NBTBase base)
            {
                if (!(instance instanceof IItemHandlerModifiable))
                    throw new RuntimeException("IItemHandler instance does not implement IItemHandlerModifiable");
                IItemHandlerModifiable itemHandlerModifiable = (IItemHandlerModifiable) instance;
                NBTTagList tagList = (NBTTagList) base;
                for (int i = 0; i < tagList.tagCount(); i++)
                {
                    NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                    int j = itemTags.getInteger("Slot");

                    if (j >= 0 && j < instance.getSlots())
                    {
                        itemHandlerModifiable.setStackInSlot(j, ItemStack.loadItemStackFromNBT(itemTags));
                    }
                }
            }
        }, new Callable<ItemStackHandler>()
        {
            @Override
            public ItemStackHandler call() throws Exception
            {
                return new ItemStackHandler();
            }
        });
    }

}
