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

import javax.annotation.Nullable;
import java.util.function.Consumer;

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

    /**
     * Just testing to check
     */
    public static class TestColorTextField extends ConfigElementControls.ConfigElementTextField {
        public TestColorTextField(ITextComponent title) {
            super(title);
        }

        @Override
        public void setResponder(@Nullable Consumer<String> responderIn) {
            super.setResponder(newText -> {
                if (responderIn != null)
                    responderIn.accept(newText);
                setColor(newText);
            });
        }

        private void setColor(String newText) {
            if (newText == null)
                return;
            if (newText.matches("#[0-9A-f][0-9A-f][0-9A-f]")) {
                String r = newText.substring(1, 2);
                String g = newText.substring(2, 3);
                String b = newText.substring(3, 4);
                newText = "#" + r + r + g + g + b + b;
            }
            if (newText.matches("#[0-9A-f][0-9A-f][0-9A-f][0-9A-f][0-9A-f][0-9A-f]")) {
                String r = newText.substring(1, 3);
                String g = newText.substring(3, 5);
                String b = newText.substring(5, 7);
                int ir;
                int ig;
                int ib;
                try {
                    ir = Integer.parseInt(r, 16);
                    ig = Integer.parseInt(g, 16);
                    ib = Integer.parseInt(b, 16);
                } catch (NumberFormatException e) {
                    setTextColor(ConfigElementControls.RED);
                    return;
                }
                setTextColor(ir << 16 | ig << 8 | ib);
            }
        }
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
