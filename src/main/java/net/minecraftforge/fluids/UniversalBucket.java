package net.minecraftforge.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
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

    private final int capacity; // how much the bucket holds
    private final ItemStack empty; // empty item to return and recognize when filling
    private final boolean nbtSensitive;

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
            FluidStack fs = new FluidStack(fluid, getCapacity());
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
            if(getEmpty() != null)
            {
                return getEmpty().getDisplayName();
            }
            return super.getItemStackDisplayName(stack);
        }

        String unloc = this.getUnlocalizedNameInefficiently(stack);

        if (I18n.canTranslate(unloc + "." + fluidStack.getFluid().getName()))
        {
            return I18n.translateToLocal(unloc + "." + fluidStack.getFluid().getName());
        }

        return I18n.translateToLocalFormatted(unloc + ".name", fluidStack.getLocalizedName());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer player, EnumHand hand)
    {
        FluidStack fluidStack = getFluid(itemstack);
        // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
        if (fluidStack == null)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        // clicked on a block?
        RayTraceResult mop = this.getMovingObjectPositionFromPlayer(world, player, false);

        if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

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
                if (this.tryPlaceFluid(player, player.getEntityWorld(), fluidStack.getFluid().getBlock(), targetPos)
                        && !player.capabilities.isCreativeMode)
                {
                    // success!
                    player.addStat(StatList.func_188057_b(this));

                    itemstack.stackSize--;
                    ItemStack emptyStack = getEmpty() != null ? getEmpty().copy() : new ItemStack(this);

                    // check whether we replace the item or add the empty one to the inventory
                    if (itemstack.stackSize <= 0)
                    {
                        return ActionResult.newResult(EnumActionResult.SUCCESS, emptyStack);
                    }
                    else
                    {
                        // add empty bucket to player inventory
                        ItemHandlerHelper.giveItemToPlayer(player, emptyStack);
                        return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
                    }
                }
            }
        }

        // couldn't place liquid there2
        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
    }

    // compatibility
    @Deprecated
    public boolean tryPlaceFluid(Block block, World worldIn, BlockPos pos)
    {
        return tryPlaceFluid(null, worldIn, block, pos);
    }

    private boolean tryPlaceFluid(EntityPlayer player, World worldIn, Block block, BlockPos pos)
    {
        if (block == null)
        {
            return false;
        }
        if(worldIn == null && player != null)
        {
            worldIn = player.getEntityWorld();
        }

        Material material = worldIn.getBlockState(pos).getMaterial();
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
            worldIn.playSound(null, pos, SoundEvents.block_fire_extinguish, SoundCategory.BLOCKS, 0.5F,
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

            if(player != null)
            {
                SoundEvent soundevent = block.getMaterial(block.getDefaultState()) == Material.lava ? SoundEvents.item_bucket_empty_lava : SoundEvents.item_bucket_empty;
                worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlockState(pos, block.getDefaultState(), 3);
            }
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
        if (event.getEmptyBucket() == null ||
                !event.getEmptyBucket().isItemEqual(getEmpty()) ||
                (isNbtSensitive() && ItemStack.areItemStackTagsEqual(event.getEmptyBucket(), getEmpty())))
        {
            return;
        }

        // needs to target a block
        if (event.getTarget() == null || event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = event.getTarget().getBlockPos();
        IBlockState state = world.getBlockState(pos);
        // Note that water and lava are NOT an instance of IFluidBlock! They are therefore not handled by this code!
        if (state.getBlock() instanceof IFluidBlock)
        {
            IFluidBlock fluidBlock = (IFluidBlock) state.getBlock();
            if (fluidBlock.canDrain(world, pos))
            {
                FluidStack drained = fluidBlock.drain(world, pos, false);
                // check if it fits exactly
                if (drained != null && drained.amount == getCapacity())
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
                        event.setFilledBucket(filledBucket);

                        // sound!
                        SoundEvent soundevent = state.getMaterial() == Material.lava ? SoundEvents.item_bucket_fill_lava : SoundEvents.item_bucket_fill;
                        event.getEntityPlayer().playSound(soundevent, 1.0F, 1.0F);
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
        item.fill(stack, new FluidStack(fluid, item.getCapacity()), true);
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
        return getCapacity();
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
        if (resource == null || resource.amount != getCapacity())
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
        return getCapacity();
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        // can only drain everything at once
        if (maxDrain < getCapacity())
        {
            return null;
        }

        FluidStack fluidStack = getFluid(container);
        if (doDrain && fluidStack != null)
        {
            if(getEmpty() != null)
            {
                container.setItem(getEmpty().getItem());
                container.setTagCompound(getEmpty().getTagCompound());
                container.setItemDamage(getEmpty().getItemDamage());
            }
            else {
                container.stackSize = 0;
            }
        }

        return fluidStack;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public ItemStack getEmpty()
    {
        return empty;
    }

    public boolean isNbtSensitive()
    {
        return nbtSensitive;
    }
}
