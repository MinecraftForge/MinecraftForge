package net.minecraftforge.debug.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod("extended_button_glow_on_hover_test")
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "extended_button_glow_on_hover_test")
public class ExtendedButtonGlowOnHoverTest {
   private static final boolean ENABLED = true;

   @SubscribeEvent
   public static void replaceButton(GuiScreenEvent.InitGuiEvent.Post event){
      if(ENABLED && event.getGui() instanceof MainMenuScreen) {
         List<Widget> widgets = event.getWidgetList();
         Screen screen = event.getGui();
         event.removeWidget(widgets.get(5));
         event.addWidget(new ExtendedButton(
                 screen.width / 2 - 100,
                 screen.height / 4 + 48 + 72 + 12,
                 98, 20, I18n.format("menu.options"),
                 (button) -> Minecraft.getInstance().displayGuiScreen(new OptionsScreen(screen, Minecraft.getInstance().gameSettings))));
      }
   }
}
