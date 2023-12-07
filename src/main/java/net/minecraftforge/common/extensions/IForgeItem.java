/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.items.wrapper.ShulkerItemStackInvWrapper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO systemic review of all extension functions. lots of unused -C
public interface IForgeItem
{
    private Item self()
    {
        return (Item) this;
    }

    /**
     * ItemStack sensitive version of getItemAttributeModifiers
     */
    @SuppressWarnings("deprecation")
    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
    {
        return self().getDefaultAttributeModifiers(slot);
    }

    /**
     * Called when a player drops the item into the world, returning false from this
     * will prevent the item from being removed from the players inventory and
     * spawning in the world
     *
     * @param player The player that dropped the item
     * @param item   The item stack, before the item is removed.
     */
    default boolean onDroppedByPlayer(ItemStack item, Player player)
    {
        return true;
    }

    /**
     * Allow the item one last chance to modify its name used for the tool highlight
     * useful for adding something extra that can't be removed by a user in the
     * displayed name, such as a mode of operation.
     *
     * @param item        the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in
     *                    this method.
     */
    default Component getHighlightTip(ItemStack item, Component displayName)
    {
        return displayName;
    }

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    default InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        return InteractionResult.PASS;
    }

    /**
     * Called by Piglins when checking to see if they will give an item or something in exchange for this item.
     *
     * @return True if this item can be used as "currency" by piglins
     */
    default boolean isPiglinCurrency(ItemStack stack)
    {
        return stack.getItem() == PiglinAi.BARTERING_ITEM;
    }

    /**
     * Called by Piglins to check if a given item prevents hostility on sight. If this is true the Piglins will be neutral to the entity wearing this item, and will not
     * attack on sight. Note: This does not prevent Piglins from becoming hostile due to other actions, nor does it make Piglins that are already hostile stop being so.
     *
     * @param wearer The entity wearing this ItemStack
     *
     * @return True if piglins are neutral to players wearing this item in an armor slot
     */
    default boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer)
    {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getMaterial() == ArmorMaterials.GOLD;
    }

    /**
     * Called by CraftingManager to determine if an item is reparable.
     *
     * @return True if reparable
     */
    boolean isRepairable(ItemStack stack);

    /**
    * Determines the amount of durability the mending enchantment
    * will repair, on average, per point of experience.
    */
    default float getXpRepairRatio(ItemStack stack)
    {
        return 2f;
    }

    /**
     * Override this method to change the NBT data being sent to the client. You
     * should ONLY override this when you have no other choice, as this might change
     * behavior client side!
     *
     * Note that this will sometimes be applied multiple times, the following MUST
     * be supported:
     *   Item item = stack.getItem();
     *   NBTTagCompound nbtShare1 = item.getNBTShareTag(stack);
     *   stack.setTagCompound(nbtShare1);
     *   NBTTagCompound nbtShare2 = item.getNBTShareTag(stack);
     *   assert nbtShare1.equals(nbtShare2);
     *
     * @param stack The stack to send the NBT tag for
     * @return The NBT tag
     */
    @Nullable
    default CompoundTag getShareTag(ItemStack stack)
    {
        return stack.getTag();
    }

    /**
     * Override this method to decide what to do with the NBT data received from
     * getNBTShareTag().
     *
     * @param stack The stack that received NBT
     * @param nbt   Received NBT, can be null
     */
    default void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)
    {
        stack.setTag(nbt);
    }

    /**
     * Called before a block is broken. Return true to prevent default block
     * harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param pos       Block's position in world
     * @param player    The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player)
    {
        return false;
    }

    /**
     * Called when an entity stops using an item for any reason, notably when selecting another item without releasing or finishing.
     * This method is called in addition to any other hooks called when an item is finished using; when another hook is also called it will be called before this method.
     *
     * Note that if you break an item while using it (that is, it becomes empty without swapping the stack instance), this hook may not be called on the serverside as you are
     * technically still using the empty item (thus this hook is called on air instead). It is necessary to call {@link LivingEntity#stopUsingItem()} as part of your
     * {@link ItemStack#hurtAndBreak(int, LivingEntity, Consumer)} callback to prevent this issue.
     *
     * For most uses, you likely want one of the following:
     * <ul>
     *   <li>{@link Item#finishUsingItem(ItemStack, Level, LivingEntity)} for when the player releases and enough ticks have passed
     *   <li>{@link Item#releaseUsing(ItemStack, Level, LivingEntity, int)} (ItemStack, Level, LivingEntity)} for when the player releases but the full timer has not passed
     * </ul>
     *
     * @param stack  The Item being used
     * @param entity The entity using the item, typically a player
     * @param count  The amount of time in tick the item has been used for continuously
     */
    default void onStopUsing(ItemStack stack, LivingEntity entity, int count)
    {
    }

    /**
     * Called when the player Left Clicks (attacks) an entity. Processed before
     * damage is done, if return value is true further processing is canceled and
     * the entity is not attacked.
     *
     * @param stack  The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    default boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
    {
        return false;
    }

    /**
     * ItemStack sensitive version of {@link Item#getCraftingRemainingItem()}.
     * Returns a full ItemStack instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    @SuppressWarnings("deprecation")
    default ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        if (!hasCraftingRemainingItem(itemStack))
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(self().getCraftingRemainingItem());
    }

    /**
     * ItemStack sensitive version of {@link Item#hasCraftingRemainingItem()}.
     *
     * @param stack The current item stack
     * @return True if this item has a crafting remaining item
     */
    @SuppressWarnings("deprecation")
    default boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return self().hasCraftingRemainingItem();
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
     * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param level     The level the entity is in
     * @return The normal lifespan in ticks.
     */
    default int getEntityLifespan(ItemStack itemStack, Level level)
    {
        return 6000;
    }

    /**
     * Determines if this Item has a special entity for when they are in the world.
     * Is called when a EntityItem is spawned in the world, if true and
     * Item#createCustomEntity returns non null, the EntityItem will be destroyed
     * and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true,
     *         Item#createCustomEntity will be called
     */
    default boolean hasCustomEntity(ItemStack stack)
    {
        return false;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to
     * function normally. Called when the item it placed in a level.
     *
     * @param level     The level object
     * @param location  The EntityItem object, useful for getting the position of
     *                  the entity
     * @param stack The current item stack
     * @return A new Entity object to spawn or null
     */
    @Nullable
    default Entity createEntity(Level level, Entity location, ItemStack stack)
    {
        return null;
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing
     * for cleaner control over the update of the item without having to write a
     * subclass.
     *
     * @param entity The entity Item
     * @return Return true to skip any further update code.
     */
    default boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        return false;
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the
     * underlying block?
     *
     * @param level  The level
     * @param pos    Block position in level
     * @param player The Player that is wielding the item
     */
    default boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.level.LevelReader level, BlockPos pos, Player player)
    {
        return false;
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    @Deprecated(forRemoval = true, since = "1.20.1") // Use onInventoryTick
    default void onArmorTick(ItemStack stack, Level level, Player player)
    {
    }

    /**
     * Called to tick this items in a players inventory, the indexes are the global slot index.
     */
    default void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex)
    {
    	// For compatibility reasons we have to use non-local index values, I think this is a vanilla bug but lets maintain compatibility
    	int vanillaIndex = slotIndex;
    	if (slotIndex >= 36) {
    		vanillaIndex -= 36;
    		if (vanillaIndex >= 4)
    			vanillaIndex -= 4;
			else
				onArmorTick(stack, level, player);
    	}
		stack.inventoryTick(level, player, vanillaIndex, selectedIndex == vanillaIndex);
    }

    /**
     * Determines if the specific ItemStack can be placed in the specified armor
     * slot, for the entity.
     *
     * @param stack     The ItemStack
     * @param armorType Armor slot to be verified.
     * @param entity    The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    default boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity)
    {
        return Mob.getEquipmentSlotForItem(stack) == armorType;
    }

    /**
     * Override this to set a non-default armor slot for an ItemStack, but <em>do
     * not use this to get the armor slot of said stack; for that, use
     * {@link LivingEntity#getEquipmentSlotForItem(ItemStack)}..</em>
     *
     * @param stack the ItemStack
     * @return the armor slot of the ItemStack, or {@code null} to let the default
     *         vanilla logic as per {@code LivingEntity.getSlotForItemStack(stack)}
     *         decide
     */
    @Nullable
    default EquipmentSlot getEquipmentSlot(ItemStack stack)
    {
        return null;
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book  The book
     * @return if the enchantment is allowed
     */
    default boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return true;
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that
     * should be use for the currently equipped item. This will only be called on
     * instances of ItemArmor.
     *
     * Returning null from this function will use the default value.
     *
     * @param stack  ItemStack for the equipped armor
     * @param entity The entity wearing the armor
     * @param slot   The slot the armor is in
     * @param type   The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    @Nullable
    default String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
    {
        return null;
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *
     * @param entity The entity swinging the item.
     * @return True to cancel any further processing by EntityLiving
     */
    default boolean onEntitySwing(ItemStack stack, LivingEntity entity)
    {
        return false;
    }

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the Damage
     * entry in the stack NBT, but can be overridden here for other sources.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    default int getDamage(ItemStack stack)
    {
        return !stack.hasTag() ? 0 : stack.getTag().getInt("Damage");
    }

    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in
     * this item, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    @SuppressWarnings("deprecation")
    default int getMaxDamage(ItemStack stack)
    {
        return self().getMaxDamage();
    }

    /**
     * Return if this itemstack is damaged. Note only called if
     * {@link ItemStack#isDamageableItem()} is true.
     *
     * @param stack the stack
     * @return if the stack is damaged
     */
    default boolean isDamaged(ItemStack stack)
    {
        return stack.getDamageValue() > 0;
    }

    /**
     * Set the damage for this itemstack. Note, this method is responsible for zero
     * checking.
     *
     * @param stack  the stack
     * @param damage the new damage value
     */
    default void setDamage(ItemStack stack, int damage)
    {
        stack.getOrCreateTag().putInt("Damage", Math.max(0, damage));
    }

    /**
     * Queries if an item can perform the given action.
     * See {@link ToolActions} for a description of each stock action
     * @param stack The stack being used
     * @param toolAction The action being queried
     * @return True if the stack can perform the action
     */
    default boolean canPerformAction(ItemStack stack, ToolAction toolAction)
    {
        return false;
    }

    /**
     * ItemStack sensitive version of {@link Item#isCorrectToolForDrops(BlockState)}
     *
     * @param stack The itemstack used to harvest the block
     * @param state The block trying to harvest
     * @return true if the stack can harvest the block
     */
    default boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
    {
        return self().isCorrectToolForDrops(state);
    }

    /**
     * Gets the maximum number of items that this stack should be able to hold. This
     * is a ItemStack (and thus NBT) sensitive version of {@link Item#getMaxStackSize()}.
     *
     * @param stack The ItemStack
     * @return The maximum size this item can be stacked to
     */
    @SuppressWarnings("deprecation")
    default int getMaxStackSize(ItemStack stack)
    {
        return self().getMaxStackSize();
    }

    /**
     * ItemStack sensitive version of {@link Item#getEnchantmentValue()}.
     *
     * @param stack The ItemStack
     * @return the enchantment value
     */
    default int getEnchantmentValue(ItemStack stack)
    {
        return self().getEnchantmentValue();
    }

    /**
     * Checks whether an item can be enchanted with a certain enchantment. This
     * applies specifically to enchanting an item in the enchanting table and is
     * called when retrieving the list of possible enchantments for an item.
     * Enchantments may additionally (or exclusively) be doing their own checks in
     * {@link Enchantment#canApplyAtEnchantingTable(ItemStack)};
     * check the individual implementation for reference. By default this will check
     * if the enchantment type is valid for this item type.
     *
     * @param stack       the item stack to be enchanted
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.category.canEnchant(stack.getItem());
    }

    /**
     * Gets the level of the enchantment currently present on the stack. By default, returns the enchantment level present in NBT.
     * Most enchantment implementations rely upon this method.
     * For consistency, results of this method should be the same as getting the enchantment from {@link #getAllEnchantments(ItemStack)}
     *
     * @param stack        the item stack being checked
     * @param enchantment  the enchantment being checked for
     * @return  Level of the enchantment, or 0 if not present
     * @see #getAllEnchantments(ItemStack)
     */
    default int getEnchantmentLevel(ItemStack stack, Enchantment enchantment)
    {
        return EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
    }

    /**
     * Gets a map of all enchantments present on the stack. By default, returns the enchantments present in NBT.
     * Used in several places in code including armor enchantment hooks.
     * For consistency, any enchantments in the returned map should include the same level in {@link #getEnchantmentLevel(ItemStack, Enchantment)}
     *
     * @param stack        the item stack being checked
     * @return  Map of all enchantments on the stack, empty if no enchantments are present
     * @see #getEnchantmentLevel(ItemStack, Enchantment)
     */
    default Map<Enchantment, Integer> getAllEnchantments(ItemStack stack)
    {
        return EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
    }

    /**
     * Determine if the player switching between these two item stacks
     *
     * @param oldStack    The old stack that was equipped
     * @param newStack    The new stack
     * @param slotChanged If the current equipped slot was changed, Vanilla does not
     *                    play the animation if you switch between two slots that
     *                    hold the exact same item.
     * @return True to play the item change animation
     */
    default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return !oldStack.equals(newStack); // !ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    /**
     * Called when the player is mining a block and the item in his hand changes.
     * Allows to not reset blockbreaking if only NBT or similar changes.
     *
     * @param oldStack The old stack that was used for mining. Item in players main
     *                 hand
     * @param newStack The new stack
     * @return True to reset block break progress
     */
    default boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
    {
        // Fix MC-176559 mending resets mining progress / breaking animation
        if (!newStack.is(oldStack.getItem()))
            return true;

        if (!newStack.isDamageableItem() || !oldStack.isDamageableItem())
            return !ItemStack.isSameItemSameTags(newStack, oldStack);

        CompoundTag newTag = newStack.getTag();
        CompoundTag oldTag = oldStack.getTag();

        if (newTag == null || oldTag == null)
            return !(newTag == null && oldTag == null);

        Set<String> newKeys = new HashSet<>(newTag.getAllKeys());
        Set<String> oldKeys = new HashSet<>(oldTag.getAllKeys());

        newKeys.remove(ItemStack.TAG_DAMAGE);
        oldKeys.remove(ItemStack.TAG_DAMAGE);

        if (!newKeys.equals(oldKeys))
            return true;

        return !newKeys.stream().allMatch(key -> Objects.equals(newTag.get(key), oldTag.get(key)));
        // return !(newStack.is(oldStack.getItem()) && ItemStack.tagMatches(newStack, oldStack)
        //         && (newStack.isDamageableItem() || newStack.getDamageValue() == oldStack.getDamageValue()));
    }

    /**
     * Called while an item is in 'active' use to determine if usage should
     * continue. Allows items to continue being used while sustaining damage, for
     * example.
     *
     * @param oldStack the previous 'active' stack
     * @param newStack the stack currently in the active hand
     * @return true to set the new stack to active and continue using it
     */
    default boolean canContinueUsing(ItemStack oldStack, ItemStack newStack)
    {
        if (oldStack == newStack) {
            return true;
        } else {
            return !oldStack.isEmpty() && !newStack.isEmpty() && ItemStack.isSameItem(newStack, oldStack);
        }
    }

    /**
     * Called to get the Mod ID of the mod that *created* the ItemStack, instead of
     * the real Mod ID that *registered* it.
     *
     * For example the Forge Universal Bucket creates a subitem for each modded
     * fluid, and it returns the modded fluid's Mod ID here.
     *
     * Mods that register subitems for other mods can override this. Informational
     * mods can call it to show the mod that created the item.
     *
     * @param itemStack the ItemStack to check
     * @return the Mod ID for the ItemStack, or null when there is no specially
     *         associated mod and {@link IForgeRegistry#getKey(Object)} would return null.
     */
    @Nullable
    default String getCreatorModId(ItemStack itemStack)
    {
        return net.minecraftforge.common.ForgeHooks.getDefaultCreatorModId(itemStack);
    }

    /**
     * Called from ItemStack.setItem, will hold extra data for the life of this
     * ItemStack. Can be retrieved from stack.getCapabilities() The NBT can be null
     * if this is not called from readNBT or if the item the stack is changing FROM
     * is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem()
     * to see the OLD item. Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt   NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold
     *         capabilities for the life of this item.
     */
    @Nullable
    default net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        var ret = ShulkerItemStackInvWrapper.createDefaultProvider(stack);
        return ret;
    }

    /**
     * Can this Item disable a shield
     *
     * @param stack    The ItemStack
     * @param shield   The shield in question
     * @param entity   The LivingEntity holding the shield
     * @param attacker The LivingEntity holding the ItemStack
     * @return True if this ItemStack can disable the shield in question.
     */
    default boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker)
    {
        return this instanceof AxeItem;
    }

    /**
     * @return the fuel burn time for this itemStack in a furnace. Return 0 to make
     *         it not act as a fuel. Return -1 to let the default vanilla logic
     *         decide.
     */
    default int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
    {
        return -1;
    }

    /**
     * Called every tick from {@code Horse#playGallopSound(SoundEvent)} on the item in the
     * armor slot.
     *
     * @param stack the armor itemstack
     * @param level the level the horse is in
     * @param horse the horse wearing this armor
     */
    default void onHorseArmorTick(ItemStack stack, Level level, Mob horse)
    {
    }

    /**
     * Reduce the durability of this item by the amount given.
     * This can be used to e.g. consume power from NBT before durability.
     *
     * @param stack The itemstack to damage
     * @param amount The amount to damage
     * @param entity The entity damaging the item
     * @param onBroken The on-broken callback from vanilla
     * @return The amount of damage to pass to the vanilla logic
     */
    default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return amount;
    }

    /**
     * Called when an item entity for this stack is destroyed. Note: The {@link ItemStack} can be retrieved from the item entity.
     *
     * @param itemEntity   The item entity that was destroyed.
     * @param damageSource Damage source that caused the item entity to "die".
     */
    default void onDestroyed(ItemEntity itemEntity, DamageSource damageSource)
    {
        self().onDestroyed(itemEntity);
    }

    /**
     * Whether this Item can be used to hide player head for enderman.
     *
     * @param stack the ItemStack
     * @param player The player watching the enderman
     * @param endermanEntity The enderman that the player look
     * @return true if this Item can be used to hide player head for enderman
     */
    default boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity)
    {
        return stack.getItem() == Blocks.CARVED_PUMPKIN.asItem();
    }

    /**
     * Used to determine if the player can use Elytra flight.
     * This is called Client and Server side.
     *
     * @param stack The ItemStack in the Chest slot of the entity.
     * @param entity The entity trying to fly.
     * @return True if the entity can use Elytra flight.
     */
    default boolean canElytraFly(ItemStack stack, LivingEntity entity)
    {
        return false;
    }

    /**
     * Used to determine if the player can continue Elytra flight,
     * this is called each tick, and can be used to apply ItemStack damage,
     * consume Energy, or what have you.
     * For example the Vanilla implementation of this, applies damage to the
     * ItemStack every 20 ticks.
     *
     * @param stack       ItemStack in the Chest slot of the entity.
     * @param entity      The entity currently in Elytra flight.
     * @param flightTicks The number of ticks the entity has been Elytra flying for.
     * @return True if the entity should continue Elytra flight or False to stop.
     */
    default boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks)
    {
        return false;
    }

    /**
     * Called by the powdered snow block to check if a living entity wearing this can walk on the snow, granting the same behavior as leather boots.
     * Only affects items worn in the boots slot.
     *
     * @param stack  Stack instance
     * @param wearer The entity wearing this ItemStack
     *
     * @return True if the entity can walk on powdered snow
     */
    default boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer)
    {
        return stack.is(Items.LEATHER_BOOTS);
    }

    /**
     * Used to test if this item can be damaged, but with the ItemStack in question.
     * Please note that in some cases no ItemStack is available, so the stack-less method will be used.
     *
     * @param stack       ItemStack in the Chest slot of the entity.
     */
    default boolean isDamageable(ItemStack stack)
    {
        return self().canBeDepleted();
    }

    /**
     * Get a bounding box ({@link AABB}) of a sweep attack.
     *
     * @param stack the stack held by the player.
     * @param player the performing the attack the attack.
     * @param target the entity targeted by the attack.
     * @return the bounding box.
     */
    @NotNull
    default AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target)
    {
        return target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D);
    }

    /**
     * Get the tooltip parts that should be hidden by default on the given stack if the {@code HideFlags} tag is not set.
     * @see ItemStack.TooltipPart
     * @param stack the stack
     * @return the default hide flags
     */
    default int getDefaultTooltipHideFlags(@NotNull ItemStack stack)
    {
        return 0;
    }

    /**
     * Get the food properties for this item.
     * Use this instead of the {@link Item#getFoodProperties()} method, for ItemStack sensitivity.
     *
     * The @Nullable annotation was only added, due to the default method, also being @Nullable.
     * Use this with a grain of salt, as if you return null here and true at {@link Item#isEdible()}, NPEs will occur!
     *
     * @param stack The ItemStack the entity wants to eat.
     * @param entity The entity which wants to eat the food. Be aware that this can be null!
     * @return The current FoodProperties for the item.
     */
    @Nullable // read javadoc to find a potential problem
    default FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity)
    {
        return self().getFoodProperties();
    }

    /**
     * Whether the given ItemStack should be excluded (if possible) when selecting the target hotbar slot of a "pick" action.
     * By default, this returns true for enchanted stacks.
     *
     * @see Inventory#getSuitableHotbarSlot()
     * @param player the player performing the picking
     * @param inventorySlot the inventory slot of the item being up for replacement
     * @return true to leave this stack in the hotbar if possible
     */
    default boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot)
    {
        return stack.isEnchanted();
    }

    /**
     * {@return true if the given ItemStack can be put into a grindstone to be repaired and/or stripped of its enchantments}
     */
    default boolean canGrindstoneRepair(ItemStack stack)
    {
        return false;
    }
}
