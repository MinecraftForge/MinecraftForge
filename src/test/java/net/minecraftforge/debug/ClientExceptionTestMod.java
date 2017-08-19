package net.minecraftforge.debug;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "clientexceptiontest", version = "0", name = "Client Exception Test")
public class ClientExceptionTestMod {

    // Disabled so other test mods can still work.
    public static boolean ENABLE_PREINIT = false, ENABLE_INIT = true;
    private static String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onPreInit(FMLPreInitializationEvent e) {
        if (ENABLE_PREINIT)
        {
            throw new CustomModLoadingErrorDisplayException("Custom Test Exception", new RuntimeException("Thrown in Pre-Init"))
            {
                @Override
                public void initGui(GuiErrorScreen parent, FontRenderer fontRenderer) {}

                @Override
                public void drawScreen(GuiErrorScreen parent, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
                    parent.drawCenteredString(parent.mc.fontRenderer, "Custom Test Exception", parent.width / 2, 90, 16777215);
                    parent.drawCenteredString(parent.mc.fontRenderer, LOREM_IPSUM, parent.width / 2, 110, 16777215);
                }
            };
        }
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onInit(FMLInitializationEvent e) {
        if (ENABLE_INIT)
        {
            throw new CustomModLoadingErrorDisplayException("Custom Test Exception", new RuntimeException("Thrown in Initialization"))
            {
                @Override
                public void initGui(GuiErrorScreen parent, FontRenderer fontRenderer) {}

                @Override
                public void drawScreen(GuiErrorScreen parent, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
                    parent.drawCenteredString(parent.mc.fontRenderer, "Custom Test Exception", parent.width / 2, 90, 16777215);
                    parent.drawCenteredString(parent.mc.fontRenderer, LOREM_IPSUM, parent.width / 2, 110, 16777215);
                }
            };
        }
    }
}
