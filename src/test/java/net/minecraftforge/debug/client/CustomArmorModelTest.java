/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@Mod(CustomArmorModelTest.MOD_ID)
public class CustomArmorModelTest
{
    static final String MOD_ID = "custom_armor_model_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    // demonstrates custom non-humanoid model
    private static final RegistryObject<Item> RED_LEGGINGS = ITEMS.register("red_leggings", () -> new TintedArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Properties().stacksTo(1)));
    // demonstrates the properties are copied from the vanilla model
    private static final RegistryObject<Item> ENDERMAN_CHESTPLATE = ITEMS.register("enderman_chestplate", () -> new EndermanArmorItem(ArmorMaterials.GOLD, ArmorItem.Type.CHESTPLATE, new Properties().stacksTo(1)));
    private static final RegistryObject<Item> ENDERMAN_BOOTS = ITEMS.register("enderman_boots", () -> new EndermanArmorItem(ArmorMaterials.GOLD, ArmorItem.Type.BOOTS, new Properties().stacksTo(1)));

    public CustomArmorModelTest()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {
            event.accept(RED_LEGGINGS);
            event.accept(ENDERMAN_CHESTPLATE);
            event.accept(ENDERMAN_BOOTS);
        }
    }

    private static class TintedArmorItem extends ArmorItem
    {
        public TintedArmorItem(ArmorMaterial material, ArmorItem.Type slot, Properties props)
        {
            super(material, slot, props);
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer)
        {
            consumer.accept(new IClientItemExtensions()
            {
                @Override @NotNull
                public Model getGenericArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default)
                {
                    TintedArmorModel.INSTANCE.base = _default;
                    return TintedArmorModel.INSTANCE;
                }
            });
        }
    }

    private static class EndermanArmorItem extends ArmorItem
    {
        public EndermanArmorItem(ArmorMaterial material, ArmorItem.Type slot, Properties props)
        {
            super(material, slot, props);
        }

        @Nullable
        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
        {
            return "textures/entity/enderman/enderman.png";
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer)
        {
            consumer.accept(new IClientItemExtensions()
            {
                @Override
                public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default)
                {
                    return TintedArmorModel.ENDERMAN.get();
                }
            });
        }
    }

    private static class TintedArmorModel extends Model
    {
        private static final TintedArmorModel INSTANCE = new TintedArmorModel(RenderType::entityCutoutNoCull);
        private static final Lazy<HumanoidModel<LivingEntity>> ENDERMAN = Lazy.of(() -> new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ENDERMAN)));

        private HumanoidModel<?> base;
        private TintedArmorModel(Function<ResourceLocation,RenderType> renderTypeFunction)
        {
            super(renderTypeFunction);
        }

        @Override
        public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha)
        {
            if (base != null)
            {
                base.renderToBuffer(poseStack, consumer, light, overlay, red, 0, 0, alpha);
            }
        }
    }
}
