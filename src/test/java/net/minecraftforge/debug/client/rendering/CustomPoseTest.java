/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.model.pose.IPose;
import net.minecraftforge.client.model.pose.PosePriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CustomPoseTest.MODID)
public class CustomPoseTest {
    private static final boolean ENABLED = true;
    public static final String MODID = "forgedebugcustompose";
    public static final String VERSION = "0.0";

    public CustomPoseTest()
    {
        if (!ENABLED)
            return;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::registerPoses);
    }

    private void registerPoses(EntityRenderersEvent.AddLayers event)
    {
        event.registerPose(new LeatherArmorWavePose(), PosePriority.NORMAL, false);
        event.registerPose(new TPose(), PosePriority.NORMAL, true);
    }

    private static final class LeatherArmorWavePose implements IPose {

        @Override
        public boolean isActive(LivingEntity entity)
        {
            return entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.LEATHER_CHESTPLATE);
        }

        @Override
        public <T extends LivingEntity> void updatePose(T entity, PoseStack stack, EntityModel<T> model, float partialTicks) {
            if (model instanceof HumanoidModel<T> humanoidModel)
            {
                humanoidModel.rightArm.zRot = (float)Math.toRadians(180d + Mth.sin((entity.tickCount + partialTicks)/2) *10);
                humanoidModel.rightArm.xRot = 0;
                if (model instanceof PlayerModel<T> playerModel)
                {
                    playerModel.rightSleeve.zRot = (float)Math.toRadians(180d + Mth.sin((entity.tickCount + partialTicks)/2) *10);
                    playerModel.rightSleeve.xRot = 0;
                }
            }
        }
    }
    private static final class TPose implements IPose
    {

        @Override
        public boolean isActive(LivingEntity entity)
        {
            return entity.getMainHandItem().is(Items.ACACIA_SLAB);
        }

        @Override
        public <T extends LivingEntity> void updatePose(T entity, PoseStack stack, EntityModel<T> model, float partialTicks)
        {
            if (model instanceof HumanoidModel<T> humanoidModel)
            {
                humanoidModel.rightArm.loadPose(humanoidModel.rightArm.getInitialPose());
                humanoidModel.leftArm.loadPose(humanoidModel.leftArm.getInitialPose());
                humanoidModel.head.loadPose(humanoidModel.head.getInitialPose());
                humanoidModel.hat.loadPose(humanoidModel.hat.getInitialPose());
                humanoidModel.rightArm.zRot = (float)Math.toRadians(90);
                humanoidModel.leftArm.zRot = (float)Math.toRadians(-90);
                if (model instanceof PlayerModel<T> playerModel)
                {
                    playerModel.rightSleeve.loadPose(playerModel.rightSleeve.getInitialPose());
                    playerModel.leftSleeve.loadPose(playerModel.leftSleeve.getInitialPose());
                    playerModel.rightSleeve.zRot = (float)Math.toRadians(90);
                    playerModel.leftSleeve.zRot = (float)Math.toRadians(-90);
                }
            }
        }
    }
}
