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

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.entity.PartEntity;

public interface IForgeEntity extends ICapabilitySerializable<CompoundTag>
{
    private Entity self() { return (Entity) this; }

    default void deserializeNBT(CompoundTag nbt)
    {
        self().load(nbt);
    }

    default CompoundTag serializeNBT()
    {
        CompoundTag ret = new CompoundTag();
        String id = self().getEncodeId();
        if (id != null)
        {
            ret.putString("id", self().getEncodeId());
        }
        return self().saveWithoutId(ret);
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
    CompoundTag getPersistentData();

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
    default ItemStack getPickedResult(HitResult target)
    {
        ItemStack result = self().getPickResult();
        if (result == null) {
            SpawnEggItem egg = SpawnEggItem.byId(self().getType());
            if (egg != null)
                result = new ItemStack(egg);
            else
                result = ItemStack.EMPTY;
        }
        return result;
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
        return self().rideableUnderWater();
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
    default MobCategory getClassification(boolean forSpawnCount)
    {
        return self().getType().getCategory();
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


    /**
     * This is used to specify that your entity has multiple individual parts, such as the Vanilla Ender Dragon.
     *
     * See {@link net.minecraft.entity.boss.dragon.EnderDragonEntity} for an example implementation.
     * @return true if this is a multipart entity.
     */
    default boolean isMultipartEntity()
    {
        return false;
    }

    /**
     * Gets the individual sub parts that make up this entity.
     *
     * The entities returned by this method are NOT saved to the world in nay way, they exist as an extension
     * of their host entity. The child entity does not track its server-side(or client-side) counterpart, and
     * the host entity is responsible for moving and managing these children.
     *
     * Only used if {@link #isMultipartEntity()} returns true.
     *
     * See {@link net.minecraft.entity.boss.dragon.EnderDragonEntity} for an example implementation.
     * @return The child parts of this entity. The value to be returned here should be cached.
     */
    @Nullable
    default PartEntity<?>[] getParts()
    {
        return null;
    }
}
