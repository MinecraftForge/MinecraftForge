/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.event.entity.living;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingBreatheEvent extends LivingEvent
{
    private boolean canBreathe;
    private int consumeAirAmount;
    private int refillAirAmount;

    public LivingBreatheEvent(LivingEntity entity, int consumeAirAmount, int refillAirAmount)
    {
        super(entity);
        this.canBreathe = !entity.isEyeInFluid(FluidTags.WATER)
                || entity.level.getBlockState(new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ())).is(Blocks.BUBBLE_COLUMN)
                || entity.canBreatheUnderwater()
                || MobEffectUtil.hasWaterBreathing(entity)
                || (entity instanceof Player && ((Player) entity).getAbilities().invulnerable);
        this.consumeAirAmount = consumeAirAmount;
        this.refillAirAmount = refillAirAmount;
    }

    public boolean canBreathe()
    {
        return canBreathe;
    }

    public void setCanBreathe(boolean canBreathe)
    {
        this.canBreathe = canBreathe;
    }

    public int getConsumeAirAmount()
    {
        return consumeAirAmount;
    }

    public void setConsumeAirAmount(int consumeAirAmount)
    {
        this.consumeAirAmount = consumeAirAmount;
    }

    public int getRefillAirAmount()
    {
        return refillAirAmount;
    }

    public void setRefillAirAmount(int refillAirAmount)
    {
        this.refillAirAmount = refillAirAmount;
    }
}
