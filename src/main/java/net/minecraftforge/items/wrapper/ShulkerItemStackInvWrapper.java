/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = "forge")
public class ShulkerItemStackInvWrapper implements IItemHandler, ICapabilityProvider
{
    private final ItemStack stack;
    private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);

    public ShulkerItemStackInvWrapper(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public int getSlots()
    {
        return 27;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot)
    {
        if (slot < getSlots()) {
            return getItemList().get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        NonNullList<ItemStack> itemStacks = getItemList();
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stackInSlot = itemStacks.get(slot);

        int m;
        if (!stackInSlot.isEmpty())
        {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
                return stack;

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            if (!this.isItemValid(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    itemStacks.set(slot, copy);
                    this.setItemList(itemStacks);
                }

                return ItemStack.EMPTY;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    itemStacks.set(slot, copy);
                    this.setItemList(itemStacks);
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
        }
        else
        {
            if (!this.isItemValid(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount())
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    itemStacks.set(slot, stack.split(m));
                    this.setItemList(itemStacks);
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
            else
            {
                if (!simulate)
                {
                    itemStacks.set(slot, stack);
                    this.setItemList(itemStacks);
                }
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        NonNullList<ItemStack> itemStacks = getItemList();
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = itemStacks.get(slot);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate)
        {
            if (stackInSlot.getCount() < amount)
            {
                return stackInSlot.copy();
            }
            else
            {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        }
        else
        {
            int m = Math.min(stackInSlot.getCount(), amount);

            ItemStack decrStackSize = ContainerHelper.removeItem(itemStacks, slot, m);
            this.setItemList(itemStacks);
            return decrStackSize;
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return stack.getItem().canFitInsideContainerItems();
    }

    public NonNullList<ItemStack> getItemList()
    {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
        CompoundTag rootTag = BlockItem.getBlockEntityData(this.stack);
        if (rootTag != null && rootTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(rootTag, itemStacks);
        }
        return itemStacks;
    }

    public void setItemList(NonNullList<ItemStack> itemStacks)
    {
        CompoundTag existing = BlockItem.getBlockEntityData(this.stack);
        BlockItem.setBlockEntityData(this.stack, BlockEntityType.SHULKER_BOX,
                ContainerHelper.saveAllItems(existing == null ? new CompoundTag() : existing, itemStacks));
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, this.holder);
    }

    private static final Set<Item> SHULKER_ITEMS = Set.of(Items.SHULKER_BOX,
            Items.BLACK_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX,
            Items.BROWN_SHULKER_BOX,
            Items.CYAN_SHULKER_BOX,
            Items.GRAY_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX,
            Items.LIGHT_BLUE_SHULKER_BOX,
            Items.LIGHT_GRAY_SHULKER_BOX,
            Items.LIME_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX,
            Items.ORANGE_SHULKER_BOX,
            Items.PINK_SHULKER_BOX,
            Items.PURPLE_SHULKER_BOX,
            Items.RED_SHULKER_BOX,
            Items.WHITE_SHULKER_BOX,
            Items.YELLOW_SHULKER_BOX);

    @SubscribeEvent
    public static void listenCapabilitiesAttachment(AttachCapabilitiesEvent<ItemStack> event) {
        if (SHULKER_ITEMS.contains(event.getObject().getItem())) {
            event.addCapability(new ResourceLocation("forge","shulker_box"), new ShulkerItemStackInvWrapper(event.getObject()));
        }
    }
}
