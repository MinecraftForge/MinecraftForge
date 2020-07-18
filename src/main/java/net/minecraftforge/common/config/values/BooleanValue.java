package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;

public class BooleanValue extends ConfigValue<Boolean>
{
    public BooleanValue(ModConfigSpec parent, List<String> path, Supplier<Boolean> defaultSupplier)
    {
        super(parent, path, defaultSupplier);
    }
}
