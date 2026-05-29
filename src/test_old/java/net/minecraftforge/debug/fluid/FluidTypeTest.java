/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftsingularity.client.event.RegisterColorHandlersEvent;
import net.minecraftsingularity.common.singularityMod;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.fluids.FluidInteractionRegistry;
import net.minecraftsingularity.fluids.FluidType;
import net.minecraftsingularity.fluids.singularityFlowingFluid;
import net.minecraftsingularity.fml.DistExecutor;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftsingularity.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A test case used to define and test fluid type integration into fluids.
 *
 * <ul>
 *     <li>Checks that each fluid has a fluid type</li>
 *     <li>Adds a new fluid with its type, source/flowing, block, and bucket</li>
 *     <li>Sets properties to test out fluid logic</li>
 *     <li>Overrides fluid rendering methods</li>
 *     <li>Adds block color and render layer</li>
 *     <li>Adds fluid interaction definitions</li>
 * </ul>
 */
@Mod(FluidTypeTest.ID)
public class FluidTypeTest
{
    private static final boolean ENABLE = true;

    protected static final String ID = "fluid_type_test";
    private static Logger logger;

    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(singularityRegistries.Keys.FLUID_TYPES, ID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(singularityRegistries.FLUIDS, ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(singularityRegistries.BLOCKS, ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, ID);

    private static singularityFlowingFluid.Properties fluidProperties()
    {
        return new singularityFlowingFluid.Properties(TEST_FLUID_TYPE, TEST_FLUID, TEST_FLUID_FLOWING)
                .block(TEST_FLUID_BLOCK)
                .bucket(TEST_FLUID_BUCKET);
    }

    private static final RegistryObject<FluidType> TEST_FLUID_TYPE = FLUID_TYPES.register("test_fluid", () ->
            new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(true))
            {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        private static final ResourceLocation STILL = new ResourceLocation("block/water_still"),
                                FLOW = new ResourceLocation("block/water_flow"),
                                OVERLAY = new ResourceLocation("block/obsidian"),
                                VIEW_OVERLAY = new ResourceLocation("textures/block/obsidian.png");

                        @Override
                        public ResourceLocation getStillTexture()
                        {
                            return STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture()
                        {
                            return FLOW;
                        }

                        @Override
                        public ResourceLocation getOverlayTexture()
                        {
                            return OVERLAY;
                        }

                        @Override
                        public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                        {
                            return VIEW_OVERLAY;
                        }

                        @Override
                        public int getTintColor()
                        {
                            return 0xAF7FFFD4;
                        }

                        @Override
                        public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
                        {
                            int color = this.getTintColor();
                            return new Vector3f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F);
                        }

                        @Override
                        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
                        {
                            nearDistance = -8F;
                            farDistance = 24F;

                            if (farDistance > renderDistance)
                            {
                                farDistance = renderDistance;
                                shape = FogShape.CYLINDER;
                            }

                            RenderSystem.setShaderFogStart(nearDistance);
                            RenderSystem.setShaderFogEnd(farDistance);
                            RenderSystem.setShaderFogShape(shape);
                        }
                    });
                }
            });
    private static final RegistryObject<FlowingFluid> TEST_FLUID = FLUIDS.register("test_fluid", () ->
            new singularityFlowingFluid.Source(fluidProperties()));
    private static final RegistryObject<Fluid> TEST_FLUID_FLOWING = FLUIDS.register("test_fluid_flowing", () ->
            new singularityFlowingFluid.Flowing(fluidProperties()));
    private static final RegistryObject<LiquidBlock> TEST_FLUID_BLOCK = BLOCKS.register("test_fluid_block", () ->
            new LiquidBlock(TEST_FLUID, BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable()));
    private static final RegistryObject<Item> TEST_FLUID_BUCKET = ITEMS.register("test_fluid_bucket", () ->
            new BucketItem(TEST_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public FluidTypeTest()
    {
        if (ENABLE)
        {
            logger = LogManager.getLogger();
            singularityMod.enableMilkFluid();

            var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

            FLUID_TYPES.register(modEventBus);
            FLUIDS.register(modEventBus);
            BLOCKS.register(modEventBus);
            ITEMS.register(modEventBus);

            modEventBus.addListener(this::commonSetup);
            modEventBus.addListener(this::addCreative);

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new FluidTypeTestClient(modEventBus));
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(TEST_FLUID_BUCKET);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        // Add Interactions for sources
        FluidInteractionRegistry.addInteraction(TEST_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(singularityMod.LAVA_TYPE.get(), Blocks.GOLD_BLOCK.defaultBlockState()));
        FluidInteractionRegistry.addInteraction(singularityMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(TEST_FLUID_TYPE.get(), state -> state.isSource() ? Blocks.DIAMOND_BLOCK.defaultBlockState() : Blocks.IRON_BLOCK.defaultBlockState()));

        // Log Fluid Types for all fluids
        event.enqueueWork(() ->
                singularityRegistries.FLUIDS.forEach(fluid ->
                        logger.info("Fluid {} has FluidType {}", singularityRegistries.FLUIDS.getKey(fluid), singularityRegistries.FLUID_TYPES.get().getKey(fluid.getFluidType())))
        );
    }

    private static class FluidTypeTestClient
    {
        private FluidTypeTestClient(IEventBus modEventBus)
        {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerBlockColors);
        }

        private void clientSetup(FMLClientSetupEvent event)
        {
            Stream.of(TEST_FLUID, TEST_FLUID_FLOWING).map(RegistryObject::get)
                    .forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent()));
        }

        private void registerBlockColors(RegisterColorHandlersEvent.Block event)
        {
            event.register((state, getter, pos, index) ->
            {
                if (getter != null && pos != null)
                {
                    FluidState fluidState = getter.getFluidState(pos);
                    return IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, getter, pos);
                } else return 0xAF7FFFD4;
            }, TEST_FLUID_BLOCK.get());
        }
    }
}
