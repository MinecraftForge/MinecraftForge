/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

@Mod("overlay_layers_test")
public class OverlayLayersTest
{
    public static final boolean ENABLE = true;
    private boolean visible = false;

    public OverlayLayersTest()
    {
        if (ENABLE && FMLEnvironment.dist == Dist.CLIENT)
        {
            MinecraftForge.EVENT_BUS.addListener(this::renderTextEvent);
            MinecraftForge.EVENT_BUS.addListener(this::keyInputEvent);
        }
    }

    int overlayIndex = 0;

    public void renderTextEvent(RenderGameOverlayEvent.Text event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT || !visible)
            return;

        List<OverlayRegistry.OverlayEntry> overlays = OverlayRegistry.orderedEntries();
        for(int i=0;i<overlays.size();i++)
        {
            OverlayRegistry.OverlayEntry entry = overlays.get(i);
            event.getLeft().add(String.format(Locale.ENGLISH, overlayIndex == i ? "> %s [%s] <" : "  %s [%s]  ", entry.getDisplayName(), entry.isEnabled()));
        }
    }

    public void keyInputEvent(InputEvent.KeyInputEvent event)
    {
        if (event.getAction() != GLFW.GLFW_PRESS || Minecraft.getInstance().level == null || Minecraft.getInstance().screen != null)
            return;

        if (event.getKey() == GLFW.GLFW_KEY_H)
        {
            visible = !visible;
            return;
        }
        if (!visible)
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
