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

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterSpawnEggsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(SuppliableSpawnEggsTest.MODID)
public class SuppliableSpawnEggsTest
{
    public static final String MODID = "suppliable_spawn_eggs_test";
    public static final String TEST_PIG_NAME = "test_pig";
    public static final String TEST_CHICKEN_NAME = "test_chicken";
    public static final String TEST_COW_NAME = "test_cow";
    
    // there are three existing standards for making spawn eggs that we should test for backwards compatibility with the new spawn egg logic
    // A) Extend SpawnEggItem to use a supplier and pass a null entity type to the base class's constructor. We will test this with a pig-type entity
    // B) preinit the entity type and just use SpawnEggItem as-is. We will test this with a chicken-like entity.
    // C) Reimplement spawn egg behavior using a new class that does not extend SpawnEggItem, ignoring the entitytype->SpawnEggItem map.
        // We won't test against this as we're only concerned with SpawnEggItem and its type map.
    // We'll also test a spawn egg that intentionally takes advantage of our new spawn egg patches with a cow-like entity. 
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final RegistryObject<EntityType<PigEntity>> TEST_PIG = ENTITIES.register(TEST_PIG_NAME, () -> EntityType.Builder.of(PigEntity::new, EntityClassification.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10).build(new ResourceLocation(MODID, TEST_PIG_NAME).toString()));
    private static final EntityType<ChickenEntity> PRE_TEST_CHICKEN = EntityType.Builder.of(ChickenEntity::new, EntityClassification.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10).build(new ResourceLocation(MODID, TEST_CHICKEN_NAME).toString());
    public static final RegistryObject<EntityType<ChickenEntity>> TEST_CHICKEN = ENTITIES.register(TEST_CHICKEN_NAME, () -> PRE_TEST_CHICKEN);
    public static final RegistryObject<EntityType<CowEntity>> TEST_COW = ENTITIES.register(TEST_COW_NAME, () -> EntityType.Builder.of(CowEntity::new, EntityClassification.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10).build(new ResourceLocation(MODID, TEST_COW_NAME).toString()));
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<SpawnEggItem> TEST_PIG_EGG = ITEMS.register(TEST_PIG_NAME + "_spawn_egg", () -> new PostHocSpawnEggItem(TEST_PIG, 0, 0xFFDDDD, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> TEST_CHICKEN_EGG = ITEMS.register(TEST_CHICKEN_NAME + "_spawn_egg", () -> new SpawnEggItem(PRE_TEST_CHICKEN, 0, 0xFF0000, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<ForgeSpawnEggItem> TEST_COW_EGG = ITEMS.register(TEST_COW_NAME + "_spawn_egg", () -> new ForgeSpawnEggItem(TEST_COW, 0, 0xDDCC55, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    
    public SuppliableSpawnEggsTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        
        // subscribe deferred registers
        ITEMS.register(modBus);
        ENTITIES.register(modBus);
        
        // subscribe events
        modBus.addListener(this::onRegisterEntityAttributes);
        modBus.addListener(this::onRegisterSpawnEggs);
        
        // classload-safely subscribe client events
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            SuppliableSpawnEggsTestClientEvents.subscribeEvents(modBus, forgeBus);
        }
    }
    
    private void onRegisterEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(TEST_PIG.get(), PigEntity.createAttributes().build());
        event.put(TEST_CHICKEN.get(), ChickenEntity.createAttributes().build());
        event.put(TEST_COW.get(), CowEntity.createAttributes().build());
    }
    
    private void onRegisterSpawnEggs(RegisterSpawnEggsEvent event)
    {
        event.getStandardModEggs().put(TEST_COW_EGG.getId(), TEST_COW_EGG);
    }
    
    public static class SuppliableSpawnEggsTestClientEvents
    {
        private static void subscribeEvents(IEventBus modBus, IEventBus forgeBus)
        {
            modBus.addListener(SuppliableSpawnEggsTestClientEvents::onClientSetup);
            modBus.addListener(SuppliableSpawnEggsTestClientEvents::onRegisterItemColors);
        }
        
        private static void onClientSetup(FMLClientSetupEvent event)
        {
            RenderingRegistry.registerEntityRenderingHandler(TEST_PIG.get(), PigRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(TEST_CHICKEN.get(), ChickenRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(TEST_COW.get(), CowRenderer::new);
        }
        
        private static void onRegisterItemColors(ColorHandlerEvent.Item event)
        {
            // null-type pigs must have color tinter manually registered
            event.getItemColors().register((stack,index) -> TEST_PIG_EGG.get().getColor(index), TEST_PIG_EGG.get());
            // preinit-type chickens' color tinter is automatically registered
        }
    }
    
    // One of the existing standards for registering spawn eggs: pass in a null type to the base class, but use a type supplier when queried
    // we use this here to test backwards-compatibility of the new spawn egg logic
    public static class PostHocSpawnEggItem extends SpawnEggItem
    {
        private final Supplier<? extends EntityType<?>> typeSupplier;
        public PostHocSpawnEggItem(Supplier<? extends EntityType<?>> typeSupplier, int bgcolor, int fgcolor, Properties properties)
        {
            super(null, bgcolor, fgcolor, properties);
            this.typeSupplier = typeSupplier;
        }
        @Override
        public EntityType<?> getType(CompoundNBT nbt)
        {
            // run base logic in case we have NBT to override the type
            @Nullable EntityType<?> baseType = super.getType(nbt);
            // otherwise get the type from our supplier
            return baseType != null ? baseType : typeSupplier.get();
        }
        
    }
}
