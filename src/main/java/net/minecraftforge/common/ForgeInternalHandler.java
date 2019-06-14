/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.CloudRenderer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ForgeInternalHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity.getClass().equals(EntityItem.class))
        {
            ItemStack stack = ((EntityItem)entity).getItem();
            Item item = stack.getItem();
/*
            if (item.hasCustomEntity(stack))
            {
                Entity newEntity = item.createEntity(event.getWorld(), entity, stack);
                if (newEntity != null)
                {
                    entity.setDead();
                    event.setCanceled(true);
                    event.getWorld().spawnEntity(newEntity);
                }
            }
*/
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDimensionUnload(WorldEvent.Unload event)
    {
        if (event.getWorld() instanceof WorldServer)
            FakePlayerFactory.unloadWorld((WorldServer) event.getWorld());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        WorldWorkerManager.tick(event.phase == TickEvent.Phase.START);
    }

    @SubscribeEvent
    public void checkSettings(ClientTickEvent event)
    {
        if (event.phase == Phase.END)
            CloudRenderer.updateCloudSettings();
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event)
    {
        if (!event.getWorld().isRemote())
            FarmlandWaterManager.removeTickets(event.getChunk());
    }
}

