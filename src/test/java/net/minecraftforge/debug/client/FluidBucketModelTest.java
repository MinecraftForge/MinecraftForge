/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

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
import net.minecraftforge.client.event.ModelEvent.BakeFluidModels;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(FluidBucketModelTest.MODID)
public class FluidBucketModelTest extends BaseTestMod {
    public static final String MODID = "fluid_bucket_model";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.FLUID_TYPES, MODID);


    private static ForgeFlowingFluid.Properties FLUID_PROPERTIES;
    private static final RegistryObject<ForgeFlowingFluid.Source>  GAS_STILL   = FLUIDS.register("gas",         () -> new ForgeFlowingFluid.Source(FLUID_PROPERTIES));
    private static final RegistryObject<ForgeFlowingFluid.Flowing> GAS_FLOWING = FLUIDS.register("gas_flowing", () -> new ForgeFlowingFluid.Flowing(FLUID_PROPERTIES));

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
        FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(GAS_TYPE, GAS_STILL, GAS_FLOWING).block(GAS_BLOCK).bucket(GAS_BUCKET);
    }


    public static final RegistryObject<Item> BUCKET = ITEMS.register("bucket", () -> new BucketItem(() -> Fluids.LAVA, new Item.Properties().setId(ITEMS.key("bucket"))));

    public FluidBucketModelTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        this.testItem(_ -> BUCKET.get().getDefaultInstance());
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
