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

package net.minecraftforge.debug.entity.player;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "playerdamagereworktest", name = "PlayerDamageReworkTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class DamageReworkTest
{
    private static final boolean ENABLE = false;
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("23373DC8-1F3D-11E7-93AE-92361F002671");
    private static final AttributeModifier mod = new AttributeModifier(ARMOR_MODIFIER_UUID, "Player Damage Rework Test", 20, 0);

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE) MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void checkForSneakEvent(LivingUpdateEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player.isSneaking())
            {
                if (!player.getEntityAttribute(SharedMonsterAttributes.ARMOR).hasModifier(mod))
                {
                    player.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(mod);
                }
            }
            else if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR).hasModifier(mod))
            {
                player.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            }
        }
    }
}