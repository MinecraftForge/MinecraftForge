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

package net.minecraftforge.debug.entity.living;

import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

@Mod(modid = "entitymobgriefingeventtest", name = "EntityMobGriefingEventTest", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class MobGriefingEventTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onMobGriefing(EntityMobGriefingEvent event)
    {
        if (ENABLED)
        {
            String customName = event.getEntity().getCustomNameTag();

            try
            {
                Result result = Result.valueOf(customName);
                event.setResult(result);
            }
            catch (IllegalArgumentException iae)
            {
                // Thrown if the name tag did not match a result value, can be ignored and DEFAULT will still be used.
            }
        }
    }
}
