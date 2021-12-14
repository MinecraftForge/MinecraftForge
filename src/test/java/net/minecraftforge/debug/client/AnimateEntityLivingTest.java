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

import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.animation.EntityAnimation;
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
        this.setupArmorStandAnimationTest(event);
    }

    private void setupPlayerEmoteAnimationTest(EntityRenderersEvent.AddAnimations event)
    {
        // Player waving animation when holding a cookie
        event.addAnimation(EntityType.PLAYER, new EntityAnimation<>(EntityAnimation.Mode.ACTIVE, EntityAnimation.Priority.FIRST)
        {
            @Override
            public boolean canRun(Player entity)
            {
                return entity.getMainHandItem().getItem() == Items.COOKIE;
            }

            @Override
            public void apply(Player player, EntityModel<Player> model, Context context)
            {
                if (model instanceof PlayerModel<?> playerModel)
                {
                    float angle = (float) Math.sin(player.tickCount + context.partialTicks()) * 20F;
                    playerModel.rightArm.x -= 1;
                    playerModel.rightArm.zRot = (float) Math.toRadians(150F + angle);
                    playerModel.rightSleeve.copyFrom(playerModel.rightArm);
                }
            }
        });
    }

    /* Creates a test to demonstrate priority in passive animations */
    private void setupPassiveAnimationPriorityTest(EntityRenderersEvent.AddAnimations event)
    {
        // Custom player pose when riding a pig.
        event.addAnimation(EntityType.PLAYER, new EntityAnimation<>(EntityAnimation.Mode.PASSIVE, EntityAnimation.Priority.FIRST)
        {
            @Override
            public boolean canRun(Player entity)
            {
                return entity.getVehicle() != null && entity.getVehicle().getType() == EntityType.PIG;
            }

            @Override
            public void apply(Player player, EntityModel<Player> model, Context context)
            {
                if (model instanceof PlayerModel<?> playerModel)
                {
                    playerModel.leftLeg.xRot = (float) Math.toRadians(-90F);
                    playerModel.rightLeg.xRot = (float) Math.toRadians(-90F);
                    playerModel.leftLeg.yRot = (float) Math.toRadians(-45F);
                    playerModel.rightLeg.yRot = (float) Math.toRadians(45F);
                    playerModel.leftLeg.zRot = 0;
                    playerModel.rightLeg.zRot = 0;
                    playerModel.leftPants.copyFrom(playerModel.leftLeg);
                    playerModel.rightPants.copyFrom(playerModel.rightLeg);
                }
            }
        });

        // This should overlay an animation on top of the animation defined above
        event.addAnimation(EntityType.PLAYER, new EntityAnimation<>(EntityAnimation.Mode.PASSIVE, EntityAnimation.Priority.DEFAULT)
        {
            @Override
            public boolean canRun(Player entity)
            {
                return entity.getVehicle() != null && entity.getVehicle().getType() == EntityType.PIG && entity.getMainHandItem().getItem() == Items.PIG_SPAWN_EGG;
            }

            @Override
            public void apply(Player player, EntityModel<Player> model, Context context)
            {
                if (model instanceof PlayerModel<?> playerModel)
                {
                    playerModel.leftLeg.yRot = (float) Math.toRadians(-90F);
                    playerModel.rightLeg.yRot = (float) Math.toRadians(90F);
                    playerModel.leftPants.copyFrom(playerModel.leftLeg);
                    playerModel.rightPants.copyFrom(playerModel.rightLeg);
                }
            }
        });
    }

    private void setupZombieAnimationTest(EntityRenderersEvent.AddAnimations event)
    {
        event.addAnimation(EntityType.ZOMBIE, new EntityAnimation<>(EntityAnimation.Mode.PASSIVE, EntityAnimation.Priority.FIRST)
        {
            @Override
            public boolean canRun(Zombie entity)
            {
                return entity.isOnFire();
            }

            @Override
            public void apply(Zombie entity, EntityModel<Zombie> model, Context context)
            {
                if (model instanceof ZombieModel<?> zombieModel)
                {
                    float rotation = (entity.tickCount + context.partialTicks()) * 20F;
                    zombieModel.rightArm.xRot = (float) Math.toRadians(rotation);
                    zombieModel.leftArm.xRot = (float) Math.toRadians(rotation + 180F);
                }
            }
        });
    }

    // Creates a test to show active animations cancelling later priority active animations
    private void setupArmorStandAnimationTest(EntityRenderersEvent.AddAnimations event)
    {
        event.addAnimation(EntityType.ARMOR_STAND, new EntityAnimation<>(EntityAnimation.Mode.ACTIVE, EntityAnimation.Priority.FIRST)
        {
            @Override
            public boolean canRun(ArmorStand entity)
            {
                return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.NETHERITE_HELMET;
            }

            @Override
            public void apply(ArmorStand entity, EntityModel<ArmorStand> model, Context context)
            {
                if (model instanceof ArmorStandModel standModel)
                {
                    float angle = (float) Math.sin(entity.tickCount + context.partialTicks()) * 20F;
                    standModel.rightArm.zRot = (float) Math.toRadians(90F + angle);
                    standModel.leftArm.zRot = (float) Math.toRadians(-90F + angle);
                    standModel.rightLeg.zRot = (float) Math.toRadians(25F + angle);
                    standModel.leftLeg.zRot = (float) Math.toRadians(-25F + angle);
                }
            }
        });

        // This should never be able to execute since the active animation above is running
        event.addAnimation(EntityType.ARMOR_STAND, new EntityAnimation<>(EntityAnimation.Mode.ACTIVE, EntityAnimation.Priority.DEFAULT)
        {
            @Override
            public boolean canRun(ArmorStand entity)
            {
                return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.NETHERITE_HELMET;
            }

            @Override
            public void apply(ArmorStand entity, EntityModel<ArmorStand> model, Context context)
            {
                if (model instanceof ArmorStandModel standModel)
                {
                    standModel.rightArm.zRot = 0;
                    standModel.leftArm.zRot = 0;
                }
            }
        });
    }
}
