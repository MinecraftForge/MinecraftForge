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

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = AttackEventTest.MODID, name = AttackEventTest.NAME, version = "1.0.0", acceptableRemoteVersions = "*")
public class AttackEventTest
{

    public static final String MODID = "livingattackeventtest";
    public static final String NAME = "LivingAttackEventTest";
    private static final Logger LOGGER = LogManager.getLogger(NAME);

    @EventBusSubscriber
    public static class LivingAttackEventHandler
    {

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event)
        {
            if (event.getSource() == DamageSource.ANVIL)
            {
                LOGGER.info("{} was hit by an anvil!", event.getEntityLiving().getName());
            }
        }
    }
}
