/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.progress;

import java.util.concurrent.atomic.AtomicInteger;

public final class ProgressMeter {
    private final String name;
    private final int steps;
    private AtomicInteger current;
    private Message label;

    public ProgressMeter(String name, int steps, int current, Message label) {
        this.name = name;
        this.steps = steps;
        this.current = new AtomicInteger(current);
        this.label = label;
    }

    public String name() {
        return name;
    }

    public int steps() {
        return steps;
    }

    public int current() {
        return current.get();
    }

    public Message label() {
        return label;
    }

    public void increment() {
        this.current.incrementAndGet();
    }

    public void complete() {
        StartupNotificationManager.popBar(this);
    }

    public float progress() {
        return current.get()/(float)steps;
    }

    public void setAbsolute(final int absolute) {
        this.current.set(absolute);
    }

    public void label(final String message) {
        this.label = new Message(message, Message.MessageType.ML);
    }
}
