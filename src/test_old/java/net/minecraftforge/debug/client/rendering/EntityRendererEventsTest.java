/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Collections;

@Mod("entity_renderer_events_test")
@Mod.EventBusSubscriber(modid="entity_renderer_events_test", bus= Mod.EventBusSubscriber.Bus.MOD)
public class EntityRendererEventsTest
{
    private static final ResourceLocation MY_ENTITY = new ResourceLocation("entity_renderer_events_test", "test_entity");

    @ObjectHolder(registryName = "entity_type", value = "entity_renderer_events_test:test_entity")
    public static EntityType<MyEntity> MY_ENTITY_TYPE;

    @SubscribeEvent
    public static void entityRegistry(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.ENTITY_TYPES))
        {
            event.register(ForgeRegistries.Keys.ENTITY_TYPES, MY_ENTITY, () -> EntityType.Builder.of(MyEntity::new, MobCategory.MONSTER).build("test_entity"));
        }
    }
    
    @SubscribeEvent
    public static void entityRegistry(EntityAttributeCreationEvent event)
    {
        event.put(MY_ENTITY_TYPE, Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1.0D).build());
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
    private static class EntityRenderEventsTestClientModStuff
    {
        private static final ModelLayerLocation MAIN_LAYER = new ModelLayerLocation(MY_ENTITY, "main");
        private static final ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(MY_ENTITY, "main");
        private static final ModelLayerLocation ADDED_LAYER = new ModelLayerLocation(MY_ENTITY, "added");

        @SubscribeEvent
        public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            event.registerLayerDefinition(MAIN_LAYER, MyEntityModel::createMainLayer);
            event.registerLayerDefinition(OUTER_LAYER, () -> MyEntityModel.createLayer(new CubeDeformation(1.0f)));
            event.registerLayerDefinition(ADDED_LAYER, () -> MyEntityModel.createLayer(new CubeDeformation(2.0f)));
        }

        @SubscribeEvent
        public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event)
        {
            event.registerEntityRenderer(MY_ENTITY_TYPE, MyEntityRenderer::new);
        }

        @SubscribeEvent
        public static void entityLayers(EntityRenderersEvent.AddLayers event)
        {
            MyEntityRenderer renderer = event.getRenderer(MY_ENTITY_TYPE);
            renderer.addLayer(new MyEntityLayer(renderer, new MyEntityModel(event.getEntityModels().bakeLayer(ADDED_LAYER)), 0.5f));
        }

        private static class MyEntityModel extends EntityModel<MyEntity>
        {
            public static final String BODY = "body";
            public static final String HEAD = "head";

            public static LayerDefinition createMainLayer()
            {
                return createLayer(CubeDeformation.NONE);
            }

            public static LayerDefinition createLayer(CubeDeformation deformation)
            {
                MeshDefinition definition = new MeshDefinition();
                PartDefinition root = definition.getRoot();
                root.addOrReplaceChild(BODY, CubeListBuilder.create().addBox(-4,0,-4,4,10,4, deformation), PartPose.ZERO);
                root.addOrReplaceChild(HEAD, CubeListBuilder.create().addBox(-2,10,-2,2,4,2, deformation), PartPose.ZERO);
                return LayerDefinition.create(definition, 64, 32);
            }
            
            private final ModelPart headRenderer;
            private final ModelPart bodyRenderer;

            public MyEntityModel(ModelPart modelPart)
            {
                this.headRenderer = modelPart.getChild(HEAD);
                this.bodyRenderer = modelPart.getChild(BODY);
            }

            @Override
            public void setupAnim(MyEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_)
            {

            }

            @Override
            public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
            {
                headRenderer.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                bodyRenderer.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }
        
        private static class MyEntityRenderer extends LivingEntityRenderer<MyEntity, MyEntityModel>
        {
            private static final ResourceLocation TEXTURE = new ResourceLocation("entity_renderer_events_test:textures/entity/test_entity.png");
                    
            public MyEntityRenderer(EntityRendererProvider.Context context)
            {
                super(context, new MyEntityModel(context.bakeLayer(MAIN_LAYER)), 1.0f);
                addLayer(new MyEntityLayer(this, new MyEntityModel(context.bakeLayer(OUTER_LAYER)), 0.1f));
            }

            @Override
            public ResourceLocation getTextureLocation(MyEntity p_114482_)
            {
                return TEXTURE;
            }
        }
        
        private static class MyEntityLayer extends RenderLayer<MyEntity, MyEntityModel>
        {
            private final MyEntityModel model;
            private final float r;

            public MyEntityLayer(MyEntityRenderer renderer, MyEntityModel model, float r)
            {
                super(renderer);
                this.model = model;
                this.r = r;
            }

            @Override
            public void render(PoseStack poseStack, MultiBufferSource bufferSource, int lightness, MyEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
            {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(this.getParentModel().renderType(this.getTextureLocation(entity)));
                model.renderToBuffer(poseStack, vertexConsumer, lightness, OverlayTexture.NO_OVERLAY, r, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private static class MyEntity extends LivingEntity
    {
        protected MyEntity(EntityType<? extends LivingEntity> p_20966_, Level p_20967_)
        {
            super(p_20966_, p_20967_);
        }

        @Override
        public Iterable<ItemStack> getArmorSlots()
        {
            return Collections.emptyList();
        }

        @Override
        public ItemStack getItemBySlot(EquipmentSlot p_21127_)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_)
        {

        }

        @Override
        public HumanoidArm getMainArm()
        {
            return HumanoidArm.LEFT;
        }
    }    
}
