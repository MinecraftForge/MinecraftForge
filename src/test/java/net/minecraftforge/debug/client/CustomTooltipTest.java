package net.minecraftforge.debug.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(CustomTooltipTest.ID)
public class CustomTooltipTest
{

    static final boolean ENABLED = true;
    static final String ID = "custom_tooltip_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register(
            "test_item",
            () -> new CustomItemWithTooltip(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public CustomTooltipTest()
    {
        if (ENABLED) {
            if (FMLEnvironment.dist.isClient())
            {
                MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
                FMLJavaModLoadingContext.get().getModEventBus().register(ClientModBusEventHandler.class);
            }
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    static record CustomTooltip(int color) implements TooltipComponent
    {
    }

    record CustomClientTooltip(CustomTooltip tooltip) implements ClientTooltipComponent
    {

        @Override
        public int getHeight()
        {
            return 10;
        }

        @Override
        public int getWidth(Font font)
        {
            return 10;
        }

        @Override
        public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer_, int zIndex, TextureManager textureManager)
        {
            GuiComponent.fill(poseStack, x, y,  x + 10, y+ 10, tooltip.color);
        }
    }

    static class CustomItemWithTooltip extends Item
    {
        public CustomItemWithTooltip(Properties properties)
        {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag)
        {
            super.appendHoverText(stack, level, components, flag);
            components.add(new TextComponent("This is a very very very very very very long hover text that should really really be split across multiple lines.").withStyle(ChatFormatting.YELLOW));
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
        {
            if (level.isClientSide)
            {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TooltipTestScreen::show);
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        @Override
        public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
        {
            return Optional.of(new CustomTooltip(0xFFFF0000));
        }
    }


    private static class ClientModBusEventHandler
    {

        static FontManager customFontManager;
        static Font customFont;

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            MinecraftForgeClient.registerTooltipComponentFactory(CustomTooltip.class, CustomClientTooltip::new);
            event.enqueueWork(() -> {
                customFontManager = new FontManager(Minecraft.getInstance().textureManager);
                customFont = customFontManager.createFont();
                ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(customFontManager.getReloadListener());
                customFontManager.setRenames(ImmutableMap.of(Minecraft.DEFAULT_FONT, Minecraft.UNIFORM_FONT));
            });
        }

    }

    private static class ClientEventHandler
    {

        @SubscribeEvent
        public static void gatherTooltips(RenderTooltipEvent.GatherComponents event)
        {
            if (event.getStack().getItem() == Items.STICK)
            {
                event.getTooltipElements().add(Either.right(new CustomTooltip(0xFF0000FF)));
            }
            if (event.getStack().getItem() == Items.CLOCK)
            {
                event.setMaxWidth(30);
            }
        }

        @SubscribeEvent
        public static void preTooltip(RenderTooltipEvent.Color event)
        {
            if (event.getStack().getItem() == Items.APPLE)
            {
                event.setBackgroundStart(0xFF0000FF);
                event.setBackgroundEnd(0xFFFFFF00);
                event.setBorderStart(0xFFFF0000);
                event.setBorderEnd(0xFFFF0011);
            }
        }

    }

    static class TooltipTestScreen extends Screen
    {

        private ItemStack testStack = ItemStack.EMPTY;
        private Font testFont = null;

        protected TooltipTestScreen()
        {
            super(new TextComponent("TooltipMethodTest"));
        }

        static void show()
        {
            Minecraft.getInstance().setScreen(new TooltipTestScreen());
        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
            super.render(poseStack, mouseX, mouseY, partialTicks);
            this.font.draw(poseStack, "* must have Stack, # must have custom font", 0, 0, 0xFFFFFF);
        }

        @Override
        protected void init()
        {
            addRenderableWidget(new Button(10, 10, 200, 20, new TextComponent("Toggle Stack: EMPTY"), button -> {
                this.testStack = this.testStack.isEmpty() ? new ItemStack(Items.APPLE) : ItemStack.EMPTY;
                button.setMessage(new TextComponent("Toggle Stack: " + (testStack.isEmpty() ? "EMPTY" : "Apple")));
            }));

            addRenderableWidget(new Button(220, 10, 200, 20, new TextComponent("Toggle Font: null"), button -> {
                this.testFont = this.testFont == null ? ClientModBusEventHandler.customFont : null;
                button.setMessage(new TextComponent("Toggle Font: " + (testFont == null ? "null" : "customFont")));
            }));

            // * must have stack context
            // # must have custom font
            List<Map.Entry<String, Button.OnTooltip>> tooltipTests = Arrays.asList(
                    Map.entry(" 1 * ", this::test1),
                    Map.entry(" 2 * ", this::test2),
                    Map.entry(" 3  #", this::test3),
                    Map.entry(" 4 *#", this::test4),
                    Map.entry(" 5   ", this::test5),
                    Map.entry(" 6   ", this::test6),
                    Map.entry(" 7 * ", this::test7),
                    Map.entry(" 8  #", this::test8),
                    Map.entry(" 9 *#", this::test9),
                    Map.entry("10   ", this::test10),
                    Map.entry("11   ", this::test11),
                    Map.entry("12  #", this::test12),
                    Map.entry("13  #", this::test13),
                    Map.entry("14  #", this::test14)
            );
            int x = 50;
            int y = 50;
            for (var test : tooltipTests)
            {
                addRenderableWidget(new Button(x, y, 100, 20, new TextComponent(test.getKey()), button -> {}, test.getValue()));
                y+= 22;
                if (y >= height - 50)
                {
                    y = 50;
                    x += 110;
                }
            }
        }

        private void test1(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, this.testStack, mouseX, mouseY);
        }

        // renderTooltip with List<Component> and all combinations of ItemStack/Font
        private void test2(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, List.of(new TextComponent("test")), Optional.empty(), mouseX, mouseY, this.testStack);
        }

        private void test3(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, List.of(new TextComponent("test")), Optional.empty(), mouseX, mouseY, this.testFont);
        }

        private void test4(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, List.of(new TextComponent("test")), Optional.empty(), mouseX, mouseY, this.testFont, this.testStack);
        }

