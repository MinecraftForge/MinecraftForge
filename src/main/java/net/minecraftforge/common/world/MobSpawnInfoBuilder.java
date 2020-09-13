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

package net.minecraftforge.common.world;

import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.mixin.MixinMobSpawnAccessors;

public class MobSpawnInfoBuilder extends MobSpawnInfo.Builder {
    public MobSpawnInfoBuilder(MobSpawnInfo orig) {
        ((MixinMobSpawnAccessors)orig).getField_242554_e().forEach((k, l)-> {
            field_242567_a.get(k).clear();
            field_242567_a.get(k).addAll(new java.util.ArrayList<>(l));
        });
        field_242568_b.putAll(((MixinMobSpawnAccessors)orig).getField_242555_f());
        field_242569_c = orig.func_242557_a();
        field_242570_d = orig.func_242562_b();
    }
}

