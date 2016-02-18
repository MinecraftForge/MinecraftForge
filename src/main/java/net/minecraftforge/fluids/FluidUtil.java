package net.minecraftforge.fluids;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import java.util.concurrent.Callable;

public class FluidUtil
{

    private FluidUtil()
    {
    }

    /** Returns true if intercation was successful. */
    public static boolean interactWithTank(ItemStack stack, EntityPlayer player, IFluidHandler tank, EnumFacing side)
    {
        if (stack == null || player.worldObj.isRemote)
        {
            return true;
        }

        ItemStack result;

        // regular bucket?
        int slot = player.inventory.currentItem;
        if ((result = FluidUtil.tryFillBucket(stack, tank, side)) != null ||
                (result = FluidUtil.tryEmptyBucket(stack, tank, side)) != null)
        {
            // "use up" the input item if the player is not in creative
            if (!player.capabilities.isCreativeMode)
            {
                player.inventory.decrStackSize(slot, 1);
                ItemHandlerHelper.giveItemToPlayer(player, result, slot);
            }
            // send inventory updates to client
            if (player.inventoryContainer != null)
            {
                player.inventoryContainer.detectAndSendChanges();
            }
            return true;
        }
        // IFluidContainerItems
        else
        {
            // copy of the original item for creative mode
            ItemStack copy = stack.copy();
            boolean changedBucket = false;
            // convert to fluidcontainer-bucket if it's a regular empty bucket
            if (ItemStack.areItemsEqual(stack, FluidContainerRegistry.EMPTY_BUCKET) && FluidRegistry.isUniversalBucketEnabled())
            {
                // try using the forge fluid bucket if it's enabled
                stack = new ItemStack(ForgeModContainer.getInstance().universalBucket, copy.stackSize);
                changedBucket = true;
            }

            // try filling an empty fluidcontainer or emptying a filled fluidcontainer
            if (FluidUtil.tryFillFluidContainerItem(stack, tank, side, player) ||
                    FluidUtil.tryEmptyFluidContainerItem(stack, tank, side, player))
            {
                if (player.capabilities.isCreativeMode)
                {
                    // reset the stack that got modified
                    player.inventory.setInventorySlotContents(slot, copy);
                }
                else
                {
                    // we passed in multiple stacksize and it changed, that means the new items are in the inventory
                    // but we have to readjust the old ones back
                    if (changedBucket && stack.stackSize != copy.stackSize)
                    {
                        copy.stackSize = stack.stackSize;
                        // replace the previously changed buckets that were not used back
                        player.inventory.setInventorySlotContents(slot, copy);
                    }
                    // we have the new stack now, but since we changed it from its original we have to set the contents anew
                    else
                    {
                        // if the original stack was multiple, replace it
                        if (copy.stackSize > 1)
                        {
                            player.inventory.setInventorySlotContents(slot, stack);
                        }
                        // otherwise reinsert it into the inventory
                        else
                        {
                            player.inventory.setInventorySlotContents(slot, null);
                            ItemHandlerHelper.giveItemToPlayer(player, stack, slot);
                        }
                    }
                }
                // send inventory updates to client
                if (player.inventoryContainer != null)
                {
                    player.inventoryContainer.detectAndSendChanges();
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Fill an empty bucket from the given tank. Uses the FluidContainerRegistry.
     *
     * @param bucket The empty bucket
     * @param tank   The tank to fill the bucket from
     * @param side   Side to access the tank from
     * @return The filled bucket or null if the liquid couldn't be taken from the tank.
     */
    public static ItemStack tryFillBucket(ItemStack bucket, IFluidHandler tank, EnumFacing side)
    {
        FluidTankInfo[] info = tank.getTankInfo(side);
        // check for fluid in the tank
        if (info == null || info.length == 0)
        {
            return null;
        }
        // check if we actually have an empty bucket
        if (!FluidContainerRegistry.isEmptyContainer(bucket))
        {
            return null;
        }
        // fluid in the tank
        FluidStack inTank = info[0].fluid;
        // drain one bucket if possible
        FluidStack liquid = tank.drain(side, FluidContainerRegistry.getContainerCapacity(inTank, bucket), false);
        if (liquid != null && liquid.amount > 0)
        {
            // success, return filled bucket
            tank.drain(side, FluidContainerRegistry.getContainerCapacity(liquid, bucket), true);
            return FluidContainerRegistry.fillFluidContainer(liquid, bucket);
        }

        return null;
    }

    /**
     * Takes a filled bucket and tries to empty it into the given tank. Uses the FluidContainerRegistry.
     *
     * @param bucket The filled bucket
     * @param tank   The tank to fill with the bucket
     * @param side   Side to access the tank from
     * @return The empty bucket if successful, null if the tank couldn't be filled.
     */
    public static ItemStack tryEmptyBucket(ItemStack bucket, IFluidHandler tank, EnumFacing side)
    {
        // not a filled bucket
        if (!FluidContainerRegistry.isFilledContainer(bucket))
        {
            return null;
        }

        // try filling the fluid from the bucket into the tank
        FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(bucket);
        if (tank.canFill(side, liquid.getFluid()))
        {
            // how much can we put into the tank?
            int amount = tank.fill(side, liquid, false);
            // not everything?
            if (amount == liquid.amount)
            {
                // success, fully filled it into the tank, return empty bucket
                tank.fill(side, liquid, true);
                return FluidContainerRegistry.drainFluidContainer(bucket);
            }
        }

        return null;
    }

    /**
     * Takes an IFluidContainerItem and tries to fill it from the given tank.
     *
     * @param container The IFluidContainerItem Itemstack to fill. WILL BE MODIFIED!
     * @param tank      The tank to fill from
     * @param side      Side to access the tank from
     * @param player    The player that tries to fill the bucket. Needed if the input itemstack has a stacksize > 1 to determine where the filled container goes.
     * @return True if the IFluidContainerItem was filled successfully, false otherwise. The passed container will have been modified to accomodate for anything done in this method. New Itemstacks might have been added to the players inventory.
     */
    public static boolean tryFillFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, EntityPlayer player)
    {
        return tryFillFluidContainerItem(container, tank, side, new PlayerMainInvWrapper(player.inventory), -1, player);
    }

    public static boolean tryEmptyFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, EntityPlayer player)
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
     * @return True if the IFluidContainerItem was filled successfully, false otherwise. The passed container will have been modified to accomodate for anything done in this method. New Itemstacks might have been added to the players inventory.
     */
    public static boolean tryFillFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, IItemHandler inventory, int max, EntityPlayer player)
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
     */
    public static boolean tryEmptyFluidContainerItem(ItemStack container, IFluidHandler tank, EnumFacing side, IItemHandler inventory, int max, EntityPlayer player)
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
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean insertItemIntoo(ItemStack stack, IInventory inventory, World world, BlockPos pos, boolean isCreative)
    {
        if (stack == null)
        {
            return false;
        }
        // add it to the inventory

        if (inventory != null && addItemStackToInventory(stack, inventory, isCreative))
        {
            if (world != null && pos != null)
            {
                world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            return true;
        }
        else if (world != null && pos != null)
        {
            double d0 = pos.getY() + 0.5d;
            EntityItem entityitem = new EntityItem(world, pos.getX(), d0, pos.getZ(), stack);
            entityitem.setPickupDelay(40);

            entityitem.motionX = 0;
            //entityitem.motionY = 0;
            entityitem.motionZ = 0;

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(entityitem);
            }
            return true;
        }
        return false;
    }

    // generalized copy of InventoryPlayer.addItemStackToInventory without regarding itemstack sizes > 1
    private static boolean addItemStackToInventory(final ItemStack itemstack, IInventory inventory, boolean isCreative)
    {
        if (itemstack != null && itemstack.stackSize == 1 && itemstack.getItem() != null)
        {
            try
            {
                int sizeInventory = inventory.getSizeInventory();
                // player inventory requires hardcoding because we don't want to add to the armor slots
                if (inventory instanceof InventoryPlayer)
                {
                    sizeInventory -= 4;
                }
                if (itemstack.isItemDamaged())
                {
                    int j = 0;
                    for (; j < sizeInventory; ++j)
                    {
                        if (inventory.getStackInSlot(j) == null)
                        {
                            break;
                        }
                    }

                    // found empty slot
                    if (j < sizeInventory)
                    {
                        ItemStack copy = ItemStack.copyItemStack(itemstack);
                        copy.animationsToGo = 5;
                        inventory.setInventorySlotContents(j, copy);
                        itemstack.stackSize = 0;
                        return true;
                    }
                    else if (isCreative)
                    {
                        itemstack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    int origSize = itemstack.stackSize;
                    // go through the inventory and try to fill up already existing items
                    for (int i = 0; i < sizeInventory; i++)
                    {
                        ItemStack slot = inventory.getStackInSlot(i);
                        if (slot != null && slot.getItem() == itemstack.getItem() &&
                                slot.isStackable() && slot.stackSize < slot.getMaxStackSize() &&
                                slot.stackSize < inventory.getInventoryStackLimit() &&
                                (!slot.getHasSubtypes() || slot.getMetadata() == itemstack.getMetadata()) &&
                                ItemStack.areItemStackTagsEqual(slot, itemstack))
                        {
                            // stackable
                            int dif = itemstack.stackSize;
                            if (dif > slot.getMaxStackSize() - slot.stackSize)
                            {
                                dif = slot.getMaxStackSize() - slot.stackSize;
                            }
                            if (dif > inventory.getInventoryStackLimit())
                            {
                                dif = inventory.getInventoryStackLimit();
                            }
                            slot.stackSize += dif;
                            slot.animationsToGo = 5;
                            itemstack.stackSize -= dif;
                            inventory.setInventorySlotContents(i, slot);

                            if (itemstack.stackSize <= 0)
                            {
                                break;
                            }
                        }
                    }

                    if (itemstack.stackSize > 0)
                    {
                        // find empty slot
                        for (int i = 0; i < sizeInventory; i++)
                        {
                            if (inventory.getStackInSlot(i) == null)
                            {
                                ItemStack slot = ItemStack.copyItemStack(itemstack);
                                if (slot.stackSize > inventory.getInventoryStackLimit())
                                {
                                    slot.stackSize = inventory.getInventoryStackLimit();
                                }
                                slot.animationsToGo = 5;

                                inventory.setInventorySlotContents(i, slot);
                                itemstack.stackSize -= slot.stackSize;
                            }

                            if (itemstack.stackSize <= 0)
                            {
                                break;
                            }
                        }
                    }

                    if (itemstack.stackSize > 0 && isCreative)
                    {
                        itemstack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return itemstack.stackSize < origSize;
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(itemstack.getItem())));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(itemstack.getMetadata()));
                crashreportcategory.addCrashSectionCallable("Item name", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return itemstack.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return false;
        }
    }
}
