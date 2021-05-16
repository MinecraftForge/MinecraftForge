package net.minecraftforge.debug.client;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod("overlay_layers_test")
public class OverlayLayersTest
{
    public OverlayLayersTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::renderTextEvent);
        MinecraftForge.EVENT_BUS.addListener(this::keyInputEvent);
    }

    int overlayIndex = 0;

    public void renderTextEvent(RenderGameOverlayEvent.Text event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT)
            return;

        List<OverlayRegistry.OverlayEntry> overlays = OverlayRegistry.orderedEntries();
        for(int i=0;i<overlays.size();i++)
        {
            OverlayRegistry.OverlayEntry entry = overlays.get(i);
            event.getLeft().add(String.format(overlayIndex == i ? "> %s [%s] <" : "  %s [%s]  ", entry.getDisplayName(), entry.isEnabled()));
        }
    }

    public void keyInputEvent(InputEvent.KeyInputEvent event)
    {
        if (event.getAction() != GLFW.GLFW_PRESS)
            return;

        if (event.getKey() == GLFW.GLFW_KEY_J)
        {
            List<OverlayRegistry.OverlayEntry> overlays = OverlayRegistry.orderedEntries();
            if (overlayIndex >= overlays.size())
                overlayIndex = 0;
            else
                overlayIndex = (overlayIndex + overlays.size() - 1) % overlays.size();
        }
        else if (event.getKey() == GLFW.GLFW_KEY_K)
        {
            List<OverlayRegistry.OverlayEntry> overlays = OverlayRegistry.orderedEntries();
            if (overlayIndex >= overlays.size())
                overlayIndex = 0;
            else
                overlayIndex = (overlayIndex + 1) % overlays.size();
        }
        else if (event.getKey() == GLFW.GLFW_KEY_I)
        {
            List<OverlayRegistry.OverlayEntry> overlays = OverlayRegistry.orderedEntries();
            if (overlayIndex >= overlays.size())
                overlayIndex = 0;
            else
            {
                OverlayRegistry.OverlayEntry entry = overlays.get(overlayIndex);
                OverlayRegistry.enableOverlay(entry.getOverlay(), !entry.isEnabled());
            }
        }
    }
}
