package net.minecraftforge.debug.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

@Mod("gui_open_event_test")
@Mod.EventBusSubscriber
public class GuiOpenEventTest
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean IS_ENABLE = false;

    @SubscribeEvent
    public static void onGuiOpenEvent(GuiOpenEvent event)
    {
        if (IS_ENABLE)
        {
            Screen gui = event.getGui();
            Screen oldGui = event.getOldGui();

            LOGGER.info("[GuiOpenEventTest] new gui: {}", gui);
            LOGGER.info("[GuiOpenEventTest] old gui: {}", oldGui);

            // tests
            try
            {
                Field field = null;

                if (gui instanceof ChatOptionsScreen)
                    field = SettingsScreen.class.getDeclaredField("parentScreen");
                else if (gui instanceof ShareToLanScreen)
                    field = ShareToLanScreen.class.getDeclaredField("lastScreen");

                if (field != null)
                {
                    field.setAccessible(true);
                    Screen parentGui = (Screen) field.get(gui);

                    if (oldGui == parentGui)
                        LOGGER.info("[GuiOpenEventTest] YEAH! parent gui {}", parentGui);
                }
            }
            catch (IllegalAccessException | NoSuchFieldException e)
            {
                e.printStackTrace();
            }

            // example use case
            if (gui instanceof ChatOptionsScreen || gui instanceof ShareToLanScreen)
                event.setGui(new MyCustomScreen(oldGui));
        }
    }

    private static class MyCustomScreen extends Screen
    {
        private final Screen oldGui;

        public MyCustomScreen(Screen oldGui)
        {
            super(new StringTextComponent("My Custom Screen"));
            this.oldGui = oldGui;
        }

        // init gui
        @Override
        protected void func_231160_c_()
        {
            int x = (this.field_230708_k_ - 150) / 2;
            int y = (this.field_230709_l_ - 20) / 2;

            // add close button
            this.func_230480_a_(new Button(x, y, 150, 20, new StringTextComponent("Done"), (p_213085_1_) -> {
                LOGGER.info("[GuiOpenEventTest] Done Button - onPress");
                this.field_230706_i_.displayGuiScreen(this.oldGui); // return to parent gui
            }));
        }

        // draw gui
        @Override
        public void func_230430_a_(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
            this.func_230446_a_(p_230430_1_); // draw background
            super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }
    }
}
