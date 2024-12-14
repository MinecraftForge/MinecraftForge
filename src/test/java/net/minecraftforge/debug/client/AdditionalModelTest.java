/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;

@Mod(AdditionalModelTest.MODID)
@GameTestHolder("forge." + AdditionalModelTest.MODID)
public class AdditionalModelTest extends BaseTestMod {
    public static final String MODID = "additional_model";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    private static final RegistryObject<Block> PIG_HEAD = BLOCKS.register("pig_head", () -> new Block(BlockBehaviour.Properties.of().setId(BLOCKS.key("pig_head"))));
    private static final RegistryObject<Block> COW_HEAD = BLOCKS.register("cow_head", () -> new Block(BlockBehaviour.Properties.of().setId(BLOCKS.key("cow_head"))));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    private static final RegistryObject<Item> PIG_HEAD_ITEM = ITEMS.register("pig_head", () -> new Item(new Item.Properties().setId(ITEMS.key("pig_head"))));

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public AdditionalModelTest(FMLJavaModLoadingContext context) {
        super(context, false);

        modBus.register(this);

        if (FMLLoader.getLaunchHandler().isData()) {
            BLOCKS.register(modBus);
            ITEMS.register(modBus);
        }
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(event.includeClient(), new ModelProvider(out));
    }


    @SubscribeEvent
    public void onRegisterAdditional(ModelEvent.RegisterModelStateDefinitions event) {
        // Our fake blocks are only created during data gen, so we have to add a fake mapper
        event.register(rl("cow_head"),
            new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)
                .create(Block::defaultBlockState, BlockState::new)
        );
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void item_model(GameTestHelper helper) {
        var manager = Minecraft.getInstance().getModelManager();

        var key = rl("pig_head");
        var model = manager.getItemModel(key);
        if (model == null)
            helper.fail("Failed to retreive " + key + " item model");

        if (!(model instanceof BlockModelWrapper))
            helper.fail("Itme Model was " + model.getClass()  + " when BlockModelWrapper expected");

        helper.succeed();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void block_model(GameTestHelper helper) {
        var manager = Minecraft.getInstance().getModelManager();

        var key = new ModelResourceLocation(rl("cow_head"), "");
        var model = manager.getModel(key);

        if (model == null)
            helper.fail("Failed to retreive " + key + " block model");

        helper.succeed();
    }

    // An example on how to render both the item and block model variants
    //@SubscribeEvent
    public void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // Pigs get a block on their head, this is a test of going through the ItemModel loader
        LivingEntityRenderer<Pig, PigRenderState, PigModel> pig = event.getEntityRenderer(EntityType.PIG);
        pig.addLayer(new RenderLayer<>(pig) {
            @Override
            public void render(PoseStack stack, MultiBufferSource source, int light, PigRenderState state, float xRot, float yRot) {
                this.getParentModel().getAnyDescendantWithName("head").ifPresent(part -> {
                    var manager = Minecraft.getInstance().getModelManager();
                    stack.pushPose();
                    this.getParentModel().root().translateAndRotate(stack);
                    part.translateAndRotate(stack);
                    stack.scale(0.5F, 0.5F, 0.5F);
                    stack.translate(0, -1, -0.5);

                    var model = manager.getItemModel(rl("pig_head"));
                    if (model instanceof BlockModelWrapper wrapper) {
                        var itemState = new ItemStackRenderState();
                        itemState.newLayer().setupBlockModel(wrapper.model, RenderType.solid());
                        itemState.render(stack, source, light, light);
                    }
                    stack.popPose();
                });
            }
        });


        // Cows get a block on their head, this is a test of going through the BlockModel loader
        LivingEntityRenderer<Cow, LivingEntityRenderState, CowModel> cow = event.getEntityRenderer(EntityType.COW);
        cow.addLayer(new RenderLayer<>(cow) {
            @Override
            public void render(PoseStack stack, MultiBufferSource source, int light, LivingEntityRenderState state, float xRot, float yRot) {
                this.getParentModel().getAnyDescendantWithName("head").ifPresent(part -> {
                    var manager = Minecraft.getInstance().getModelManager();
                    var renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

                    stack.pushPose();

                    this.getParentModel().root().translateAndRotate(stack);
                    part.translateAndRotate(stack);
                    stack.scale(0.5F, 0.5F, 0.5F);
                    stack.translate(-0.5, -0.5, -0.5);
                    stack.translate(0, -1, -0.5);

                    var ml = new ModelResourceLocation(rl("cow_head"), "");
                    var model = manager.getModel(ml);

                    var consumer = source.getBuffer(RenderType.solid());
                    renderer.renderModel(stack.last(), consumer, null, model, 1.0F, 1.0F, 1.0F, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
                    stack.popPose();
                });
            }
        });
    }

    public class ModelProvider extends net.minecraft.client.data.models.ModelProvider {
        public ModelProvider(PackOutput output) {
            super(output);
        }

        protected Stream<Block> getKnownBlocks() {
            return Stream.of(PIG_HEAD.get(), COW_HEAD.get());
        }

        protected Stream<Item> getKnownItems() {
            return Stream.of(PIG_HEAD_ITEM.get());
        }

        protected BlockModelGenerators getBlockModelGenerators(BlockStateGeneratorCollector blocks, ItemInfoCollector items, SimpleModelCollector models) {
            return new BlockModelGenerators(blocks, items, models) {
                @Override
                public void run() {
                    ModelTemplates.CUBE_ALL.create(PIG_HEAD.get(), TextureMapping.cube(Blocks.COBBLESTONE), this.modelOutput);
                    var cow  = ModelTemplates.CUBE_ALL.create(COW_HEAD.get(), TextureMapping.cube(Blocks.BEDROCK), this.modelOutput);
                    this.blockStateOutput.accept(createSimpleBlock(COW_HEAD.get(), cow));
                }
            };
        }

        protected ItemModelGenerators getItemModelGenerators(ItemInfoCollector items, SimpleModelCollector models) {
            return new ItemModelGenerators(items, models) {
                @Override
                public void run() {
                    this.itemModelOutput.accept(PIG_HEAD_ITEM.get(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(PIG_HEAD.get())));
                }
            };
        }

        static <T> CompletableFuture<?> saveAll(CachedOutput p_378816_, Function<T, Path> p_377914_, Map<T, ? extends Supplier<JsonElement>> p_377836_) {
            return DataProvider.saveAll(p_378816_, Supplier::get, p_377914_, p_377836_);
        }
    }
}
