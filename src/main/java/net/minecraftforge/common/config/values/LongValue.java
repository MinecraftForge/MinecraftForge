package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;

public class LongValue extends ConfigValue<Long>
{
    public LongValue(ModConfigSpec parent, List<String> path, Supplier<Long> defaultSupplier)
    {
        super(parent, path, defaultSupplier);
    }

    @Override
    protected Long getRaw(CommentedConfig config, List<String> path, Supplier<Long> defaultSupplier)
    {
        return config.getLongOrElse(path, defaultSupplier::get);
    }
}
