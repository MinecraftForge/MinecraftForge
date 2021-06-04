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

package net.minecraftforge.debug.errors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EntityRendererValidatorTest.MOD_ID)
public class EntityRendererValidatorTest
{
    public static final String MOD_ID = "entity_renderer_validator_test";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    public static final RegistryObject<EntityType<NoRendererEntity>> NO_RENDERER_ENTITY = ENTITIES.register("no_renderer_entity",
            () -> EntityType.Builder.of(NoRendererEntity::new, EntityClassification.MISC)
            .noSave()
            .noSummon()
            .build("no_renderer_entity"));

    public EntityRendererValidatorTest() { ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus()); }

    public static class NoRendererEntity extends Entity
    {
        public NoRendererEntity(EntityType<? extends NoRendererEntity> type, World world) { super(type, world); }

        @Override
        protected void defineSynchedData() {}

        @Override
        protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {}

        @Override
        protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {}

        @Override
        public IPacket<?> getAddEntityPacket() { return new SSpawnObjectPacket(this); }
    }
}
