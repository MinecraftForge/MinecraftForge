/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.animation.ITimeValue;

// TODO review most of the methods in this "patch"
public interface IForgeItem
{
    // Helpers for accessing Item data
    default Item getItem()
    {
        return (Item) this;
    }

    /**
     * ItemStack sensitive version of getItemAttributeModifiers
     */
    @SuppressWarnings("deprecation")
    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack)
    {
        return getItem().getAttributeModifiers(slot);
    }

    /**
     * Called when a player drops the item into the world, returning false from this
     * will prevent the item from being removed from the players inventory and
     * spawning in the world
     *
     * @param player The player that dropped the item
     * @param item   The item stack, before the item is removed.
     */
    default boolean onDroppedByPlayer(ItemStack item, PlayerEntity player)
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
    default ITextComponent getHighlightTip(ItemStack item, ITextComponent displayName)
    {
        return displayName;
    }

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    default ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        return ActionResultType.PASS;
    }

    /**
     * Called by Piglins when checking to see if they will give an item or something in exchange for this item.
     *
     * @return True if this item can be used as "currency" by piglins
     */
    default boolean isPiglinCurrency(ItemStack stack)
    {
        return stack.getItem() == PiglinTasks.field_234444_a_;
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
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getArmorMaterial() == ArmorMaterial.GOLD;
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
    default CompoundNBT getShareTag(ItemStack stack)
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
    default void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt)
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
    default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player)
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
    default boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity)
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
    default ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!hasContainerItem(itemStack))
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(getItem().getContainerItem());
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
        return getItem().hasContainerItem();
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
     * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param world     The world the entity is in
     * @return The normal lifespan in ticks.
     */
    default int getEntityLifespan(ItemStack itemStack, World world)
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
    default Entity createEntity(World world, Entity location, ItemStack itemstack)
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
    default java.util.Collection<ItemGroup> getCreativeTabs()
    {
        return java.util.Collections.singletonList(getItem().getGroup());
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
    default boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IWorldReader world, BlockPos pos, PlayerEntity player)
    {
        return false;
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    default void onArmorTick(ItemStack stack, World world, PlayerEntity player)
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
    default boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity)
    {
        return MobEntity.getSlotForItemStack(stack) == armorType;
    }

    /**
     * Override this to set a non-default armor slot for an ItemStack, but <em>do
     * not use this to get the armor slot of said stack; for that, use
     * {@link net.minecraft.entity.EntityLiving#getSlotForItemStack(ItemStack)}.</em>
     *
     * @param stack the ItemStack
     * @return the armor slot of the ItemStack, or {@code null} to let the default
     *         vanilla logic as per {@code EntityLiving.getSlotForItemStack(stack)}
     *         decide
     */
    @Nullable
    default EquipmentSlotType getEquipmentSlot(ItemStack stack)
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
    default String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type)
    {
        return null;
    }

    /**
     * Returns the font renderer used to render tooltips and overlays for this item.
     * Returning null will use the standard font renderer.
     *
     * @param stack The current item stack
     * @return A instance of FontRenderer or null to use default
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    default net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack)
    {
        return null;
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     *
     * @param entityLiving The entity wearing the armor
     * @param itemStack    The itemStack to render the model of
     * @param armorSlot    The slot the armor is in
     * @param _default     Original armor model. Will have attributes set.
     * @return A ModelBiped to render instead of the default
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    default <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default)
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
     * Called when the client starts rendering the HUD, for whatever item the player
     * currently has as a helmet. This is where pumpkins would render there overlay.
     *
     * @param stack        The ItemStack that is equipped
     * @param player       Reference to the current client entity
     * @param resolution   Resolution information about the current viewport and
     *                     configured GUI Scale
     * @param partialTicks Partial ticks for the renderer, useful for interpolation
     */
    @OnlyIn(Dist.CLIENT)
    default void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks)
    {
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
        return (double) stack.getDamage() / (double) stack.getMaxDamage();
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
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
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
        return getItem().getMaxDamage();
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
        return stack.getDamage() > 0;
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
     * ItemStack sensitive version of {@link #canHarvestBlock(IBlockState)}
     *
     * @param stack The itemstack used to harvest the block
     * @param state The block trying to harvest
     * @return true if can harvest the block
     */
    default boolean canHarvestBlock(ItemStack stack, BlockState state)
    {
        return getItem().canHarvestBlock(state);
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
        return getItem().getMaxStackSize();
    }

    Set<ToolType> getToolTypes(ItemStack stack);

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack      This item stack instance
     * @param toolClass  Tool Class
     * @param player     The player trying to harvest the given blockstate
     * @param blockState The block to harvest
     * @return Harvest level, or -1 if not the specified tool type.
     */
    int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState);

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @param stack The ItemStack
     * @return the item echantability value
     */
    default int getItemEnchantability(ItemStack stack)
    {
        return getItem().getItemEnchantability();
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
    default boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment)
    {
        return enchantment.type.canEnchantItem(stack.getItem());
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
        return !(newStack.getItem() == oldStack.getItem() && ItemStack.areItemStackTagsEqual(newStack, oldStack)
                && (newStack.isDamageable() || newStack.getDamage() == oldStack.getDamage()));
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
        return ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack);
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
    default net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
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
     * @param entity   The EntityLivingBase holding the shield
     * @param attacker The EntityLivingBase holding the ItemStack
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
    default int getBurnTime(ItemStack itemStack)
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
    default void onHorseArmorTick(ItemStack stack, World world, MobEntity horse)
    {
    }

    /**
     * @return This Item's renderer, or the default instance if it does not have
     *         one.
     */
    @OnlyIn(Dist.CLIENT)
    ItemStackTileEntityRenderer getItemStackTileEntityRenderer();

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
    default boolean isEnderMask(ItemStack stack, PlayerEntity player, EndermanEntity endermanEntity)
    {
        return stack.getItem() == Blocks.CARVED_PUMPKIN.asItem();
    }

}
