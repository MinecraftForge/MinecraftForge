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

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod(GuiLayeringTest.MODID)
public class GuiLayeringTest
{
    private static final Random RANDOM = new Random();
    public static final String MODID="gui_layer_test";

    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid=MODID, bus= Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents
    {
        @SubscribeEvent
        public static void guiOpen(GuiScreenEvent.InitGuiEvent event)
        {
            if (event.getGui() instanceof ContainerScreen)
            {
                event.addWidget(new Button(2, 2, 150, 20, new StringTextComponent("Test Gui Layering"), btn -> {
                    Minecraft.getInstance().pushGuiLayer(new TestLayer(new StringTextComponent("LayerScreen")));
                }));
                event.addWidget(new Button(2, 25, 150, 20, new StringTextComponent("Test Gui Normal"), btn -> {
                    Minecraft.getInstance().setScreen(new TestLayer(new StringTextComponent("LayerScreen")));
                }));
            }
        }

        public static class TestLayer extends Screen
        {
            protected TestLayer(ITextComponent titleIn)
            {
                super(titleIn);
            }

            @Override
            public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks)
            {
                this.renderBackground(mStack);
                drawString(mStack, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
                super.render(mStack, mouseX, mouseY, partialTicks);
            }

            @Override
            protected void init()
            {
                int buttonWidth = 150;
                int buttonHeight = 20;
                int buttonGap = 4;
                int buttonSpacing = (buttonHeight + buttonGap);
                int buttons = 3;

                int xoff = (this.width - buttonWidth);
                int yoff = (this.height - buttonHeight - buttonSpacing * (buttons - 1));
                int cnt = 0;

                xoff = RANDOM.nextInt(xoff);
                yoff = RANDOM.nextInt(yoff);

                this.addButton(new Button(xoff, yoff + buttonSpacing * (cnt++), buttonWidth, buttonHeight, new StringTextComponent("Push New Layer"), this::pushLayerButton));
                this.addButton(new Button(xoff, yoff + buttonSpacing * (cnt++), buttonWidth, buttonHeight, new StringTextComponent("Pop Current Layer"), this::popLayerButton));
                this.addButton(new Button(xoff, yoff + buttonSpacing * (cnt++), buttonWidth, buttonHeight, new StringTextComponent("Close entire stack"), this::closeStack));
            }

            private void closeStack(Button button)
            {
                this.minecraft.setScreen(null);
            }

            private void popLayerButton(Button button)
            {
                this.minecraft.popGuiLayer();
            }

            private void pushLayerButton(Button button)
            {
                this.minecraft.pushGuiLayer(new TestLayer(new StringTextComponent("LayerScreen")));
            }
        }
    }
}
