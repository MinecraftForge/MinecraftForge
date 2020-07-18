package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;
import net.minecraftforge.common.config.ModObjectConverter;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.base.Preconditions;

public class ModValue<V> extends ConfigValue<V>
{
    private final ModObjectConverter<V> converter;
    public ModValue(ModConfigSpec parent, List<String> path, Supplier<V> defaultSupplier, ModObjectConverter<V> converter)
    {
        super(parent, path, defaultSupplier);
        this.converter = converter;
    }

    @Override
    protected V getRaw(CommentedConfig config, List<String> path, Supplier<V> defaultSupplier)
    {
        CommentedConfig section = config.getOrElse(path, config.createSubConfig());
        return converter.loadFromConfig(section, path, defaultSupplier);
    }

    @Override
    public void set(V value)
    {
        Preconditions.checkNotNull(parent,"Cannot set config value before spec is built");
        Preconditions.checkNotNull(parent.getConfig(),"Cannot set config value without assigned Config present");
        CommentedConfig subConfig = parent.getConfig().createSubConfig();
        converter.saveToConfig(subConfig, getPath(), value, defaultSupplier);
        parent.getConfig().set(getPath(), subConfig);
    }
}
