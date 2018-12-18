/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import javax.annotation.Nullable;

import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/*
 * Extension added to ItemStack that bounces to ItemSack sensitive Item methods. Typically this is just for convince.
 */
public interface IForgeItemStack extends ICapabilitySerializable<NBTTagCompound>
{
    // Helpers for accessing Item data
    default ItemStack getStack()
    {
        return (ItemStack)this;
    }

    /**
     * ItemStack sensitive version of getContainerItem. Returns a full ItemStack
     * instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    default ItemStack getContainerItem()
    {
        return getStack().getItem().getContainerItem(getStack());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     *
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    default boolean hasContainerItem()
    {
        return getStack().getItem().hasContainerItem(getStack());
    }

    /**
     * @return the fuel burn time for this itemStack in a furnace. Return 0 to make
     *         it not act as a fuel. Return -1 to let the default vanilla logic
     *         decide.
     */
    default int getBurnTime()
    {
        return getStack().getItem().getBurnTime(getStack());
    }

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack      This item stack instance
     * @param toolClass  Tool Class
     * @param player     The player trying to harvest the given blockstate
     * @param state The block to harvest
     * @return Harvest level, or -1 if not the specified tool type.
     */
    default int getHarvestLevel(ToolType tool, @Nullable EntityPlayer player, @Nullable IBlockState state)
    {
        return getStack().getItem().getHarvestLevel(getStack(), tool, player, state);
    }

    default Set<ToolType> getToolTypes() {
        return getStack().getItem().getToolTypes(getStack());
    }

    default EnumActionResult onItemUseFirst(ItemUseContext context)
    {
       EntityPlayer entityplayer = context.getPlayer();
       BlockPos blockpos = context.getPos();
       BlockWorldState blockworldstate = new BlockWorldState(context.getWorld(), blockpos, false);
       if (entityplayer != null && !entityplayer.abilities.allowEdit && !getStack().canPlaceOn(context.getWorld().getTags(), blockworldstate)) {
          return EnumActionResult.PASS;
       } else {
          Item item = getStack().getItem();
          EnumActionResult enumactionresult = item.onItemUseFirst(getStack(), context);
          if (entityplayer != null && enumactionresult == EnumActionResult.SUCCESS) {
             entityplayer.addStat(StatList.ITEM_USED.get(item));
          }

          return enumactionresult;
       }
    }

    default NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        getStack().write(ret);
        return ret;
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
    default boolean onBlockStartBreak(BlockPos pos, EntityPlayer player)
    {
        return !getStack().isEmpty() && getStack().getItem().onBlockStartBreak(getStack(), pos, player);
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
        return getStack().getItem().shouldCauseBlockBreakReset(getStack(), newStack);
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
    default boolean canApplyAtEnchantingTable(Enchantment enchantment)
    {
        return getStack().getItem().canApplyAtEnchantingTable(getStack(), enchantment);
    }

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @return the item echantability value
     */
    default int getItemEnchantability()
    {
        return getStack().getItem().getItemEnchantability(getStack());
    }

    /**
     * Override this to set a non-default armor slot for an ItemStack, but <em>do
     * not use this to get the armor slot of said stack; for that, use
     * {@link net.minecraft.entity.EntityLiving#getSlotForItemStack(ItemStack)}.</em>
     *
     * @return the armor slot of the ItemStack, or {@code null} to let the default
     *         vanilla logic as per {@code EntityLiving.getSlotForItemStack(stack)}
     *         decide
     */
    @Nullable
    default EntityEquipmentSlot getEquipmentSlot()
    {
        return getStack().getItem().getEquipmentSlot(getStack());
    }

    /**
     * Can this Item disable a shield
     *
     * @param shield   The shield in question
     * @param entity   The EntityLivingBase holding the shield
     * @param attacker The EntityLivingBase holding the ItemStack
     * @retrun True if this ItemStack can disable the shield in question.
     */
    default boolean canDisableShield(ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker)
    {
        return getStack().getItem().canDisableShield(getStack(), shield, entity, attacker);
    }

    /**
     * Is this Item a shield
     *
     * @param entity The Entity holding the ItemStack
     * @return True if the ItemStack is considered a shield
     */
    default boolean isShield(@Nullable EntityLivingBase entity)
    {
        return getStack().getItem().isShield(getStack(), entity);
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *
     * @param entity The entity swinging the item.
     * @return True to cancel any further processing by EntityLiving
     */
    default boolean onEntitySwing(EntityLivingBase entity)
    {
        return getStack().getItem().onEntitySwing(getStack(), entity);
    }

    /**
     * Called each tick while using an item.
     *
     * @param player The Player using the item
     * @param count  The amount of time in tick the item has been used for
     *               continuously
     */
    default void onUsingTick(EntityLivingBase player, int count)
    {
        getStack().getItem().onUsingTick(getStack(), player, count);
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
     * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param world     The world the entity is in
     * @return The normal lifespan in ticks.
     */
    default int getEntityLifespan(World world)
    {
        return getStack().getItem().getEntityLifespan(getStack(), world);
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing
     * for cleaner control over the update of the item without having to write a
     * subclass.
     *
     * @param entity The entity Item
     * @return Return true to skip any further update code.
     */
    default boolean onEntityItemUpdate(EntityItem entity)
    {
        return getStack().getItem().onEntityItemUpdate(getStack(), entity);
    }

    /**
     * Returns an enum constant of type {@code HorseArmorType}. The returned enum
     * constant will be used to determine the armor value and texture of this item
     * when equipped.
     *
     * @return an enum constant of type {@code HorseArmorType}. Return
     *         HorseArmorType.NONE if this is not horse armor
     */
    default HorseArmorType getHorseArmorType()
    {
        return getStack().getItem().getHorseArmorType(getStack());
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    default void onArmorTick(World world, EntityPlayer player)
    {
        getStack().getItem().onArmorTick(getStack(), world, player);
    }

    /**
     * Called every tick from {@link EntityHorse#onUpdate()} on the item in the
     * armor slot.
     *
     * @param world the world the horse is in
     * @param horse the horse wearing this armor
     */
    default void onHorseArmorTick(World world, EntityLiving horse)
    {
        getStack().getItem().onHorseArmorTick(getStack(), world, horse);
    }

    /**
     * Whether this Item can be used as a payment to activate the vanilla beacon.
     *
     * @return true if this Item can be used
     */
    default boolean isBeaconPayment()
    {
        return getStack().getItem().isBeaconPayment(getStack());
    }

    /**
     * Determines if the specific ItemStack can be placed in the specified armor
     * slot, for the entity.
     *
     * @param armorType Armor slot to be verified.
     * @param entity    The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    default boolean canEquip(EntityEquipmentSlot armorType, Entity entity)
    {
        return getStack().getItem().canEquip(getStack(), armorType, entity);
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book  The book
     * @return if the enchantment is allowed
     */
    default boolean isBookEnchantable(ItemStack book)
    {
        return getStack().getItem().isBookEnchantable(getStack(), book);
    }


    /**
     * Called when a player drops the item into the world, returning false from this
     * will prevent the item from being removed from the players inventory and
     * spawning in the world
     *
     * @param player The player that dropped the item
     * @param item   The item stack, before the item is removed.
     */
    default boolean onDroppedByPlayer(EntityPlayer player)
    {
        return getStack().getItem().onDroppedByPlayer(getStack(), player);
    }
}
