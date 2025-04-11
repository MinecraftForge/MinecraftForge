package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterItemModelPropertiesEvent;
import net.minecraftforge.client.event.RegisterItemModelsEvent;
import net.minecraftforge.client.event.RegisterSpecialModelRenderersEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// @GameTestNamespace("forge")
@Mod(ItemModelsTest.MODID)
public class ItemModelsTest extends BaseTestMod {
    public static final String MODID = "item_models";
    private static final ResourceLocation RESOURCE_LOCATION = rl("test");

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> MODEL = item("your_head");
    public static final RegistryObject<Item> SPECIAL_RENDERER = item("warden_plushy");
    public static final RegistryObject<Item> TINT_SRC = item("yellow_cap_covered_in_snow");
    public static final RegistryObject<Item> SELECT = item("rod_of_game_mode");
    public static final RegistryObject<Item> CONDITIONAL = item("swim_with_this");
    public static final RegistryObject<Item> RANGE = item("speedometer");

    public ItemModelsTest(FMLJavaModLoadingContext ctx) {
        super(ctx);
        for (RegistryObject<Item> item: List.of(MODEL, SPECIAL_RENDERER, TINT_SRC, SELECT, CONDITIONAL, RANGE)) {
            testItem(provider -> item.get().getDefaultInstance());
        }
    }

    private static RegistryObject<Item> item(String name) {
        return ITEMS.register(name, () -> new Item(name(smodid(), name, new Item.Properties())));
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        LogUtils.getLogger().info("about to register custom model provider");
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(event.includeClient(), new ModelProvider(out));
        LogUtils.getLogger().info("registered custom model provider");
    }

