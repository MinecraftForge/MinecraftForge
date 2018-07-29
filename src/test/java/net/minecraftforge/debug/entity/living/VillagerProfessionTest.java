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

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@Mod(modid = "professiontest", name = "ProfessionTest2000", version = "1.0", acceptableRemoteVersions = "*")
@EventBusSubscriber
public class VillagerProfessionTest
{
    @SubscribeEvent
    public static void registerVillagers(RegistryEvent.Register<VillagerProfession> event)
    {
        VillagerProfession profession = new VillagerProfession("professiontest:test_villager", "professiontest:textures/entity/test_villager.png", "professiontest:textures/entity/zombie_test_villager.png");
        new VillagerCareer(profession, "professiontest:test_villager");
        event.getRegistry().register(profession);
    }
}
