/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.extensions;

import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.world.item.*;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;

// TODO review most of the methods in this "patch"
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
     * Called each tick while using an item.
     *
     * @param stack  The Item being used
     * @param player The Player using the item
     * @param count  The amount of time in tick the item has been used for
     *               continuously
     */
    default void onUsingTick(ItemStack stack, LivingEntity player, int count)
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
     * ItemStack sensitive version of getContainerItem. Returns a full ItemStack
     * instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    @SuppressWarnings("deprecation")
    default ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!hasContainerItem(itemStack))
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(self().getCraftingRemainingItem());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     *
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    @SuppressWarnings("deprecation")
    default boolean hasContainerItem(ItemStack stack)
    {
        return self().hasCraftingRemainingItem();
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
     * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param world     The world the entity is in
     * @return The normal lifespan in ticks.
     */
    default int getEntityLifespan(ItemStack itemStack, Level world)
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
     * function normally. Called when the item it placed in a world.
     *
     * @param world     The world object
     * @param location  The EntityItem object, useful for getting the position of
     *                  the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
    @Nullable
    default Entity createEntity(Level world, Entity location, ItemStack itemstack)
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
     * Gets a list of tabs that items belonging to this class can display on,
     * combined properly with getSubItems allows for a single item to span many
     * sub-items across many tabs.
     *
     * @return A list of all tabs that this item could possibly be one.
     */
    default java.util.Collection<CreativeModeTab> getCreativeTabs()
    {
        return java.util.Collections.singletonList(self().getItemCategory());
    }

    /**
     * Determines the base experience for a player when they remove this item from a
     * furnace slot. This number must be between 0 and 1 for it to be valid. This
     * number will be multiplied by the stack size to get the total experience.
     *
     * @param item The item stack the player is picking up.
     * @return The amount to award for each item.
     */
    default float getSmeltingExperience(ItemStack item)
    {
        return -1; // -1 will default to the old lookups.
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the
     * underlying block?
     *
     * @param world  The world
     * @param pos    Block position in world
     * @param player The Player that is wielding the item
     * @return
     */
    default boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.level.LevelReader world, BlockPos pos, Player player)
    {
        return false;
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    default void onArmorTick(ItemStack stack, Level world, Player player)
    {
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
     * {@link net.minecraft.entity.LivingEntity#getSlotForItemStack(ItemStack)}.</em>
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
     * Determines if the durability bar should be rendered for this item. Defaults
     * to vanilla stack.isDamaged behavior. But modders can use this for any data
     * they wish.
     *
     * @param stack The current Item Stack
     * @return True if it should render the 'durability' bar.
     */
    default boolean showDurabilityBar(ItemStack stack)
    {
        return stack.isDamaged();
    }

    /**
     * Queries the percentage of the 'Durability' bar that should be drawn.
     *
     * @param stack The current ItemStack
     * @return 0.0 for 100% (no damage / full bar), 1.0 for 0% (fully damaged /
     *         empty bar)
     */
    default double getDurabilityForDisplay(ItemStack stack)
    {
        return (double) stack.getDamageValue() / (double) stack.getMaxDamage();
    }

    /**
     * Returns the packed int RGB value used to render the durability bar in the
     * GUI. Defaults to a value based on the hue scaled based on
     * {@link #getDurabilityForDisplay}, but can be overriden.
     *
     * @param stack Stack to get durability from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    default int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return Mth.hsvToRgb(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
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
     * {@link #isDamageable()} is true.
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
     * See {@link net.minecraftforge.common.ToolActions} for a description of each stock action
     * @param stack The stack being used
     * @param toolAction The action being queried
     * @return True if the stack can perform the action
     */
    default boolean canPerformAction(ItemStack stack, ToolAction toolAction)
    {
        return false;
    }

    /**
     * ItemStack sensitive version of {@link #canHarvestBlock(IBlockState)}
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
     * is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
     *
     * @param stack The ItemStack
     * @return The maximum number this item can be stacked to
     */
    @SuppressWarnings("deprecation")
    default int getItemStackLimit(ItemStack stack)
    {
        return self().getMaxStackSize();
    }

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @param stack The ItemStack
     * @return the item echantability value
     */
    default int getItemEnchantability(ItemStack stack)
    {
        return self().getEnchantmentValue();
    }

    /**
     * Checks whether an item can be enchanted with a certain enchantment. This
     * applies specifically to enchanting an item in the enchanting table and is
     * called when retrieving the list of possible enchantments for an item.
     * Enchantments may additionally (or exclusively) be doing their own checks in
     * {@link net.minecraft.enchantment.Enchantment#canApplyAtEnchantingTable(ItemStack)};
     * check the individual implementation for reference. By default this will check
     * if the enchantment type is valid for this item type.
     *
     * @param stack       the item stack to be enchanted
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    default boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment)
    {
        return enchantment.category.canEnchant(stack.getItem());
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
        return !(newStack.getItem() == oldStack.getItem() && ItemStack.tagMatches(newStack, oldStack)
                && (newStack.isDamageableItem() || newStack.getDamageValue() == oldStack.getDamageValue()));
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
        return ItemStack.isSameIgnoreDurability(oldStack, newStack);
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
     *         associated mod and {@link #getRegistryName()} would return null.
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
        return null;
    }

    //TODO, properties dont exist anymore
//    default ImmutableMap<String, ITimeValue> getAnimationParameters(final ItemStack stack, final World world, final LivingEntity entity)
//    {
//        com.google.common.collect.ImmutableMap.Builder<String, ITimeValue> builder = ImmutableMap.builder();
//        getItem().properties.forEach((k,v) -> builder.put(k.toString(), input -> v.call(stack, world, entity)));
//        return builder.build();
//    }

    /**
     * Can this Item disable a shield
     *
     * @param stack    The ItemStack
     * @param shield   The shield in question
     * @param entity   The LivingEntity holding the shield
     * @param attacker The LivingEntity holding the ItemStack
     * @retrun True if this ItemStack can disable the shield in question.
     */
    default boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker)
    {
        return this instanceof AxeItem;
    }

    /**
     * Is this Item a shield
     *
     * @param stack  The ItemStack
     * @param entity The Entity holding the ItemStack
     * @return True if the ItemStack is considered a shield
     */
    default boolean isShield(ItemStack stack, @Nullable LivingEntity entity)
    {
        return stack.getItem() == Items.SHIELD;
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
     * Called every tick from {@link EntityHorse#onUpdate()} on the item in the
     * armor slot.
     *
     * @param stack the armor itemstack
     * @param world the world the horse is in
     * @param horse the horse wearing this armor
     */
    default void onHorseArmorTick(ItemStack stack, Level world, Mob horse)
    {
    }

    /**
     * Retrieves a list of tags names this is known to be associated with.
     * This should be used in favor of TagCollection.getOwningTags, as this caches the result and automatically updates when the TagCollection changes.
     */
    Set<ResourceLocation> getTags();

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
     * @param statck the stack held by the player.
     * @param player the performing the attack the attack.
     * @param target the entity targeted by the attack.
     * @return the bounding box.
     */
    @Nonnull
    default AABB getSweepHitBox(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull Entity target)
    {
        return target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D);
    }
}
