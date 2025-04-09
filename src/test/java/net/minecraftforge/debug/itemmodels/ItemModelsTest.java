package net.minecraftforge.debug.itemmodels;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterItemModelPropertiesEvent;
import net.minecraftforge.client.event.RegisterItemModelsEvent;
import net.minecraftforge.client.event.RegisterSpecialModelRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.Nullable;

@GameTestNamespace("forge")
@Mod(ItemModelsTest.MODID)
public class ItemModelsTest extends BaseTestMod {
    public static final String MODID = "item_models";
    private static final ResourceLocation RESOURCE_LOCATION = rl("test");

    public ItemModelsTest(FMLJavaModLoadingContext ctx) {
        super(ctx);
        IEventBus bus = ctx.getModEventBus();
        DeferredRegister<Item> register = DeferredRegister.create(ForgeRegistries.ITEMS, modid());
        for (String s: new String[] {"your_head", "warden_plushy", "yellow_cap_covered_in_snow", "rod_of_game_mode", "swim_with_this", "fullinventorymeter"}) {
            RegistryObject<Item> registryObject = register.register(s, () -> new Item(name(modid(), s, new Item.Properties())));
            testItem(provider -> registryObject.get().getDefaultInstance());
        }
        register.register(bus);
        bus.register(ItemModelsTest.class);
    }

    @SubscribeEvent
    public static void test(RegisterItemModelsEvent event) {
        event.register(RESOURCE_LOCATION, TestItemModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void test(RegisterSpecialModelRenderersEvent event) {
        event.register(RESOURCE_LOCATION, TestSpecialModelRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void test(RegisterColorHandlersEvent.Item event) {
        event.register(RESOURCE_LOCATION, TestItemTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void test(RegisterItemModelPropertiesEvent.Select event) {
        event.register(RESOURCE_LOCATION, TestSelectProperty.TYPE);
    }

    @SubscribeEvent
    public static void test(RegisterItemModelPropertiesEvent.Conditional event) {
        event.register(RESOURCE_LOCATION, TestConditionalProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void test(RegisterItemModelPropertiesEvent.RangeSelect event) {
        event.register(RESOURCE_LOCATION, TestRangeSelectProperty.MAP_CODEC);
    }

    public static class TestItemModel implements ItemModel {

        @Override
        public void update(ItemStackRenderState p_377489_, ItemStack p_376390_, ItemModelResolver p_378232_, ItemDisplayContext p_376927_, @Nullable ClientLevel p_377374_, @Nullable LivingEntity p_376127_, int p_377873_) {
            ItemStack stack = Items.PLAYER_HEAD.getDefaultInstance();
            stack.set(DataComponents.PROFILE, new ResolvableProfile(Minecraft.getInstance().player.getGameProfile()));
            p_378232_.appendItemLayers(p_377489_, stack, p_376927_, p_377374_, p_376127_, p_377873_);
        }

        public static class Unbaked implements ItemModel.Unbaked {
            public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

            @Override
            public MapCodec<? extends ItemModel.Unbaked> type() {
                return MAP_CODEC;
            }

            @Override
            public ItemModel bake(BakingContext p_376062_) {
                return new TestItemModel();
            }

            @Override
            public void resolveDependencies(Resolver p_376736_) {

            }
        }
    }

    public static class TestSpecialModelRenderer implements NoDataSpecialModelRenderer {
        private final ModelPart model;

        public TestSpecialModelRenderer(ModelPart model) {
            this.model = model;
        }

        @Override
        public void render(ItemDisplayContext p_376384_, PoseStack p_377457_, MultiBufferSource p_378580_, int p_375653_, int p_376500_, boolean p_376690_) {
            p_377457_.pushPose();
            p_377457_.translate(0.5f, 0.5f, 0.5f);
            p_377457_.scale(0.5f, -0.5f, -0.5f);
            model.render(p_377457_, p_378580_.getBuffer(RenderType.entityCutoutNoCull(ResourceLocation.withDefaultNamespace("textures/entity/warden/warden.png"))), p_375653_, p_376500_);
            p_377457_.popPose();
        }

        public static class Unbaked implements SpecialModelRenderer.Unbaked {
            public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

            @Override
            public @Nullable SpecialModelRenderer<?> bake(EntityModelSet p_378825_) {
                return new TestSpecialModelRenderer(p_378825_.bakeLayer(ModelLayers.WARDEN));
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
        public int calculate(ItemStack p_376017_, @Nullable ClientLevel p_378129_, @Nullable LivingEntity p_376885_) {
            return p_378129_ != null && p_376885_ != null ? -1 - (int) (p_378129_.getBiome(p_376885_.blockPosition()).value().getBaseTemperature() * 255) : -1;
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }

    public static class TestSelectProperty implements SelectItemModelProperty<GameType> {
        public static final Type<TestSelectProperty, GameType> TYPE = Type.create(MapCodec.unit(new TestSelectProperty()), GameType.CODEC);

        @Override
        public @Nullable GameType get(ItemStack p_378017_, @Nullable ClientLevel p_375849_, @Nullable LivingEntity p_377186_, int p_375524_, ItemDisplayContext p_378106_) {
            return p_377186_ instanceof Player player ? player.gameMode() : null;
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
        public boolean get(ItemStack p_395889_, @Nullable ClientLevel p_393910_, @Nullable LivingEntity p_393617_, int p_396904_, ItemDisplayContext p_395957_) {
            return p_393617_ != null && p_393617_.isInWaterOrRain();
        }
    }

    public static class TestRangeSelectProperty implements RangeSelectItemModelProperty {
        public static final MapCodec<TestRangeSelectProperty> MAP_CODEC = MapCodec.unit(new TestRangeSelectProperty());

        @Override
        public float get(ItemStack p_376822_, @Nullable ClientLevel p_376153_, @Nullable LivingEntity p_377311_, int p_376174_) {
            return p_377311_ instanceof Player player ? player.getInventory().getFreeSlot() : 0;
        }

        @Override
        public MapCodec<? extends RangeSelectItemModelProperty> type() {
            return MAP_CODEC;
        }
    }
}
