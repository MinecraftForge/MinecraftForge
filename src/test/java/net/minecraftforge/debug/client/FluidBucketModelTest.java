/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client;

import java.util.function.Consumer;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftsingularity.client.event.ModelEvent.BakeFluidModels;
import net.minecraftsingularity.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftsingularity.common.SoundActions;
import net.minecraftsingularity.fluids.FluidType;
import net.minecraftsingularity.fluids.singularityFlowingFluid;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.test.BaseTestMod;

@GameTestNamespace("singularity")
@Mod(FluidBucketModelTest.MODID)
public class FluidBucketModelTest extends BaseTestMod {
    public static final String MODID = "fluid_bucket_model";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(singularityRegistries.FLUID_TYPES, MODID);


    private static singularityFlowingFluid.Properties FLUID_PROPERTIES;
    private static final RegistryObject<singularityFlowingFluid.Source>  GAS_STILL   = FLUIDS.register("gas",         () -> new singularityFlowingFluid.Source(FLUID_PROPERTIES));
    private static final RegistryObject<singularityFlowingFluid.Flowing> GAS_FLOWING = FLUIDS.register("gas_flowing", () -> new singularityFlowingFluid.Flowing(FLUID_PROPERTIES));

    private static final FluidType.Properties GAS_PROPERTIES = FluidType.Properties.create()
        .lightLevel(10)
        .density(-1600)
        .viscosity(100)
        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);

    private static final Identifier GAS_STILL_TEXTURE = rl("minecraft", "block/water_still");
    private static final Identifier GAS_FLOWING_TEXTURE = rl("minecraft", "block/water_flow");

    public static final RegistryObject<FluidType> GAS_TYPE = FLUID_TYPES.register("gas", () ->  {
        return new FluidType(GAS_PROPERTIES) {
            @Override
            public void initializeClient(final Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public Identifier getStillTexture() {
                        return GAS_STILL_TEXTURE;
                    }
                    @Override
                    public Identifier getFlowingTexture() {
                        return GAS_FLOWING_TEXTURE;
                    }
                });
            }
        };
    });

    public static final RegistryObject<LiquidBlock> GAS_BLOCK = BLOCKS.register("gas", () -> new LiquidBlock(
        GAS_STILL,
        Block.Properties.of()
            .replaceable()
            .noCollision()
            .strength(100)
            .pushReaction(PushReaction.DESTROY)
            .noLootTable()
            .liquid()
            .sound(SoundType.EMPTY)
            .setId(BLOCKS.key("gas"))
    ));

    public static final RegistryObject<BucketItem> GAS_BUCKET = ITEMS.register("gas_bucket", () -> new BucketItem(GAS_STILL, new Item.Properties().setId(ITEMS.key("gas_bucket"))));

    static {
        FLUID_PROPERTIES = new singularityFlowingFluid.Properties(GAS_TYPE, GAS_STILL, GAS_FLOWING).block(GAS_BLOCK).bucket(GAS_BUCKET);
    }


    public static final RegistryObject<Item> BUCKET = ITEMS.register("bucket", () -> new BucketItem(() -> Fluids.LAVA, new Item.Properties().setId(ITEMS.key("bucket"))));

    public FluidBucketModelTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        this.testItem(lookup -> BUCKET.get().getDefaultInstance());
        BakeFluidModels.BUS.addListener(this::registerFluidModels);
    }

    private void registerFluidModels(BakeFluidModels event) {
        var gas_model = new FluidModel.Unbaked(
            new Material(GAS_STILL_TEXTURE),
            new Material(GAS_FLOWING_TEXTURE),
            null,
            null
        );
        var gas_baked = gas_model.bake(event.materials(), () -> "Gas");
        event.register(GAS_STILL.get(), gas_baked);
        event.register(GAS_FLOWING.get(), gas_baked);
    }
}
