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

package net.minecraftforge.debug.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = BigNetworkMessageTest.MOD_ID, name = "Big network message test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class BigNetworkMessageTest
{
    static final boolean ENABLED = false;
    static final String MOD_ID = "big_packet_test";
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        if (ENABLED)
        {
            INSTANCE.registerMessage((msg, ctx) -> null, BigMessage.class, 0, Side.SERVER);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent evt)
    {
        if (ENABLED)
        {
            INSTANCE.sendTo(new BigMessage(), (EntityPlayerMP) evt.player);
        }
    }

    public static class BigMessage implements IMessage
    {
        private static final int BYTE_COUNT = 0x200000;// 2MB

        @Override public void toBytes(ByteBuf buf)
        {
            // write consecutive bytes, overflowing back to zero as necessary
            for (int i = 0; i < BYTE_COUNT; i++)
            {
                buf.writeByte(i & 0xFF);
            }
        }

        @Override public void fromBytes(ByteBuf buf)
        {
            for (int i = 0; i < BYTE_COUNT; i++)
            {
                int read = buf.readUnsignedByte();
                int expected = i & 0xFF;
                if (read != expected)
                {
                    throw new RuntimeException("Found incorrect byte at " + (i + 1) + " expected " + expected + " but found " + read);
                }
            }
        }
    }
}
