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

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeEntity extends ICapabilitySerializable<CompoundNBT>
{
    default Entity getEntity() { return (Entity) this; }

    default void deserializeNBT(CompoundNBT nbt)
    {
        getEntity().read(nbt);
    }

    default CompoundNBT serializeNBT()
    {
        CompoundNBT ret = new CompoundNBT();
        String id = getEntity().getEntityString();
        if (id != null)
        {
            ret.putString("id", getEntity().getEntityString());
        }
        return getEntity().writeWithoutTypeId(ret);
    }

    boolean canUpdate();
    void canUpdate(boolean value);

    @Nullable
    Collection<ItemEntity> captureDrops();
    Collection<ItemEntity> captureDrops(@Nullable Collection<ItemEntity> captureDrops);


    /**
     * Returns a NBTTagCompound that can be used to store custom data for this entity.
     * It will be written, and read from disc, so it persists over world saves.
     * @return A NBTTagCompound
     */
    CompoundNBT getPersistentData();

    /**
     * Used in model rendering to determine if the entity riding this entity should be in the 'sitting' position.
     * @return false to prevent an entity that is mounted to this entity from displaying the 'sitting' animation.
     */
    default boolean shouldRiderSit()
    {
        return true;
    }

    /**
     * Called when a user uses the creative pick block button on this entity.
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty ItemStack if nothing should be added.
     */
    default ItemStack getPickedResult(RayTraceResult target)
    {
        if (this instanceof PaintingEntity)
            return new ItemStack(Items.PAINTING);
        else if (this instanceof LeashKnotEntity)
            return new ItemStack(Items.LEAD);
        else if (this instanceof ItemFrameEntity)
        {
            ItemStack held = ((ItemFrameEntity)this).getDisplayedItem();
            if (held.isEmpty())
                return new ItemStack(Items.ITEM_FRAME);
            else
                return held.copy();
        }
        else if (this instanceof AbstractMinecartEntity)
            return ((AbstractMinecartEntity)this).getCartItem();
        else if (this instanceof BoatEntity)
            return new ItemStack(((BoatEntity)this).getItemBoat());
        else if (this instanceof ArmorStandEntity)
            return new ItemStack(Items.ARMOR_STAND);
        else if (this instanceof EnderCrystalEntity)
            return new ItemStack(Items.END_CRYSTAL);
        else
        {
            SpawnEggItem egg = SpawnEggItem.getEgg(getEntity().getType());
            if (egg != null) return new ItemStack(egg);
        }
        return ItemStack.EMPTY;
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the
     * ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    default boolean canRiderInteract()
    {
        return false;
    }

    /**
     * Checks if this entity can continue to be ridden while it is underwater.
     *
     * @param rider The entity that is riding this entity
     * @return {@code true} if the entity can continue to ride underwater. {@code false} otherwise.
     */
    default boolean canBeRiddenInWater(Entity rider)
    {
        return this instanceof LivingEntity;
    }

    /**
     * Checks if this {@link Entity} can trample a {@link Block}.
     *
     * @param pos The block pos
     * @param fallDistance The fall distance
     * @return {@code true} if this entity can trample, {@code false} otherwise
     */
    boolean canTrample(BlockState state, BlockPos pos, float fallDistance);

    /**
     * Returns The classification of this entity
     * @param forSpawnCount If this is being invoked to check spawn count caps.
     * @return If the creature is of the type provided
     */
    default EntityClassification getClassification(boolean forSpawnCount)
    {
        return getEntity().getType().getClassification();
    }

    /**
     * Gets whether this entity has been added to a world (for tracking). Specifically
     * between the times when an entity is added to a world and the entity being removed
     * from the world's tracked lists. See {@link World#onEntityAdded(Entity)} and
     * {@link World#onEntityRemoved(Entity)}.
     *
     * @return True if this entity is being tracked by a world
     */
    boolean isAddedToWorld();

    /**
     * Called after the entity has been added to the world's
     * ticking list. Can be overriden, but needs to call super
     * to prevent MC-136995.
     */
    void onAddedToWorld();

    /**
     * Called after the entity has been removed to the world's
     * ticking list. Can be overriden, but needs to call super
     * to prevent MC-136995.
     */
    void onRemovedFromWorld();

    /**
     * Revives an entity that has been removed from a world.
     * Used as replacement for entity.removed = true. Having it as a function allows
     * the entity to react to being revived.
     */
    void revive();
}
