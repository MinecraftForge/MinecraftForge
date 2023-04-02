/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

/**
 * Test mod for the custom transform types feature.
 * This test mod adds an item that should be held in the main hand, while another item is in the offhand.
 * When right-clicked on a block, the item will "hang" the stack from the offhand slot on the wall.
 * In addition, a replacement model for the stick is provided in the resources.
 * If the feature is working, the stick will have a custom transform while hanging on walls,
 * when compared to similar items such as a fishing rod.
 * Editing that model json will reflect ingame after a resource reload (F3+T).
 */
@Mod(CustomItemDisplayContextTest.MODID)
public class CustomItemDisplayContextTest
{
    public static final String MODID = "custom_transformtype_test";

     @Mod.EventBusSubscriber(value= Dist.CLIENT, modid = MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
    private static class RendererEvents
    {
        public static final ItemDisplayContext HANGING = ItemDisplayContext.create("custom_transformtype_test_hanging",  new ResourceLocation("custom_transformtype_test", "hanging"), null);
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
        {
            event.registerBlockEntityRenderer(ITEM_HANGER_BE.get(), ItemHangerBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerContext(final RegisterEvent event) {
            event.register(ForgeRegistries.Keys.DISPLAY_CONTEXTS, helper -> helper.register("hanging", HANGING));
        }

        private static class ItemHangerBlockEntityRenderer
                implements BlockEntityRenderer<ItemHangerBlockEntity>
        {
            public ItemHangerBlockEntityRenderer(BlockEntityRendererProvider.Context context)
            {
            }

            @Override
            public void render(ItemHangerBlockEntity blocken, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlayCoord)
            {
                var state = blocken.getBlockState();

                if (!(state.getBlock() instanceof ItemHangerBlock)) return;

                poseStack.pushPose();

                poseStack.translate(0.5,0.5,0.5);
                poseStack.mulPose(state.getValue(ItemHangerBlock.FACING).getRotation());
                poseStack.translate(-0.5,-0.5,-0.5);

                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

                var model = itemRenderer.getModel(blocken.heldItem, blocken.getLevel(), null, 0);

                itemRenderer.render(blocken.heldItem, HANGING, false, poseStack, bufferSource, packedLight, overlayCoord, model);

                poseStack.popPose();
            }
        }
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<Block>
            ITEM_HANGER_BLOCK = BLOCKS.register("item_hanger", () -> new ItemHangerBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion().noLootTable()));
    public static final RegistryObject<BlockEntityType<ItemHangerBlockEntity>>
            ITEM_HANGER_BE = BLOCK_ENTITY_TYPES.register("item_hanger", () -> BlockEntityType.Builder.of(ItemHangerBlockEntity::new, ITEM_HANGER_BLOCK.get()).build(null));
    public static final RegistryObject<Item>
            ITEM_HANGER_ITEM = ITEMS.register("item_hanger", () -> new ItemHangerItem(ITEM_HANGER_BLOCK.get(), new Item.Properties()));

    public CustomItemDisplayContextTest()
    {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::gatherData);
        BLOCKS.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(ITEM_HANGER_ITEM);
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        final PackOutput output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new ItemModels(output, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new BlockStateModels(output, event.getExistingFileHelper()));
    }

    public static class BlockStateModels extends BlockStateProvider
    {

        public BlockStateModels(PackOutput output, ExistingFileHelper exFileHelper)
        {
            super(output, MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            {
                Block block = ITEM_HANGER_BLOCK.get();
                horizontalBlock(block, models().getExistingFile(ModelLocationUtils.getModelLocation(block)));
            }
        }
    }

    public static class ItemModels extends ItemModelProvider
    {
        public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper)
        {
            super(output, MODID, existingFileHelper);
        }

        @Override
        protected void registerModels()
        {
            basicItem(ITEM_HANGER_ITEM.get());

            basicItem(Items.STICK)
                   .transforms()
                       .transform(RendererEvents.HANGING)
                           .rotation(62, 180 - 33, 40)
                           .translation(-2.25f, 1.5f, -0.25f).scale(0.48f)
                       .end()
                   .end();
        }
    }

    private static class ItemHangerBlock extends HorizontalDirectionalBlock implements EntityBlock
    {
        public ItemHangerBlock(BlockBehaviour.Properties properties)
        {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING);
        }

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext ctx)
        {
            return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
        {
            return new ItemHangerBlockEntity(pos, state);
        }

        @Deprecated
        @Override
        public RenderShape getRenderShape(BlockState state)
        {
            return RenderShape.ENTITYBLOCK_ANIMATED;
        }
    }

    private static class ItemHangerBlockEntity extends BlockEntity
    {
        private ItemStack heldItem;

        public ItemHangerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState)
        {
            super(type, blockPos, blockState);
        }

        public ItemHangerBlockEntity(BlockPos blockPos, BlockState blockState)
        {
            this(ITEM_HANGER_BE.get(), blockPos, blockState);
        }

        @Override
        public CompoundTag getUpdateTag()
        {
            return saveWithoutMetadata();
        }

        @Nullable
        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket()
        {
            return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
        }

        @Override
        public void handleUpdateTag(CompoundTag tag)
        {
            load(tag);
        }

        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
        {
            handleUpdateTag(pkt.getTag());
        }

        @Override
        protected void saveAdditional(CompoundTag tag)
        {
            var c = new CompoundTag();
            heldItem.save(c);
            tag.put("item", c);
        }

        @Override
        public void load(CompoundTag tag)
        {
            var c = tag.getCompound("item");
            heldItem = ItemStack.of(c);
        }
    }

    private static class ItemHangerItem extends BlockItem
    {
        public ItemHangerItem(Block block, Item.Properties properties)
        {
            super(block, properties);
        }

        @Override
        protected boolean placeBlock(BlockPlaceContext ctx, BlockState state)
        {
            Player player = ctx.getPlayer();
            if (player == null)
                return false;
            var hand = ctx.getHand();
            if (hand != InteractionHand.MAIN_HAND)
            {
                return false;
            }
            if (!super.placeBlock(ctx, state))
                return false;
            if (ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof ItemHangerBlockEntity be)
            {
                be.heldItem = player.getItemInHand(InteractionHand.OFF_HAND);
            }
            return true;
        }
    }
}
