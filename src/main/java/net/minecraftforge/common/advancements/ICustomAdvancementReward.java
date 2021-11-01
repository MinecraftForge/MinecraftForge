package net.minecraftforge.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Interface for a custom advancement reward
 */
public interface ICustomAdvancementReward {

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
    Serializer<?> getSerializer();

    abstract class Serializer<T extends ICustomAdvancementReward> extends ForgeRegistryEntry<Serializer<?>> {
        public abstract T deserialize(JsonObject json);
        public abstract JsonObject serialize(T reward);
    }
}
