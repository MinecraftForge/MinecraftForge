package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Event;

public class PauseGameEvent extends Event {
    private final boolean paused;
    public PauseGameEvent(boolean isPaused) {
        this.paused = isPaused;
    }

    public boolean isPaused() {
        return paused;
    }
}