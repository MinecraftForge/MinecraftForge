/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "equipment_change_test", version = "1.0.0")
public class EquipmentChangeTest 
{

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) 
    {
        //register the eventhandler
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * the Method handling the {@link LivingEquipmentChangeEvent}
     * Serverside only!
     */
    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) 
    {
        //a debug console print
        FMLLog.info("[Equipment-Change] " + event.getEntity() + " changed his Equipment in "
                + event.getSlot() + " from " + event.getFrom() + " to " + event.getTo());
    }

}
