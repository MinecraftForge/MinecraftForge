package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;

public class IntValue extends ConfigValue<Integer>
{
    public IntValue(ModConfigSpec parent, List<String> path, Supplier<Integer> defaultSupplier)
    {
        super(parent, path, defaultSupplier);
    }

    @Override
    protected Integer getRaw(CommentedConfig config, List<String> path, Supplier<Integer> defaultSupplier)
    {
        return config.getIntOrElse(path, defaultSupplier::get);
    }
}
