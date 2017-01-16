package net.minecraftforge.fml.common;

import java.util.List;

public class MultipleModsException extends RuntimeException {
    public List<RuntimeException> exceptions;
    public MultipleModsException(List<RuntimeException> exceptions) {
        this.exceptions = exceptions;
    }
}
