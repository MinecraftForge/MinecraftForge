package net.minecraftforge.fml.config;

public class ConfigLoadingException extends RuntimeException {
    public ConfigLoadingException(final String configFileName, final String typeName, final String modId, Exception cause) {
        super("Failed loading config file " + configFileName + " of type " + typeName + " for modid " + modId, cause);
    }
}
