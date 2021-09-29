/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.gui.MouseCursorImage;
import net.minecraftforge.client.gui.MouseCursorImage.StandardCursor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod(CursorTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CursorTest
{
    public static final String MODID = "cursor_test";
    private static final boolean ENABLED = false;

    private static List<Button> buttons;

    @SubscribeEvent
    public static void onGuiInitPost(final InitGuiEvent.Post event)
    {
        if (ENABLED && event.getGui() instanceof TitleScreen)
        {
            buttons = event.getWidgetList().stream().filter(w -> w instanceof Button).map(Button.class::cast).toList();
        }
        else if (ENABLED)
        {
            MouseCursorImage.resetCursor();
        }
    }

    @SubscribeEvent
    public static void onGuiRenderPost(final DrawScreenEvent.Post event)
    {
        if (ENABLED && event.getGui() instanceof TitleScreen && buttons != null)
        {
            if (buttons.size() > 0 && buttons.get(0).isHovered())
            {
                MouseCursorImage.setStandardCursor(StandardCursor.HAND);
            }
            else if (buttons.size() > 1 && buttons.get(1).isHovered())
            {
                MouseCursorImage.setCursorImage(new ResourceLocation(MODID, "textures/test_cursor.png")).setHotspot(12, 12);
            }
            else
            {
                MouseCursorImage.resetCursor();
            }
        }
    }
}
