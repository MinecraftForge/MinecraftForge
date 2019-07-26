/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fluids;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item.Properties;

/**
 * A universal bucket that can hold any liquid
 */
public class UniversalBucket extends Item
{

    private final int capacity; // how much the bucket holds
    @Nonnull
    private final ItemStack empty; // empty item to return and recognize when filling
    private final boolean nbtSensitive;

    public UniversalBucket(Properties properties)
    {
        this(properties, Fluid.BUCKET_VOLUME, new ItemStack(Items.BUCKET), false);
    }

    /**
     * @param capacity        Capacity of the container
     * @param empty           Item used for filling with the bucket event and returned when emptied
     * @param nbtSensitive    Whether the empty item is NBT sensitive (usually true if empty and full are the same items)
     */
    public UniversalBucket(Properties properties, int capacity, @Nonnull ItemStack empty, boolean nbtSensitive)
    {
        super(properties);
        this.capacity = capacity;
        this.empty = empty;
        this.nbtSensitive = nbtSensitive;

        /* TODO move to builder construction
        this.setMaxStackSize(1);

        this.setCreativeTab(CreativeTabs.MISC);
*/
        DispenserBlock.registerDispenseBehavior(this, DispenseFluidContainer.getInstance());
    }

    @Override
    public boolean hasContainerItem(@Nonnull ItemStack stack)
    {
        return !getEmpty().isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack getContainerItem(@Nonnull ItemStack itemStack)
    {
        if (!getEmpty().isEmpty())
        {
            // Create a copy such that the game can't mess with it
            return getEmpty().copy();
        }
        return super.getContainerItem(itemStack);
    }

    @Override
    public void fillItemGroup(@Nullable ItemGroup tab, @Nonnull NonNullList<ItemStack> subItems)
    {
        if (!this.isInGroup(tab))
            return;/* TODO fluids
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
        {
            if (fluid != FluidRegistry.WATER && fluid != FluidRegistry.LAVA && !fluid.getName().equals("milk"))
            {
                // add all fluids that the bucket can be filled  with
                FluidStack fs = new FluidStack(fluid, getCapacity());
                ItemStack stack = new ItemStack(this);
                IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
                if (fluidHandler.fill(fs, true) == fs.amount)
                {
                    ItemStack filled = fluidHandler.getContainer();
                    subItems.add(filled);
                }
            }
        }*/
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName(@Nonnull ItemStack stack)
    {
        FluidStack fluidStack = getFluid(stack);
        if (fluidStack == null)
        {
            if(!getEmpty().isEmpty())
            {
                return getEmpty().getDisplayName();
            }
            return super.getDisplayName(stack);
        }

        String unloc = this.getTranslationKey();

        // TODO this is not reliable on the server
        if (LanguageMap.getInstance().exists(unloc + "." + fluidStack.getFluid().getName()))
        {
            return new TranslationTextComponent(unloc + "." + fluidStack.getFluid().getName());
        }

        return new TranslationTextComponent(unloc + ".name", fluidStack.getLocalizedName());
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        FluidStack fluidStack = getFluid(itemstack);
        // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
        if (fluidStack == null)
        {
            return new ActionResult<ItemStack>(ActionResultType.PASS, itemstack);
        }

        // clicked on a block?
        RayTraceResult rt = rayTrace(world, player, FluidMode.NONE);

        ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, itemstack, rt);
        if (ret != null) return ret;

        if(rt == null || rt.getType() != RayTraceResult.Type.BLOCK)
        {
            return new ActionResult<ItemStack>(ActionResultType.PASS, itemstack);
        }

        BlockRayTraceResult brt = (BlockRayTraceResult) rt;
        BlockPos clickPos = brt.getPos();
        // can we place liquid there?
        if (world.isBlockModifiable(player, clickPos))
        {
            // the block adjacent to the side we clicked on
            BlockPos targetPos = clickPos.offset(brt.getFace());

            // can the player place there?
            if (player.canPlayerEdit(targetPos, brt.getFace(), itemstack))
            {
                // try placing liquid
                FluidActionResult result = FluidUtil.tryPlaceFluid(player, world, hand, targetPos, itemstack, fluidStack);
                if (result.isSuccess() && !player.abilities.isCreativeMode)
                {
                    // success!
                    player.addStat(Stats.ITEM_USED.get(this));

                    itemstack.shrink(1);
                    ItemStack drained = result.getResult();
                    ItemStack emptyStack = !drained.isEmpty() ? drained.copy() : new ItemStack(this);

                    // check whether we replace the item or add the empty one to the inventory
                    if (itemstack.isEmpty())
                    {
                        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, emptyStack);
                    }
                    else
                    {
                        // add empty bucket to player inventory
                        ItemHandlerHelper.giveItemToPlayer(player, emptyStack);
                        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
                    }
                }
            }
        }

        // couldn't place liquid there2
        return new ActionResult<ItemStack>(ActionResultType.FAIL, itemstack);
    }

    @SubscribeEvent(priority = EventPriority.LOW) // low priority so other mods can handle their stuff first
    public void onFillBucket(FillBucketEvent event)
    {
        if (event.getResult() != net.minecraftforge.eventbus.api.Event.Result.DEFAULT)
        {
            // event was already handled
            return;
        }

        // not for us to handle
        ItemStack emptyBucket = event.getEmptyBucket();
        if (emptyBucket.isEmpty() ||
                !emptyBucket.isItemEqual(getEmpty()) ||
                (isNbtSensitive() && ItemStack.areItemStackTagsEqual(emptyBucket, getEmpty())))
        {
            return;
        }

        // needs to target a block
        RayTraceResult target = event.getTarget();
        if (target == null || target.getType() != RayTraceResult.Type.BLOCK)
        {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = ((BlockRayTraceResult) target).getPos();

        ItemStack singleBucket = emptyBucket.copy();
        singleBucket.setCount(1);

        FluidActionResult filledResult = FluidUtil.tryPickUpFluid(singleBucket, event.getEntityPlayer(), world, pos, ((BlockRayTraceResult) target).getFace());
        if (filledResult.isSuccess())
        {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
            event.setFilledBucket(filledResult.getResult());
        }
        else
        {
            // cancel event, otherwise the vanilla minecraft ItemBucket would
            // convert it into a water/lava bucket depending on the blocks material
            event.setCanceled(true);
        }
    }

    @Nullable
    public FluidStack getFluid(@Nonnull ItemStack container)
    {
        return FluidStack.loadFluidStackFromNBT(container.getTag());
    }

    public int getCapacity()
    {
        return capacity;
    }

    @Nonnull
    public ItemStack getEmpty()
    {
        return empty;
    }

    public boolean isNbtSensitive()
    {
        return nbtSensitive;
    }

    @Nullable
    @Override
    public String getCreatorModId(@Nonnull ItemStack itemStack)
    {
        FluidStack fluidStack = getFluid(itemStack);
        String modId = null; // TODO fluids FluidRegistry.getModId(fluidStack);
        return modId != null ? modId : super.getCreatorModId(itemStack);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, CompoundNBT nbt)
    {
        return new FluidBucketWrapper(stack);
    }
}
