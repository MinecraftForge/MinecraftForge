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

package net.minecraftforge.debug;

import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.UsedEnderEyeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.advancements.ICustomAdvancementReward;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

@Mod("custom_advancement_rewards_test")
public class CustomAdvancementRewardsTest
{
    private static final DeferredRegister<ICustomAdvancementReward.Serializer<?>> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.CUSTOM_ADVANCEMENT_REWARD_SERIALIZERS, "custom_advancement_rewards_test");
    private static final RegistryObject<SendMessageReward.Serializer> SEND_MESSAGE = DEFERRED_REGISTER.register("send_message", SendMessageReward.Serializer::new);

    public CustomAdvancementRewardsTest()
    {
        DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::datagen);
    }

    private void datagen(GatherDataEvent event)
    {
        if (event.includeServer())
        {
            event.getGenerator().addProvider(new Advancements(event.getGenerator(), event.getExistingFileHelper()));
        }
    }

    public record SendMessageReward(String message) implements ICustomAdvancementReward
    {
        @Override
        public void grant(ServerPlayer player)
        {
            player.sendMessage(new TextComponent(this.message()), Util.NIL_UUID);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Serializer getSerializer()
        {
            return SEND_MESSAGE.get();
        }

        public static class Serializer extends ICustomAdvancementReward.Serializer<SendMessageReward>
        {

            @Override
            public SendMessageReward deserialize(JsonObject json)
            {
                return new SendMessageReward(GsonHelper.getAsString(json, "message"));
            }

            @Override
            public JsonObject serialize(SendMessageReward reward)
            {
                JsonObject obj = new JsonObject();
                obj.addProperty("message", reward.message());
                return obj;
            }
        }
    }

    private static class Advancements extends AdvancementProvider
    {
        public Advancements(DataGenerator generator, ExistingFileHelper helper)
        {
            super(generator, helper);
        }

        @Override
        protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper)
        {
            Advancement.Builder.advancement()
                    .display(Items.ENDER_EYE,
                            new TranslatableComponent("advancements.test.root.title"),
                            new TranslatableComponent("advancements.test.root.description"),
                            new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"),
                            FrameType.CHALLENGE,
                            true,
                            false,
                            false)
                    .addCriterion("use_ender_eye",
                            new UsedEnderEyeTrigger.TriggerInstance(
                                    EntityPredicate.Composite.ANY,
                                    MinMaxBounds.Doubles.ANY))
                    .requirements(RequirementsStrategy.OR)
                    .rewards(AdvancementRewards.Builder.custom(new SendMessageReward("Hello World!")))
                    .save(consumer,
                            new ResourceLocation("custom_advancement_rewards_test", "test/root"),
                            fileHelper);
        }
    }
}
