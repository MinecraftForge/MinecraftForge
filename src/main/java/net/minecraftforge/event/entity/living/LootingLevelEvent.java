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

package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class LootingLevelEvent extends LivingEvent {

    private final DamageSource damageSource;

    private int lootingLevel;

    public LootingLevelEvent(LivingEntity entity, DamageSource damageSource, int lootingLevel) {
        super(entity);
        this.damageSource = damageSource;
        this.lootingLevel = lootingLevel;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public int getLootingLevel() {
        return lootingLevel;
    }

    public void setLootingLevel(int lootingLevel) {
        this.lootingLevel = lootingLevel;
    }
}
