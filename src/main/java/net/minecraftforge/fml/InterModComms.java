/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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


import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InterModComms
{
    public static final class IMCMessage {
        private final String modId;
        private final String method;
        private final String senderModId;
        private final Supplier<?> thing;

        IMCMessage(String senderModId, String modId, String method, Supplier<?> thing)
        {
            this.senderModId = senderModId;
            this.modId = modId;
            this.method = method;
            this.thing = thing;
        }

        /**
         * @return The modid of the sender. This is supplied by the caller, or by the active mod container context.
         * Consider it unreliable.
         */
        public final String getSenderModId() {
            return this.senderModId;
        }

        /**
         * @return The modid being sent to.
         */
        public final String getModId() {
            return this.modId;
        }

        /**
         * @return The method being sent to.
         */
        public final String getMethod() {
            return this.method;
        }

        /**
         * @param <T> The type of the message.
         * @return A {@link Supplier} of the message.
         */
        @SuppressWarnings("unchecked")
        public final <T> Supplier<T> getMessageSupplier() {
            return (Supplier<T>)this.thing;
        }
    }

    private static ConcurrentMap<String, ConcurrentLinkedQueue<IMCMessage>> containerQueues = new ConcurrentHashMap<>();

    /**
     * Send IMC to remote. Sender will default to the active modcontainer, or minecraft if not.
     *
     * @param modId the mod id to send to
     * @param method the method name to send
     * @param thing the thing associated with the method name
     * @return true if the message was enqueued for sending (the target modid is loaded)
     */
    public static boolean sendTo(final String modId, final String method, final Supplier<?> thing) {
        if (!ModList.get().isLoaded(modId)) return false;
        containerQueues.computeIfAbsent(modId, k->new ConcurrentLinkedQueue<>()).add(new IMCMessage(ModLoadingContext.get().getActiveContainer().getModId(), modId, method, thing));
        return true;
    }

    /**
     * Send IMC to remote.
     *
     * @param senderModId the mod id you are sending from
     * @param modId the mod id to send to
     * @param method the method name to send
     * @param thing the thing associated with the method name
     * @return true if the message was enqueued for sending (the target modid is loaded)
     */
    public static boolean sendTo(final String senderModId, final String modId, final String method, final Supplier<?> thing) {
        if (!ModList.get().isLoaded(modId)) return false;
        containerQueues.computeIfAbsent(modId, k->new ConcurrentLinkedQueue<>()).add(new IMCMessage(senderModId, modId, method, thing));
        return true;
    }

    /**
     * Retrieve pending messages for your modid. Use the predicate to filter the method name.
     *
     * @param modId the modid you are querying for
     * @param methodMatcher a predicate for the method you are interested in
     * @return All messages passing the supplied method predicate
     */
    public static Stream<IMCMessage> getMessages(final String modId, final Predicate<String> methodMatcher) {
        ConcurrentLinkedQueue<IMCMessage> queue = containerQueues.get(modId);
        if (queue == null) return Stream.empty();
        return StreamSupport.stream(new QueueFilteringSpliterator(queue, methodMatcher), false);
    }

    /**
     * Retrieve all message for your modid.
     *
     * @param modId the modid you are querying for
     * @return All messages
     */
    public static Stream<IMCMessage> getMessages(final String modId) {
        return getMessages(modId, s->Boolean.TRUE);
    }

    private static class QueueFilteringSpliterator implements Spliterator<IMCMessage>
    {
        private final ConcurrentLinkedQueue<IMCMessage> queue;
        private final Predicate<String> methodFilter;
        private final Iterator<IMCMessage> iterator;

        public QueueFilteringSpliterator(final ConcurrentLinkedQueue<IMCMessage> queue, final Predicate<String> methodFilter) {
            this.queue = queue;
            this.iterator = queue.iterator();
            this.methodFilter = methodFilter;
        }

        @Override
        public int characteristics() {
            return Spliterator.CONCURRENT | Spliterator.NONNULL | Spliterator.ORDERED;
        }

        @Override
        public long estimateSize() {
            return queue.size();
        }

        @Override
        public boolean tryAdvance(final Consumer<? super IMCMessage> action)
        {
            IMCMessage next;
            do
            {
                if (!iterator.hasNext())
                {
                    return false;
                }
                next = this.iterator.next();
            }
            while (!methodFilter.test(next.method));
            action.accept(next);
            this.iterator.remove();
            return true;
        }

        @Override
        public Spliterator<IMCMessage> trySplit() {
            return null;
        }

    }
}
