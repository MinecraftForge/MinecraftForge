package net.minecraftforge.fml.config;

public class ConfigLoadingException extends RuntimeException
{
    private final ModConfig config;

    public ConfigLoadingException(ModConfig config, Exception cause) {
        super(cause);
        this.config = config;
    }

    @Override
    public String getMessage() {
        return "Failed loading config file " + config.getFullPath().toString() + " with type " + config.getType() + " for modid " + config.getModId();
    }
}
