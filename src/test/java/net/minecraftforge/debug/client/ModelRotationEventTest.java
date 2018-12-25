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

import net.minecraft.client.model.ModelBiped;
import net.minecraftforge.client.event.ModelRotationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Example mod to test ModelRotationEvent.
 */
@Mod(modid = "modelrotationtest", name = "Model Rotation Test", version = "0.0.0", clientSideOnly = true)
public class ModelRotationEventTest
{
    static final boolean ENABLED = true;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void modelEvent(ModelRotationEvent event)
    {
		if(event.getRender().getMainModel() instanceof ModelBiped)
		{
			ModelBiped model = (ModelBiped) event.getRender().getMainModel();
			model.bipedHead.showModel=false;	
		}
    }
}
