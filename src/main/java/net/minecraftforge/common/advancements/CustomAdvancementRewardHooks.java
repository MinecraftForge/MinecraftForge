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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Class holding the hooks for custom advancement rewards
 */
public final class CustomAdvancementRewardHooks {

    /**
     * Hook for AdvancementRewards#grant
     */
    public static void grant(ServerPlayer player, ICustomAdvancementReward[] customRewards)
    {
        for (ICustomAdvancementReward reward : customRewards)
        {
            reward.grant(player);
        }
    }

    /**
     * Hook for AdvancementRewards#serialize
     */
    public static void serialize(JsonObject jsonObject, ICustomAdvancementReward[] customRewards)
    {
        if (customRewards.length == 0) return;
        JsonArray arr = new JsonArray();
        for (ICustomAdvancementReward customReward : customRewards)
        {
            ICustomAdvancementReward.Serializer<ICustomAdvancementReward> serializer = customReward.getSerializer();
            JsonObject element = serializer.serialize(customReward);
            element.addProperty("type", serializer.getRegistryName().toString());
            arr.add(element);
        }
        jsonObject.add("custom", arr);
    }

    /**
     * Hook for AdvancementRewards#deserialize
     */
    public static ICustomAdvancementReward[] deserialize(JsonObject jsonObject)
    {
        JsonArray arr = GsonHelper.getAsJsonArray(jsonObject, "custom", new JsonArray());
        ICustomAdvancementReward[] out = new ICustomAdvancementReward[arr.size()];
        for (int i = 0; i < out.length; i++)
        {
            JsonObject element = GsonHelper.convertToJsonObject(arr.get(i), "custom[" + i + "]");
            ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(element, "type"));
            element.remove("type");
            ICustomAdvancementReward.Serializer<?> serializer = ForgeRegistries.CUSTOM_ADVANCEMENT_REWARD_SERIALIZERS.getValue(type);
            if (serializer == null)
                throw new JsonParseException("Unregistered custom advancement reward type: "+type);
            out[i] = serializer.deserialize(element);
        }
        return out;
    }
}
