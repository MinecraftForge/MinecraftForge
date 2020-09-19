package net.minecraftforge.client.gui.config;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ConfigCategoryInfo {

    Collection<String> elements();

    Object getValue(String key);

    Object getSpec(String key);

    /** Special case categories because reasons */
    String getCategoryComment(String key);

    static ConfigCategoryInfo of(Supplier<Collection<String>> elements, Function<String, Object> getValue, Function<String, Object> getSpec) {
        return of(elements, getValue, getSpec, $ -> null);
    }

    static ConfigCategoryInfo of(Supplier<Collection<String>> elements, Function<String, Object> getValue, Function<String, Object> getSpec, Function<String, String> getCategoryComment) {
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

            @Override
            public String getCategoryComment(String key) {
                return getCategoryComment.apply(key);
            }
        };
    }
}
