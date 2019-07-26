/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml;

import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StartupMessageManager {
    private static final EnumMap<MessageType, List<Message>> messages = new EnumMap<>(MessageType.class);
    static {
        Arrays.stream(MessageType.values()).forEach(mt->messages.computeIfAbsent(mt, k->new CopyOnWriteArrayList<>()));
    }

    public static List<Pair<Integer,Message>> getMessages() {
        final long ts = System.nanoTime();
        return messages.values().stream().flatMap(Collection::stream).
                sorted(Comparator.comparingLong(Message::getTimestamp).thenComparing(Message::getText).reversed()).
                map(m -> Pair.of((int) ((ts - m.timestamp) / 1e6), m)).
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
        MC(0.0f, 0.0f, 0.0f),
        ML(0.0f, 0.0f, 0.5f),
        MOD(0.5f, 0.0f, 0.0f);

        private final float[] colour;

        MessageType(final float r, final float g, final float b) {
            colour = new float[] {r,g,b};
        }

        public float[] colour() {
            return colour;
        }
    }

    public static void addModMessage(final String message) {
        final String safeMessage = Ascii.truncate(CharMatcher.ascii().retainFrom(message),80,"~");
        final List<Message> messages = StartupMessageManager.messages.get(MessageType.MOD);
        messages.subList(0, Math.max(0, messages.size() - 20)).clear();
        messages.add(new Message(safeMessage, MessageType.MOD));
    }

    static Optional<Consumer<String>> modLoaderConsumer() {
        return Optional.of(s-> messages.get(MessageType.ML).add(new Message(s, MessageType.ML)));
    }

    static Optional<Consumer<String>> mcLoaderConsumer() {
        return Optional.of(s-> messages.get(MessageType.MC).add(new Message(s, MessageType.MC)));
    }
}
