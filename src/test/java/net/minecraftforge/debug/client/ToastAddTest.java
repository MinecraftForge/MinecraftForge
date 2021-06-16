package net.minecraftforge.debug.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod("toast_add_test")
@Mod.EventBusSubscriber
public class ToastAddTest
{
    @SubscribeEvent
    public static void onToastAdd(ToastAddEvent event)
    {
        IToast toast = event.getToast();

        if (toast instanceof RecipeToast)
        {
            event.setCanceled(true);
            return;
        }

        if (toast instanceof SystemToast && ((SystemToast) toast).getToken().equals(SystemToast.Type.TUTORIAL_HINT))
        {
            event.setToast(new CustomToast("Tutorial hints have been disabled"));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class CustomToast implements IToast
    {
        private final String text;

        public CustomToast(String text)
        {
            this.text = text;
        }

        @Override
        public Visibility render(MatrixStack matrixStack, ToastGui toastGui, long ticks)
        {
            toastGui.getMinecraft().font.draw(matrixStack, text, 0, 0, -1);
            return ticks >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
        }
    }
}
