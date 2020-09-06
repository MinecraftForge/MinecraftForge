package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import java.util.Collection;

public class ConfigCategoryInfo {
    private final String path;
    private final UnmodifiableConfig rootValues;
    private final UnmodifiableConfig rootSpec;

    public ConfigCategoryInfo(String path, UnmodifiableConfig rootValues, UnmodifiableConfig rootSpec) {
        this.path = path;
        this.rootValues = rootValues;
        this.rootSpec = rootSpec;
    }

    public Collection<String> elements() {
        return rootValues.valueMap().keySet();
    }

    public Object getValue(String key) {
        return ((UnmodifiableConfig) rootValues.get(path)).get(key);
    }

    public Object getSpec(String key) {
        return ((UnmodifiableConfig) rootSpec.get(path)).get(key);
    }

}
