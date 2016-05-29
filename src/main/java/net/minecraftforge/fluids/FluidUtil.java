package net.minecraftforge.fluids;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerItemWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerRegistryWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidHandlerWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class FluidUtil
{

    private FluidUtil()
    {
    }

    /**
     * Used to handle the common case of a fluid item right-clicking on a fluid handler.
     * First it tries to fill the container item from the fluid handler,
     * if that action fails then it tries to drain the container item into the fluid handler.
     *
     * Returns true if interaction was successful.
     * Returns false if interaction failed.
     */
    public static boolean interactWithFluidHandler(ItemStack stack, IFluidHandler fluidHandler, EntityPlayer player)
    {
        if (stack == null || fluidHandler == null || player == null)
        {
            return false;
        }

        IItemHandler playerInventory = new InvWrapper(player.inventory);
        return tryFillContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player) ||
                tryEmptyContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player);
    }

    /**
     * Fill a container from the given fluidSource.
     *
     * @param container The container to be filled. Will not be modified.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount The largest amount of fluid that should be transferred.
     * @param player The player to make the filling noise. Pass null for no noise.
     * @param doFill true if the container should actually be filled, false if it should be simulated.
     * @return The filled container or null if the liquid couldn't be taken from the tank.
     */
    public static ItemStack tryFillContainer(ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable EntityPlayer player, boolean doFill)
    {
        container = container.copy(); // do not modify the input
        IFluidHandler containerFluidHandler = getFluidHandler(container);
        if (containerFluidHandler != null)
        {
            FluidStack simulatedTransfer = tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
            if (simulatedTransfer != null)
            {
                if (doFill)
                {
                    tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
                    if (player != null)
                    {
                        SoundEvent soundevent = simulatedTransfer.getFluid().getFillSound(simulatedTransfer);
                        player.playSound(soundevent, 1f, 1f);
                    }
                }
                else
                {
                    containerFluidHandler.fill(simulatedTransfer, true);
                }
                return container;
            }
        }
        return null;
    }

    /**
     * Takes a filled container and tries to empty it into the given tank.
     *
     * @param container The filled container. Will not be modified.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount The largest amount of fluid that should be transferred.
     * @param player Player for making the bucket drained sound. Pass null for no noise.
     * @param doDrain true if the container should actually be drained, false if it should be simulated.
     * @return The empty container if successful, null if the fluid handler couldn't be filled.
     */
    @Nullable
    public static ItemStack tryEmptyContainer(ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable EntityPlayer player, boolean doDrain)
    {
        container = container.copy(); // do not modify the input
        IFluidHandler containerFluidHandler = getFluidHandler(container);
        if (containerFluidHandler != null)
        {
            FluidStack simulatedTransfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, false);
            if (simulatedTransfer != null)
            {
                if (doDrain)
                {
                    tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
                    if (player != null)
                    {
                        SoundEvent soundevent = simulatedTransfer.getFluid().getEmptySound(simulatedTransfer);
                        player.playSound(soundevent, 1f, 1f);
                    }
                }
                else
                {
                    containerFluidHandler.drain(simulatedTransfer, true);
                }
                return container;
            }
        }
        return null;
    }

    /**
     * Takes an Fluid Container Item and tries to fill it from the given tank.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the filled container in the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container   The Fluid Container Itemstack to fill. This stack WILL be modified on success.
     * @param fluidSource The fluid source to fill from
     * @param inventory   An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount   Maximum amount of fluid to take from the tank.
     * @param player      The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the filled stack.
     * @return True if the container was filled successfully and stowed, false otherwise.
     */
    public static boolean tryFillContainerAndStow(ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container == null || container.stackSize < 1)
        {
            return false;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            ItemStack singleContainer = container.copy();
            singleContainer.stackSize = 1;
            ItemStack filledReal = tryFillContainer(singleContainer, fluidSource, maxAmount, player, true);
            if (filledReal != null)
            {
                return true;
            }
        }
        else if (container.stackSize == 1) // don't need to stow anything, just fill and edit the container stack
        {
            ItemStack filledReal = tryFillContainer(container, fluidSource, maxAmount, player, true);
            if (filledReal != null)
            {
                container.setItem(filledReal.getItem());
                container.setTagCompound(filledReal.getTagCompound());
                container.setItemDamage(filledReal.getItemDamage());
                return true;
            }
        }
        else
        {
            ItemStack singleContainer = container.copy();
            singleContainer.stackSize = 1;

            ItemStack filledSimulated = tryFillContainer(singleContainer, fluidSource, maxAmount, player, false);
            if (filledSimulated != null)
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledSimulated, true);
                if (remainder == null || player != null)
                {
                    ItemStack filledReal = tryFillContainer(singleContainer, fluidSource, maxAmount, player, true);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal, false);

                    // give it to the player or drop it at their feet
                    if (remainder != null && player != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    container.stackSize--;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Takes an Fluid Container Item, tries to empty it into the fluid handler, and stows it in the given inventory.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the emptied container in the given inventory.
     * If the inventory does not accept the emptied container, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container   The Fluid Container Itemstack to fill. This stack WILL be modified on success.
     * @param fluidSource The fluid source to fill from
     * @param inventory   An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param maxAmount   Maximum amount of fluid to take from the tank.
     * @param player      The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the filled stack.
     * @return True if the container was filled successfully and stowed, false otherwise.
     */
    public static boolean tryEmptyContainerAndStow(ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container == null || container.stackSize < 1)
        {
            return false;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            ItemStack singleContainer = container.copy();
            singleContainer.stackSize = 1;
            ItemStack emptiedReal = tryEmptyContainer(singleContainer, fluidSource, maxAmount, player, true);
            if (emptiedReal != null)
            {
                return true;
            }
        }
        else if (container.stackSize == 1) // don't need to stow anything, just fill and edit the container stack
        {
            ItemStack emptiedReal = tryEmptyContainer(container, fluidSource, maxAmount, player, true);
            if (emptiedReal != null)
            {
                container.setItem(emptiedReal.getItem());
                container.setTagCompound(emptiedReal.getTagCompound());
                container.setItemDamage(emptiedReal.getItemDamage());
                return true;
            }
        }
        else
        {
            ItemStack singleContainer = container.copy();
            singleContainer.stackSize = 1;

            ItemStack emptiedSimulated = tryEmptyContainer(singleContainer, fluidSource, maxAmount, player, false);
            if (emptiedSimulated != null)
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated, true);
                if (remainder == null || player != null)
                {
                    ItemStack emptiedReal = tryEmptyContainer(singleContainer, fluidSource, maxAmount, player, true);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal, false);

                    // give it to the player or drop it at their feet
                    if (remainder != null && player != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    container.stackSize--;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Fill a destination fluid handler from a source fluid handler.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount The largest amount of fluid that should be transferred.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nullable
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doFill)
    {
        FluidStack drainable = fluidSource.drain(maxAmount, false);
        if (drainable != null && drainable.amount > 0)
        {
            int fillableAmount = fluidDestination.fill(drainable, false);
            if (fillableAmount > 0)
            {
                FluidStack drained = fluidSource.drain(fillableAmount, doFill);
                if (drained != null)
                {
                    drained.amount = fluidDestination.fill(drained, doFill);
                    return drained;
                }
            }
        }
        return null;
    }

    /**
     * Helper method to get an IFluidHandler for an itemStack.
     *
     * The itemStack passed in here WILL be modified, the IFluidHandler acts on it directly.
     *
     * Note that the itemStack MUST have a stackSize of 1 if you want to fill or drain it.
     * You can't fill or drain a whole stack at once, if you do then liquid is multiplied or destroyed.
     *
     * Vanilla buckets will be converted to universal buckets if they are enabled.
     *
     * Returns null if the itemStack passed in does not have a fluid handler.
     */
    @Nullable
    public static IFluidHandler getFluidHandler(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return null;
        }

        // convert vanilla empty bucket to forge universal bucket
        if (FluidRegistry.isUniversalBucketEnabled() && ItemStack.areItemsEqual(itemStack, FluidContainerRegistry.EMPTY_BUCKET))
        {
            itemStack.setItem(ForgeModContainer.getInstance().universalBucket);
        }

        // check for capability
        if (itemStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        }

        // legacy container handling
        Item item = itemStack.getItem();
        if (item instanceof IFluidContainerItem)
        {
            return new FluidContainerItemWrapper((IFluidContainerItem) item, itemStack);
        }
        else if (FluidContainerRegistry.isContainer(itemStack))
        {
            return new FluidContainerRegistryWrapper(itemStack);
        }
        else
        {
            return null;
        }
    }

    /**
     * Helper method to get an IFluidHandler for at a block position.
     *
     * Returns null if there is no valid fluid handler.
     */
    @Nullable
    public static IFluidHandler getFluidHandler(World world, BlockPos blockPos, @Nullable EnumFacing side)
    {
        IBlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state))
        {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side))
            {
                return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            }
        }
        else if (block instanceof IFluidBlock)
        {
            return new FluidBlockWrapper((IFluidBlock) block, world, blockPos);
        }
        else if (block instanceof BlockLiquid)
        {
            return new BlockLiquidWrapper((BlockLiquid) block, world, blockPos);
        }

        return null;
    }

    /**
     * Tries to place a fluid in the world in block form.
     * Makes a fluid emptying sound when successful.
     * Checks if water-like fluids should vaporize like in the nether.
     *
     * Modeled after {@link net.minecraft.item.ItemBucket#tryPlaceContainedLiquid(EntityPlayer, World, BlockPos)}
     *
     * @param player  Player who places the fluid. May be null for blocks like dispensers.
     * @param worldIn World to place the fluid in
     * @param fluid   The fluid to place.
     * @param pos     The position in the world to place the fluid block
     * @return true if successful
     */
    public static boolean tryPlaceFluid(@Nullable EntityPlayer player, World worldIn, Fluid fluid, BlockPos pos)
    {
        if (worldIn == null || fluid == null || pos == null)
        {
            return false;
        }

        // check that we can place the fluid at the destination
        IBlockState destBlockState = worldIn.getBlockState(pos);
        Material destMaterial = destBlockState.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(worldIn, pos);
        if (!worldIn.isAirBlock(pos) && !isDestNonSolid && !isDestReplaceable)
        {
            return false; // Non-air, solid, unreplacable block. We can't put fluid here.
        }

        IBlockState fluidBlockState = fluid.getBlock().getDefaultState();

        if (worldIn.provider.doesWaterVaporize() && fluidBlockState.getMaterial() == Material.WATER)
        {
            worldIn.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

            for (int l = 0; l < 8; ++l)
            {
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) pos.getX() + Math.random(), (double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
            }
        }
        else
        {
            if (!worldIn.isRemote && (isDestNonSolid || isDestReplaceable) && !destMaterial.isLiquid())
            {
                worldIn.destroyBlock(pos, true);
            }

            SoundEvent soundevent = fluid.getEmptySound(worldIn, pos);
            worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);

            worldIn.setBlockState(pos, fluidBlockState, 11);
        }
        return true;
    }

	/**
     *
     * @param emptyContainer The empty container to fill. Will not be modified.
     * @param playerIn       The player filling the container. Optional.
     * @param worldIn        The world the fluid is in.
     * @param pos            The position of the fluid in the world.
     * @param side           The side of the fluid that is being drained.
     * @return a filled container if it was successful. returns null on failure.
	 */
    @Nullable
    public static ItemStack tryPickUpFluid(ItemStack emptyContainer, @Nullable EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side)
    {
        if (emptyContainer == null || worldIn == null || pos == null) {
            return null;
        }

        IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(worldIn, pos, side);
        if (targetFluidHandler != null)
        {
            return FluidUtil.tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
        }
        return null;
    }

    /**
     * Returns true if interaction was successful.
     * @deprecated use {@link #interactWithFluidHandler(ItemStack, IFluidHandler, EntityPlayer)}
     */
    @Deprecated
    public static boolean interactWithTank(ItemStack stack, EntityPlayer player, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side)
    {
        IFluidHandler fluidHandler = new FluidHandlerWrapper(tank, side);
        return interactWithFluidHandler(stack, fluidHandler, player);
    }

    /**
     * @deprecated use {@link #tryFillContainer(ItemStack, IFluidHandler, int, EntityPlayer, boolean)}
     */
    @Deprecated
    public static ItemStack tryFillBucket(ItemStack bucket, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side)
    {
        return tryFillBucket(bucket, tank, side, null);
    }

    /**
     * Fill an empty bucket from the given tank. Uses the FluidContainerRegistry.
     *
     * @param bucket The empty bucket
     * @param tank   The tank to fill the bucket from
     * @param side   Side to access the tank from
     * @return The filled bucket or null if the liquid couldn't be taken from the tank.
     * @deprecated use {@link #tryFillContainer(ItemStack, IFluidHandler, int, EntityPlayer, boolean)}
     */
    @Deprecated
    public static ItemStack tryFillBucket(ItemStack bucket, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side, EntityPlayer player)
    {
        IFluidHandler newFluidHandler = new FluidHandlerWrapper(tank, side);
        return tryFillContainer(bucket, newFluidHandler, FluidContainerRegistry.BUCKET_VOLUME, player, true);
    }

    /**
     * @deprecated use {@link #tryEmptyContainer(ItemStack, IFluidHandler, int, EntityPlayer, boolean)}
     */
    @Deprecated
    public static ItemStack tryEmptyBucket(ItemStack bucket, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side)
    {
        return tryEmptyBucket(bucket, tank, side, null);
    }

    /**
     * Takes a filled bucket and tries to empty it into the given tank. Uses the FluidContainerRegistry.
     *
     * @param bucket The filled bucket
     * @param tank   The tank to fill with the bucket
     * @param side   Side to access the tank from
     * @param player Player for making the bucket drained sound.
     * @return The empty bucket if successful, null if the tank couldn't be filled.
     * @deprecated use {@link #tryFillContainer(ItemStack, IFluidHandler, int, EntityPlayer, boolean)}
     */
    @Deprecated
    public static ItemStack tryEmptyBucket(ItemStack bucket, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side, EntityPlayer player)
    {
        IFluidHandler destination = new FluidHandlerWrapper(tank, side);
        return tryEmptyContainer(bucket, destination, FluidContainerRegistry.BUCKET_VOLUME, player, true);
    }



    /**
     * Takes an IFluidContainerItem and tries to fill it from the given tank.
     *
     * @param container The IFluidContainerItem Itemstack to fill. WILL BE MODIFIED!
     * @param tank      The tank to fill from
     * @param side      Side to access the tank from
     * @param player    The player that tries to fill the bucket. Needed if the input itemstack has a stacksize > 1 to determine where the filled container goes.
     * @return True if the IFluidContainerItem was filled successfully, false otherwise. The passed container will have been modified to accommodate for anything done in this method. New Itemstacks might have been added to the players inventory.
     * @deprecated use {@link #tryFillContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, EntityPlayer)}
     */
    @Deprecated
    public static boolean tryFillFluidContainerItem(ItemStack container, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side, EntityPlayer player)
    {
        return tryFillFluidContainerItem(container, tank, side, new PlayerMainInvWrapper(player.inventory), -1, player);
    }

    /**
     * @deprecated use {@link #tryEmptyContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, EntityPlayer)}
     */
    @Deprecated
    public static boolean tryEmptyFluidContainerItem(ItemStack container, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side, EntityPlayer player)
    {
        return tryEmptyFluidContainerItem(container, tank, side, new PlayerMainInvWrapper(player.inventory), -1, player);
    }

    /**
     * Takes an IFluidContainerItem and tries to fill it from the given tank.
     * If the input itemstack has a stacksize >1 new itemstacks will be created and inserted into the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     * If player is null in this case, the action will be aborted.
     * To support buckets that are not in the FluidContainerRegistry but implement IFluidContainerItem be sure to convert
     * the empty bucket to your empty bucket variant before passing it to this function.
     *
     * @param container  The IFluidContainerItem Itemstack to fill. WILL BE MODIFIED!
     * @param tank       The tank to fill from
     * @param side       Side to access the tank from
     * @param inventory  An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param max        Maximum amount to take from the tank. Uses IFluidContainerItem capacity if <= 0
     * @param player     The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the filled stack.
     * @return True if the IFluidContainerItem was filled successfully, false otherwise. The passed container will have been modified to accommodate for anything done in this method. New Itemstacks might have been added to the players inventory.
     * @deprecated use {@link #tryFillContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, EntityPlayer)}
     */
    @Deprecated
    public static boolean tryFillFluidContainerItem(ItemStack container, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side, IItemHandler inventory, int max, @Nullable EntityPlayer player)
    {
        if (!(container.getItem() instanceof IFluidContainerItem))
        {
            // not a fluid container
            return false;
        }

        IFluidContainerItem fluidContainer = (IFluidContainerItem) container.getItem();
        if (fluidContainer.getFluid(container) != null)
        {
            // not empty
            return false;
        }

        // if no maximum is given, fill fully
        if (max <= 0)
        {
            max = fluidContainer.getCapacity(container);
        }
        // check how much liquid we can drain from the tank
        FluidStack liquid = tank.drain(side, max, false);
        if (liquid != null && liquid.amount > 0)
        {
            // check which itemstack shall be altered by the fill call
            if (container.stackSize > 1)
            {
                // create a copy of the container and fill it
                ItemStack toFill = container.copy();
                toFill.stackSize = 1;
                int filled = fluidContainer.fill(toFill, liquid, false);
                if (filled > 0)
                {
                    // This manipulates the container Itemstack!
                    filled = fluidContainer.fill(toFill, liquid, true);
                }
                else
                {
                    // IFluidContainer does not accept the fluid/amount
                    return false;
                }

                if(player == null || !player.worldObj.isRemote)
                {
                    // check if we can give the itemstack to the inventory
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, toFill, true);
                    if (remainder != null && player == null)
                    {
                        // couldn't add to the inventory and don't have a player to drop the item at
                        return false;
                    }
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, toFill, false);
                    // give it to the player or drop it at his feet
                    if (remainder != null && player != null)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }
                }

                // the result has been given to the player, drain the tank since everything is ok
                tank.drain(side, filled, true);

                // decrease its stacksize to accommodate the filled one (it was >1 from the check above)
                container.stackSize--;
            }
            // just 1 empty container to fill, no special treatment needed
            else
            {
                int filled = fluidContainer.fill(container, liquid, false);
                if (filled > 0)
                {
                    // This manipulates the container Itemstack!
                    filled = fluidContainer.fill(container, liquid, true);
                }
                else
                {
                    // IFluidContainer does not accept the fluid/amount
                    return false;
                }
                tank.drain(side, filled, true);
            }

            // play sound
            if(player != null)
            {
                SoundEvent soundevent = liquid.getFluid().getFillSound(liquid);
                player.playSound(soundevent, 1f, 1f);
            }

            return true;
        }

        return false;
    }

    /**
     * Takes an IFluidContainerItem and tries to empty it into the given tank.
     * If the input itemstack has a stacksize >1 new itemstacks will be created and inserted into the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     * If player is null in this case, the action will be aborted.
     *
     * @param container The IFluidContainerItem Itemstack to empty. WILL BE MODIFIED!
     * @param tank      The tank to fill
     * @param side      Side to access the tank from
     * @param inventory  An inventory where any additionally created item (filled container if multiple empty are present) are put
     * @param max        Maximum amount to take from the tank. Uses IFluidContainerItem capacity if <= 0
     * @param player     The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the emptied stack.
     * @return True if the container successfully emptied at least 1 mb into the tank, false otherwise. The passed container itemstack will be modified to accommodate for the liquid transaction.
     * @deprecated use {@link #tryEmptyContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, EntityPlayer)}
     */
    @Deprecated
    public static boolean tryEmptyFluidContainerItem(ItemStack container, net.minecraftforge.fluids.IFluidHandler tank, EnumFacing side, IItemHandler inventory, int max, EntityPlayer player)
    {
        if (!(container.getItem() instanceof IFluidContainerItem))
        {
            // not a fluid container
            return false;
        }

        IFluidContainerItem fluidContainer = (IFluidContainerItem) container.getItem();
        if (fluidContainer.getFluid(container) != null)
        {
            // drain out of the fluidcontainer
            if (max <= 0)
            {
                max = fluidContainer.getCapacity(container);
            }
            FluidStack drained = fluidContainer.drain(container, max, false);
            if (drained != null)
            {
                // check how much we can fill into the tank
                int filled = tank.fill(side, drained, false);
                if (filled > 0)
                {
                    // verify that the new amount can also be drained (buckets can only extract full amounts for example)
                    drained = fluidContainer.drain(container, filled, false);
                    // actually transfer the liquid if everything went well
                    if (drained != null && drained.amount == filled)
                    {
                        // more than 1 filled itemstack, ensure that we can insert the changed container
                        if (container.stackSize > 1)
                        {
                            // create a copy of the container and drain it
                            ItemStack toEmpty = container.copy();
                            toEmpty.stackSize = 1;
                            drained = fluidContainer.drain(toEmpty, filled, true); // modifies the container!

                            if(player == null || !player.worldObj.isRemote)
                            {
                                // try adding the drained container to the inventory
                                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, toEmpty, true);
                                if (remainder != null && player == null)
                                {
                                    // couldn't add to the inventory and don't have a player to drop the item at
                                    return false;
                                }
                                remainder = ItemHandlerHelper.insertItemStacked(inventory, toEmpty, false);
                                // give it to the player or drop it at his feet
                                if (remainder != null && player != null)
                                {
                                    ItemHandlerHelper.giveItemToPlayer(player, remainder);
                                }
                            }

                            // the result has been given to the player, fill the tank since everything is ok
                            tank.fill(side, drained, true);

                            // decrease its stacksize to accommodate the filled one (it was >1 from the check above)
                            container.stackSize--;
                        }
                        // itemstack of size 1, no special treatment needed
                        else
                        {
                            // This manipulates the container Itemstack!
                            drained = fluidContainer.drain(container, filled, true); // modifies the container!
                            tank.fill(side, drained, true);
                        }

                        // play sound
                        if(player != null)
                        {
                            SoundEvent soundevent = drained.getFluid().getEmptySound(drained);
                            player.playSound(soundevent, 1f, 1f);
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
