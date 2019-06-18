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

package net.minecraftforge.debug.world;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLInitializationEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@Mod(modid = DecorateEventDebug.MODID, name = DecorateEventDebug.NAME, version = DecorateEventDebug.VERSION, acceptableRemoteVersions = "*")
public class DecorateEventDebug
{

    private static final boolean ENABLED = false;
    public static final String MODID = "decorateeventdebug";
    public static final String NAME = "DecorateEventDebug";
    public static final String VERSION = "1.0.0";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.TERRAIN_GEN_BUS.register(this);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void decorateEvent(DecorateBiomeEvent.Decorate event)
    {
        event.setResult(Result.DENY);
    }
}
