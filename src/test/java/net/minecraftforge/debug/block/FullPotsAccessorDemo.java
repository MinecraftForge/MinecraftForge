/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.common.util.ConcatenatedListView;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod(FullPotsAccessorDemo.MOD_ID)
public class FullPotsAccessorDemo
{
    public static final String MOD_ID = "full_pots_accessor_demo";
    private static final boolean ENABLED = true;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    private static final RegistryObject<Block> DIORITE_POT = BLOCKS.register("diorite_pot", DioriteFlowerPotBlock::new);
    private static final RegistryObject<Item> DIORITE_POT_ITEM = ITEMS.register(
            "diorite_pot",
            () -> new BlockItem(DIORITE_POT.get(), new Item.Properties())
    );
    private static final RegistryObject<BlockEntityType<DioriteFlowerPotBlockEntity>> DIORITE_POT_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "diorite_pot",
            () -> BlockEntityType.Builder.of(DioriteFlowerPotBlockEntity::new, DIORITE_POT.get()).build(null)
    );

    public FullPotsAccessorDemo()
    {
        if (ENABLED)
        {
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(bus);
            ITEMS.register(bus);
            BLOCK_ENTITIES.register(bus);
            bus.addListener(this::addCreative);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(DIORITE_POT_ITEM);
    }

    private static class DioriteFlowerPotBlock extends Block implements EntityBlock
    {
        private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

        public DioriteFlowerPotBlock()
        {
            super(Properties.of().mapColor(MapColor.NONE).instabreak().noOcclusion());
        }

        @Override
        @SuppressWarnings("deprecation")
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
        {
            if (level.getBlockEntity(pos) instanceof DioriteFlowerPotBlockEntity be)
            {
                ItemStack stack = player.getItemInHand(hand);
                boolean isFlower = stack.getItem() instanceof BlockItem item && ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().containsKey(ForgeRegistries.ITEMS.getKey(item));
                boolean hasFlower = be.plant != Blocks.AIR;

                if (isFlower != hasFlower)
                {
                    if (!level.isClientSide())
                    {
                        if (isFlower)
                        {
                            be.setPlant(((BlockItem) stack.getItem()).getBlock());

                            player.awardStat(Stats.POT_FLOWER);
                            if (!player.getAbilities().instabuild)
                            {
                                stack.shrink(1);
                            }
                        }
                        else
                        {
                            ItemStack flowerStack = new ItemStack(be.getPlant());
                            if (stack.isEmpty())
                            {
                                player.setItemInHand(hand, flowerStack);
                            }
                            else if (!player.addItem(flowerStack))
                            {
                                player.drop(flowerStack, false);
                            }

                            be.setPlant(Blocks.AIR);
                        }
                    }

                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                else
                {
                    return InteractionResult.CONSUME;
                }
            }
            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        @SuppressWarnings("deprecation")
        public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
        {
            return SHAPE;
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
        {
            return new DioriteFlowerPotBlockEntity(pos, state);
        }
    }

    private static class DioriteFlowerPotBlockEntity extends BlockEntity
    {
        public static final ModelProperty<Block> PLANT_PROPERTY = new ModelProperty<>();

        private ModelData modelData;
        private Block plant = Blocks.AIR;

        public DioriteFlowerPotBlockEntity(BlockPos pos, BlockState state)
        {
            super(DIORITE_POT_BLOCK_ENTITY.get(), pos, state);
            modelData = ModelData.builder().with(PLANT_PROPERTY, plant).build();
        }

        public void setPlant(Block plant)
        {
            this.plant = plant;
            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();
        }

        public Block getPlant() { return plant; }

        @Override
        public ModelData getModelData() {return modelData; }

        @Override
        public CompoundTag getUpdateTag()
        {
            return saveWithFullMetadata();
        }

        @Override
        public void handleUpdateTag(CompoundTag tag)
        {
            super.handleUpdateTag(tag);
            modelData = modelData.derive().with(PLANT_PROPERTY, plant).build();
            requestModelDataUpdate();
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket()
        {
            return ClientboundBlockEntityDataPacket.create(this, be -> be.getUpdateTag());
        }

        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
        {
            handleUpdateTag(pkt.getTag());
            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }

        @Override
        public void load(CompoundTag tag)
        {
            super.load(tag);
            plant = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("plant")));
        }

        @Override
        protected void saveAdditional(CompoundTag tag)
        {
            //noinspection ConstantConditions
            tag.putString("plant", ForgeRegistries.BLOCKS.getKey(plant).toString());
            super.saveAdditional(tag);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientHandler
    {
        @SubscribeEvent
        public static void registerLoader(final ModelEvent.RegisterGeometryLoaders event)
        {
            event.register("diorite_pot", new DioritePotGeometryLoader());
        }

        private static class DioritePotGeometryLoader implements IGeometryLoader<DioritePotModelGeometry>
        {
            @Override
            public DioritePotModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext)
            {
                JsonObject wrappedModel = jsonObject.getAsJsonObject("model");
                return new DioritePotModelGeometry(deserializationContext.deserialize(wrappedModel, BlockModel.class));
            }
        }

        private record DioritePotModelGeometry(UnbakedModel wrappedModel) implements IUnbakedGeometry<DioritePotModelGeometry>
        {
            @Override
            public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<net.minecraft.client.resources.model.Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
                return new DioritePotModel(wrappedModel.bake(baker, spriteGetter, modelState, modelLocation));
            }

            @Override
            public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
            {
                wrappedModel.resolveParents(modelGetter);
            }
        }

        private static class DioritePotModel extends BakedModelWrapper<BakedModel>
        {
            private static final ChunkRenderTypeSet CUTOUT = ChunkRenderTypeSet.of(RenderType.cutout());
            private static final ResourceLocation POT_TEXTURE = new ResourceLocation("minecraft:block/flower_pot");
            private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:block/dirt");

            public DioritePotModel(BakedModel wrappedModel) { super(wrappedModel); }

            @NotNull
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType)
            {
                List<List<BakedQuad>> quads = new ArrayList<>();
                quads.add(originalModel.getQuads(state, side, rand, extraData, renderType));

                Block plant = extraData.get(DioriteFlowerPotBlockEntity.PLANT_PROPERTY);
                if (plant != null && plant != Blocks.AIR)
                {
                    quads.add(getPlantQuads(plant, side, rand, renderType));
                }

                return ConcatenatedListView.of(quads);
            }

            private List<BakedQuad> getPlantQuads(Block plant, @Nullable Direction face, RandomSource rand, @Nullable RenderType renderType)
            {
                BlockState potState = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().getOrDefault(ForgeRegistries.BLOCKS.getKey(plant), ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.AIR)).get().defaultBlockState();
                BakedModel potModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(potState);

                return potModel.getQuads(potState, face, rand, ModelData.EMPTY, renderType)
                        .stream()
                        .filter(q -> !q.getSprite().contents().name().equals(POT_TEXTURE))
                        .filter(q -> !q.getSprite().contents().name().equals(DIRT_TEXTURE))
                        .collect(Collectors.toList());
            }

            @Override
            public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
            {
                return CUTOUT;
            }
        }
    }
}
