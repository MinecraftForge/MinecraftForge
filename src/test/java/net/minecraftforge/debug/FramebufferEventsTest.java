/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.FramebufferEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "framebuffereventstest", name = "Framebuffer Events Test", version = "0.0.0", clientSideOnly = true)
@Mod.EventBusSubscriber(Side.CLIENT)
public class FramebufferEventsTest
{
    public static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onRenderOutlines(FramebufferEvent.RenderEntityOutline event)
    {
        if (!ENABLED)
            return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        // Hold clay to cancel outlines, they're totally overpowered and in need of balancing!
        if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.CLAY_BALL)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderShaders(FramebufferEvent.RenderShaders event)
    {
        if (!ENABLED)
            return;
        // Go into spectator mode and "possess" a spider, then behold 5 mighty red rectangles!
        renderRectangle(0xFFFF0000);
    }

    @SubscribeEvent
    public static void onRenderBuffers(FramebufferEvent.RenderBuffers event)
    {
        if (!ENABLED)
            return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        // Hold a slime ball to coat your screen with it, then "possess" a spider in spectator mode to see 5 small red rectangles and a big green one.
        if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.SLIME_BALL)
        {
            renderRectangle(0xFF00FF00);
        }
    }

    private static void renderRectangle(int color)
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.pushMatrix();
        ScaledResolution resolution = new ScaledResolution(mc);
        mc.entityRenderer.setupOverlayRendering();
        int width = resolution.getScaledWidth() / 2;
        int height = resolution.getScaledHeight() / 2;
        int x = width / 2;
        int y = height / 2;
        GuiScreen.drawRect(x, y, x + width, y + height, color);
        GlStateManager.popMatrix();
    }
}

