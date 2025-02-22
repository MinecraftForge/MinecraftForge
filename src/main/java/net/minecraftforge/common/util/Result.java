package net.minecraftforge.common.util;

import net.minecraftforge.eventbus.api.event.RecordEvent;

import java.util.Objects;

public enum Result {
    ALLOW,
    DEFAULT,
    DENY;

    public boolean isAllowed() {
        return this == ALLOW;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public boolean isDenied() {
        return this == DENY;
    }

    /**
     * A mutable holder for a Result, useful for {@link RecordEvent}s combined with {@link HasResult.Record}
     */
    public static final class Holder {
        private Result value;

        public Holder() {
            set(DEFAULT);
        }

        public Holder(Result value) {
            set(value);
        }

        public Result get() {
            return value;
        }

        public void set(Result value) {
            Objects.requireNonNull(value);
            this.value = value;
        }

        @Override
        public String toString() {
            return "Result.Holder{value=" + value + "\"}";
        }

        @Override
        public boolean equals(Object that) {
            return this == that || (that instanceof Holder thatHolder && this.value == thatHolder.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
