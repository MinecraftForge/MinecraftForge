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

package net.minecraftforge.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Interface for a custom advancement reward
 */
public interface ICustomAdvancementReward
{

    /**
     * Called when the advancement is granted to the given player.
     * Only runs on the server.
     *
     * @param player the player the advancement is granted to
     */
    void grant(ServerPlayer player);

    /**
     * Get the serializer for this custom advancement reward
     * @return the serializer for this custom advancement reward
     */
    <T extends ICustomAdvancementReward> Serializer<T> getSerializer();

    abstract class Serializer<T extends ICustomAdvancementReward> extends ForgeRegistryEntry<Serializer<?>>
    {
        public abstract T deserialize(JsonObject json);
        public abstract JsonObject serialize(T reward);
    }
}
