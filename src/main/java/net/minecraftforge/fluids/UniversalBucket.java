package net.minecraftforge.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * A universal bucket that can hold any liquid
 */
public class UniversalBucket extends Item implements IFluidContainerItem
{

    public final int capacity; // how much the bucket holds
    public final ItemStack empty; // empty item to return and recognize when filling
    public final boolean nbtSensitive;

    public UniversalBucket()
    {
        this(FluidContainerRegistry.BUCKET_VOLUME, FluidContainerRegistry.EMPTY_BUCKET, false);
    }

    /**
     * @param capacity        Capacity of the container
     * @param empty           Item used for filling with the bucket event and returned when emptied
     * @param nbtSensitive    Whether the empty item is NBT sensitive (usually true if empty and full are the same items)
     */
    public UniversalBucket(int capacity, ItemStack empty, boolean nbtSensitive)
    {
        this.capacity = capacity;
        this.empty = empty;
        this.nbtSensitive = nbtSensitive;

        this.setMaxStackSize(1);

        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
        {
            // add all fluids that the bucket can be filled  with
            FluidStack fs = new FluidStack(fluid, capacity);
            ItemStack stack = new ItemStack(this);
            if (fill(stack, fs, true) == fs.amount)
            {
                subItems.add(stack);
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        FluidStack fluidStack = getFluid(stack);
        if (fluidStack == null)
        {
            if(empty != null)
            {
                return empty.getDisplayName();
            }
            return super.getItemStackDisplayName(stack);
        }

        String unloc = this.getUnlocalizedNameInefficiently(stack);

        if (StatCollector.canTranslate(unloc + "." + fluidStack.getFluid().getName()))
        {
            return StatCollector.translateToLocal(unloc + "." + fluidStack.getFluid().getName());
        }

        return StatCollector.translateToLocalFormatted(unloc + ".name", fluidStack.getLocalizedName());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        FluidStack fluidStack = getFluid(itemstack);
        // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
        if (fluidStack == null)
        {
            return itemstack;
        }

        // clicked on a block?
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            BlockPos clickPos = mop.getBlockPos();
            // can we place liquid there?
            if (world.isBlockModifiable(player, clickPos))
            {
                // the block adjacent to the side we clicked on
                BlockPos targetPos = clickPos.offset(mop.sideHit);

                // can the player place there?
                if (player.canPlayerEdit(targetPos, mop.sideHit, itemstack))
                {
                    // try placing liquid
                    if (this.tryPlaceFluid(fluidStack.getFluid().getBlock(), world, targetPos)
                            && !player.capabilities.isCreativeMode)
                    {
                        // success!
                        player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

                        itemstack.stackSize--;
                        ItemStack emptyStack = empty != null ? empty.copy() : new ItemStack(this);

                        // check whether we replace the item or add the empty one to the inventory
                        if (itemstack.stackSize <= 0)
                        {
                            return emptyStack;
                        }
                        else
                        {
                            // add empty bucket to player inventory
                            ItemHandlerHelper.giveItemToPlayer(player, emptyStack);
                            return itemstack;
                        }
                    }
                }
            }
        }

        // couldn't place liquid there2
        return itemstack;
    }


    public boolean tryPlaceFluid(Block block, World worldIn, BlockPos pos)
    {
        if (block == null)
        {
            return false;
        }

        Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
        boolean isSolid = material.isSolid();

        // can only place in air or non-solid blocks
        if (!worldIn.isAirBlock(pos) && isSolid)
        {
            return false;
        }

        // water goes poof?
        if (worldIn.provider.doesWaterVaporize() && (block == Blocks.flowing_water || block == Blocks.water))
        {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            worldIn.playSoundEffect((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k
                            + 0.5F), "random.fizz", 0.5F,
                    2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

            for (int l = 0; l < 8; ++l)
            {
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                        (double) i + Math.random(),
                        (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
            }
        }
        else
        {
            if (!worldIn.isRemote && !isSolid && !material.isLiquid())
            {
                worldIn.destroyBlock(pos, true);
            }

            worldIn.setBlockState(pos, block.getDefaultState(), 3);
        }
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOW) // low priority so other mods can handle their stuff first
    public void onFillBucket(FillBucketEvent event)
    {
        if (event.getResult() != Event.Result.DEFAULT)
        {
            // event was already handled
            return;
        }

        // not for us to handle
        if (event.current == null ||
                !event.current.isItemEqual(empty) ||
                (nbtSensitive && ItemStack.areItemStackTagsEqual(event.current, empty)))
        {
            return;
        }

        // needs to target a block
        if (event.target == null || event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
        {
            return;
        }

        World world = event.world;
        BlockPos pos = event.target.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        // Note that water and lava are NOT an instance of IFluidBlock! They are therefore not handled by this code!
        if (state.getBlock() instanceof IFluidBlock)
        {
            IFluidBlock fluidBlock = (IFluidBlock) state.getBlock();
            if (fluidBlock.canDrain(world, pos))
            {
                FluidStack drained = fluidBlock.drain(world, pos, false);
                // check if it fits exactly
                if (drained != null && drained.amount == capacity)
                {
                    // check if the container accepts it
                    ItemStack filledBucket = new ItemStack(this);
                    int filled = this.fill(filledBucket, drained, false);
                    if (filled == drained.amount)
                    {
                        // actually transfer the fluid
                        drained = fluidBlock.drain(world, pos, true);
                        this.fill(filledBucket, drained, true);

                        // set it as the result
                        event.setResult(Event.Result.ALLOW);
                        event.result = filledBucket;
                    }
                    else
                    {
                        // cancel event, otherwise the vanilla minecraft ItemBucket would
                        // convert it into a water/lava bucket depending on the blocks material
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public static ItemStack getFilledBucket(UniversalBucket item, Fluid fluid)
    {
        ItemStack stack = new ItemStack(item);
        item.fill(stack, new FluidStack(fluid, item.capacity), true);
        return stack;
    }

  /* FluidContainer Management */

    @Override
    public FluidStack getFluid(ItemStack container)
    {
        return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
    }

    @Override
    public int getCapacity(ItemStack container)
    {
        return capacity;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill)
    {
        // has to be exactly 1, must be handled from the caller
        if (container.stackSize != 1)
        {
            return 0;
        }

        // can only fill exact capacity
        if (resource == null || resource.amount != capacity)
        {
            return 0;
        }
        // registered in the registry?
        if (!FluidRegistry.getBucketFluids().contains(resource.getFluid()))
        {
            return 0;
        }
        // fill the container
        if (doFill)
        {
            NBTTagCompound tag = container.getTagCompound();
            if (tag == null)
            {
                tag = new NBTTagCompound();
            }
            resource.writeToNBT(tag);
            container.setTagCompound(tag);
        }
        return capacity;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        // can only drain everything at once
        if (maxDrain < capacity)
        {
            return null;
        }

        FluidStack fluidStack = getFluid(container);
        if (doDrain && fluidStack != null)
        {
            if(empty != null)
            {
                container.setItem(empty.getItem());
                container.setTagCompound(empty.getTagCompound());
                container.setItemDamage(empty.getItemDamage());
            }
            else {
                container.stackSize = 0;
            }
        }

        return fluidStack;
    }
}
