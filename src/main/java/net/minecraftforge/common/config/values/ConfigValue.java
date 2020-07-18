package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;

public class ConfigValue<T>
{
    protected final ModConfigSpec parent;
    protected final List<String> path;
    protected final Supplier<T> defaultSupplier;

    public ConfigValue(ModConfigSpec parent, List<String> path, Supplier<T> defaultSupplier)
    {
        this.parent = parent;
        this.path = path;
        this.defaultSupplier = defaultSupplier;
    }

    public List<String> getPath()
    {
        return path;
    }

    public T get()
    {
        Preconditions.checkNotNull(parent, "Cannot get config value before spec is built");
        if (parent.getConfig() == null) {
            return defaultSupplier.get();
        }
        return getRaw(parent.getConfig(), path, defaultSupplier);
    }

    protected T getRaw(CommentedConfig config, List<String> path, Supplier<T> defaultSupplier)
    {
        return config.getOrElse(path, defaultSupplier);
    }

    public void save()
    {
        Preconditions.checkNotNull(parent,"Cannot save config value before spec is built");
        Preconditions.checkNotNull(parent.getConfig(),"Cannot save config value without assigned Config present");
        parent.save();
    }

    public void set(T value)
    {
        Preconditions.checkNotNull(parent,"Cannot set config value before spec is built");
        Preconditions.checkNotNull(parent.getConfig(),"Cannot set config value without assigned Config present");
        parent.getConfig().set(getPath(), value);
    }
}
