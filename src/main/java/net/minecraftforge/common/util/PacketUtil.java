/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
package net.minecraftforge.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class PacketUtil
{
    private PacketUtil() {}

    /**
     * Serializes an ItemStack to send to the client. If the player is in creative mode, all NBT
     * data of the item is sent to the client. This prevents the NBT data of the ItemStack being lost
     * due to the fact that creative serializes from Client to Server, rather than the opposite way.
     * This still respects the original getShareTag() behavior in PacketBuffer#writeItemStack()
     *
     * @param buffer the packet buffer
     * @param player the player being sent the packet
     * @param stack the itemstack to serialize
     */
    public static void writeItemStackFromServerToClient(PacketBuffer buffer, @Nullable EntityPlayerMP player, ItemStack stack)
    {
        if (stack.isEmpty())
        {
            buffer.writeShort(-1);
        }
        else
        {
            buffer.writeShort(Item.getIdFromItem(stack.getItem()));
            buffer.writeByte(stack.getCount());
            buffer.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if(player != null && player.interactionManager.isCreative())
            {
                nbttagcompound = stack.getTagCompound();
            }
            else if(stack.getItem().isDamageable() || stack.getItem().getShareTag())
            {
                nbttagcompound = stack.getItem().getNBTShareTag(stack);
            }

            buffer.writeCompoundTag(nbttagcompound);
        }
    }

    /**
     * Most ItemStack serialization is Server to Client, and must go through PacketBuffer.writeItemStack which uses Item.getNBTShareTag.
     * One exception is items from the creative menu, which must be sent from Client to Server with their full NBT.
     * <br/>
     * This method matches PacketBuffer.writeItemStack but without the Item.getNBTShareTag patch.
     * It is compatible with PacketBuffer.readItemStack.
     */
    public static void writeItemStackFromClientToServer(PacketBuffer buffer, @Nullable EntityPlayer player, ItemStack stack)
    {
        if (stack.isEmpty())
        {
            buffer.writeShort(-1);
        }
        else
        {
            buffer.writeShort(Item.getIdFromItem(stack.getItem()));
            buffer.writeByte(stack.getCount());
            buffer.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag() || player != null && player.capabilities.isCreativeMode)
            {
                nbttagcompound = stack.getTagCompound();
            }

            buffer.writeCompoundTag(nbttagcompound);
        }
    }
}
