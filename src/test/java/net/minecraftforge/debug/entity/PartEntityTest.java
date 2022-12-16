/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity;

import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(value = PartEntityTest.MOD_ID)
public class PartEntityTest
{
    static final String MOD_ID = "part_entity_test";
    static final boolean ENABLED = true;

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    private static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITIES.register("test_entity", () -> EntityType.Builder.of(TestEntity::new, MobCategory.CREATURE).sized(16.0F, 8.0F).clientTrackingRange(10).build("test_entity"));

    public PartEntityTest()
    {
        if (ENABLED)
        {
            ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
        }
    }

    @SubscribeEvent
    public void onRegisterAttributes(final EntityAttributeCreationEvent event)
    {
        event.put(TEST_ENTITY.get(), Pig.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientEvents
    {
        @SubscribeEvent
        public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event)
        {
            if (!ENABLED) return;

            event.registerEntityRenderer(TEST_ENTITY.get(), PigRenderer::new);
        }
    }

    private static class TestEntity extends Pig
    {
        private final TestEntityPart[] subEntities;
        public final TestEntityPart head;
        private final TestEntityPart neck;
        private final TestEntityPart body;
        private final TestEntityPart tail1;
        private final TestEntityPart tail2;
        private final TestEntityPart tail3;
        private final TestEntityPart wing1;
        private final TestEntityPart wing2;

        public TestEntity(EntityType<? extends Pig> entity, Level world)
        {
            super(entity, world);
            this.head = new TestEntityPart(this, 1.0F, 1.0F);
            this.neck = new TestEntityPart(this, 3.0F, 3.0F);
            this.body = new TestEntityPart(this, 5.0F, 3.0F);
            this.tail1 = new TestEntityPart(this, 2.0F, 2.0F);
            this.tail2 = new TestEntityPart(this, 2.0F, 2.0F);
            this.tail3 = new TestEntityPart(this, 2.0F, 2.0F);
            this.wing1 = new TestEntityPart(this, 4.0F, 2.0F);
            this.wing2 = new TestEntityPart(this, 4.0F, 2.0F);
            this.subEntities = new TestEntityPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
        }

        @Override
        public void aiStep()
        {
            super.aiStep();

            for (TestEntityPart part : this.subEntities)
            {
                part.setPos(this.getX(), this.getY(), this.getZ());
            }

            Vec3[] vec3 = new Vec3[this.subEntities.length];
            for(int j = 0; j < this.subEntities.length; ++j)
            {
                vec3[j] = new Vec3(this.subEntities[j].getX(), this.subEntities[j].getY(), this.subEntities[j].getZ());
            }

            for(int l = 0; l < this.subEntities.length; ++l)
            {
                this.subEntities[l].xo = vec3[l].x;
                this.subEntities[l].yo = vec3[l].y;
                this.subEntities[l].zo = vec3[l].z;
                this.subEntities[l].xOld = vec3[l].x;
                this.subEntities[l].yOld = vec3[l].y;
                this.subEntities[l].zOld = vec3[l].z;
            }
        }

        @Override
        public boolean isMultipartEntity()
        {
            return true;
        }

        @Override
        public PartEntity<?>[] getParts()
        {
            return this.subEntities;
        }

        @Override
        public void recreateFromPacket(ClientboundAddEntityPacket packet)
        {
            super.recreateFromPacket(packet);
            PartEntity<?>[] parts = this.getParts();

            for(int i = 0; i < parts.length; ++i)
            {
                parts[i].setId(i + packet.getId());
            }

        }
    }

    private static class TestEntityPart extends PartEntity<TestEntity>
    {
        public final TestEntity parent;
        private final EntityDimensions size;

        public TestEntityPart(TestEntity parent, float width, float height)
        {
            super(parent);
            this.size = EntityDimensions.scalable(width, height);
            this.refreshDimensions();
            this.parent = parent;
        }

        protected void defineSynchedData() {}

        protected void readAdditionalSaveData(CompoundTag nbt) {}

        protected void addAdditionalSaveData(CompoundTag nbt) {}

        public boolean isPickable()
        {
            return true;
        }

        public boolean hurt(DamageSource source, float amount)
        {
            return !this.isInvulnerableTo(source) && this.parent.hurt(source, amount);
        }

        public boolean is(Entity entity)
        {
            return this == entity || this.parent == entity;
        }

        public Packet<ClientGamePacketListener> getAddEntityPacket()
        {
            throw new UnsupportedOperationException();
        }

        public EntityDimensions getDimensions(Pose matrix)
        {
            return this.size;
        }

        public boolean shouldBeSaved()
        {
            return false;
        }
    }
}
