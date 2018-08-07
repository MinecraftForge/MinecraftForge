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

package net.minecraftforge.debug.block;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FarmlandTrampleEventTest.MOD_ID, name = "Farmland Trample Event Test", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class FarmlandTrampleEventTest
{
    static final String MOD_ID = "farmland_trample_test";
    static final boolean ENABLED = true;

    @SubscribeEvent
    public static void onFarmlandTrampled(BlockEvent.FarmlandTrampleEvent event)
    {
        if (!ENABLED)
        {
            return;
        }
        if(event.getEntity().isSneaking())
        {
            event.setCanceled(true);
        }
    }
}
