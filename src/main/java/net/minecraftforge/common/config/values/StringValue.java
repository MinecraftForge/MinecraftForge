package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;

public class StringValue extends ConfigValue<String>
{
    public StringValue(ModConfigSpec parent, List<String> path,
            Supplier<String> defaultSupplier)
    {
        super(parent, path, defaultSupplier);
    }
}
