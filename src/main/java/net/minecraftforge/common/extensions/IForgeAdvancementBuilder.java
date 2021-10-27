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

package net.minecraftforge.common.extensions;

import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public interface IForgeAdvancementBuilder
{

    private Advancement.Builder self()
    {
        return (Advancement.Builder) this;
    }

    /**
     * save function for the {@link Advancement.Builder} which uses the {@link ExistingFileHelper} to check if the parent is already known
     * @param consumer A {@link Consumer} which the build {@link Advancement} is passed to
     * @param id The {@link ResourceLocation} for the new {@link Advancement}
     * @param fileHelper The {@link ExistingFileHelper} where all known {@link Advancement}s are registered
     * @return The build {@link Advancement}
     * @throws IllegalStateException is thrown if the parent of the {@link Advancement} is not known
     */
    default Advancement save(Consumer<Advancement> consumer ,ResourceLocation id, ExistingFileHelper fileHelper)
    {
        boolean canBuild = self().canBuild((advancementId) ->
        {
            if (fileHelper.exists(advancementId, PackType.SERVER_DATA, ".json", "advancements"))
            {
                return new Advancement(advancementId, null, null, AdvancementRewards.EMPTY, Maps.newHashMap(), null);
            }
            return null;
        });
        if (!canBuild)
        {
            throw new IllegalStateException("Tried to build Advancement without valid Parent!");
        }

        Advancement advancement = self().build(id);
        consumer.accept(advancement);
        return advancement;
    }
}
