/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(CustomTooltipTest.ID)
public class CustomTooltipTest {
    static final boolean ENABLED = true;
    static final String ID = "custom_tooltip_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register("test_item", () -> new CustomItemWithTooltip(new Item.Properties()));

    public CustomTooltipTest() {
        if (ENABLED) {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            if (FMLEnvironment.dist.isClient()) {
                MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
                modEventBus.register(ClientModBusEventHandler.class);
            }
            ITEMS.register(modEventBus);
            modEventBus.addListener(this::addCreative);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(CUSTOM_ITEM);
    }

    record CustomTooltip(int color) implements TooltipComponent {}

    record CustomClientTooltip(CustomTooltip tooltip) implements ClientTooltipComponent {
        @Override
        public int getHeight() {
            return 10;
        }

        @Override
        public int getWidth(Font font) {
            return 10;
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
            graphics.fill(x, y, x + 10, y + 10,tooltip.color);
        }
    }

    static class CustomItemWithTooltip extends Item {
        public CustomItemWithTooltip(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
            super.appendHoverText(stack, level, components, flag);
            components.add(Component.literal("This is a very very very very very very long hover text that should really really be split across multiple lines.").withStyle(ChatFormatting.YELLOW));
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            if (level.isClientSide)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TooltipTestScreen::show);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        @Override
        public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
            return Optional.of(new CustomTooltip(0xFFFF0000));
        }
    }


    private static class ClientModBusEventHandler {
        @SubscribeEvent
        public static void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(CustomTooltip.class, CustomClientTooltip::new);
        }
    }

    private static class ClientEventHandler {
        @SubscribeEvent
        public static void gatherTooltips(RenderTooltipEvent.GatherComponents event) {
            if (event.getItemStack().getItem() == Items.STICK)
                event.getTooltipElements().add(Either.right(new CustomTooltip(0xFF0000FF)));
            if (event.getItemStack().getItem() == Items.CLOCK)
                event.setMaxWidth(30);
        }

        @SubscribeEvent
        public static void preTooltip(RenderTooltipEvent.Color event) {
            if (event.getItemStack().getItem() == Items.APPLE) {
                event.setBackgroundStart(0xFF0000FF);
                event.setBackgroundEnd(0xFFFFFF00);
                event.setBorderStart(0xFFFF0000);
                event.setBorderEnd(0xFF000011);
            }
        }
    }

    static class TooltipTestScreen extends Screen {
        private ItemStack testStack = ItemStack.EMPTY;
        private boolean testFont = false;

        protected TooltipTestScreen() {
            super(Component.literal("TooltipMethodTest"));
        }

        static void show() {
            Minecraft.getInstance().setScreen(new TooltipTestScreen());
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            this.renderBackground(graphics, mouseX, mouseY, partialTicks);
            super.render(graphics, mouseX, mouseY, partialTicks);
            graphics.drawString(font, "* must have Stack, # must have custom font", 0, 0, 0xFFFFFF);
        }

        @Override
        protected void init() {
            addRenderableWidget(Button.builder(Component.literal("Toggle Stack: EMPTY"), button -> {
                this.testStack = this.testStack.isEmpty() ? new ItemStack(Items.APPLE) : ItemStack.EMPTY;
                button.setMessage(Component.literal("Toggle Stack: " + (testStack.isEmpty() ? "EMPTY" : "Apple")));
            }).pos(10, 10).size(200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Toggle Font: Default"), button -> {
                this.testFont = !this.testFont;
                button.setMessage(Component.literal("Toggle Font: " + (this.testFont ? "Unicode" : "Default")));
            }).pos(220, 10).size(200, 20).build());

            // * must have stack context
            // # must have custom font
            List<Map.Entry<String, TooltipTest>> tooltipTests = Arrays.asList(
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
            for (var test : tooltipTests) {
                addRenderableWidget(new Button.Builder(Component.literal(test.getKey()), button -> {})
                        .bounds(x, y, 100, 20).build(b -> new Button(b) {
                            @Override
                            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                                super.renderWidget(graphics, mouseX, mouseY, partialTick);

                                boolean showTooltip = this.isHovered || this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
                                if (showTooltip)
                                    test.getValue().render(graphics, mouseX, mouseY);
                            }
                        }));
                y+= 22;
                if (y >= height - 50) {
                    y = 50;
                    x += 110;
                }
            }
        }

        private Component getTestComponent(boolean testFont) {
            return Component.literal("test").withStyle(s -> s.withFont(testFont ? Minecraft.UNIFORM_FONT : Minecraft.DEFAULT_FONT));
        }

        private void test1(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, this.testStack, mouseX, mouseY);
        }

        // renderTooltip with List<Component> and all combinations of ItemStack/Font
        private void test2(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, List.of(this.getTestComponent(false)), Optional.empty(), mouseX, mouseY);
        }

        private void test3(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, List.of(this.getTestComponent(this.testFont)), Optional.empty(), mouseX, mouseY);
        }

        private void test4(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, List.of(this.getTestComponent(this.testFont)), Optional.empty(), mouseX, mouseY);
        }

        private void test5(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, List.of(this.getTestComponent(false)), Optional.empty(), mouseX, mouseY);
        }

        // renderTooltip with just Component
        private void test6(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, this.getTestComponent(false), mouseX, mouseY);
        }

        // renderComponentTooltip with all combinations of ItemStack/Font
        private void test7(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderComponentTooltip(this.font, List.of(this.getTestComponent(false)), mouseX, mouseY);
        }

        private void test8(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderComponentTooltip(this.font, List.of(this.getTestComponent(this.testFont)), mouseX, mouseY);
        }

        private void test9(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderComponentTooltip(this.font, List.of(this.getTestComponent(this.testFont)), mouseX, mouseY);
        }

        private void test10(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderComponentTooltip(this.font, List.of(this.getTestComponent(false)), mouseX, mouseY);
        }

        // renderTooltip with list of FormattedCharSequence
        private void test11(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, List.of(this.getTestComponent(false).getVisualOrderText()), mouseX, mouseY);
        }

        // renderTooltip with list of FormattedCharSequence and Font
        private void test12(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, List.of(this.getTestComponent(this.testFont).getVisualOrderText()), mouseX, mouseY);
        }

        // legacy ToolTip methods
        private void test13(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderTooltip(this.font, List.of(this.getTestComponent(this.testFont).getVisualOrderText()), mouseX, mouseY);
        }

        private void test14(GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.renderComponentTooltip(this.font, List.of(this.getTestComponent(this.testFont)), mouseX, mouseY);
        }
    }

    private interface TooltipTest {
        void render(GuiGraphics graphics, int mouseX, int mouseY);
    }
}
