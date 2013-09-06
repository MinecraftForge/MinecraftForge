package net.minecraftforge.client.event;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import org.lwjgl.input.Mouse;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 2:46 PM, 9/4/13
 */
@Cancelable
public class MouseEvent extends Event
{
    public final int x;
    public final int y;
    public final int dx;
    public final int dy;
    public final int dwheel;
    public final int button;
    public final boolean buttonstate;
    public final long nanoseconds;

    public MouseEvent()
    {
        this.x = Mouse.getEventX();
        this.y = Mouse.getEventY();
        this.dx = Mouse.getEventDX();
        this.dy = Mouse.getEventDY();
        this.dwheel = Mouse.getEventDWheel();
        this.button = Mouse.getEventButton();
        this.buttonstate = Mouse.getEventButtonState();
        this.nanoseconds = Mouse.getEventNanoseconds();
    }
}
