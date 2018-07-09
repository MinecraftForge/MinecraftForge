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

package net.minecraftforge.debug.fluid;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "createfluidsourcetest", name = "CreateFluidSourceTest", version = "1.0", acceptableRemoteVersions = "*")
public class CreateFluidSourceEventTest
{
    public static final boolean ENABLE = false;

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(CreateFluidSourceEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onCreateFluidSource(CreateFluidSourceEvent event)
    {
        // make it work exactly the opposite of how it works by default
        if (event.getState().getBlock() == Blocks.FLOWING_WATER)
        {
            event.setResult(Result.DENY);
        }
        else
        {
            event.setResult(Result.ALLOW);
        }
    }
}
