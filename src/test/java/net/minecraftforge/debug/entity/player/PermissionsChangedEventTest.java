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

package net.minecraftforge.debug.entity.player;

import net.minecraftforge.event.entity.player.PermissionsChangedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("permissions_changed_event_test")
@Mod.EventBusSubscriber
public class PermissionsChangedEventTest
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPermissionChanged(PermissionsChangedEvent event)
    {
        LOGGER.info("{} permission level changed to {} from {}",
                event.getPlayer().getName().getString(),
                event.getNewLevel(),
                event.getOldLevel());
    }
}
