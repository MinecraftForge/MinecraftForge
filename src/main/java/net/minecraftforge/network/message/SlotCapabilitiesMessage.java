/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.network.message;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

public record SlotCapabilitiesMessage(int entityId, int slotIndex, FriendlyByteBuf capabilityData)
{
    public void encode(FriendlyByteBuf out)
    {
        out.writeVarInt(this.entityId);
        out.writeShort(this.slotIndex);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
        this.capabilityData.release();
    }

    public static SlotCapabilitiesMessage decode(FriendlyByteBuf in)
    {
        return new SlotCapabilitiesMessage(in.readVarInt(), in.readVarInt(),
            new FriendlyByteBuf(in.readBytes(in.readVarInt())));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get()
            .enqueueWork(() -> LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .map(player -> player.containerMenu)
                .filter(menu -> this.slotIndex < menu.slots.size())
                .map(container -> container.getSlot(this.slotIndex))
                .ifPresent(slot -> slot.getItem().readCapabilities(this.capabilityData)))
            .thenRun(this.capabilityData::release);
        return true;
    }
}
