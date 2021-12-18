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

package net.minecraftforge.debug.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.animation.AnimationData;
import net.minecraftforge.client.animation.AnimationKey;
import net.minecraftforge.client.animation.ModelAnimation;
import net.minecraftforge.client.animation.ModelTree;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("animate_entity_test")
public class AnimateEntityLivingTest
{
    private static final boolean ENABLED = true;

    public AnimateEntityLivingTest()
    {
        if (ENABLED && FMLEnvironment.dist == Dist.CLIENT)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAnimations);
        }
    }

    private void registerAnimations(EntityRenderersEvent.AddAnimations event)
    {
        this.setupPlayerEmoteAnimationTest(event);
        this.setupPassiveAnimationPriorityTest(event);
        this.setupZombieAnimationTest(event);
        this.setupArmorStandActiveAnimationTest(event);
        this.setupAnimationDataKeyTest(event);
    }

    private void setupPlayerEmoteAnimationTest(EntityRenderersEvent.AddAnimations event)
    {
        // Player waving animation when holding a cookie
        event.addAnimation(EntityType.PLAYER, new ModelAnimation<>(ModelAnimation.Mode.ACTIVE, ModelAnimation.Priority.FIRST)
        {
            @Override
            public boolean canStart(Player entity)
            {
                return entity.getMainHandItem().getItem() == Items.COOKIE;
            }

            @Override
            public void apply(Player player, ModelTree root, AnimationData data, float partialTick)
            {
                float angle = (float) Math.sin(player.tickCount + partialTick) * 20F;
                ModelPart rightArm = root.get("right_arm");
                rightArm.x -= 1;
                rightArm.zRot = (float) Math.toRadians(150F + angle);
                root.get("right_sleeve").copyFrom(rightArm);
            }
        });
    }

    /* Creates a test to demonstrate priority in passive animations */
    private void setupPassiveAnimationPriorityTest(EntityRenderersEvent.AddAnimations event)
    {
        // Custom player pose when riding a pig.
        event.addAnimation(EntityType.PLAYER, new ModelAnimation<>(ModelAnimation.Mode.PASSIVE, ModelAnimation.Priority.FIRST)
        {
            @Override
            public boolean canStart(Player entity)
            {
                return entity.getVehicle() != null && entity.getVehicle().getType() == EntityType.PIG;
            }

            @Override
            public void apply(Player player, ModelTree root, AnimationData data, float partialTick)
            {
                ModelPart leftLeg = root.get("left_leg");
                ModelPart rightLeg = root.get("right_leg");
                leftLeg.xRot = (float) Math.toRadians(-90F);
                rightLeg.xRot = (float) Math.toRadians(-90F);
                leftLeg.yRot = (float) Math.toRadians(-45F);
                rightLeg.yRot = (float) Math.toRadians(45F);
                leftLeg.zRot = 0;
                rightLeg.zRot = 0;
                root.get("left_pants").copyFrom(leftLeg);
                root.get("right_pants").copyFrom(rightLeg);
            }
        });

        // This should overlay an animation on top of the animation defined above
        event.addAnimation(EntityType.PLAYER, new ModelAnimation<>(ModelAnimation.Mode.PASSIVE, ModelAnimation.Priority.DEFAULT)
        {
            @Override
            public boolean canStart(Player entity)
            {
                return entity.getVehicle() != null && entity.getVehicle().getType() == EntityType.PIG && entity.getMainHandItem().getItem() == Items.PIG_SPAWN_EGG;
            }

            @Override
            public void apply(Player player, ModelTree root, AnimationData data, float partialTick)
            {
                ModelPart leftLeg = root.get("left_leg");
                ModelPart rightLeg = root.get("right_leg");
                leftLeg.yRot = (float) Math.toRadians(-90F);
                rightLeg.yRot = (float) Math.toRadians(90F);
                root.get("left_pants").copyFrom(leftLeg);
                root.get("right_pants").copyFrom(rightLeg);
            }
        });
    }

    private void setupZombieAnimationTest(EntityRenderersEvent.AddAnimations event)
    {
        /* A simple example of creating an animation for an entity. In this case, when a zombie is
         * on fire it will spin it's arms around. */
        event.addAnimation(EntityType.ZOMBIE, new ModelAnimation<>(ModelAnimation.Mode.PASSIVE, ModelAnimation.Priority.FIRST)
        {
            @Override
            public boolean canStart(Zombie entity)
            {
                return entity.isOnFire();
            }

            @Override
            public void apply(Zombie entity, ModelTree root, AnimationData data, float partialTick)
            {
                float rotation = (entity.tickCount + partialTick) * 20F;
                root.get("right_arm").xRot = (float) Math.toRadians(rotation);
                root.get("left_arm").xRot = (float) Math.toRadians(rotation + 180F);
            }
        });
    }

    // Creates a test to show an active animation preventing a later priority active animation
    private void setupArmorStandActiveAnimationTest(EntityRenderersEvent.AddAnimations event)
    {
        // Creates an active animation for an armour stand with a first priority
        event.addAnimation(EntityType.ARMOR_STAND, new ModelAnimation<>(ModelAnimation.Mode.ACTIVE, ModelAnimation.Priority.FIRST)
        {
            @Override
            public boolean canStart(ArmorStand entity)
            {
                return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.NETHERITE_HELMET;
            }

            @Override
            public void apply(ArmorStand entity, ModelTree root, AnimationData data, float partialTick)
            {
                // Makes the limbs move like creating an angel in snow
                float angle = (float) Math.sin(entity.tickCount + partialTick) * 20F;
                root.get("right_arm").zRot = (float) Math.toRadians(90F + angle);
                root.get("left_arm").zRot = (float) Math.toRadians(-90F + angle);
                root.get("right_leg").zRot = (float) Math.toRadians(25F + angle);
                root.get("left_leg").zRot = (float) Math.toRadians(-25F + angle);
            }
        });

        /* Another active animation for the armour stand with the same canRun condition. Since the
         * animation created above will run first due to it's first priority, this animation will
         * never run as default priority runs after first priority */
        event.addAnimation(EntityType.ARMOR_STAND, new ModelAnimation<>(ModelAnimation.Mode.ACTIVE, ModelAnimation.Priority.DEFAULT)
        {
            @Override
            public boolean canStart(ArmorStand entity)
            {
                return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.NETHERITE_HELMET;
            }

            @Override
            public void apply(ArmorStand entity, ModelTree root, AnimationData data, float partialTick)
            {
                // This code will never be executed
                root.get("right_arm").zRot = 0;
                root.get("left_arm").zRot = 0;
            }
        });
    }

    private void setupAnimationDataKeyTest(EntityRenderersEvent.AddAnimations event)
    {
        event.addAnimation(EntityType.PLAYER, new ModelAnimation<>(ModelAnimation.Mode.ACTIVE, ModelAnimation.Priority.FIRST)
        {
            @Override
            public boolean canStart(Player entity)
            {
                return entity.getMainHandItem().getItem() == Items.ARROW;
            }

            @Override
            public void apply(Player entity, ModelTree root, AnimationData data, float partialTick)
            {
                float movement = data.get(AnimationKey.MOVEMENT_TICKS);
                root.get("right_arm").xRot = (float) Math.toRadians(movement * 90F);
                root.get("left_arm").xRot = (float) Math.toRadians(movement * 90F + 180F);
            }
        });
    }
}