        private void test5(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, List.of(new TextComponent("test")), Optional.empty(), mouseX, mouseY);
        }

        // renderTooltip with just Component
        private void test6(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, new TextComponent("test"), mouseX, mouseY);
        }

        // renderComponentTooltip with all combinations of ItemStack/Font
        private void test7(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderComponentTooltip(poseStack, List.of(new TextComponent("test")), mouseX, mouseY, this.testStack);
        }

        private void test8(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderComponentTooltip(poseStack, List.of(new TextComponent("test")), mouseX, mouseY, this.testFont);
        }

        private void test9(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderComponentTooltip(poseStack, List.of(new TextComponent("test")), mouseX, mouseY, this.testFont, this.testStack);
        }

        private void test10(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderComponentTooltip(poseStack, List.of(new TextComponent("test")), mouseX, mouseY);
        }

        // renderTooltip with list of FormattedCharSequence
        private void test11(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, List.of(new TextComponent("test").getVisualOrderText()), mouseX, mouseY);
        }

        // renderTooltip with list of FormattedCharSequence and Font
        private void test12(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderTooltip(poseStack, List.of(new TextComponent("test").getVisualOrderText()), mouseX, mouseY, this.testFont);
        }

        // legacy ToolTip methods
        @SuppressWarnings("removal")
        private void test13(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderToolTip(poseStack, List.of(new TextComponent("test").getVisualOrderText()), mouseX, mouseY, this.testFont);
        }

        @SuppressWarnings("removal")
        private void test14(Button button, PoseStack poseStack, int mouseX, int mouseY)
        {
            renderComponentToolTip(poseStack, List.of(new TextComponent("test")), mouseX, mouseY, this.testFont);
        }
    }

}
