package net.minecraftforge.registries.injection;

public enum MergeStrategy {
    /**
     * Adds all content from the source that is not already contained in the destination.
     */
    ADD_ALL_CONTENT,

    /**
     * Adds all modded content from the source that is not already contained in the destination.
     */
    ADD_MOD_CONTENT,

    /**
     * Only adds content to the destination from mods whose namespace does not already appear in the destination.
     */
    ADD_MISSING_MOD_CONTENT,
    ;
}
