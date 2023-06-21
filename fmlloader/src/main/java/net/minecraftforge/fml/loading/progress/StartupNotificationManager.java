/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.progress;

import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StartupNotificationManager {
    private static volatile EnumMap<Message.MessageType, List<Message>> messages = new EnumMap<>(Message.MessageType.class);

    private static final Deque<ProgressMeter> progressMeters = new ArrayDeque<>();

    public static List<ProgressMeter> getCurrentProgress() {
        synchronized (progressMeters) {
            return progressMeters.stream().toList();
        }
    }

    public static ProgressMeter prependProgressBar(final String barName, final int count) {
        var pm = new ProgressMeter(barName, count, 0, new Message(barName, Message.MessageType.ML));
        synchronized (progressMeters) {
            progressMeters.addLast(pm);
        }
        return pm;
    }
    public static ProgressMeter addProgressBar(final String barName, final int count) {
        var pm = new ProgressMeter(barName, count, 0, new Message(barName, Message.MessageType.ML));
        synchronized (progressMeters) {
            progressMeters.push(pm);
        }
        return pm;
    }

    public static void popBar(final ProgressMeter progressMeter) {
        synchronized (progressMeters) {
            progressMeters.remove(progressMeter);
        }
    }

    public record AgeMessage(int age, Message message) {}
    public static List<AgeMessage> getMessages() {
        final long ts = System.nanoTime();
        return messages.values().stream().flatMap(Collection::stream)
                .sorted(Comparator.comparingLong(Message::timestamp).thenComparing(Message::getText).reversed())
                .map(m -> new AgeMessage((int) ((ts - m.timestamp()) / 1000000), m))
                .limit(2)
                .toList();
    }

    private synchronized static void addMessage(Message.MessageType type, String message, int maxSize)
    {
        EnumMap<Message.MessageType, List<Message>> newMessages = new EnumMap<>(messages);
        newMessages.compute(type, (key, existingList) -> {
            List<Message> newList = new ArrayList<>();
            if (existingList != null)
            {
                if (maxSize < 0)
                {
                    newList.addAll(existingList);
                }
                else
                {
                    newList.addAll(existingList.subList(0, Math.min(existingList.size(), maxSize)));
                }
            }
            newList.add(new Message(message, type));
            return newList;
        });
        messages = newMessages;
    }

    public static void addModMessage(final String message) {
        final String safeMessage = Ascii.truncate(CharMatcher.ascii().retainFrom(message),80,"~");
        addMessage(Message.MessageType.MOD, safeMessage, 20);
    }

    public static Optional<Consumer<String>> modLoaderConsumer() {
        return Optional.of(s-> addMessage(Message.MessageType.ML, s, -1));
    }

    public static Optional<Consumer<String>> locatorConsumer() {
        return Optional.of(s -> addMessage(Message.MessageType.LOC, s, -1));
    }

    public static Optional<Consumer<String>> mcLoaderConsumer() {
        return Optional.of(s-> addMessage(Message.MessageType.MC, s, -1));
    }
}
