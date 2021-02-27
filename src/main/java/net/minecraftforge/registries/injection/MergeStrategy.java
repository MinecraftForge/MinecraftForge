package net.minecraftforge.registries.injection;

public enum MergeStrategy
{
    /**
     * Adds all content from all namespaces to the registry entry's data.
     */
    ALL_NAMESPACES,

    /**
     * Adds all content from modded namespaces (ie not "minecraft:") to the registry entry's data.
     */
    MOD_NAMESPACES,

    /**
     * Only adds content from a namespace when that namespace does not already appear in the registry entry's data.
     * <p>
     * For example; if the data currently contains "minecraft:content_a" and a lower-order datapack or registry
     * contains "minecraft:content_b" it is assumed that the omission of "minecraft:content_b" was a deliberate
     * configuration choice, so it will not be added by the merge/inject operation.
     */
    ABSENT_NAMESPACES,
    ;
}
