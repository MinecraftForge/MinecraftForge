package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

@Mod(CustomTooltipTest.ID)
public class CustomTooltipTest
{

    static final boolean ENABLED = true;
    static final String ID = "custom_tooltip_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register("test_item", () -> new CustomItemWithTooltip(new Item.Properties()));

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
        public int getHeight() {
            return 10;
        }

        @Override
        public int getWidth(Font font) {
            return 10;
        }

        @Override
        public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer_, int zIndex, TextureManager textureManager) {
            GuiComponent.fill(poseStack, x, y,  x + 10, y+ 10, tooltip.color);
        }
    }

    static class CustomItemWithTooltip extends Item
    {
        public CustomItemWithTooltip(Properties properties) {
            super(properties);
        }

        @Override
        public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
            return Optional.of(new CustomTooltip(0xFFFF0000));
        }
    }


    private static class ClientModBusEventHandler
    {

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            MinecraftForgeClient.registerTooltipComponentFactory(CustomTooltip.class, CustomClientTooltip::new);
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
                event.setBackground(0xFF0000FF);
                event.setBorderStart(0xFFFF0000);
                event.setBorderEnd(0xFFFFFF00);
            }
        }

    }


}
