/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

/**
 * The new mod style in FML 1.3
 *
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod
{
    /**
     * The unique mod identifier for this mod
     */
    String modid();
    /**
     * A user friendly name for the mod
     */
    String name() default "";
    /**
     * A version string for this mod
     */
    String version() default "";
    /**
     * A simple dependency string for this mod (see modloader's "priorities" string specification)
     */
    String dependencies() default "";
    /**
     * Whether to use the mcmod.info metadata by default for this mod.
     * If true, settings in the mcmod.info file will override settings in these annotations.
     */
    boolean useMetadata() default false;

    /**
     * The acceptable range of minecraft versions that this mod will load and run in
     * The default ("empty string") indicates that only the current minecraft version is acceptable.
     * FML will refuse to run with an error if the minecraft version is not in this range across all mods.
     * @return A version range as specified by the maven version range specification or the empty string
     */
    String acceptedMinecraftVersions() default "";
    /**
     * An optional bukkit plugin that will be injected into the bukkit plugin framework if
     * this mod is loaded into the FML framework and the bukkit coremod is present.
     * Instances of the bukkit plugin can be obtained via the {@link BukkitPluginRef} annotation on fields.
     * @return The name of the plugin to load for this mod
     */
    String bukkitPlugin() default "";
    /**
     * Mods that this mod will <strong>not</strong> load with.
     * An optional comma separated string of (+|-)(*|modid[@value]) which specify mods that
     * this mod will refuse to load with, resulting in the game failing to start.
     * Entries can be prefixed with a + for a positive exclusion assertion, or - for a negative exclusion
     * assertion. Asterisk is the wildcard and represents <strong>all</strong> mods.
     *
     * The <strong>only</strong> mods that cannot be excluded are FML and MCP, trivially.
     * Other special values:
     * <ul>
     * <li>+f indicates that the mod will accept a minecraft forge environment.</li>
     * <li>-* indicates that the mod will not accept any other mods.</li>
     * </ul>
     *
     * Some examples:
     * <ul>
     * <li><em>-*,+f,+IronChest</em>: Will run only in a minecraft forge environment with the mod IronChests.
     * The -* forces all mods to be excluded, then the +f and +IronChest add into the "allowed list".</li>
     * <li><em>+f,-IC2</em>: Will run in a minecraft forge environment but will <strong>not</strong> run if
     * IndustrialCraft 2 (IC2) is loaded alongside.</li>
     * <li><em>-*</em>: Will not run if <strong>any</strong> othe mod is loaded except MCP/FML itself.</li>
     * </ul>
     *
     * If a mod is present on the excluded list, the game will stop and show an error screen. If the
     * class containing the {@link Mod} annotation has a "getCustomErrorException" method, it will be
     * called to retrieve a custom error message for display in this case. If two mods have a declared
     * exclusion which is matched, the screen that is shown is indeterminate.
     *
     * @return A string listing modids to exclude from loading with this mod.
     */
    String modExclusionList() default "";
    /**
     * Specifying this field allows for a mod to expect a signed jar with a fingerprint matching this value.
     * The fingerprint should be SHA-1 encoded, lowercase with ':' removed. An empty value indicates that
     * the mod is not expecting to be signed.
     *
     * Any incorrectness of the fingerprint, be it missing or wrong, will result in the {@link FingerprintWarning}
     * method firing <i>prior to any other event on the mod</i>.
     *
     * @return A certificate fingerprint that is expected for this mod.
     */
    String certificateFingerprint() default "";
    /**
     * Mark the designated method as to be called at if there is something wrong with the certificate fingerprint of
     * the mod's jar, or it is missing, or otherwise a problem.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface FingerprintWarning {}
    /**
     * Mark the designated method as being called at the "pre-initialization" phase
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PreInit {}
    /**
     * Mark the designated method as being called at the "initialization" phase
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Init {}
    /**
     * Mark the designated method as being called at the "post-initialization" phase
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PostInit {}
    /**
     * Mark the designated method as being called at the "server-starting" phase
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ServerStarting {}
    /**
     * Mark the designated method as being called at the "server-started" phase
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ServerStarted {}
    /**
     * Mark the designated method as being called at the "server-stopping" phase
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ServerStopping {}
    /**
     * Mark the designated method as being called at the "server-stopped" phase
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ServerStopped {}
    /**
     * Mark the designated method as the receiver for {@link FMLInterModComms} messages
     * Called between {@link Init} and {@link PostInit}
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface IMCCallback {}
    /**
     * Populate the annotated field with the mod instance.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Instance {
        /**
         * The mod object to inject into this field
         */
        String value() default "";
    }
    /**
     * Populate the annotated field with the mod's metadata.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Metadata {
        /**
         * The mod id specifying the metadata to load here
         */
        String value() default "";
    }
    /**
     * Populate the annotated field with an instance of the Block as specified
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Block {
        /**
         * The block's name
         */
        String name();
        /**
         * The associated ItemBlock subtype for the item (can be null for an ItemBlock)
         */
        Class<?> itemTypeClass() default ItemBlock.class;
    }
    /**
     * Populate the annotated field with an Item
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Item {
        /**
         * The name of the item
         */
        String name();
        /**
         * The type of the item
         */
        String typeClass();
    }
}
