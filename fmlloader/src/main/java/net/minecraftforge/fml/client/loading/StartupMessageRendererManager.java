package net.minecraftforge.fml.client.loading;

import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.ServiceLoader;

public final class StartupMessageRendererManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final StartupMessageRendererManager INSTANCE = new StartupMessageRendererManager();

    public static StartupMessageRendererManager getInstance()
    {
        return INSTANCE;
    }

    private IStartupMessageRenderer renderer;

    private StartupMessageRendererManager()
    {
    }

    public Optional<IStartupMessageRenderer> get() {
        return Optional.ofNullable(renderer);
    }

    public void setup(final Dist dist, final IModuleLayerManager manager) {
        if (dist == Dist.CLIENT && renderer == null) {
            var serviceLayer = manager.getLayer(IModuleLayerManager.Layer.SERVICE);
            if (serviceLayer.isEmpty())
                return;

            renderer = ServiceLoaderUtils.streamServiceLoader(
              () -> ServiceLoader.load(serviceLayer.get(), IStartupMessageRenderer.class),
              sce -> LOGGER.fatal("Encountered serious error loading transformation service, expect problems", sce)
            )
              .findFirst()
              .orElse(null);
        }
    }
}
