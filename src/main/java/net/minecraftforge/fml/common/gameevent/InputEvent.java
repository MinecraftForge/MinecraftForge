package net.minecraftforge.fml.common.gameevent;

import net.minecraftforge.fml.common.eventhandler.Event;

public class InputEvent extends Event {
    public static class MouseInputEvent extends InputEvent {}
    public static class KeyInputEvent extends InputEvent {}
}
