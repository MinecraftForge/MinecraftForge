package net.minecraftforge.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.event.RegisterPictureInPictureRendererEvent;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.Color;


@Mod(RegisterPictureInPictureRendererEventTest.MODID)
public class RegisterPictureInPictureRendererEventTest
{
    private static final boolean ENABLED = true;

    public static final String MODID = "pip_registration_event_test";

    public RegisterPictureInPictureRendererEventTest() {
        if(ENABLED) {
            RegisterPictureInPictureRendererEvent.BUS.addListener(this::registerTestPip);
            ScreenEvent.BackgroundRendered.BUS.addListener(this::onScreenBackground);
        }
    }

    // this will draw a blue box outline in red in the top left corner of a screen.
    private void onScreenBackground(ScreenEvent.BackgroundRendered event) {
        var window = Minecraft.getInstance().getWindow();
        System.out.println(event.getScreen().getClass());
        event.getGuiGraphics().getRenderState().submitPicturesInPictureState(new TestPipRendererState(
                event.getGuiGraphics(),
                10,
                10,
                20,
                20,
                new ScreenRectangle(new ScreenPosition(0, 0), window.getGuiScaledWidth(), window.getGuiScaledHeight())));
    }

    private void registerTestPip(RegisterPictureInPictureRendererEvent event) {
        event.register(new TestPipRenderer(event.getBufferSource()));
    }


    static class TestPipRenderer extends PictureInPictureRenderer<TestPipRendererState> {

        protected TestPipRenderer(MultiBufferSource.BufferSource bufferSource) {
            super(bufferSource);
        }

        @Override
        public Class<TestPipRendererState> getRenderStateClass() {
            return TestPipRendererState.class;
        }

        @Override
        protected void renderToTexture(TestPipRendererState state, PoseStack poseStack) {
            var graphics = state.graphics();
            int red = Color.red.getRGB();
            int x1 = state.x0 - 1;
            int y1 = state.y0 - 1;
            int x2 = x1 + state.x1() + 2;
            int y2 = y1 + state.y1() + 2;

            graphics.fill(x1 + 2, y1+ 2, x2, y2, Color.BLUE.getRGB());
            graphics.fill(x1, y1, x2, y1 + 2, red);
            graphics.fill(x1, y2, x2, y2 + 2, red);
            graphics.fill(x1, y1, x1 + 2, y2, red);
            graphics.fill(x2, y1, x2 + 2, y2 + 2, red);


        }

        @Override
        protected String getTextureLabel()
        {
            return "test_pip_renderer";
        }
    }

    record TestPipRendererState(GuiGraphics graphics, int x0, int y0, int x1, int y1, ScreenRectangle scissorArea, ScreenRectangle bounds) implements  PictureInPictureRenderState {
        public TestPipRendererState(GuiGraphics graphics, int x0, int y0, int x1, int y1, ScreenRectangle scissorArea) {
            this(graphics, x0, y0, x1, y1, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
        }

        @Override
        public float scale() {
            return 32;
        }
    }
}
