package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
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
    static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register("test_item", () -> new CustomItem(new Item.Properties()));

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

    static record CustomTooltip(String text) implements TooltipComponent
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
            GuiComponent.fill(poseStack, x, y,  x + 10, y+ 10, 0xFFFF0000);
        }
    }

    static class CustomItem extends Item
    {
        public CustomItem(Properties properties) {
            super(properties);
        }

        @Override
        public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
            return Optional.of(new CustomTooltip("Cool text"));
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
    }


}
