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

package net.minecraftforge.fml.test;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ForgeNetworkTestMod.MOD_ID, name = ForgeNetworkTestMod.MOD_ID, version = "1.0", acceptableRemoteVersions = "*")
public class ForgeNetworkTestMod
{
    private static final boolean ENABLED = false;
    public static final String MOD_ID = "forgenetworktest";
    private FMLEventChannel channel;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent e)
    {
        if (ENABLED && e.getSide() == Side.SERVER)
        {
            MinecraftForge.EVENT_BUS.register(this);
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MOD_ID);
            channel.register(this);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent e)
    {
        if (channel == null)
        {
            return;
        }
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeByte(0);
        channel.sendTo(new FMLProxyPacket(buffer, MOD_ID), (EntityPlayerMP) e.player); // disconnects vanilla clients in 1.11
    }
}
