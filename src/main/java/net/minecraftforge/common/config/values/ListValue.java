package net.minecraftforge.common.config.values;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraftforge.common.config.ModConfigSpec;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;

public class ListValue<T> extends ConfigValue<List<T>>
{
    private final Function<Object, T> elementConverter;
    public ListValue(ModConfigSpec parent, List<String> path, Supplier<List<T>> defaultSupplier, Function<Object, T> converter)
    {
        super(parent, path, defaultSupplier);
        this.elementConverter = converter;
    }

    @Override
    protected List<T> getRaw(CommentedConfig config, List<String> path, Supplier<List<T>> defaultSupplier)
    {
        List<?> rawList = config.getOrElse(path, defaultSupplier);
        return rawList.stream().map(elementConverter).collect(Collectors.toList()); // Unfortunately, we have to run map twice in order to get the correct type back
    }
}
