package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModConfigSpec;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.EnumGetMethod;

public class EnumValue<T extends Enum<T>> extends ConfigValue<T>
{
    private final EnumGetMethod converter;
    private final Class<T> clazz;
    public EnumValue(ModConfigSpec parent, List<String> path, Supplier<T> defaultSupplier, EnumGetMethod converter, Class<T> clazz)
    {
        super(parent, path, defaultSupplier);
        this.converter = converter;
        this.clazz = clazz;
    }

    @Override
    protected T getRaw(CommentedConfig config, List<String> path, Supplier<T> defaultSupplier)
    {
        return config.getEnumOrElse(path, clazz, converter, defaultSupplier);
    }
}
