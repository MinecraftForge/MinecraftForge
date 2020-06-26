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

import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.event.ParticleWorldHandleEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ParticleWorldHandleEventTest.MODID)
@Mod(value = ParticleWorldHandleEventTest.MODID)
public class ParticleWorldHandleEventTest {

    public static final String MODID = "particle_world_handle_event_test";

    /**
     * The following will replace all {@link ParticleTypes#DAMAGE_INDICATOR} with {@link ParticleTypes#ANGRY_VILLAGER}
     * as well as raising the Y-coord with 2, and doubling the particle speed.
     * @param event
     */
    @SubscribeEvent
    public static void onEvent(ParticleWorldHandleEvent event) {
        if(event.getParticle().getParameters().equals(ParticleTypes.DAMAGE_INDICATOR.getParameters())) {
            event.setYCoord(event.getYCoord() + 2);
            event.setParticle(ParticleTypes.ANGRY_VILLAGER);
            event.setParticleSpeed(event.getParticleSpeed() * 2);
        }
    }
}
