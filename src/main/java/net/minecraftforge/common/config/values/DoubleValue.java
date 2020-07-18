package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;

public class DoubleValue extends ConfigValue<Double>
{
    public DoubleValue(ModConfigSpec parent, List<String> path, Supplier<Double> defaultSupplier)
    {
        super(parent, path, defaultSupplier);
    }

    @Override
    protected Double getRaw(CommentedConfig config, List<String> path, Supplier<Double> defaultSupplier)
    {
        Number n = config.<Number>get(path);
        return n == null ? defaultSupplier.get() : n.doubleValue();
    }
}
