package net.minecraftforge.debug;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "clientexceptiontest", version = "1.0", name = "Client Exception Test", clientSideOnly = true)
public class ClientExceptionTestMod
{

    // Disabled so other test mods can still work.
    public static boolean ENABLE_PREINIT = false;
    public static boolean ENABLE_INIT = false;
    public static boolean ENABLE_LOAD_COMPLETE = false;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e)
    {
        if (ENABLE_PREINIT)
        {
            throwException("Thrown in Pre-Init");
        }
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent e)
    {
        if (ENABLE_INIT)
        {
            throwException("Thrown in Init");
        }
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent e)
    {
        if (ENABLE_LOAD_COMPLETE)
        {
            throwException("Thrown in load complete");
        }
    }

    private void throwException(String runtimeMessage)
    {
        throw new CustomModLoadingErrorDisplayException("Custom Test Exception", new RuntimeException(runtimeMessage))
        {
            @Override
            public void initGui(GuiErrorScreen parent, FontRenderer fontRenderer) {}

            @Override
            public void drawScreen(GuiErrorScreen parent, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime)
            {
                parent.drawCenteredString(parent.mc.fontRenderer, "Custom Test Exception", parent.width / 2, 90, 16777215);
                parent.drawCenteredString(parent.mc.fontRenderer, runtimeMessage, parent.width / 2, 110, 16777215);
            }
        };
    }
}
