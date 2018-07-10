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
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mod(modid = TrackingTargetTest.ID, name = "Tracking Target Test", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class TrackingTargetTest
{
    public static final String ID = "trackingtargettest";
    private static boolean ENABLED = false;
    private static final SimpleNetworkWrapper NET = new SimpleNetworkWrapper(ID);
    private static final Logger LOGGER = LogManager.getLogger(ID);

    @Mod.EventHandler
    public static void preinit(FMLPreInitializationEvent evt)
    {
        NET.registerMessage(TestMessageHandler.class, TestMessage.class, 0, Side.CLIENT);
        NET.registerMessage(TestEntityMessageHandler.class, TestEntityMessage.class, 1, Side.CLIENT);
    }

    // Every 3 seconds, send a message to all players tracking overworld (500, 500).
    // If you move sufficiently far away (i.e greater than the server render distance) from (500, 500), you should stop receiving the messages.
    @SubscribeEvent
    public static void tick(TickEvent.WorldTickEvent evt)
    {
        if (ENABLED && evt.side == Side.SERVER && evt.phase == TickEvent.Phase.END)
        {
            if (evt.world.getWorldTime() % 60 == 0)
            {
                NetworkRegistry.TargetPoint pt = new NetworkRegistry.TargetPoint(0, 500, 0, 500, -1);
                NET.sendToAllTracking(new TestMessage(), pt);
            }
        }
    }

    public static class TestMessage implements IMessage
    {
        @Override
        public void fromBytes(ByteBuf buf) {}

        @Override
        public void toBytes(ByteBuf buf) {}
    }

    public static class TestMessageHandler implements IMessageHandler<TestMessage, IMessage>
    {
        @Override
        public IMessage onMessage(TestMessage message, MessageContext ctx)
        {
            LOGGER.info("Received tracking point test message");
            return null;
        }
    }

    // Every 3 seconds, send a message to all players tracking any item frame with a stick in it
    // If you move sufficiently far away from the frame, you should stop receiving the messages.
    private static final Set<EntityItemFrame> FRAMES = Collections.newSetFromMap(new WeakHashMap<>());
    @SubscribeEvent
    public static void frameJoin(EntityJoinWorldEvent evt)
    {
        if (ENABLED && !evt.getWorld().isRemote && evt.getEntity() instanceof EntityItemFrame)
        {
            FRAMES.add((EntityItemFrame) evt.getEntity());
        }
    }

    @SubscribeEvent
    public static void tickEntity(TickEvent.WorldTickEvent evt)
    {
        if (ENABLED && evt.side == Side.SERVER && evt.phase == TickEvent.Phase.END)
        {
            if (evt.world.getWorldTime() % 60 == 0)
            {
                for (EntityItemFrame frame : FRAMES)
                {
                    if (!frame.isDead && !frame.getDisplayedItem().isEmpty() && frame.getDisplayedItem().getItem() == Items.STICK)
                    {
                        NET.sendToAllTracking(new TestEntityMessage(), frame);
                    }
                }
            }
        }
    }

    public static class TestEntityMessage implements IMessage
    {
        @Override
        public void fromBytes(ByteBuf buf) {}

        @Override
        public void toBytes(ByteBuf buf) {}
    }

    public static class TestEntityMessageHandler implements IMessageHandler<TestEntityMessage, IMessage>
    {
        @Override
        public IMessage onMessage(TestEntityMessage message, MessageContext ctx)
        {
            LOGGER.info("Received tracking point test entity message");
            return null;
        }
    }
}
