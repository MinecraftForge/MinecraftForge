/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_conversion_event_test")
public class LivingConversionEventTest
{
    public LivingConversionEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::canLivingConversion);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingConversion);
    }

    public void canLivingConversion(LivingConversionEvent.Pre event)
    {
        if (event.getEntityLiving() instanceof PiglinEntity)
        {
            event.setCanceled(true);
            event.setConversionTimer(0);
        }
    }

    public void onLivingConversion(LivingConversionEvent.Post event)
    {
        if (event.getEntityLiving() instanceof VillagerEntity)
            event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.LUCK, 20));
    }
}
