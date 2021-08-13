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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/*
 * Extension added to ItemStack that bounces to ItemSack sensitive Item methods. Typically this is just for convince.
 */
public interface IForgeItemStack extends ICapabilitySerializable<CompoundTag>
{
    // Helpers for accessing Item data
    private ItemStack self()
    {
        return (ItemStack)this;
    }

    /**
     * ItemStack sensitive version of getContainerItem. Returns a full ItemStack
     * instance of the result.
     *
     * @return The resulting ItemStack
     */
    default ItemStack getContainerItem()
    {
        return self().getItem().getContainerItem(self());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     *
     * @return True if this item has a 'container'
     */
    default boolean hasContainerItem()
    {
        return self().getItem().hasContainerItem(self());
    }

    /**
     * @return the fuel burn time for this itemStack in a furnace. Return 0 to make
     *         it not act as a fuel. Return -1 to let the default vanilla logic
     *         decide.
     */
    default int getBurnTime(@Nullable RecipeType<?> recipeType)
    {
        return self().getItem().getBurnTime(self(), recipeType);
    }

    default InteractionResult onItemUseFirst(UseOnContext context)
    {
       Player entityplayer = context.getPlayer();
       BlockPos blockpos = context.getClickedPos();
       BlockInWorld blockworldstate = new BlockInWorld(context.getLevel(), blockpos, false);
       if (entityplayer != null && !entityplayer.getAbilities().mayBuild && !self().hasAdventureModePlaceTagForBlock(context.getLevel().getTagManager(), blockworldstate)) {
          return InteractionResult.PASS;
       } else {
          Item item = self().getItem();
          InteractionResult enumactionresult = item.onItemUseFirst(self(), context);
          if (entityplayer != null && enumactionresult == InteractionResult.SUCCESS) {
             entityplayer.awardStat(Stats.ITEM_USED.get(item));
          }

          return enumactionresult;
       }
    }

    default CompoundTag serializeNBT()
    {
        CompoundTag ret = new CompoundTag();
        self().save(ret);
        return ret;
    }

    /**
     * Queries if an item can perform the given action.
     * See {@link net.minecraftforge.common.ToolActions} for a description of each stock action
     * @param stack The stack being used
     * @param toolAction The action being queried
     * @return True if the stack can perform the action
     */
    default boolean canPerformAction(ToolAction toolAction)
    {
        return self().getItem().canPerformAction(self(), toolAction);
    }

    /**
     * Called before a block is broken. Return true to prevent default block
     * harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param pos       Block's position in world
     * @param player    The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    default boolean onBlockStartBreak(BlockPos pos, Player player)
    {
        return !self().isEmpty() && self().getItem().onBlockStartBreak(self(), pos, player);
    }

    /**
     * Called when the player is mining a block and the item in his hand changes.
     * Allows to not reset blockbreaking if only NBT or similar changes.
     *
     * @param newStack The new stack
     * @return True to reset block break progress
     */
    default boolean shouldCauseBlockBreakReset(ItemStack newStack)
    {
        return self().getItem().shouldCauseBlockBreakReset(self(), newStack);
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
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    default boolean canApplyAtEnchantingTable(Enchantment enchantment)
    {
        return self().getItem().canApplyAtEnchantingTable(self(), enchantment);
    }

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @return the item echantability value
     */
    default int getItemEnchantability()
    {
        return self().getItem().getItemEnchantability(self());
    }

    /**
     * Override this to set a non-default armor slot for an ItemStack, but <em>do
     * not use this to get the armor slot of said stack; for that, use
     * {@link net.minecraft.entity.LivingEntity#getSlotForItemStack(ItemStack)}.</em>
     *
     * @return the armor slot of the ItemStack, or {@code null} to let the default
     *         vanilla logic as per {@code LivingEntity.getSlotForItemStack(stack)}
     *         decide
     */
    @Nullable
    default EquipmentSlot getEquipmentSlot()
    {
        return self().getItem().getEquipmentSlot(self());
    }

    /**
     * Can this Item disable a shield
     *
     * @param shield   The shield in question
     * @param entity   The LivingEntity holding the shield
     * @param attacker The LivingEntity holding the ItemStack
     * @retrun True if this ItemStack can disable the shield in question.
     */
    default boolean canDisableShield(ItemStack shield, LivingEntity entity, LivingEntity attacker)
    {
        return self().getItem().canDisableShield(self(), shield, entity, attacker);
    }

    /**
     * Is this Item a shield
     *
     * @param entity The Entity holding the ItemStack
     * @return True if the ItemStack is considered a shield
     */
    default boolean isShield(@Nullable LivingEntity entity)
    {
        return self().getItem().isShield(self(), entity);
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *
     * @param entity The entity swinging the item.
     * @return True to cancel any further processing by EntityLiving
     */
    default boolean onEntitySwing(LivingEntity entity)
    {
        return self().getItem().onEntitySwing(self(), entity);
    }

    /**
     * Called each tick while using an item.
     *
     * @param player The Player using the item
     * @param count  The amount of time in tick the item has been used for
     *               continuously
     */
    default void onUsingTick(LivingEntity player, int count)
    {
        self().getItem().onUsingTick(self(), player, count);
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
     * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param world     The world the entity is in
     * @return The normal lifespan in ticks.
     */
    default int getEntityLifespan(Level world)
    {
        return self().getItem().getEntityLifespan(self(), world);
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing
     * for cleaner control over the update of the item without having to write a
     * subclass.
     *
     * @param entity The entity Item
     * @return Return true to skip any further update code.
     */
    default boolean onEntityItemUpdate(ItemEntity entity)
    {
        return self().getItem().onEntityItemUpdate(self(), entity);
    }

    /**
    * Determines the amount of durability the mending enchantment
    * will repair, on average, per point of experience.
    */
    default float getXpRepairRatio()
    {
        return self().getItem().getXpRepairRatio(self());
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    default void onArmorTick(Level world, Player player)
    {
        self().getItem().onArmorTick(self(), world, player);
    }

    /**
     * Called every tick from {@link HorseEntity#onUpdate()} on the item in the
     * armor slot.
     *
     * @param world the world the horse is in
     * @param horse the horse wearing this armor
     */
    default void onHorseArmorTick(Level world, Mob horse)
    {
        self().getItem().onHorseArmorTick(self(), world, horse);
    }

    /**
     * Determines if the specific ItemStack can be placed in the specified armor
     * slot, for the entity.
     *
     * @param armorType Armor slot to be verified.
     * @param entity    The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    default boolean canEquip(EquipmentSlot armorType, Entity entity)
    {
        return self().getItem().canEquip(self(), armorType, entity);
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param book  The book
     * @return if the enchantment is allowed
     */
    default boolean isBookEnchantable(ItemStack book)
    {
        return self().getItem().isBookEnchantable(self(), book);
    }


    /**
     * Called when a player drops the item into the world, returning false from this
     * will prevent the item from being removed from the players inventory and
     * spawning in the world
     *
     * @param player The player that dropped the item
     */
    default boolean onDroppedByPlayer(Player player)
    {
        return self().getItem().onDroppedByPlayer(self(), player);
    }

    /**
     * Allow the item one last chance to modify its name used for the tool highlight
     * useful for adding something extra that can't be removed by a user in the
     * displayed name, such as a mode of operation.
     *
     * @param displayName the name that will be displayed unless it is changed in
     *                    this method.
     */
    default Component getHighlightTip(Component displayName)
    {
        return self().getItem().getHighlightTip(self(), displayName);
    }

    /**
     * Get the NBT data to be sent to the client. The Item can control what data is kept in the tag.
     *
     * Note that this will sometimes be applied multiple times, the following MUST
     * be supported:
     *   Item item = stack.getItem();
     *   NBTTagCompound nbtShare1 = item.getNBTShareTag(stack);
     *   stack.setTagCompound(nbtShare1);
     *   NBTTagCompound nbtShare2 = item.getNBTShareTag(stack);
     *   assert nbtShare1.equals(nbtShare2);
     *
     * @return The NBT tag
     */
    @Nullable
    default CompoundTag getShareTag()
    {
        return self().getItem().getShareTag(self());
    }

    /**
     * Override this method to decide what to do with the NBT data received from
     * getNBTShareTag().
     *
     * @param nbt   Received NBT, can be null
     */
    default void readShareTag(@Nullable CompoundTag nbt)
    {
        self().getItem().readShareTag(self(), nbt);
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
     *
     * @param world The world
     * @param pos Block position in world
     * @param player The Player that is wielding the item
     * @return
     */
    default boolean doesSneakBypassUse(net.minecraft.world.level.LevelReader world, BlockPos pos, Player player)
    {
        return self().isEmpty() || self().getItem().doesSneakBypassUse(self(), world, pos, player);
    }

    /**
     * Modeled after ItemStack.areItemStackTagsEqual
     * Uses Item.getNBTShareTag for comparison instead of NBT and capabilities.
     * Only used for comparing itemStacks that were transferred from server to client using Item.getNBTShareTag.
     */
    default boolean areShareTagsEqual(ItemStack other)
    {
        CompoundTag shareTagA = self().getShareTag();
        CompoundTag shareTagB = other.getShareTag();
        if (shareTagA == null)
            return shareTagB == null;
        else
            return shareTagB != null && shareTagA.equals(shareTagB);
    }

    /**
     * Determines if the ItemStack is equal to the other item stack, including Item, Count, and NBT.
     *
     * @param other The other stack
     * @param limitTags True to use shareTag False to use full NBT tag
     * @return true if equals
     */
    default boolean equals(ItemStack other, boolean limitTags)
    {
        if (self().isEmpty())
            return other.isEmpty();
        else
            return !other.isEmpty() && self().getCount() == other.getCount() && self().getItem() == other.getItem() &&
            (limitTags ? self().areShareTagsEqual(other) : ItemStack.tagMatches(self(), other));
    }

    /**
     * Determines if a item is reparable, used by Repair recipes and Grindstone.
     *
     * @return True if reparable
     */
    default boolean isRepairable()
    {
        return self().getItem().isRepairable(self());
    }

    /**
     * Called by Piglins when checking to see if they will give an item or something in exchange for this item.
     *
     * @return True if this item can be used as "currency" by piglins
     */
    default boolean isPiglinCurrency()
    {
        return self().getItem().isPiglinCurrency(self());
    }

    /**
     * Called by Piglins to check if a given item prevents hostility on sight. If this is true the Piglins will be neutral to the entity wearing this item, and will not
     * attack on sight. Note: This does not prevent Piglins from becoming hostile due to other actions, nor does it make Piglins that are already hostile stop being so.
     *
     * @param wearer The entity wearing this ItemStack
     *
     * @return True if piglins are neutral to players wearing this item in an armor slot
     */
    default boolean makesPiglinsNeutral(LivingEntity wearer)
    {
        return self().getItem().makesPiglinsNeutral(self(), wearer);
    }

    /**
     * Whether this Item can be used to hide player head for enderman.
     *
     * @param player The player watching the enderman
     * @param endermanEntity The enderman that the player look
     * @return true if this Item can be used.
     */
    default boolean isEnderMask(Player player, EnderMan endermanEntity)
    {
        return self().getItem().isEnderMask(self(), player, endermanEntity);
    }

    /**
     * Used to determine if the player can use Elytra flight.
     * This is called Client and Server side.
     *
     * @param entity The entity trying to fly.
     * @return True if the entity can use Elytra flight.
     */
    default boolean canElytraFly(LivingEntity entity)
    {
        return self().getItem().canElytraFly(self(), entity);
    }

    /**
     * Used to determine if the player can continue Elytra flight,
     * this is called each tick, and can be used to apply ItemStack damage,
     * consume Energy, or what have you.
     * For example the Vanilla implementation of this, applies damage to the
     * ItemStack every 20 ticks.
     *
     * @param entity      The entity currently in Elytra flight.
     * @param flightTicks The number of ticks the entity has been Elytra flying for.
     * @return True if the entity should continue Elytra flight or False to stop.
     */
    default boolean elytraFlightTick(LivingEntity entity, int flightTicks)
    {
        return self().getItem().elytraFlightTick(self(), entity, flightTicks);
    }
    
    /**
     * Get a bounding box ({@link AABB}) of a sweep attack.
     * 
     * @param player the performing the attack the attack.
     * @param target the entity targeted by the attack.
     * @return the bounding box.
     */
    @Nonnull
    default AABB getSweepHitBox(@Nonnull Player player, @Nonnull Entity target)
    {
        return self().getItem().getSweepHitBox(self(), player, target);
    }
}
