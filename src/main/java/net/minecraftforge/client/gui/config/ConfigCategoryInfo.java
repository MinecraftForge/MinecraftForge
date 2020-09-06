package net.minecraftforge.client.gui.config;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ConfigCategoryInfo {

    Collection<String> elements();

    Object getValue(String key);

    Object getSpec(String key);

    static ConfigCategoryInfo of(Supplier<Collection<String>> elements, Function<String, Object> getValue, Function<String, Object> getSpec) {
        return new ConfigCategoryInfo() {

            @Override
            public Collection<String> elements() {
                return elements.get();
            }

            @Override
            public Object getValue(String key) {
                return getValue.apply(key);
            }

            @Override
            public Object getSpec(String key) {
                return getSpec.apply(key);
            }
        };
    }
}
