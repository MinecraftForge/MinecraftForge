/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

/**
 * Tests if item usage animation system works as intended. `item_use_animation_test:thing` is edible item with custom usage animation made with this system.
 * In game, use `/give @s item_use_animation_test:thing 1` to obtain test item
 * When you try to eat it, your arm in 3d person should start swinging really fast.
 * And item in your hand will go down little.
 */
@Mod(ItemUseAnimationTest.MOD_ID)
public class ItemUseAnimationTest
{

    public static final String MOD_ID = "item_use_animation_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Item> THING = ITEMS.register("thing", () -> new ThingItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).alwaysEat().build())));

    public ItemUseAnimationTest()
    {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
            event.accept(THING);
    }

    private static class ThingItem extends Item
    {

        public ThingItem(Item.Properties props)
        {
            super(props);
        }

        @Override
        public UseAnim getUseAnimation(ItemStack stack)
        {
            return UseAnim.CUSTOM;
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer)
        {
            consumer.accept(new IClientItemExtensions()
            {

                private static final HumanoidModel.ArmPose SWING_POSE = HumanoidModel.ArmPose.create("SWING", false, (model, entity, arm) -> {
                    if (arm == HumanoidArm.RIGHT)
                    {
                        model.rightArm.xRot = (float) (Math.random() * Math.PI * 2);
                    } else
                    {
                        model.leftArm.xRot = (float) (Math.random() * Math.PI * 2);
                    }
                });

                @Override
                public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack)
                {
                    if (!itemStack.isEmpty())
                    {
                        if (entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0)
                        {
                            return SWING_POSE;
                        }
                    }
                    return HumanoidModel.ArmPose.EMPTY;
                }

                @Override
                public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                    applyItemArmTransform(poseStack, arm);
                    if (player.getUseItem() != itemInHand) {
                        return true;
                    }
                    if (player.isUsingItem()) {
                        poseStack.translate(0.0, -0.05, 0.0);
                    }
                    return true;
                }

                private void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm) {
                    int i = arm == HumanoidArm.RIGHT ? 1 : -1;
                    poseStack.translate(i * 0.56F, -0.52F, -0.72F);
                }
            });
        }
    }

}