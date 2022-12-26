/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.data.RegistryAttachmentProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.attachment.AddBuiltInRegistryAttachmentsEvent;
import net.minecraftforge.registries.attachment.IRegistryAttachmentType;

import java.util.Optional;

@Mod("attachment_test")
public class RegistryAttachmentTest
{
    public static final boolean ENABLED = true;

    private static final DeferredRegister<IRegistryAttachmentType<?>> TYPES = DeferredRegister.create(ForgeRegistries.Keys.REGISTRY_ATTACHMENT_TYPES, "attachment_test");
    private static final Codec<MobEffectInstance> MOB_EFFECT_CODEC = RecordCodecBuilder.create(in -> in.group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
            Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
            Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance::getAmplifier)
    ).apply(in, MobEffectInstance::new));

    public static final ResourceKey<IRegistryAttachmentType<MobEffectInstance>> RIGHT_CLICK_EFFECT = TYPES.register("right_click_effect", () ->
            IRegistryAttachmentType.builder(MOB_EFFECT_CODEC).withNetworkCodec(MOB_EFFECT_CODEC).attachOnRegistry(Registries.ITEM).build()).getKey();

    public RegistryAttachmentTest()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TYPES.register(bus);

        bus.addListener((final GatherDataEvent event) ->
        {
            event.getGenerator().addProvider(true, RegistryAttachmentProvider.forRegistry(
                    event.getGenerator().getPackOutput(),
                    Registries.ITEM,
                    RIGHT_CLICK_EFFECT,
                    false, BuiltInRegistries.ITEM,
                    builder -> builder.add(Items.CRAFTING_TABLE, new MobEffectInstance(MobEffects.BLINDNESS, 90))
                            .add(Items.ACACIA_FENCE, new MobEffectInstance(MobEffects.CONFUSION, 6))
                            .add(Items.CACTUS, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 3), new ModLoadedCondition("not_a_mod"))
            ));
        });

        MinecraftForge.EVENT_BUS.addListener((final AddBuiltInRegistryAttachmentsEvent event) -> event.register(Registries.ITEM, RIGHT_CLICK_EFFECT, (registry, holder) -> {
            holder.attach(registry.getHolderOrThrow(ResourceKey.create(Registries.ITEM, new ResourceLocation("polished_granite"))), new MobEffectInstance(MobEffects.HARM));
        }));

        if (ENABLED) {
            MinecraftForge.EVENT_BUS.addListener((final PlayerInteractEvent.RightClickItem event) ->
            {
                if (event.getSide().isServer()) {
                    Optional.ofNullable(ForgeRegistries.ITEMS.attachment(RIGHT_CLICK_EFFECT).get(event.getItemStack().getItem()))
                            .ifPresent(effect -> event.getEntity().addEffect(effect));
                }
            });

            MinecraftForge.EVENT_BUS.addListener((final ItemTooltipEvent event) ->
                    Optional.ofNullable(ForgeRegistries.ITEMS.attachment(RIGHT_CLICK_EFFECT).get(event.getItemStack().getItem()))
                            .ifPresent(effect -> event.getToolTip().add(Component.literal("When clicked, adds effect: ")
                                    .append(Component.translatable(effect.getDescriptionId())))));
        }
    }

}
