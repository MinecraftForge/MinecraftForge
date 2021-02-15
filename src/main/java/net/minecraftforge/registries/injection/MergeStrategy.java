package net.minecraftforge.registries.injection;

public enum MergeStrategy {
    /**
     * Adds all content from all namespaces to the merge destination.
     */
    ALL_NAMESPACES,

    /**
     * Only adds content from modded namespaces to the merge destination (ie not "minecraft:").
     */
    MOD_NAMESPACES,

    /**
     * Only adds content from a namespace which does not already appear in the merge destination.
     */
    ABSENT_NAMESPACES,
    ;
}
