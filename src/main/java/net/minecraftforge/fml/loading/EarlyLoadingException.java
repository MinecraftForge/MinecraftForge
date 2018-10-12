package net.minecraftforge.fml.loading;

import java.util.Arrays;
import java.util.List;

/**
 * Thrown during early loading phase, and collected by the LoadingModList for handoff to the client
 * or server.
 */
public class EarlyLoadingException extends RuntimeException {
    public static class ExceptionData {


        private final String i18message;
        private final Object[] args;
        public ExceptionData(final String message, Object... args) {
            this.i18message = message;
            this.args = args;
        }

        public String getI18message() {
            return i18message;
        }

        public Object[] getArgs() {
            return args;
        }
    }
    private final List<ExceptionData> errorMessages;

    public List<ExceptionData> getAllData() {
        return errorMessages;
    }

    EarlyLoadingException(final String message, final Throwable originalException, List<ExceptionData> errorMessages) {
        super(message, originalException);
        this.errorMessages = errorMessages;
    }


}
