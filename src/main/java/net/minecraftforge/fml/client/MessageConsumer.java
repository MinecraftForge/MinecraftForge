package net.minecraftforge.fml.client;

import java.util.function.BiConsumer;

public interface MessageConsumer extends BiConsumer<String, String>
{
    default void accept(String param) {
        this.accept(param, param);
    }
}
