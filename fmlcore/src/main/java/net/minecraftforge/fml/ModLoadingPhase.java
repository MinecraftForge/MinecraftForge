package net.minecraftforge.fml;

public enum ModLoadingPhase {
    ERROR, // Special state for error handling
    GATHER,
    LOAD,
    COMPLETE,
    DONE;
}
