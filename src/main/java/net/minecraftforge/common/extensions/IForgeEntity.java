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

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

public interface IForgeEntity extends ICapabilitySerializable<NBTTagCompound>
{
    default Entity getEntity() { return (Entity) this; }

    default void deserializeNBT(NBTTagCompound nbt)
    {
        getEntity().read(nbt);
    }

    default NBTTagCompound serializeNBT()
    {
        NBTTagCompound ret = new NBTTagCompound();
        String id = getEntity().getEntityString();
        if (id != null)
        {
            ret.setString("id", getEntity().getEntityString());
        }
        return getEntity().writeWithoutTypeId(ret);
    }

    boolean canUpdate();
    void canUpdate(boolean value);

    @Nullable
    Collection<EntityItem> captureDrops();
    Collection<EntityItem> captureDrops(@Nullable Collection<EntityItem> captureDrops);

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
        if (this instanceof EntityPainting)
        {
            return new ItemStack(Items.PAINTING);
        }
        else if (this instanceof EntityLeashKnot)
        {
            return new ItemStack(Items.LEAD);
        }
        else if (this instanceof EntityItemFrame)
        {
            ItemStack held = ((EntityItemFrame)this).getDisplayedItem();
            if (held.isEmpty())
                return new ItemStack(Items.ITEM_FRAME);
            else
                return held.copy();
        }
        else if (this instanceof EntityMinecart)
        {
            return ((EntityMinecart)this).getCartItem();
        }
        else if (this instanceof EntityBoat)
        {
            return new ItemStack(((EntityBoat)this).getItemBoat());
        }
        else if (this instanceof EntityArmorStand)
        {
            return new ItemStack(Items.ARMOR_STAND);
        }
        else if (this instanceof EntityEnderCrystal)
        {
            return new ItemStack(Items.END_CRYSTAL);
        }
        else
        {
            ItemSpawnEgg egg = ItemSpawnEgg.getEgg(getEntity().getType());
            if (egg != null) return new ItemStack(egg);
        }
        return ItemStack.EMPTY;
    }

    default boolean shouldRenderInPass(int pass)
    {
        return pass == 0;
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
     * If the rider should be dismounted from the entity when the entity goes under water
     *
     * @param rider The entity that is riding
     * @return if the entity should be dismounted when under water
     */
    default boolean shouldDismountInWater(Entity rider)
    {
        return this instanceof EntityLivingBase;
    }


    /**
     * Checks if this {@link Entity} can trample a {@link Block}.
     *
     * @param pos The block pos
     * @param fallDistance The fall distance
     * @return {@code true} if this entity can trample, {@code false} otherwise
     */
    boolean canTrample(IBlockState state, BlockPos pos, float fallDistance);

    /**
     * Returns true if the entity is of the @link{EnumCreatureType} provided
     * @param type The EnumCreatureType type this entity is evaluating
     * @param forSpawnCount If this is being invoked to check spawn count caps.
     * @return If the creature is of the type provided
     */
    default boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
        if (forSpawnCount && (this instanceof EntityLiving) && ((EntityLiving)this).isNoDespawnRequired()) return false;
        return type.getBaseClass().isAssignableFrom(this.getClass());
    }
}