    @SubscribeEvent
    public void test(RegisterItemModelsEvent event) {
        event.register(RESOURCE_LOCATION, TestItemModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public void test(RegisterSpecialModelRenderersEvent event) {
        event.register(RESOURCE_LOCATION, TestSpecialModelRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public void test(RegisterColorHandlersEvent.Item event) {
        event.register(RESOURCE_LOCATION, TestItemTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public void test(RegisterItemModelPropertiesEvent.Select event) {
        event.register(RESOURCE_LOCATION, TestSelectProperty.TYPE);
    }

    @SubscribeEvent
    public void test(RegisterItemModelPropertiesEvent.Conditional event) {
        event.register(RESOURCE_LOCATION, TestConditionalProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public void test(RegisterItemModelPropertiesEvent.RangeSelect event) {
        event.register(RESOURCE_LOCATION, TestRangeSelectProperty.MAP_CODEC);
    }

    public static class TestItemModel implements ItemModel {
        @Override
        public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver p_378232_, ItemDisplayContext p_376927_, @Nullable ClientLevel p_377374_, @Nullable LivingEntity p_376127_, int p_377873_) {
            ItemStack stack1 = Items.PLAYER_HEAD.getDefaultInstance();
            stack1.set(DataComponents.PROFILE, new ResolvableProfile(Minecraft.getInstance().player.getGameProfile()));
            p_378232_.appendItemLayers(state, stack1, p_376927_, p_377374_, p_376127_, p_377873_);
        }

        public static class Unbaked implements ItemModel.Unbaked {
            public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

            @Override
            public MapCodec<? extends ItemModel.Unbaked> type() {
                return MAP_CODEC;
            }

            @Override
            public ItemModel bake(BakingContext ctx) {
                return new TestItemModel();
            }

            @Override
            public void resolveDependencies(Resolver resolver) {

            }
        }
    }

    public static class TestSpecialModelRenderer implements NoDataSpecialModelRenderer {
        private final ModelPart model;

        public TestSpecialModelRenderer(ModelPart model) {
            this.model = model;
        }

        @Override
        public void render(ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource src, int packedLight, int packedOverlay, boolean someRedundantBooleanIdk) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.scale(0.5f, -0.5f, -0.5f);
            model.render(poseStack, src.getBuffer(RenderType.entityCutoutNoCull(ResourceLocation.withDefaultNamespace("textures/entity/warden/warden.png"))), packedLight, packedOverlay);
            poseStack.popPose();
        }

        public static class Unbaked implements SpecialModelRenderer.Unbaked {
            public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

            @Override
            public @Nullable SpecialModelRenderer<?> bake(EntityModelSet models) {
                return new TestSpecialModelRenderer(models.bakeLayer(ModelLayers.WARDEN));
            }

            @Override
            public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
                return MAP_CODEC;
            }
        }
    }

    public static class TestItemTintSource implements ItemTintSource {
        public static final MapCodec<TestItemTintSource> MAP_CODEC = MapCodec.unit(new TestItemTintSource());

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel lvl, @Nullable LivingEntity entity) {
            return lvl != null && entity != null ? -1 - (int) (lvl.getBiome(entity.blockPosition()).value().getBaseTemperature() * 255) : -1;
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }

    public static class TestSelectProperty implements SelectItemModelProperty<GameType> {
        public static final Type<TestSelectProperty, GameType> TYPE = Type.create(MapCodec.unit(new TestSelectProperty()), GameType.CODEC);

        @Override
        public @Nullable GameType get(ItemStack stack, @Nullable ClientLevel lvl, @Nullable LivingEntity entity, int someNumber, ItemDisplayContext ctx) {
            return entity instanceof Player player ? player.gameMode() : null;
        }

        @Override
        public Codec<GameType> valueCodec() {
            return GameType.CODEC;
        }

        @Override
        public Type<? extends SelectItemModelProperty<GameType>, GameType> type() {
            return TYPE;
        }
    }

    public static class TestConditionalProperty implements ConditionalItemModelProperty {
        public static final MapCodec<TestConditionalProperty> MAP_CODEC = MapCodec.unit(new TestConditionalProperty());

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }

        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel lvl, @Nullable LivingEntity entity, int anotherNumber, ItemDisplayContext ctx) {
            return entity != null && entity.isInWaterOrRain();
        }
    }

    public static class TestRangeSelectProperty implements RangeSelectItemModelProperty {
        public static final MapCodec<TestRangeSelectProperty> MAP_CODEC = MapCodec.unit(new TestRangeSelectProperty());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel lvl, @Nullable LivingEntity entity, int aNumberAgain) {
            return entity != null ? (float) entity.getDeltaMovement().horizontalDistance() : 0;
        }

        @Override
        public MapCodec<? extends RangeSelectItemModelProperty> type() {
            return MAP_CODEC;
        }
    }

    public static class ModelProvider extends net.minecraft.client.data.models.ModelProvider {
        public ModelProvider(PackOutput output) {
            super(output);
        }

        @Override
        protected Stream<Item> getKnownItems() {
            return Stream.of(MODEL.get(), SPECIAL_RENDERER.get(), TINT_SRC.get(), SELECT.get(), CONDITIONAL.get(), RANGE.get());
        }

        @Override
        protected Stream<Block> getKnownBlocks() {
            return Stream.of();
        }

        @Override
        protected ItemModelGenerators getItemModelGenerators(ItemInfoCollector items, SimpleModelCollector models) {
            return new ItemModelGenerators(items, models) {
                @Override
                public void run() {
                    itemModelOutput.accept(MODEL.get(), new TestItemModel.Unbaked());
                    itemModelOutput.accept(SPECIAL_RENDERER.get(), ItemModelUtils.specialModel(ResourceLocation.withDefaultNamespace("item/generated"), new TestSpecialModelRenderer.Unbaked()));
                    itemModelOutput.accept(TINT_SRC.get(), ItemModelUtils.tintedModel(ModelLocationUtils.getModelLocation(Items.LEATHER_HELMET), new TestItemTintSource()));
                    itemModelOutput.accept(SELECT.get(), ItemModelUtils.select(new TestSelectProperty(), plain(Items.STICK),
                            new SelectItemModel.SwitchCase<>(List.of(GameType.CREATIVE), plain(Items.BLAZE_ROD)),
                            new SelectItemModel.SwitchCase<>(List.of(GameType.ADVENTURE), plain(Items.BREEZE_ROD))));
                    itemModelOutput.accept(CONDITIONAL.get(), ItemModelUtils.conditional(new TestConditionalProperty(), plain(Items.TADPOLE_BUCKET), plain(Items.WATER_BUCKET)));
                    ArrayList<RangeSelectItemModel.Entry> list = new ArrayList<>();
                    for (int i = 1; i < 24; i++) {
                        list.add(new RangeSelectItemModel.Entry(i * 0.015f, compass(i + 4)));
                    }
                    itemModelOutput.accept(RANGE.get(), ItemModelUtils.rangeSelect(new TestRangeSelectProperty(), compass(4), list));
                }

                private static ItemModel.Unbaked plain(Item item) {
                    return ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
                }

                private static ItemModel.Unbaked compass(int frame) {
                    return ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(Items.RECOVERY_COMPASS,
                            (frame >= 10 ? "_" : "_0") + frame));
                }
            };
        }

        @Override
        protected BlockModelGenerators getBlockModelGenerators(BlockStateGeneratorCollector blocks, ItemInfoCollector items, SimpleModelCollector models) {
            return new BlockModelGenerators(blocks, items, models) {
                @Override
                public void run() {
                }
            };
        }
    }
}
