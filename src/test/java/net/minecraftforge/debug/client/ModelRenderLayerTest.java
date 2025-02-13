/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@Mod(ModelRenderLayerTest.MODID)
@GameTestHolder("forge." + ModelRenderLayerTest.MODID)
public class ModelRenderLayerTest extends BaseTestMod {
    public static final String MODID = "model_render_type";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);

    private static final String OLD_LEAVES_NAME = "old_leaves";
    private static final BlockBehaviour.StatePredicate FALSE = (state, level, pos) -> false;
    public static final RegistryObject<LeavesBlock> OLD_LEAVES = BLOCKS.register(OLD_LEAVES_NAME,
        () -> new LeavesBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isSuffocating(FALSE)
                .isViewBlocking(FALSE)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor(FALSE)
        )
    );

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final RegistryObject<BlockItem> OLD_LEAVES_ITEM = ITEMS.register(OLD_LEAVES_NAME, () -> new BlockItem(OLD_LEAVES.get(), new Item.Properties()));

    public ModelRenderLayerTest(FMLJavaModLoadingContext context) {
        super(context);
        testItem(lookup -> new ItemStack(OLD_LEAVES_ITEM.get()));
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new BlockStates(event));
        event.getGenerator().addProvider(event.includeClient(), new ItemModels(event));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void old_leaves_render_type(GameTestHelper helper) {
        var manager = Minecraft.getInstance().getModelManager();

        var state = OLD_LEAVES.get().defaultBlockState();
        var model = manager.getBlockModelShaper().getBlockModel(state);
        var random = helper.getLevel().random;
        boolean originalRenderState = ItemBlockRenderTypes.isFancy();
        helper.addCleanup(passed -> ItemBlockRenderTypes.setFancy(originalRenderState));

        ItemBlockRenderTypes.setFancy(true);
        var layer = model.getRenderTypes(state, random, ModelData.EMPTY);
        helper.assertTrue(layer.contains(RenderType.cutoutMipped()), "Block model does not contain the correct render type for fancy graphics. Expected: cutout_mipped");

        ItemBlockRenderTypes.setFancy(false);
        layer = model.getRenderTypes(state, random, ModelData.EMPTY);
        helper.assertTrue(layer.contains(RenderType.solid()), "Block model does not contain the correct render type for fast graphics. Expected: solid");

        helper.succeed();
    }

    public static class BlockStates extends BlockStateProvider {
        public BlockStates(GatherDataEvent event) {
            super(event.getGenerator().getPackOutput(), MODID, event.getExistingFileHelper());
        }

        @Override
        protected void registerStatesAndModels() {
            this.simpleBlock(
                OLD_LEAVES.get(),
                this.models()
                    .leaves(OLD_LEAVES_NAME, blockTexture(Blocks.OAK_LEAVES))
                    // only define the single render type. this test checks if "solid" is implicitly the fast graphics render type
                    .renderType("cutout_mipped")
            );
        }

    }
    private static class ItemModels extends ItemModelProvider {
        public ItemModels(GatherDataEvent event) {
            super(event.getGenerator().getPackOutput(), MODID, event.getExistingFileHelper());
        }

        @Override
        protected void registerModels() {
            this.withExistingParent(OLD_LEAVES_NAME, OLD_LEAVES.getKey().location().withPrefix(BLOCK_FOLDER + '/'));
        }
    }
}
