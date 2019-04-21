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

package net.minecraftforge.debug.client;

import net.minecraftforge.client.GuiIngameForge;

@Mod(LookingAtDistanceTest.MODID)
@Mod.EventBusSubscriber
public class LookingAtDistanceTest
{
    public static final String MODID = "lookingatdistancetest";
    /*
     * Enabling this mod should increase tenfold the maximum distance at which the "Looking at" block/fluid info appears on the Debug overlay.
     */
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public void init(FMLServerStartedEvent event) throws IOException
    {
        if (!ENABLED)
        {
            return;
        }

        GuiIngameForge.ray_trace_distance *= 10;
    }
}
