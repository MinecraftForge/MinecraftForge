package net.minecraftforge.ingredients;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.ingredients.capability.CapabilityIngredientHandler;
import net.minecraftforge.ingredients.capability.IIngredientHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import javax.annotation.Nullable;

public class IngredientUtil
{
    private IngredientUtil()
    {
    }

    /**
     * Used to hand common cases of ingredient right-clicking on an ingredient handler.
     * <p/>
     * First it tries to add the container item from the ingredient handler,
     * if that action fails, then it tries to pull from the ingredient handler into the container.
     *
     * @param stack
     *          The ingredient-container/source ItemStack that holds the ingredient stack.
     * @param handler
     *          The {@link IIngredientHandler} that is being interacted with.
     * @param player
     *          The player that is using the container
     * @return true if the interaction passed, and false if it failed.
     * */
    public static boolean interactWithIngredientHandler(ItemStack stack, IIngredientHandler handler, EntityPlayer player)
    {
        if(stack == null || handler == null || player == null)
        {
            return false;
        }

        IItemHandler playerInventory = new InvWrapper(player.inventory);
        return false;
    }

    /**
     * Fill a container from the given handler.
     *
     * @param container   The container to be added. Will not be modified.
     * @param handler The ingredient handler to be removeed.
     * @param maxAmount   The largest amount of ingredient that should be transferred.
     * @param player      The player to make the adding noise. Pass null for no noise.
     * @param doFill      true if the container should actually be added, false if it should be simulated.
     * @return The added container or null if the ingredient couldn't be taken from the source.
     */
    public static ItemStack tryFillContainer(ItemStack container, IIngredientHandler handler, int maxAmount, @Nullable EntityPlayer player, boolean doFill)
    {
        container = container.copy(); // do not modify the input
        container.stackSize = 1;
        IIngredientHandler containerIngredientHandler = getIngredientHandler(container);
        if (containerIngredientHandler != null)
        {
            IngredientStack simulatedTransfer = tryIngredientTransfer(containerIngredientHandler, handler, maxAmount, false);
            if (simulatedTransfer != null)
            {
                if (doFill)
                {
                    tryIngredientTransfer(containerIngredientHandler, handler, maxAmount, true);
                    if (player != null)
                    {
                        SoundEvent soundevent = simulatedTransfer.getIngredient().getAddedSound(simulatedTransfer);
                        player.playSound(soundevent, 1f, 1f);
                    }
                }
                else
                {
                    containerIngredientHandler.add(simulatedTransfer, true);
                }
                return container;
            }
        }
        return null;
    }

    /**
     * Takes a added container and tries to empty it into the given source.
     *
     * @param container        The added container. Will not be modified.
     * @param ingredientDestination The ingredient handler to be added by the container.
     * @param maxAmount        The largest amount of ingredient that should be transferred.
     * @param player           Player for making the bucket removeed sound. Pass null for no noise.
     * @param doDrain          true if the container should actually be removeed, false if it should be simulated.
     * @return The empty container if successful, null if the ingredient handler couldn't be added.
     *         NOTE The empty container will have a stackSize of 0 when a added container is consumable,
     *              i.e. it has a "null" empty container but has successfully been emptied.
     */
    @Nullable
    public static ItemStack tryEmptyContainer(ItemStack container, IIngredientHandler ingredientDestination, int maxAmount, @Nullable EntityPlayer player, boolean doDrain)
    {
        container = container.copy(); // do not modify the input
        container.stackSize = 1;
        IIngredientHandler containerIngredientHandler = getIngredientHandler(container);
        if (containerIngredientHandler != null)
        {
            IngredientStack simulatedTransfer = tryIngredientTransfer(ingredientDestination, containerIngredientHandler, maxAmount, false);
            if (simulatedTransfer != null)
            {
                if (doDrain)
                {
                    tryIngredientTransfer(ingredientDestination, containerIngredientHandler, maxAmount, true);
                    if (player != null)
                    {
                        SoundEvent soundevent = simulatedTransfer.getIngredient().getRemovedSound(simulatedTransfer);
                        player.playSound(soundevent, 1f, 1f);
                    }
                }
                else
                {
                    containerIngredientHandler.remove(simulatedTransfer, true);
                }
                return container;
            }
        }
        return null;
    }

