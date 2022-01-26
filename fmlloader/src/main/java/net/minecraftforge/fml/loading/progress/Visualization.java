package net.minecraftforge.fml.loading.progress;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface Visualization
{
    int windowWidth();

    int windowHeight();

    Runnable start(@Nullable String mcVersion);

    default long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, LongSupplier monitorSupplier)
    {
        return FMLLoader.getGameLayer().findModule("forge")
          .map(l -> Class.forName(l, "net.minecraftforge.client.loading.NoVizFallback"))
          .map(LamdbaExceptionUtils.rethrowFunction(c -> c.getMethod("fallback", IntSupplier.class, IntSupplier.class, Supplier.class, LongSupplier.class)))
          .map(LamdbaExceptionUtils.rethrowFunction(m -> (LongSupplier) m.invoke(null, width, height, title, monitorSupplier)))
          .map(LongSupplier::getAsLong)
          .orElseThrow(() -> new IllegalStateException("Why are you here?"));
    }

    default void updateFBSize(IntConsumer width, IntConsumer height)
    {
    }
}
