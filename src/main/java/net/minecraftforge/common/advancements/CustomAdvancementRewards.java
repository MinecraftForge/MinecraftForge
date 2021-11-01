package net.minecraftforge.common.advancements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Class holding the hooks and registry for custom advancement rewards
 */
public final class CustomAdvancementRewards {

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
            @SuppressWarnings("unchecked")
            ICustomAdvancementReward.Serializer<ICustomAdvancementReward> serializer = (ICustomAdvancementReward.Serializer<ICustomAdvancementReward>) customReward.getSerializer();
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
