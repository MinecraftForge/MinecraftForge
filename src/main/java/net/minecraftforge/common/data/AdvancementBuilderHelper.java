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

package net.minecraftforge.common.data;

import com.google.common.collect.Maps;
import net.minecraft.advancements.*;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public class AdvancementBuilderHelper {

    public static Advancement build(ResourceLocation id, Consumer<Advancement> consumer, ExistingFileHelper fileHelper, Advancement.Builder builder) {
        boolean canBuild = builder.canBuild((advancementId) -> {
            if (fileHelper.exists(advancementId, ResourcePackType.SERVER_DATA, ".json", "advancements")) {
                return new Advancement(advancementId, null, null, AdvancementRewards.EMPTY, Maps.newHashMap(), null);
            }
            return null;
        });
        if (!canBuild) {
            throw new IllegalStateException("Tried to build Advancement without valid Parent!");
        }

        Advancement advancement = builder.build(id);
        consumer.accept(advancement);
        return advancement;
    }
}
