package net.minecraftforge.debug;

import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.advancements.ICustomAdvancementReward;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("custom_advancement_rewards_test")
public class CustomAdvancementRewardsTest {
    private static final DeferredRegister<ICustomAdvancementReward.Serializer<?>> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.CUSTOM_ADVANCEMENT_REWARD_SERIALIZERS, "custom_advancement_rewards_test");
    private static final RegistryObject<SendMessageReward.Serializer> SEND_MESSAGE = DEFERRED_REGISTER.register("send_message", SendMessageReward.Serializer::new);

    public CustomAdvancementRewardsTest() {
        DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public record SendMessageReward(String message) implements ICustomAdvancementReward {
        @Override
        public void grant(ServerPlayer player) {
            player.sendMessage(new TextComponent(this.message()), Util.NIL_UUID);
        }

        @Override
        public Serializer getSerializer() {
            return SEND_MESSAGE.get();
        }

        public static class Serializer extends ICustomAdvancementReward.Serializer<SendMessageReward> {

            @Override
            public SendMessageReward deserialize(JsonObject json) {
                return new SendMessageReward(GsonHelper.getAsString(json, "message"));
            }

            @Override
            public JsonObject serialize(SendMessageReward reward) {
                JsonObject obj = new JsonObject();
                obj.addProperty("message", reward.message());
                return obj;
            }
        }
    }
}
