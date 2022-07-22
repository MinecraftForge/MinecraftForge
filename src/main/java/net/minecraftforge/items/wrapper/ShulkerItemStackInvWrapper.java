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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

@ApiStatus.Internal
public class ShulkerItemStackInvWrapper implements IItemHandlerModifiable, ICapabilityProvider
{
    private final ItemStack stack;
    private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);

    private CompoundTag cachedTag;
    private NonNullList<ItemStack> itemStacksCache;

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
        validateSlotIndex(slot);
        return getItemList().get(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        NonNullList<ItemStack> itemStacks = getItemList();

        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = itemStacks.get(slot);

        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                itemStacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            setItemList(itemStacks);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        NonNullList<ItemStack> itemStacks = getItemList();
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = itemStacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                itemStacks.set(slot, ItemStack.EMPTY);
                setItemList(itemStacks);
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if (!simulate)
            {
                itemStacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                setItemList(itemStacks);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= getSlots())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
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

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack)
    {
        validateSlotIndex(slot);
        if (!isItemValid(slot, stack)) throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
        NonNullList<ItemStack> itemStacks = getItemList();
        itemStacks.set(slot, stack);
        setItemList(itemStacks);
    }

    protected NonNullList<ItemStack> getItemList()
    {
        CompoundTag rootTag = BlockItem.getBlockEntityData(this.stack);
        if (cachedTag == null || !cachedTag.equals(rootTag))
            itemStacksCache = refreshItemList(rootTag);
        return itemStacksCache;
    }

    protected NonNullList<ItemStack> refreshItemList(CompoundTag rootTag)
    {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
        if (rootTag != null && rootTag.contains("Items", CompoundTag.TAG_LIST))
        {
            ContainerHelper.loadAllItems(rootTag, itemStacks);
        }
        cachedTag = rootTag;
        return itemStacks;
    }

    protected void setItemList(NonNullList<ItemStack> itemStacks)
    {
        CompoundTag existing = BlockItem.getBlockEntityData(this.stack);
        CompoundTag rootTag = ContainerHelper.saveAllItems(existing == null ? new CompoundTag() : existing, itemStacks);
        BlockItem.setBlockEntityData(this.stack, BlockEntityType.SHULKER_BOX, rootTag);
        cachedTag = rootTag;
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, this.holder);
    }

    @ApiStatus.Internal
    public static class AttachmentHandler
    {
        private static final Set<ResourceLocation> SHULKER_ITEMS = Set.of(
                new ResourceLocation("shulker_box"),
                new ResourceLocation("black_shulker_box"),
                new ResourceLocation("blue_shulker_box"),
                new ResourceLocation("brown_shulker_box"),
                new ResourceLocation("cyan_shulker_box"),
                new ResourceLocation("gray_shulker_box"),
                new ResourceLocation("green_shulker_box"),
                new ResourceLocation("light_blue_shulker_box"),
                new ResourceLocation("light_gray_shulker_box"),
                new ResourceLocation("lime_shulker_box"),
                new ResourceLocation("magenta_shulker_box"),
                new ResourceLocation("orange_shulker_box"),
                new ResourceLocation("pink_shulker_box"),
                new ResourceLocation("purple_shulker_box"),
                new ResourceLocation("red_shulker_box"),
                new ResourceLocation("white_shulker_box"),
                new ResourceLocation("yellow_shulker_box"));

        @ApiStatus.Internal
        @Nullable
        public static ICapabilityProvider listenCapabilitiesAttachment(ItemStack itemStack)
        {
            if (SHULKER_ITEMS.contains(ForgeRegistries.ITEMS.getKey(itemStack.getItem())))
            {
                return new ShulkerItemStackInvWrapper(itemStack);
            }
            return null;
        }
    }
}
