package net.minecraftforge.debug.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.resources.IResourceManager;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    private static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);

    private static final RegistryObject<Block> DIORITE_POT = BLOCKS.register("diorite_pot", DioriteFlowerPotBlock::new);
    private static final RegistryObject<Item> DIORITE_POT_ITEM = ITEMS.register(
            "diorite_pot",
            () -> new BlockItem(DIORITE_POT.get(), new Item.Properties().tab(ItemGroup.TAB_MISC))
    );
    private static final RegistryObject<TileEntityType<DioriteFlowerPotTileEntity>> DIORITE_POT_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "diorite_pot",
            () -> TileEntityType.Builder.of(DioriteFlowerPotTileEntity::new, DIORITE_POT.get()).build(null)
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

    private static class DioriteFlowerPotBlock extends Block
    {
        private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

        public DioriteFlowerPotBlock()
        {
            super(Properties.of(Material.DECORATION).instabreak().noOcclusion());
        }

        @Override
        @SuppressWarnings("deprecation")
        public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
        {
            TileEntity te = level.getBlockEntity(pos);
            if (te instanceof DioriteFlowerPotTileEntity)
            {
                DioriteFlowerPotTileEntity pot = (DioriteFlowerPotTileEntity) te;
                ItemStack stack = player.getItemInHand(hand);
                boolean isFlower = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().containsKey(stack.getItem().getRegistryName());
                boolean hasFlower = pot.plant != Blocks.AIR;

                if (isFlower != hasFlower)
                {
                    if (!level.isClientSide())
                    {
                        if (isFlower)
                        {
                            pot.setPlant(((BlockItem) stack.getItem()).getBlock());

                            player.awardStat(Stats.POT_FLOWER);
                            if (!player.isCreative())
                            {
                                stack.shrink(1);
                            }
                        }
                        else
                        {
                            ItemStack flowerStack = new ItemStack(pot.getPlant());
                            if (stack.isEmpty())
                            {
                                player.setItemInHand(hand, flowerStack);
                            }
                            else if (!player.addItem(flowerStack))
                            {
                                player.drop(flowerStack, false);
                            }

                            pot.setPlant(Blocks.AIR);
                        }
                    }

                    return ActionResultType.sidedSuccess(level.isClientSide());
                }
                else
                {
                    return ActionResultType.CONSUME;
                }
            }
            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        @SuppressWarnings("deprecation")
        public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
        {
            return SHAPE;
        }

        @Override
        public boolean hasTileEntity(BlockState state)
        {
            return true;
        }

        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world)
        {
            return new DioriteFlowerPotTileEntity();
        }
    }

    private static class DioriteFlowerPotTileEntity extends TileEntity
    {
        public static final ModelProperty<Block> PLANT_PROPERTY = new ModelProperty<>();

        private final IModelData modelData = new ModelDataMap.Builder().build();
        private Block plant = Blocks.AIR;

        public DioriteFlowerPotTileEntity()
        {
            super(DIORITE_POT_BLOCK_ENTITY.get());
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
        public CompoundNBT getUpdateTag()
        {
            return save(new CompoundNBT());
        }

        @Override
        public void handleUpdateTag(BlockState state, CompoundNBT tag)
        {
            super.handleUpdateTag(state, tag);
            modelData.setData(PLANT_PROPERTY, plant);
            requestModelDataUpdate();
        }

        @Override
        public SUpdateTileEntityPacket getUpdatePacket()
        {
            return new SUpdateTileEntityPacket(getBlockPos(), -1, getUpdateTag());
        }

        @Override
        public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
        {
            handleUpdateTag(getBlockState(), pkt.getTag());
            //noinspection ConstantConditions
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);
        }

        @Override
        public void load(BlockState state, CompoundNBT tag)
        {
            super.load(state, tag);
            plant = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("plant")));
        }

        @Override
        public CompoundNBT save(CompoundNBT tag)
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

            RenderTypeLookup.setRenderLayer(DIORITE_POT.get(), RenderType.cutout());
        }

        @SubscribeEvent
        public static void registerLoader(final ModelRegistryEvent event)
        {
            ModelLoaderRegistry.registerLoader(new ResourceLocation(MOD_ID, "diorite_pot"), new DioritePotModelLoader());
        }

        private static class DioritePotModelLoader implements IModelLoader<DioritePotModelGeometry>
        {
            @Override
            public void onResourceManagerReload(IResourceManager manager) { }

            @Override
            public DioritePotModelGeometry read(JsonDeserializationContext context, JsonObject modelContents)
            {
                JsonObject wrappedModel = modelContents.getAsJsonObject("model");
                return new DioritePotModelGeometry(context.deserialize(wrappedModel, BlockModel.class));
            }
        }

        private static class DioritePotModelGeometry implements IModelGeometry<DioritePotModelGeometry>
        {
            private final IUnbakedModel wrappedModel;

            private DioritePotModelGeometry(IUnbakedModel wrappedModel)
            {
                this.wrappedModel = wrappedModel;
            }

            @Override
            public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
            {
                return new DioritePotModel(wrappedModel.bake(bakery, spriteGetter, modelTransform, modelLocation));
            }

            @Override
            public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
            {
                return wrappedModel.getMaterials(modelGetter, missingTextureErrors);
            }
        }

        private static class DioritePotModel extends BakedModelWrapper<IBakedModel>
        {
            private static final ResourceLocation POT_TEXTURE = new ResourceLocation("minecraft:block/flower_pot");
            private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:block/dirt");

            public DioritePotModel(IBakedModel wrappedModel) { super(wrappedModel); }

            @Nonnull
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
            {
                List<BakedQuad> quads = new ArrayList<>(originalModel.getQuads(state, side, rand, extraData));

                Block plant = extraData.getData(DioriteFlowerPotTileEntity.PLANT_PROPERTY);
                if (plant != null && plant != Blocks.AIR)
                {
                    quads.addAll(getPlantQuads(plant, side, rand));
                }

                return quads;
            }

            private List<BakedQuad> getPlantQuads(Block plant, @Nullable Direction face, Random rand)
            {
                BlockState potState = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView().getOrDefault(plant.getRegistryName(), Blocks.AIR.delegate).get().defaultBlockState();
                IBakedModel potModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(potState);

                return potModel.getQuads(potState, face, rand, EmptyModelData.INSTANCE)
                        .stream()
                        .filter(q -> !q.getSprite().getName().equals(POT_TEXTURE))
                        .filter(q -> !q.getSprite().getName().equals(DIRT_TEXTURE))
                        .collect(Collectors.toList());
            }
        }
    }
}