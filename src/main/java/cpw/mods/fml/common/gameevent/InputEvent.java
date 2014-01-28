package cpw.mods.fml.common.gameevent;

import cpw.mods.fml.common.eventhandler.Event;

public class InputEvent extends Event {
    public static class MouseInputEvent extends InputEvent {}
    public static class KeyInputEvent extends InputEvent {}
}