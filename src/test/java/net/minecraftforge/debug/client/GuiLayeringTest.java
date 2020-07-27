/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod("gui_layer_test")
public class GuiLayeringTest
{
    private static final Random RANDOM = new Random();

    public GuiLayeringTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::guiOpen);
    }

    private void guiOpen(GuiScreenEvent.InitGuiEvent event)
    {
        if (event.getGui() instanceof ContainerScreen)
        {
            event.addWidget(new Button(2, 2, 150, 20, new StringTextComponent("Test Gui Layering"), btn -> {
                Minecraft.getInstance().pushGuiLayer(new LayerScreen(new StringTextComponent("LayerScreen")));
            }));
            event.addWidget(new Button(2, 25, 150, 20, new StringTextComponent("Test Gui Normal"), btn -> {
                Minecraft.getInstance().displayGuiScreen(new LayerScreen(new StringTextComponent("LayerScreen")));
            }));
        }
    }

    public class LayerScreen extends Screen
    {
        protected LayerScreen(ITextComponent titleIn)
        {
            super(titleIn);
        }

        @Override
        public void func_230430_a_(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
            this.func_230446_a_(p_230430_1_);
            this.func_238472_a_(p_230430_1_, this.field_230712_o_, this.field_230704_d_, this.field_230708_k_ / 2, 15, 16777215);
            super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }

        @Override
        protected void func_231160_c_() // init
        {
            int buttonWidth = 150;
            int buttonHeight = 20;
            int buttonGap = 4;
            int buttonSpacing = (buttonHeight+buttonGap);
            int buttons = 3;

            int xoff = (this.field_230708_k_-buttonWidth);
            int yoff = (this.field_230709_l_-buttonHeight-buttonSpacing*(buttons-1));
            int cnt = 0;

            xoff = RANDOM.nextInt(xoff);
            yoff = RANDOM.nextInt(yoff);

            this.func_230480_a_(new Button(xoff, yoff + buttonSpacing*(cnt++), buttonWidth, buttonHeight, new StringTextComponent("Push New Layer"), this::pushLayerButton));
            this.func_230480_a_(new Button(xoff, yoff + buttonSpacing*(cnt++), buttonWidth, buttonHeight, new StringTextComponent("Pop Current Layer"), this::popLayerButton));
            this.func_230480_a_(new Button(xoff, yoff + buttonSpacing*(cnt++), buttonWidth, buttonHeight, new StringTextComponent("Close entire stack"), this::closeStack));
        }

        private void closeStack(Button button)
        {
            this.field_230706_i_.displayGuiScreen(null);
        }

        private void popLayerButton(Button button)
        {
            this.field_230706_i_.popGuiLayer();
        }

        private void pushLayerButton(Button button)
        {
            this.field_230706_i_.pushGuiLayer(new LayerScreen(new StringTextComponent("LayerScreen")));
        }
    }
}
