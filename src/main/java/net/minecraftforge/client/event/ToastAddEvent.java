package net.minecraftforge.client.event;

import net.minecraft.client.gui.toasts.IToast;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ToastAddEvent extends Event {
    private IToast toast;
    public ToastAddEvent(IToast toast) {
        setToast(toast);
    }

    public IToast getToast() {
        return toast;
    }

    public void setToast(IToast toast) {
        this.toast = toast;
    }
}