    /**
     * Takes an Ingredient Container Item and tries to add it from the given source.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the added container in the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container   The Ingredient Container Itemstack to add. This stack WILL be modified on success.
     * @param ingredientSource The ingredient source to add from
     * @param inventory   An inventory where any additionally created item (added container if multiple empty are present) are put
     * @param maxAmount   Maximum amount of ingredient to take from the source.
     * @param player      The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the added stack.
     * @return True if the container was added successfully and stowed, false otherwise.
     */
    public static boolean tryFillContainerAndStow(ItemStack container, IIngredientHandler ingredientSource, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container == null || container.stackSize < 1)
        {
            return false;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            ItemStack addedReal = tryFillContainer(container, ingredientSource, maxAmount, player, true);
            if (addedReal != null)
            {
                return true;
            }
        }
        else if (container.stackSize == 1) // don't need to stow anything, just add and edit the container stack
        {
            ItemStack addedReal = tryFillContainer(container, ingredientSource, maxAmount, player, true);
            if (addedReal != null)
            {
                container.deserializeNBT(addedReal.serializeNBT());
                return true;
            }
        }
        else
        {
            ItemStack addedSimulated = tryFillContainer(container, ingredientSource, maxAmount, player, false);
            if (addedSimulated != null)
            {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, addedSimulated, true);
                if (remainder == null || player != null)
                {
                    ItemStack addedReal = tryFillContainer(container, ingredientSource, maxAmount, player, true);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, addedReal, false);

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
     * Takes an Ingredient Container Item, tries to empty it into the ingredient handler, and stows it in the given inventory.
     * If the player is in creative mode, the container will not be modified on success, and no additional items created.
     * If the input itemstack has a stacksize > 1 it will stow the emptied container in the given inventory.
     * If the inventory does not accept the emptied container, it will be given to the player or dropped at the players feet.
     *      If player is null in this case, the action will be aborted.
     *
     * @param container        The added Ingredient Container Itemstack to empty. This stack WILL be modified on success.
     * @param ingredientDestination The ingredient destination to add from the ingredient container.
     * @param inventory        An inventory where any additionally created item (added container if multiple empty are present) are put
     * @param maxAmount        Maximum amount of ingredient to take from the source.
     * @param player           The player that gets the items the inventory can't take. Can be null, only used if the inventory cannot take the added stack.
     * @return True if the container was added successfully and stowed, false otherwise.
     */
    public static boolean tryEmptyContainerAndStow(ItemStack container, IIngredientHandler ingredientDestination, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player)
    {
        if (container == null || container.stackSize < 1)
        {
            return false;
        }

        if (player != null && player.capabilities.isCreativeMode)
        {
            ItemStack emptiedReal = tryEmptyContainer(container, ingredientDestination, maxAmount, player, true);
            if (emptiedReal != null)
            {
                return true;
            }
        }
        else if (container.stackSize == 1) // don't need to stow anything, just add and edit the container stack
        {
            ItemStack emptiedReal = tryEmptyContainer(container, ingredientDestination, maxAmount, player, true);
            if (emptiedReal != null)
            {
                if (emptiedReal.stackSize <= 0)
                {
                    container.stackSize--;
                }
                else
                {
                    container.deserializeNBT(emptiedReal.serializeNBT());
                }
                return true;
            }
        }
        else
        {
            ItemStack emptiedSimulated = tryEmptyContainer(container, ingredientDestination, maxAmount, player, false);
            if (emptiedSimulated != null)
            {
                if (emptiedSimulated.stackSize <= 0)
                {
                    tryEmptyContainer(container, ingredientDestination, maxAmount, player, true);
                    container.stackSize--;
                    return true;
                }
                else
                {
                    // check if we can give the itemStack to the inventory
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated, true);
                    if (remainder == null || player != null)
                    {
                        ItemStack emptiedReal = tryEmptyContainer(container, ingredientDestination, maxAmount, player, true);
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
        }

        return false;
    }

    /**
     * Fill a destination ingredient handler from a source ingredient handler.
     *
     * @param ingredientDestination The ingredient handler to be added.
     * @param ingredientSource      The ingredient handler to be removeed.
     * @param maxAmount        The largest amount of ingredient that should be transferred.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the ingredientStack that was transferred from the source to the destination. null on failure.
     */
    @Nullable
    public static IngredientStack tryIngredientTransfer(IIngredientHandler ingredientDestination, IIngredientHandler ingredientSource, int maxAmount, boolean doTransfer)
    {
        IngredientStack removeable = ingredientSource.remove(maxAmount, false);
        if (removeable != null && removeable.amount > 0)
        {
            int addableAmount = ingredientDestination.add(removeable, false);
            if (addableAmount > 0)
            {
                IngredientStack removed = ingredientSource.remove(addableAmount, doTransfer);
                if (removed != null)
                {
                    removed.amount = ingredientDestination.add(removed, doTransfer);
                    return removed;
                }
            }
        }
        return null;
    }

    /**
     * Helper method to get an IIngredientHandler for an itemStack.
     *
     * The itemStack passed in here WILL be modified, the IIngredientHandler acts on it directly.
     *
     * Note that the itemStack MUST have a stackSize of 1 if you want to add or remove it.
     * You can't add or remove a whole stack at once, if you do then ingredient is multiplied or destroyed.
     *
     * Vanilla buckets will be converted to universal buckets if they are enabled.
     *
     * Returns null if the itemStack passed in does not have an ingredient handler.
     */
    @Nullable
    public static IIngredientHandler getIngredientHandler(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return null;
        }

        // check for capability
        if (itemStack.hasCapability(CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY, null))
        {
            return itemStack.getCapability(CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY, null);
        }
        return null;
    }

    /**
     * Helper method to get the ingredient contained in an itemStack
     */
    @Nullable
    public static IngredientStack getIngredientContained(ItemStack container)
    {
        if (container != null)
        {
            container = container.copy();
            container.stackSize = 1;
            IIngredientHandler ingredientHandler = IngredientUtil.getIngredientHandler(container);

            if (ingredientHandler != null)
            {
                return ingredientHandler.remove(Integer.MAX_VALUE, false);
            }
        }
        return null;
    }

    /**
     * Helper method to get an IIngredientHandler for at a block position.
     *
     * Returns null if there is no valid ingredient handler.
     */
    @Nullable
    public static IIngredientHandler getIngredientHandler(World world, BlockPos blockPos, @Nullable EnumFacing side)
    {
        IBlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state))
        {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity.hasCapability(CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY, side))
            {
                return tileEntity.getCapability(CapabilityIngredientHandler.INGREDIENT_HANDLER_CAPABILITY, side);
            }
        }
        return null;
    }

    /**
     * Tries to place an ingredient in the world in block form.
     * Makes an ingredient removing sound when it is successful.
     *
     * Modeled after {@link net.minecraft.item.ItemBucket#tryPlaceContainedLiquid(EntityPlayer, World, BlockPos)}
     *
     * @param player     Player who places the ingredient. May be null for blocks like dispensers.
     * @param worldIn    World to place the ingredient in
     * @param ingredientStack The ingredientStack to place.
     * @param pos        The position in the world to place the ingredient block
     * @return true if successful
     */
    public static boolean tryPlaceIngredient(@Nullable EntityPlayer player, World worldIn, IngredientStack ingredientStack, BlockPos pos)
    {
        if (worldIn == null || ingredientStack == null || pos == null)
        {
            return false;
        }
        Ingredient ingredient = ingredientStack.getIngredient();

        if (ingredient == null || !ingredient.canBePlacedInWorld())
        {
            return false;
        }
        // check that we can place the ingredient at the destination
        IBlockState destBlockState = worldIn.getBlockState(pos);
        boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(worldIn, pos);

        if (!worldIn.isAirBlock(pos) && !isDestReplaceable)
        {
            return false; // Non-air, irreplacable block. We can't put ingredient here.
        }
        SoundEvent soundevent = ingredient.getRemovedSound(ingredientStack);
        worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        IBlockState ingredientBlockState = ingredient.getBlock().getDefaultState();
        worldIn.setBlockState(pos, ingredientBlockState, 11);
        return true;
    }

    /**
     * Attempts to pick up an ingredient in the world and put it in an empty container item.
     *
     * @param emptyContainer The empty container to add. Will not be modified.
     * @param playerIn       The player adding the container. Optional.
     * @param worldIn        The world the ingredient is in.
     * @param pos            The position of the ingredient in the world.
     * @param side           The side of the ingredient that is being removeed.
     * @return a added container if it was successful. returns null on failure.
     */
    @Nullable
    public static ItemStack tryPickUpIngredient(ItemStack emptyContainer, @Nullable EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side)
    {
        if (emptyContainer == null || worldIn == null || pos == null) {
            return null;
        }
        IIngredientHandler targetIngredientHandler = IngredientUtil.getIngredientHandler(worldIn, pos, side);

        if (targetIngredientHandler != null)
        {
            return IngredientUtil.tryFillContainer(emptyContainer, targetIngredientHandler, Integer.MAX_VALUE, playerIn, true);
        }
        return null;
    }

}
