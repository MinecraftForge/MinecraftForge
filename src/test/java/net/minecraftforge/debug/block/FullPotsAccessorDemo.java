package net.minecraftforge.debug.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID);

    private static final RegistryObject<Block> DIORITE_POT = BLOCKS.register("diorite_pot", DioriteFlowerPotBlock::new);
    private static final RegistryObject<Item> DIORITE_POT_ITEM = ITEMS.register(
            "diorite_pot",
            () -> new BlockItem(DIORITE_POT.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC))
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
        }
    }

    private static class DioriteFlowerPotBlock extends Block implements EntityBlock
    {
        private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

        public DioriteFlowerPotBlock()
        {
            super(Properties.of(Material.DECORATION).instabreak().noOcclusion());
        }

        @Override
        @SuppressWarnings("deprecation")
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
        {
            if (level.getBlockEntity(pos) instanceof DioriteFlowerPotBlockEntity be)
            {
                ItemStack stack = player.getItemInHand(hand);
                boolean isFlower = stack.getItem() instanceof BlockItem item && ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().containsKey(item.getRegistryName());
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

        private final IModelData modelData = new ModelDataMap.Builder().build();
        private Block plant = Blocks.AIR;

        public DioriteFlowerPotBlockEntity(BlockPos pos, BlockState state)
        {
            super(DIORITE_POT_BLOCK_ENTITY.get(), pos, state);
            modelData.setData(PLANT_PROPERTY, plant);
        }

        public void setPlant(Block plant)
        {
            this.plant = plant;
            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);
            setChanged();
        }

        public Block getPlant() { return plant; }

        @Override
        public IModelData getModelData() { return modelData; }

        @Override
        public CompoundTag getUpdateTag()
        {
            return save(new CompoundTag());
        }

        @Override
        public void handleUpdateTag(CompoundTag tag)
        {
            super.handleUpdateTag(tag);
            modelData.setData(PLANT_PROPERTY, plant);
            requestModelDataUpdate();
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket()
        {
            return new ClientboundBlockEntityDataPacket(getBlockPos(), -1, getUpdateTag());
        }

        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
        {
            handleUpdateTag(pkt.getTag());
            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);
        }

        @Override
        public void load(CompoundTag tag)
        {
            super.load(tag);
            plant = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("plant")));
        }

        @Override
        public CompoundTag save(CompoundTag tag)
        {
            //noinspection ConstantConditions
            tag.putString("plant", plant.getRegistryName().toString());
            return super.save(tag);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientHandler
    {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event)
        {
            if (!ENABLED) { return; }

            ItemBlockRenderTypes.setRenderLayer(DIORITE_POT.get(), RenderType.cutout());
        }

        @SubscribeEvent
        public static void registerLoader(final ModelRegistryEvent event)
        {
            ModelLoaderRegistry.registerLoader(new ResourceLocation(MOD_ID, "diorite_pot"), new DioritePotModelLoader());
        }

        private static class DioritePotModelLoader implements IModelLoader<DioritePotModelGeometry>
        {
            @Override
            public void onResourceManagerReload(ResourceManager manager) { }

            @Override
            public DioritePotModelGeometry read(JsonDeserializationContext context, JsonObject modelContents)
            {
                JsonObject wrappedModel = modelContents.getAsJsonObject("model");
                return new DioritePotModelGeometry(context.deserialize(wrappedModel, BlockModel.class));
            }
        }

        private record DioritePotModelGeometry(UnbakedModel wrappedModel) implements IModelGeometry<DioritePotModelGeometry>
        {
            @Override
            public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<net.minecraft.client.resources.model.Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
            {
                return new DioritePotModel(wrappedModel.bake(bakery, spriteGetter, modelTransform, modelLocation));
            }

            @Override
            public Collection<net.minecraft.client.resources.model.Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
            {
                return wrappedModel.getMaterials(modelGetter, missingTextureErrors);
            }
        }

        private static class DioritePotModel extends BakedModelWrapper<BakedModel>
        {
            private static final ResourceLocation POT_TEXTURE = new ResourceLocation("minecraft:block/flower_pot");
            private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:block/dirt");

            public DioritePotModel(BakedModel wrappedModel) { super(wrappedModel); }

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
            {
                List<BakedQuad> quads = new ArrayList<>(originalModel.getQuads(state, side, rand, extraData));

                Block plant = extraData.getData(DioriteFlowerPotBlockEntity.PLANT_PROPERTY);
                if (plant != null && plant != Blocks.AIR)
                {
                    quads.addAll(getPlantQuads(plant, side, rand));
                }

                return quads;
            }

            private List<BakedQuad> getPlantQuads(Block plant, @Nullable Direction face, Random rand)
            {
                BlockState potState = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().getOrDefault(plant.getRegistryName(), Blocks.AIR.delegate).get().defaultBlockState();
                BakedModel potModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(potState);

                return potModel.getQuads(potState, face, rand, EmptyModelData.INSTANCE)
                        .stream()
                        .filter(q -> !q.getSprite().getName().equals(POT_TEXTURE))
                        .filter(q -> !q.getSprite().getName().equals(DIRT_TEXTURE))
                        .collect(Collectors.toList());
            }
        }
    }
}
