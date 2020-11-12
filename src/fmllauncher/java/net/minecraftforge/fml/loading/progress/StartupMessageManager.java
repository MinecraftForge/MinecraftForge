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

package net.minecraftforge.fml.loading.progress;

import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StartupMessageManager {

    private static volatile Map<MessageType, List<Message>> messages = ImmutableMap.of();

    public static List<Pair<Integer,Message>> getMessages() {
        final long ts = System.nanoTime();
        return messages.values().stream().flatMap(Collection::stream).
                sorted(Comparator.comparingLong(Message::getTimestamp).thenComparing(Message::getText).reversed()).
                map(m -> Pair.of((int) ((ts - m.timestamp) / 1e6), m)).
                limit(5).
                collect(Collectors.toList());
    }

    public static class Message {
        private final String text;
        private final MessageType type;
        private final long timestamp;

        public Message(final String text, final MessageType type) {
            this.text = text;
            this.type = type;
            this.timestamp = System.nanoTime();
        }

        public String getText() {
            return text;
        }

        MessageType getType() {
            return type;
        }

        long getTimestamp() {
            return timestamp;
        }

        public float[] getTypeColour() {
            return type.colour();
        }
    }

    enum MessageType {
        MC(1.0f, 1.0f, 1.0f),
        ML(0.0f, 0.0f, 0.5f),
        LOC(0.0f, 0.5f, 0.0f),
        MOD(0.5f, 0.0f, 0.0f);

        private final float[] colour;

        MessageType(final float r, final float g, final float b) {
            colour = new float[] {r,g,b};
        }

        public float[] colour() {
            return colour;
        }
    }

    private synchronized static void addMessage(MessageType type, String message, int maxSize)
    {
        Map<MessageType, List<Message>> messages = StartupMessageManager.messages;

        List<Message> oldMessagesForType = messages.getOrDefault(type, ImmutableList.of());
        List<Message> newMessagesForType = ImmutableList.<Message>builder()
                .addAll(maxSize < 0 ? oldMessagesForType : oldMessagesForType.subList(0, Math.min(oldMessagesForType.size(), maxSize)))
                .add(new Message(message, type))
                .build();

        ImmutableMap.Builder<MessageType, List<Message>> builder = ImmutableMap.builder();

        StartupMessageManager.messages.entrySet().stream()
                .filter(entry -> entry.getKey() != type)
                .forEach(builder::put);

        builder.put(type, newMessagesForType);

        StartupMessageManager.messages = builder.build();
    }

    public static void addModMessage(final String message) {
        final String safeMessage = Ascii.truncate(CharMatcher.ascii().retainFrom(message),80,"~");
        addMessage(MessageType.MOD, safeMessage, 20);
    }

    public static Optional<Consumer<String>> modLoaderConsumer() {
        return Optional.of(s-> addMessage(MessageType.ML, s, -1));
    }

    public static Optional<Consumer<String>> locatorConsumer() {
        return Optional.of(s -> addMessage(MessageType.LOC, s, -1));
    }

    public static Optional<Consumer<String>> mcLoaderConsumer() {
        return Optional.of(s-> addMessage(MessageType.MC, s, -1));
    }
}
