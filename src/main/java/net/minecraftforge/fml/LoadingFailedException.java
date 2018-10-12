package net.minecraftforge.fml;

import java.util.List;

public class LoadingFailedException extends RuntimeException {
    private final List<ModLoadingException> loadingExceptions;

    public LoadingFailedException(final List<ModLoadingException> loadingExceptions) {
        this.loadingExceptions = loadingExceptions;
    }

    public List<ModLoadingException> getErrors() {
        return this.loadingExceptions;
    }
}
