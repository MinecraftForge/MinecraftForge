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

package net.minecraftforge.debug.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(SpawnEggItemTest.MODID)
public class SpawnEggItemTest
{
    public static final String MODID = "spawnegg_item_test";

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final RegistryObject<EntityType<TestEntity>> ENTITY = ENTITIES.register("test_entity", () ->
            EntityType.Builder.of(TestEntity::new, EntityClassification.MISC).sized(1, 1).build("test_entity")
    );

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<SpawnEggItem> EGG = ITEMS.register("test_spawn_egg", () ->
            new SpawnEggItem(ENTITY, 0x0000FF, 0xFF0000, new Item.Properties().tab(ItemGroup.TAB_MISC))
    );

    public SpawnEggItemTest()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static class TestEntity extends Entity
    {
        public TestEntity(EntityType<? extends Entity> type, World worldIn) { super(type, worldIn); }

        @Override
        public boolean isPickable() { return true; }

        @Override
        protected void defineSynchedData() { }

        @Override
        protected void readAdditionalSaveData(CompoundNBT compound) { }

        @Override
        protected void addAdditionalSaveData(CompoundNBT compound) { }

        @Override
        public IPacket<?> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientEvents
    {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            EntityRendererManager rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
            rendererManager.register(ENTITY.get(), new EntityRenderer<TestEntity>(rendererManager)
            {
                @Override
                public ResourceLocation getTextureLocation(TestEntity entity) { return null; }
            });
        }
    }
}