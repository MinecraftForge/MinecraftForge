package net.minecraftforge.client.gui.config;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.forgespi.language.IModInfo;

@EventBusSubscriber(bus = Bus.MOD, modid = "forge", value = Dist.CLIENT)
public class CustomConfigScreenTest extends ModConfigScreen {

    private final FontRenderer fontRenderer;
    private final ConfigElementControls configElementControls;

    public CustomConfigScreenTest(Screen screen, IModInfo mod) {
        super(screen, mod);
        Font font = new Font(Minecraft.getInstance().getTextureManager(), FontResourceManager.field_238544_a_);
        font.setGlyphProviders(Lists.newArrayList(new DefaultGlyphProvider()));
        fontRenderer = new FontRenderer(rl -> font);
        configElementControls = new ConfigElementControls() {
            @Override
            public ConfigElementTextField createTextField(ITextComponent title) {
                return new TestColorTextField(title);
            }

            @Override
            public Button createButton(ITextComponent title, Button.IPressable iPressable) {
                return new ConfigElementButton(title, iPressable) {
                    @Override
                    public void func_230431_b_(MatrixStack mStack, int mouseX, int mouseY, float partial) {
                        setFGColor((int) (Math.random() * (double) 0xFFFFFF));
                        super.func_230431_b_(mStack, mouseX, mouseY, partial);
                    }
                };
            }
        };
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModList.get().getModContainerById("forge").ifPresent(forge -> {
            forge.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new CustomConfigScreenTest(screen, forge.getModInfo()));
        });
    }

    @Override
    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    @Override
    public ConfigElementControls getControlCreator() {
        return configElementControls;
    }
}
