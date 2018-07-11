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

package net.minecraftforge.debug.world;

import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.event.world.CreateWorldProviderEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = WorldProviderEventTest.MOD_ID, name = "CreateWorldProviderEvent test mod", version = "1.0")
@Mod.EventBusSubscriber
public class WorldProviderEventTest
{
    static final String MOD_ID = "world_provider_event_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void setWorldProvider(CreateWorldProviderEvent event)
    {
        if (!ENABLED) return;

        if (event.getDimension() == 0)
        {
            event.setProvider(new TestWorldProvider());
        }
    }

    private static final class TestWorldProvider extends WorldProviderSurface
    {
        @Override
        public void getLightmapColors(float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors)
        {
            colors[0] = 0.22F + blockLight * 0.75F;
            colors[1] = 0.28F + blockLight * 0.75F;
            colors[2] = 0.25F + blockLight * 0.75F;
        }
    }
}
